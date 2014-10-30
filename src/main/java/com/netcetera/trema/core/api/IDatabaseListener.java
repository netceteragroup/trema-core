package com.netcetera.trema.core.api;




/**
 * A listener wanting to be notified of changes in a database.
 */
public interface IDatabaseListener {
  
  /**
   * Handles the event of a change in the master language of a
   * database.
   * @param db the database whose master language has changed
   */
  void masterLanguageChanged(IDatabase db);

  /**
   * Handles the event of a change in a text node (a change in the key
   * or in the context, that is). If a value node was changed, added
   * or removed,
   * {@link IDatabaseListener#valueNodeChanged(IValueNode)},
   * {@link IDatabaseListener#valueNodeAdded(IValueNode)} and
   * {@link IDatabaseListener#valueNodeRemoved(IValueNode)}
   * are the appropriate handlers.
   * @param db the parent database of the text node
   * @param textNode the text node that has changed
   */
  void textNodeChanged(IDatabase db, ITextNode textNode);
  
  /**
   * Handles the event of an addition of a text node to the database.
   * @param db the database the text node has been added to
   * @param textNode the text node that has been added
   */
  void textNodeAdded(IDatabase db, ITextNode textNode);
  
  /**
   * Handles the event of a removal of some text node from the
   * database.
   * @param db the database the text node has been removed from
   * @param textNodes the text nodes that have been removed
   * @param index the position of the topmost text node that has been
   * removed
   */
  void textNodesRemoved(IDatabase db, ITextNode[] textNodes, int index);
  
  /**
   * Handles the event of a movement of some text nodes.
   * @param db the parent database of the text node
   * @param textNodes the text nodes that have been moved
   */
  void textNodesMoved(IDatabase db, ITextNode[] textNodes);
  
  /**
   * Handles the event of a change in a value node (a change in the
   * value or the status, that is). Note that the language of a value
   * node is <b>not</b> supposed to be changed.
   * @param valueNode the value node that has been changed
   */
  void valueNodeChanged(IValueNode valueNode);
  
  /**
   * Handles the event of an addition of a value node to a text node.
   * to
   * @param valueNode the value node that has been added
   */
  void valueNodeAdded(IValueNode valueNode);
  
  /**
   * Handles the event of an removal of a value node from a text node.
   * removed from
   * @param valueNode the value node that has been removed
   */
  void valueNodeRemoved(IValueNode valueNode);
  
}
