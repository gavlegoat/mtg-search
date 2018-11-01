import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

// Maintains a list of criteria added from the StatSelector. This panel also
// holds the username entry and collection loading elements.
public class CriteriaPanel extends JPanel implements ScryfallConstraint {
  private JProgressBar pb;
  private JTextField usernameField;
  private java.util.List<String> searchStrings;

  public CriteriaPanel() {
    // This progress bar is used when the user's collection is loaded from
    // Deckbox
    pb = new JProgressBar(0, 100);
    pb.setValue(0);
    pb.setStringPainted(true);

    searchStrings = new LinkedList<>();

    JPanel usernamePanel = new JPanel(new FlowLayout());
    usernamePanel.add(new JLabel("Deckbox Username:"));
    usernameField = new JTextField();
    usernameField.setColumns(20);
    usernamePanel.add(usernameField);
    add(usernamePanel);

    setPreferredSize(new Dimension(300, 800));
    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

    JButton usernameButton = new JButton("Load Collection");
    usernameButton.addActionListener(e -> {
      // Set up a new frame to hold the progress bar
      JFrame pbFrame = new JFrame("Progress");
      pbFrame.setLayout(new BorderLayout());
      pbFrame.setLocationRelativeTo(null);
      pbFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      pbFrame.add(pb, BorderLayout.CENTER);
      pbFrame.pack();
      pbFrame.setVisible(true);
      Task task = new Task(pbFrame);
      task.addPropertyChangeListener(e2 -> {
        if ("progress".equals(e2.getPropertyName())) {
          pb.setValue((Integer) e2.getNewValue());
        }
      });
      task.execute();
    });
    add(usernameButton);

    add(new JSeparator(SwingConstants.HORIZONTAL));
  }

  // Add a new constraint. Here disp is the string to display and search is
  // the constraint rendered as a Scryfall search string
  public void addRow(String disp, String search) {
    JPanel newPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    newPanel.add(new JLabel(disp));
    JButton remove = new JButton("Remove");
    remove.addActionListener(e -> {
      remove(newPanel);
      searchStrings.remove(search);
      validate();
      repaint();
    });
    newPanel.add(remove);
    add(newPanel);
    validate();
    repaint();
    searchStrings.add(search);
  }

  @Override
  public String createQuery() {
    String ret = "";
    for (String s : searchStrings) {
      ret += s + " ";
    }
    return ret.trim();
  }

  // This task loads the user's collection
  private class Task extends SwingWorker<Void, Void> {
    private JFrame frame;

    public Task(JFrame frame) {
      this.frame = frame;
    }

    @Override
    protected Void doInBackground() {
      // TODO
      return null;
    }

    @Override
    protected void done() {
      frame.dispose();
    }
  }
}