import javax.swing.*;
import java.awt.*;

// Allow the user to add price constraints. If either the min or max box is left
// empty then the price is unconstrained in the appropriate direction.
public class PriceSelector extends JLabel implements ScryfallConstraint {
  private final String[] codes = {"usd", "eur", "tix"};
  private JComboBox<String> choice;
  private JTextField minField;
  private JTextField maxField;

  public PriceSelector() {
    setLayout(new FlowLayout(FlowLayout.LEFT));

    String[] currencies = {"USD", "Euros", "MTGO Tix"};
    choice = new JComboBox<>(currencies);
    add(choice);
    add(new JLabel("From:"));

    minField = new JTextField();
    minField.setColumns(7);
    add(minField);

    add(new JLabel("To:"));

    maxField = new JTextField();
    maxField.setColumns(7);
    add(maxField);
  }

  @Override
  public String createQuery() {
    String ret = "";
    if (!minField.getText().equals("")) {
      ret += codes[choice.getSelectedIndex()] + ">=" + minField.getText() + " ";
    }
    if (!maxField.getText().equals("")) {
      ret += codes[choice.getSelectedIndex()] + "<=" + maxField.getText();
    }
    return ret.trim();
  }
}
