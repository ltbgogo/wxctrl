package com.abc.wxctrl.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import lombok.Cleanup;
import lombok.SneakyThrows;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * The XML utility.
 */
public class XmlUtil {
	
	//Single instance
	public static final XmlUtil XU = new XmlUtil(false);
	public static final XmlUtil NXU = new XmlUtil(true);

    //This final variables are used to specify what kind of data you want.
    public final QName T_N = XPathConstants.NODE;
    public final QName T_NS = XPathConstants.NODESET;
    public final QName T_D = XPathConstants.NUMBER;
    public final QName T_S = XPathConstants.STRING;
    public final QName T_B = XPathConstants.BOOLEAN;
    //It specifies whether the current XmlUtil object uses name space to query using XPath.
    public final boolean NAMESPACE_AWARE;

    /**
     * When constructing a XmlUtil object, to initialize the global final
     * variable NAMESPACE_AWARE.
     *
     * @param namespaceAware It is assigned to the global final variable NAMESPACE_AWARE.
     */
    private XmlUtil(boolean namespaceAware) {
        this.NAMESPACE_AWARE = namespaceAware;
    }

    /**
     * It creates a org.w3c.dom.Document object using a XML stream.
     *
     * @param stream The XML stream used to create a org.w3c.dom.Documentobject.
     * @return The org.w3c.dom.Document object.
     * @If the argument stream is not from a standard XML, then a exception will be thrown.
     */
    @SneakyThrows
    public Document getDocument(InputStream in) {
    	try {
	        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	        docFactory.setNamespaceAware(this.NAMESPACE_AWARE);
	        DocumentBuilder db = docFactory.newDocumentBuilder();
	        return db.parse(in);
    	} finally {
	        IOUtils.closeQuietly(in);
    	}
    }
    
    @SneakyThrows
    public Document getDocument(String xml) {
    	return this.getDocument(new ByteArrayInputStream(xml.getBytes("UTF-8")));
    }

