package net.sourceforge.plantuml.elk.proxy.graph;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.ElkProperty;
import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.core.math.ElkPadding;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class ElkWithProperty {

	/** A real ELK object, or an {@link ElkJsonGraph} one. */
	public final Object obj;

	public ElkWithProperty(Object obj) {
		this.obj = Objects.requireNonNull(obj);
	}

	@Override
	final public int hashCode() {
		return this.obj.hashCode();
	}

	@Override
	final public boolean equals(Object other) {
		return this.obj.equals(((ElkWithProperty) other).obj);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	final public void setProperty(ElkProperty key, Object value) {
		if (obj instanceof ElkJsonGraph.Element) {
			((ElkJsonGraph.Element) obj).setOption(key.getJsonId(), toJsonValue(value));
		} else if (value instanceof EnumSet) {
			// Note: the set is filled with the proxy enums themselves, not with the ELK
			// ones, so ELK finds none of its own values in it (see toJsonValue)
			EnumSet result = null;
			for (Object foo : (Collection) value) {
				final ElkObjectProxy elk = (ElkObjectProxy) foo;
				if (result == null) {
					result = EnumSet.noneOf((Class) elk.getClass());
				}
				result.add(elk);
			}
			Reflect.call2(obj, "setProperty", key.getTrueObject(), result);
		} else if (value instanceof ElkObjectProxy) {
			final Object elk = ((ElkObjectProxy) value).getTrueObject();
			Reflect.call2(obj, "setProperty", key.getTrueObject(), elk);
		} else {
			Reflect.call2(obj, "setProperty", key.getTrueObject(), value);
		}
	}

	/** The value of an option, as written in the ELK JSON format. */
	private static Object toJsonValue(Object value) {
		if (value instanceof Collection) {
			final StringBuilder sb = new StringBuilder("[");
			for (Object element : (Collection<?>) value) {
				if (sb.length() > 1)
					sb.append(", ");
				sb.append(((Enum<?>) element).name());
			}
			return sb.append("]").toString();
		}
		if (value instanceof ElkPadding)
			return ((ElkPadding) value).toJsonValue();
		if (value instanceof Enum)
			return ((Enum<?>) value).name();
		return value;
	}

}
