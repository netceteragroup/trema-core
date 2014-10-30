package ch.netcetera.trema.core.exporting;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import ch.netcetera.trema.core.Status;
import ch.netcetera.trema.core.TestConstants;
import ch.netcetera.trema.core.XMLDatabase;
import ch.netcetera.trema.core.api.IImportSource;
import ch.netcetera.trema.core.importing.XLSFile;


/**
 * Tests the XLSExporter.
 */
public class XLSExporterTest {

  /**
   * Tests for xls exporting. Exports a db then imports the file and does some
   * regression tests.
   * Columns in the xls are: Key | Status | Master (de) | Value (fr) | Context

   * @throws Exception in case of an error
   */
  @Test
  public void testExport() throws Exception {

    XMLDatabase db = new XMLDatabase();
    db.build(TestConstants.TESTXML, false);
    File output = new File("test.xls");
    XLSExporter exporter = new XLSExporter(output);
    exporter.export(db.getTextNodes(), db.getMasterLanguage(), "fr", Status.getAvailableStatus());

    IImportSource file = new XLSFile("test.xls");
    Assert.assertTrue(3 == file.getKeys().length);
    Assert.assertEquals("de", file.getMasterLanguage());
    Assert.assertEquals("masterValue1\u12AB", file.getMasterValue("key1"));
    Assert.assertEquals("masterValue2���", file.getMasterValue("key2"));
    Assert.assertEquals("masterValue3", file.getMasterValue("key3"));
    Assert.assertEquals("value1\u12AB", file.getValue("key1"));
    Assert.assertEquals("value2���", file.getValue("key2"));
    Assert.assertEquals("value3", file.getValue("key3"));

    File cleanup = new File("test.xls");
    cleanup.delete();

  }

  /**
   * Tests export of the masterlanguage into an xls and re-import of the same.
   * Columns in the xls are: Key | Status | Value (fr) | Context
   *
   * When the masterlanguage is exported into an xls then the xls doesnt have a Master Column.
   * This needs to be handled by the import
   *
   * @throws Exception in case of an error
   */
  @Test
  public void testExportMasterLanguage() throws Exception {

    XMLDatabase db = new XMLDatabase();
    db.build(TestConstants.TESTXML, false);
    File output = new File("test1.xls");
    XLSExporter exporter = new XLSExporter(output);
    exporter.export(db.getTextNodes(), db.getMasterLanguage(), "de", Status.getAvailableStatus());

    IImportSource file = new XLSFile("test1.xls");
    Assert.assertTrue(3 == file.getKeys().length);
    Assert.assertFalse(file.hasMasterLanguage());
    Assert.assertNull(file.getMasterValue("key1"));
    Assert.assertNull(file.getMasterValue("key2"));
    Assert.assertNull(file.getMasterValue("key3"));
    Assert.assertEquals("masterValue1\u12AB", file.getValue("key1"));
    Assert.assertEquals("masterValue2���", file.getValue("key2"));
    Assert.assertEquals("masterValue3", file.getValue("key3"));

    File cleanup = new File("test1.xls");
    cleanup.delete();

  }

  /**
   * Tests creation of the file header.
   */
  @Test
  public void testHeaderCreation() {
   XLSExporter exporter = new XLSExporter();
   String[] result = exporter.getHeaderRow("de", new String[]{"de"});
   Assert.assertEquals("Key", result[0]);
   Assert.assertEquals("Status", result[1]);
   Assert.assertEquals("Value (de)", result[2]);
   Assert.assertEquals("Context", result[3]);

   result = exporter.getHeaderRow("de", new String[]{"it"});
   Assert.assertEquals("Key", result[0]);
   Assert.assertEquals("Status", result[1]);
   Assert.assertEquals("Master (de)", result[2]);
   Assert.assertEquals("Value (it)", result[3]);
   Assert.assertEquals("Context", result[4]);
  }

}
