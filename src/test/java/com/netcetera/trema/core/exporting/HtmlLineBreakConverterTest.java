package com.netcetera.trema.core.exporting;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.netcetera.trema.core.api.IKeyValuePair;
import com.netcetera.trema.core.exporting.HtmlLineBreakConverter;
import com.netcetera.trema.core.exporting.KeyValuePair;

/**
 * Test class.
 */
public class HtmlLineBreakConverterTest {

  private HtmlLineBreakConverter filter;
  @Before
  public void setup() {
    filter = new HtmlLineBreakConverter();
  }
  @Test
  public void shouldLeaveTextWithoutNewlineUntouched() {
    String s = "The quick brown fox jumps over the lazy dog.";
    IKeyValuePair keyValuePair = createPair(s);
    filter.filter(keyValuePair);
    Assert.assertEquals(s, keyValuePair.getValue());
  }

  @Test
  public void shouldReplaceWindowsLineBreak() {
    String s = "The quick brown fox jumps\\r\\n over the lazy dog.";
    IKeyValuePair keyValuePair = createPair(s);
    filter.filter(keyValuePair);
    String expected = "The quick brown fox jumps<br/> over the lazy dog.";
    Assert.assertEquals(expected, keyValuePair.getValue());
  }
  @Test
  public void shouldReplaceNixLineBreak() {
    String s = "The quick brown fox jumps\\n over the lazy dog.";
    IKeyValuePair keyValuePair = createPair(s);
    filter.filter(keyValuePair);
    String expected = "The quick brown fox jumps<br/> over the lazy dog.";
    Assert.assertEquals(expected, keyValuePair.getValue());
  }
  @Test
  public void shouldReplaceMacLineBreak() {
    String s = "The quick brown fox jumps\\r over the lazy dog.";
    IKeyValuePair keyValuePair = createPair(s);
    filter.filter(keyValuePair);
    String expected = "The quick brown fox jumps<br/> over the lazy dog.";
    Assert.assertEquals(expected, keyValuePair.getValue());
  }
  private IKeyValuePair createPair(String v) {
    return new KeyValuePair("key", v);
  }
}
