package com.netcetera.trema.core.api;


/**
 * A key value pair.
 */
public interface IKeyValuePair {

  /**
   * Gets the key.
   * @return the key
   */
  String getKey();
  /**
   * Gets the value.
   * @return the value
   */
  String getValue();
  /**
   * Sets the key.
   * @param key the key
   */
  void setKey(String key);
  /**
   * Sets the value.
   * @param value the value
   */
  void setValue(String value);

}
