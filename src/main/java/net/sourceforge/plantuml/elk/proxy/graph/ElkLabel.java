package net.sourceforge.plantuml.elk.proxy.graph;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class ElkLabel extends ElkWithProperty {

	public ElkLabel(Object obj) {
		super(obj);
	}

	private ElkJsonGraph.Label json() {
		return obj instanceof ElkJsonGraph.Label ? (ElkJsonGraph.Label) obj : null;
	}

	public void setText(String text) {
		final ElkJsonGraph.Label json = json();
		if (json != null)
			json.setText(text);
		else
			Reflect.call(obj, "setText", text);
	}

	public void setDimensions(double width, double height) {
		final ElkJsonGraph.Label json = json();
		if (json != null)
			json.setDimensions(width, height);
		else
			Reflect.call2(obj, "setDimensions", width, height);
	}

	public String getText() {
		final ElkJsonGraph.Label json = json();
		if (json != null)
			return json.getText();
		return (String) Reflect.call(obj, "getText");
	}

	public double getX() {
		final ElkJsonGraph.Label json = json();
		if (json != null)
			return json.getX();
		return (Double) Reflect.call(obj, "getX");
	}

	public double getY() {
		final ElkJsonGraph.Label json = json();
		if (json != null)
			return json.getY();
		return (Double) Reflect.call(obj, "getY");
	}

}
