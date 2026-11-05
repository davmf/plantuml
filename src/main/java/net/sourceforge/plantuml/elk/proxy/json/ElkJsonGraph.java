/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2025, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 *
 * If you like this project or if you find it useful, you can support us at:
 *
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  David Fyfe
 *
 */
package net.sourceforge.plantuml.elk.proxy.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.json.Json;
import net.sourceforge.plantuml.json.JsonArray;
import net.sourceforge.plantuml.json.JsonObject;
import net.sourceforge.plantuml.json.JsonValue;

/**
 * A plain Java ELK graph, for the hosts where ELK itself cannot run (the
 * browser): the proxies of {@code net.sourceforge.plantuml.elk.proxy.graph}
 * wrap these objects instead of the real ELK ones. The graph is written in the
 * ELK JSON format, laid out by an {@link ElkJsonLayouter} (elkjs), and the
 * coordinates are read back into the same objects.
 * <p>
 * The coordinates follow the conventions of the ELK Java graph, which
 * {@code CucaDiagramFileMakerElk} relies on: a node is relative to its parent,
 * and an edge (sections and labels) to its containing node. Each edge is
 * written in the JSON node that ELK Java would choose as its container (see
 * {@link #findContainer(Node, Node)}), and with the default JSON coordinate
 * modes ELK writes the edge relative to that very node, so nothing has to be
 * converted.
 * <p>
 * Pure Java on purpose, with no TeaVM dependency, so that it can be tested on
 * the JVM.
 */
public final class ElkJsonGraph {

	private static ElkJsonLayouter layouter;

	private ElkJsonGraph() {
	}

	/**
	 * Registers the layouter. From then on, a new ELK graph is built from these
	 * objects rather than from the ELK Java classes; <code>null</code> goes back to
	 * ELK Java.
	 */
	public static void setLayouter(ElkJsonLayouter newLayouter) {
		layouter = newLayouter;
	}

	public static ElkJsonLayouter getLayouter() {
		return layouter;
	}

	public static Node createGraph() {
		return new Node(null, "root");
	}

	// =========================================================================
	// The graph
	// =========================================================================

	/** What nodes, ports, edges and labels have in common: an id, options and labels. */
	public static abstract class Element {

		private final String id;
		private final Map<String, Object> options = new LinkedHashMap<String, Object>();
		private final List<Label> labels = new ArrayList<Label>();

		private Element(String id) {
			this.id = id;
		}

		public final String getId() {
			return id;
		}

		/**
		 * @param key   the full id of the ELK option (<code>org.eclipse.elk...</code>)
		 * @param value a {@link String}, a {@link Boolean} or a {@link Number}
		 */
		public final void setOption(String key, Object value) {
			if (value instanceof String || value instanceof Boolean || value instanceof Number)
				options.put(key, value);
			else
				throw new IllegalArgumentException(key + "=" + value);
		}

		public final Map<String, Object> getOptions() {
			return Collections.unmodifiableMap(options);
		}

		public final List<Label> getLabels() {
			return Collections.unmodifiableList(labels);
		}

		public final Label createLabel() {
			final Label label = new Label(getRoot().nextId("l"), this);
			labels.add(label);
			return label;
		}

		abstract Node getRoot();

	}

	/**
	 * What an edge can be connected to: a node or a port
	 * (<code>ElkConnectableShape</code>).
	 */
	public static abstract class Shape extends Element {

		private Shape(String id) {
			super(id);
		}

		/**
		 * The node this shape stands for when an edge is put in its container: the
		 * node itself, or the node of a port (<code>connectableShapeToNode</code>).
		 */
		abstract Node getNode();

	}

	public static final class Node extends Shape {

		private final Node parent;
		private final List<Node> children = new ArrayList<Node>();
		private final List<Port> ports = new ArrayList<Port>();
		private final List<Edge> edges = new ArrayList<Edge>();
		private int counter;
		private double x;
		private double y;
		private double width;
		private double height;

