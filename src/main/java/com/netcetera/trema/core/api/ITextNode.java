package com.netcetera.trema.core.api;





/**
 * Represents a text node in a Trema database. A text node consists of
 * key, a context and an alphabetically sordered set of 
 * <code>IValueNode</code>s, one for each language.
 */
public interface ITextNode extends IChildNode<IDatabase> {
  
  /**
   * Gets the key of this text node.
   * @return the key of this text node.
   */
  String getKey();
  
  /**
   * Sets the key of this text node.
   * @param key the key to set, must not be <code>null</code>
   */
  void setKey(String key);
  
  /**
   * Gets the context of this text node.
   * @return the context of this text node.
   */
  String getContext();
  
  /**
   * Sets the context of this text node.
   * @param context the context to set, must not be <code>null</code>
   */
  void setContext(String context);
  
  /**
   * Adds a value node (i.e. a language, a status and a value) to this
   * text node. If a value node for the specified language
   * already exists, it will be overwritten.
   * @param valueNode the value node to add, must not be
   * <code>null</code>
   */
  void addValueNode(IValueNode valueNode);
  
  /**
   * Removes a value node from this text node. If the given value node
   * does not exist, this method has no effect.
   * @param valueNode the value node to be removed, must not be
   * <code>null</code>
   */
  void removeValueNode(IValueNode valueNode);
  
  /**
   * Gets the value node for a given language.
   * @param language the language of the value node to get
   * @return the value node for the given language or <code>null</code>
   * if no value node exists for the given language.
   */
  IValueNode getValueNode(String language);
  
  /**
   * Gets all value nodes of this text node, ordered aplhabetically
   * by the corresponding languages.
   * @return all value nodes of this text node, ordered aplhabetically
   * by the corresponding languages.
   */
  IValueNode[] getValueNodes();
  
  /**
   * Gets all languages for this text node in alphabetical order.
   * @return all languages for this text node in alphabetical order.
   */
  String[] getLanguages();
  
  /**
   * Returns true if a value node for a given language exists.
   * @param language the language code
   * @return true if a value node for the given language exists.
   */
  boolean existsValueNode(String language);

  /**
   * Indicates that a valueNode has changed.
   * 
   * @param valueNode the valueNode that changed   
   */
  void fireValueNodeChanged(IValueNode valueNode);
  
  
  
}
