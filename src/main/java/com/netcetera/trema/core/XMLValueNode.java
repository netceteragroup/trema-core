package com.netcetera.trema.core;

import com.netcetera.trema.core.api.ITextNode;
import com.netcetera.trema.core.api.IValueNode;



/** Standard implementation of <code>IValueNode</code>. */
public class XMLValueNode implements IValueNode {
  
  private String language = null;
  private Status status = null;
  private String value = null;
  private ITextNode parent = null;
  
  /**
   * Constructs a value node.
   * @param language the lanugage of this value node
   * @param status the status of this value node
   * @param value the valu of this value node
   */
  public XMLValueNode(String language, Status status, String value) {
    this.language = language;
    this.status = status;
    this.value = value;
  }
  
  /** {@inheritDoc} */
  @Override
  public String getLanguage() {
    return language;
  }
  
  /** {@inheritDoc} */
  @Override
  public Status getStatus() {
    return status;
  }
  
  /** {@inheritDoc} */
  @Override
  public void setStatus(Status status) {
    if (!status.equals(this.status)) {
      this.status = status;
      if (parent != null) {
        parent.fireValueNodeChanged(this);
      }
    }
  }
  
  /** {@inheritDoc} */
  @Override
  public String getValue() {
    return value;
  }
  
  /** {@inheritDoc} */
  @Override
  public void setValue(String value) {
    if (!value.equals(this.value)) {
      this.value = value;
      if (parent != null) {
        parent.fireValueNodeChanged(this);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public void setParent(ITextNode parent) {
    this.parent = parent;
  }
  
  /** {@inheritDoc} */
  @Override
  public ITextNode getParent() {
    return parent;
  }
  
}
