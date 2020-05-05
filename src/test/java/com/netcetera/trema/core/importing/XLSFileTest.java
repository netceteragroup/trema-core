package com.netcetera.trema.core.importing;


import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.api.IImportSource;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;


/**
 * Test for {@link XLSFile}.
 */
class XLSFileTest {

  /**
   * Read from xls file.
   *
   * @throws Exception in case the test fails
   */
  @Test
  void shouldReadXlsFile() throws Exception {
    // given / when
    // file contains a text, blank field, and numeric field that must be imported
    IImportSource xls = new XLSFile("src/test/resources/test.xls");

    // then
    String[] keys = xls.getKeys();
    assertThat(keys, arrayWithSize(4));
    assertThat(xls.getValue(keys[0]), equalTo("value1"));
    assertThat(xls.getValue(keys[1]), equalTo("value2öäü"));
    assertThat(xls.getValue(keys[2]), equalTo(""));
    assertThat(xls.getValue(keys[3]), equalTo("2.2"));
  }


  /**
   * The file used here contains two 'empty' rows at the end (row 4 + 5). They must be ignored in the import.
   * Test also checks that its possible to import a blank text value,
   * and that an unknown status in the xls is translated to status undefined.
   *
   * @throws Exception exception
   */
  @Test
  void shouldReadXlsFileAndIgnoreEmptyLines() throws Exception {
    // given / when
    // file contains a text, blank field, and numeric field that must be imported
    IImportSource xls = new XLSFile("src/test/resources/test_import.xls");

    // then
    String[] keys = xls.getKeys();
    assertThat(keys, arrayWithSize(3));
    assertThat(xls.getValue(keys[0]), equalTo("Value1"));
    // a blank value is tolerated
    assertThat(xls.getValue(keys[1]), equalTo(""));
    assertThat(xls.getValue(keys[2]), equalTo("Value3"));

    assertThat(xls.getMasterValue(keys[0]), equalTo("MasterValue1"));
    assertThat(xls.getMasterValue(keys[1]), equalTo("MasterValue2"));
    assertThat(xls.getMasterValue(keys[2]), equalTo("MasterValue3"));

    // status 'evil' is undefined and converted to Status.UNDEFINED
    assertThat(xls.getStatus(keys[0]), equalTo(Status.UNDEFINED));
    assertThat(xls.getStatus(keys[1]), equalTo(Status.INITIAL));
    assertThat(xls.getStatus(keys[2]), equalTo(Status.INITIAL));
  }
}
