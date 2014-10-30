package com.netcetera.trema.core.exporting;

import com.netcetera.trema.core.api.IExportFilter;
import com.netcetera.trema.core.api.IKeyValuePair;


/**
 * Appends the key to the value. Can be useful for debugging purposes to see which key is used at what place.
 */
public class AddKeyToValueExportFilter implements IExportFilter {

  /** {@inheritDoc} */
  @Override
  public void filter(IKeyValuePair keyValuePair) {
    String value = keyValuePair.getValue();
    if (value != null) {
      keyValuePair.setValue(value + " [" + keyValuePair.getKey() + "]");
    } else {
      keyValuePair.setValue("[" + keyValuePair.getKey() + "]");
    }
  }
}
