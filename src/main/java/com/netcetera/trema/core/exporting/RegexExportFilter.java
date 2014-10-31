package com.netcetera.trema.core.exporting;

import com.netcetera.trema.core.api.IExportFilter;
import com.netcetera.trema.core.api.IKeyValuePair;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * Allows regex filtering for the values to be imported into the property files (e.g. search/replace).
 */
public class RegexExportFilter implements IExportFilter {

  private final Pattern pattern;
  private final String replacement;

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
