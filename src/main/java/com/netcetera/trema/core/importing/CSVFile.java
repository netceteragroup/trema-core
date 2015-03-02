package com.netcetera.trema.core.importing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.netcetera.trema.core.ParseException;
import com.netcetera.trema.core.Status;

/**
 * Represents a CSV text resource file.
 */
public class CSVFile extends AbstractFile {

  public static final Logger LOG = LogManager.getLogger(MethodHandles.lookup().lookupClass());

  /** Error message if a wrong header format is encountered. */
  private static final String WRONG_HEADER_ERROR_MESSAGE =
    "Expected header format: Key;Status;[Master (<language>);]Value (<language>);Context";

  private String pathName = null;
  private String masterLanguage = null;
  private String language = null;

  /**
   * For unit test purposes.
   * @param masterLanguage the masterLanguage
   * @param language the language
   */
  protected CSVFile(String masterLanguage, String language) {
    this.masterLanguage = masterLanguage;
    this.language = language;
  }

  /**
   * Constructs a new CSV file from a path name.
   * @param pathName the path
   * @param encoding the encoding of the file
   * @param separator the CSV separator used in the file
   * @throws ParseException if any parse errors occurs
   * @throws IOException if any I/O errors occur
   */
  public CSVFile(String pathName, String encoding, char separator) throws ParseException, IOException {
    this.pathName = pathName;
    try (Reader reader = new InputStreamReader(new FileInputStream(pathName), encoding)) {
      parse(reader, separator);
    }
  }

  /**
   * Constructs a new CSV file from a string reader.
   * @param stringReader the string reader to read from
   * @param separator the CSV separator
   * @throws ParseException if any parse errors ocur
   * @throws IOException if any IO errors occur
   */
  public CSVFile(StringReader stringReader, char separator) throws ParseException, IOException {
    parse(stringReader, separator);
  }

  /**
   * Parses a CSV file from a given reader.
   * @param reader the reader
   * @param separator the CSV separator
   * @throws ParseException if any parse errors ocur
   * @throws IOException if any IO errors occur
   */
  private void parse(Reader reader, char separator) throws IOException, ParseException {
    LOG.info("Parsing CSV file...");
    /*
     * Ideally one would configure the format using withHeader(String ...) but since the master column is optional
     * this is of no use. Therefore, you'll get null if you were to call getHeaderMap() later on the parser. The header
     * is returned as the 1st record (i.e. row).
     */
    CSVParser csvParser = CSVFormat.DEFAULT.withDelimiter(separator).withIgnoreSurroundingSpaces(true).parse(reader);

    List<CSVRecord> records = csvParser.getRecords();

    String[] header = transformHeaderMapToArray(records.get(0));
    verifyHeader(header);

    for (int i = 1; i < records.size(); i++) {
      CSVRecord csvRecord = records.get(i);

      if (csvRecord.size() != header.length) {
        throw new ParseException(String.format("Expected %d columns but got %d.",  header.length, csvRecord.size()),
          i + 1);
      }
      String key = csvRecord.get(0);
      Status status = Status.valueOf(csvRecord.get(1));
      String masterValue = null;
      String value;
      if (hasMasterLanguage()) {
        masterValue = csvRecord.get(2);
        value = csvRecord.get(3);
      } else {
        value = csvRecord.get(2);
      }

      // the context is irrelevant, so it is not added
      add(key, status, masterValue, value);
    }
    LOG.info("Parsing of CSV file finished.");
  }

  private String[] transformHeaderMapToArray(CSVRecord headerRecord) {
    String[] header = new String[headerRecord.size()];
    for (int i = 0; i < headerRecord.size(); i++) {
      header[i] = headerRecord.get(i);
    }
    return header;
  }

  private void verifyHeader(String[] header) throws ParseException {
    if (header == null || header.length == 0) {
      throwWrongHeaderException();
    } else {
      verifyHeaderSize(header);
      verifyHeaderColumn(header, 0, AbstractFile.KEY_HEADER);
      verifyHeaderColumn(header, 1, AbstractFile.STATUS_HEADER);
      boolean masterHeaderPresent = hasHeaderColumnStartingWith(header, 2, AbstractFile.MASTER_HEADER);
      verifyHeaderColumnStartsWith(header, masterHeaderPresent ? 3 : 2, AbstractFile.VALUE_HEADER);
      verifyHeaderColumn(header, masterHeaderPresent ? 4 : 3, AbstractFile.CONTEXT_HEADER);
      if (masterHeaderPresent) {
        masterLanguage = extractLanguage(header[2]);
      }
      language = extractLanguage(header[masterHeaderPresent ? 3 : 2]);
      if (language.equals(masterLanguage)) {
        throw new ParseException("The master language cannot be the same as the exported language.", 1);
      }
    }
  }

  private void verifyHeaderColumnStartsWith(String[] header, int index, String expectedHaderNamePrefix)
      throws ParseException {
    if (!hasHeaderColumnStartingWith(header, index, expectedHaderNamePrefix)) {
      throwWrongHeaderException();
    }
  }

  private boolean hasHeaderColumnStartingWith(String[] header, int index, String expectedHeaderNamePrefix) {
    String headerName = header[index];
    return headerName != null && headerName.startsWith(expectedHeaderNamePrefix);
  }

  private void verifyHeaderColumn(String[] header, int index, String expectedHeaderName) throws ParseException {
    String headerName = header[index];
    if (headerName == null || !headerName.equals(expectedHeaderName)) {
      throwWrongHeaderException();
    }
  }

  private void verifyHeaderSize(String[] headerMap) throws ParseException {
    // header's got 4 or 5 elements (master lang is optional)
    if (headerMap.length < 4) {
      throwWrongHeaderException();
    }
  }

  private void throwWrongHeaderException() throws ParseException {
    throw new ParseException(WRONG_HEADER_ERROR_MESSAGE, 1);
  }

  /**
   * Extracts the language of the "master language value" and "value"
   * column heading in the first line of the trema CSV file.
   * @param columnHeading the column heading. The expected format is:
   * <code>master (&lt;lang&gt;)</code> or <code>value (&lt;lang&gt;)</code>.
   * @return the extracted language
   * @throws ParseException if any parse errors occur
   */
  private String extractLanguage(String columnHeading) throws ParseException {
    int start = columnHeading.indexOf('(');
    int end = columnHeading.indexOf(')');

    if (start == -1 || end == -1 || start >= end) {
      throwWrongHeaderException();
    }
    return columnHeading.substring(start + 1, end);
  }



  /**
   * Gets the pathname of this CSV file.
   * @return the pathname of this CSV file.
   */
  public String getPathname() {
    return pathName;
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasMasterLanguage() {
    return masterLanguage != null;
  }

  /** {@inheritDoc} */
  @Override
  public String getLanguage() {
    return language;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMasterLanguage() {
    return masterLanguage;
  }

}
