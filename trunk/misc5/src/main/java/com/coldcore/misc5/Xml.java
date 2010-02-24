/*
 * Date: 25.06.1985
 * Time: 5:51:17
 * Operations on XML format.
 */
package com.coldcore.misc5;

import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.serialize.SerializerFactory;
import org.apache.xalan.templates.OutputProperties;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

public class Xml {

  private Xml() {}


  /** Load XML
   * @param in Input
   * @return XML
   */
  public static Document loadXml(InputStream in) throws ParserConfigurationException, IOException, SAXException {
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
    normalize(doc.getFirstChild());
    return doc;
  }


  /** Load XML
   * @param file Input
   * @return XML
   */
  public static Document loadXml(File file) throws ParserConfigurationException, IOException, SAXException {
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
    normalize(doc.getFirstChild());
    return doc;
  }


  /** Load XML
   * @param is Input
   * @return XML
   */
  public static Document loadXml(InputSource is) throws ParserConfigurationException, IOException, SAXException {
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
    normalize(doc.getFirstChild());
    return doc;
  }


  /** Save XML
   * @param file Target file
   * @param node Node to save
   */
  public static void saveXml(File file, Node node) throws Exception {
    String text = nodeToString(node, 2);
    new CFile(file).rewriteFile(text.getBytes("UTF-8"));
  }


  /** Removes #text nodes from elements that contain other elements (recursive)
   * @param node Node to process
   */
  private static void normalize(Node node) {
    if (containsRegularNodes(node)) removeTextNodes(node);
    //Also normalize evely child
    NodeList list = node.getChildNodes();
    int size = list.getLength();
    for (int z = 0; z < size; z++) {
      Node n = list.item(z);
      normalize(n);
    }
  }


  /** Returns true if current node contains regular nodes (not #text type)
   * @param node Node to process
   * @return True if there are only regular nodes inside, false otherwise
   */
  private static boolean containsRegularNodes(Node node) {
    NodeList list = node.getChildNodes();
    int size = list.getLength();
    for (int z = 0; z < size; z++) {
      Node n = list.item(z);
      if (n.getNodeType() != Document.TEXT_NODE) return true; //If it's not a text then it's a regular node
    }
    return false;
  }


  /** Removes all text nodes
   * @param node Node to process
   */
  private static void removeTextNodes(Node node) {
    NodeList list = node.getChildNodes();
    for (int loop = 0; loop < list.getLength(); loop++) {
      Node n = list.item(loop);
      if (n.getNodeType() == Document.TEXT_NODE) node.removeChild(n);
    }
  }


  /** Search for elements starting from a specified node
   * @param parent Node to search in
   * @param elem Full element path relative to its parent node (my/tag/name), if empty then all elements in the parent will be returned
   * @return Found elements, never null
   */
  public static Element[] findElements(String elem, Node parent) {
    //Get tags names in element path
    StringReaper reaper = new StringReaper(elem == null ? "" : elem);
    String[] values = reaper.getValues(null, "/");
    if (values.length == 0) values = new String[]{""}; //All children will be returned

    //Search
    List<Node> l = new ArrayList<Node>();
    l.add(parent);
    for (String name : values)
      l = shallowFindElements(name, l); //List gets shorter if tags do not contain requested node name

    return l.toArray(new Element[l.size()]);
  }


  /** Search for elements in specified nodes without going deeper than one level
   * @param elemName Element name that must be accepted by the search, if empty then all elements in the parent will be returned
   * @param nodes Nodes to search in
   * @return Children elements, never null
   */
  private static List<Node> shallowFindElements(String elemName, List<Node> nodes) {
    ArrayList<Node> elems = new ArrayList<Node>();
    for (Node node : nodes) {
      NodeList list = node.getChildNodes();
      int size = list.getLength();
      for (int loop = 0; loop < size; loop++) {
        Node n = list.item(loop);
        if (n.getNodeType() != Document.ELEMENT_NODE) continue;
        if (n.getNodeName().equals(elemName) || elemName.equals("")) elems.add((Element) n);
      }
    }
    return elems;
  }


  /** Create element if it does not already exist, otherwise the existing element will be added with missing nodes
   * @param elem Full element path relative to its parent node (my/tag/name)
   * @param parent Node to add a new element to
   * @param document Document
   * @return Created or existing element
   */
  public static Element createSingleElement(String elem, Element parent, Document document) {
    //Get tags names
    StringReaper reaper = new StringReaper(elem);
    String[] values = reaper.getValues(null, "/");

    Element element = parent;
    for (String name : values) {
      //Get specified children of current element
      Element[] elems = findElements(name, element);
      if (elems.length == 0) element = createElement(name, element, document); //Child does not exist - create new
      else element = elems[0]; //Already exists
    }
    return element;
  }


