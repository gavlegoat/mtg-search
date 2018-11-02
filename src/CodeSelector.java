import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// A generic selector for adding text-based criteria
public class CodeSelector extends JTextField implements ScryfallConstraint {
  final private String code;

  CodeSelector(String c) {
    code = c;
  }

  @Override
  public String createQuery() {
    String regex = "\"([^\"]*)\"|(\\S+)";
    Matcher m = Pattern.compile(regex).matcher(getText());
    StringBuilder ret = new StringBuilder("");
    while (m.find()) {
      String next = m.group(1) == null ? m.group(2) : "\"" + m.group(1) + "\"";
      if (code.equals("")) {
        ret.append(next).append(" ");
      } else {
        ret.append(code).append(":").append(next).append(" ");
      }
    }
    return ret.toString().trim();
  }
}
