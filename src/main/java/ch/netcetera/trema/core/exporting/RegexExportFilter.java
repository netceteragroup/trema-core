package ch.netcetera.trema.core.exporting;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import ch.netcetera.trema.core.api.IExportFilter;
import ch.netcetera.trema.core.api.IKeyValuePair;


/**
 * Allows regex filtering for the values to be imported into the property files (e.g. search/replace).
 */
public class RegexExportFilter implements IExportFilter {

  private Pattern pattern;
  private String replacement;
  
  /**
   * @param regex the regex to match
   * @param replacement the replacement for the match
   * @throws PatternSyntaxException thrown if the regex cannot be compiled.
   */
  public RegexExportFilter(String regex, String replacement) throws PatternSyntaxException {
    pattern = Pattern.compile(regex);
    this.replacement = replacement;
  }

  /** {@inheritDoc} */
  @Override
  public void filter(IKeyValuePair keyValuePair) {
    String value = keyValuePair.getValue();
    if (value != null) {
      keyValuePair.setValue(pattern.matcher(value).replaceAll(replacement));
    }
  }

}
