package com.netcetera.trema.core.exporting;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.api.ITextNode;


/**
 * Exports an <code>IDatabase</code> to a CSV file.
 */
public class CSVExporter extends AbstractSpreadSheetExporter {

  public static final Logger LOG = LogManager.getLogger(MethodHandles.lookup().lookupClass());

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
    LOG.info("Exporting CSV file...");
    String[][] values = getValues(nodes, masterlanguage, language, states);
    try {
      printer.print(values);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    LOG.info("Exporting of CSV file finished.");
  }

}
