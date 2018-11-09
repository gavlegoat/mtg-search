import java.util.Map;

// Defines a magic card including information about how many are in a user's
// collection.
public class Card {
  private String name;
  // sets maps set codes to the number of cards with this name which are in that
  // set
  private Map<String,Integer> sets;

  Card(String n, Map<String,Integer> s) {
    name = n;
    sets = s;
  }

  // Add `count` copies of this card belonging to set `sn`
  void addSet(String sn, int count) {
    if (sets.containsKey(sn)) {
      sets.replace(sn, sets.get(sn) + count);
    } else {
      sets.put(sn, count);
    }
  }
}
