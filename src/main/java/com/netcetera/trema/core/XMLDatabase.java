package com.netcetera.trema.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.contrib.input.LineNumberElement;
import org.jdom.contrib.input.LineNumberSAXBuilder;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.netcetera.trema.core.api.IDatabase;
import com.netcetera.trema.core.api.IDatabaseListener;
import com.netcetera.trema.core.api.ITextNode;
import com.netcetera.trema.core.api.IValueNode;



/**
 * Represents a Trema XML text resource database.
 * <p>
 * Uses an AVL <code>TreeList</code> for storing the text nodes.
 */
public class XMLDatabase implements IDatabase {

  // xml element and attribute names
  private static final String ROOT_ELEMENT_NAME = "trema";
  private static final String TEXT_ELEMENT_NAME = "text";
  private static final String CONTEXT_ELEMENT_NAME = "context";
  private static final String VALUE_ELEMENT_NAME = "value";
  private static final String MASTER_LANGUAGE_ATTRIBUTE_NAME = "masterLang";
  private static final String KEY_ATTRIBUTE_NAME = "key";
  private static final String LANGUAGE_ATTRIBUTE_NAME = "lang";
  private static final String STATUS_ATTRIBUTE_NAME = "status";

  private List<IDatabaseListener> listeners = new ArrayList<IDatabaseListener>();
  private String masterLanguage = null;
  //root element attributes besides "masterLang"
  private List<Attribute> additionalRootAttrs = new ArrayList<Attribute>();
  private List<Namespace> additionalNamespaces = new ArrayList<Namespace>();
  private List<ITextNode> textNodeList = new ArrayList<ITextNode>();
  private boolean treatWarningsAsErrors = true;
  private List<ParseWarning> parseWarnings = new ArrayList<ParseWarning>();
  private boolean xmlInternalized = false;


  /**
   * Builds and internalizes this database from a given input stream
   * containing an XML document.
   * <p>
   * This method can be called repeatedly on the same instance, but the
   * database will be built from scratch each time.
   * <p>
   * The registered database listeners will <b>not</b> be notified
   * during this method.
   * @param inputStream the input stream to read from
   * document
   * @param treatWarningsAsError if true, warnings during parsing will
   * trigger a <code>ParseException</code>. If false, no exception will
   * be thrown if the parser reports a warning. In the latter case, the
   * warnings can be obtained using the <code>getParseWarnings()</code>
   * method.
   * @throws IOException if any IO errors occur
   * @throws ParseException if any parse errors occur
   */
  public void build(InputStream inputStream, boolean treatWarningsAsError)
  throws IOException, ParseException {
    build(new InputSource(inputStream), treatWarningsAsError);
  }

  /**
   * Builds and internalizes this database from a given
   * <code>String</code> containing an XML document.
   * <p>
   * This method can be called repeatedly on the same instance, but the
   * database will be built from scratch each time.
   * <p>
   * The registered database listeners will <b>not</b> be notified
   * during this method.
   * @param input the input string containing XML code
   * document
   * @param treatWarningsAsError if true, warnings during parsing will
   * trigger a <code>ParseException</code>. If false, no exception will
   * be thrown if the parser reports a warning. In the latter case, the
   * warnings can be obtained using the <code>getParseWarnings()</code>
   * method.
   * @throws IOException if any IO errors occur
   * @throws ParseException if any parse errors occur
   */
  public void build(String input, boolean treatWarningsAsError) throws IOException, ParseException {
    build(new InputSource(new StringReader(input)), treatWarningsAsError);
  }

  private void build(InputSource inputSource, boolean treatWarningsAsError)
  throws IOException, ParseException {
    this.treatWarningsAsErrors = treatWarningsAsError;
    this.xmlInternalized = false;
    Document document;
    try {
      document = getSAXBuilder().build(inputSource);
    } catch (JDOMParseException e) {
      throw new ParseException(e.toString(), e.getLineNumber());
    } catch (JDOMException e) {
      throw new ParseException(e.toString());
    }
    internalizeDocument(document);
    this.xmlInternalized = true;
  }

