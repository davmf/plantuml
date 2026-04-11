package net.sourceforge.plantuml.svek.orthoroute;

public final class VisEdge {

	private final VisNode from;
	private final VisNode to;
	private final boolean horizontal;
	private final double length;

	public VisEdge(VisNode from, VisNode to) {
		this.from = from;
		this.to = to;
		final double dx = Math.abs(to.getX() - from.getX());
		final double dy = Math.abs(to.getY() - from.getY());
		if (dx > 0.5 && dy > 0.5)
			throw new IllegalArgumentException(
					"VisEdge must be orthogonal: " + from + " -> " + to);
		this.horizontal = dy < 0.5;
		this.length = horizontal ? dx : dy;
	}

	public VisNode getFrom() {
		return from;
	}

	public VisNode getTo() {
		return to;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public double getLength() {
		return length;
	}

	public VisNode getOther(VisNode node) {
		if (node.equals(from))
			return to;
		if (node.equals(to))
			return from;
		throw new IllegalArgumentException(
				"Node not part of this edge: " + node);
	}

	@Override
	public String toString() {
		return "VisEdge(" + from + " -> " + to
				+ (horizontal ? " H" : " V")
				+ " len=" + length + ")";
	}
}
