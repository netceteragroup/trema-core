package com.netcetera.trema.core.exporting;

import com.google.common.collect.ImmutableMap;
import com.netcetera.trema.core.api.IKeyValuePair;
import org.junit.jupiter.api.Test;

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
    ImmutableMap<String, String> textAndExpectedOutput = ImmutableMap.<String, String>builder()
      .put("it\\'s", "it\\&#039;s")
      .put("it's", "it\\&#039;s")
      .put("it's it's", "it\\&#039;s it\\&#039;s")
      .put("nothingToEscape", "nothingToEscape")
      .put("html <b>allow</b>", "html <b>allow</b>")
      .put("tom&jerry", "tom&amp;jerry")
      .build();

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
