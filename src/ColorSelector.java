import javax.swing.*;
import java.awt.*;

// Allows the user to choose a set of colors for the search
public class ColorSelector extends JPanel implements ScryfallConstraint {
  private JComboBox<String> typeBox;
  private JCheckBox wBox;
  private JCheckBox uBox;
  private JCheckBox bBox;
  private JCheckBox rBox;
  private JCheckBox gBox;
  private String code;

  ColorSelector(String c) {
    setLayout(new FlowLayout(FlowLayout.LEFT));

    code = c;

    String[] constraintTypes = {"Exactly", "Including", "At Most"};
    typeBox = new JComboBox<>(constraintTypes);

    wBox = new JCheckBox("White");
    uBox = new JCheckBox("Blue");
    bBox = new JCheckBox("Black");
    rBox = new JCheckBox("Red");
    gBox = new JCheckBox("Green");

    add(typeBox);
    add(wBox);
    add(uBox);
    add(bBox);
    add(rBox);
    add(gBox);
  }

  @Override
  public String createQuery() {
    String colors = "";
    if (wBox.isSelected()) {
      colors += "w";
    }
    if (uBox.isSelected()) {
      colors += "u";
    }
    if (bBox.isSelected()) {
      colors += "b";
    }
    if (rBox.isSelected()) {
      colors += "r";
    }
    if (gBox.isSelected()) {
      colors += "g";
    }
    if (colors.equals("")) {
      return "";
    }
    switch (typeBox.getSelectedIndex()) {
      case 0:
        return code + "=" + colors;
      case 1:
        return code + ">=" + colors;
      default:
        return code + "<=" + colors;
    }
  }
}
