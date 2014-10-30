package com.netcetera.trema.core;

import com.netcetera.trema.core.api.IBaseNode;


/**
 * An abstract standard implementation of <code>IBaseNode</code>.
 */
public abstract class BaseNode implements IBaseNode {

  /** The parent of this base node. */
  private IBaseNode parent = null;

  /** {@inheritDoc} */
  @Override
  public IBaseNode getParent() {
    return parent;
  }

  /** {@inheritDoc} */
  @Override
  public void setParent(IBaseNode parent) {
    this.parent = parent;
  }

}
