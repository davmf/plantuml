package net.sourceforge.plantuml.svek.orthoroute;

import net.sourceforge.plantuml.klimt.geom.XPoint2D;

public final class VisNode {

	private final XPoint2D point;
	private final VisNodeKind kind;

	public VisNode(XPoint2D point, VisNodeKind kind) {
		this.point = point;
		this.kind = kind;
	}

	public VisNode(double x, double y, VisNodeKind kind) {
		this(new XPoint2D(x, y), kind);
	}

	public XPoint2D getPoint() {
		return point;
	}

	public double getX() {
		return point.getX();
	}

	public double getY() {
		return point.getY();
	}

	public VisNodeKind getKind() {
		return kind;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof VisNode == false)
			return false;
		final VisNode other = (VisNode) obj;
		return Double.compare(point.getX(), other.point.getX()) == 0
				&& Double.compare(point.getY(), other.point.getY()) == 0;
	}

	@Override
	public int hashCode() {
		final long xBits = Double.doubleToLongBits(point.getX());
		final long yBits = Double.doubleToLongBits(point.getY());
		return (int) (xBits ^ (xBits >>> 32)
				^ yBits ^ (yBits >>> 32));
	}

	@Override
	public String toString() {
		return "VisNode(" + point.getX() + ", " + point.getY()
				+ ", " + kind + ")";
	}
}
