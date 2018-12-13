import javax.swing.*;
import java.io.File;

// This task loads the user's collection
public class LoadCollectionTask extends SwingWorker<Boolean, Void> {
  private JFrame frame;
  private CollectionManager man;
  private File file;

  LoadCollectionTask(JFrame frame, CollectionManager m, File file) {
    this.frame = frame;
    man = m;
    this.file = file;
  }

  @Override
  protected Boolean doInBackground() {
    int finished = man.loadCollection(file);
    return finished != 0;
  }

  @Override
  protected void done() {
    frame.dispose();
  }
}
