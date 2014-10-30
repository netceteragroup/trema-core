package ch.netcetera.trema.core.exporting;

import ch.netcetera.trema.core.api.IKeyValuePair;


/**
 * Implements {@link IKeyValuePair}.
 */
public class KeyValuePair implements IKeyValuePair {


  private String key;
  private String value;

  /**
   * @param key the text key
   * @param value the text value
   */
  public KeyValuePair(String key, String value) {
    this.key = key;
    this.value = value;
  }


  /** {@inheritDoc} */
  @Override
  public String getKey() {
    return key;
  }

  /** {@inheritDoc} */
  @Override
  public String getValue() {
    return value;
  }

  /** {@inheritDoc} */
  @Override
  public void setKey(String key) {
    this.key = key;

  }

  /** {@inheritDoc} */
  @Override
  public void setValue(String value) {
    this.value = value;
  }

}
