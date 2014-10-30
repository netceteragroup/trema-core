package com.netcetera.trema.core.importing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import com.netcetera.trema.core.ParseException;
import com.netcetera.trema.core.Status;

import ch.netcetera.wake.core.format.csv.CSVParser;



/**
 * Represents a CSV text resource file.
 */
public class CSVFile extends AbstractFile {
  
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
   * @throws ParseException if any parse errors ocur
   * @throws UnsupportedEncodingException if the given encoding is not
   * supported
   * @throws IOException if any IO errors occur
   */
  public CSVFile(String pathName, String encoding, char separator)
  throws ParseException, UnsupportedEncodingException, IOException {
    this.pathName = pathName;
    Reader reader = new InputStreamReader(new FileInputStream(pathName), encoding);
    try {
      parse(reader, separator);
    } finally {
      reader.close();
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
    CSVParser csvParser = new CSVParser(reader, separator);
    csvParser.setIgnoreEmptyLines(true);
    csvParser.setIgnoreLeadingWhitespaces(true);
    
    String[][] allValues = csvParser.getAllValues();
    if (allValues != null && allValues.length > 0) {
      String[] headerRow = allValues[0];
      int currentColumn = -1;
      
      // key
      if (headerRow.length < 4 || !headerRow[++currentColumn].equalsIgnoreCase(AbstractFile.KEY_HEADER)) {
        throw new ParseException(WRONG_HEADER_ERROR_MESSAGE, 1);
      }
      
      // status
      if (!headerRow[++currentColumn].equalsIgnoreCase(AbstractFile.STATUS_HEADER)) {
        throw new ParseException(WRONG_HEADER_ERROR_MESSAGE, 1);
      }
      
      // master
      if (startsWithIgnoreCase(headerRow[++currentColumn], AbstractFile.MASTER_HEADER)) {
        masterLanguage = extractLanguage(headerRow[currentColumn]);
      } else {
        currentColumn--;
      }
      
      // value
      if (startsWithIgnoreCase(headerRow[++currentColumn], AbstractFile.VALUE_HEADER)) {
        language = extractLanguage(headerRow[currentColumn]);        
        if (language.equals(masterLanguage)) {
          throw new ParseException("The master language cannot be the same as the exported langugae.", 1);
        }
      } else {
        throw new ParseException(WRONG_HEADER_ERROR_MESSAGE, 1);
      }
      
      // context
      if (!headerRow[++currentColumn].equalsIgnoreCase(AbstractFile.CONTEXT_HEADER)) {
        throw new ParseException(WRONG_HEADER_ERROR_MESSAGE, 1);
      }
      
      // loop over the keys
      int numberOfColumns = currentColumn + 1;
      for (int i = 1; i < allValues.length; i++) {
        String[] row = allValues[i];
        
        if (row.length == 1 && row[0].length() == 0) {
          // even though csvParser.setIgnoreEmptyLines(true) was stated, the CSV parser
          // does not ignore trailing empty lines
          continue;
        }
        
        if (row.length != numberOfColumns) {
          throw new ParseException("Expected " + numberOfColumns + " columns, but got " + row.length + ".", i + 1);
        }
        currentColumn = 0;
        String key = row[currentColumn++];
        String statusName = row[currentColumn++];
        Status status = Status.valueOf(statusName);
        if (status == null) {
          throw new ParseException("Invalid status: " + statusName, i + 1);
        }
        String masterValue = null;
        if (hasMasterLanguage()) {
          masterValue = row[currentColumn++];
        }
        String value = row[currentColumn++];
        
        // the context is irrelevant, so it is not added
        add(key, status, masterValue, value);
      }
    }
  }
  
  private boolean startsWithIgnoreCase(String string, String prefix) {
    return string.toLowerCase().startsWith(prefix.toLowerCase());
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
      throw new ParseException(WRONG_HEADER_ERROR_MESSAGE, 1);
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
