package net.sourceforge.plantuml.abel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.klimt.color.Colors;

public class Harness implements Bag {

	private final String label;
	private final Harness parent;
	private final List<Link> links = new ArrayList<Link>();
	private Colors colors = Colors.empty();

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

}
