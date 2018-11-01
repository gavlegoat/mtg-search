import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// A generic selector for adding text-based criteria
public class CodeSelector extends JTextField implements ScryfallConstraint {
  private String code;

  public CodeSelector(String c) {
    code = c;
  }

  @Override
  public String createQuery() {
    String regex = "\"([^\"]*)\"|(\\S+)";
    Matcher m = Pattern.compile(regex).matcher(getText());
    String ret = "";
    while (m.find()) {
      String next = m.group(1) == null ? m.group(2) : "\"" + m.group(1) + "\"";
      ret += code.equals("") ? next + " " : code + ":" + next + " ";
    }
    return ret.trim();
  }
}
