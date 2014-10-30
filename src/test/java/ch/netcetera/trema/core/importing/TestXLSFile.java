package ch.netcetera.trema.core.importing;


import org.junit.Assert;
import org.junit.Test;

import ch.netcetera.trema.core.Status;
import ch.netcetera.trema.core.api.IImportSource;


/**
 * Test for xls file import.
 */
public class TestXLSFile {
  /**
   * Read from xls file.
   *
   * @throws Exception in case the test fails
   */
  @Test
  public void testReadXLS() throws Exception {
    // file contains a text, blank field, and numeric field that must be imported
    IImportSource xls = new XLSFile("src/test/resources/test.xls");
    String[] keys = xls.getKeys();
    Assert.assertEquals(xls.getValue(keys[0]), "value1");
    Assert.assertEquals(xls.getValue(keys[1]), "value2öäü");
    Assert.assertEquals(xls.getValue(keys[2]), "");
    Assert.assertEquals(xls.getValue(keys[3]), "2.2");
  }


  /**
   * The file used here contains two 'empty' rows at the end (row 4 + 5). They must be ignored in the import.
   * Test also checks that its possible to import a blank text value,
   * and that an unknown status in the xls is translated to status undefined.
   *
   * @throws Exception exception
   */
  @Test
  public void testReadXLSEmptyLinesInFile() throws Exception {
    // file contains a text, blank field, and numeric field that must be imported
    IImportSource xls = new XLSFile("src/test/resources/test_import.xls");
    String[] keys = xls.getKeys();
    Assert.assertEquals(xls.getValue(keys[0]), "Value1");
    // a blank value is tolerated
    Assert.assertEquals(xls.getValue(keys[1]), "");
    Assert.assertEquals(xls.getValue(keys[2]), "Value3");

    Assert.assertEquals(xls.getMasterValue(keys[0]), "MasterValue1");
    Assert.assertEquals(xls.getMasterValue(keys[1]), "MasterValue2");
    Assert.assertEquals(xls.getMasterValue(keys[2]), "MasterValue3");

    // status 'evil' is undefined and converted to Status.UNDEFINED
    Assert.assertEquals(xls.getStatus(keys[0]), Status.UNDEFINED);
    Assert.assertEquals(xls.getStatus(keys[1]), Status.INITIAL);
    Assert.assertEquals(xls.getStatus(keys[2]), Status.INITIAL);
  }
}
