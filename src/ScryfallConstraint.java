// A ScryfallConstraint is anything which can be converted to a Scryfall search
// query
public interface ScryfallConstraint {
  String createQuery();
}
