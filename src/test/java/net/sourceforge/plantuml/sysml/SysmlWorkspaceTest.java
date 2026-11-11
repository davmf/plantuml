package net.sourceforge.plantuml.sysml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * The tracker layout (one file per item, parents including children with
 * <code>public import</code>), the parse cache and the text helpers behind
 * saved views. The diagrams themselves are covered by the Vega tests in
 * <code>vega/sysml/workspace</code>.
 */
class SysmlWorkspaceTest {

	private static final String TRACKER = "package 'TRK-SYS' {\n"
			+ "    @StrictDocWorkspace::Tracker { title = \"System requirements\"; kind = \"requirements\"; }\n"
			+ "    public import 'SYS-F-A'::*;\n" //
			+ "}\n";
	private static final String FOLDER = "package 'SYS-F-A' {\n"
			+ "    @StrictDocWorkspace::Folder { title = \"Heating\"; }\n" //
			+ "    public import 'SYS-001'::*;\n" //
			+ "    private import 'OTHER'::*;\n" //
			+ "}\n";
	private static final String ITEM = "package 'SYS-001' {\n" //
			+ "    private import 'TRK-SYS'::*;\n" //
			+ "    requirement r { doc /* x */ }\n" //
			+ "    dependency from r to 'STK-001';\n" //
			+ "}\n";
	private static final String OTHER = "package 'OTHER' { part p; }\n";

	private static SysmlWorkspace workspace(List<SysmlNode> roots) {
		return new SysmlWorkspace(roots);
	}

	private static List<SysmlNode> parse() throws SysmlException {
		return Arrays.asList(SysmlParser.parse("t.sysml", TRACKER), SysmlParser.parse("f.sysml", FOLDER),
				SysmlParser.parse("i.sysml", ITEM), SysmlParser.parse("o.sysml", OTHER));
	}

	@Test
	void public_imports_include_private_ones_do_not() throws SysmlException {
		final SysmlWorkspace ws = workspace(parse());
		final SysmlNode tracker = ws.getTopLevel("TRK-SYS");
		final SysmlNode folder = ws.getTopLevel("SYS-F-A");
		final SysmlNode item = ws.getTopLevel("SYS-001");
		assertEquals(Collections.singletonList(folder), ws.getIncludes(tracker));
		assertEquals(Collections.singletonList(item), ws.getIncludes(folder));
		// the item's private import of its tracker is not an include
		assertTrue(ws.getIncludes(item).isEmpty());
		assertNull(ws.getIncluder(ws.getTopLevel("OTHER")));

		final SysmlNode requirement = item.getChildren().get(1);
		assertTrue(ws.contains(tracker, requirement));
		assertFalse(ws.contains(ws.getTopLevel("OTHER"), requirement));
		assertEquals(Arrays.asList(tracker, folder), ws.getIncluders(requirement));
	}

	@Test
	void item_ids_and_titles() throws SysmlException {
		final SysmlWorkspace ws = workspace(parse());
		final SysmlNode item = ws.getTopLevel("SYS-001");
		final SysmlNode requirement = item.getChildren().get(1);
		// no short name: the ID is the item package's name
		assertEquals("SYS-001", ws.getItemId(requirement));
		assertEquals(item, ws.getItemPackage(requirement));
		// an import or a dependency is not the item
		assertNull(ws.getItemPackage(item.getChildren().get(0)));
		// a package nothing includes holds no item
		assertNull(ws.getItemPackage(ws.getTopLevel("OTHER").getChildren().get(0)));
		assertEquals("System requirements", SysmlWorkspace.getTitle(ws.getTopLevel("TRK-SYS")));
		assertEquals("Heating", SysmlWorkspace.getTitle(ws.getTopLevel("SYS-F-A")));
		assertEquals("OTHER", SysmlWorkspace.getTitle(ws.getTopLevel("OTHER")));
	}

	@Test
	void rst_directives_are_removed_from_doc_text() {
		final String doc = "The kettle shall boil.\n\n.. image:: _assets/curve.svg\n   :alt: Curve\n\nMeasured at 230 V.";
		assertEquals("The kettle shall boil.\n\nMeasured at 230 V.", SysmlText.withoutDirectives(doc));
		assertEquals("No directive here.", SysmlText.withoutDirectives("No directive here."));
		assertNull(SysmlText.withoutDirectives(null));
	}

	@Test
	void view_options_and_expose_paths() {
		assertEquals(Arrays.asList("actions=false", "focus=\"Boil time\"", "depth=2"),
				SysmlDiagramView.words(" actions=false  focus=\"Boil time\" depth=2 "));
		assertEquals(Arrays.asList("SM-001", "top", "idle"), SysmlDiagramView.segments("'SM-001'::top::idle::**"));
		assertEquals(Collections.singletonList("TRK-SYS"), SysmlDiagramView.segments("'TRK-SYS'::*"));
	}

	@Test
	void links_are_percent_encoded() {
		final SysmlOptions options = new SysmlOptions(Collections.singletonMap("links", "/item/{id}"));
		assertEquals(" [[/item/SYS-001.1]]", SysmlText.link(options, "SYS-001.1"));
		assertEquals(" [[/item/A%20B%3A%3AC]]", SysmlText.link(options, "A B::C"));
		assertEquals("", SysmlText.link(options, null));
		assertEquals("", SysmlText.link(new SysmlOptions(Collections.<String, String>emptyMap()), "X"));
	}

	@Test
	void a_changed_file_is_parsed_again() throws SysmlException {
		final List<String> names = Collections.singletonList("cache-test.sysml");
		final Map<String, String> options = Collections.singletonMap("view", "requirements");
		final String before = String.join("\n",
				SysmlImport.importSysml(names, Collections.singletonList("requirement <'R-1'> 'First' { }"), options,
						name -> false));
		assertTrue(before.contains("First"));
		final String after = String.join("\n",
				SysmlImport.importSysml(names, Collections.singletonList("requirement <'R-1'> 'Second' { }"),
						options, name -> false));
		assertTrue(after.contains("Second"));
		assertFalse(after.contains("First"));
	}

}
