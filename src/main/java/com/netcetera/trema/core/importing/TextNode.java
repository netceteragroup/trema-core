package com.netcetera.trema.core.importing;

import com.netcetera.trema.core.Status;



/**
 * Represents a text node (i.e. a key, a status, a value and possibly a
 * master value) in a trema CSV file. Note that there is no need to
 * store the context since it will be ignored during import.
 * <p>
 * This class should only be used internally by {@link CSVFile} or {@link XLSFile}}.
 */
class TextNode {
  
  private String key = null;
  private Status status = null;
  private String masterValue = null;
  private String value = null;
  
  /**
   * Constructs a new csv text element.
   * @param key the key
   * @param status the status of this text
   * @param masterValue the value of this text in the master language,
   * <code>null</code> if none
   * @param value the value of this text
   */
  public TextNode(String key, Status status, String masterValue, String value) {
    this.key = key;
    this.status = status;
    this.masterValue = masterValue;
    this.value = value;
  }
  
  /**
   * Gets the key of this text.
   * @return the key of this text.
   */
  public String getKey() {
    return key;
  }
  
  /**
   * Gets the master language value of this text.
   * @return the master language value of this text, might be
   * <code>null</code>
   */
  public String getMasterValue() {
    return masterValue;
  }
  
  /**
   * Gets the status of this text.
   * @return the status of this text.
   */
  public Status getStatus() {
    return status;
  }
  
  /**
   * Gets the value of this text.
   * @return the value of this text.
   */
  public String getValue() {
    return value;
  }
  
}
