import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Holds the results of a search
class ResultsFrame extends JFrame {
  // Original image size: 488 x 680
  static final int CARD_WIDTH = 244;
  static final int CARD_HEIGHT = 340;

  private CollectionManager man;
  private String queryStr;
  private String nextPageURL;
  private List<Card> results;
  // The offset into `results` where the currently displayed results start
  private int offset;
  private ResultsPanel rp;
  private JButton nextButton;
  private JButton prevButton;

  ResultsFrame(String q, CollectionManager cm) {
    man = cm;
    queryStr = q;

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    setLayout(new BorderLayout());
    rp = new ResultsPanel();
    add(rp, BorderLayout.CENTER);

    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new GridLayout(1, 2));

    prevButton = new JButton("< Previous");
    prevButton.addActionListener(e -> setContents(offset - ResultsPanel.ROWS * ResultsPanel.COLS));
    controlPanel.add(prevButton);

    nextButton = new JButton("Next >");
    nextButton.setEnabled(false);
    nextButton.addActionListener(e -> setContents(offset + ResultsPanel.ROWS * ResultsPanel.COLS));
    controlPanel.add(nextButton);

    add(controlPanel, BorderLayout.SOUTH);
    pack();
  }

  boolean prepare() {
    if (!man.isLoaded()) {
      JProgressBar pb = new JProgressBar(0, 100);
      pb.setValue(0);
      pb.setStringPainted(true);
      JFrame pbFrame = new JFrame("Progress");
      pbFrame.setLayout(new BorderLayout());
      pbFrame.setLocationRelativeTo(null);
      pbFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      pbFrame.add(pb, BorderLayout.CENTER);
      pbFrame.pack();
      pbFrame.setVisible(true);
      JFileChooser fc = new JFileChooser();
      fc.showOpenDialog(this);
      LoadCollectionTask task = new LoadCollectionTask(pbFrame, man, fc.getSelectedFile());
      task.addPropertyChangeListener(e2 -> {
        if ("progress".equals(e2.getPropertyName())) {
          pb.setValue((Integer) e2.getNewValue());
        }
      });
      task.execute();
      try {
        // We need the loading process to finish before we can do the search
        task.get();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Failed to load collection");
        return false;
      }
    }
    // Send the search to Scryfall
    String url = "https://api.scryfall.com/cards/search/";
    String charset = StandardCharsets.UTF_8.name();
    String query;
    try {
      query = String.format("q=%s", URLEncoder.encode(queryStr, charset));
    } catch (UnsupportedEncodingException e) {
      JOptionPane.showMessageDialog(null, "Internal error: Unsupported encoding");
      return false;
    }

    nextPageURL = url + "?" + query;
    results = new ArrayList<>();

    return true;
  }

  boolean setContents(int off) {
    offset = off;
    while (results.size() < offset + ResultsPanel.ROWS * ResultsPanel.COLS && !nextPageURL.equals("")) {
      // We need to load more results
      JSONObject obj = loadPage(nextPageURL);
      JSONArray cards = obj.getJSONArray("data");
      for (int i = 0; i < cards.length(); i++) {
        String card = cards.getJSONObject(i).getString("name");
        if (man.inCollection(card)) {
          results.add(man.getCard(card));
        }
      }
      if (obj.getBoolean("has_more")) {
        nextPageURL = obj.getString("next_page");
      } else {
        nextPageURL = "";
        break;
      }
    }

    rp.setContent(offset);

    if (offset == 0) {
      prevButton.setEnabled(false);
    } else {
      prevButton.setEnabled(true);
    }

    if (offset + ResultsPanel.COLS * ResultsPanel.ROWS >= results.size()) {
      nextButton.setEnabled(false);
    } else {
      nextButton.setEnabled(true);
    }

    return true;
  }

  private JSONObject loadPage(String pageURL) {
    String charset = StandardCharsets.UTF_8.name();
    HttpURLConnection con;
    try {
      con = (HttpURLConnection) (new URL(pageURL).openConnection());
    } catch (MalformedURLException e) {
      JOptionPane.showMessageDialog(null, "Internal error: Bad URL");
      return null;
    } catch (IOException e) {
      return null;
    }
    try {
      con.setRequestMethod("GET");
    } catch (ProtocolException e) {
      JOptionPane.showMessageDialog(null, "Internal error: Protocol error");
      return null;
    }
    con.setRequestProperty("Accept-Charset", charset);
    try {
      con.connect();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "An error occured while connecting to Scryfall");
      return null;
    }
    int responseCode;
    try{
      responseCode = con.getResponseCode();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "An error occured while reading the response from Scryfall");
      return null;
    }
    if (responseCode != HttpURLConnection.HTTP_OK) {
      JOptionPane.showMessageDialog(null, "Error while connecting to Scryfall: " + responseCode);
      return null;
    }
    String content;
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      StringBuilder sb = new StringBuilder();
      String inputLine = in.readLine();
      while (inputLine != null) {
        sb.append(inputLine);
        inputLine = in.readLine();
      }
      content = sb.toString();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "An error occured while reading the response from Scryfall");
      return null;
    }
    return new JSONObject(content);
  }

  private class ResultsPanel extends JPanel {
    static final int ROWS = 10;
    static final int COLS = 4;
    private JPanel display;
    private JScrollPane scrollPane;
    private CardPanel[] displayedCards;

    ResultsPanel() {
      setLayout(new FlowLayout());

      display = new JPanel();
      display.setLayout(new GridLayout(ROWS, COLS));

      displayedCards = new CardPanel[ROWS * COLS];
      for (int i = 0; i < ROWS * COLS; i++) {
        displayedCards[i] = new CardPanel();
        display.add(displayedCards[i]);
      }

      scrollPane = new JScrollPane(display);
      scrollPane.setPreferredSize(new Dimension(COLS * CARD_WIDTH + 20, 800));
      scrollPane.getVerticalScrollBar().setUnitIncrement(16);
      add(scrollPane);
    }

    void setContent(int off) {
      for (int i = 0; i < ROWS * COLS; i++) {
        if (off + i >= results.size()) {
          displayedCards[i].setCard(null);
        } else {
          displayedCards[i].setCard(results.get(off + i));
        }
      }
      scrollPane.getVerticalScrollBar().setValue(0);
    }
  }

  // Displays a single card
  private class CardPanel extends JPanel {
    private JLabel cardImage;

    CardPanel() {
      cardImage = new JLabel();
      cardImage.setSize(CARD_WIDTH, CARD_HEIGHT);
      setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
      add(cardImage);
    }

    void setCard(Card card) {
      if (card == null) {
        // There is no card to display here
        setVisible(false);
        return;
      }
      // Load images on demand
      card.fetchImage();
      cardImage.setIcon(new ImageIcon(card.getImage()));
      cardImage.setToolTipText(card.getName());
      setVisible(true);
    }
  }
}