		private Node(Node parent, String id) {
			super(id);
			this.parent = parent;
		}

		private String nextId(String prefix) {
			return prefix + (counter++);
		}

		@Override
		Node getNode() {
			return this;
		}

		@Override
		Node getRoot() {
			Node result = this;
			while (result.parent != null)
				result = result.parent;
			return result;
		}

		public Node createNode() {
			final Node node = new Node(this, getRoot().nextId("n"));
			children.add(node);
			return node;
		}

		public Port createPort() {
			final Port port = new Port(getRoot().nextId("p"), this);
			ports.add(port);
			return port;
		}

		public Node getParent() {
			return parent;
		}

		public List<Port> getPorts() {
			return Collections.unmodifiableList(ports);
		}

		public List<Node> getChildren() {
			return Collections.unmodifiableList(children);
		}

		/** The edges this node contains (not the ones it is connected to). */
		public List<Edge> getContainedEdges() {
			return Collections.unmodifiableList(edges);
		}

		public void setDimensions(double newWidth, double newHeight) {
			this.width = newWidth;
			this.height = newHeight;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}

		private boolean isAncestorOrSelfOf(Node node) {
			for (Node current = node; current != null; current = current.parent)
				if (current == this)
					return true;
			return false;
		}

	}

	/** A port: its coordinates are relative to its node. */
	public static final class Port extends Shape {

		private final Node parent;
		private boolean located;
		private double x;
		private double y;
		private double width;
		private double height;

		private Port(String id, Node parent) {
			super(id);
			this.parent = parent;
		}

		@Override
		Node getNode() {
			return parent;
		}

		@Override
		Node getRoot() {
			return parent.getRoot();
		}

		public Node getParent() {
			return parent;
		}

		public void setDimensions(double newWidth, double newHeight) {
			this.width = newWidth;
			this.height = newHeight;
		}

		/** Written only once set: ELK reads it for a port at a fixed position. */
		public void setLocation(double newX, double newY) {
			this.x = newX;
			this.y = newY;
			this.located = true;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}

	}

	public static final class Edge extends Element {

		private final Shape source;
		private final Shape target;
		private final Node container;
		private final List<Section> sections = new ArrayList<Section>();

		private Edge(String id, Shape source, Shape target, Node container) {
			super(id);
			this.source = source;
			this.target = target;
			this.container = container;
		}

		@Override
		Node getRoot() {
			return container.getRoot();
		}

		public Shape getSource() {
			return source;
		}

		public Shape getTarget() {
			return target;
		}

		public Node getContainingNode() {
			return container;
		}

		public List<Section> getSections() {
			return Collections.unmodifiableList(sections);
		}

	}

	public static final class Label extends Element {

		private final Element owner;
		private String text;
		private double x;
		private double y;
		private double width;
		private double height;

		private Label(String id, Element owner) {
			super(id);
			this.owner = owner;
		}

		@Override
		Node getRoot() {
			return owner.getRoot();
		}

		public void setText(String newText) {
			this.text = newText;
		}

		public String getText() {
			return text;
		}

		public void setDimensions(double newWidth, double newHeight) {
			this.width = newWidth;
			this.height = newHeight;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

	}

	public static final class Section {

		private final Point start;
		private final Point end;
		private final List<Point> bendPoints;

		private Section(Point start, Point end, List<Point> bendPoints) {
			this.start = start;
			this.end = end;
			this.bendPoints = Collections.unmodifiableList(bendPoints);
		}

		public Point getStart() {
			return start;
		}

		public Point getEnd() {
			return end;
		}

		public List<Point> getBendPoints() {
			return bendPoints;
		}

	}

	public static final class Point {

		private final double x;
		private final double y;

		private Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

	}

