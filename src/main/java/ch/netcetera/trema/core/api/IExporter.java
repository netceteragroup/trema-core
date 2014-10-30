package ch.netcetera.trema.core.api;

import ch.netcetera.trema.core.Status;
import ch.netcetera.trema.core.exporting.ExportException;


/**
 * Interface for exporting.
 */
public interface IExporter {
  
  /**
   * Exports the specified language/status using the printer.
   * 
   * @param nodes the nodes to export
   * @param masterlanguage the master language
   * @param language the language to export 
   * @param states the states to export (all if null)
   * @throws ExportException in case the export could not be done
   */
  void export(ITextNode [] nodes, String masterlanguage, String language, Status[] states)
  throws ExportException;

}
