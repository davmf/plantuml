package net.sourceforge.plantuml.svek.orthoroute;

/**
 * A* search state: a visibility graph node plus the direction
 * of the last edge taken to reach it. This allows the cost
 * function to penalise direction changes (bends).
 */
public final class AStarState {

	private final VisNode node;
	private final boolean lastHorizontal;

	public AStarState(VisNode node, boolean lastHorizontal) {
		this.node = node;
		this.lastHorizontal = lastHorizontal;
	}

	public VisNode getNode() {
		return node;
	}

	public boolean isLastHorizontal() {
		return lastHorizontal;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof AStarState == false)
			return false;
		final AStarState other = (AStarState) obj;
		return node.equals(other.node)
				&& lastHorizontal == other.lastHorizontal;
	}

	@Override
	public int hashCode() {
		return node.hashCode() * 31
				+ (lastHorizontal ? 1 : 0);
	}
}
