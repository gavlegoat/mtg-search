import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
  private JTextField scryfall;
  private JTextField nameField;
  private JTextField oracleField;
  private JTextField typeField;
  private ColorSelector colors;
  private ColorSelector colorID;

  public MainPanel() {
    setLayout(new GridLayout(0, 2));
    add(new JLabel("Scryfall String"));
    scryfall = new JTextField();
    add(scryfall);
    add(new JLabel("Card Name"));
    nameField = new JTextField();
    add(nameField);
    add(new JLabel("Oracle Text"));
    oracleField = new JTextField();
    add(oracleField);
    add(new JLabel("Type Line"));
    typeField = new JTextField();
    add(typeField);
    add(new JLabel("Colors"));
    colors = new ColorSelector();
    add(colors);
    add(new JLabel("Color ID"));
    colorID = new ColorSelector();
    add(colorID);
    // TODO: Color ID
    // TODO: Mana cost
    // TODO: Stats
    // TODO: Format legality
    // TODO: Sets/Blocks
    // TODO: Rarity
    // TODO: Price
    // TODO: Artist
    // TODO: Flavor text
    // TODO: Submit button
  }
}
