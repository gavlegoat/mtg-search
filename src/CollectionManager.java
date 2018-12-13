import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

// This class manages a user's collection. All queries to the user's collection
// go through this class, and it decides when to reload the collection from
// Deckbox.
class CollectionManager {
  private HashMap<String,Card> collection;
  private boolean loaded;
  private Image defaultImage;

  CollectionManager() {
    collection = new HashMap<>();
    loaded = false;
    try {
      BufferedImage original = ImageIO.read(new File("src/unh-17-look-at-me-i-m-r-d.jpg"));
      defaultImage = original.getScaledInstance(366, 510, Image.SCALE_DEFAULT);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Internal error: Failed to load the default image");
    }
  }

  // Load a users collection from a deckbox CSV file
  int loadCollection(File file) {
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader(file));
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Unable to open collection file");
      loaded = false;
      return 0;
    }
    try {
      // The first line is column headers
      String headLine = br.readLine();
      String[] headers = headLine.split(",");
      int nameInd = -1, numInd = -1, setInd = -1, urlInd = -1;
      for (int i = 0; i < headers.length; i++) {
        if (headers[i].equals("Count")) {
          numInd = i;
        } else if (headers[i].equals("Name")) {
          nameInd = i;
        } else if (headers[i].equals("Edition")) {
          setInd = i;
        } else if (headers[i].equals("Image URL")) {
          urlInd = i;
        }
      }
      if (nameInd == -1) {
        JOptionPane.showMessageDialog(null, "Name not found in collection file");
        loaded = false;
        return 0;
      }
      if (numInd == -1) {
        JOptionPane.showMessageDialog(null, "Count not found in collection file");
        loaded = false;
        return 0;
      }
      if (setInd == -1) {
        JOptionPane.showMessageDialog(null, "Set not found in collection file");
        loaded = false;
        return 0;
      }
      if (urlInd == -1) {
        JOptionPane.showMessageDialog(null, "Image URL not found in collection file");
        loaded = false;
        return 0;
      }
      String line = br.readLine();
      while (line != null) {
        if (line.equals("")) {
          line = br.readLine();
          continue;
        }
        String[] fields = line.split(",");
        try {
          int num = Integer.parseInt(fields[numInd]);
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(null, "Internal: Couldn't parse integer: " + fields[numInd]);
          loaded = false;
          return 0;
        }
        if (collection.keySet().contains(fields[nameInd])) {
          Card c = collection.get(fields[nameInd]);
          c.addSet(fields[setInd], Integer.parseInt(fields[numInd]), fields[urlInd]);
        } else {
          Map<String,Integer> sn = new HashMap<>();
          sn.put(fields[setInd], Integer.parseInt(fields[numInd]));
          Map<String,String> urls = new HashMap<>();
          urls.put(fields[setInd], fields[urlInd]);
          collection.put(fields[nameInd], new Card(fields[nameInd], sn, urls, defaultImage));
        }
        line = br.readLine();
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Unable to read collection file");
      loaded = false;
      return 0;
    }
    loaded = true;
    return 1;
  }

  boolean inCollection(String cardName) {
    return collection.keySet().contains(cardName);
  }

  Card getCard(String cardName) {
    return collection.get(cardName);
  }

  boolean isLoaded() {
    return loaded;
  }
}
