import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

// This class manages a user's collection. All queries to the user's collection
// go through this class, and it decides when to reload the collection from
// Deckbox.
class CollectionManager {
  private String username;
  private HashMap<String,HashMap<String,Integer>> collection;
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
  int loadCollection(String un, int page, String fn) throws IOException {
    username = un;
    String url = "https://deckbox-api.herokuapp.com/api/users/" + username + "/inventory";
    String charset = StandardCharsets.UTF_8.name();
    String query = String.format("page=%s",
            URLEncoder.encode(Integer.toString(page), charset));
    URLConnection con = new URL(url + "?" + query).openConnection();
    con.setRequestProperty("Accept-Charset", charset);
    InputStream response = con.getInputStream();
    Scanner scanner = new Scanner(response, charset);
    String content = scanner.useDelimiter("\\A").next();
    scanner.close();
    JSONObject obj = new JSONObject(content);
    JSONArray cards = obj.getJSONArray("items");
    for (int i = 0; i < cards.length(); i++) {
      JSONObject card = cards.getJSONObject(i);
      String cardName = card.getString("name");
      String setName = card.getString("set");
      int count = card.getInt("count");
      if (collection.containsKey(cardName)) {
        if (collection.get(cardName).containsKey(setName)) {
          int existing = collection.get(cardName).get(setName);
          collection.get(cardName).replace(setName, existing + count);
        } else {
          collection.get(cardName).put(setName, count);
        }
      } else {
        HashMap<String,Integer> newMap = new HashMap<>();
        newMap.put(setName, count);
        collection.put(cardName, newMap);
      }
    }

    int pages = obj.getInt("total_pages");
    if (page >= pages) {
      loaded = true;
      FileOutputStream fos = new FileOutputStream(fn);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(collection);
      oos.close();
      fos.close();
      for (String k : collection.keySet()) {
        int total = 0;
        for (String k2 : collection.get(k).keySet()) {
          total += collection.get(k).get(k2);
        }
        System.out.println(k + ": " + total);
      }
      return 0;
    } else {
      return pages;
    }
  }
}
