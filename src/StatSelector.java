import javax.swing.*;
import java.awt.*;

// Allow the user to add constraints on CMC, power, toughness, and loyalty. Each
// new constraint is added to the CriteriaPanel as it is created.
public class StatSelector extends JLabel implements ScryfallConstraint {
  private final String[] codes = {"cmc", "pow", "tou", "loy"};

  private CriteriaPanel critPanel;
  private JComboBox<String> typeBox;
  private JComboBox<String> compBox;
  private JTextField numField;

  public StatSelector(CriteriaPanel critP) {
    critPanel = critP;

    setLayout(new FlowLayout(FlowLayout.LEFT));

    String[] stats = {"CMC", "Power", "Toughness", "Loyalty"};
    typeBox = new JComboBox<>(stats);
    add(typeBox);

    String[] comparisons = {"<", ">", "<=", ">=", "=", "!="};
    compBox = new JComboBox<>(comparisons);
    add(compBox);

    numField = new JTextField();
    numField.setColumns(7);
    add(numField);

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> {
      String search = codes[typeBox.getSelectedIndex()] +
              compBox.getSelectedItem() + numField.getText();
      String disp = stats[typeBox.getSelectedIndex()] +
              compBox.getSelectedItem() + numField.getText();
      critPanel.addRow(disp, search);
    });
    add(addButton);
  }

  @Override
  public String createQuery() {
    String critStr = critPanel.createQuery();
    String thisStr = "";
    if (!numField.getText().equals("")) {
      thisStr = codes[typeBox.getSelectedIndex()] + compBox.getSelectedItem() +
              numField.getText();
    }
    if (critStr.equals("")) {
      return thisStr;
    }
    return (critStr + " " + thisStr).trim();
  }
}
