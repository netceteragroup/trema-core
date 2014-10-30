package ch.netcetera.trema.core.exporting;

import java.io.Writer;

import ch.netcetera.wake.core.format.csv.CSVPrinter;



/**
 * Subclass of <code>CSVPrinter</code> to support progress monitors,
 * since a CSV export has turned out to be rather a time consuming
 * operation.
 */
public class TremaCSVPrinter extends CSVPrinter {

  /**
   * Create a printer that will print values to the given
   * stream. Comments will be
   * written using the default comment character '#'.
   * @param out the writer to print to
   * @param separator the separator to use between entries
   */
  public TremaCSVPrinter(Writer out, char separator) {
    super(out);
    this.setSeparatorChar(separator);
  }

  /**
   * Print several lines of comma separated values.
   * The values will be quoted if needed. Quotes and
   * new line characters will be escaped.
   * @param values the values to be put out
   */
  @Override
  public void println(String[][] values) {
    for (int i = 0; i < values.length; i++) {
      println(values[i]);
    }
    if (values.length == 0) {
      out.println();
    }
    newLine = true;
    out.flush();
  }

}
