package com.netcetera.trema.core.importing;

import java.util.LinkedHashMap;
import java.util.Map;

import com.netcetera.trema.core.Status;
import com.netcetera.trema.core.api.IImportSource;


/**
 * A set of constants used in xls and csv files.
 */
public abstract class AbstractFile implements IImportSource {
  
  /** The header for the "key" column. */
  public static final String KEY_HEADER = "Key";
  
  /** The header for the "status" column. */
  public static final String STATUS_HEADER = "Status";
  
  /**
   * The header for the "master language" column.
   * <code>" (&lt;master language&gt;)"</code> will be appended to this
   * value.
   */
  public static final String MASTER_HEADER = "Master";
  
  /**
   * The header for the "value" column.
   * <code>" (&lt;language&gt;)"</code> will be appended to this
   * value.
   */
  public static final String VALUE_HEADER = "Value";
  
  /** The header for the "context" column. */
  public static final String CONTEXT_HEADER = "Context";
  
  private Map<String, TextNode> textNodeMap = new LinkedHashMap<String, TextNode>();

  
  /**
   * Gets the textNodeMap.
   * 
   * @return the map
   */
  protected Map<String, TextNode> getTextNodeMap() {
    return textNodeMap;
  }

  
  /**
   * Sets the textNodeMap.
   * 
   * @param textNodeMap the map
   */
  protected void setTextNodeMap(Map<String, TextNode> textNodeMap) {
    this.textNodeMap = textNodeMap;
  }
  
  /**
   * Adds a record to this CSV file.
   * @param key the key
   * @param status the status
   * @param masterValue the master value, may be <code>null</code>
   * @param value the value
   */
  protected void add(String key, Status status, String masterValue, String value) {
    textNodeMap.put(key, new TextNode(key, status, masterValue, value));
  }
  
  /** {@inheritDoc} */
  @Override
  public int getSize() {
    return textNodeMap.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getValue(String key) {
    if (textNodeMap.containsKey(key)) {
      TextNode textNode = textNodeMap.get(key);
      return textNode.getValue();
    }
    
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMasterValue(String key) {
    if (textNodeMap.containsKey(key)) {
      TextNode textNode = textNodeMap.get(key);
      return textNode.getMasterValue();
    }
    
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Status getStatus(String key) {
    if (textNodeMap.containsKey(key)) {
      TextNode textNode = textNodeMap.get(key);
      return textNode.getStatus();
    }
    
    return null;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean existsKey(String key) {
    return textNodeMap.containsKey(key);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getKeys() {
    return textNodeMap.keySet().toArray(new String[textNodeMap.size()]);
  }
  
}
