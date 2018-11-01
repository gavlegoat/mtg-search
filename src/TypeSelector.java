import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Allow the user to place constraints on the type. In particular, this setup
// allows the user to easily search only for commanders.
public class TypeSelector extends JLabel implements ScryfallConstraint {
  private JTextField textField;
  private JCheckBox partialBox;
  private JCheckBox commanderBox;

  public TypeSelector() {
    setLayout(new BorderLayout());

    textField = new JTextField();
    add(textField, BorderLayout.CENTER);

    JPanel checkBoxPanel = new JPanel(new FlowLayout());
    partialBox = new JCheckBox("Partial Match");
    checkBoxPanel.add(partialBox);
    commanderBox = new JCheckBox("Commander");
    checkBoxPanel.add(commanderBox);

    add(checkBoxPanel, BorderLayout.EAST);
  }

  @Override
  public String createQuery() {
    String ret = "";
    if (commanderBox.isSelected()) {
      ret += "is:commander ";
    }
    String regex = "\"([^\"]*)\"|(\\S+)";
    Matcher m = Pattern.compile(regex).matcher(textField.getText());
    if (m.find()) {
      ret += "t:" + (m.group(1) == null ? m.group(2) : "\"" + m.group(1) + "\"") + " ";
    }
    while (m.find()) {
      String next = m.group(1) == null ? m.group(2) : "\"" + m.group(1) + "\"";
      if (partialBox.isSelected()) {
        ret += "or t:" + next + " ";
      } else {
        ret += "t:" + next + " ";
      }
    }
    return ret.trim();
  }
}
