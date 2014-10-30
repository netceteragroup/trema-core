package ch.netcetera.trema.core;

import java.util.SortedMap;
import java.util.TreeMap;

import ch.netcetera.trema.core.api.IDatabase;
import ch.netcetera.trema.core.api.ITextNode;
import ch.netcetera.trema.core.api.IValueNode;



/**
 * Standard implementation of <code>ITextNode</code>. 
 */ 
public class XMLTextNode implements ITextNode {
  
  private String key = null;
  private String context = null;
  private SortedMap<String, IValueNode> valueNodeMap = null;
  private IDatabase parent = null;
  
  /**
   * Constructs a text node with no value nodes.
   * @param key the key of this text node
   * @param context the context of this text node
   */
  public XMLTextNode(String key, String context) {
    this(key, context, null);
  }
  
  /**
   * Constructs a text node with some value nodes.
   * @param key the key of this text node
   * @param context the context of this text node
   * @param valueNodes the value nodes of this text node
   */
  public XMLTextNode(String key, String context, IValueNode[] valueNodes) {
    this.key = key;
    this.context = context;
    valueNodeMap = new TreeMap<String, IValueNode>();
    if (valueNodes != null) {
      for (int i = 0; i < valueNodes.length; i++) {
        addValueNode(valueNodes[i]);
      }
    }
  }
  
  /** {@inheritDoc} */
  @Override
  public String getKey() {
    return key;
  }
  
  /** {@inheritDoc} */
  @Override
  public void setKey(String key) {
    if (!key.equals(this.key)) {
      this.key = key;
      if (parent != null) {
        ((XMLDatabase) parent).fireTextNodeChanged(this);
      }
    }
  }
  
  /** {@inheritDoc} */
  @Override
  public String getContext() {
    return context;
  }
  
  /** {@inheritDoc} */
  @Override
  public void setContext(String context) {
    if (!context.equals(this.context)) {
      this.context = context;
      if (parent != null) {
        ((XMLDatabase) parent).fireTextNodeChanged(this);
      }
    }
  }
  
  /** {@inheritDoc} */
  @Override
  public void addValueNode(IValueNode valueNode) {
    valueNodeMap.put(valueNode.getLanguage(), valueNode);
    valueNode.setParent(this);
    if (parent != null) {
      parent.fireValueNodeAdded(valueNode);
    }
  }
  
  /** {@inheritDoc} */
  @Override
  public void removeValueNode(IValueNode valueNode) {
    valueNodeMap.remove(valueNode.getLanguage());
    if (parent != null) {
      parent.fireValueNodeRemoved(valueNode);
    }
  }
  
  /** {@inheritDoc} */
  @Override
  public IValueNode getValueNode(String language) {
    if (language == null) {
      return null;
    }
    return valueNodeMap.get(language);
  }
  
  /**
   * Gets all value nodes of this text node, ordered aplhabetically
   * by the corresponding languages.
   * @return all value nodes of this text node, ordered aplhabetically
   * by the corresponding languages.
   */
  @Override
  public IValueNode[] getValueNodes() {
    return valueNodeMap.values().toArray(new IValueNode[valueNodeMap.size()]);
  }
  
  /** {@inheritDoc} */
  @Override
  public String[] getLanguages() {
    return valueNodeMap.keySet().toArray(new String[valueNodeMap.size()]);
  }
  
  /**
   * Returns true if a value node for a given language exists.
   * @param language the language code
   * @return true if a value node for the given language exists.
   */
  @Override
  public boolean existsValueNode(String language) {
    return valueNodeMap.containsKey(language);
  }
  
  /** {@inheritDoc} */
  @Override
  public void fireValueNodeChanged(IValueNode valueNode) {
    if (parent != null) {
      parent.fireValueNodeChanged(valueNode);
    }
  }

  /** {@inheritDoc} */
  @Override
  public IDatabase getParent() {
    return parent;
  }

  /** {@inheritDoc} */
  @Override
  public void setParent(IDatabase parent) {
    this.parent = parent;
  }
  
}


