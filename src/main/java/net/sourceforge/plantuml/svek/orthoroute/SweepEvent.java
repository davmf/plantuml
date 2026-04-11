package net.sourceforge.plantuml.svek.orthoroute;

import net.sourceforge.plantuml.klimt.geom.RectangleArea;
import net.sourceforge.plantuml.klimt.geom.XPoint2D;

public final class SweepEvent implements Comparable<SweepEvent> {

	private final double coord;
	private final SweepEventKind kind;
	private final RectangleArea obstacle;
	private final XPoint2D point;

	private SweepEvent(double coord, SweepEventKind kind,
			RectangleArea obstacle, XPoint2D point) {
		this.coord = coord;
		this.kind = kind;
		this.obstacle = obstacle;
		this.point = point;
	}

	public static SweepEvent open(double coord, RectangleArea obstacle) {
		return new SweepEvent(coord, SweepEventKind.OPEN, obstacle, null);
	}

	public static SweepEvent close(double coord, RectangleArea obstacle) {
		return new SweepEvent(coord, SweepEventKind.CLOSE, obstacle, null);
	}

	public static SweepEvent point(double coord, XPoint2D point) {
		return new SweepEvent(coord, SweepEventKind.POINT, null, point);
	}

	public double getCoord() {
		return coord;
	}

	public SweepEventKind getKind() {
		return kind;
	}

	public RectangleArea getObstacle() {
		return obstacle;
	}

	public XPoint2D getPoint() {
		return point;
	}

	@Override
	public int compareTo(SweepEvent other) {
		final int coordCmp = Double.compare(coord, other.coord);
		if (coordCmp != 0)
			return coordCmp;
		return kind.compareTo(other.kind);
	}
}
