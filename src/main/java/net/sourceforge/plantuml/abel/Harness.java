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

	// Where the spine channel sits along the source-to-destination span. AUTO
	// (the default) keeps the geometry-driven midpoint; SRC hugs the source
	// column, DST hugs the destination column. For a dual-spine bundle both
	// spines shift together toward the chosen side.
	public enum Align {
		AUTO, SRC, MID, DST
	}

	private final String label;
	private final Harness parent;
	private final List<Link> links = new ArrayList<Link>();
	private Colors colors = Colors.empty();
	private Shape shape = Shape.AUTO;
	private Align align = Align.AUTO;
	// Explicit channel slot. null means "unset — auto placement"; otherwise the
	// spine is pinned to this slot (slot * pitch from the channel base) and
	// exempt from overlap nudging.
	private Integer lane = null;

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

	public Align getAlign() {
		return align;
	}

	public void setAlign(Align align) {
		this.align = align;
	}

	public boolean hasLane() {
		return lane != null;
	}

	public int getLane() {
		return lane.intValue();
	}

	public void setLane(int lane) {
		this.lane = Integer.valueOf(lane);
	}

}
