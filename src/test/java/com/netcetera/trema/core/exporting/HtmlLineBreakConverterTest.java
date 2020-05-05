package com.netcetera.trema.core.exporting;

import com.netcetera.trema.core.api.IKeyValuePair;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test for {@link HtmlLineBreakConverter}.
 */
public class HtmlLineBreakConverterTest {

  private HtmlLineBreakConverter filter = new HtmlLineBreakConverter();

  @Test
  public void shouldLeaveTextWithoutNewlineUntouched() {
    // given
    String s = "The quick brown fox jumps over the lazy dog.";
    IKeyValuePair keyValuePair = createPair(s);

    // when
    filter.filter(keyValuePair);

    // then
    assertThat(keyValuePair.getValue(), equalTo(s));
  }

  @Test
  public void shouldReplaceWindowsLineBreak() {
    // given
    String s = "The quick brown fox jumps\\r\\n over the lazy dog.";
    IKeyValuePair keyValuePair = createPair(s);

    // when
    filter.filter(keyValuePair);

    // then
    String expected = "The quick brown fox jumps<br/> over the lazy dog.";
    assertThat(keyValuePair.getValue(), equalTo(expected));
  }

  @Test
  public void shouldReplaceNixLineBreak() {
    // given
    String s = "The quick brown fox jumps\\n over the lazy dog.";
    IKeyValuePair keyValuePair = createPair(s);

    // when
    filter.filter(keyValuePair);

    // then
    String expected = "The quick brown fox jumps<br/> over the lazy dog.";
    assertThat(keyValuePair.getValue(), equalTo(expected));
  }

  @Test
  public void shouldReplaceMacLineBreak() {
    // given
    String s = "The quick brown fox jumps\\r over the lazy dog.";
    IKeyValuePair keyValuePair = createPair(s);

    // when
    filter.filter(keyValuePair);

    // then
    String expected = "The quick brown fox jumps<br/> over the lazy dog.";
    assertThat(keyValuePair.getValue(), equalTo(expected));
  }

  private static IKeyValuePair createPair(String v) {
    return new KeyValuePair("key", v);
  }
}
