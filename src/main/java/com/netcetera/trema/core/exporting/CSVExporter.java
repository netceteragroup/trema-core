package com.netcetera.trema.core.exporting;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.api.ITextNode;

import java.io.IOException;


/**
 * Exports an <code>IDatabase</code> to a CSV file.
 */
public class CSVExporter extends AbstractSpreadSheetExporter {

  private TremaCSVPrinter printer;

  /**
   * Constructs a new instance of a <code>CSVExporter</code>. All text
   * nodes of the given database are considered during the export.
   *
   * @param printer the printer to use
   */
  public CSVExporter(TremaCSVPrinter printer) {
    this.printer = printer;
  }

  /**
   * For unit esting only constructor.
   */
  protected CSVExporter() {

  }

  /** {@inheritDoc} */
  @Override
  public void export(ITextNode[] nodes, String masterlanguage, String language, Status[] states) {
    String[][] values = getValues(nodes, masterlanguage, language, states);
    try {
      printer.print(values);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

}
