package ch.netcetera.trema.core.api;




/**
 * Basic node for a "parent relationship".
 */
public interface IBaseNode {
  
  /**
   * Gets the parent of this node.
   * @return the parent of this node
   */
  IBaseNode getParent();
  
  /**
   * Sets the parent of this node.
   * @param parent the parent to set
   */
  void setParent(IBaseNode parent);
  
}