  /**
   * Creates a <code>SAXBuilder</code> to build a JDOM tree using SAX.
   * @return a <code>SAXBuilder</code> to build a JDOM tree using SAX.
   */
  private SAXBuilder getSAXBuilder() {
    final LineNumberSAXBuilder builder = new LineNumberSAXBuilder();

    // this parser configuration will use an xsd schema for validation
    // if one is specified in the xml itself and if that xsd can be found
    builder.setFeature("http://apache.org/xml/features/validation/schema", true);
    builder.setFeature("http://apache.org/xml/features/validation/dynamic", true);
    builder.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

    builder.setErrorHandler(new ErrorHandler() {

      /** {@inheritDoc} */
      @Override
      public void error(SAXParseException exception) throws SAXParseException {
        throw exception;
      }

      /** {@inheritDoc} */
      @Override
      public void fatalError(SAXParseException exception) throws SAXParseException {
        throw exception;
      }

      /** {@inheritDoc} */
      @Override
      public void warning(SAXParseException exception) throws SAXParseException {
        if (treatWarningsAsErrors) {
          throw exception;
        } else {
          parseWarnings.add(new ParseWarning(exception.getMessage(), exception.getLineNumber()));
        }
      }

    });
    return builder;
  }

  /** Reinitializes the instance variables for a new build. */
  private void init() {
    masterLanguage = null;
    additionalRootAttrs.clear();
    // the collection is not modifiable and therefore cannot be cleared.
    additionalNamespaces = new ArrayList<Namespace>();
    textNodeList.clear();
    parseWarnings.clear();
  }

  /**
   * Internalizes a <code>Document</code>. The registered database
   * listeners will <b>not</b> be notified during this method.
   * @param document the document to internalize
   */
  private void internalizeDocument(Document document) throws ParseException {
    init();

    // temporarily remove the database listeners
    List<IDatabaseListener> tmpListeners = new ArrayList<IDatabaseListener>(listeners);
    listeners.clear();

    Hashtable<String, String> keyMap = new Hashtable<String, String>();

    try {
      Element rootElement = document.getRootElement();
      List<Attribute> rootAttrs = rootElement.getAttributes();

      // extract the master language and put all other root element
      // attributes aside
      for (Attribute attribute : rootAttrs) {
        if (MASTER_LANGUAGE_ATTRIBUTE_NAME.equals(attribute.getName())) {
          masterLanguage = attribute.getValue();
        } else {
          additionalRootAttrs.add(attribute);
        }
      }

      // store additional namespaces. the xsi namespace is used for the attribute xsi:noSchemaNamespaceLocation
      for (Object namespace : rootElement.getAdditionalNamespaces()) {
        // The list is not type save, therefore a cast is done
        if (namespace instanceof Namespace) {
          additionalNamespaces.add((Namespace) namespace);
        } else {
          throw new RuntimeException("Namespace List contains Objects that are not of type Namespace");
        }
      }

      if (masterLanguage == null) {
        // since we cannot determine the line numbers in this methods
        // we pass 0 to occuring ParseExceptions
        throw new ParseException("Master language missing.", 0);
      }

      List<Element> textList = rootElement.getChildren(TEXT_ELEMENT_NAME);

      // the validation in here is kind of unnecessary when the xsd is used for validation.
      // however the xsd validation was added later and it cant hurt to doublecheck some things, there might be cases
      // where the xsd cannot be used
      for (Element textElement : textList) {
        String key = textElement.getAttributeValue(KEY_ATTRIBUTE_NAME);
        if (key == null) {
          throw new ParseException("No key found for text.",
              ((LineNumberElement) textElement).getStartLine());
        }

        if (keyMap.containsKey(key)) {
          parseWarnings.add(new ParseWarning("Duplicate key: " + key,
              ((LineNumberElement) textElement).getStartLine()));
        } else {
          keyMap.put(key, "");
        }

        String context = textElement.getChildText(CONTEXT_ELEMENT_NAME);
        if (context == null) {
          throw new ParseException("No context found for key \"" + key + "\".",
              ((LineNumberElement) textElement).getStartLine());
        }

        ITextNode textNode = new XMLTextNode(key, context);
        addTextNode(textNode);

        List<Element> valueList = textElement.getChildren(VALUE_ELEMENT_NAME);
        for (Element valueElement : valueList) {
          String language = valueElement.getAttributeValue(LANGUAGE_ATTRIBUTE_NAME);
          if (language == null) {
            throw new ParseException("No language found for value of key \"" + key + "\".",
                ((LineNumberElement) valueElement).getStartLine());
          }
          String statusName = valueElement.getAttributeValue(STATUS_ATTRIBUTE_NAME);
          Status status = Status.valueOf(statusName);
          if (status == null) {
            throw new ParseException("Invalid status for key \"" + key + "\": " + statusName,
                ((LineNumberElement) valueElement).getStartLine());
          }
          String value = valueElement.getText();
          textNode.addValueNode(new XMLValueNode(language, status, value));
        }
      }
    } finally {
      listeners.addAll(tmpListeners);
    }
  }