  /** Create a new Document. */
  public static Document createDocument() throws ParserConfigurationException {
    return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
  }


  /** Creates a new element and appends it to another node. */
  public static Element createElement(String name, Node appendTo, Document document) {
    Element element = document.createElement(name);
    appendTo.appendChild(element);
    return element;
  }


  /** Creates a new node that contains a text node and appends it to another node */
  public static Element createTextElement(String name, String value, Node appendTo, Document document) {
    Element elem = createElement(name, appendTo, document);
    Text text = document.createTextNode(value);
    elem.appendChild(text);
    return elem;
  }


  /** Extract node text
   * @param node Node
   * @return Trimmed node's text or null if text cannot be extracted
   */
  public static String getNodeText(Node node) {
    //Check if node contains text in first child node
    if (!node.hasChildNodes()) return ""; //Empty tag
    Node n = node.getFirstChild();
    if (n.getNodeType() != Document.TEXT_NODE) return null;

    String value = n.getNodeValue();
    if (value != null) return value.trim();
    else return "";
  }


  /** Combine XML with XSL
   * @param node XML
   * @param transformer XSL
   * @param output Output
   */
  public static void combine(Node node, Transformer transformer, Result output) throws Exception {
    transformer.transform(new DOMSource(node), output);
  }


  /** Create XSL template
   * @param source Input
   * @return Template
   */
  public static Templates createTemplate(Source source) throws Exception {
    return TransformerFactory.newInstance().newTemplates(source);
  }


  /** Convenience method
   * @see com.coldcore.misc5.Xml#createTransformer(javax.xml.transform.Templates, java.util.Properties)
   */
  public static Transformer createTransformer(Templates templates) throws TransformerConfigurationException {
    return createTransformer(templates, null);
  }


  /** Create transformer (transformer is not thread safe)
   * @param templates XSL template
   * @param p Transformer output properties
   * @return Transformer
   */
  public static Transformer createTransformer(Templates templates, Properties p) throws TransformerConfigurationException {
    Transformer transformer = templates.newTransformer();
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

    if (p != null) {
      Enumeration en = p.propertyNames();
      while (en.hasMoreElements()) {
        String name  = (String) en.nextElement();
        String value = p.getProperty(name);
        transformer.setOutputProperty(name, value);
      }
    }

    return transformer;
  }


  /** Convert a node to a string with specified indent and without XML header.
   * @param node Node to convert
   * @param indent Ident (how many spaces to add)
   * @return String representation of XML
   */
  public static String nodeToString(Node node, int indent) throws IOException {
    Properties p = OutputProperties.getDefaultMethodProperties("xml");
    p.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    p.setProperty(OutputKeys.INDENT, "yes");
    p.setProperty(OutputKeys.ENCODING, "UTF-8");
    p.setProperty("{http://xml.apache.org/xalan}indent-amount", ""+indent);

    Serializer serializer = SerializerFactory.getSerializer(p);
    StringWriter sw = new StringWriter();
    serializer.setWriter(sw);
    serializer.asDOMSerializer().serialize(node);

    return sw.toString();
  }


  /** Import a node from another document.
   *
   * @param node Node to import
   * @param attachTo Node to attach to the imported node
   * @param doc Document to associate with the imported node
   * @return Imported node
   */
  public static Node importNode(Node node, Node attachTo, Document doc) {
    Node impNode = doc.importNode(node.cloneNode(true), true);
    attachTo.appendChild(impNode);
    return impNode;
  }


  /**
   * labels.xml reader
   */
  public static class LabelsXml {

    /** Convert labels.xml to map */
    public static Map<String,String> toMap(Node node) {
      if (node == null) return null;
      Map<String,String> map = new HashMap<String,String>();

      //Get all "label" tags
      Element[] labels = findElements("label", node);
      for (Element label : labels) {
        //Get name and value (name cannot be null, if missing it will be "")
        String name = label.getAttribute("name").trim();

        //Every tag inside "label" tag must be a language, we will get all languages.
        Element[] langs = findElements(null, label);
        for (Element lang : langs) {
          //Get language and it's tag value and store
          String langName = lang.getNodeName();
          String langValue = getNodeText(lang).trim();
          map.put(langName+"."+name, langValue);
        }
      }
      return map;
    }
  }
}