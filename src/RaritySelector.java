import javax.swing.*;
import java.awt.*;

// Allow the user to select rarity constraints. Note that unlike other
// selectors, the result in this case is always or'd
public class RaritySelector extends JPanel implements ScryfallConstraint {
  private JCheckBox mythicBox;
  private JCheckBox rareBox;
  private JCheckBox uncommonBox;
  private JCheckBox commonBox;

  public RaritySelector() {
    setLayout(new FlowLayout(FlowLayout.LEFT));

    mythicBox = new JCheckBox("Mythic");
    rareBox = new JCheckBox("Rare");
    uncommonBox = new JCheckBox("Uncommon");
    commonBox = new JCheckBox("Common");

    add(mythicBox);
    add(rareBox);
    add(uncommonBox);
    add(commonBox);
  }

  @Override
  public String createQuery() {
    String ret = "";
    JCheckBox[] boxes = {mythicBox, rareBox, uncommonBox, commonBox};
    String[] codes = {"m", "r", "u", "c"};
    for (int i = 0; i < boxes.length; i++) {
      if (boxes[i].isSelected()) {
        if (ret.equals("")) {
          ret = "r:" + codes[i] + " ";
        } else {
          ret += "or r:" + codes[i] + " ";
        }
      }
    }
    return ret.trim();
  }
}
