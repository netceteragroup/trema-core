package com.netcetera.trema.core.api;

import java.util.Collection;



/**
 * Represents a writable Trema text resource database for multiple
 * languages, such as a trema XML file. An <code>IDatabase</code>
 * consists of a master language and an ordered list of
 * <code>ITextNode</code>s, which in turn consist of a number of
 * <code>IValueNode</code>s.
 */
public interface IDatabase extends INode {
  
  /**
   * Gets the master language of this database.
   * @return the master language.
   */
  String getMasterLanguage();
  
  /**
   * Sets the master language of this database.
   * @param masterLanguage the master language to set
   */
  void setMasterLanguage(String masterLanguage);
  
  /**
   * Gets the number of text nodes (keys) in this database.
   * @return the number of text nodes (keys) in this database.
   */
  int getSize();
  
  /**
   * Gets all text nodes of this database.
   * @return all text nodes of this database.
   */ 
  ITextNode[] getTextNodes();
  
  /**
   * Adds a text node to the end of this database.
   * @param textNode the text node to add
   */
  void addTextNode(ITextNode textNode);
  
  /**
   * Adds a text node at a given position to this database.
   * @param position the position to add the text node to
   * @param textNode the text node to add
   */
  void addTextNode(int position, ITextNode textNode);
  
  /**
   * Removes a text node from this database. If a text node does not
   * exist it will be ignored.
   * @param textNodes the text nodes to remove
   */
  void removeTextNodes(ITextNode[] textNodes);
  
  /**
   * Removes a text node given by its key from this database. If the
   * text node does not exist this method has no effect.
   * @param key the key of the text node to remove
   */
  void removeTextNode(String key);
  
  /**
   * Moves a text node to a given position.
   * @param textNode the text node to be moved
   * @param targetIndex the target position
   */
  void moveTextNode(ITextNode textNode, int targetIndex);
  
  /**
   * Moves given text nodes up by 1 position. If a text node is at the
   * top it will be ignored.
   * @param textNodes the text nodes to move up. The text nodes
   * mus be <b>ascendingly ordered</b> by their position in the
   * database.
   */
  void moveUpTextNodes(ITextNode[] textNodes);

  /**
   * Moves given text nodes down by 1 position. If a text node is at
   * bottom it will be ignored.
   * @param textNodes the text nodes to move down. The text nodes
   * must be <b>ascendingly ordered</b> by their position in the
   * database.
   */
  void moveDownTextNodes(ITextNode[] textNodes);
  
  /**
   * Gets the position of a given text node in the database.
   * @param textNode the node to query
   * @return the position of a given text node in the database.
   */
  int indexOf(ITextNode textNode);
  
  /**
   * Returns the text node to a given position.
   * @param index the position
   * @return the text node or <code>null</code> if no text node could
   * be found.
   */
  ITextNode getTextNode(int index);
  

  /**
   * Returns the text node for a given key.
   * 
   * @param key the key
   * @return the text node or <code>null</code> if no text node could
   */
  ITextNode getTextNode(String key);
  
  /**
   * Tests if a text node with a given key exists in this database.
   * @param key the key to look for
   * @return true if a text node with the given key exists in this
   * database.
   */
  boolean existsTextNode(String key);
  
  /**
   * Returns all the keys of the database as <code>Collection</code>.
   * @return the keys as <code>Set</code>.
   */
  Collection<String> getKeysAsCollection();
 
  /**
   * Adds an event listener to this database.
   * @param listener the listener to add
   */
  void addListener(IDatabaseListener listener);
  
  /**
   * Removes an event listener from this database.
   * @param listener the listener to remove
   */
  void removeListener(IDatabaseListener listener);

  /**
   * Indicates that a value node has changed.
   * 
   * @param valueNode the node
   */
  void fireValueNodeChanged(IValueNode valueNode);

  /**
   * Indicates that a value node was added.
   * 
   * @param valueNode the node
   */
  void fireValueNodeAdded(IValueNode valueNode);

  /**
   * Indicates that a value node was removed.
   * 
   * @param valueNode the node
   */
  void fireValueNodeRemoved(IValueNode valueNode);
  
}
