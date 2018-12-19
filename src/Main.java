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
    frame.setContentPane(new MainScroller());
    frame.pack();
    frame.setResizable(true);
    frame.setVisible(true);
  }

  private static class MainScroller extends JPanel {
    MainScroller() {
      setLayout(new BorderLayout());

      MainPanel mp = new MainPanel();

      JScrollPane scrollPane = new JScrollPane(
              mp,
              JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
              JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      int horiz = mp.getPreferredSize().width +
              scrollPane.getVerticalScrollBar().getMaximumSize().width;
      int vert = mp.getPreferredSize().height +
              scrollPane.getHorizontalScrollBar().getMaximumSize().height;
      scrollPane.setPreferredSize(new Dimension(horiz, vert));
      scrollPane.getVerticalScrollBar().setUnitIncrement(16);
      scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
      add(scrollPane);
    }
  }
}
