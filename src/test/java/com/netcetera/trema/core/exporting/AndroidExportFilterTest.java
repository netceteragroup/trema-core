package com.netcetera.trema.core.exporting;

import com.netcetera.trema.core.api.IKeyValuePair;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;


/**
 * Test for {@link AndroidExportFilter}.
 */
public class AndroidExportFilterTest {

  /**
   * Test for escaping of android text values.
   */
  @Test
  public void shouldEscapeXmlSpecialCharacters() {
    // given
    Map<String, String> textAndExpectedOutput = new HashMap<>();
    textAndExpectedOutput.put("it\\'s", "it\\&#039;s");
    textAndExpectedOutput.put("it's", "it\\&#039;s");
    textAndExpectedOutput.put("it's it's", "it\\&#039;s it\\&#039;s");
    textAndExpectedOutput.put("nothingToEscape", "nothingToEscape");
    textAndExpectedOutput.put("html <b>allow</b>", "html <b>allow</b>");
    textAndExpectedOutput.put("tom&jerry", "tom&amp;jerry");

    textAndExpectedOutput.forEach((input, expected) -> {
      // when
      String escaped = AndroidExportFilter.escapeXmlSpecialCharacters(input);

      // then
      assertThat(escaped, escaped, equalTo(expected));
    });
  }

  /**
   * Test for conversion of '.' and '-' to '_' in keys.
   */
  @Test
  public void shouldConvertKeyProperly() {
    //same rules as for java variables are valid for android text keys
    // given / when / then
    assertThat(AndroidExportFilter.escapeKeyName("my.key"), equalTo("my_key"));
    assertThat(AndroidExportFilter.escapeKeyName("my-key"), equalTo("my_key"));
    assertThat(AndroidExportFilter.escapeKeyName("my_key"), equalTo("my_key"));
  }

  /**
   * Tests the whole filter.
   */
  @Test
  public void shouldFilterKeyAndValues() {
    // given
    IKeyValuePair keyValuePair = new KeyValuePair("needs.filtering", "it's it's");
    AndroidExportFilter filter = new AndroidExportFilter();

    // when
    filter.filter(keyValuePair);

    // then
    assertThat(keyValuePair.getKey(), equalTo("needs_filtering"));
    assertThat(keyValuePair.getValue(), equalTo("it\\&#039;s it\\&#039;s"));
  }

  @Test
  void shouldHandleKeyValuePairWithoutValue() {
    // given
    KeyValuePair keyValuePair = new KeyValuePair("needs.filtering", null);
    AndroidExportFilter filter = new AndroidExportFilter();

    // when
    filter.filter(keyValuePair);

    // then
    assertThat(keyValuePair.getKey(), equalTo("needs_filtering"));
    assertThat(keyValuePair.getValue(), nullValue());
  }
}
