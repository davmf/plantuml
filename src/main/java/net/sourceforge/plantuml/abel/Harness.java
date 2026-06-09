package net.sourceforge.plantuml.abel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.klimt.color.Colors;

public class Harness implements Bag {

	// Topology of the rendered bundle. AUTO lets SvekHarness choose between a
	// single spine and a dual-spine U based on geometry; the others force it.
	// U keeps the connecting trunk under the bottom of the stubs; CAP (an
	// inverted U) runs the trunk over the top.
	public enum Shape {
		AUTO, SINGLE, U, CAP
	}

	private final String label;
	private final Harness parent;
	private final List<Link> links = new ArrayList<Link>();
	private Colors colors = Colors.empty();
	private Shape shape = Shape.AUTO;

	public Harness(String label) {
		this(label, null);
	}

	public Harness(String label, Harness parent) {
		this.label = label;
		this.parent = parent;
	}

	public String getLabel() {
		return label;
	}

	public Harness getParent() {
		return parent;
	}

	public Harness getRoot() {
		Harness h = this;
		while (h.parent != null)
			h = h.parent;
		return h;
	}

	public boolean isNested() {
		return parent != null;
	}

	public void addLink(Link link) {
		this.links.add(link);
	}

	public List<Link> getLinks() {
		return Collections.unmodifiableList(links);
	}

	public Colors getColors() {
		return colors;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

}