	/**
	 * Creates an edge between two nodes or ports of the same graph, in the node ELK
	 * Java would put it (<code>ElkGraphUtil.createSimpleEdge</code>): a port
	 * counts as its node.
	 */
	public static Edge createSimpleEdge(Shape source, Shape target) {
		final Node container = findContainer(source.getNode(), target.getNode());
		if (container == null)
			throw new IllegalArgumentException("The nodes are not in the same graph");
		final Edge edge = new Edge(container.getRoot().nextId("e"), source, target, container);
		container.edges.add(edge);
		return edge;
	}

	/**
	 * Same choice as <code>ElkGraphUtil.findBestEdgeContainment</code> of ELK
	 * 0.12 for an edge with one source and one target: a self loop belongs to the
	 * parent of its node, any other edge to the lowest common ancestor of its two
	 * nodes, a node counting as its own ancestor. So an edge between two siblings
	 * belongs to their parent, and an edge from a cluster to one of its
	 * descendants to the cluster.
	 */
	static Node findContainer(Node source, Node target) {
		if (source == target)
			return source.parent;
		for (Node current = source; current != null; current = current.parent)
			if (current.isAncestorOrSelfOf(target))
				return current;
		return null;
	}

	// =========================================================================
	// Layout
	// =========================================================================

	/**
	 * Lays the graph out with the registered layouter, and stores the coordinates
	 * in the nodes, edges and labels.
	 */
	public static void layout(Node root) {
		final ElkJsonLayouter current = layouter;
		if (current == null)
			throw new IllegalStateException("No ELK JSON layouter registered");
		if (root.parent != null)
			throw new IllegalArgumentException("Not a root node");
		applyLayout(root, current.layout(toJson(root)));
	}

	/** The graph, in the ELK JSON format. */
	public static String toJson(Node root) {
		return writeNode(root).toString();
	}

	private static JsonObject writeNode(Node node) {
		final JsonObject result = writeElement(node);
		// ELK Java nodes are 0 x 0 until they are given a size
		result.add("width", node.width);
		result.add("height", node.height);
		if (node.ports.isEmpty() == false) {
			final JsonArray ports = new JsonArray();
			for (Port port : node.ports)
				ports.add(writePort(port));
			result.add("ports", ports);
		}
		if (node.children.isEmpty() == false) {
			final JsonArray children = new JsonArray();
			for (Node child : node.children)
				children.add(writeNode(child));
			result.add("children", children);
		}
		if (node.edges.isEmpty() == false) {
			final JsonArray edges = new JsonArray();
			for (Edge edge : node.edges)
				edges.add(writeEdge(edge));
			result.add("edges", edges);
		}
		return result;
	}

	private static JsonObject writePort(Port port) {
		final JsonObject result = writeElement(port);
		if (port.located) {
			result.add("x", port.x);
			result.add("y", port.y);
		}
		result.add("width", port.width);
		result.add("height", port.height);
		return result;
	}

	private static JsonObject writeEdge(Edge edge) {
		final JsonObject result = writeElement(edge);
		result.add("sources", new JsonArray().add(edge.source.getId()));
		result.add("targets", new JsonArray().add(edge.target.getId()));
		return result;
	}

	private static JsonObject writeLabel(Label label) {
		final JsonObject result = writeElement(label);
		if (label.text != null)
			result.add("text", label.text);
		result.add("width", label.width);
		result.add("height", label.height);
		return result;
	}

	private static JsonObject writeElement(Element element) {
		final JsonObject result = new JsonObject();
		result.add("id", element.getId());
		if (element.options.isEmpty() == false) {
			final JsonObject options = new JsonObject();
			for (Map.Entry<String, Object> ent : element.options.entrySet()) {
				final Object value = ent.getValue();
				if (value instanceof Boolean)
					options.add(ent.getKey(), (Boolean) value);
				else if (value instanceof Number)
					options.add(ent.getKey(), ((Number) value).doubleValue());
				else
					options.add(ent.getKey(), (String) value);
			}
			result.add("layoutOptions", options);
		}
		if (element.labels.isEmpty() == false) {
			final JsonArray labels = new JsonArray();
			for (Label label : element.labels)
				labels.add(writeLabel(label));
			result.add("labels", labels);
		}
		return result;
	}

