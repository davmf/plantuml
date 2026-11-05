package net.sourceforge.plantuml.elk.proxy;

/**
 * A layout option, known both by its ELK Java constant (resolved through
 * reflection, the first time it is used) and by its id in the ELK JSON format.
 */
public final class ElkProperty implements ElkObjectProxy {

	private final String className;
	private final String fieldName;
	private final String jsonId;
	private volatile Object trueObject;

	public ElkProperty(String className, String fieldName, String jsonId) {
		this.className = className;
		this.fieldName = fieldName;
		this.jsonId = jsonId;
	}

	@Override
	public Object getTrueObject() {
		Object result = trueObject;
		if (result == null) {
			result = Reflect.field(className, fieldName);
			trueObject = result;
		}
		return result;
	}

	public String getJsonId() {
		return jsonId;
	}

	@Override
	public String toString() {
		return jsonId;
	}

}
