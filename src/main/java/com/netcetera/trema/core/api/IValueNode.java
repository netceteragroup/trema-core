package com.netcetera.trema.core.api;

import com.netcetera.trema.core.Status;



/**
 * Represents a value node of a <code>ITextNode</code>. A value node
 * consists of a language, a status and a value. Note that changing
 * the language is not a permitted operation for a value node.
 */
public interface IValueNode extends IChildNode<ITextNode> {

  /**
   * Gets the language of this value node.
   * @return the language of this value node.
   */
  String getLanguage();

  /**
   * Gets the status of this value node.
   * @return the status of this value node.
   */
  Status getStatus();

  /**
   * Sets the status of this value node.
   * @param status the status to set, must not be <code>null</code>
   */
  void setStatus(Status status);

  /**
   * Gets the value of this value node.
   * @return the value of this value node.
   */
  String getValue();

  /**
   * Sets the value of this value node.
   * @param value the value to set, must not be <code>null</code>
   */
  void setValue(String value);

  /** {@inheritDoc} */
  @Override
  void setParent(ITextNode parent);

  /** {@inheritDoc} */
  @Override
  ITextNode getParent();

}
