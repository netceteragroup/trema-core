package com.netcetera.trema.core.exporting;

import org.junit.Test;

import com.netcetera.trema.core.api.IKeyValuePair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


/**
 * Test for {@link AndroidExportFilter}.
 */
public class AndroidExportFilterTest {

  /**
   * Test for escaping of android text values.
   */
  @Test
  public void testEscapeXmlSpecialCharacters() {
    //same rules as for java variables are valid for android text keys
    String original = "it's";
    String escaped = AndroidExportFilter.escapeXmlSpecialCharacters(original);
    assertEquals("it\\&#039;s", escaped);

    original = "it\'s";
    escaped = AndroidExportFilter.escapeXmlSpecialCharacters(original);
    assertEquals("it\\&#039;s", escaped);

    original = "it's it's";
    escaped = AndroidExportFilter.escapeXmlSpecialCharacters(original);
    assertEquals("it\\&#039;s it\\&#039;s", escaped);

    original = "nothingToEscape";
    escaped = AndroidExportFilter.escapeXmlSpecialCharacters(original);
    assertEquals(original, escaped);

    original = "html <b>allow</b>";
    escaped = AndroidExportFilter.escapeXmlSpecialCharacters(original);
    assertEquals(original, escaped);

    original = "tom&jerry";
    escaped = AndroidExportFilter.escapeXmlSpecialCharacters(original);
    assertEquals("tom&amp;jerry", escaped);
  }

  /**
   * Test for conversion of '.' and '-' to '_' in keys.
   */
  @Test
  public void testKeyConversion() {
    //same rules as for java variables are valid for android text keys
    String originalKey = "my.key";
    String escaped = AndroidExportFilter.escapeKeyName(originalKey);
    assertEquals("my_key", escaped);

    originalKey = "my-key";
    escaped = AndroidExportFilter.escapeKeyName(originalKey);
    assertEquals("my_key", escaped);

    originalKey = "my_key";
    escaped = AndroidExportFilter.escapeKeyName(originalKey);
    assertEquals("my_key", escaped);
  }

  /**
   * Tests the whole filter.
   */
  @Test
  public void testFiltering() {
    IKeyValuePair keyValuePair = new KeyValuePair("needs.filtering", "it's it's");
    AndroidExportFilter filter = new AndroidExportFilter();
    filter.filter(keyValuePair);
    assertEquals("needs_filtering", keyValuePair.getKey());
    assertEquals("it\\&#039;s it\\&#039;s", keyValuePair.getValue());


    keyValuePair = new KeyValuePair("needs.filtering", null);
    filter.filter(keyValuePair);
    assertEquals("needs_filtering", keyValuePair.getKey());
    assertNull(keyValuePair.getValue());
  }

}
