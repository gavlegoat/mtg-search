// This class manages a user's collection. All queries to the user's collection
// go through this class, and it decides when to reload the collection from
// Deckbox.
public class CollectionManager {
  private String username;

  public CollectionManager(String un) {
    username = un;
  }
}
