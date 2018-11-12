import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// This class manages a user's collection. All queries to the user's collection
// go through this class, and it decides when to reload the collection from
// Deckbox.
class CollectionManager {
  private String username;
  private HashMap<String,Card> collection;
  private boolean loaded;
  private Image defaultImage;

  CollectionManager() {
    username = "";
    collection = new HashMap<>();
    loaded = false;
    try {
      BufferedImage original = ImageIO.read(new File("src/unh-17-look-at-me-i-m-r-d.jpg"));
      defaultImage = original.getScaledInstance(366, 510, Image.SCALE_DEFAULT);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Internal error: Failed to load the default image");
    }
  }

  void setUsername(String u) {
    username = u;
  }

  // Load one page of the user's collection from Deckbox. Append that data to
  // the file in fw and add it to collection. Return the total
  // number of pages in the collection if there are more pages to be read.
  // Otherwise, return 0 to indicate that we're finished. This set up allows
  // for a progress bar.
  int loadCollection(int page) {
    String url = "https://deckbox-api.herokuapp.com/api/users/" + username + "/inventory";
    String charset = StandardCharsets.UTF_8.name();
    String query;
    try {
      query = String.format("page=%s",
              URLEncoder.encode(Integer.toString(page), charset));
    } catch (UnsupportedEncodingException e) {
      JOptionPane.showMessageDialog(null, "Internal error: Unsupported encoding");
      loaded = false;
      return 0;
    }
    HttpURLConnection con;
    try {
      con = (HttpURLConnection) (new URL(url + "?" + query).openConnection());
    } catch (MalformedURLException e) {
      JOptionPane.showMessageDialog(null, "Internal error: Malformed URL");
      loaded = false;
      return 0;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "An error occured while connecting to Deckbox");
      loaded = false;
      return 0;
    }
    try {
      con.setRequestMethod("GET");
    } catch (ProtocolException e) {
      JOptionPane.showMessageDialog(null, "Internal error: Protocol error");
      loaded = false;
      return 0;
    }
    con.setRequestProperty("Accept-Charset", charset);
    try {
      con.connect();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "An error occured while connecting to Deckbox");
      loaded = false;
      return 0;
    }
    int responseCode;
    try {
      responseCode = con.getResponseCode();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "An error occured while reading the response from Deckbox");
      loaded = false;
      return 0;
    }
    if (responseCode != HttpURLConnection.HTTP_OK) {
      JOptionPane.showMessageDialog(null, "Error connecting to Deckbox: " + responseCode);
      loaded = false;
      return 0;
    }
    String content;
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      StringBuilder sb = new StringBuilder();
      String inputLine = in.readLine();
      while (inputLine != null) {
        sb.append(inputLine);
        inputLine = in.readLine();
      }
      content = sb.toString();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "An error occured while reading the response from Deckbox");
      loaded = false;
      return 0;
    }
    JSONObject obj = new JSONObject(content);
    JSONArray cards = obj.getJSONArray("items");
    for (int i = 0; i < cards.length(); i++) {
      JSONObject card = cards.getJSONObject(i);
      String cardName = card.getString("name");
      String setName = card.getString("set");
      int count = card.getInt("count");
      String imageURL = card.getJSONObject("images").getString("large");
      if (collection.containsKey(cardName)) {
        collection.get(cardName).addSet(setName, count, imageURL);
      } else {
        HashMap<String, Integer> newMap = new HashMap<>();
        newMap.put(setName, count);
        HashMap<String, String> imageMap = new HashMap<>();
        imageMap.put(setName, imageURL);
        Card newCard = new Card(cardName, newMap, imageMap, defaultImage);
        collection.put(cardName, newCard);
      }
    }

    int pages = obj.getInt("total_pages");
    if (page >= pages) {
      loaded = true;
      JSONObject topLevel = new JSONObject();
      topLevel.put("username", username);
      topLevel.put("num_cards", collection.size());
      JSONArray data = new JSONArray();
      for (String k : collection.keySet()) {
        JSONArray cardArray = collection.get(k).getJSONRepresentation();
        for (int i = 0; i < cardArray.length(); i++) {
          data.put(cardArray.get(i));
        }
      }
      topLevel.put("data", data);
      try {
        String fn = "cache/" + username;
        FileWriter fw = new FileWriter(fn);
        fw.write(topLevel.toString());
        fw.close();
      } catch (IOException e) {
        JOptionPane.showMessageDialog(null,
                "Unable to cache collection (collection was still loaded)");
      }
      return 0;
    } else {
      return pages;
    }
  }

  // If the collection is not loaded, load it from a local cache. If no such
  // cache exists, return false to indicate that the collection needs to be
  // loaded from Deckbox
  @SuppressWarnings("unchecked")
  boolean loadLocal() {
    if (loaded) {
      // The collection is already loaded
      return true;
    }
    File dir = new File("cache");
    if (!dir.exists()) {
      return false;
    }
    File cache = new File("cache/" + username);
    if (!cache.exists()) {
      return false;
    }
    BufferedReader fr;
    try {
      fr = new BufferedReader(new FileReader(cache));
    } catch (FileNotFoundException e) {
      return false;
    }
    StringBuilder sb = new StringBuilder();
    try {
      String line = fr.readLine();
      while (line != null) {
        sb.append(line);
        line = fr.readLine();
      }
    } catch (IOException e) {
      return false;
    }
    JSONObject cached = new JSONObject(sb.toString());
    JSONArray cardArray = cached.getJSONArray("data");
    for (int i = 0; i < cardArray.length(); i++) {
      JSONObject jcard = cardArray.getJSONObject(i);
      String cardName = jcard.getString("name");
      int count = jcard.getInt("count");
      String set = jcard.getString("set");
      String image = jcard.getString("image");
      if (collection.keySet().contains(cardName)) {
        collection.get(cardName).addSet(set, count, image);
      } else {
        Map<String, Integer> newSets = new HashMap<>();
        newSets.put(set, count);
        Map<String, String> newURLs = new HashMap<>();
        newURLs.put(set, image);
        Card card = new Card(cardName, newSets, newURLs, defaultImage);
        collection.put(cardName, card);
      }
    }
    return true;
  }

  boolean inCollection(String cardName) {
    return collection.keySet().contains(cardName);
  }

  Card getCard(String cardName) {
    return collection.get(cardName);
  }
}
