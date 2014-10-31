package com.netcetera.trema.core.exporting;

import com.netcetera.trema.core.api.IExportFilter;
import com.netcetera.trema.core.api.IKeyValuePair;


/**
 * Export filter for android, does transformation / escaping in keys and values.
 */
public class AndroidExportFilter implements IExportFilter {

  /** {@inheritDoc} */
  @Override
  public void filter(IKeyValuePair keyValuePair) {
    keyValuePair.setKey(escapeKeyName(keyValuePair.getKey()));
    if (keyValuePair.getValue() != null) {
      keyValuePair.setValue(escapeXmlSpecialCharacters(keyValuePair.getValue()));
    }
  }

  /**
   * Android specific escaping of characters. Note that since this is no real XML escaping, the
   * output string.xml file is potenticially not valid xml. However this is explicitly allowed by
   * Android.
   *
   * <P>
   * The following characters are replaced with corresponding character entities :
   * <table border='1' cellpadding='3' cellspacing='0'>
   * <tr>
   * <th>Character</th>
   * <th>Encoding</th>
   * </tr>
   * <tr>
   * <td>'</td>
   * <td>\\&amp#039;</td>
   * </tr>
   * <tr>
   * <td>\'</td>
   * <td>\\&amp#039;</td>
   * </tr>
   * <tr>
   * <td>&</td>
   * <td>&amp;amp;</td>
   * </tr>
   * </table>
   *
   * @param aText text to escape, must not be null
   * @return escaped text
   */
  protected static String escapeXmlSpecialCharacters(String aText) {
    String result = aText;
    result = result.replaceAll("&", "&amp;");
    result = result.replaceAll("\'", "\\\\&#039;");
    result = result.replaceAll("'", "\\\\&#039;");
    return result;
  }

  /**
   * Replaces some special characters that are not allowed in android strings key name but are valid
   * trema key names.
   *
   * @param keyName trema key name
   * @return android strings key name
   */
  protected static String escapeKeyName(String keyName) {
    // replace dot with underscore
    return keyName.replaceAll("[\\.\\-]", "\\_");
  }

}
