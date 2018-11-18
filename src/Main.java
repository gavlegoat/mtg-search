import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
  public static void main(String[] args) {
    Card.loadImageCache();
    JFrame frame = new JFrame("MtG Search");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        Card.saveImageCache();
        super.windowClosing(e);
      }
    });
    frame.setContentPane(new MainPanel());
    frame.pack();
    frame.setResizable(false);
    frame.setVisible(true);
  }
}
