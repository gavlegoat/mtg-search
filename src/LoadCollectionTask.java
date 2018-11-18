import javax.swing.*;
import java.io.File;

// This task loads the user's collection
public class LoadCollectionTask extends SwingWorker<Boolean, Void> {
  private JFrame frame;
  private CollectionManager man;

  LoadCollectionTask(JFrame frame, CollectionManager m) {
    this.frame = frame;
    man = m;
  }

  @Override
  protected Boolean doInBackground() {
    int i = 1;
    int pages = 1;
    while (pages != 0) {
      File dir = new File("cache");
      if (!dir.exists()) {
        dir.mkdir();
      }
      pages = man.loadCollection(i);
      setProgress((i * 100) / pages);
      i++;
    }
    return true;
  }

  @Override
  protected void done() {
    frame.dispose();
  }
}
