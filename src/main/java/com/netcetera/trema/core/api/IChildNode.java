package com.netcetera.trema.core.api;




/**
 * Basic node for a "parent relationship".
 * @param <T> the type of this nodes parent.
 */
public interface IChildNode<T extends INode> extends INode {
  
  /**
   * Gets the parent of this node.
   * @return the parent of this node
   */
  T getParent();
  
  /**
   * Sets the parent of this node.
   * @param parent the parent to set
   */
  void setParent(T parent);
  
}
