package com.netcetera.trema.core.exporting;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.XMLDatabase;
import com.netcetera.trema.core.api.IImportSource;
import com.netcetera.trema.core.importing.XLSFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;


/**
 * Test for {@link XLSExporter}.
 */
class XLSExporterTest {

  public static final String TESTXML = ""
    + "<?xml version='1.0' encoding='UTF-8'?>"
    + "<trema masterLang='de'>"
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
    + "</trema>";

  /**
   * Tests for xls exporting. Exports a db then imports the file and does some
   * regression tests.
   * Columns in the xls are: Key | Status | Master (de) | Value (fr) | Context

   * @throws Exception in case of an error
   */
  @Test
  void shouldExportSuccessfully(@TempDir Path tempDirectory) throws Exception {
    // given
    XMLDatabase db = new XMLDatabase();
    db.build(TESTXML, false);
    File output = tempDirectory.resolve("test.xls").toFile();
    XLSExporter exporter = new XLSExporter(output);

    // when
    exporter.export(db.getTextNodes(), db.getMasterLanguage(), "fr", Status.getAvailableStatus());

    // then
    IImportSource file = new XLSFile(output.getPath());
    assertThat(file.getKeys(), arrayWithSize(3));
    assertThat(file.getMasterLanguage(), equalTo("de"));
    assertThat(file.getMasterValue("key1"), equalTo("masterValue1\u12AB"));
    assertThat(file.getMasterValue("key2"), equalTo("masterValue2öäü"));
    assertThat(file.getMasterValue("key3"), equalTo("masterValue3"));
    assertThat(file.getValue("key1"), equalTo("value1\u12AB"));
    assertThat(file.getValue("key2"), equalTo("value2öäü"));
    assertThat(file.getValue("key3"), equalTo("value3"));
  }

  /**
   * Tests export of the masterlanguage into an xls and re-import of the same.
   * Columns in the xls are: Key | Status | Value (fr) | Context
   *
   * When the masterlanguage is exported into an xls then the xls doesn't have a Master Column.
   * This needs to be handled by the import
   *
   * @throws Exception in case of an error
   */
  @Test
  void shouldExportMasterLanguage(@TempDir Path tempDirectory) throws Exception {
    // given
    XMLDatabase db = new XMLDatabase();
    db.build(TESTXML, false);
    File output = tempDirectory.resolve("test1.xls").toFile();
    XLSExporter exporter = new XLSExporter(output);

    // when
    exporter.export(db.getTextNodes(), db.getMasterLanguage(), "de", Status.getAvailableStatus());

    // then
    IImportSource file = new XLSFile(output.getPath());
    assertThat(file.getKeys(), arrayWithSize(3));
    assertThat(file.hasMasterLanguage(), equalTo(false));
    assertThat(file.getMasterValue("key1"), nullValue());
    assertThat(file.getMasterValue("key2"), nullValue());
    assertThat(file.getMasterValue("key3"), nullValue());
    assertThat(file.getValue("key1"), equalTo("masterValue1\u12AB"));
    assertThat(file.getValue("key2"), equalTo("masterValue2öäü"));
    assertThat(file.getValue("key3"), equalTo("masterValue3"));
  }

  /**
   * Tests creation of the file header.
   */
  @Test
  void shouldCreateAppropriateHeaders() {
    // given
    XLSExporter exporter = new XLSExporter();

    // when / then
    String[] result = exporter.getHeaderRow("de", new String[]{"de"});
    assertThat(result, arrayWithSize(4));
    assertThat(result[0], equalTo("Key"));
    assertThat(result[1], equalTo("Status"));
    assertThat(result[2], equalTo("Value (de)"));
    assertThat(result[3], equalTo("Context"));

    result = exporter.getHeaderRow("de", new String[]{"it"});
    assertThat(result, arrayWithSize(5));
    assertThat(result[0], equalTo("Key"));
    assertThat(result[1], equalTo("Status"));
    assertThat(result[2], equalTo("Master (de)"));
    assertThat(result[3], equalTo("Value (it)"));
    assertThat(result[4], equalTo("Context"));
  }
}
