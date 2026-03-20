package net.sourceforge.plantuml.abel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Harness implements Bag {

	private final String label;
	private final List<Link> links = new ArrayList<Link>();

	public Harness(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void addLink(Link link) {
		this.links.add(link);
	}

	public List<Link> getLinks() {
		return Collections.unmodifiableList(links);
	}

}
