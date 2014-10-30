package com.netcetera.trema.core.importing;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import com.netcetera.trema.core.ParseException;
import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.importing.CSVFile;



/**
 * Unit test for the <code>CSVFile</code> class.
 */
public class TestCSVFile {


  /**
   * CSV file with master language.
   * @throws Exception incase the test fails
   */
  @Test
  public void testValidFile1() throws Exception {
    StringBuilder contents = new StringBuilder();
    contents.append("Key;Status;Master (de);Value (fr);Context\n");
    contents.append("key1;initial;masterValue1;value1���;context1\n");
    contents.append("key2;translated;masterValue2;value2;context2");
    CSVFile csvFile = new CSVFile(new StringReader(contents.toString()), ';');

    Assert.assertTrue(csvFile.hasMasterLanguage());
    Assert.assertEquals(2, csvFile.getSize());
    Assert.assertEquals("de", csvFile.getMasterLanguage());
    Assert.assertEquals("fr", csvFile.getLanguage());
    Assert.assertFalse(csvFile.existsKey("KEY1"));

    // key1
    Assert.assertTrue(Status.INITIAL == csvFile.getStatus("key1"));
    Assert.assertEquals("masterValue1", csvFile.getMasterValue("key1"));
    Assert.assertEquals("value1���", csvFile.getValue("key1"));

    // key2
    Assert.assertTrue(Status.TRANSLATED == csvFile.getStatus("key2"));
    Assert.assertEquals("masterValue2", csvFile.getMasterValue("key2"));
    Assert.assertEquals("value2", csvFile.getValue("key2"));
  }

  /**
   * CSV file with no master language.
   *
   * @throws Exception in case the test fails
   */
  public void testValidFile2() throws Exception {
    StringBuilder contents = new StringBuilder();
    contents.append("Key;Status;Value (de);Context\n");
    contents.append("key1;initial;masterValue1;context1\n");
    contents.append("key2;translated;masterValue2;context2");
    CSVFile csvFile = new CSVFile(new StringReader(contents.toString()), ';');

    Assert.assertFalse(csvFile.hasMasterLanguage());
    Assert.assertEquals(2, csvFile.getSize());
    Assert.assertEquals("de", csvFile.getLanguage());
    Assert.assertFalse(csvFile.existsKey("KEY1"));

    // key1
    Assert.assertTrue(Status.INITIAL == csvFile.getStatus("key1"));
    Assert.assertEquals("masterValue1", csvFile.getValue("key1"));
    Assert.assertNull(csvFile.getMasterValue("key1"));

    // key2
    Assert.assertTrue(Status.TRANSLATED == csvFile.getStatus("key2"));
    Assert.assertEquals("masterValue2", csvFile.getValue("key2"));
    Assert.assertNull(csvFile.getMasterValue("key2"));
  }

  /**
   * Invalid header.
   *
   * @throws Exception incase the test fails
   */
  @SuppressWarnings("unused")
  public void testInvalidHeader() throws Exception {
    try {
      new CSVFile(new StringReader("Key;Status;Master;Value (fr);Context"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      // this is good
    }

    try {
      new CSVFile(new StringReader("Key;Status;Master (de);Value;Context"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      // this is good
    }

    try {
      new CSVFile(new StringReader("Key;Status;Master (de);Value (de);Context"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      // this is good
    }

    try {
      new CSVFile(new StringReader("Key;Master (de);Value (fr);Context"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      // this is good
    }

    try {
      new CSVFile(new StringReader("invalid;Status;Master (de);Context"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      // this is good
    }

    try {
      new CSVFile(new StringReader("Key;Status;Status (de);Value (fr);Context"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      // this is good
    }

    try {
      new CSVFile(new StringReader("Key;Status;Value (de);Master (fr);Context"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      // this is good
    }

    try {
      new CSVFile(new StringReader("Key;Status;Value (de)"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      // this is good
    }

    try {
      new CSVFile(new StringReader("Key;Status;"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      // this is good
    }

    try {
      new CSVFile(new StringReader("Key;Status;Value (de);Master (fr);context"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      // this is good
    }

    try {
      new CSVFile(new StringReader("Key;Status;Value (de);Master (fr);context"), ',');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      // this is good
    }
  }

  /**
   * Invalid rows.
   * @throws Exception incase the test fails
   */
  @SuppressWarnings("unused")
  public void testInvalidRows() throws Exception {
    try {
      new CSVFile(new StringReader(
          "Key;Status;Master (de);Value (fr);Context\n"
          + "key1;"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      Assert.assertEquals(2, e.getLineNumber());
    }

    try {
      new CSVFile(new StringReader(
          "Key;Status;Master (de);Value (fr);Context\n"
          + "key1;invalidStatus;masterValue1;value1;context1"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      Assert.assertEquals(2, e.getLineNumber());
    }

    try {
      new CSVFile(new StringReader(
          "Key;Status;Master (de);Value (fr);Context\n"
          + "key1;translated;masterValue1;value1;context1;"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      Assert.assertEquals(2, e.getLineNumber());
    }

    try {
      new CSVFile(new StringReader(
          "Key;Status;Master (de);Value (fr);Context\n"
          + "key1,translated,masterValue1,value1,context1"), ';');
      Assert.fail("Expected exception was not thrown.");
    } catch (ParseException e) {
      Assert.assertEquals(2, e.getLineNumber());
    }
  }

}
