package ch.netcetera.trema.core.api;



/**
 * Interface for filtering text values while exporting trm to properties.
 */
public interface IExportFilter {

  /**
   * Processes the value and returns the result.
   *
   * @param keyValuePair the key value object
   */
  void filter(IKeyValuePair keyValuePair);

}
