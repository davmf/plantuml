package net.sourceforge.plantuml.sysml.interconnect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.sysml.SysmlException;
import net.sourceforge.plantuml.sysml.SysmlModel;
import net.sourceforge.plantuml.sysml.SysmlParser;

class InterconnectModelTest {

	private static final String PROFILE = "library package InterconnectProfile {\n"
			+ "  port def ConnectorPort { attribute designator : String; attribute gender : ConnectorGender; }\n"
			+ "  abstract part def Unit { attribute designator : String; }\n"
			+ "  part def PCBAUnit :> Unit;\n" + "  part def Cable { attribute designator : String; }\n"
			+ "  part def InterconnectSystem { part units : Unit[0..*]; part cables : Cable[0..*]; }\n" + "}\n";

	private static final String MODEL = "package M {\n" //
			+ "  port def Pin :> ConnectorPort {\n" //
			+ "    attribute redefines series = \"S\";\n" //
			+ "    port c1 : ContactPort { attribute redefines contactId = \"1\"; attribute redefines kind = ContactKind::power; }\n"
			+ "  }\n" //
			+ "  part def Board :> PCBAUnit { port J1 : Pin { attribute redefines gender = ConnectorGender::male; } }\n"
			+ "  part def SpecialBoard :> Board { port J2 : Pin; }\n" //
			+ "  part def Lead :> Cable { port P1 : Pin; port P2 : Pin { port redefines c1 { attribute redefines kind = ContactKind::nc; } } }\n"
			+ "  part sys : InterconnectSystem {\n" //
			+ "    part A1 : SpecialBoard { attribute redefines designator = \"A1x\"; }\n" //
			+ "    part X : Assembly { part W1 : Lead; part A2 : Board; connection : Mate connect W1.P2 to A2.J1; }\n"
			+ "    connection : Mate connect A1.J1 to X.W1.P1;\n" //
			+ "    connection : Mate connect A1.J7 to X.A2.J1;\n" //
			+ "  }\n" + "}\n";

	private static InterconnectModel build(boolean withProfile) throws SysmlException {
		final SysmlModel model = InterconnectModel.newSysmlModel();
		if (withProfile)
			model.add(SysmlParser.parse("profile.sysml", PROFILE));
		model.add(SysmlParser.parse("m.sysml", MODEL));
		return InterconnectModel.build(model, null);
	}

	@Test
	void builds_the_system_tree() throws SysmlException {
		for (boolean withProfile : new boolean[] { false, true }) {
			final InterconnectModel m = build(withProfile);
			final List<IcInstance> all = m.getInstances();
			assertEquals(4, all.size(), "profile placeholders must not become units");
			final IcInstance a1 = m.getSystem().getChild("A1");
			assertEquals(IcCategory.PCBA_UNIT, a1.getCategory());
			assertEquals("A1x", a1.getDesignator());
			assertEquals("SpecialBoard", a1.getTypeName());
			// Inherited from Board, then its own
			assertEquals(2, a1.getConnectors().size());
			final IcConnector j1 = a1.getConnector("J1");
			assertEquals("male", j1.getGender());
			assertEquals("S", j1.getSeries());
			assertEquals("J1", j1.getDesignator());
			assertEquals("power", j1.getContacts().get(0).getKind());

			final IcInstance w1 = m.getSystem().getChild("X").getChild("W1");
			assertEquals(IcCategory.CABLE, w1.getCategory());
			assertEquals("X.W1", w1.getPath());
			// A usage-level redefinition changes one copy only
			assertEquals("power", w1.getConnector("P1").getContacts().get(0).getKind());
			assertEquals("nc", w1.getConnector("P2").getContacts().get(0).getKind());
		}
	}

	@Test
	void resolves_connection_ends_and_reports_the_rest() throws SysmlException {
		final InterconnectModel m = build(false);
		assertEquals(2, m.getConnections().size());
		final IcConnection outer = m.getConnections().get(0);
		assertEquals(IcCategory.MATE, outer.getCategory());
		assertEquals("A1.J1", outer.getSource().toString());
		assertEquals("X.W1.P1", outer.getTarget().toString());
		assertEquals("X", m.getConnections().get(1).getOwner().getName());
		assertEquals(Collections.singletonList("m.sysml:13: cannot resolve A1.J7 from the system"),
				m.getWarnings());
	}

	@Test
	void resolves_contacts_and_splices() throws SysmlException {
		final InterconnectModel m = build(false);
		final IcInstance w1 = m.getSystem().getChild("X").getChild("W1");
		final IcEnd contact = InterconnectModel.resolveEnd(w1, "P1.c1");
		assertNotNull(contact.getContact());
		assertNull(InterconnectModel.resolveEnd(w1, "P1.c9"));
		// A whole instance is a UnitLink end
		final IcEnd unit = InterconnectModel.resolveEnd(m.getSystem(), "X.A2");
		assertEquals("A2", unit.getInstance().getName());
		assertNull(unit.getConnector());
		assertNull(InterconnectModel.resolveEnd(m.getSystem(), "X.Nope"));
	}

}
