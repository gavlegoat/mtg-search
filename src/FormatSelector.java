import javax.swing.*;
import java.awt.*;

// Allow the user to add format legality constraints
public class FormatSelector extends JPanel implements ScryfallConstraint {
  private JComboBox<String> legalityBox;
  private JComboBox<String> formatBox;

  FormatSelector() {
    setLayout(new FlowLayout(FlowLayout.LEFT));

    String[] legalities = {"Legal", "Banned", "Restricted"};
    legalityBox = new JComboBox<>(legalities);
    add(legalityBox);

    String[] formats = {"--", "Standard", "Modern", "Legacy", "Vintage",
            "Commander", "Pauper", "Frontier"};
    formatBox = new JComboBox<>(formats);
    add(formatBox);
  }

  @Override
  public String createQuery() {
    if (formatBox.getSelectedIndex() == 0) {
      return "";
    }
    String form = "";
    if (formatBox.getSelectedItem() != null) {
      form = formatBox.getSelectedItem().toString().toLowerCase();
    }
    switch (legalityBox.getSelectedIndex()) {
      case 0:
        return "f:" + form;
      case 1:
        return "banned:" + form;
      default:
        return "restricted:" + form;
    }
  }
}
