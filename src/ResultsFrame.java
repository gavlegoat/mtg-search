import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;

// Holds the results of a search
class ResultsFrame extends JFrame {
  private CollectionManager man;
  private String queryStr;

  ResultsFrame(String q, CollectionManager cm) {
    man = cm;
    queryStr = q;
  }

  boolean prepare() {
    // Send the search to Scryfall
    String url = "https://api.scryfall.com/cards/search/";
    String charset = StandardCharsets.UTF_8.name();
    String query;
    try {
      query = String.format("q=%s", URLEncoder.encode(queryStr, charset));
    } catch (UnsupportedEncodingException e) {
      JOptionPane.showMessageDialog(null, "Internal error: Unsupported encoding");
      return false;
    }
    HttpURLConnection con;
    try {
      con = (HttpURLConnection) (new URL(url + "?" + query).openConnection());
    } catch (MalformedURLException e) {
      JOptionPane.showMessageDialog(null, "Internal error: Bad URL");
      return false;
    } catch (IOException e) {
      return false;
    }
    try {
      con.setRequestMethod("GET");
    } catch (ProtocolException e) {
      JOptionPane.showMessageDialog(null, "Internal error: Protocol error");
      return false;
    }
    con.setRequestProperty("Accept-Charset", charset);
    try {
      con.connect();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "An error occured while connecting to Scryfall");
      return false;
    }
    int responseCode = -1;
    try{
      responseCode = con.getResponseCode();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "An error occured while reading the response from Scryfall");
      return false;
    }
    if (responseCode != HttpURLConnection.HTTP_OK) {
      JOptionPane.showMessageDialog(null, "Error while connecting to Scryfall: " + responseCode);
      return false;
    }
    String content;
    try {
      content = con.getResponseMessage();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "An error occured while reading the response from Scryfall");
      return false;
    }
    JSONObject obj = new JSONObject(content);

    // TODO
    // Get pages from the result and cross reference the loaded collection until
    //   There are enough results to fill a page.
    // Keep track of the current page and index into the page to use when the
    //   user wants to move to the next page of results
    // Also store all previous pages so the user can go back quickly
  }
}