    /**
     * It creates a blank a org.w3c.dom.Document object.
     *
     * @return A blank org.w3c.dom.Document object.
     * @I have no idea to what situation where a exception will be throwed.
     */
    @SneakyThrows
    public Document createDocument() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(this.NAMESPACE_AWARE);
        DocumentBuilder db = docFactory.newDocumentBuilder();
        return db.newDocument();
    }

    /**
     * It parse a XPath using a context.
     *
     * @param expr The XPath.
     * @param context You can start your querying from this context.
     * @param returnType It specifies what kind of data you want.
     * @return It returns a result data which type is specified by the argument returnType.
     * @If the name space can not be found, a exception will be thrown.
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
	public <T> T parse(String expr, Node context, QName returnType) {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        if (this.NAMESPACE_AWARE) {
            Node temp = context;
            do {
                if (temp.getNodeType() == Node.DOCUMENT_NODE) {
                    xpath.setNamespaceContext(this.getNamespaceContext((Document) temp));
                    break;
                }
                temp = temp.getParentNode();
            } while (true);
        }
        XPathExpression xe = xpath.compile(expr);
        return (T) xe.evaluate(context, returnType);
    }
    
    /**
     * Fetch the text value in the context, and return it as required type.
     */
    public Node getNode(String expr, Node context) {return (Node) this.parse(expr, context, this.T_N); }
    public Iterable<Node> getNodeList(String expr, Node context) { return this.iterate((NodeList) this.parse(expr, context, this.T_NS)); }
    public Double getDouble(String expr, Node context) {return (Double) this.parse(expr, context, this.T_D); }
    public Boolean getBoolean(String expr, Node context) {return (Boolean) this.parse(expr, context, this.T_B); }
    public String getString(String expr, Node context) {return (String) this.parse(expr, context, this.T_S); }
    
    /**
     * Iterate node list, fetch every text, and process them with String.replaceAll().
     */
    public List<String> getStrings(String expr, Node context, String regex, String replacement) { 
    	List<String> strings = new ArrayList<>(); 
    	for (Node node : getNodeList(expr, context)) { 
    		if (regex != null && replacement != null) {
    			strings.add(getString(".", node).replaceAll(regex, replacement));
    		} else {
        		strings.add(getString(".", node));
    		}
    	} 
    	return strings; 
    }
    
    /**
     * It returns a XML string from a org.w3c.dom.Document object.
     *
     * @param node The resulting data is from it.
     * @param properties It specifies the resulting xml's properties.
     * @return The resulting XML string.
     * @If the passed properties are not suitable, a exception will be thrown.
     */
    @SneakyThrows
    public String getXml(Node node, Map<String, String> outputProperties) {
    	Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        for (Entry<String, String> outputProperty : ObjectUtils.defaultIfNull(outputProperties, new HashMap<String, String>()).entrySet()) {
        	transformer.setOutputProperty(outputProperty.getKey(), outputProperty.getValue());
        }
        StringWriter writer = null;
        try {
        	writer = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(writer));
        } finally {
        	 IOUtils.closeQuietly(writer);
        }
        return writer.toString();
    }
    public String getXml(Node node) {
    	return this.getXml(node, null);
    }

    /**
     * It searches all the text nodes among the argument and its descendants,
     * and returns their values.
     *
     * @param node The searching starts with this node.
     * @return The searched text nodes' values.
     * @I have no idea to what situation where a exception will be thrown.
     */
    public String getText(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            return node.getNodeValue();
        } else {
            StringBuilder text = new StringBuilder();
            NodeList textNodeList = (NodeList) this.parse("descendant::text()", node, this.T_NS);
            for (int i = 0; i < textNodeList.getLength(); i++) {
                text.append(textNodeList.item(i).getNodeValue());
            }
            return text.toString();
        }
    }
    
    /**
     * Encapsulate NodeList as Iterable for supporting the Java feature 'foreach'
     * @param nodeList Encapsulated object
     * @return Iterable object on behalf of 'nodeList'
     */
	public Iterable<Node> iterate(final NodeList nodeList) {
		return new Iterable<Node>() {
			public Iterator<org.w3c.dom.Node> iterator() {
				return new Iterator<Node>() {
        			private int index = 0;
        			public boolean hasNext() { return nodeList.getLength() > this.index; }
        			public Node next() { return nodeList.item(this.index++); }
        			public void remove() { throw new UnsupportedOperationException(); }
            	};
			}
        };
	}

    /**
     * It returns a javax.xml.namespace.NamespaceContext object. 
     * 
     * @param document The resulting object serves this one.
     * @return It returns serves XPath querying by name space.
     */
    private NamespaceContext getNamespaceContext(final Document document) {
        return new NamespaceContext() {
            public String getNamespaceURI(String prefix) { return XmlUtil.this.getNamespaceForPrefix(prefix, document); }
            public String getPrefix(String namespaceURI) { return null; }
            public Iterator<?> getPrefixes(String namespaceURI) { return null; }
        };
    }

    /**
     * It searches a correction name space by a prefix in a org.w3c.dom.Document.
     * 
     * @param prefix It is corresponding with a name space in a org.w3c.dom.Document.
     * @param namespaceContext The searching is under this org.w3c.dom.Document.
     * @return It return a name space.
     */
    private String getNamespaceForPrefix(String prefix, Document namespaceContext) {
        Node parent = namespaceContext.getDocumentElement();
        String namespace = null;
        if (prefix.equals("xml")) {
            namespace = "\"http://www.w3.org/XML/1998/namespace\"";
        } else {
            int type;
            while ((null != parent) && (null == namespace) && (((type = parent.getNodeType()) == Node.ELEMENT_NODE) || (type == Node.ENTITY_REFERENCE_NODE))) {
                if (type == Node.ELEMENT_NODE) {
                    if (parent.getNodeName().indexOf(prefix + ":") == 0) {
                        return parent.getNamespaceURI();
                    }
                    NamedNodeMap nnm = parent.getAttributes();
                    for (int i = 0; i < nnm.getLength(); i++) {
                        Node attr = nnm.item(i);
                        String aname = attr.getNodeName();
                        boolean isPrefix = aname.startsWith("xmlns:");
                        if (isPrefix || aname.equals("xmlns")) {
                            int index = aname.indexOf(':');
                            String p = isPrefix ? aname.substring(index + 1) : "";
                            if (p.equals(prefix)) {
                                namespace = attr.getNodeValue();
                                break;
                            }
                        }
                    }
                }
                parent = parent.getParentNode();
            }
        }
        return namespace;
    }
    
    /**
     * This is just a example. Since situations are many more. You can take this method as a reference to add new node.
     * Note: If the two nodes inverse in insertBefore(), the newNode must be add by node.getParentNode().appendChild() in advance.
     * 
     * @param before Insert the new node before this one.
     * @param newNodeName The new node's name.
     * @param attributes Put this attributes into the new node.
     * @return The new node.
     */
    public Node addNodeBefore(Node node, String newNodeName) {
    	Document document = node.getOwnerDocument();
		Node newNode = document.createElement(newNodeName);
		node.getParentNode().insertBefore(newNode, node);
		return newNode;
    }
    
    /**
     * Set attributes for a node.
     * 
     * @param node Set attributes for this one.
     * @param attributes Set these attributes.
     * @return The one past in.
     */
    public Node setAttribute(Node node, String attributeName, String attributeValue) {
		Node attr = node.getAttributes().getNamedItem(attributeName);
		if (attr == null) {
			attr = node.getOwnerDocument().createAttribute(attributeName);
			node.getAttributes().setNamedItem(attr);
		}
		attr.setNodeValue(attributeValue);
    	return node;
    }
    
    /**
     * Get a specific attribute's value by the attribute's name from a node.
     * 
     * @param node Searching the attribute from this node.
     * @param name Searched attribute's name.
     * @return The attribute's value. Return null if searching fail.
     */
    public String getAttribute(Node node, String name) {
    	Node attr = node.getAttributes().getNamedItem(name);
    	return attr == null ? null : attr.getNodeValue();
    }
    
    /**
     * Qualified Name
     */
    private static final boolean IS_STANDARD_QNAME = isStandardQualifiedName();
    private static boolean isStandardQualifiedName() {
    	try {
    		XmlUtil util = new XmlUtil(true);
    		Document doc = util.getDocument("<root xmlns:w=\"www\"><w:a>XXX</w:a></root>");
    		Map<String, String> xmlProperties = new HashMap<String, String>();
    		xmlProperties.put(OutputKeys.CDATA_SECTION_ELEMENTS, "{www}a"); 
    		return util.getXml(doc, xmlProperties).toLowerCase().contains("cdata");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
    public String getQName(String namespace, String localname) {
    	return " " + (StringUtils.isBlank(namespace) ? localname : IS_STANDARD_QNAME ?  ("{" + namespace + "}" + localname) : (namespace + ":" + localname)) + " ";
    }
    
    @SneakyThrows
    public static String xslt(InputStream xslStream, InputStream xmlStream) {         
        // 构建输入输出流  
        @Cleanup ByteArrayOutputStream out = new ByteArrayOutputStream();  
        Source from = new StreamSource(xmlStream);  
        Result to = new StreamResult(out);  
        // 构建转换器并执行转换  
        SAXSource xsl = new SAXSource(new InputSource(xslStream));
        Transformer transformer = TransformerFactory.newInstance().newTransformer(xsl);
        transformer.transform(from, to);  
        // 输出转换后结果  
        return out.toString();  
    }
    
//    public static void main(String[] args) throws IOException {
//		for (URL resource : CollUtil.toIterable(XmlUtil.class.getClassLoader().getResources("sql.xml"))) {
//			final Document doc = XmlUtil.XU.getDocument(resource.openStream());
//			String driver = XmlUtil.XU.getString("/sqls/ds/driver", doc);
//			String url = XmlUtil.XU.getString("/sqls/ds/url", doc);
//			String user = XmlUtil.XU.getString("/sqls/ds/user", doc);
//			String password = XmlUtil.XU.getString("/sqls/ds/password", doc);
//			final JdbcTemplate jt = new JdbcTemplate(DSUtil.get(driver, url, user, password));
//			DBUtil.execute(jt.getDataSource(), new TransactionCallback<Void>() {
//				public Void doInTransaction(TransactionStatus arg0) {
//					for (Node sqlNode : XmlUtil.XU.getNodeList("/sqls/sql", doc)) {
//						String sql = XmlUtil.XU.getString("text", sqlNode).trim();
//						List<String> params = XmlUtil.XU.getStrings("param", sqlNode, null, null);
//						jt.update(sql, params.toArray());
//					}
//					return null;
//				}
//			});
//		}
//	}
}



