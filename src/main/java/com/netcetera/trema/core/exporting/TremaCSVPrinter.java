package com.netcetera.trema.core.exporting;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;



/**
 * Subclass of <code>CSVPrinter</code> to support progress monitors,
 * since a CSV export has turned out to be rather a time consuming
 * operation.
 */
public class TremaCSVPrinter   {

  private CSVPrinter csvPrinter;

  /**
   * Create a printer that will print values to the given
   * stream. Comments will be
   * written using the default comment character '#'.
   * @param out the writer to print to
   * @param delimiter the delimiter to use between entries
   */
  public TremaCSVPrinter(Writer out, char delimiter) {
    try {
      csvPrinter = new CSVPrinter(out, CSVFormat.DEFAULT.withDelimiter(delimiter));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Print several lines of comma separated values.
   * The values will be quoted if needed. Quotes and
   * new line characters will be escaped.
   * @param values the values to be put out
   */
  public void print(String[][] values) throws IOException {
    if (values == null || values.length == 0) {
      csvPrinter.println();
    } else {
      for (String[] value : values) {
        csvPrinter.printRecords((Object)value);
      }
    }
    csvPrinter.flush();
  }

}