  /**
   * Serializes the database to the output stream.
   *
   * @param outputStream the output
   * @param encoding the encoding to use
   * @param indent the indent
   * @param lineSeparator the lineSeparator
   * @throws IOException in case the xml could not be written
   */
  public void writeXML(OutputStream outputStream, String encoding, String indent, String lineSeparator)
  throws IOException {
    XMLOutputter outputter = new XMLOutputter();
    Format format = Format.getPrettyFormat();
    format.setEncoding(encoding);
    format.setIndent(indent);
    format.setLineSeparator(lineSeparator);
    outputter.setFormat(format);
    outputter.output(getDocument(), outputStream);
  }

  /**
   * Writes XML code to a given <code>StringWriter</code>.
   * @param stringWriter the string writer to write to
   * @param encoding the encoding to be used
   * @param indent the indent string to be used
   * @param lineSeparator the line separator to be used
   * @throws IOException if any IO errors occur
   */
  public void writeXML(StringWriter stringWriter, String encoding, String indent, String lineSeparator)
  throws IOException {
    XMLOutputter outputter = new XMLOutputter();
    Format format = Format.getPrettyFormat();
    format.setEncoding(encoding);
    format.setIndent(indent);
    format.setLineSeparator(lineSeparator);
    outputter.setFormat(format);
    outputter.output(getDocument(), stringWriter);
  }

  /**
   * Creates a <code>Document</code> object for the current state of
   * this xml database.
   * @return a <code>Document</code> object.
   */
  private Document getDocument() {
    Document document = new Document();
    Comment comment = new Comment(" generated on " + new Date() + " ");
    document.addContent(comment);

    Element rootElement = new Element(ROOT_ELEMENT_NAME);
    rootElement.setAttribute(MASTER_LANGUAGE_ATTRIBUTE_NAME, getMasterLanguage());
    for (Attribute attribute : additionalRootAttrs) {
      // need to set the attribute like a new attribute,
      // as the original one stored the parent element and cannot be set to a new parent
      rootElement.setAttribute(attribute.getName(), attribute.getValue(), attribute.getNamespace());
    }
    // set the namespace, this makes sure that it is written into the trema node
    for (Namespace namespace : additionalNamespaces) {
      rootElement.addNamespaceDeclaration(namespace);
    }

    Iterator<ITextNode> textIterator = textNodeList.iterator();
    while (textIterator.hasNext()) {
      ITextNode textNode = textIterator.next();
      String key = textNode.getKey();
      Element textElement = new Element(TEXT_ELEMENT_NAME);
      textElement.setAttribute(KEY_ATTRIBUTE_NAME, key);

      String context = textNode.getContext();
      Element contextElement = new Element(CONTEXT_ELEMENT_NAME);
      contextElement.setText(context);
      textElement.addContent(contextElement);

      IValueNode[] valueNodes = textNode.getValueNodes();
      for (IValueNode valueNode : valueNodes) {
        Element valueElement = new Element(VALUE_ELEMENT_NAME);
        valueElement.setAttribute(LANGUAGE_ATTRIBUTE_NAME, valueNode.getLanguage());
        valueElement.setAttribute(STATUS_ATTRIBUTE_NAME, valueNode.getStatus().getName());
        valueElement.setText(valueNode.getValue());
        textElement.addContent(valueElement);
      }
      rootElement.addContent(textElement);
    }
    document.setRootElement(rootElement);
    return document;
  }

