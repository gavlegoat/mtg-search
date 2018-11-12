import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

// Defines a magic card including information about how many are in a user's
// collection.
class Card {
  private String name;
  // sets maps set codes to the number of cards with this name which are in that
  // set
  private Map<String, Integer> sets;
  private Image image;
  private Map<String, String> imageURLs;
  private boolean loaded;

  Card(String n, Map<String,Integer> s, Map<String, String> is, Image defaultImage) {
    name = n;
    sets = s;
    imageURLs = is;
    image = defaultImage;
    loaded = false;
  }

  // Add `count` copies of this card belonging to set `sn`
  void addSet(String sn, int count, String url) {
    if (sets.containsKey(sn)) {
      sets.replace(sn, sets.get(sn) + count);
    } else {
      sets.put(sn, count);
    }
    if (!imageURLs.containsKey(sn)) {
      imageURLs.put(sn, url);
    }
  }

  void fetchImage() {
    if (loaded) {
      // The image has already been loaded
      return;
    }
    BufferedImage original;
    String url = imageURLs.get(imageURLs.keySet().iterator().next());
    HttpURLConnection con;
    try {
      con = (HttpURLConnection) (new URL(url)).openConnection();
    } catch (Exception e) {
      return;
    }
    try {
      con.setRequestMethod("GET");
    } catch (ProtocolException e) {
      return;
    }
    String charset = StandardCharsets.UTF_8.name();
    con.setRequestProperty("Accept-Charset", charset);
    try {
      con.connect();
    } catch (IOException e) {
      return;
    }
    int responseCode;
    try {
      responseCode = con.getResponseCode();
    } catch (IOException e) {
      return;
    }
    if (responseCode != HttpURLConnection.HTTP_OK) {
      return;
    }
    try {
      original = ImageIO.read(con.getInputStream());
    } catch (IOException e) {
      return;
    }

    image = original.getScaledInstance(366, 510, Image.SCALE_DEFAULT);
    loaded = true;
  }

  Image getImage() {
    return image;
  }

  String getName() {
    return name;
  }

  JSONArray getJSONRepresentation() {
    JSONArray ret = new JSONArray();
    for (String k : sets.keySet()) {
      JSONObject card = new JSONObject();
      card.put("name", name);
      card.put("set", k);
      card.put("count", sets.get(k));
      card.put("image", imageURLs.get(k));
      ret.put(card);
    }
    return ret;
  }
}
