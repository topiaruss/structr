package org.structr.schema.importer;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.structr.common.error.FrameworkException;
import org.structr.core.app.App;
import org.structr.core.app.StructrApp;
import org.structr.core.entity.RDFNode;
import org.structr.core.entity.SchemaNode;
import org.structr.core.entity.relationship.SchemaRelationship;
import org.structr.core.graph.MaintenanceCommand;
import org.structr.core.graph.NodeAttribute;
import org.structr.core.graph.NodeServiceCommand;
import org.structr.core.graph.Tx;
import org.structr.core.property.PropertyMap;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian Morgner
 */
public class RDFImporter extends NodeServiceCommand implements MaintenanceCommand {

	private static final Logger logger = Logger.getLogger(RDFImporter.class.getName());
	
	public static void main(String[] args) {
		
		try {
			RDFImporter.importRDF(new FileInputStream("/home/chrisi/structr-ui/cidoc_crm_v5.1-draft-2013May.rdfs"));
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	@Override
	public void execute(final Map<String, Object> attributes) throws FrameworkException {
				
		final String fileName = (String)attributes.get("file");
		final String source   = (String)attributes.get("source");
		final String url      = (String)attributes.get("url");
		
		if (fileName == null && source == null && url == null) {
			throw new FrameworkException(422, "Please supply file, url or source parameter.");
		}
		
		if (fileName != null && source != null) {
			throw new FrameworkException(422, "Please supply only one of file, url or source.");
		}
		
		if (fileName != null && url != null) {
			throw new FrameworkException(422, "Please supply only one of file, url or source.");
		}
		
		if (url != null && source != null) {
			throw new FrameworkException(422, "Please supply only one of file, url or source.");
		}
		
		try {
			
			if (fileName != null) {
		
				importRDF(new FileInputStream(fileName));
				
			} else if (url != null) {

				importRDF(new URL(url).openStream());

			} else if (source != null) {

				importRDF(new ByteArrayInputStream(source.getBytes()));

			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public static void importRDF(final InputStream is) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {

		final Document doc                            = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		final Set<HasSubclassRelationship> subclasses = new LinkedHashSet<>();
		final Map<String, String> comments            = new LinkedHashMap<>();
		final Map<String, String> properties          = new LinkedHashMap<>();
		final Map<String, String> classes             = new LinkedHashMap<>();
		final Set<Triple> triples                     = new LinkedHashSet<>();
		
		for (Node node = doc.getElementsByTagName("rdfs:Class").item(0); node != null; node = node.getNextSibling()) {

			final String type = node.getNodeName();

			if ("rdfs:Class".equals(type)) {
				handleClass(classes, subclasses, comments, node);
			}

			if ("rdf:Property".equals(type)) {
				handleProperty(properties, triples, comments, node);
			}
		}
		
		System.out.println("################################################################# classes");
		System.out.println(classes);
		
		System.out.println("################################################################# subclasses");
		System.out.println(subclasses);

		System.out.println("################################################################# properties");
		System.out.println(properties);

		System.out.println("################################################################# relationships");
		System.out.println(triples);
		
		final Map<String, RDFNode> nodes = new LinkedHashMap<>();
		final App app                    = StructrApp.getInstance();
		
		try (final Tx tx = app.tx()) {
			
			for (final Entry<String, String> entry : classes.entrySet()) {
				
				final String id      = entry.getKey();
				final String name    = entry.getValue();
				final String comment = comments.get(id);
				
				final RDFNode node = app.create(RDFNode.class,
					new NodeAttribute<>(SchemaNode.name, name.replaceAll("[\\W]+", "")),
					new NodeAttribute<>(SchemaNode.rdfId, id),
					new NodeAttribute<>(RDFNode.comment, comment != null ? comment : null)
					
				);
				
				nodes.put(id, node);
			}

			for (final HasSubclassRelationship rel : subclasses) {
				
				final SchemaNode sourceNode   = nodes.get(rel.parent);
				final SchemaNode targetNode   = nodes.get(rel.child);
				final PropertyMap propertyMap = new PropertyMap();

				propertyMap.put(SchemaRelationship.relationshipType, "HAS_SUBTYPE");
				
				app.create(sourceNode, targetNode, SchemaRelationship.class, propertyMap);
			}
			
			for (final Triple triple : triples) {
				
				final SchemaNode sourceNode   = nodes.get(triple.source);
				final SchemaNode targetNode   = nodes.get(triple.target);
				final String relationshipType = properties.get(triple.relationship);
				
				if (sourceNode != null && targetNode != null && relationshipType != null) {
					
					final PropertyMap propertyMap = new PropertyMap();

					propertyMap.put(SchemaRelationship.relationshipType, relationshipType.replaceAll("[\\W]+", ""));

					app.create(sourceNode, targetNode, SchemaRelationship.class, propertyMap);
					
				} else {
					
					logger.log(Level.WARNING, "Unable to find source or target node for {0} / {1}", new Object[] { triple.source, triple.target });
				}
			}
			
		} catch (FrameworkException fex) {
			
			fex.printStackTrace();
		}
	}
	
	private static void handleClass(final Map<String, String> classes, final Set<HasSubclassRelationship> subclasses, final Map<String, String> comments, final Node classNode) {

		final NamedNodeMap attributes = classNode.getAttributes();
		final String comment          = getChildString(classNode, "rdfs:comment");
		final String rawName          = getString(attributes, "rdf:about");
		final int position            = rawName.indexOf("_");
		final String name             = rawName.substring(position+1);
		final String id               = rawName.substring(0, position);
		
		classes.put(id, name);
		
		if (comment != null) {
			comments.put(id, comment);
		}
		
		for (Node node = classNode.getChildNodes().item(0); node != null; node = node.getNextSibling()) {

			final String type = node.getNodeName();

			if ("rdfs:subClassOf".equals(type)) {
				
				final String parent = handleSubclass(classes, node);
				subclasses.add(new HasSubclassRelationship(parent, id));
			}
		}
	}
	
	private static String handleSubclass(final Map<String, String> classes, final Node classNode) {

		final NamedNodeMap attributes = classNode.getAttributes();
		final String rawName          = getString(attributes, "rdf:resource");
		final int position            = rawName.indexOf("_");
		final String name             = rawName.substring(position+1);
		final String id               = rawName.substring(0, position);
		
		classes.put(id, name);
		
		return id;
	}
	
	private static void handleProperty(final Map<String, String> properties, final Set<Triple> triples, final Map<String, String> comments, final Node propertyNode) {

		final NamedNodeMap attributes = propertyNode.getAttributes();
		final String comment          = getChildString(propertyNode, "rdfs:comment");
		final String rawName          = getString(attributes, "rdf:about");
		final int position            = rawName.indexOf("_");
		final String name             = rawName.substring(position+1);
		final String id               = rawName.substring(0, position);

		properties.put(id, name);
		
		if (comment != null) {
			comments.put(id, comment);
		}
		
		String domain = null;
		String range  = null;
		
		for (Node node = propertyNode.getChildNodes().item(0); node != null; node = node.getNextSibling()) {

			final String type = node.getNodeName();

			if ("rdfs:domain".equals(type)) {
				
				domain = handleDomain(node);
			}

			if ("rdfs:range".equals(type)) {
				
				range = handleRange(node);
			}
		}

		triples.add(new Triple(domain, id, range));
	}
	
	private static String handleDomain(final Node propertyNode) {

		final NamedNodeMap attributes = propertyNode.getAttributes();
		final String rawName          = getString(attributes, "rdf:resource");
		final int position            = rawName.indexOf("_");
		
		if (position >= 0) {
			
			final String id = rawName.substring(0, position);

			return id;
			
		} else {
			
			return rawName;
		}
	}
	
	private static String handleRange(final Node propertyNode) {

		final NamedNodeMap attributes = propertyNode.getAttributes();
		final String rawName          = getString(attributes, "rdf:resource");
		final int position            = rawName.indexOf("_");
		
		if (position >= 0) {

			final String id = rawName.substring(0, position);

			return id;
			
		} else {
			
			return rawName;
		}
	}
	
	private static String getString(final NamedNodeMap attributes, final String key) {
		return attributes.getNamedItem(key).getNodeValue();
	}
	
	private static String getChildString(final Node parent, final String key) {
		
		for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			
			if (key.equals(child.getNodeName())) {
				
				return child.getTextContent();
			}
		}
		
		return null;
	}
	
	private static class HasSubclassRelationship {
		
		public String parent = null;
		public String child  = null;
		
		public HasSubclassRelationship(final String parent, final String child) {
			this.parent = parent;
			this.child  = child;
		}
		
		@Override
		public String toString() {
			return parent + "->" + child;
		}
	}
	
	private static class Triple {
		
		public String relationship = null;
		public String source   = null;
		public String target   = null;
		
		public Triple(final String source, final String property, final String target) {
			
			this.relationship = property;
			this.source   = source;
			this.target   = target;
		}
		
		@Override
		public String toString() {
			return source + "-[" + relationship + "]->" + target;
		}
	}
}