  /**
   * Returns a flag indicating warnings during parse.
   *
   * @return true if there are parse warnings.
   */
  public boolean hasParseWarnings() {
    return parseWarnings.size() > 0;
  }

  /**
   * Gets the parse warnings.
   * @return the parse warnings or an empty array if there are none.
   */
  public ParseWarning[] getParseWarnings() {
    return parseWarnings.toArray(new ParseWarning[parseWarnings.size()]);
  }

  /**
   * Gets the text node for a given key.
   * @param key the key of the text node to get
   * @return the text node for the given key or <code>null</code> if
   * no text node exists for the given key.
   */
  @Override
  public ITextNode getTextNode(String key) {
    Iterator<ITextNode> iterator = textNodeList.iterator();
    while (iterator.hasNext()) {
      ITextNode textNode = iterator.next();
      if (key.equals(textNode.getKey())) {
        return textNode;
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public ITextNode getTextNode(int index) {
    if (index < 0 || index >= textNodeList.size()) {
      return null;
    }
    return textNodeList.get(index);
  }
  /** {@inheritDoc} */
  @Override
  public String getMasterLanguage() {
    return masterLanguage;
  }

  /** {@inheritDoc} */
  @Override
  public int getSize() {
    return textNodeList.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setMasterLanguage(String masterLanguage) {
    this.masterLanguage = masterLanguage;
    fireMasterLanguageChanged();
  }

  /** {@inheritDoc} */
  @Override
  public void addTextNode(ITextNode textNode) {
    textNodeList.add(textNode);
    textNode.setParent(this);
    fireTextNodeAdded(textNode);
  }

  /** {@inheritDoc} */
  @Override
  public void addTextNode(int position, ITextNode textNode) {
    textNodeList.add(position, textNode);
    textNode.setParent(this);
    fireTextNodeAdded(textNode);
  }

  /** {@inheritDoc} */
  @Override
  public void removeTextNodes(ITextNode[] textNodes) {
    if (textNodes.length > 0) {
      int index = indexOf(textNodes[0]);
      for (ITextNode textNode : textNodes) {
        textNodeList.remove(textNode);
      }
      fireTextNodesRemoved(textNodes, index);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void removeTextNode(String key) {
    ITextNode textNode = getTextNode(key);
    if (textNode != null) {
      removeTextNodes(new ITextNode[] {textNode});
    }
  }

  /** {@inheritDoc} */
  @Override
  public void moveTextNode(ITextNode textNode, int targetPosition) {
    removeTextNodes(new ITextNode[] {textNode});
    addTextNode(targetPosition, textNode);
  }

  /** {@inheritDoc} */
  @Override
  public void moveUpTextNodes(ITextNode[] textNodes) {
    for (ITextNode textNode : textNodes) {
      int index = textNodeList.indexOf(textNode);

      if (index > 0) {
        ITextNode previousTextNode = textNodeList.get(index - 1);
        textNodeList.set(index, previousTextNode);
        textNodeList.set(index - 1, textNode);
      }
    }
    fireTextNodesMoved(textNodes);
  }

  /** {@inheritDoc} */
  @Override
  public void moveDownTextNodes(ITextNode[] textNodes) {
    for (int i = textNodes.length - 1; i >= 0; i--) {
      int index = textNodeList.indexOf(textNodes[i]);

      if (index < textNodeList.size() - 1) {
        ITextNode nextTextNode = textNodeList.get(index + 1);
        textNodeList.set(index, nextTextNode);
        textNodeList.set(index + 1, textNodes[i]);
      }
    }
    fireTextNodesMoved(textNodes);
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(ITextNode textNode) {
    return textNodeList.indexOf(textNode);
  }

  /** {@inheritDoc} */
  @Override
  public ITextNode[] getTextNodes() {
    return textNodeList.toArray(new ITextNode[textNodeList.size()]);
  }

  /** {@inheritDoc} */
  @Override
  public boolean existsTextNode(String key) {
    return getTextNode(key) != null;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<String> getKeysAsCollection() {
    List<String> keyList = new ArrayList<String>();
    Iterator<ITextNode> i = textNodeList.iterator();
    while (i.hasNext()) {
      keyList.add(((XMLTextNode) i.next()).getKey());
    }
    return keyList;
  }

  /** {@inheritDoc} */
  @Override
  public void addListener(IDatabaseListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void removeListener(IDatabaseListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notifies the registered listeners of a master language change.
   */
  protected void fireMasterLanguageChanged() {
    for (IDatabaseListener iDatabaseListener : listeners) {
      iDatabaseListener.masterLanguageChanged(this);
    }
  }

  /**
   * Notifies the registered listeners of a change in a text node (key
   * or context).
   * @param textNode the TextNode that has changed
   */
  protected void fireTextNodeChanged(ITextNode textNode) {
    for (IDatabaseListener iDatabaseListener : listeners) {
      iDatabaseListener.textNodeChanged(this, textNode);
    }
  }

  /**
   * Notifies the registered listeners of an addidion of a text node.
   * @param textNode the TextNode that has been added
   */
  protected void fireTextNodeAdded(ITextNode textNode) {
    for (IDatabaseListener iDatabaseListener : listeners) {
      iDatabaseListener.textNodeAdded(this, textNode);
    }
  }

  /**
   * Notifies the registered listeners that some text nodes have been
   * removed.
   * @param textNodes the text nodes that have been removed
   * @param index the position of the topmost text node that has been
   * removed
   */
  protected void fireTextNodesRemoved(ITextNode[] textNodes, int index) {
    for (IDatabaseListener iDatabaseListener : listeners) {
      iDatabaseListener.textNodesRemoved(this, textNodes, index);
    }
  }

  /**
   * Notifies the registered listeners that some text nodes have been
   * moved.
   * @param textNodes the list of text nodes that have been moved. The
   * list shall only contains <code>ITextNode</code> members.
   */
  protected void fireTextNodesMoved(ITextNode[] textNodes) {
    for (IDatabaseListener iDatabaseListener : listeners) {
      iDatabaseListener.textNodesMoved(this, textNodes);
    }
  }

  /**
   * Notifies the registered listeners of a change in value node (value or status).
   * @param valueNode the value node that has changed
   */
  @Override
  public void fireValueNodeChanged(IValueNode valueNode) {
    for (IDatabaseListener iDatabaseListener : listeners) {
      iDatabaseListener.valueNodeChanged(valueNode);
    }
  }

  /**
   * Notifies the registered listeners of an addition of a value node.
   * to
   * @param valueNode the value node that has been added
   */
  @Override
  public void fireValueNodeAdded(IValueNode valueNode) {
    for (IDatabaseListener iDatabaseListener : listeners) {
      iDatabaseListener.valueNodeAdded(valueNode);
    }
  }

  /**
   * Notifies the registered listeners of a removal of a value node.
   * removed from
   * @param valueNode the value node that has been removed
   */
  @Override
  public void fireValueNodeRemoved(IValueNode valueNode) {
    for (IDatabaseListener iDatabaseListener : listeners) {
      iDatabaseListener.valueNodeRemoved(valueNode);
    }
  }

  /**
   * Returns the state of the DB, true if the DB was built without errors / exceptions, false if there was a problem.
   *
   * @return the state of the db
   */
  public boolean isXmlInternalized() {
    return xmlInternalized;
  }

}



