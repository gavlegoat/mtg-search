import javax.swing.*;
import java.awt.*;

// This is the "home screen" of the search application
public class MainPanel extends JPanel {
  private CodeSelector scryfall;
  private CodeSelector nameField;
  private CodeSelector oracleField;
  private CodeSelector manaField;
  private CodeSelector setField;
  private CodeSelector blockField;
  private CodeSelector artisttField;
  private CodeSelector flavorField;
  private ColorSelector colors;
  private ColorSelector colorID;
  private TypeSelector typeField;
  private StatSelector statField;
  private FormatSelector formatField;
  private RaritySelector rarityField;
  private PriceSelector priceField;
  private CriteriaPanel critPanel;

  public MainPanel() {
    setPreferredSize(new Dimension(900, 800));

    critPanel = new CriteriaPanel();
    scryfall = new CodeSelector("");
    nameField = new CodeSelector("");
    oracleField = new CodeSelector("o");
    typeField = new TypeSelector();
    colors = new ColorSelector("color");
    colorID = new ColorSelector("id");
    manaField = new CodeSelector("mana");
    statField = new StatSelector(critPanel);
    formatField = new FormatSelector();
    setField = new CodeSelector("e");
    blockField = new CodeSelector("b");
    rarityField = new RaritySelector();
    priceField = new PriceSelector();
    artisttField = new CodeSelector("a");
    flavorField = new CodeSelector("ft");

    JPanel leftPanel = new JPanel(new GridLayout(0, 1, 30, 20));
    JPanel rightPanel = new JPanel(new GridLayout(0, 1, 30, 20));

    Component[] fields = {scryfall, nameField, oracleField, typeField, colors,
            colorID, manaField, formatField, statField, setField, blockField,
            rarityField, priceField, artisttField, flavorField};

    String[] labels = {"Formatted Search:", "Name:", "Oracle Text:",
            "Type Line:", "Colors:", "Color Identity:", "Pips:",
            "Format Legality:", "Stats:", "Set:", "Block:", "Rarity:", "Price:",
            "Artist:", "Flavor Text:"};

    for (int i = 0; i < fields.length; i++) {
      leftPanel.add(new JLabel(labels[i], SwingConstants.RIGHT));
      rightPanel.add(fields[i]);
    }

    JButton submit = new JButton("Submit");
    submit.addActionListener(e -> {
      System.out.println("<<<" + createQuery() + ">>>");
      //ResultsFrame rf = new ResultsFrame(createQuery());
      //rf.setVisible(true);
    });

    setLayout(new BorderLayout(30, 20));
    add(leftPanel, BorderLayout.WEST);
    add(rightPanel, BorderLayout.CENTER);
    add(critPanel, BorderLayout.EAST);
    add(submit, BorderLayout.SOUTH);
  }

  private String createQuery() {
    ScryfallConstraint[] fields = {scryfall, nameField, oracleField, typeField,
            colors, colorID, manaField, formatField, statField, setField,
            blockField, rarityField, priceField, artisttField, flavorField};
    String ret = "";
    for (ScryfallConstraint f : fields) {
      String q = f.createQuery();
      if (!q.equals("")) {
        ret += "(" + q + ") ";
      }
    }
    return ret.trim();
  }
}
