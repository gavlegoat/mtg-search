import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Main {
  public static void main(String[] args) {
    JFrame frame = new JFrame("MtG Search");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setContentPane(new MainPanel());
    frame.pack();
    frame.setResizable(false);
    frame.setVisible(true);
  }
}
