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
import java.util.HashMap;
import java.util.Map;

// Defines a magic card including information about how many are in a user's
// collection.
class Card {
  private static Map<String, File> cardImages;

  static void loadImageCache() {
    cardImages = new HashMap<>();

    File dir = new File("cache");
    if (!dir.exists()) {
      // No images are cached
      return;
    }
    File imageDir = new File("cache/images");
    if (!imageDir.exists()) {
      return;
    }
    File cache = new File("cache/images/saved_images");
    if (!cache.exists()) {
      return;
    }
    BufferedReader fr;
    try {
      fr = new BufferedReader(new FileReader(cache));
    } catch (FileNotFoundException e) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    try {
      String line = fr.readLine();
      while (line != null) {
        sb.append(line);
        line = fr.readLine();
      }
    } catch (IOException e) {
      return;
    }
    JSONObject cached = new JSONObject(sb.toString());
    JSONArray cardArray = cached.getJSONArray("data");
    for (int i = 0; i < cardArray.length(); i++) {
      JSONObject c = cardArray.getJSONObject(i);
      String name = c.getString("name");
      String imageFileName = c.getString("image");
      File imageFile = new File("cache/images/" + imageFileName);
      if (imageFile.exists()) {
        cardImages.put(name, imageFile);
      }
    }
  }

  static void saveImageCache() {
    JSONObject topLevel = new JSONObject();
    JSONArray data = new JSONArray();
    for (String k : cardImages.keySet()) {
      JSONObject c = new JSONObject();
      c.put("name", k);
      c.put("image", cardImages.get(k).getName());
      data.put(c);
    }
    topLevel.put("data", data);
    File dir = new File("cache");
    if (!dir.exists()) {
      dir.mkdir();
    }
    File cacheFile = new File("cache/images/saved_images");
    try {
      FileWriter fw = new FileWriter(cacheFile);
      fw.write(topLevel.toString());
      fw.close();
    } catch (IOException e) {
      // Failed to save image cache
    }
  }

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
    if (cardImages.containsKey(name)) {
      // This image is in our cache
      File imageFile = cardImages.get(name);
      try {
        image = ImageIO.read(imageFile);
        loaded = true;
        return;
      } catch (IOException e) {
        // Couldn't load from cache, continue to load from Scryfall
      }
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

    image = original.getScaledInstance(ResultsFrame.CARD_WIDTH, ResultsFrame.CARD_HEIGHT, Image.SCALE_SMOOTH);
    // Add this image to the cache for future runs
    File dir = new File("cache");
    if (!dir.exists()) {
      dir.mkdir();
    }
    File imageDir = new File("cache/images");
    if (!imageDir.exists()) {
      imageDir.mkdir();
    }
    String imageFileName = "cache/images/" + name.replace(" ", "") + ".jpg";
    BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
    Graphics g = bi.getGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();
    try {
      File imageFile = new File(imageFileName);
      ImageIO.write(bi, "jpg", imageFile);
      cardImages.put(name, imageFile);
    } catch (IOException e) {
      // Failed to save this image
    }
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
