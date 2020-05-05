package com.netcetera.trema.core.importing;

import com.netcetera.trema.core.ParseException;
import com.netcetera.trema.core.Status;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Test for {@link CSVFile}.
 */
class CSVFileTest {

  /**
   * According to RFC 4180, line breaks are delimited by CRLF.
   */
  private static final String CRLF = "\r\n";

  /**
   * CSV file with master language.
   *
   * @throws Exception in case the test fails
   */
  @Test
  void testValidFile1() throws Exception {
    String contents = "Key;Status;Master (de);Value (fr);Context"
      + CRLF + "key1;initial;masterValue1;value1öäü;context1"
      + CRLF + "key2;translated;masterValue2;value2;context2";
    CSVFile csvFile = new CSVFile(new StringReader(contents), ';');

    assertThat(csvFile.hasMasterLanguage(), equalTo(true));
    assertThat(csvFile.getSize(), equalTo(2));
    assertThat(csvFile.getMasterLanguage(), equalTo("de"));
    assertThat(csvFile.getLanguage(), equalTo("fr"));
    assertThat(csvFile.existsKey("KEY1"), equalTo(false));

    // key1
    assertThat(csvFile.getStatus("key1"), equalTo(Status.INITIAL));
    assertThat(csvFile.getMasterValue("key1"), equalTo("masterValue1"));
    assertThat(csvFile.getValue("key1"), equalTo("value1öäü"));

    // key2
    assertThat(csvFile.getStatus("key2"), equalTo(Status.TRANSLATED));
    assertThat(csvFile.getMasterValue("key2"), equalTo("masterValue2"));
    assertThat(csvFile.getValue("key2"), equalTo("value2"));
  }

  /**
   * CSV file with no master language.
   *
   * @throws Exception in case the test fails
   */
  @Test
  void testValidFile2() throws Exception {
    String contents = "Key;Status;Value (de);Context"
      + CRLF + "key1;initial;masterValue1;context1"
      + CRLF + "key2;translated;masterValue2;context2";
    CSVFile csvFile = new CSVFile(new StringReader(contents), ';');

    assertThat(csvFile.hasMasterLanguage(), equalTo(false));
    assertThat(csvFile.getSize(), equalTo(2));
    assertThat(csvFile.getLanguage(), equalTo("de"));
    assertThat(csvFile.existsKey("KEY1"), equalTo(false));

    // key1
    assertThat(csvFile.getStatus("key1"), equalTo(Status.INITIAL));
    assertThat(csvFile.getValue("key1"), equalTo("masterValue1"));
    assertThat(csvFile.getMasterValue("key1"), nullValue());

    // key2
    assertThat(csvFile.getStatus("key2"), equalTo(Status.TRANSLATED));
    assertThat(csvFile.getValue("key2"), equalTo("masterValue2"));
    assertThat(csvFile.getMasterValue("key2"), nullValue());
  }

  @Test
  void shouldThrowForInvalidHeaders() {
    assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("Key;Status;Master;Value (fr);Context"), ';'));

    assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("Key;Status;Master (de);Value;Context"), ';'));

    assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("Key;Status;Master (de);Value (de);Context"), ';'));

    assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("Key;Master (de);Value (fr);Context"), ';'));

    assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("invalid;Status;Master (de);Context"), ';'));

    assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("Key;Status;Status (de);Value (fr);Context"), ';'));

    assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("Key;Status;Value (de);Master (fr);Context"), ';'));

    assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("Key;Status;Value (de)"), ';'));

    assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("Key;Status;"), ';'));

    assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("Key;Status;Value (de);Master (fr);context"), ';'));

    assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("Key;Status;Value (de);Master (fr);context"), ','));
  }

  @Test
  void shouldThrowForInvalidRows() {
    ParseException ex = assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("Key;Status;Master (de);Value (fr);Context" + CRLF + "key1;"), ';'));
    assertThat(ex.getLineNumber(), equalTo(2));

    ParseException ex2 = assertThrows(ParseException.class,
      () -> new CSVFile(new StringReader("Key;Status;Master (de);Value (fr);Context" + CRLF + "key1;translated;masterValue1;value1;context1;"), ';'));
    assertThat(ex2.getLineNumber(), equalTo(2));
  }

}
