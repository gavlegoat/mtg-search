import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

// This class manages a user's collection. All queries to the user's collection
// go through this class, and it decides when to reload the collection from
// Deckbox.
class CollectionManager {
  private String username;
  private HashMap<String,Card> collection;
  private boolean loaded;

  CollectionManager() {
    username = "";
    collection = new HashMap<>();
    loaded = false;
  }

  // Load one page of the user's collection from Deckbox. Append that data to
  // the file in fw and add it to collection. Return the total
  // number of pages in the collection if there are more pages to be read.
  // Otherwise, return 0 to indicate that we're finished. This set up allows
  // for a progress bar.
  int loadCollection(String un, int page, String fn) {
    username = un;
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
    int responseCode = -1;
    try {
      responseCode = con.getResponseCode();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "An error occured while reading the response from Deckbox");
    }
    if (responseCode != HttpURLConnection.HTTP_OK) {
      JOptionPane.showMessageDialog(null, "Error connecting to Deckbox: " + responseCode);
      loaded = false;
      return 0;
    }
    String content;
    try {
      content = con.getResponseMessage();
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
      if (collection.containsKey(cardName)) {
        collection.get(cardName).addSet(setName, count);
      } else {
        HashMap<String,Integer> newMap = new HashMap<>();
        newMap.put(setName, count);
        Card newCard = new Card(cardName, newMap);
        collection.put(cardName, newCard);
      }
    }

    int pages = obj.getInt("total_pages");
    if (page >= pages) {
      loaded = true;
      try {
        FileOutputStream fos = new FileOutputStream(fn);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(collection);
        oos.close();
        fos.close();
      } catch (IOException e) {
        JOptionPane.showMessageDialog(null,
                "Unable to cache collection (collection was still loaded)");
      }
      return 0;
    } else {
      return pages;
    }
  }
}
