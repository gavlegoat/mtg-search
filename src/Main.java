import javax.swing.*;
import java.awt.event.ActionEvent;

public class Main {
  public static void main(String[] args) {
    JFrame frame = new JFrame("MtG Search");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JButton quit = new JButton("Quit");
    quit.setBounds(0, 0, 100, 20);
    quit.addActionListener((ActionEvent e) -> System.exit(0));
    frame.add(quit);
    frame.setSize(200, 200);
    frame.setLayout(null);
    frame.setVisible(true);
  }
}