	/**
	 * Reads the coordinates of a laid out graph (the answer of the layouter) into
	 * the graph it was written from. The elements are matched by id.
	 */
	public static void applyLayout(Node root, String layoutJson) {
		final Map<String, Element> byId = new HashMap<String, Element>();
		index(root, byId);
		readNode(Json.parse(layoutJson).asObject(), byId);
	}

	private static void index(Node node, Map<String, Element> byId) {
		byId.put(node.getId(), node);
		indexLabels(node, byId);
		for (Port port : node.ports) {
			byId.put(port.getId(), port);
			indexLabels(port, byId);
		}
		for (Node child : node.children)
			index(child, byId);
		for (Edge edge : node.edges) {
			byId.put(edge.getId(), edge);
			indexLabels(edge, byId);
		}
	}

	private static void indexLabels(Element element, Map<String, Element> byId) {
		for (Label label : element.labels)
			byId.put(label.getId(), label);
	}

	private static Element find(JsonObject json, Map<String, Element> byId) {
		final String id = json.getString("id", null);
		final Element element = byId.get(id);
		if (element == null)
			throw new IllegalArgumentException("Unknown id in the ELK layout: " + id);
		return element;
	}

	private static void readNode(JsonObject json, Map<String, Element> byId) {
		final Node node = (Node) find(json, byId);
		node.x = json.getDouble("x", 0);
		node.y = json.getDouble("y", 0);
		node.width = json.getDouble("width", node.width);
		node.height = json.getDouble("height", node.height);
		readLabels(json, byId);
		for (JsonObject port : objects(json, "ports"))
			readPort(port, byId);
		for (JsonObject child : objects(json, "children"))
			readNode(child, byId);
		for (JsonObject edge : objects(json, "edges"))
			readEdge(edge, byId);
	}

	private static void readPort(JsonObject json, Map<String, Element> byId) {
		final Port port = (Port) find(json, byId);
		port.x = json.getDouble("x", 0);
		port.y = json.getDouble("y", 0);
		port.width = json.getDouble("width", port.width);
		port.height = json.getDouble("height", port.height);
		readLabels(json, byId);
	}

	private static void readEdge(JsonObject json, Map<String, Element> byId) {
		final Edge edge = (Edge) find(json, byId);
		edge.sections.clear();
		for (JsonObject section : objects(json, "sections")) {
			final List<Point> bendPoints = new ArrayList<Point>();
			for (JsonObject point : objects(section, "bendPoints"))
				bendPoints.add(readPoint(point));
			edge.sections.add(new Section(readPoint(section.get("startPoint").asObject()),
					readPoint(section.get("endPoint").asObject()), bendPoints));
		}
		readLabels(json, byId);
	}

	private static void readLabels(JsonObject json, Map<String, Element> byId) {
		for (JsonObject labelJson : objects(json, "labels")) {
			final Label label = (Label) find(labelJson, byId);
			label.x = labelJson.getDouble("x", 0);
			label.y = labelJson.getDouble("y", 0);
			label.width = labelJson.getDouble("width", label.width);
			label.height = labelJson.getDouble("height", label.height);
		}
	}

	private static Point readPoint(JsonObject json) {
		return new Point(json.getDouble("x", 0), json.getDouble("y", 0));
	}

	private static List<JsonObject> objects(JsonObject json, String name) {
		final JsonValue value = json.get(name);
		if (value == null || value.isArray() == false)
			return Collections.emptyList();
		final List<JsonObject> result = new ArrayList<JsonObject>();
		for (JsonValue element : value.asArray())
			result.add(element.asObject());
		return result;
	}

}
