package com.netcetera.trema.core.exporting;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.XMLDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;

/**
 * Unit test for {@link CSVExporter}.
 */
public class CSVExporterTest {

  private final CSVExporter exporter = new CSVExporter();
  private XMLDatabase db;

  @BeforeEach
  public void setUp() throws Exception {
    db = new XMLDatabase();
    db.build(
      "<?xml version='1.0' encoding='UTF-8'?><trema masterLang='de'>"
        + "<text key='key1'> <context>context1</context>"
        + "  <value lang='de' status='initial'>masterValue1\u12AB</value>"
        + "  <value lang='fr' status='initial'>value1\u12AB</value>"
        + "</text>"
        + "<text key='key2'> <context>context2</context>"
        + "  <value lang='de' status='verified'>masterValue2öäü</value>"
        + "  <value lang='fr' status='translated'>value2öäü</value>"
        + "</text>"
        + "<text key='key3'> <context>context3</context>"
        + "  <value lang='de' status='special'>masterValue3</value>"
        + "  <value lang='fr' status='special'>value3</value>"
        + "</text>"
        + "</trema>", false);
  }

  /**
   * Tests CSVExporter. Export French with all status.
   */
  @Test
  public void shouldExportAllValuesOfLanguage() {
    // given / when
    String[][] values = exporter.getValues(db.getTextNodes(), db.getMasterLanguage(), "fr", null);

    // then
    assertThat(values, arrayWithSize(4));

    // header
    assertThat(values[0], arrayContaining("Key", "Status", "Master (de)", "Value (fr)", "Context"));
    // text rows
    assertThat(values[1], arrayContaining("key1", "initial", "masterValue1\u12AB", "value1\u12AB", "context1"));
    assertThat(values[2], arrayContaining("key2", "translated", "masterValue2öäü", "value2öäü", "context2"));
    assertThat(values[3], arrayContaining("key3", "special", "masterValue3", "value3", "context3"));
  }

  /**
   * Tests CSVExporter. Export French, status "initial" and "translated"
   */
  @Test
  public void shouldExportLanguageAndFilterByStatus() {
    // given / when
    String[][] values = exporter.getValues(
      db.getTextNodes(), db.getMasterLanguage(), "fr", new Status[]{Status.INITIAL, Status.TRANSLATED});

    // then
    assertThat(values, arrayWithSize(3));
    assertThat(values[0], arrayContaining("Key", "Status", "Master (de)", "Value (fr)", "Context"));
    assertThat(values[1], arrayContaining("key1", "initial", "masterValue1\u12AB", "value1\u12AB", "context1"));
    assertThat(values[2], arrayContaining("key2", "translated", "masterValue2öäü", "value2öäü", "context2"));
  }

  /**
   * Tests CSVExporter. Export French, only status "initial".
   */
  @Test
  public void shouldExportByLanguageAndStatus() {
    // given / when
    String[][] values = exporter.getValues(
      db.getTextNodes(), db.getMasterLanguage(), "fr", new Status[]{Status.INITIAL});

    // then
    assertThat(values, arrayWithSize(2));
    assertThat(values[0], arrayContaining("Key", "Status", "Master (de)", "Value (fr)", "Context"));
    assertThat(values[1], arrayContaining("key1", "initial", "masterValue1\u12AB", "value1\u12AB", "context1"));
  }

  /**
   * Tests CSVExporter. Export French but no status
   */
  @Test
  public void shouldHandleEmptyStatusArray() {
    // given / when
    String[][] values = exporter.getValues(db.getTextNodes(), db.getMasterLanguage(), "fr", new Status[0]);

    // then
    assertThat(values, arrayWithSize(1));
    assertThat(values[0], arrayContaining("Key", "Status", "Master (de)", "Value (fr)", "Context"));
  }

  /**
   * Tests CSVExporter. Export German (master language).
   */
  @Test
  public void shouldExportByLanguageAndAllStatuses() {
    // given / when
    String[][] values = exporter.getValues(db.getTextNodes(), db.getMasterLanguage(), "de", null);

    // then
    assertThat(values, arrayWithSize(4));

    // header
    assertThat(values[0], arrayContaining("Key", "Status", "Value (de)", "Context"));
    // text rows
    assertThat(values[1], arrayContaining("key1", "initial", "masterValue1\u12AB", "context1"));
    assertThat(values[2], arrayContaining("key2", "verified", "masterValue2öäü", "context2"));
    assertThat(values[3], arrayContaining("key3", "special", "masterValue3", "context3"));
  }
}
