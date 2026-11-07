package net.sourceforge.plantuml.sysml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class SysmlParserTest {

	private static SysmlNode first(String text) throws SysmlException {
		return SysmlParser.parse("t.sysml", text).getChildren().get(0);
	}

	@Test
	void definition_with_specialization_and_body() throws SysmlException {
		final SysmlNode def = first("abstract part def Board :> PCBAUnit, Other { port J1 : Pinout; }");
		assertEquals("part", def.getKeyword());
		assertTrue(def.isDefinition());
		assertEquals("Board", def.getName());
		assertEquals(Arrays.asList("PCBAUnit", "Other"), def.getSpecializes());
		final SysmlNode port = def.getChildren().get(0);
		assertEquals("port", port.getKeyword());
		assertEquals("Pinout", port.getType());
	}

	@Test
	void values_are_simplified() throws SysmlException {
		final SysmlNode p = first("part p { attribute redefines a = \"XT30\"; attribute redefines b = ContactKind::power; "
				+ "attribute redefines c = 600 [mm]; :>> d = 12; attribute redefines e default = Kind::shield; }");
		assertEquals("XT30", p.getChildren().get(0).getValue().getText());
		assertEquals("power", p.getChildren().get(1).getValue().getText());
		assertEquals("600 mm", p.getChildren().get(2).getValue().getText());
		final SysmlNode d = p.getChildren().get(3);
		assertNull(d.getKeyword());
		assertTrue(d.isRedefines());
		assertEquals("d", d.getName());
		assertEquals("12", d.getValue().getText());
		assertEquals("shield", p.getChildren().get(4).getValue().getText());
	}

	@Test
	void connections() throws SysmlException {
		final SysmlNode p = first("part s { connection : Mate connect A5.A3.J1 to W1.P3; connect a to b;"
				+ " connection w1 : Wire connect P1.c1 to SP1.node { attribute redefines gauge = \"18AWG\"; } }");
		final SysmlNode anonymous = p.getChildren().get(0);
		assertNull(anonymous.getName());
		assertEquals("Mate", anonymous.getType());
		assertEquals(Arrays.asList("A5.A3.J1", "W1.P3"), anonymous.getConnectEnds());
		assertEquals("connection", p.getChildren().get(1).getKeyword());
		final SysmlNode wire = p.getChildren().get(2);
		assertEquals("w1", wire.getName());
		assertEquals("18AWG", wire.getChildren().get(0).getValue().getText());
	}

	@Test
	void doc_comments_and_library() throws SysmlException {
		final SysmlNode pkg = first("library package L { doc /* First line\n   * second */ // note\n part def X; }");
		assertTrue(pkg.isLibrary());
		assertEquals("First line\nsecond", pkg.getDoc());
		assertTrue(pkg.getChildren().get(1).isInLibrary());
		assertEquals("L::X", pkg.getChildren().get(1).getQualifiedName());
	}

	@Test
	void imports_and_unknown_constructs_are_skipped() throws SysmlException {
		final SysmlNode pkg = first("package P { private import ISQ::*; #Meta part x : T [0..*] ordered; "
				+ "constraint def R { doc /* r */ } calc def F { in x : Real; x * 2 } }");
		assertEquals("import", pkg.getChildren().get(0).getKeyword());
		assertEquals("x", pkg.getChildren().get(1).getName());
		assertEquals("T", pkg.getChildren().get(1).getType());
	}

	@Test
	void short_names_and_multiple_types() throws SysmlException {
		final SysmlNode r = first("requirement <'REQ-001.1'> 'Flight time' :> Base { subject dut : 'CONF-1', C2; }");
		assertEquals("requirement", r.getKeyword());
		assertEquals("REQ-001.1", r.getShortName());
		assertEquals("Flight time", r.getName());
		assertEquals(Arrays.asList("Base"), r.getSpecializes());
		final SysmlNode subject = r.getChildren().get(0);
		assertEquals("subject", subject.getKeyword());
		assertEquals("dut", subject.getName());
		assertEquals("CONF-1", subject.getType());
		assertEquals(Arrays.asList("CONF-1", "C2"), subject.getTypes());
	}

	@Test
	void use_case() throws SysmlException {
		final SysmlNode u = first("use case def <'UC-1'> Survey;");
		assertEquals("use case", u.getKeyword());
		assertTrue(u.isDefinition());
		assertEquals("UC-1", u.getShortName());
		assertEquals("Survey", u.getName());
	}

	@Test
	void relationships() throws SysmlException {
		final SysmlNode p = first("package P { satisfy 'R-1' by a.b; verify requirement 'R-2' { @M; } "
				+ "#derivation dependency from 'R-3', 'R-4' to 'R-1'; dependency d from x to y, z; dependency u to v; "
				+ "allocate act to part1.sub; assert not satisfy R5 by w; "
				+ "#derivation connection { end #original ::> 'R-1'; end #derive ::> 'R-3'; } }");
		final SysmlNode satisfy = p.getChildren().get(0);
		assertEquals("satisfy", satisfy.getKeyword());
		assertNull(satisfy.getName());
		assertEquals(Arrays.asList("a.b"), satisfy.getSources());
		assertEquals(Arrays.asList("R-1"), satisfy.getTargets());
		final SysmlNode verify = p.getChildren().get(1);
		assertEquals("verify", verify.getKeyword());
		assertEquals(Arrays.asList("R-2"), verify.getTargets());
		assertTrue(verify.getSources().isEmpty());
		final SysmlNode derivation = p.getChildren().get(2);
		assertEquals(Arrays.asList("derivation"), derivation.getAnnotations());
		assertEquals(Arrays.asList("R-3", "R-4"), derivation.getSources());
		assertEquals(Arrays.asList("R-1"), derivation.getTargets());
		final SysmlNode named = p.getChildren().get(3);
		assertEquals("d", named.getName());
		assertEquals(Arrays.asList("x"), named.getSources());
		assertEquals(Arrays.asList("y", "z"), named.getTargets());
		assertEquals(Arrays.asList("u"), p.getChildren().get(4).getSources());
		final SysmlNode allocate = p.getChildren().get(5);
		assertEquals(Arrays.asList("act"), allocate.getSources());
		assertEquals(Arrays.asList("part1.sub"), allocate.getTargets());
		final SysmlNode asserted = p.getChildren().get(6);
		assertEquals("satisfy", asserted.getKeyword());
		assertEquals(Arrays.asList("R5"), asserted.getTargets());
		final SysmlNode connection = p.getChildren().get(7);
		assertEquals(Arrays.asList("derivation"), connection.getAnnotations());
		final SysmlNode original = connection.getChildren().get(0);
		assertEquals("end", original.getKeyword());
		assertEquals(Arrays.asList("original"), original.getAnnotations());
		assertEquals(Arrays.asList("R-1"), original.getSpecializes());
	}

	@Test
	void errors_name_file_and_line() {
		final SysmlException e = assertThrows(SysmlException.class,
				() -> SysmlParser.parse("b.sysml", "package P {\n part def X {\n"));
		assertEquals("b.sysml:2: missing '}' to close X", e.getMessage());
		final SysmlException s = assertThrows(SysmlException.class,
				() -> SysmlParser.parse("b.sysml", "part x { attribute a = \"open; }"));
		assertEquals("b.sysml:1: unterminated string", s.getMessage());
	}

	@Test
	void multiplicities_and_modifiers() throws SysmlException {
		final SysmlNode def = first("abstract part def V { part wheels : Wheel[4..*]; ref part driver[0..1] : Person;"
				+ " in item fuel : Fuel; return distance : Real; attribute mass : Real = 5 [kg];"
				+ " @Reviewed::Tag { by = 1; } }");
		assertTrue(def.hasModifier("abstract"));
		final SysmlNode wheels = def.getChildren().get(0);
		assertEquals("4..*", wheels.getMultiplicity());
		final SysmlNode driver = def.getChildren().get(1);
		assertEquals("part", driver.getKeyword());
		assertTrue(driver.hasModifier("ref"));
		assertEquals("0..1", driver.getMultiplicity());
		assertEquals("Person", driver.getType());
		assertTrue(def.getChildren().get(2).hasModifier("in"));
		final SysmlNode ret = def.getChildren().get(3);
		assertTrue(ret.hasModifier("return"));
		assertEquals("distance", ret.getName());
		// A unit after the value is not a multiplicity
		assertNull(def.getChildren().get(4).getMultiplicity());
		assertTrue(def.getChildren().get(5).hasModifier("@"));
	}

	@Test
	void use_case_definition() throws SysmlException {
		final SysmlNode def = first("use case def Drive { subject v : Vehicle; }");
		assertEquals("use case", def.getKeyword());
		assertTrue(def.isDefinition());
		assertEquals("Drive", def.getName());
	}

}
