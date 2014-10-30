package com.netcetera.trema.core.exporting;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.XMLDatabase;
import com.netcetera.trema.core.api.IKeyValuePair;
import com.netcetera.trema.core.exporting.AddKeyToValueExportFilter;
import com.netcetera.trema.core.exporting.FileOutputStreamFactory;
import com.netcetera.trema.core.exporting.KeyValuePair;
import com.netcetera.trema.core.exporting.MessageFormatEscapingFilter;
import com.netcetera.trema.core.exporting.PropertiesExporter;



/**
 * Test class for the PropertiesExporter.
 */
public class PropertiesExporterTest {

  private PropertiesExporter exporter = null;
  private XMLDatabase db = null;

  /**
   * SetUp method.
   *
   * @throws Exception in case the method fails
   */
  @Before
  public void setUp() throws Exception {
    db = new XMLDatabase();
    db.build("<?xml version='1.0' encoding='UTF-8'?><trema masterLang='de'>"
        + "<text key='key2'> <context>context2</context>"
        + "  <value lang='de' status='verified'>masterValue2���</value>"
        + "  <value lang='fr' status='translated'>value2���</value>"
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
  public void testGetProperties1() {
    Properties properties = exporter.getProperties(db.getTextNodes(), "fr", null);
    Assert.assertEquals("value1\u12AB", properties.getProperty("key1"));
    Assert.assertEquals("value2���", properties.getProperty("key2"));
    Assert.assertEquals("value3", properties.getProperty("key3"));
    Assert.assertNull(properties.getProperty("unexisting.key"));
  }

  /**
   * Export French, status "initial" and "translated".
   */
  @Test
  public void testGetProperties2() {
    Properties properties =
      exporter.getProperties(db.getTextNodes(), "fr", new Status[] {Status.INITIAL, Status.TRANSLATED});
    Assert.assertEquals("value1\u12AB", properties.getProperty("key1"));
    Assert.assertEquals("value2���", properties.getProperty("key2"));
    Assert.assertNull(properties.getProperty("key3"));
    Assert.assertNull(properties.getProperty("unexisting.key"));
  }

  /**
   * Export French, only status "initial".
   */
  @Test
  public void testGetProperties3() {
    Properties properties = exporter.getProperties(db.getTextNodes(), "fr", new Status[] {Status.INITIAL});
    Assert.assertEquals("value1\u12AB", properties.getProperty("key1"));
    Assert.assertNull(properties.getProperty("key2"));
    Assert.assertNull(properties.getProperty("unexisting.key"));
  }

  /**
   * Export French but no status.
   */
  @Test
  public void testGetProperties4() {
    Properties properties = exporter.getProperties(db.getTextNodes(), "fr", new Status[0]);
    Assert.assertTrue(properties.isEmpty());
  }

  /**
   * Export German (master language).
   */
  @Test
  public void testGetProperties5() {
    Properties properties = exporter.getProperties(db.getTextNodes(), "de", null);
    Assert.assertEquals("masterValue1\u12AB", properties.getProperty("key1"));
    Assert.assertEquals("masterValue2���", properties.getProperty("key2"));
    Assert.assertEquals("masterValue3", properties.getProperty("key3"));
    Assert.assertNull(properties.getProperty("unexisting.key"));
  }

  /**
   * Export unavailable language.
   */
  @Test
  public void testGetProperties6() {
    Properties properties = exporter.getProperties(db.getTextNodes(), "unavailable", null);
    Assert.assertTrue(properties.isEmpty());
  }

  /**
   * Tests escaping of single-quotes for usage with MessageFormat.
   */
  @Test
  public void testMessageFormatExportFiltering() {
    MessageFormatEscapingFilter filter = new MessageFormatEscapingFilter();
    IKeyValuePair keyValuePair = new KeyValuePair("akey", "hallo 'velo'");
    filter.filter(keyValuePair);
    Assert.assertEquals("hallo ''velo''", keyValuePair.getValue());
    Assert.assertEquals("akey", keyValuePair.getKey());
  }

  /**
   * Tests escaping of single-quotes for usage with MessageFormat.
   */
  @Test
  public void testAddKeyToValueExportFiltering() {
    AddKeyToValueExportFilter filter = new AddKeyToValueExportFilter();
    IKeyValuePair keyValuePair = new KeyValuePair("key", "value");
    filter.filter(keyValuePair);
    Assert.assertEquals("value [key]", keyValuePair.getValue());
    Assert.assertEquals("key", keyValuePair.getKey());
  }

  /**
   * Checks that enumerations are ordered.
   */
  @Test
  public void testOrdered() {
    Properties properties = exporter.getProperties(db.getTextNodes(), "de", null);
    // Damn Enumerations
    @SuppressWarnings({"unchecked", "rawtypes" })
    Enumeration<String> keys = (Enumeration) properties.keys();
    List<String> unorderedList = Collections.list(keys);
    List<String> orderedList = new ArrayList<String>(unorderedList);
    Collections.sort(orderedList);
    for (int i = 0; i < unorderedList.size(); i++) {
      Assert.assertEquals(unorderedList.get(i), orderedList.get(i));
    }
  }

}
