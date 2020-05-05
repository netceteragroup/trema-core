package com.netcetera.trema.core.exporting;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.XMLDatabase;
import com.netcetera.trema.core.api.IKeyValuePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;


/**
 * Test class for {@link PropertiesExporter}.
 */
public class PropertiesExporterTest {

  private PropertiesExporter exporter;
  private XMLDatabase db;

  @BeforeEach
  void setUp() throws Exception {
    db = new XMLDatabase();
    db.build("<?xml version='1.0' encoding='UTF-8'?><trema masterLang='de'>"
        + "<text key='key2'> <context>context2</context>"
        + "  <value lang='de' status='verified'>masterValue2öäü</value>"
        + "  <value lang='fr' status='translated'>value2öäü</value>"
        + "</text>"
        + "<text key='key1'> <context>context1</context>"
        + "  <value lang='de' status='initial'>masterValue1\u12AB</value>"
        + "  <value lang='fr' status='initial'>value1\u12AB</value>"
        + "</text>"
        + "<text key='key3'> <context>context3</context>"
        + "  <value lang='de' status='special'>masterValue3</value>"
        + "  <value lang='fr' status='special'>value3</value>"
        + "</text>"
        + "</trema>", false);

    File file = null;
    exporter = new PropertiesExporter(file, new FileOutputStreamFactory());
  }

  /**
   * Export French with all status.
   */
  @Test
  void shouldExportAllEntriesByLanguage() {
    // given / when
    Properties properties = exporter.getProperties(db.getTextNodes(), "fr", null);

    // then
    assertThat(properties.getProperty("key1"), equalTo("value1\u12AB"));
    assertThat(properties.getProperty("key2"), equalTo("value2öäü"));
    assertThat(properties.getProperty("key3"), equalTo("value3"));
    assertThat(properties.getProperty("unexisting.key"), nullValue());
  }

  /**
   * Export French, status "initial" and "translated".
   */
  @Test
  void shouldExportEntriesByLanguageAndStatuses() {
    // given / when
    Properties properties =
      exporter.getProperties(db.getTextNodes(), "fr", new Status[] {Status.INITIAL, Status.TRANSLATED});

    // then
    assertThat(properties.getProperty("key1"), equalTo("value1\u12AB"));
    assertThat(properties.getProperty("key2"), equalTo("value2öäü"));
    assertThat(properties.getProperty("key3"), nullValue());
    assertThat(properties.getProperty("unexisting.key"), nullValue());
  }

  /**
   * Export French, only status "initial".
   */
  @Test
  void shouldExportEntriesByLanguageAndInitialStatus() {
    // given / when
    Properties properties = exporter.getProperties(db.getTextNodes(), "fr", new Status[] {Status.INITIAL});

    // then
    assertThat(properties.getProperty("key1"), equalTo("value1\u12AB"));
    assertThat(properties.getProperty("key2"), nullValue());
    assertThat(properties.getProperty("key3"), nullValue());
    assertThat(properties.getProperty("unexisting.key"), nullValue());
  }

  /**
   * Export French but no status.
   */
  @Test
  void shouldHandleEmptyStatusArray() {
    // given / when
    Properties properties = exporter.getProperties(db.getTextNodes(), "fr", new Status[0]);

    // then
    assertThat(properties.isEmpty(), equalTo(true));
  }

  /**
   * Export German (master language).
   */
  @Test
  void shouldExportGermanEntries() {
    // given / when
    Properties properties = exporter.getProperties(db.getTextNodes(), "de", null);

    // then
    assertThat(properties.getProperty("key1"), equalTo("masterValue1\u12AB"));
    assertThat(properties.getProperty("key2"), equalTo("masterValue2öäü"));
    assertThat(properties.getProperty("key3"), equalTo("masterValue3"));
    assertThat(properties.getProperty("unexisting.key"), nullValue());
  }

  /**
   * Export unavailable language.
   */
  @Test
  void shouldHandleUnavailableLanguage() {
    // given / when
    Properties properties = exporter.getProperties(db.getTextNodes(), "unavailable", null);

    // then
    assertThat(properties.isEmpty(), equalTo(true));
  }

  /**
   * Tests escaping of single-quotes for usage with MessageFormat.
   */
  @Test
  void shouldEscapeSingleQuotes() {
    // given
    MessageFormatEscapingFilter filter = new MessageFormatEscapingFilter();
    IKeyValuePair keyValuePair = new KeyValuePair("akey", "hallo 'velo'");

    // when
    filter.filter(keyValuePair);

    // then
    assertThat(keyValuePair.getKey(), equalTo("akey"));
    assertThat(keyValuePair.getValue(), equalTo("hallo ''velo''"));
  }

  @Test
  void shouldAddKeyToValue() {
    // given
    AddKeyToValueExportFilter filter = new AddKeyToValueExportFilter();
    IKeyValuePair keyValuePair = new KeyValuePair("key", "value");

    // when
    filter.filter(keyValuePair);

    // then
    assertThat(keyValuePair.getKey(), equalTo("key"));
    assertThat(keyValuePair.getValue(), equalTo("value [key]"));
  }

  /**
   * Checks that enumerations are ordered.
   */
  @Test
  void shouldHaveOrderedKeys() {
    // given / when
    Properties properties = exporter.getProperties(db.getTextNodes(), "de", null);

    // then
    List<Object> result = Collections.list(properties.keys());
    Object[] orderedKeys = result.stream().sorted().toArray();
    assertThat(result, contains(orderedKeys));
  }
}
