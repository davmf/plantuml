## User

Is there an in-browser version of plantuml?  Does it include the eclipse layout kernel?

**Tool Call: ls; echo ---; grep -ril teavm --include=*.gradle --include=*.xml --include=*.md --include=*.java --include=*.properties . 2>/dev/null | grep -v '^./build/' | head -30; echo ---; grep -ril "elk" --include=*.gradle --include=*.xml --include=*.md --include=*.properties . 2>/dev/null | grep -v '^./build/' | head -20**
Status: Completed

Terminal:
```
ad5941-f767zi-interconnection.puml
ad5941_states_after.png
ad5941_states_before.png
ad5941_states.png
ad5941_states.puml
axis-break-example.png
build.gradle.kts
build.gradle.kts.eclipse
build.xml
CHANGES.md
CITATION.cff
CLAUDE-FIXING-ISSUES.md
CLAUDE.md
compilec.bat
component-ports-examples
CONTRIBUTING.md
COPYING
COPYING.LESSER
Dockerfile
docs
glama.json
gradle
gradle.properties
gradlew
gradlew.bat
issues
LICENSE
LICENSES.md
license-variants
manifest.txt
ortho-stubs-examples
perf.bat
perf.sh
plantuml.jar
plantuml-mcp-js
plantuml-natif
PUBLISHING_NPM.md
README.md
sample.png
scenario_tagging_examples
settings.gradle.kts
site
sjpp.jar
Splitting_overlapping_component_port_branches.md
src
state_machine_routines.iuml
State_ortho_edge_stubs_PR.md
teavm.bat
teavm.sh
teavm-wasm.bat
teavm-wasm.sh
tools
u_clockcycles.png
u_clockcyclesscale100as50pixels.png
u_ns.png
u_nsscale100as50pixels.png
---
build.xml
CLAUDE.md
Splitting_overlapping_component_port_branches.md
State_ortho_edge_stubs_PR.md
CHANGES.md
docs/CONVENTIONAL_COMMIT.md
README.md
PUBLISHING_NPM.md
docs/PR_SVG_PARSER.md
docs/OBSIDIAN_INTEGRATION.md
docs/OUTLINE_INTEGRATION.md
tools/perf-bench/README.md
license-variants/README.md
tools/clide_stubs/teavm/src/org/teavm/jso/JSObject.java
docs/BUILDING.md
docs/GITHUB_INTEGRATION.md
docs/NOTION_INTEGRATION.md
tools/browser-test/README.md
tools/clide_stubs/teavm/src/org/teavm/jso/typedarrays/Int8Array.java
tools/clide_stubs/teavm/src/org/teavm/jso/typedarrays/Uint8ClampedArray.java
tools/font-metrics/GenerateFontMetrics.java
tools/clide_stubs/teavm/src/org/teavm/jso/canvas/CanvasRenderingContext2D.java
tools/clide_stubs/teavm/src/org/teavm/jso/dom/xml/Document.java
tools/clide_stubs/teavm/src/org/teavm/jso/dom/xml/Element.java
tools/clide_stubs/teavm/src/org/teavm/interop/PlatformMarker.java
tools/clide_stubs/teavm/src/org/teavm/jso/JSExport.java
docs/LOGSEQ_INTEGRATION.md
tools/clide_stubs/teavm/README.md
tools/clide_stubs/teavm/build.xml
tools/clide_stubs/teavm/src/org/teavm/interop/AsyncCallback.java
---
Splitting_overlapping_component_port_branches.md
CHANGES.md
State_ortho_edge_stubs_PR.md
src/main/java/net/sourceforge/plantuml/dot/readme.md
src/main/java/net/sourceforge/plantuml/vizjs/readme.md
src/main/java/net/sourceforge/plantuml/sdot/readme.md
src/main/java/net/sourceforge/plantuml/elk/readme.md
```


**Tool Call: cat teavm.sh | head -60; echo ---; cat src/main/java/net/sourceforge/plantuml/elk/readme.md | head -40; echo ---; ls plantuml-natif plantuml-mcp-js | head; echo ---; grep -il "browser\|wasm\|teavm\|elk" README.md docs/BUILDING.md | head; grep -n -i "elk\|teavm\|wasm\|browser" README.md docs/BUILDING.md | head -40**
Status: Completed

Terminal:
```
#!/bin/bash
set -e
./gradlew clean teavm -Pfast
cd build/generated/teavm/js
python3 -m http.server 8080
---
# Directory Documentation for `elk`

## Description
This package provides classes used to export diagram with the [Eclipse Layout Kernel (ELK)](https://plantuml.com/elk) layout engine.

## Link
- [PlantUML 'Eclipse Layout Kernel (ELK)' Engine](https://plantuml.com/elk)

## Reference
- [Eclipse Layout Kernel (ELK)](https://eclipse.dev/elk/)
- [ELK Documentation](https://eclipse.dev/elk/documentation.html)

## Credit
- [`elk/proxy`](./proxy/) _(included in PlantUML)_
- :octocat: [eclipse/elk](https://github.com/eclipse/elk)

## See also other engines
- [GraphViz dot](https://plantuml.com/graphviz-dot)
  - [`plantuml/dot`](../dot/)
- [Smetana _(internal port of GraphViz in plantuml)_](https://plantuml.com/smetana02) and :octocat: [plantuml/smetana](https://github.com/plantuml/smetana)
  - [`plantuml/sdot`](../sdot/)
  - [`src/gen`](../../../../gen)
  - [`src/smetana/core`](../../../../smetana/core)
  - [`src/h`](../../../../h)
- [PlantUML Vizjs _(a port to Javascript of GraphViz)_](https://plantuml.com/vizjs)
  - [`plantuml/vizjs`](../vizjs/) _(included in PlantUML)_
  - :octocat: [Plantuml/Vizjs](https://github.com/plantuml/vizjs) _(not included in `plantuml/plantuml`)_---
plantuml-mcp-js:
build.gradle.kts
LICENSE
package.json
package-lock.json
README.md
README-NPM.md
server.js

plantuml-natif:
---
README.md
docs/BUILDING.md
README.md:3:> 🧩 **New!** Render PlantUML diagrams directly inside GitHub with [our official browser extension](https://github.com/plantuml/plantuml-for-github) &mdash; No server. No tokens. No tracking. Zero permissions but clipboard.
README.md:5:> [![Add to Chrome](https://img.shields.io/badge/Add%20to%20Chrome-4285F4?style=flat-square&logo=googlechrome&logoColor=white)](https://chromewebstore.google.com/detail/plantuml-for-github/lbokhidfopkdehkmlmpaabacljoediic) [![Add to Firefox](https://img.shields.io/badge/Add%20to%20Firefox-FF7139?style=flat-square&logo=firefoxbrowser&logoColor=white)](https://addons.mozilla.org/en-US/firefox/addon/plantuml-for-github/)
README.md:115:### 🌐 Building for the Browser (TeaVM)
README.md:117:PlantUML can be compiled to JavaScript with [TeaVM](https://teavm.org/) to run entirely client-side in the browser, with no server required. To produce the JavaScript artifacts and use them in your own project:
README.md:123:./gradlew clean teavm -Pfast
README.md:124:cp ./build/generated/teavm/js/plantuml.js $YOUR_PROJECT/plantuml.js
README.md:125:cp ./build/generated/teavm/js/viz-global.js $YOUR_PROJECT/viz-global.js
README.md:128:The `-Pfast` flag skips the slow parts of a regular build (tests, Javadoc, JaCoCo) so you only get the TeaVM output. The `viz-global.js` file provides [Viz.js](https://github.com/mdaines/viz-js) (Graphviz) support for diagrams that rely on Dot layout.
README.md:130:The `teavm` task also copies the PlantUML stdlib bundles and a ready-to-use `index.html` demo into `./build/generated/teavm/js/`.
docs/BUILDING.md:112:The `plantuml-natif` subproject builds a GraalVM native binary of the same code as `plantuml.jar` (headless CLI). CI no longer builds it: the snapshot ships the TeaVM C executables instead (see [`src/teavm-c/README.md`](../src/teavm-c/README.md)). The subproject is kept for now.
```


**Tool Call: grep -n -i -B2 -A25 "teavm" build.gradle.kts | head -150; echo ---; sed -n 110,140p README.md; echo ---; ls src/main/java/net/sourceforge/plantuml/elk/ ; find src/main/java -path '*elk*' -name '*.java' | head -5; echo ---; grep -rli "elk" src/main/java --include=*.java | grep -i teavm | head; grep -rn -i "elk" src/main/java/net/sourceforge/plantuml/code 2>/dev/null | head -3; git log --oneline -1 -- src/main/java/net/sourceforge/plantuml/elk 2>/dev/null**
Status: Completed

Terminal:
```
23-	alias(libs.plugins.gorylenko.gradle.git.properties)
24-//	alias(libs.plugins.adarshr.test.logger)
25:	alias(libs.plugins.teavm)
26-	alias(libs.plugins.sonarqube)
27-}
28-
29-group = "net.sourceforge.plantuml"
30-description = "PlantUML"
31-
32-java {
33-    if (!fastBuild) {
34-        withSourcesJar()
35-        withJavadocJar()
36-    }
37-}
38-
39-val jdependConfig by configurations.creating
40-
41-dependencies {
42-	compileOnly(libs.ant)
43-    testImplementation(platform(libs.junit.bom))
44-    testImplementation(libs.junit.jupiter)
45-    testRuntimeOnly(libs.junit.platform.launcher)
46-
47-	testImplementation(libs.xmlunit.core)
48-	testImplementation(libs.junit.pioneer)
49-    implementation(libs.elk.core)
50-    implementation(libs.elk.alg.layered)
--
55-	jdependConfig(libs.jdepend)
56-
57:	// TeaVM JSO APIs for browser interop (provided to the teavm source set by the plugin)
58:	teavm(teavm.libs.jsoApis)
59-
60-}
61-
62-repositories {
63-	mavenLocal()
64-	mavenCentral()
65-}
66-
67-// ============================================
68:// TeaVM Configuration - Java to JavaScript
69-// ============================================
70-
71:teavm {
72-	js {
73:		mainClass.set("net.sourceforge.plantuml.teavm.js.browser.PlantUMLBrowser")
74:		moduleType.set(org.teavm.gradle.api.JSModuleType.ES2015)
75-		// Diagnostic switch: -PjsStrict=true makes the JS build check nulls, array
76-		// bounds and casts like the JVM (and like the Wasm GC build, which is strict
77-		// by default), with readable names. Default: false, unchanged behavior.
78-		val jsStrict = providers.gradleProperty("jsStrict").map { it.toBoolean() }.getOrElse(false)
79-		strict.set(jsStrict)
80-		obfuscated.set(!jsStrict)
81:		optimization.set(org.teavm.gradle.api.OptimizationLevel.BALANCED)
82-		// obfuscated.set(false)
83:		// optimization.set(org.teavm.gradle.api.OptimizationLevel.NONE)
84:		// outputDir defaults to build/generated/teavm/js
85-	}
86:	// Experimental: Java to C (gradlew generateC), see src/teavm-c/README.md
87-	c {
88-		// The entry point is the one of the JVM (net.sourceforge.plantuml.Run), so that the
89-		// C executable has the same help and the same flags (what the C build cannot do is
90:		// guarded by TeaVM.isC(), see src/teavm-c/README.md).
91:		// -PteavmCMainClass=PngDemoC generates another entry point (see src/teavm-c/README.md).
92:		// A simple name is looked up in the teavm.c packages: PowerShell splits an
93-		// unquoted argument at its first dot, so a full name needs quotes there.
94:		mainClass.set(providers.gradleProperty("teavmCMainClass").map { name ->
95-			if (name.contains('.'))
96-				name
97-			else
98:				listOf("net.sourceforge.plantuml.teavm.c.", "net.sourceforge.plantuml.teavm.c.demo.")
99-					.map { it + name }
100-					.firstOrNull { project.file("src/main/java/" + it.replace('.', '/') + ".java").exists() }
101:					?: throw GradleException("teavmCMainClass: no class $name in the teavm.c packages")
102-		}.orElse("net.sourceforge.plantuml.Run"))
103-		// Easier to read while experimenting
104-		obfuscated.set(false)
105-		shortFileNames.set(false)
106:		// TeaVM C defaults to AGGRESSIVE (heavy inlining), which makes the coroutine
107-		// transformation of async methods fail with an ArrayIndexOutOfBoundsException
108-
109:		optimization.set(org.teavm.gradle.api.OptimizationLevel.BALANCED)
110:		// Heap size in MB (TeaVM default: 1 to 16). The heap starts at its maximum size, so that
111-		// it is never resized: with a smaller start (1 MB), plantuml.exe crashed with an access
112-		// violation (0xC0000005) on Windows ARM64, for instance on a diagram made of a single class.
113-		minHeapSize.set(64)
114-		maxHeapSize.set(64)
115:		// outputDir defaults to build/generated/teavm/c
116-	}
117-
118-	// Experimental WebAssembly (Wasm GC) build of the very same browser entry
119-	// point, shipped NEXT TO the JS build, never instead of it: Wasm GC needs a
120-	// recent runtime (Chrome 119+, Firefox 120+, Safari 18.2+, Node 22+) and is
121-	// not available in every sandbox (e.g. GitHub's rendering pipeline).
122:	// Build with: gradlew teavmWasm   (see the 'teavmWasm' task below)
123-	wasmGC {
124:		mainClass.set("net.sourceforge.plantuml.teavm.js.browser.PlantUMLBrowser")
125-		targetFileName.set("plantuml.wasm")
126-		// Diagnostic switch: -PwasmDebug=true keeps class names (obfuscated=false)
127-		// and emits plantuml.wasm.teadbg + plantuml.wasm-deobfuscator.wasm, so that
128-		// Java stack traces can be mapped back to classes/methods/lines when the
129-		// page opts in ('?wasmdebug' in the page URL, see plantuml-wasm.js).
130-		// Default: production build (obfuscated, no debug files).
131-		val wasmDebug = providers.gradleProperty("wasmDebug").map { it.toBoolean() }.getOrElse(false)
132-		obfuscated.set(!wasmDebug)
133-		debugInformation.set(wasmDebug)
134-		// BALANCED, like the JS build. AGGRESSIVE (the Wasm GC default) crashes
135:		// TeaVM 0.16.0 while inlining UBrexNamed.<init> (super(create(...)), i.e. a
136-		// static call with two branches as argument of the super constructor) into
137-		// UBrexCommandPartition.executeArg:
138-		//   AssertionError: Variable used before definition: @149 at $26
139:		optimization.set(org.teavm.gradle.api.OptimizationLevel.BALANCED)
140-		// Keep Java semantics (bounds checks, NPE, ClassCastException). Setting
141-		// this to false is faster, but must be validated before being used.
142-		// -PwasmStrict=false to compare with the JS build, which is not strict.
143-		strict.set(providers.gradleProperty("wasmStrict").map { it.toBoolean() }.getOrElse(true))
144-		// Emit plantuml.wasm-runtime.js as an ES module exporting load(), so
145-		// that plantuml-wasm.js can import it like plantuml.js is imported.
146-		copyRuntime.set(true)
147-		modularRuntime.set(true)
148:		// outputDir defaults to build/generated/teavm/wasm-gc
149-	}
150-}
151-
152:// TeaVM only writes the C sources: add a CMakeLists.txt next to them so the
153-// native build is just "cmake + build" (Windows: MSVC or clang-cl, see README),
154-// and the hand-written C they link with (plutovg, PNG facade, fonts) in native/
155-val copyNativeSources = tasks.register<Copy>("copyNativeSources") {
156:	from("src/teavm-c/CMakeLists.txt")
157:	from("src/teavm-c/native") {
158-		into("native")
159-	}
160-	// Files that Java reads at run time (EmbeddedFiles), embedded by CMake
161-	from("src/main/resources/themes") {
162-		include("*.puml")
163-		into("native/resources/themes")
164-	}
165-	// The CSS and JavaScript of the interactive SVG (!pragma svginteractive)
166-	from("src/main/resources/svg") {
167-		include("*.min.css", "*.min.js")
168-		into("native/resources/svg")
169-	}
170-	// The SVG sprites of the stereotypes (archimate)
171-	from("src/main/resources/sprites") {
172-		include("**/*.svg")
173-		into("native/resources/sprites")
174-	}
175-	// The OpenIconic icons (<&name>), and their list (listopeniconic)
---
1. Using Gradle: `./gradlew jar`
2. Using Ant: `ant`

For prerequisites and detailed build instructions, see our [BUILDING.md](https://github.com/plantuml/plantuml/blob/master/docs/BUILDING.md) guide.

### 🌐 Building for the Browser (TeaVM)

PlantUML can be compiled to JavaScript with [TeaVM](https://teavm.org/) to run entirely client-side in the browser, with no server required. To produce the JavaScript artifacts and use them in your own project:

```sh
git clone https://github.com/plantuml/plantuml.git
cd plantuml
chmod +x ./gradlew
./gradlew clean teavm -Pfast
cp ./build/generated/teavm/js/plantuml.js $YOUR_PROJECT/plantuml.js
cp ./build/generated/teavm/js/viz-global.js $YOUR_PROJECT/viz-global.js
```

The `-Pfast` flag skips the slow parts of a regular build (tests, Javadoc, JaCoCo) so you only get the TeaVM output. The `viz-global.js` file provides [Viz.js](https://github.com/mdaines/viz-js) (Graphviz) support for diagrams that rely on Dot layout.

The `teavm` task also copies the PlantUML stdlib bundles and a ready-to-use `index.html` demo into `./build/generated/teavm/js/`.

For a live example of client-side rendering, see the [JavaScript PlantUML Demo](https://plantuml.github.io/plantuml/js-plantuml).

## 🧱 Contributing

PlantUML is an open-source project, and we welcome contributions of all kinds. Whether you're helping us fix bugs, improve the docs, or spread the word, we appreciate your support. See our [contributing guide](CONTRIBUTING.md) for more information on how to get started.

The language guide on [plantuml.com](https://plantuml.com) is updated through [alphadoc](http://alphadoc.plantuml.com/). Edit a page there (pick a language and wiki syntax), save, and maintainers publish it after review. Details are in [CONTRIBUTING.md](CONTRIBUTING.md#updating-the-plantuml-guide-alphadoc).

For comprehensive and detailed documentation on using PlantUML, refer to the [official Javadoc, available here](https://plantuml.github.io/plantuml/javadoc). Please note that this documentation is a work in progress and may not be complete.
---
CucaDiagramFileMakerElk.java
MyElkCluster.java
MyElkDrawing.java
MyElkEdge.java
package-info.java
proxy
readme.md
src/main/java/net/sourceforge/plantuml/elk/MyElkEdge.java
src/main/java/net/sourceforge/plantuml/elk/MyElkCluster.java
src/main/java/net/sourceforge/plantuml/elk/package-info.java
src/main/java/net/sourceforge/plantuml/elk/MyElkDrawing.java
src/main/java/net/sourceforge/plantuml/elk/CucaDiagramFileMakerElk.java
---
331a6e774 🧹 Remove obsolete SJPP directives targeting __HAXE__
```


**Tool Call: ls src/main/java/net/sourceforge/plantuml/teavm/ 2>/dev/null; grep -rn -i "elk" src/main/java/net/sourceforge/plantuml/teavm src/main/java/net/sourceforge/plantuml/teavm/js 2>/dev/null | head; grep -rn -i "elk" src/main/java/net/sourceforge/plantuml/*.java src/main/java/net/sourceforge/plantuml/core/*.java 2>/dev/null | grep -i "teavm\|isJs\|TeaVM" | head; grep -rln "TeaVM.isJs\|isJs()" src/main/java | xargs grep -ln -i "elk\|layout" 2>/dev/null | head; grep -n -i "elk" build.xml | head -5; grep -rn -i "elk" src/main/java/net/sourceforge/plantuml/skin/rose/*.java 2>/dev/null | head -2; grep -rln -i "ElkCluster\|CucaDiagramFileMakerElk" src/main/java | head**
Status: Completed

Terminal:
```
c
DiagramRendererTeaVM.java
EmbeddedResourcesGenerator.java
EmbeddedResources.java
js
package-info.java
readme.md
TeaVM.java
TextareaSource.java
src/main/java/net/sourceforge/plantuml/svek/SvekEdge.java
src/main/java/net/atmp/CucaDiagram.java
src/main/java/net/sourceforge/plantuml/sdot/CucaDiagramFileMakerSmetana.java
src/main/java/net/sourceforge/plantuml/elk/MyElkDrawing.java
src/main/java/net/sourceforge/plantuml/elk/MyElkEdge.java
src/main/java/net/sourceforge/plantuml/elk/CucaDiagramFileMakerElk.java
src/main/java/net/sourceforge/plantuml/elk/MyElkCluster.java
src/main/java/net/sourceforge/plantuml/svek/SvekEdge.java
```


**Tool Call: grep -n -i "elk\|isJs\|TeaVM" src/main/java/net/atmp/CucaDiagram.java | head -20; echo ---; grep -n -i "elk" src/main/java/net/sourceforge/plantuml/svek/SvekEdge.java | head -5; echo ---; grep -rn -i "elk" src/main/java/net/sourceforge/plantuml/teavm/js/ | head -5; echo ---; grep -rn -i "elk" src/main/java/net/sourceforge/plantuml/teavm/*.java | head -5**
Status: Completed

Terminal:
```
83:import net.sourceforge.plantuml.elk.CucaDiagramFileMakerElk;
100:import net.sourceforge.plantuml.svek.CucaDiagramFileMakerTeaVM;
101:import net.sourceforge.plantuml.teavm.TeaVM;
269:				if (TeaVM.a())
467:		if (TeaVM.isTeaVM())
475:			maker = (TeaVM.isC() || this.isUseSmetana() || net.sourceforge.plantuml.teavm.js.GraphVizEngineJs.vizMissingFallback()) ? new CucaDiagramFileMakerSmetana(this) : new CucaDiagramFileMakerTeaVM(this);
478:		else if (this.isUseElk())
479:			maker = new CucaDiagramFileMakerElk(this);
489:		if (TeaVM.isTeaVM() && TeaVM.isC() == false && maker instanceof CucaDiagramFileMakerTeaVM
490:				&& net.sourceforge.plantuml.teavm.js.GraphVizEngineJs.isAvailable() == false)
838:		if (TeaVM.a())
987:		if (this.isUseElk())
988:			return InstallationRequirement.ELK_LIBRARY_REQUIRED;
---
120:@DuplicateCode(reference = "SvekEdge, CucaDiagramFileMakerElk, CucaDiagramFileMakerSmetana")
965:	@DuplicateCode(reference = "MyElkPath")
---
---
```


**Tool Call: sed -n 460,500p src/main/java/net/atmp/CucaDiagram.java; echo ---; sed -n 980,992p src/main/java/net/atmp/CucaDiagram.java**
Status: Completed

Terminal:
```
	@Override
	public TextBlock getTextBlock(int num, FileFormatOption fileFormatOption) throws IOException, InterruptedException {

		this.eventuallyBuildPhantomGroups(null);
		final CucaDiagramFileMaker maker;

		if (TeaVM.isTeaVM())
			// In the browser build, "!pragma layout smetana" selects the pure-Java
			// layout engine; with viz-global.js loaded, the default remains the
			// Graphviz bridge. When viz-global.js is NOT loaded, the diagram falls
			// back to Smetana instead of failing, mirroring the dotIsAvailable()
			// fallback of the JVM branch below (the pragma short-circuits, so the
			// probe and its one-time console note only run on the default path).
			// ::revert when JAVA8
			maker = (TeaVM.isC() || this.isUseSmetana() || net.sourceforge.plantuml.teavm.js.GraphVizEngineJs.vizMissingFallback()) ? new CucaDiagramFileMakerSmetana(this) : new CucaDiagramFileMakerTeaVM(this);
		// maker = new CucaDiagramFileMakerSmetana(this);
		// ::done
		else if (this.isUseElk())
			maker = new CucaDiagramFileMakerElk(this);
		else if (this.isUseSmetana() || this.dotIsAvailable() == false)
			maker = new CucaDiagramFileMakerSmetana(this);
		else
			maker = new CucaDiagramFileMakerSvek(this);

		// ::comment when JAVA8
		// The browser build renders the diagram again once the Viz.js instance is
		// ready (PendingResources): an attempt made before is discarded, so it stops
		// here instead of computing a layout for nothing.
		if (TeaVM.isTeaVM() && TeaVM.isC() == false && maker instanceof CucaDiagramFileMakerTeaVM
				&& net.sourceforge.plantuml.teavm.js.GraphVizEngineJs.isAvailable() == false)
			throw new IOException("Viz is loading");
		// ::done

		return maker.getTextBlock(getDotStrings(), fileFormatOption);
	}

	private boolean dotIsAvailable() {
		final GraphvizRuntimeEnvironment gre = GraphvizRuntimeEnvironment.getInstance();

		// An explicit request for VizJs (skinparam or GRAPHVIZ_DOT=vizjs) must be honored
---

	public int countByName(String full) {
		return this.namespace.countByName(full);
	}

	@Override
	public final InstallationRequirement getInstallationRequirement() {
		if (this.isUseElk())
			return InstallationRequirement.ELK_LIBRARY_REQUIRED;
		else if (this.isUseSmetana())
			return InstallationRequirement.NONE;
		else
			return InstallationRequirement.GRAPHVIZ_ENGINE_REQUIRED;
```


## Assistant

**Yes, there is an in-browser build, but it does not use the Eclipse Layout Kernel.**

**The browser build**
- PlantUML can be compiled to JavaScript with TeaVM and runs entirely client-side with no server. The README (section "Building for the Browser (TeaVM)") gives the command `./gradlew clean teavm -Pfast`, which outputs `plantuml.js` and `viz-global.js` to `build/generated/teavm/js/`, plus a demo `index.html`.
- There is also an experimental WebAssembly GC build (`teavmWasm`, `plantuml.wasm`), shipped alongside the JS build.
- A live demo is at https://plantuml.github.io/plantuml/js-plantuml.

**ELK in the browser**
- The Gradle build does pull in the ELK libraries (`libs.elk.core`, `libs.elk.alg.layered` in `build.gradle.kts`), so the classes are on the classpath.
- But the engine selection in `src/main/java/net/atmp/CucaDiagram.java` (around lines 467–479) checks `TeaVM.isTeaVM()` first. In the browser build, `!pragma layout elk` never reaches the ELK branch. The diagram goes to Viz.js (Graphviz) by default, or to Smetana (the pure-Java Graphviz port) if `!pragma layout smetana` is set or Viz.js isn't loaded.
- So in the browser, the ELK engine is effectively unavailable. I read this from the code; I did not run a browser build to confirm it.

**Where ELK does work**
- The JVM build (`plantuml.jar`, the CLI, and the PlantUML server) includes ELK, selected with `!pragma layout elk`.

If you need ELK layouts client-side, that would mean changing the `TeaVM.isTeaVM()` branch in `CucaDiagram.java` to route ELK to `CucaDiagramFileMakerElk`, then checking that the ELK code compiles and runs under TeaVM. I can look into that if you want.

## User

I am interested in a client-side version that uses ELK.

**Tool Call: wc -l src/main/java/net/sourceforge/plantuml/elk/*.java; ls src/main/java/net/sourceforge/plantuml/elk/proxy | head -50; echo ---; ls src/main/java/net/sourceforge/plantuml/teavm/js src/main/java/net/sourceforge/plantuml/teavm/js/*/ ; echo ---; cat src/main/java/net/sourceforge/plantuml/teavm/readme.md | head -80**
Status: Completed

Terminal:
```
  501 src/main/java/net/sourceforge/plantuml/elk/CucaDiagramFileMakerElk.java
  171 src/main/java/net/sourceforge/plantuml/elk/MyElkCluster.java
  352 src/main/java/net/sourceforge/plantuml/elk/MyElkDrawing.java
  322 src/main/java/net/sourceforge/plantuml/elk/MyElkEdge.java
   12 src/main/java/net/sourceforge/plantuml/elk/package-info.java
 1358 total
core
ElkObjectProxy.java
graph
Reflect.java
---
src/main/java/net/sourceforge/plantuml/teavm/js:
browser
DriverCenteredCharacterJs.java
DriverDotPathJs.java
DriverEllipseJs.java
DriverImageJs.java
DriverImageSvgJs.java
DriverLineJs.java
DriverPathJs.java
DriverPolygonJs.java
DriverRectangleJs.java
DriverTextJs.java
ElementAdapterJs.java
GraphVizEngineJs.java
headless
JlatexmathResourcesJs.java
PSystemBuilderJs.java
StringBounderTeaVM.java
SvgDocumentJs.java
SvgGraphicsJs.java
UGraphicJs.java

src/main/java/net/sourceforge/plantuml/teavm/js/browser/:
BrowserLog.java
FileLoaderJs.java
package-info.java
PendingResources.java
PlantUMLBrowser.java
RenderQueue.java
ScriptLoaderJs.java

src/main/java/net/sourceforge/plantuml/teavm/js/headless/:
JsonJs.java
PlantUMLHeadless.java
---
# Directory Documentation for `teavm`

## Description
This package provides classes used to manage JS-PlantUML using TeaVM.

## Link
- [PlantUML JavaScript Editor Collection](https://plantuml.github.io/plantuml/js-plantuml/index-collection.html)

## Reference
- [TeaVM](https://teavm.org/)
- [Viz-js.com](https://viz-js.com/)

## Credit
- :octocat: [Konsoletyper/Teavm](https://github.com/konsoletyper/teavm)
- :octocat: [Mdaines/Viz-js](https://github.com/mdaines/viz-js)

## Misc.
```


**Tool Call: find src/main/java/net/sourceforge/plantuml/elk/proxy -name '*.java' | xargs wc -l | tail -1; cat src/main/java/net/sourceforge/plantuml/elk/proxy/Reflect.java | sed -n 30,200p; echo; cat src/main/java/net/sourceforge/plantuml/elk/proxy/ElkObjectProxy.java | sed -n 35,120p**
Status: Completed

Terminal:
```
  658 total
 *
 *
 * Original Author:  Arnaud Roques
 *
 *
 */
package net.sourceforge.plantuml.elk.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sourceforge.plantuml.log.Logme;

/*
 * Various methods to do Java introspection
 */
public class Reflect {

	public static Class clazz(String className) {
		try {
			return Class.forName(className);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Enum getEnum(String clazz, String name) {
		try {
			final Class cl = clazz(clazz);
			for (Object en : cl.getEnumConstants()) {
				if (en.toString().equals(name)) {
					return (Enum) en;
				}
			}
			throw new UnsupportedOperationException(name);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object opt(String className, String fieldname) {
		try {
			final Class<?> cl = Class.forName(className);
			final Field field = cl.getField(fieldname);
			return field.get(null);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object newInstance(String className) {
		try {
			final Class<?> cl = Class.forName(className);
			return cl.newInstance();
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object newInstance(String className, Object arg1) {
		try {
			final Class<?> cl = Class.forName(className);
			final Constructor<?> m = cl.getConstructor(arg1.getClass());
			return m.newInstance(arg1);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object newInstance(String className, double arg1, double arg2, double arg3, double arg4) {
		try {
			final Class<?> cl = Class.forName(className);
			final Constructor<?> m = cl.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE);
			return m.newInstance(arg1, arg2, arg3, arg4);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object field(String className, String fieldName) {
		try {
			final Class<?> cl = Class.forName(className);
			final Field f = cl.getField(fieldName);
			return f.get(null);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object callStatic(String className, String method) {
		try {
			final Class<?> cl = Class.forName(className);
			final Method m = cl.getMethod(method);
			return m.invoke(null);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object callStatic(String className, String method, Object arg1) {
		try {
			final Class<?> cl = Class.forName(className);
			final Method m = cl.getMethod(method, arg1.getClass());
			return m.invoke(null, arg1);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object callStatic(String className, String method, Object arg1, Object arg2) {
		try {
			final Class<?> cl = Class.forName(className);
			final Method m = cl.getMethod(method, arg1.getClass(), arg2.getClass());
			return m.invoke(null, arg1, arg2);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object callStatic2(String className, String method, Object arg1) {
		try {
			final Class<?> cl = Class.forName(className);
			final Method m = getStaticMethod(cl, method, 1);
			return m.invoke(null, arg1);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object callStatic2(String className, String method, Object arg1, Object arg2) {
		try {
			final Class<?> cl = Class.forName(className);
			final Method m = getStaticMethod(cl, method, 2);
			return m.invoke(null, arg1, arg2);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Method getStaticMethod(Class<?> cl, String method, int nbArgs) {
		for (Method m : cl.getMethods()) {
			if (m.getName().equals(method) && m.getParameters().length == nbArgs) {
				return m;
			}
		}
		throw new IllegalArgumentException();
	}

	public static Object call(Object instance, String method) {
		try {
			final Method m = instance.getClass().getMethod(method);
			return m.invoke(instance);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}
```


**Tool Call: wc -l src/main/java/net/sourceforge/plantuml/teavm/js/GraphVizEngineJs.java src/main/java/net/sourceforge/plantuml/svek/CucaDiagramFileMakerTeaVM.java src/main/java/net/sourceforge/plantuml/teavm/js/browser/*.java; sed -n 35,400p src/main/java/net/sourceforge/plantuml/teavm/js/GraphVizEngineJs.java**
Status: Completed

Terminal:
```
  216 src/main/java/net/sourceforge/plantuml/teavm/js/GraphVizEngineJs.java
  152 src/main/java/net/sourceforge/plantuml/svek/CucaDiagramFileMakerTeaVM.java
   67 src/main/java/net/sourceforge/plantuml/teavm/js/browser/BrowserLog.java
  279 src/main/java/net/sourceforge/plantuml/teavm/js/browser/FileLoaderJs.java
   36 src/main/java/net/sourceforge/plantuml/teavm/js/browser/package-info.java
  407 src/main/java/net/sourceforge/plantuml/teavm/js/browser/PendingResources.java
  530 src/main/java/net/sourceforge/plantuml/teavm/js/browser/PlantUMLBrowser.java
  201 src/main/java/net/sourceforge/plantuml/teavm/js/browser/RenderQueue.java
  229 src/main/java/net/sourceforge/plantuml/teavm/js/browser/ScriptLoaderJs.java
 2117 total
package net.sourceforge.plantuml.teavm.js;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

import net.sourceforge.plantuml.teavm.js.browser.PendingResources;
import net.sourceforge.plantuml.teavm.js.browser.ScriptLoaderJs;

/**
 * Renders DOT graphs to SVG with Viz.js, synchronously.
 * <p>
 * In {@code @viz-js/viz} 3.x, only {@code Viz.instance()} is asynchronous (it
 * loads the WebAssembly module); {@code renderString} on the instance is
 * synchronous. So the instance is created once, from JavaScript
 * ({@link #prepare(ScriptLoaderJs.Ok)}), and kept on the global object as
 * {@code __plantumlVizInstance}; rendering then just calls it. Nothing waits
 * in Java: when the instance is not ready yet, rendering notes it as missing
 * and fails, and the diagram is rendered again once it is ready (see
 * {@link PendingResources}).
 * <p>
 * Usage:
 * <pre>
 * String svg = GraphVizEngineJs.renderDotToSvg("digraph G { A -> B; }");
 * </pre>
 */
public class GraphVizEngineJs {

	/**
	 * Where the Viz.js object comes from. A host that bundles viz-global.js (a UMD)
	 * into its own file does not get a global {@code Viz}: the UMD attaches it to
	 * the bundle's exports. Such a host sets {@code globalThis.PLANTUML_VIZ = Viz}
	 * itself; it takes precedence over the global {@code Viz} that loading
	 * viz-global.js with a script tag defines.
	 */
	private static final String FIND_VIZ_STMT = "var V = globalThis.PLANTUML_VIZ || (typeof Viz !== 'undefined' ? Viz : null);";

	private static final String NOT_LOADED = "Viz is not loaded: this diagram type needs the Graphviz layout engine (viz-global.js). Load viz-global.js before rendering.";

	// ::remove file when JAVA8

	/**
	 * True when the Viz.js bridge (viz-global.js) is NOT loaded on the page,
	 * so the caller should fall back to the Smetana layout engine. Logs a
	 * one-time console note the first time the fallback is taken.
	 * <p>
	 * A Viz.js that is loaded but whose instance cannot be created (the
	 * WebAssembly module fails to load) is not a fallback: the diagram reports
	 * that failure, naming Viz.
	 */
	@JSBody(script = FIND_VIZ_STMT + "var missing = !V || typeof V.instance !== 'function';" +
			"if (missing && !globalThis.__plantumlSmetanaFallbackNoted) {" +
			"  globalThis.__plantumlSmetanaFallbackNoted = true;" +
			"  console.info('PlantUML: viz-global.js is not loaded, falling back to the Smetana layout engine');" +
			"}" +
			"return missing;")
	public static native boolean vizMissingFallback();

	/**
	 * Whether a layout can call {@link #renderDotToSvg(String)} now: the
	 * instance is there, or creating it failed (which {@code renderDotToSvg}
	 * then reports). When it is not there yet, it is noted as missing and this
	 * returns <code>false</code>: the attempt will be discarded and rendered
	 * again once the instance is ready, so the caller should stop at once rather
	 * than compute a layout for nothing.
	 */
	public static boolean isAvailable() {
		if (isReady() || getFailure(NOT_LOADED) != null)
			return true;
		PendingResources.requireViz();
		return false;
	}

	/**
	 * Forgets why creating the instance failed, so that the next request tries
	 * again: a network failure while loading the WebAssembly module may be
	 * transient. Called at the start of each request.
	 */
	@JSBody(script = "delete globalThis.__plantumlVizError;")
	public static native void forgetFailure();

	/**
	 * Renders a DOT graph description to SVG format, with the {@code dot}
	 * engine.
	 *
	 * @param dotSource the graph description in DOT format
	 * @return the rendered SVG as a string
	 * @throws RuntimeException when Viz.js is not loaded, failed to load, is
	 *                          still loading, or fails on this graph
	 */
	public static String renderDotToSvg(String dotSource) {
		return renderDotToSvg(dotSource, "dot");
	}

	/**
	 * Renders a DOT graph using a specific GraphViz engine.
	 *
	 * @param dotSource the graph description in DOT format
	 * @param engine    the GraphViz engine to use (dot, neato, fdp, sfdp, twopi, circo)
	 * @return the rendered SVG as a string
	 * @throws RuntimeException when Viz.js is not loaded, failed to load, is
	 *                          still loading, or fails on this graph
	 */
	public static String renderDotToSvg(String dotSource, String engine) {
		if (isReady() == false) {
			final String failure = getFailure(NOT_LOADED);
			if (failure != null)
				throw new RuntimeException(failure);
			// The instance is being created: this attempt is discarded, and the diagram
			// is rendered again once it is ready.
			PendingResources.requireViz();
			throw new RuntimeException("Viz is loading");
		}
		final JSObject result = renderString(dotSource, engine);
		final String error = getProperty(result, "error");
		if (error != null)
			throw new RuntimeException(error);
		return getProperty(result, "svg");
	}

	/** Whether the Viz.js instance has been created. */
	@JSBody(script = "return !!globalThis.__plantumlVizInstance;")
	private static native boolean isReady();

	/**
	 * Why there is no instance: Viz.js is not on the page, or creating the
	 * instance failed; <code>null</code> when it is being created, or was never
	 * asked for.
	 */
	@JSBody(params = "notLoaded", script = FIND_VIZ_STMT
			+ "if (!V || typeof V.instance !== 'function') return notLoaded;"
			+ "var e = globalThis.__plantumlVizError;"
			+ "return (typeof e === 'string') ? e : null;")
	private static native String getFailure(String notLoaded);

	/**
	 * Creates the Viz.js instance, once, and calls <code>done</code> when it is
	 * there or when creating it failed (the reason is then kept as
	 * {@code __plantumlVizError}, until the next request: see
	 * {@link #forgetFailure()}), or at once when there is nothing to wait for.
	 * Concurrent calls share the same creation.
	 */
	@JSBody(params = "done", script = FIND_VIZ_STMT
			+ "var g = globalThis;"
			+ "if (g.__plantumlVizInstance || typeof g.__plantumlVizError === 'string'"
			+ "    || !V || typeof V.instance !== 'function') { done(); return; }"
			+ "if (g.__plantumlVizWaiting) { g.__plantumlVizWaiting.push(done); return; }"
			+ "g.__plantumlVizWaiting = [done];"
			+ "var flush = function() {"
			+ "  var list = g.__plantumlVizWaiting; g.__plantumlVizWaiting = null;"
			+ "  for (var i = 0; i < list.length; i++) list[i]();"
			+ "};"
			+ "try {"
			+ "  Promise.resolve(V.instance()).then(function(viz) {"
			+ "    if (viz && typeof viz.renderString === 'function') g.__plantumlVizInstance = viz;"
			+ "    else g.__plantumlVizError = 'Viz.instance() did not give a Viz instance';"
			+ "    flush();"
			+ "  }, function(err) {"
			+ "    g.__plantumlVizError = String(err);"
			+ "    flush();"
			+ "  });"
			+ "} catch (e) {"
			+ "  g.__plantumlVizError = String(e);"
			+ "  flush();"
			+ "}")
	public static native void prepare(ScriptLoaderJs.Ok done);

	/**
	 * Renders with the instance, synchronously. Viz.js throws on a graph it
	 * cannot render; the exception is caught here, so that nothing thrown by
	 * JavaScript crosses into Java, and returned as {@code error}.
	 */
	@JSBody(params = { "dotSource", "engine" }, script = "try {"
			+ "  return { svg: globalThis.__plantumlVizInstance.renderString(dotSource, { format: 'svg', engine: engine }) };"
			+ "} catch (e) {"
			+ "  return { error: String(e) };"
			+ "}")
	private static native JSObject renderString(String dotSource, String engine);

	@JSBody(params = { "obj", "key" }, script = "var v = obj[key]; return (typeof v === 'string') ? v : null;")
	private static native String getProperty(JSObject obj, String key);

}
```


**Tool Call: ls src/main/java/net/sourceforge/plantuml/elk/proxy/*/ ; which node npm java gradle; node --version; java -version 2>&1 | head -1; ls ~/.gradle/caches/modules-2/files-2.1 2>/dev/null | grep -i -E "teavm|elk" ; ls build/generated/teavm 2>/dev/null; grep -n -i "elk\|teavm" gradle/libs.versions.toml**
Status: Completed

Terminal:
```
src/main/java/net/sourceforge/plantuml/elk/proxy/core/:
math
options
RecursiveGraphLayoutEngine.java
util

src/main/java/net/sourceforge/plantuml/elk/proxy/graph/:
ElkBendPoint.java
ElkEdge.java
ElkEdgeSection.java
ElkLabel.java
ElkNode.java
ElkWithProperty.java
util
/home/david/.local/share/zed/node/node-v24.22.1-linux-x64/bin/node
/home/david/.local/share/zed/node/node-v24.22.1-linux-x64/bin/npm
/usr/bin/java
v24.22.1
openjdk version "21.0.12.1" 2026-08-18
8:elk                 = "0.12.0"
18:teavm               = "0.16.0"
24:elk-core                    = { module = "org.eclipse.elk:org.eclipse.elk.core", version.ref = "elk" }
25:elk-alg-layered             = { module = "org.eclipse.elk:org.eclipse.elk.alg.layered", version.ref = "elk" }
26:elk-alg-mrtree              = { module = "org.eclipse.elk:org.eclipse.elk.alg.mrtree", version.ref = "elk" }
35:teavm-classlib              = { module = "org.teavm:teavm-classlib", version.ref = "teavm" }
36:teavm-jso-apis              = { module = "org.teavm:teavm-jso-apis", version.ref = "teavm" }
44:teavm = { id = "org.teavm", version.ref = "teavm" }
```


**Tool Call: cd /tmp && timeout 60 npm view elkjs version dist.unpackedSize 2>&1 | tail -3; cd /home/david/plantuml && timeout 120 ./gradlew --version 2>&1 | tail -5**
Status: Completed

Terminal:
```
npm notice Changelog: https://github.com/npm/cli/releases/tag/v12.2.2
npm notice To update run: npm install -g npm@12.2.2
npm notice
Ant:           Apache Ant(TM) version 1.10.15 compiled on August 25 2024
Launcher JVM:  21.0.12.1 (Debian 21.0.12.1+1-1-deb13u1-Debian)
Daemon JVM:    /usr/lib/jvm/java-21-openjdk-amd64 (no Daemon JVM specified, using current Java home)
OS:            Linux 6.12.74+deb13+1-amd64 amd64
```


**Tool Call: cd /tmp && npm view elkjs version 2>/dev/null; mkdir -p /tmp/elkjs-dl && cd /tmp/elkjs-dl && npm pack elkjs 2>&1 | tail -1 && tar xzf elkjs-*.tgz && ls -la package/lib**
Status: Completed

Terminal:
```
0.12.1
elkjs-0.12.1.tgz
total 8028
drwxrwxr-x 2 david david     220 Nov  6 05:26 .
drwxrwxr-x 3 david david     120 Nov  6 05:26 ..
-rw-r--r-- 1 david david    3582 Oct 26  1985 elk-api.d.ts
-rw-r--r-- 1 david david    9782 Oct 26  1985 elk-api.js
-rw-r--r-- 1 david david     837 Oct 26  1985 elk.bundled.d.ts
-rw-r--r-- 1 david david 1646241 Oct 26  1985 elk.bundled.js
-rw-r--r-- 1 david david      28 Oct 26  1985 elk-worker.d.ts
-rw-r--r-- 1 david david 4899976 Oct 26  1985 elk-worker.js
-rw-r--r-- 1 david david 1631868 Oct 26  1985 elk-worker.min.js
-rw-r--r-- 1 david david     837 Oct 26  1985 main.d.ts
-rw-r--r-- 1 david david    5069 Oct 26  1985 main.js

Shell cwd was reset to /home/david/plantuml
```


## Assistant

<thinking>
I'm checking whether elkjs (version 0.12.1, matching our ELK 0.12.0) can run layout synchronously for the PlantUML render path, as a bridge like the Viz.js one; next I'll confirm that.


</thinking>

**Tool Call: cd /tmp/elkjs-dl/package/lib && grep -n "FakeWorker\|setTimeout\|postMessage\|dispatch" elk-api.js | head -40; echo ---; grep -n -o ".\{200\}FakeWorker.\{400\}" elk-worker.js | head -5; echo; grep -n "setTimeout" elk-worker.js | head**
Status: Completed

Terminal:
```
56:    if (typeof worker.postMessage !== 'function') {
57:      throw new TypeError("Created worker does not provide" + " the required 'postMessage' function.");
64:    this.worker.postMessage({
84:      return this.worker.postMessage({
97:      return this.worker.postMessage({
104:      return this.worker.postMessage({
111:      return this.worker.postMessage({
133:      setTimeout(function () {
139:    key: "postMessage",
140:    value: function postMessage(msg) {
156:        self.worker.postMessage(msg);
---

8414:      watchdogEntryDepthTimerId = $wnd.setTimeout(watchdogEntryDepthRun, 10);
8455:  $wnd.setTimeout(function(){
81849:      setTimeout(function(){

Shell cwd was reset to /home/david/plantuml
```


**Tool Call: cd /tmp/elkjs-dl/package/lib && sed -n 81800,81900p elk-worker.js; sed -n 8440,8470p elk-worker.js**
Status: Completed

Terminal:
```
function exportLayout(){
  $clinit_ElkJs();
  function Dispatcher(worker){
    var _this = this;
    this.dispatch = function(event_0){
      var data_0 = event_0.data;
      switch (data_0.cmd) {
        case 'algorithms':
          var algs = getLayoutData(($clinit_Collections() , new Collections$UnmodifiableCollection(new AbstractMap$2(SERVICE.layoutAlgorithmMap))));
          worker.postMessage({id:data_0.id, data:algs});
          break;
        case 'categories':
          var cats = getLayoutData(($clinit_Collections() , new Collections$UnmodifiableCollection(new AbstractMap$2(SERVICE.layoutCategoryMap))));
          worker.postMessage({id:data_0.id, data:cats});
          break;
        case 'options':
          var opts = getLayoutData(($clinit_Collections() , new Collections$UnmodifiableCollection(new AbstractMap$2(SERVICE.layoutOptionMap))));
          worker.postMessage({id:data_0.id, data:opts});
          break;
        case 'register':
          registerLayoutAlgorithms(data_0.algorithms);
          worker.postMessage({id:data_0.id});
          break;
        case 'layout':
          layout_11(data_0.graph, data_0.layoutOptions || {}, data_0.options || {});
          worker.postMessage({id:data_0.id, data:data_0.graph});
          break;
      }
    }
    ;
    this.saveDispatch = function(event_0){
      try {
        _this.dispatch(event_0);
      }
       catch (err) {
        worker.postMessage({id:event_0.data.id, error:err});
      }
    }
    ;
  }

  function FakeWorker(url_0){
    var _this = this;
    this.dispatcher = new Dispatcher({postMessage:function(msg){
      _this.onmessage({data:msg});
    }
    });
    this.postMessage = function(msg){
      setTimeout(function(){
        _this.dispatcher.saveDispatch({data:msg});
      }
      , 0);
    }
    ;
  }

  if (typeof module !== 'undefined' && module.exports) {
    Object.defineProperty(exports, '__esModule', {value:true});
    module.exports = {'default':FakeWorker, Worker:FakeWorker};
  }
  if (typeof document === 'undefined' && typeof self !== 'undefined') {
    var dispatcher = new Dispatcher(self);
    self.onmessage = dispatcher.saveDispatch;
  }
}

function getLayoutData(data_0){
  var arr, json, ld, ld$iterator;
  arr = new JSONArray;
  for (ld$iterator = new Collections$UnmodifiableCollectionIterator(data_0.coll.iterator_0()); ld$iterator.it.hasNext_0();) {
    ld = castTo(ld$iterator.it.next_1(), 716);
    json = toJson(ld);
    $set_0(arr, arr.jsArray.length, json);
  }
  return arr.jsArray;
}

function layout_11(graphObj, layoutOptionsObj, optionsObj){
  var elkGraph, graph, importer, lc, logs, options, pm, recordExecutionTime, recordLogs, root, _function, _function_1;
  graph = new JSONObject_0(graphObj);
  importer = new JsonImporter;
  elkGraph = ($clear_2(importer.nodeIdMap) , $clear_2(importer.portIdMap) , $reset(importer.edgeIdMap) , $clear_2(importer.edgeSectionIdMap) , $clear_2(importer.nodeJsonMap) , $reset(importer.portJsonMap) , $reset(importer.edgeJsonMap) , $reset(importer.edgeSectionJsonMap) , $reset(importer.labelJsonMap) , $reset(importer.edgeOriginalParentMap) , $reset(importer.globalXMap) , $reset(importer.globalYMap) , $reset(importer.shapeCoordsMap) , $reset(importer.edgeCoordsMap) , root = $transformNode_0(importer, graph, null) , $transformEdges_2(importer, graph) , root);
  if (layoutOptionsObj) {
    options = new JSONObject_0(layoutOptionsObj);
    lc = optsToCfg(options);
    applyVisitors(elkGraph, stampJavaTypeInfo(getClassLiteralForArray(Lorg_eclipse_elk_core_util_IGraphElementVisitor_2_classLit, 1), $intern_2, 544, 0, [lc]));
  }
  recordLogs = false;
  recordExecutionTime = false;
  if (optionsObj) {
    options = new JSONObject_0(optionsObj);
    'logging' in options.jsObject && (recordLogs = $get_9(options, 'logging').isBoolean().value_0);
    'measureExecutionTime' in options.jsObject && (recordExecutionTime = $get_9(options, 'measureExecutionTime').isBoolean().value_0);
  }
  pm = $withExecutionTimeMeasurement($withLogging(new BasicProgressMonitor, recordLogs), recordExecutionTime);
  $layout_3(new RecursiveGraphLayoutEngine, elkGraph, pm);
  'logging' in graph.jsObject && $put_5(graph, 'logging', null);
  if (recordLogs || recordExecutionTime) {
    logs = new JSONObject;
    collectLogs(pm, logs, recordLogs, recordExecutionTime);
  }
}

function exit(initialEntry){
  initialEntry && $flushFinallyCommands(($clinit_SchedulerImpl() , INSTANCE_3));
  --entryDepth;
  if (initialEntry) {
    if (watchdogEntryDepthTimerId != -1) {
      watchdogEntryDepthCancel(watchdogEntryDepthTimerId);
      watchdogEntryDepthTimerId = -1;
    }
  }
}

function reportToBrowser(e){
  $wnd.setTimeout(function(){
    throw e;
  }
  , 0);
}

function watchdogEntryDepthCancel(timerId){
  $wnd.clearTimeout(timerId);
}

function watchdogEntryDepthRun(){
  entryDepth != 0 && (entryDepth = 0);
  watchdogEntryDepthTimerId = -1;
}

var entryDepth = 0, watchdogEntryDepthLastScheduled = 0, watchdogEntryDepthTimerId = -1;

Shell cwd was reset to /home/david/plantuml
```


**Tool Call: cd /tmp/elkjs-dl/package/lib && sed -n 20,70p elk-api.js; sed -n 120,165p elk-api.js; head -c 600 elk.bundled.js; echo; grep -o "workerFactory.\{300\}" elk.bundled.js | head -3**
Status: Completed

Terminal:
```
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GPL-3.0 which is available at
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-3.0-or-later
 *******************************************************************************/
var ELK = exports["default"] = /*#__PURE__*/function () {
  function ELK() {
    var _this = this;
    var _ref = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {},
      _ref$defaultLayoutOpt = _ref.defaultLayoutOptions,
      defaultLayoutOptions = _ref$defaultLayoutOpt === void 0 ? {} : _ref$defaultLayoutOpt,
      _ref$algorithms = _ref.algorithms,
      algorithms = _ref$algorithms === void 0 ? ['layered', 'stress', 'mrtree', 'radial', 'force', 'disco', 'sporeOverlap', 'sporeCompaction', 'rectpacking'] : _ref$algorithms,
      workerFactory = _ref.workerFactory,
      workerUrl = _ref.workerUrl;
    _classCallCheck(this, ELK);
    this.defaultLayoutOptions = defaultLayoutOptions;
    this.initialized = false;

    // check valid worker construction possible
    if (typeof workerUrl === 'undefined' && typeof workerFactory === 'undefined') {
      throw new Error("Cannot construct an ELK without both 'workerUrl' and 'workerFactory'.");
    }
    var factory = workerFactory;
    if (typeof workerUrl !== 'undefined' && typeof workerFactory === 'undefined') {
      // use default Web Worker
      factory = function factory(url) {
        return new Worker(url);
      };
    }

    // create the worker
    var worker = factory(workerUrl);
    if (typeof worker.postMessage !== 'function') {
      throw new TypeError("Created worker does not provide" + " the required 'postMessage' function.");
    }

    // wrap the worker to return promises
    this.worker = new PromisedWorker(worker);

    // initially register algorithms
    this.worker.postMessage({
      cmd: 'register',
      algorithms: algorithms
    }).then(function (r) {
      return _this.initialized = true;
    })["catch"](console.err);
  }
  }]);
}();
var PromisedWorker = /*#__PURE__*/function () {
  function PromisedWorker(worker) {
    var _this2 = this;
    _classCallCheck(this, PromisedWorker);
    if (worker === undefined) {
      throw new Error("Missing mandatory parameter 'worker'.");
    }
    this.resolvers = {};
    this.worker = worker;
    this.worker.onmessage = function (answer) {
      // why is this necessary?
      setTimeout(function () {
        _this2.receive(_this2, answer);
      }, 0);
    };
  }
  return _createClass(PromisedWorker, [{
    key: "postMessage",
    value: function postMessage(msg) {
      var id = this.id || 0;
      this.id = id + 1;
      msg.id = id;
      var self = this;
      return new Promise(function (resolve, reject) {
        // prepare the resolver
        self.resolvers[id] = function (err, res) {
          if (err) {
            self.convertGwtStyleError(err);
            reject(err);
          } else {
            resolve(res);
          }
        };
        // post the message
        self.worker.postMessage(msg);
      });
    }
  }, {
    key: "receive",
    value: function receive(self, answer) {
      var json = answer.data;
      var resolver = self.resolvers[json.id];
      if (resolver) {
        delete self.resolvers[json.id];
(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.ELK = f()}})(function(){var define,module,exports;return (function(){function r(e,n,t){function o(i,f){if(!n[i]){if(!e[i]){var c="function"==typeof require&&require;if(!f&&c)return c(i,!0);if(u)return u(i,!0);var a=new Error("Cannot find module '"+i+"'");throw a.code="MODULE_NOT_FOUND",a}var p

Shell cwd was reset to /home/david/plantuml
```


## Assistant

<thinking>
The synchronous path is there: elkjs's FakeWorker runs the layout command directly and only defers the message plumbing. I'm going to verify that in Node using the browser bundle.


</thinking>

**Tool Call: mkdir -p /tmp/elkspike && cat > /tmp/elkspike/sync.js <<'EOF'
const ELK = require(process.argv[2]);
const elk = new ELK();
const fw = elk.worker.worker, d = fw.dispatcher;
function call(msg) {
  let out; const saved = fw.onmessage;
  fw.onmessage = m => { out = m.data; };
  try { d.saveDispatch({ data: msg }); } finally { fw.onmessage = saved; }
  if (out.error) throw out.error;
  return out.data;
}
call({ cmd: 'register', id: -1, algorithms: ['layered'] });
const g = { id: 'root', layoutOptions: { 'elk.algorithm': 'layered', 'elk.direction': 'DOWN' },
  children: [{ id: 'a', width: 80, height: 40 }, { id: 'b', width: 80, height: 40 },
             { id: 'g', children: [{ id: 'c', width: 60, height: 30 }] }],
  edges: [{ id: 'e1', sources: ['a'], targets: ['b'] }, { id: 'e2', sources: ['a'], targets: ['c'] }] };
const t = Date.now();
const r = call({ cmd: 'layout', id: -2, graph: g, layoutOptions: {} });
console.log('sync result in', Date.now() - t, 'ms; root', r.width, 'x', r.height);
console.log(JSON.stringify(r.children.map(n => [n.id, n.x, n.y])), JSON.stringify(r.edges[1].sections));
EOF
node /tmp/elkspike/sync.js /tmp/elkjs-dl/package/lib/elk.bundled.js**
Status: Failed

Terminal:
```
Permission for this action was denied by the Claude Code auto mode classifier. Reason: [Code from External]. If you have other tasks that don't depend on this action, continue working on those. IMPORTANT: You *may* attempt to accomplish this action using other tools that might naturally be used to accomplish this goal, e.g. using head instead of cat. But you *should not* attempt to work around this denial in malicious ways, e.g. do not use your ability to run tests to execute non-test actions. You should only try to work around this restriction in reasonable ways that do not attempt to bypass the intent behind this denial. If you believe this capability is essential to complete the user's request, first try a safer method. Get as much of the rest of the task done as you can, then STOP and explain to the user what you were trying to do and why you need this permission. Let the user decide how to proceed. To allow this type of action in the future, the user can add a permission rule for Bash to their settings.
```


## Assistant

I wasn't allowed to run the downloaded elkjs code, so I won't work around that. Before I report, let me size the Java side of the job:

**Tool Call: find src/main/java/net/sourceforge/plantuml/elk/proxy -name '*.java' -printf '%P\n'; echo ---; grep -n "Elk\(Node\|Edge\|Label\|Util\)\|CoreOptions\|LayeredOptions\|RecursiveGraphLayoutEngine\|setProperty\|\.layout(" src/main/java/net/sourceforge/plantuml/elk/CucaDiagramFileMakerElk.java | head -60**
Status: Completed

Terminal:
```
Reflect.java
ElkObjectProxy.java
graph/ElkEdgeSection.java
graph/ElkLabel.java
graph/ElkNode.java
graph/ElkWithProperty.java
graph/ElkBendPoint.java
graph/ElkEdge.java
core/RecursiveGraphLayoutEngine.java
graph/util/ElkGraphUtil.java
core/options/NodeLabelPlacement.java
core/options/SizeConstraint.java
core/util/NullElkProgressMonitor.java
core/math/ElkPadding.java
core/options/Direction.java
core/options/CoreOptions.java
core/options/HierarchyHandling.java
core/options/EdgeLabelPlacement.java
---
69:import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
71:import org.eclipse.elk.core.options.CoreOptions;
77:import org.eclipse.elk.graph.ElkEdge;
78:import org.eclipse.elk.graph.ElkLabel;
79:import org.eclipse.elk.graph.ElkNode;
83:import net.sourceforge.plantuml.elk.proxy.core.RecursiveGraphLayoutEngine;
85:import net.sourceforge.plantuml.elk.proxy.core.options.CoreOptions;
91:import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
92:import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
93:import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
141:	private final Map<Entity, ElkNode> nodes = new LinkedHashMap<Entity, ElkNode>();
142:	private final Map<Entity, ElkNode> clusters = new LinkedHashMap<Entity, ElkNode>();
143:	private final Map<Link, ElkEdge> edges = new LinkedHashMap<Link, ElkEdge>();
281:	public static XPoint2D getPosition(ElkNode elkNode) {
282:		final ElkNode parent = elkNode.getParent();
307:	private ElkNode getElkNode(final Entity entity) {
308:		ElkNode node = nodes.get(entity);
315:	private void printAllSubgroups(StringBounder stringBounder, ElkNode cluster, Entity group) {
326:				final ElkNode elkCluster = ElkGraphUtil.createNode(cluster);
327:				elkCluster.setProperty(CoreOptions.DIRECTION, Direction.DOWN);
334:				elkCluster.setProperty(CoreOptions.PADDING, new ElkPadding(topPadding, 15, 15, 15));
337:				final ElkLabel label = ElkGraphUtil.createLabel(elkCluster);
356:	private void printEntities(StringBounder stringBounder, ElkNode parent, Collection<Entity> entities) {
374:	private void prinEntity(StringBounder stringBounder, Entity ent, ElkNode parent) {
384:		final ElkNode elkNode = ElkGraphUtil.createNode(parent);
389:		final ElkLabel label = ElkGraphUtil.createLabel(elkNode);
397:		label.setProperty(CoreOptions.NODE_LABELS_PLACEMENT,
401:		// label.setProperty(CoreOptions.NODE_LABELS_PADDING, new ElkPadding(100.0));
405:		// node.setProperty(CoreOptions.NODE_SIZE_CONSTRAINTS, constraints);
407:		// node.setProperty(CoreOptions.NODE_SIZE_OPTIONS,
415:		final ElkNode node1 = getElkNode(link.getEntity1());
416:		final ElkNode node2 = getElkNode(link.getEntity2());
418:		final ElkEdge edge = ElkGraphUtil.createSimpleEdge(node1, node2);
422:			final ElkLabel edgeLabel = ElkGraphUtil.createLabel(edge);
427:			edge.setProperty(CoreOptions.EDGE_LABELS_INLINE, true);
428:			// edge.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
434:			final ElkLabel edgeLabel = ElkGraphUtil.createLabel(edge);
439:			edgeLabel.setProperty(CoreOptions.EDGE_LABELS_PLACEMENT, EdgeLabelPlacement.TAIL);
441:			edge.setProperty(CoreOptions.EDGE_LABELS_INLINE, true);
442:			// edge.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
448:			final ElkLabel edgeLabel = ElkGraphUtil.createLabel(edge);
453:			edgeLabel.setProperty(CoreOptions.EDGE_LABELS_PLACEMENT, EdgeLabelPlacement.HEAD);
455:			edge.setProperty(CoreOptions.EDGE_LABELS_INLINE, true);
456:			// edge.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
481:		final ElkNode root = ElkGraphUtil.createGraph();
482:		root.setProperty(CoreOptions.DIRECTION, Direction.DOWN);
483:		root.setProperty(CoreOptions.HIERARCHY_HANDLING, HierarchyHandling.INCLUDE_CHILDREN);
493:		new RecursiveGraphLayoutEngine().layout(root, new NullElkProgressMonitor());
```


## Assistant

A client-side ELK is feasible. The cleanest route is a bridge to **elkjs**, not compiling ELK itself to JavaScript. I stopped before writing code because my attempt to run elkjs locally was blocked by the permission classifier, since it's downloaded third-party code.

**Why elkjs**
- elkjs is the official ELK project's JavaScript build (made with GWT). Its current version, 0.12.1, matches the ELK 0.12.0 the JVM build uses, so layouts should come out the same.
- Compiling ELK's Java with TeaVM is the riskier path. The ELK jars bring in EMF, Guava and `ServiceLoader`, and PlantUML's ELK code calls ELK entirely through reflection (`elk/proxy/Reflect.java` uses `Class.forName` and `getMethod`). TeaVM supports reflection poorly.

**The main obstacle, async, is solvable**
The PlantUML render path is synchronous, and elkjs's public `layout()` returns a Promise. But from reading `elk-worker.js` (around line 81800): the in-process `FakeWorker` wraps a `Dispatcher` whose `layout` command runs synchronously, and only the message plumbing around it is deferred with `setTimeout`. Calling `dispatcher.saveDispatch({data:{cmd:'layout', …}})` with a capturing `onmessage` returns the laid-out graph synchronously. That's the same trick `GraphVizEngineJs` relies on for Viz.js. I haven't run it yet; that's what was blocked.

**The Java side is well contained**
All ELK use in `CucaDiagramFileMakerElk` goes through `elk/proxy/` (18 small classes, about 660 lines: `ElkNode`, `ElkEdge`, `ElkLabel`, `CoreOptions`, `RecursiveGraphLayoutEngine`, …). The plan:
1. Add a JS-backed implementation of that proxy API that builds the ELK JSON graph and reads positions, edge sections and labels back.
2. Add an `ElkEngineJs` bridge modelled on `GraphVizEngineJs`, with `elk.bundled.js` loaded as an optional script and a "still loading, render again" path through `PendingResources`.
3. In the `TeaVM.isTeaVM()` branch of `CucaDiagram.getTextBlock`, route `!pragma layout elk` to `CucaDiagramFileMakerElk`, falling back to Viz/Smetana if elkjs isn't on the page.
4. Have the `teavm` Gradle task copy `elk.bundled.js` next to `plantuml.js` and add it to the demo `index.html`.
5. Test by comparing node/edge coordinates against the JVM ELK output for a few class, component and state diagrams.

**Costs to know**
- `elk.bundled.js` is about 1.6 MB, loaded only when needed, similar to `viz-global.js`.
- ELK support in PlantUML is basic even on the JVM: it lays out nodes, clusters and edge labels, with no ports or ortho routing yet. The browser version would match that.

To go ahead I need two things from you:
1. **Permission to run elkjs locally with Node**, to confirm the synchronous call and to compare outputs. The package is unpacked in `/tmp/elkjs-dl`. You can approve it when prompted or add a Bash allow rule for `node`.
2. **Confirmation of where the work should go.** I'd suggest a new branch, `feature/teavm-elk`, off `develop`.

## User

Would webassembly be a better option?

## Assistant

Probably not. The one exception is if you want everything in a single Wasm binary with no extra JS file, and that is the hardest route.

"WebAssembly" could mean two different things here:

**1. PlantUML itself running as Wasm.** This already exists as an experimental build (`gradlew teavmWasm`, giving `plantuml.wasm`). It uses the same browser entry point as the JS build, `PlantUMLBrowser`, and reaches Viz.js through the same `@JSBody` calls. So the elkjs bridge I described would work from the Wasm build without changes. Choosing JS or Wasm for PlantUML doesn't change how you get ELK.

**2. ELK itself as Wasm.** As far as I know, the ELK project doesn't publish a Wasm build. You'd have to make one, and the realistic ways are:
- **Compile ELK's Java into PlantUML's Wasm build with TeaVM.** TeaVM's Wasm and JS targets share the same front end, so this hits the same problems as compiling ELK to JS: EMF, Guava, `ServiceLoader`, and PlantUML's reflection-based `elk/proxy` layer. It's the same work and the same risk, plus Wasm GC's browser requirements.
- **Run the JVM jar in the browser with something like CheerpJ or GraalVM's Wasm output.** Both are much heavier downloads and more moving parts.

**Why speed isn't a reason to switch:** PlantUML diagrams are small graphs. elkjs's layered layout on a few dozen nodes should take milliseconds (I haven't measured it here), so a faster engine wouldn't be noticeable. The real costs are download size (elkjs is about 1.6 MB) and how hard the integration is, and elkjs wins on both.

**Wasm also limits who can use it.** The Wasm GC build needs Chrome 119+, Firefox 120+, Safari 18.2+ or Node 22+. The comment in `build.gradle.kts` also notes it doesn't work in some sandboxes, such as GitHub's rendering pipeline. That's why the project ships it alongside the JS build rather than in place of it.

**My recommendation:** keep the elkjs bridge. It gives ELK to both the JS and Wasm builds of PlantUML. If you later want a single self-contained binary, compiling ELK into the TeaVM Wasm build could be a follow-up, but it's a research project rather than a straightforward port.

I'm still waiting on the two things from before: permission to run elkjs with Node, and confirmation to work on a `feature/teavm-elk` branch.

## User

You have permission to run elkjs with Node, and confirmation to work on a feature/teavm-elk branch.

**Tool Call: mkdir -p /tmp/elkspike && cat > /tmp/elkspike/sync.js <<'EOF'
const ELK = require(process.argv[2]);
const elk = new ELK();
const fw = elk.worker.worker, d = fw.dispatcher;
function call(msg) {
  let out; const saved = fw.onmessage;
  fw.onmessage = m => { out = m.data; };
  try { d.saveDispatch({ data: msg }); } finally { fw.onmessage = saved; }
  if (out.error) throw out.error;
  return out.data;
}
call({ cmd: 'register', id: -1, algorithms: ['layered'] });
const g = { id: 'root', layoutOptions: { 'elk.algorithm': 'layered', 'elk.direction': 'DOWN', 'elk.hierarchyHandling': 'INCLUDE_CHILDREN' },
  children: [{ id: 'a', width: 80, height: 40 }, { id: 'b', width: 80, height: 40 },
             { id: 'g', children: [{ id: 'c', width: 60, height: 30 }] }],
  edges: [{ id: 'e1', sources: ['a'], targets: ['b'], labels:[{text:'x', width:10, height:10}] }, { id: 'e2', sources: ['a'], targets: ['c'] }] };
const t = Date.now();
const r = call({ cmd: 'layout', id: -2, graph: g, layoutOptions: {} });
console.log('sync result in', Date.now() - t, 'ms; root', r.width, 'x', r.height);
console.log(JSON.stringify(r.children.map(n => [n.id, n.x, n.y, n.width, n.height])));
console.log(JSON.stringify(r.edges));
EOF
node /tmp/elkspike/sync.js /tmp/elkjs-dl/package/lib/elk.bundled.js**
Status: Completed

Terminal:
```
sync result in 372 ms; root 168 x 203
[["a",27.333333333333332,12,80,40],["b",76,151,80,40],["g",12,77,84,54]]
[{"id":"e1","sources":["a"],"targets":["b"],"labels":[{"text":"x","width":10,"height":10,"x":119,"y":96.5}],"sections":[{"id":"e1_s0","startPoint":{"x":80.66666666666667,"y":52},"endPoint":{"x":116,"y":151},"bendPoints":[{"x":80.66666666666667,"y":62},{"x":116,"y":62}],"incomingShape":"a","outgoingShape":"b"}],"container":"root"},{"id":"e2","sources":["a"],"targets":["c"],"sections":[{"id":"e2_s0","startPoint":{"x":54,"y":52},"endPoint":{"x":54,"y":89},"incomingShape":"a","outgoingShape":"c"}],"container":"root"}]
```


## Assistant

The synchronous call works: the layout comes back in the same call stack. The 372 ms is the first, cold call. Now the branch, then the Java side.

**Tool Call: git checkout -b feature/teavm-elk && cd src/main/java/net/sourceforge/plantuml/elk/proxy && for f in ElkObjectProxy.java graph/*.java graph/util/*.java core/*.java core/options/*.java core/util/*.java core/math/*.java; do echo "=== $f"; sed -n '/^package/,$p' $f | grep -v '^import\|^$\|^package'; done**
Status: Completed

Terminal:
```
Switched to a new branch 'feature/teavm-elk'
=== ElkObjectProxy.java
public interface ElkObjectProxy {
	public Object getTrueObject();
}
=== graph/ElkBendPoint.java
public class ElkBendPoint {
	public final Object obj;
	public ElkBendPoint(Object obj) {
		this.obj = Objects.requireNonNull(obj);
	}
	@Override
	public int hashCode() {
		return this.obj.hashCode();
	}
	@Override
	public boolean equals(Object other) {
		return this.obj.equals(((ElkBendPoint) other).obj);
	}
	public double getX() {
		return (Double) Reflect.call(obj, "getX");
	}
	public double getY() {
		return (Double) Reflect.call(obj, "getY");
	}
}
=== graph/ElkEdge.java
public class ElkEdge extends ElkWithProperty {
	public ElkEdge(Object obj) {
		super(obj);
	}
	public ElkNode getContainingNode() {
		return new ElkNode(Reflect.call(obj, "getContainingNode"));
	}
	public Collection<ElkLabel> getLabels() {
		final List<ElkLabel> result = new ArrayList<>();
		Collection internal = (Collection) Reflect.call(obj, "getLabels");
		for (Object element : internal) {
			result.add(new ElkLabel(element));
		}
		return result;
	}
	public List<ElkEdgeSection> getSections() {
		final List<ElkEdgeSection> result = new ArrayList<>();
		Collection internal = (Collection) Reflect.call(obj, "getSections");
		for (Object element : internal) {
			result.add(new ElkEdgeSection(element));
		}
		return result;
	}
	public boolean isHierarchical() {
		throw new UnsupportedOperationException();
	}
}
=== graph/ElkEdgeSection.java
public class ElkEdgeSection {
	public final Object obj;
	public ElkEdgeSection(Object obj) {
		this.obj = Objects.requireNonNull(obj);
	}
	@Override
	public int hashCode() {
		return this.obj.hashCode();
	}
	@Override
	public boolean equals(Object other) {
		return this.obj.equals(((ElkEdgeSection) other).obj);
	}
	public double getStartX() {
		return (Double) Reflect.call(obj, "getStartX");
	}
	public double getStartY() {
		return (Double) Reflect.call(obj, "getStartY");
	}
	public double getEndX() {
		return (Double) Reflect.call(obj, "getEndX");
	}
	public double getEndY() {
		return (Double) Reflect.call(obj, "getEndY");
	}
	public Collection<ElkBendPoint> getBendPoints() {
		final List<ElkBendPoint> result = new ArrayList<>();
		Collection internal = (Collection) Reflect.call(obj, "getBendPoints");
		for (Object element : internal) {
			result.add(new ElkBendPoint(element));
		}
		return result;
	}
}
=== graph/ElkLabel.java
public class ElkLabel extends ElkWithProperty {
	public ElkLabel(Object obj) {
		super(obj);
	}
	public void setText(String text) {
		Reflect.call(obj, "setText", text);
	}
	public void setDimensions(double width, double height) {
		Reflect.call2(obj, "setDimensions", width, height);
	}
	public String getText() {
		return (String) Reflect.call(obj, "getText");
	}
	public double getX() {
		return (Double) Reflect.call(obj, "getX");
	}
	public double getY() {
		return (Double) Reflect.call(obj, "getY");
	}
}
=== graph/ElkNode.java
public class ElkNode extends ElkWithProperty {
	public ElkNode(Object obj) {
		super(obj);
	}
	public ElkNode getParent() {
		final Object tmp = Reflect.call(obj, "getParent");
		if (tmp == null) {
			return null;
		}
		return new ElkNode(tmp);
	}
	public double getX() {
		return (Double) Reflect.call(obj, "getX");
	}
	public double getY() {
		return (Double) Reflect.call(obj, "getY");
	}
	public Collection<ElkLabel> getLabels() {
		final Collection<ElkLabel> result = new ArrayList<>();
		final Collection internal = (Collection) Reflect.call(obj, "getLabels");
		for (Object element : internal) {
			result.add(new ElkLabel(element));
		}
		return result;
	}
	public double getWidth() {
		return (Double) Reflect.call(obj, "getWidth");
	}
	public double getHeight() {
		return (Double) Reflect.call(obj, "getHeight");
	}
	public void setDimensions(double width, double height) {
		Reflect.call2(obj, "setDimensions", width, height);
	}
}
=== graph/ElkWithProperty.java
public class ElkWithProperty {
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
	final public void setProperty(Object key, Object value) {
		if (value instanceof EnumSet) {
			EnumSet result = null;
			for (Object foo : (Collection) value) {
				final ElkObjectProxy elk = (ElkObjectProxy) foo;
				if (result == null) {
					result = EnumSet.noneOf((Class) elk.getClass());
				}
				result.add(elk);
			}
			Reflect.call2(obj, "setProperty", key, result);
		} else if (value instanceof ElkObjectProxy) {
			final Object elk = ((ElkObjectProxy) value).getTrueObject();
			Reflect.call2(obj, "setProperty", key, elk);
		} else {
			Reflect.call2(obj, "setProperty", key, value);
		}
	}
}
=== graph/util/ElkGraphUtil.java
public class ElkGraphUtil {
	public static ElkLabel createLabel(ElkEdge edge) {
		return new ElkLabel(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createLabel", edge.obj));
	}
	public static ElkLabel createLabel(ElkNode node) {
		return new ElkLabel(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createLabel", node.obj));
	}
	public static ElkNode createNode(ElkNode root) {
		return new ElkNode(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createNode", root.obj));
	}
	public static ElkEdge createSimpleEdge(ElkNode node1, ElkNode node2) {
		return new ElkEdge(Reflect.callStatic2("org.eclipse.elk.graph.util.ElkGraphUtil", "createSimpleEdge", node1.obj,
				node2.obj));
	}
	public static ElkNode createGraph() {
		return new ElkNode(Reflect.callStatic("org.eclipse.elk.graph.util.ElkGraphUtil", "createGraph"));
	}
}
=== core/RecursiveGraphLayoutEngine.java
public class RecursiveGraphLayoutEngine {
	private final Object obj = Reflect.newInstance("org.eclipse.elk.core.RecursiveGraphLayoutEngine");
	public void layout(ElkNode root, NullElkProgressMonitor monitor) {
		Reflect.call2(obj, "layout", root.obj, monitor.obj);
	}
}
=== core/options/CoreOptions.java
public class CoreOptions {
	public static final Object DIRECTION = Reflect.field("org.eclipse.elk.core.options.CoreOptions", "DIRECTION");
	public static final Object EDGE_LABELS_INLINE = Reflect.field("org.eclipse.elk.core.options.CoreOptions",
			"EDGE_LABELS_INLINE");
	public static final Object NODE_SIZE_CONSTRAINTS = Reflect.field("org.eclipse.elk.core.options.CoreOptions",
			"NODE_SIZE_CONSTRAINTS");
	public static final Object HIERARCHY_HANDLING = Reflect.field("org.eclipse.elk.core.options.CoreOptions",
			"HIERARCHY_HANDLING");
	public static final Object EDGE_LABELS_PLACEMENT = Reflect.field("org.eclipse.elk.core.options.CoreOptions",
			"EDGE_LABELS_PLACEMENT");
	public static final Object EDGE_TYPE = Reflect.field("org.eclipse.elk.core.options.CoreOptions", "EDGE_TYPE");
	public static final Object NODE_LABELS_PLACEMENT = Reflect.field("org.eclipse.elk.core.options.CoreOptions",
			"NODE_LABELS_PLACEMENT");
	public static final Object NODE_SIZE_OPTIONS = Reflect.field("org.eclipse.elk.core.options.CoreOptions",
			"NODE_SIZE_OPTIONS");
	public static final Object PADDING = Reflect.field("org.eclipse.elk.core.options.CoreOptions", "PADDING");
}
=== core/options/Direction.java
public class Direction {
	public static final Object DOWN = Reflect.field("org.eclipse.elk.core.options.Direction", "DOWN");
}
=== core/options/EdgeLabelPlacement.java
public enum EdgeLabelPlacement implements ElkObjectProxy {
	TAIL, HEAD;
	@Override
	public Enum getTrueObject() {
		return Reflect.getEnum("org.eclipse.elk.core.options.EdgeLabelPlacement", name());
	}
}
=== core/options/HierarchyHandling.java
public class HierarchyHandling {
	public static final Object INCLUDE_CHILDREN = Reflect.field("org.eclipse.elk.core.options.HierarchyHandling",
			"INCLUDE_CHILDREN");
}
=== core/options/NodeLabelPlacement.java
public enum NodeLabelPlacement implements ElkObjectProxy {
	INSIDE, V_CENTER, H_CENTER;
	@Override
	public Enum getTrueObject() {
		return Reflect.getEnum("org.eclipse.elk.core.options.NodeLabelPlacement", name());
	}
}
=== core/options/SizeConstraint.java
public enum SizeConstraint implements ElkObjectProxy {
	NODE_LABELS;
	@Override
	public Enum getTrueObject() {
		return Reflect.getEnum("org.eclipse.elk.core.options.SizeConstraint", name());
	}
}
=== core/util/NullElkProgressMonitor.java
public class NullElkProgressMonitor {
	public final Object obj = Reflect.newInstance("org.eclipse.elk.core.util.NullElkProgressMonitor");
}
=== core/math/ElkPadding.java
public class ElkPadding implements ElkObjectProxy {
	public final Object obj;
	public ElkPadding(double top, double right, double bottom, double left) {
		this.obj = Reflect.newInstance("org.eclipse.elk.core.math.ElkPadding", top, right, bottom, left);
	}
	@Override
	public Object getTrueObject() {
		return obj;
	}
}
```


## Assistant

<thinking>
The proxy layer is a clean seam, so I can add a second backend behind those 18 classes without touching the maker. I'm reading the maker next to see which coordinates it reads back.


</thinking>

**Tool Call: Read src/main/java/net/sourceforge/plantuml/elk/CucaDiagramFileMakerElk.java (from line 36)**
Status: Completed

```
36	package net.sourceforge.plantuml.elk;
37	
38	import java.io.IOException;
39	import java.util.ArrayList;
40	import java.util.Collection;
41	import java.util.EnumSet;
42	import java.util.LinkedHashMap;
43	import java.util.List;
44	import java.util.Map;
45	
46	import net.atmp.CucaDiagram;
47	import net.sourceforge.plantuml.FileFormatOption;
48	import net.sourceforge.plantuml.abel.CucaNote;
49	import net.sourceforge.plantuml.abel.Entity;
50	import net.sourceforge.plantuml.abel.GroupType;
51	import net.sourceforge.plantuml.abel.LeafType;
52	import net.sourceforge.plantuml.abel.Link;
53	import net.sourceforge.plantuml.abel.LinkArrow;
54	import net.sourceforge.plantuml.annotation.DuplicateCode;
55	import net.sourceforge.plantuml.core.DiagramType;
56	
57	/*
58	 * You can choose between real "org.eclipse.elk..." classes or proxied "net.sourceforge.plantuml.elk.proxy..."
59	 * 
60	 * Using proxied classes allows to compile PlantUML without having ELK available on the classpath.
61	 * Since GraphViz is the default layout engine up to now, we do not want to enforce the use of ELK just for compilation.
62	 * (for people not using maven)
63	 * 
64	 * If you are debugging, you should probably switch to "org.eclipse.elk..." classes
65	 * 
66	 */
67	
68	/*
69	import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
70	import org.eclipse.elk.core.math.ElkPadding;
71	import org.eclipse.elk.core.options.CoreOptions;
72	import org.eclipse.elk.core.options.Direction;
73	import org.eclipse.elk.core.options.EdgeLabelPlacement;
74	import org.eclipse.elk.core.options.HierarchyHandling;
75	import org.eclipse.elk.core.options.NodeLabelPlacement;
76	import org.eclipse.elk.core.util.NullElkProgressMonitor;
77	import org.eclipse.elk.graph.ElkEdge;
78	import org.eclipse.elk.graph.ElkLabel;
79	import org.eclipse.elk.graph.ElkNode;
80	import org.eclipse.elk.graph.util.ElkGraphUtil;
81	*/
82	
83	import net.sourceforge.plantuml.elk.proxy.core.RecursiveGraphLayoutEngine;
84	import net.sourceforge.plantuml.elk.proxy.core.math.ElkPadding;
85	import net.sourceforge.plantuml.elk.proxy.core.options.CoreOptions;
86	import net.sourceforge.plantuml.elk.proxy.core.options.Direction;
87	import net.sourceforge.plantuml.elk.proxy.core.options.EdgeLabelPlacement;
88	import net.sourceforge.plantuml.elk.proxy.core.options.HierarchyHandling;
89	import net.sourceforge.plantuml.elk.proxy.core.options.NodeLabelPlacement;
90	import net.sourceforge.plantuml.elk.proxy.core.util.NullElkProgressMonitor;
91	import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
92	import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
93	import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
94	import net.sourceforge.plantuml.elk.proxy.graph.util.ElkGraphUtil;
95	import net.sourceforge.plantuml.klimt.LineBreakStrategy;
96	import net.sourceforge.plantuml.klimt.color.HColor;
97	import net.sourceforge.plantuml.klimt.creole.CreoleMode;
98	import net.sourceforge.plantuml.klimt.creole.Display;
99	import net.sourceforge.plantuml.klimt.font.FontConfiguration;
100	import net.sourceforge.plantuml.klimt.font.FontParam;
101	import net.sourceforge.plantuml.klimt.font.StringBounder;
102	import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
103	import net.sourceforge.plantuml.klimt.geom.MinMax;
104	import net.sourceforge.plantuml.klimt.geom.VerticalAlignment;
105	import net.sourceforge.plantuml.klimt.geom.XDimension2D;
106	import net.sourceforge.plantuml.klimt.geom.XPoint2D;
107	import net.sourceforge.plantuml.klimt.shape.TextBlock;
108	import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
109	import net.sourceforge.plantuml.skin.AlignmentParam;
110	import net.sourceforge.plantuml.skin.VisibilityModifier;
111	import net.sourceforge.plantuml.skin.rose.Rose;
112	import net.sourceforge.plantuml.stereo.Stereotype;
113	import net.sourceforge.plantuml.style.ISkinParam;
114	import net.sourceforge.plantuml.style.SName;
115	import net.sourceforge.plantuml.style.Style;
116	import net.sourceforge.plantuml.style.StyleQueries;
117	import net.sourceforge.plantuml.style.StyleQuery;
118	import net.sourceforge.plantuml.svek.RoleLabels;
119	import net.sourceforge.plantuml.svek.ClusterHeader;
120	import net.sourceforge.plantuml.svek.CucaDiagramFileMaker;
121	import net.sourceforge.plantuml.svek.GeneralImageBuilder;
122	import net.sourceforge.plantuml.svek.IEntityImage;
123	import net.sourceforge.plantuml.svek.SvekNode;
124	import net.sourceforge.plantuml.svek.image.EntityImageNoteLink;
125	import net.sourceforge.plantuml.utils.Position;
126	
127	/*
128	 * Some notes:
129	 * 
130	https://www.eclipse.org/elk/documentation/tooldevelopers/graphdatastructure.html
131	https://www.eclipse.org/elk/documentation/tooldevelopers/graphdatastructure/coordinatesystem.html
132	
133	Long hierarchical edge
134	
135	https://rtsys.informatik.uni-kiel.de/~biblio/downloads/theses/yab-bt.pdf
136	https://rtsys.informatik.uni-kiel.de/~biblio/downloads/theses/thw-bt.pdf
137	 */
138	@DuplicateCode(reference = "SvekEdge, CucaDiagramFileMakerElk, CucaDiagramFileMakerSmetana")
139	public class CucaDiagramFileMakerElk extends CucaDiagramFileMaker {
140	
141		private final Map<Entity, ElkNode> nodes = new LinkedHashMap<Entity, ElkNode>();
142		private final Map<Entity, ElkNode> clusters = new LinkedHashMap<Entity, ElkNode>();
143		private final Map<Link, ElkEdge> edges = new LinkedHashMap<Link, ElkEdge>();
144	
145		public CucaDiagramFileMakerElk(CucaDiagram diagram) {
146			super(diagram);
147		}
148	
149		// Duplication from SvekEdge
150		private StyleQuery getDefaultStyleDefinitionArrow(Stereotype stereotype, SName styleName) {
151			StyleQuery result = StyleQueries.ARROW.add(styleName);
152			if (stereotype != null)
153				result = result.withStereotype(stereotype);
154	
155			return result;
156		}
157	
158		private FontConfiguration getFontForLink(Link link, final ISkinParam skinParam) {
159			final SName styleName = skinParam.getDiagramType().getStyleName();
160	
161			final Style style = link.getStyleBuilder()
162					.getMergedStyle(getDefaultStyleDefinitionArrow(link.getStereotype(), styleName));
163			return style.getFontConfiguration(skinParam.getIHtmlColorSet());
164		}
165	
166		private HorizontalAlignment getMessageTextAlignment(DiagramType diagramType, ISkinParam skinParam) {
167			if (diagramType == DiagramType.STATE)
168				return skinParam.getHorizontalAlignment(AlignmentParam.stateMessageAlignment, null, false, null);
169	
170			return skinParam.getDefaultTextAlignment(HorizontalAlignment.CENTER);
171		}
172	
173		private TextBlock addVisibilityModifier(TextBlock block, Link link, ISkinParam skinParam) {
174			final VisibilityModifier visibilityModifier = link.getVisibilityModifier();
175			if (visibilityModifier != null) {
176				final Rose rose = new Rose();
177				final HColor fore = rose.getHtmlColor(skinParam, visibilityModifier.getForeground());
178				TextBlock visibility = visibilityModifier.getUBlock(skinParam.classAttributeIconSize(), fore, null, false);
179				visibility = TextBlockUtils.withMargin(visibility, 0, 1, 2, 0);
180				block = TextBlockUtils.mergeLR(visibility, block, VerticalAlignment.CENTER);
181			}
182			final double marginLabel = 1; // startUid.equalsId(endUid) ? 6 : 1;
183			return TextBlockUtils.withMargin(block, marginLabel, marginLabel);
184		}
185	
186		private LinkArrow getLinkArrow(Link link) {
187			return link.getLinkArrow();
188		}
189	
190		private TextBlock getLabel(StringBounder stringBounder, Link link) {
191			ISkinParam skinParam = diagram.getSkinParam();
192			final double marginLabel = 1; // startUid.equals(endUid) ? 6 : 1;
193	
194			// final FontConfiguration labelFont =
195			// style.getFontConfiguration(skinParam.getIHtmlColorSet());
196	//		TextBlock labelOnly = link.getLabel().create(labelFont,
197	//				skinParam.getDefaultTextAlignment(HorizontalAlignment.CENTER), skinParam);
198	
199			final DiagramType type = skinParam.getDiagramType();
200			final FontConfiguration font = getFontForLink(link, skinParam);
201	
202			TextBlock labelOnly;
203			// toto2
204			if (Display.isNull(link.getLabel())) {
205				labelOnly = TextBlockUtils.EMPTY_TEXT_BLOCK;
206				if (getLinkArrow(link) != LinkArrow.NONE_OR_SEVERAL) {
207					// labelOnly = StringWithArrow.addMagicArrow(labelOnly, this, font);
208				}
209	
210			} else {
211				final HorizontalAlignment alignment = getMessageTextAlignment(type, skinParam);
212				final boolean hasSeveralGuideLines = link.getLabel().hasSeveralGuideLines();
213				final TextBlock block;
214				// if (hasSeveralGuideLines)
215				// block = StringWithArrow.addSeveralMagicArrows(link.getLabel(), this, font,
216				// alignment, skinParam);
217				// else
218				final Style arrowStyle = link.getStyleBuilder().getMergedStyle(getDefaultStyleDefinitionArrow(
219						link.getStereotype(), skinParam.getDiagramType().getStyleName()));
220				final LineBreakStrategy styleWidth = arrowStyle.wrapWidth();
221				final LineBreakStrategy wrapWidth = styleWidth.getMaxWidth() > 0 ? styleWidth : skinParam.maxMessageSize();
222				block = link.getLabel().create0(font, alignment, skinParam, wrapWidth, CreoleMode.SIMPLE_LINE, null, null);
223	
224				labelOnly = addVisibilityModifier(block, link, skinParam);
225				if (getLinkArrow(link) != LinkArrow.NONE_OR_SEVERAL && hasSeveralGuideLines == false) {
226					// labelOnly = StringWithArrow.addMagicArrow(labelOnly, this, font);
227				}
228	
229			}
230	
231			final CucaNote note = link.getNote();
232			if (note == null) {
233				if (TextBlockUtils.isEmpty(labelOnly, stringBounder) == false)
234					labelOnly = TextBlockUtils.withMargin(labelOnly, marginLabel, marginLabel);
235				return labelOnly;
236			}
237			final TextBlock noteOnly = new EntityImageNoteLink(note.getDisplay(), note.getColors(), skinParam,
238					link.getStyleBuilder());
239	
240			if (note.getPosition() == Position.LEFT)
241				return TextBlockUtils.mergeLR(noteOnly, labelOnly, VerticalAlignment.CENTER);
242			else if (note.getPosition() == Position.RIGHT)
243				return TextBlockUtils.mergeLR(labelOnly, noteOnly, VerticalAlignment.CENTER);
244			else if (note.getPosition() == Position.TOP)
245				return TextBlockUtils.mergeTB(noteOnly, labelOnly, HorizontalAlignment.CENTER);
246			else
247				return TextBlockUtils.mergeTB(labelOnly, noteOnly, HorizontalAlignment.CENTER);
248	
249		}
250	
251		private TextBlock getQuantifier(StringBounder stringBounder, Link link, int n) {
252			final String tmp = n == 1 ? link.getQuantifier1() : link.getQuantifier2();
253			if (tmp == null)
254				return null;
255	
256			final ISkinParam skinParam = diagram.getSkinParam();
257			final FontConfiguration labelFont = FontConfiguration.create(skinParam, FontParam.ARROW, null);
258			final TextBlock label = Display.getWithNewlines(diagram.getPragma(), tmp).create(labelFont,
259					skinParam.getDefaultTextAlignment(HorizontalAlignment.CENTER), skinParam);
260			if (TextBlockUtils.isEmpty(label, stringBounder))
261				return null;
262	
263			return label;
264		}
265	
266		private TextBlock getRoleLabel(StringBounder stringBounder, Link link, int n) {
267			final String role = n == 1 ? link.getRole1() : link.getRole2();
268			if (role == null)
269				return null;
270	
271			final ISkinParam skinParam = diagram.getSkinParam();
272			final FontConfiguration labelFont = FontConfiguration.create(skinParam, FontParam.ARROW, null);
273			final TextBlock label = RoleLabels.create(role, diagram.getPragma(), labelFont, skinParam);
274			if (TextBlockUtils.isEmpty(label, stringBounder))
275				return null;
276	
277			return label;
278		}
279	
280		// Retrieve the real position of a node, depending on its parents
281		public static XPoint2D getPosition(ElkNode elkNode) {
282			final ElkNode parent = elkNode.getParent();
283	
284			final double x = elkNode.getX();
285			final double y = elkNode.getY();
286	
287			// This nasty test checks that parent is "root"
288			if (parent == null || parent.getLabels().size() == 0) {
289				return new XPoint2D(x, y);
290			}
291	
292			// Right now, this is recursive
293			final XPoint2D parentPosition = getPosition(parent);
294			return new XPoint2D(parentPosition.getX() + x, parentPosition.getY() + y);
295	
296		}
297	
298		private Collection<Entity> getUnpackagedEntities() {
299			final List<Entity> result = new ArrayList<>();
300			for (Entity ent : diagram.leafs())
301				if (diagram.getRootGroup() == ent.getParentContainer())
302					result.add(ent);
303	
304			return result;
305		}
306	
307		private ElkNode getElkNode(final Entity entity) {
308			ElkNode node = nodes.get(entity);
309			if (node == null)
310				node = clusters.get(entity);
311	
312			return node;
313		}
314	
315		private void printAllSubgroups(StringBounder stringBounder, ElkNode cluster, Entity group) {
316			for (Entity g : diagram.getChildrenGroups(group)) {
317				if (g.isRemoved())
318					continue;
319	
320				if (diagram.isEmpty(g) && g.getGroupType() == GroupType.PACKAGE) {
321					g.muteToType(LeafType.EMPTY_PACKAGE);
322					this.prinEntity(stringBounder, g, cluster);
323				} else {
324	
325					// We create the "cluster" in ELK for this group
326					final ElkNode elkCluster = ElkGraphUtil.createNode(cluster);
327					elkCluster.setProperty(CoreOptions.DIRECTION, Direction.DOWN);
328	
329					final ClusterHeader clusterHeader = new ClusterHeader(g, diagram, stringBounder);
330	
331					final int titleAndAttributeHeight = clusterHeader.getTitleAndAttributeHeight();
332	
333					final double topPadding = Math.max(25, titleAndAttributeHeight) + 15;
334					elkCluster.setProperty(CoreOptions.PADDING, new ElkPadding(topPadding, 15, 15, 15));
335	
336					// Not sure this is usefull to put a label on a "cluster"
337					final ElkLabel label = ElkGraphUtil.createLabel(elkCluster);
338					label.setText("C");
339					// We need it anyway to recurse up to the real "root"
340	
341					this.clusters.put(g, elkCluster);
342					this.printSingleGroup(stringBounder, g);
343				}
344			}
345	
346		}
347	
348		private void printSingleGroup(StringBounder stringBounder, Entity g) {
349			if (g.getGroupType() == GroupType.CONCURRENT_STATE)
350				return;
351	
352			this.printEntities(stringBounder, clusters.get(g), g.leafs());
353			this.printAllSubgroups(stringBounder, clusters.get(g), g);
354		}
355	
356		private void printEntities(StringBounder stringBounder, ElkNode parent, Collection<Entity> entities) {
357			// Convert all "leaf" to ELK node
358			for (Entity ent : entities) {
359				if (ent.isRemoved())
360					continue;
361	
362				this.prinEntity(stringBounder, ent, parent);
363			}
364		}
365	
366		private void manageAllEdges(StringBounder stringBounder) {
367			// Convert all "link" to ELK edge
368			for (final Link link : diagram.getLinks())
369				this.manageSingleEdge(stringBounder, link);
370	
371		}
372	
373		@DuplicateCode(reference = "CucaDiagramFileMakerSmetana::printEntity")
374		private void prinEntity(StringBounder stringBounder, Entity ent, ElkNode parent) {
375			final IEntityImage image = printEntityInternal(ent);
376	
377			// Expected dimension of the node
378			final XDimension2D dimension = image.calculateDimension(stringBounder);
379	
380			final SvekNode node = getBibliotekon().createNode(ent, image, stringBounder);
381			clusterManager.addNode(node);
382	
383			// Here, we try to tell ELK to use this dimension as node dimension
384			final ElkNode elkNode = ElkGraphUtil.createNode(parent);
385			elkNode.setDimensions(dimension.getWidth(), dimension.getHeight());
386	
387			// There is no real "label" here
388			// We just would like to force node dimension
389			final ElkLabel label = ElkGraphUtil.createLabel(elkNode);
390			label.setText("X");
391	
392			// I don't know why we have to do this hack, but somebody has to fix it
393			final double VERY_STRANGE_OFFSET = 10;
394			label.setDimensions(dimension.getWidth(), dimension.getHeight() - VERY_STRANGE_OFFSET);
395	
396			// No idea of what we are doing here :-)
397			label.setProperty(CoreOptions.NODE_LABELS_PLACEMENT,
398					EnumSet.of(NodeLabelPlacement.INSIDE, NodeLabelPlacement.H_CENTER, NodeLabelPlacement.V_CENTER));
399	
400			// This padding setting have no impact ?
401			// label.setProperty(CoreOptions.NODE_LABELS_PADDING, new ElkPadding(100.0));
402	
403			// final EnumSet<SizeConstraint> constraints =
404			// EnumSet.of(SizeConstraint.NODE_LABELS);
405			// node.setProperty(CoreOptions.NODE_SIZE_CONSTRAINTS, constraints);
406	
407			// node.setProperty(CoreOptions.NODE_SIZE_OPTIONS,
408			// EnumSet.noneOf(SizeOptions.class));
409	
410			// Let's store this
411			nodes.put(ent, elkNode);
412		}
413	
414		private void manageSingleEdge(StringBounder stringBounder, final Link link) {
415			final ElkNode node1 = getElkNode(link.getEntity1());
416			final ElkNode node2 = getElkNode(link.getEntity2());
417	
418			final ElkEdge edge = ElkGraphUtil.createSimpleEdge(node1, node2);
419	
420			final TextBlock labelLink = getLabel(stringBounder, link);
421			if (labelLink != null) {
422				final ElkLabel edgeLabel = ElkGraphUtil.createLabel(edge);
423				final XDimension2D dim = labelLink.calculateDimension(stringBounder);
424				edgeLabel.setText("X");
425				edgeLabel.setDimensions(dim.getWidth(), dim.getHeight());
426				// Duplicated, with qualifier, but who cares?
427				edge.setProperty(CoreOptions.EDGE_LABELS_INLINE, true);
428				// edge.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
429			}
430			if (link.getQuantifier1() != null || link.getRole1() != null) {
431				final TextBlock q1 = getQuantifier(stringBounder, link, 1);
432				final TextBlock r1 = getRoleLabel(stringBounder, link, 1);
433				final TextBlock forLayout = q1 != null ? q1 : r1;
434				final ElkLabel edgeLabel = ElkGraphUtil.createLabel(edge);
435				final XDimension2D dim = forLayout.calculateDimension(stringBounder);
436				// Nasty trick, we store the kind of label in the text
437				edgeLabel.setText("1");
438				edgeLabel.setDimensions(dim.getWidth(), dim.getHeight());
439				edgeLabel.setProperty(CoreOptions.EDGE_LABELS_PLACEMENT, EdgeLabelPlacement.TAIL);
440				// Duplicated, with main label, but who cares?
441				edge.setProperty(CoreOptions.EDGE_LABELS_INLINE, true);
442				// edge.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
443			}
444			if (link.getQuantifier2() != null || link.getRole2() != null) {
445				final TextBlock q2 = getQuantifier(stringBounder, link, 2);
446				final TextBlock r2 = getRoleLabel(stringBounder, link, 2);
447				final TextBlock forLayout = q2 != null ? q2 : r2;
448				final ElkLabel edgeLabel = ElkGraphUtil.createLabel(edge);
449				final XDimension2D dim = forLayout.calculateDimension(stringBounder);
450				// Nasty trick, we store the kind of label in the text
451				edgeLabel.setText("2");
452				edgeLabel.setDimensions(dim.getWidth(), dim.getHeight());
453				edgeLabel.setProperty(CoreOptions.EDGE_LABELS_PLACEMENT, EdgeLabelPlacement.HEAD);
454				// Duplicated, with main label, but who cares?
455				edge.setProperty(CoreOptions.EDGE_LABELS_INLINE, true);
456				// edge.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
457			}
458	
459			edges.put(link, edge);
460		}
461	
462		private IEntityImage printEntityInternal(Entity ent) {
463			if (ent.isRemoved())
464				throw new IllegalStateException();
465	
466			if (ent.getSvekImage() == null) {
467				final ISkinParam skinParam = diagram.getSkinParam();
468				if (skinParam.sameClassWidth())
469					System.err.println("NOT YET IMPLEMENTED");
470	
471				return GeneralImageBuilder.createEntityImageBlock(ent, diagram.isHideEmptyDescriptionForState(), diagram,
472						getBibliotekon(), null, diagram.getLinks());
473			}
474			return ent.getSvekImage();
475		}
476	
477		@Override
478		public TextBlock getTextBlock(List<String> dotStrings, FileFormatOption fileFormatOption)
479				throws IOException, InterruptedException {
480	
481			final ElkNode root = ElkGraphUtil.createGraph();
482			root.setProperty(CoreOptions.DIRECTION, Direction.DOWN);
483			root.setProperty(CoreOptions.HIERARCHY_HANDLING, HierarchyHandling.INCLUDE_CHILDREN);
484	
485			final StringBounder stringBounder = fileFormatOption.getDefaultStringBounder(diagram.getSkinParam(),
486					diagram.getPragma());
487	
488			this.printAllSubgroups(stringBounder, root, diagram.getRootGroup());
489			this.printEntities(stringBounder, root, getUnpackagedEntities());
490	
491			this.manageAllEdges(stringBounder);
492	
493			new RecursiveGraphLayoutEngine().layout(root, new NullElkProgressMonitor());
494	
495			final MinMax minMax = TextBlockUtils.getMinMax(
496					new MyElkDrawing(clusterManager, diagram, null, clusters, edges, nodes), stringBounder, false);
497	
498			return new MyElkDrawing(clusterManager, diagram, minMax, clusters, edges, nodes);
499		}
500	
501	}
```

**Tool Call: cd /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk && grep -n "getContainingNode\|getPosition\|getSections\|getLabels\|getBendPoints\|getStart\|getEnd\|getText\|getX\|getY\|getWidth\|getHeight\|isHierarchical\|translate" MyElk*.java; grep -n "^import" MyElkDrawing.java MyElkEdge.java MyElkCluster.java | grep -i elk; grep -n "call2\|public static Object call(" proxy/Reflect.java**
Status: Completed

Terminal:
```
MyElkDrawing.java:134:			final XPoint2D corner = CucaDiagramFileMakerElk.getPosition(ent.getValue());
MyElkDrawing.java:139:			final IEntityImage image = IEntityImageUtils.translate(printEntityInternal(entity),
MyElkDrawing.java:183:		// Unfortunately, we have to translate "edge" in its own "cluster" coordinate
MyElkDrawing.java:184:		final XPoint2D translate = CucaDiagramFileMakerElk.getPosition(edge.getContainingNode());
MyElkDrawing.java:204:				tailRole, headRole, magicY2, elkClusters, UTranslate.point(translate), nodeImages);
MyElkDrawing.java:298:		if (note.getPosition() == Position.LEFT)
MyElkDrawing.java:300:		else if (note.getPosition() == Position.RIGHT)
MyElkDrawing.java:302:		else if (note.getPosition() == Position.TOP)
MyElkCluster.java:83:		final XPoint2D corner = CucaDiagramFileMakerElk.getPosition(elkNode);
MyElkCluster.java:84:		final URectangle rect = URectangle.build(elkNode.getWidth(), elkNode.getHeight());
MyElkCluster.java:107:		final RectangleArea rectangleArea = new RectangleArea(0, 0, elkNode.getWidth(), elkNode.getHeight());
MyElkCluster.java:142:		final XPoint2D corner = CucaDiagramFileMakerElk.getPosition(elkNode);
MyElkCluster.java:152:		final RectangleArea rectangleArea = new RectangleArea(0, 0, elkNode.getWidth(), elkNode.getHeight());
MyElkCluster.java:158:		final TextBlock textBlock = decoration.getTextBlock(HColors.BLACK, HColors.BLACK, 0, 0,
MyElkEdge.java:117:	private final UTranslate translate;
MyElkEdge.java:122:			UTranslate translate, Map<Entity, IEntityImage> nodeImages) {
MyElkEdge.java:125:		this.translate = translate;
MyElkEdge.java:152:		ug = ug.apply(translate);
MyElkEdge.java:199:		final List<ElkEdgeSection> sections = edge.getSections();
MyElkEdge.java:202:			System.err.println("Maybe a 'Long hierarchical edge' " + edge.isHierarchical());
MyElkEdge.java:212:			final double x = sections.get(0).getEndX();
MyElkEdge.java:213:			final double y = sections.get(0).getEndY();
MyElkEdge.java:219:			final double x = sections.get(0).getStartX();
MyElkEdge.java:220:			final double y = sections.get(0).getStartY();
MyElkEdge.java:243:		for (ElkLabel label : edge.getLabels()) {
MyElkEdge.java:244:			final double x = label.getX();
MyElkEdge.java:245:			final double y = label.getY();
MyElkEdge.java:248:			final String type = label.getText();
MyElkEdge.java:264:		final List<ElkEdgeSection> sections = edge.getSections();
MyElkEdge.java:267:			final XPoint2D startPt = new XPoint2D(section.getStartX(), section.getStartY());
MyElkEdge.java:268:			final XPoint2D endPt = new XPoint2D(section.getEndX(), section.getEndY());
MyElkEdge.java:281:		final XPoint2D pos = RoleLabels.getPosition(quantifier.calculateDimension(stringBounder),
MyElkEdge.java:284:		role.drawU(ug.apply(new UTranslate(pos.getX(), pos.getY())));
MyElkEdge.java:290:			final Collection<ElkBendPoint> points = section.getBendPoints();
MyElkEdge.java:292:			double x1 = section.getStartX();
MyElkEdge.java:293:			double y1 = section.getStartY();
MyElkEdge.java:297:					translate.getTranslated(tmpStart));
MyElkEdge.java:303:				drawLine(ug, x1, y1, pt.getX(), pt.getY());
MyElkEdge.java:304:				x1 = pt.getX();
MyElkEdge.java:305:				y1 = pt.getY();
MyElkEdge.java:308:			final XPoint2D tmpEnd = new XPoint2D(section.getEndX(), section.getEndY() + magicY2);
MyElkEdge.java:310:					translate.getTranslated(tmpEnd));
MyElkEdge.java:38:import java.util.Collection;
MyElkEdge.java:39:import java.util.Collections;
MyElkEdge.java:40:import java.util.List;
MyElkEdge.java:41:import java.util.Map;
MyElkEdge.java:43:import net.atmp.CucaDiagram;
MyElkEdge.java:44:import net.sourceforge.plantuml.abel.Entity;
MyElkEdge.java:45:import net.sourceforge.plantuml.abel.Link;
MyElkEdge.java:46:import net.sourceforge.plantuml.annotation.DuplicateCode;
MyElkEdge.java:47:import net.sourceforge.plantuml.decoration.LinkDecor;
MyElkEdge.java:48:import net.sourceforge.plantuml.decoration.LinkType;
MyElkEdge.java:62:import org.eclipse.elk.graph.ElkBendPoint;
MyElkEdge.java:63:import org.eclipse.elk.graph.ElkEdge;
MyElkEdge.java:64:import org.eclipse.elk.graph.ElkEdgeSection;
MyElkEdge.java:65:import org.eclipse.elk.graph.ElkLabel;
MyElkEdge.java:68:import net.sourceforge.plantuml.elk.proxy.graph.ElkBendPoint;
MyElkEdge.java:69:import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
MyElkEdge.java:70:import net.sourceforge.plantuml.elk.proxy.graph.ElkEdgeSection;
MyElkEdge.java:71:import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
MyElkEdge.java:72:import net.sourceforge.plantuml.klimt.UStroke;
MyElkEdge.java:73:import net.sourceforge.plantuml.klimt.UTranslate;
MyElkEdge.java:74:import net.sourceforge.plantuml.klimt.color.ColorType;
MyElkEdge.java:75:import net.sourceforge.plantuml.klimt.color.HColor;
MyElkEdge.java:76:import net.sourceforge.plantuml.klimt.color.HColors;
MyElkEdge.java:77:import net.sourceforge.plantuml.klimt.drawing.UGraphic;
MyElkEdge.java:78:import net.sourceforge.plantuml.klimt.font.StringBounder;
MyElkEdge.java:79:import net.sourceforge.plantuml.klimt.geom.MagneticBorder;
MyElkEdge.java:80:import net.sourceforge.plantuml.klimt.geom.MagneticBorderNone;
MyElkEdge.java:81:import net.sourceforge.plantuml.klimt.geom.RectangleArea;
MyElkEdge.java:82:import net.sourceforge.plantuml.klimt.geom.XPoint2D;
MyElkEdge.java:83:import net.sourceforge.plantuml.klimt.shape.TextBlock;
MyElkEdge.java:84:import net.sourceforge.plantuml.klimt.shape.UDrawable;
MyElkEdge.java:85:import net.sourceforge.plantuml.klimt.shape.ULine;
MyElkEdge.java:86:import net.sourceforge.plantuml.skin.LineParam;
MyElkEdge.java:87:import net.sourceforge.plantuml.style.ISkinParam;
MyElkEdge.java:88:import net.sourceforge.plantuml.style.PName;
MyElkEdge.java:89:import net.sourceforge.plantuml.style.SName;
MyElkEdge.java:90:import net.sourceforge.plantuml.style.Style;
MyElkEdge.java:91:import net.sourceforge.plantuml.style.StyleQueries;
MyElkEdge.java:92:import net.sourceforge.plantuml.style.StyleQuery;
MyElkEdge.java:93:import net.sourceforge.plantuml.svek.RoleLabels;
MyElkEdge.java:94:import net.sourceforge.plantuml.svek.IEntityImage;
MyElkEdge.java:95:import net.sourceforge.plantuml.svek.extremity.ExtremityFactory;
MyElkEdge.java:96:import net.sourceforge.plantuml.svek.extremity.ExtremityFactoryExtends;
MyElkCluster.java:38:import net.atmp.CucaDiagram;
MyElkCluster.java:39:import net.sourceforge.plantuml.abel.Entity;
MyElkCluster.java:40:import net.sourceforge.plantuml.annotation.DuplicateCode;
MyElkCluster.java:41:import net.sourceforge.plantuml.core.DiagramType;
MyElkCluster.java:42:import net.sourceforge.plantuml.decoration.symbol.USymbol;
MyElkCluster.java:43:import net.sourceforge.plantuml.decoration.symbol.USymbols;
MyElkCluster.java:44:import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
MyElkCluster.java:45:import net.sourceforge.plantuml.klimt.UStroke;
MyElkCluster.java:46:import net.sourceforge.plantuml.klimt.UTranslate;
MyElkCluster.java:47:import net.sourceforge.plantuml.klimt.color.HColor;
MyElkCluster.java:48:import net.sourceforge.plantuml.klimt.color.ColorType;
MyElkCluster.java:49:import net.sourceforge.plantuml.klimt.color.HColors;
MyElkCluster.java:50:import net.sourceforge.plantuml.klimt.drawing.UGraphic;
MyElkCluster.java:51:import net.sourceforge.plantuml.klimt.font.StringBounder;
MyElkCluster.java:52:import net.sourceforge.plantuml.klimt.geom.MagneticBorder;
MyElkCluster.java:53:import net.sourceforge.plantuml.klimt.geom.MagneticBorderNone;
MyElkCluster.java:54:import net.sourceforge.plantuml.klimt.geom.RectangleArea;
MyElkCluster.java:55:import net.sourceforge.plantuml.klimt.geom.XPoint2D;
MyElkCluster.java:56:import net.sourceforge.plantuml.klimt.shape.TextBlock;
MyElkCluster.java:57:import net.sourceforge.plantuml.klimt.shape.URectangle;
MyElkCluster.java:58:import net.sourceforge.plantuml.skin.AlignmentParam;
MyElkCluster.java:59:import net.sourceforge.plantuml.style.ISkinParam;
MyElkCluster.java:60:import net.sourceforge.plantuml.style.PName;
MyElkCluster.java:61:import net.sourceforge.plantuml.style.Style;
MyElkCluster.java:62:import net.sourceforge.plantuml.svek.Cluster;
MyElkCluster.java:63:import net.sourceforge.plantuml.svek.ClusterDecoration;
MyElkCluster.java:64:import net.sourceforge.plantuml.svek.ClusterHeader;
MyElkCluster.java:65:import net.sourceforge.plantuml.svek.PackageStyle;
MyElkDrawing.java:38:import java.util.HashMap;
MyElkDrawing.java:39:import java.util.Map;
MyElkDrawing.java:40:import java.util.Map.Entry;
MyElkDrawing.java:42:import net.atmp.CucaDiagram;
MyElkDrawing.java:43:import net.sourceforge.plantuml.abel.CucaNote;
MyElkDrawing.java:44:import net.sourceforge.plantuml.abel.Entity;
MyElkDrawing.java:45:import net.sourceforge.plantuml.abel.Link;
MyElkDrawing.java:46:import net.sourceforge.plantuml.annotation.DuplicateCode;
MyElkDrawing.java:47:import net.sourceforge.plantuml.annotation.Fast;
MyElkDrawing.java:48:import net.sourceforge.plantuml.core.DiagramType;
MyElkDrawing.java:49:import net.sourceforge.plantuml.decoration.symbol.USymbolFolder;
MyElkDrawing.java:50:import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
MyElkDrawing.java:51:import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
MyElkDrawing.java:52:import net.sourceforge.plantuml.klimt.LineBreakStrategy;
MyElkDrawing.java:53:import net.sourceforge.plantuml.klimt.UTranslate;
MyElkDrawing.java:54:import net.sourceforge.plantuml.klimt.color.HColor;
MyElkDrawing.java:55:import net.sourceforge.plantuml.klimt.creole.CreoleMode;
MyElkDrawing.java:56:import net.sourceforge.plantuml.klimt.creole.Display;
MyElkDrawing.java:57:import net.sourceforge.plantuml.klimt.drawing.UGraphic;
MyElkDrawing.java:58:import net.sourceforge.plantuml.klimt.font.FontConfiguration;
MyElkDrawing.java:59:import net.sourceforge.plantuml.klimt.font.FontParam;
MyElkDrawing.java:60:import net.sourceforge.plantuml.klimt.font.StringBounder;
MyElkDrawing.java:61:import net.sourceforge.plantuml.klimt.geom.HorizontalAlignment;
MyElkDrawing.java:62:import net.sourceforge.plantuml.klimt.geom.MinMax;
MyElkDrawing.java:63:import net.sourceforge.plantuml.klimt.geom.VerticalAlignment;
MyElkDrawing.java:64:import net.sourceforge.plantuml.klimt.geom.XDimension2D;
MyElkDrawing.java:65:import net.sourceforge.plantuml.klimt.geom.XPoint2D;
MyElkDrawing.java:66:import net.sourceforge.plantuml.klimt.shape.TextBlock;
MyElkDrawing.java:67:import net.sourceforge.plantuml.klimt.shape.TextBlockUtils;
MyElkDrawing.java:68:import net.sourceforge.plantuml.skin.AlignmentParam;
MyElkDrawing.java:69:import net.sourceforge.plantuml.skin.VisibilityModifier;
MyElkDrawing.java:70:import net.sourceforge.plantuml.skin.rose.Rose;
MyElkDrawing.java:71:import net.sourceforge.plantuml.stereo.Stereotype;
MyElkDrawing.java:72:import net.sourceforge.plantuml.style.ISkinParam;
MyElkDrawing.java:73:import net.sourceforge.plantuml.style.SName;
MyElkDrawing.java:74:import net.sourceforge.plantuml.style.Style;
MyElkDrawing.java:75:import net.sourceforge.plantuml.style.StyleQueries;
MyElkDrawing.java:76:import net.sourceforge.plantuml.style.StyleQuery;
MyElkDrawing.java:77:import net.sourceforge.plantuml.svek.RoleLabels;
MyElkDrawing.java:78:import net.sourceforge.plantuml.svek.Bibliotekon;
MyElkDrawing.java:79:import net.sourceforge.plantuml.svek.ClusterManager;
MyElkDrawing.java:80:import net.sourceforge.plantuml.svek.GeneralImageBuilder;
MyElkDrawing.java:81:import net.sourceforge.plantuml.svek.IEntityImage;
MyElkDrawing.java:82:import net.sourceforge.plantuml.svek.IEntityImageUtils;
MyElkDrawing.java:83:import net.sourceforge.plantuml.svek.SvekNode;
MyElkDrawing.java:84:import net.sourceforge.plantuml.svek.image.EntityImageNoteLink;
MyElkDrawing.java:85:import net.sourceforge.plantuml.utils.Position;
191:	public static Object call(Object instance, String method) {
201:	public static Object call(Object instance, String method, Object arg1) {
211:	public static Object call(Object instance, String method, Object arg1, Object arg2) {
221:	public static Object call2(Object instance, String method, Object arg1, Object arg2) {
```


**Tool Call: cd /tmp/elkjs-dl/package/lib && grep -n -o "org.eclipse.elk.json.[a-zA-Z]*" elk-worker.js | sort -u -t: -k3 | head; grep -n "EdgeCoords\b\|'CONTAINER'\|'PARENT'\|'ROOT'\|'INHERIT'" elk-worker.js | head -20**
Status: Completed

Terminal:
```
70823:org.eclipse.elk.json.shapeCoords
62258:  ROOT_0 = new Property_0('ROOT', ($clinit_Boolean() , false));
70824:  JSON_EDGE_COORDS_DEFAULT = ($clinit_EdgeCoords() , INHERIT);
71078:function $clinit_EdgeCoords(){
71079:  $clinit_EdgeCoords = emptyMethod;
71080:  INHERIT = new EdgeCoords('INHERIT', 0);
71081:  CONTAINER = new EdgeCoords('CONTAINER', 1);
71082:  PARENT = new EdgeCoords('PARENT', 2);
71083:  ROOT_1 = new EdgeCoords('ROOT', 3);
71086:function EdgeCoords(enum$name, enum$ordinal){
71091:  $clinit_EdgeCoords();
71092:  return valueOf(($clinit_EdgeCoords$Map() , $MAP_93), name_0);
71096:  $clinit_EdgeCoords();
71100:defineClass(284, 23, {3:1, 34:1, 23:1, 284:1}, EdgeCoords);
71102:var Lorg_eclipse_elk_core_options_EdgeCoords_2_classLit = createForEnum('org.eclipse.elk.core.options', 'EdgeCoords', 284, Ljava_lang_Enum_2_classLit, values_111, valueOf_103);
71103:function $clinit_EdgeCoords$Map(){
71104:  $clinit_EdgeCoords$Map = emptyMethod;
71105:  $MAP_93 = createValueOfMap(($clinit_EdgeCoords() , stampJavaTypeInfo(getClassLiteralForArray(Lorg_eclipse_elk_core_options_EdgeCoords_2_classLit, 1), $intern_38, 284, 0, [INHERIT, CONTAINER, PARENT, ROOT_1])));
71250:  INHERIT_0 = new HierarchyHandling('INHERIT', 0);
71651:  INHERIT_1 = new ShapeCoords('INHERIT', 0);
71652:  PARENT_0 = new ShapeCoords('PARENT', 1);

Shell cwd was reset to /home/david/plantuml
```


## Assistant

<thinking>
elkjs handles edge coordinates the same way ELK Java does, relative to the declaring node, so if I declare each edge in the container that `createSimpleEdge` would choose, the existing drawing code keeps working. I'm checking the ELK jar for that containment rule next.


</thinking>

**Tool Call: find / -name 'org.eclipse.elk.graph*.jar' -o -name 'org.eclipse.elk.core-*.jar' 2>/dev/null | grep -v '^/proc' | head; unzip -l plantuml.jar | grep -c "org/eclipse/elk"**
Status: Completed

Terminal:
```
/tmp/elkjars/org.eclipse.elk.graph-0.12.0.jar
/tmp/elkjars/org.eclipse.elk.core-0.12.0.jar
0



```


**Tool Call: ls /tmp/elkjars/; cd /tmp && rm -rf /tmp/elkcls && mkdir /tmp/elkcls && cd /tmp/elkcls && unzip -q /tmp/elkjars/org.eclipse.elk.graph-0.12.0.jar 'org/eclipse/elk/graph/util/ElkGraphUtil.class' && javap -c -p org/eclipse/elk/graph/util/ElkGraphUtil.class | sed -n '/findBestEdgeContainment/,/^$/p' | head -120**
Status: Completed

Terminal:
```
guava-33.3.1-jre.jar
org.eclipse.elk.alg.common-0.12.0.jar
org.eclipse.elk.alg.common-0.12.0.pom
org.eclipse.elk.alg.layered-0.12.0.jar
org.eclipse.elk.alg.layered-0.12.0.pom
org.eclipse.elk.alg.mrtree-0.12.0.jar
org.eclipse.elk.alg.mrtree-0.12.0.pom
org.eclipse.elk.core-0.12.0.jar
org.eclipse.elk.core-0.12.0.pom
org.eclipse.elk.graph-0.12.0.jar
org.eclipse.elk.graph-0.12.0.pom
org.eclipse.emf.common-2.12.0.jar
org.eclipse.emf.ecore-2.12.0.jar
org.eclipse.emf.ecore.xmi-2.12.0.jar
org.eclipse.xtext.xbase.lib-2.44.0.jar
       9: invokestatic  #225                // Method findBestEdgeContainment:(Lorg/eclipse/elk/graph/ElkEdge;)Lorg/eclipse/elk/graph/ElkNode;
      12: invokeinterface #73,  2           // InterfaceMethod org/eclipse/elk/graph/ElkEdge.setContainingNode:(Lorg/eclipse/elk/graph/ElkNode;)V
      17: return

  public static org.eclipse.elk.graph.ElkNode findBestEdgeContainment(org.eclipse.elk.graph.ElkEdge);
    Code:
       0: aload_0
       1: ldc           #223                // String edge cannot be null
       3: invokestatic  #85                 // Method java/util/Objects.requireNonNull:(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
       6: pop
       7: aload_0
       8: invokeinterface #95,  1           // InterfaceMethod org/eclipse/elk/graph/ElkEdge.getSources:()Lorg/eclipse/emf/common/util/EList;
      13: invokeinterface #229,  1          // InterfaceMethod org/eclipse/emf/common/util/EList.size:()I
      18: aload_0
      19: invokeinterface #105,  1          // InterfaceMethod org/eclipse/elk/graph/ElkEdge.getTargets:()Lorg/eclipse/emf/common/util/EList;
      24: invokeinterface #229,  1          // InterfaceMethod org/eclipse/emf/common/util/EList.size:()I
      29: iadd
      30: tableswitch   { // 0 to 1
                     0: 52
                     1: 62
               default: 124
          }
      52: new           #230                // class java/lang/IllegalArgumentException
      55: dup
      56: ldc           #232                // String The edge must have at least one source or target.
      58: invokespecial #234                // Method java/lang/IllegalArgumentException."<init>":(Ljava/lang/String;)V
      61: athrow
      62: aload_0
      63: invokeinterface #95,  1           // InterfaceMethod org/eclipse/elk/graph/ElkEdge.getSources:()Lorg/eclipse/emf/common/util/EList;
      68: invokeinterface #168,  1          // InterfaceMethod org/eclipse/emf/common/util/EList.isEmpty:()Z
      73: ifeq          100
      76: aload_0
      77: invokeinterface #105,  1          // InterfaceMethod org/eclipse/elk/graph/ElkEdge.getTargets:()Lorg/eclipse/emf/common/util/EList;
      82: iconst_0
      83: invokeinterface #173,  2          // InterfaceMethod org/eclipse/emf/common/util/EList.get:(I)Ljava/lang/Object;
      88: checkcast     #135                // class org/eclipse/elk/graph/ElkConnectableShape
      91: invokestatic  #237                // Method connectableShapeToNode:(Lorg/eclipse/elk/graph/ElkConnectableShape;)Lorg/eclipse/elk/graph/ElkNode;
      94: invokeinterface #241,  1          // InterfaceMethod org/eclipse/elk/graph/ElkNode.getParent:()Lorg/eclipse/elk/graph/ElkNode;
      99: areturn
     100: aload_0
     101: invokeinterface #95,  1           // InterfaceMethod org/eclipse/elk/graph/ElkEdge.getSources:()Lorg/eclipse/emf/common/util/EList;
     106: iconst_0
     107: invokeinterface #173,  2          // InterfaceMethod org/eclipse/emf/common/util/EList.get:(I)Ljava/lang/Object;
     112: checkcast     #135                // class org/eclipse/elk/graph/ElkConnectableShape
     115: invokestatic  #237                // Method connectableShapeToNode:(Lorg/eclipse/elk/graph/ElkConnectableShape;)Lorg/eclipse/elk/graph/ElkNode;
     118: invokeinterface #241,  1          // InterfaceMethod org/eclipse/elk/graph/ElkNode.getParent:()Lorg/eclipse/elk/graph/ElkNode;
     123: areturn
     124: aload_0
     125: invokeinterface #95,  1           // InterfaceMethod org/eclipse/elk/graph/ElkEdge.getSources:()Lorg/eclipse/emf/common/util/EList;
     130: invokeinterface #229,  1          // InterfaceMethod org/eclipse/emf/common/util/EList.size:()I
     135: iconst_1
     136: if_icmpne     238
     139: aload_0
     140: invokeinterface #105,  1          // InterfaceMethod org/eclipse/elk/graph/ElkEdge.getTargets:()Lorg/eclipse/emf/common/util/EList;
     145: invokeinterface #229,  1          // InterfaceMethod org/eclipse/emf/common/util/EList.size:()I
     150: iconst_1
     151: if_icmpne     238
     154: aload_0
     155: invokeinterface #95,  1           // InterfaceMethod org/eclipse/elk/graph/ElkEdge.getSources:()Lorg/eclipse/emf/common/util/EList;
     160: iconst_0
     161: invokeinterface #173,  2          // InterfaceMethod org/eclipse/emf/common/util/EList.get:(I)Ljava/lang/Object;
     166: checkcast     #135                // class org/eclipse/elk/graph/ElkConnectableShape
     169: invokestatic  #237                // Method connectableShapeToNode:(Lorg/eclipse/elk/graph/ElkConnectableShape;)Lorg/eclipse/elk/graph/ElkNode;
     172: astore_1
     173: aload_0
     174: invokeinterface #105,  1          // InterfaceMethod org/eclipse/elk/graph/ElkEdge.getTargets:()Lorg/eclipse/emf/common/util/EList;
     179: iconst_0
     180: invokeinterface #173,  2          // InterfaceMethod org/eclipse/emf/common/util/EList.get:(I)Ljava/lang/Object;
     185: checkcast     #135                // class org/eclipse/elk/graph/ElkConnectableShape
     188: invokestatic  #237                // Method connectableShapeToNode:(Lorg/eclipse/elk/graph/ElkConnectableShape;)Lorg/eclipse/elk/graph/ElkNode;
     191: astore_2
     192: aload_1
     193: invokeinterface #241,  1          // InterfaceMethod org/eclipse/elk/graph/ElkNode.getParent:()Lorg/eclipse/elk/graph/ElkNode;
     198: aload_2
     199: invokeinterface #241,  1          // InterfaceMethod org/eclipse/elk/graph/ElkNode.getParent:()Lorg/eclipse/elk/graph/ElkNode;
     204: if_acmpne     214
     207: aload_1
     208: invokeinterface #241,  1          // InterfaceMethod org/eclipse/elk/graph/ElkNode.getParent:()Lorg/eclipse/elk/graph/ElkNode;
     213: areturn
     214: aload_1
     215: aload_2
     216: invokeinterface #241,  1          // InterfaceMethod org/eclipse/elk/graph/ElkNode.getParent:()Lorg/eclipse/elk/graph/ElkNode;
     221: if_acmpne     226
     224: aload_1
     225: areturn
     226: aload_2
     227: aload_1
     228: invokeinterface #241,  1          // InterfaceMethod org/eclipse/elk/graph/ElkNode.getParent:()Lorg/eclipse/elk/graph/ElkNode;
     233: if_acmpne     238
     236: aload_2
     237: areturn
     238: aload_0
     239: invokestatic  #244                // Method allIncidentShapes:(Lorg/eclipse/elk/graph/ElkEdge;)Ljava/lang/Iterable;
     242: invokeinterface #123,  1          // InterfaceMethod java/lang/Iterable.iterator:()Ljava/util/Iterator;
     247: astore_1
     248: aload_1
     249: invokeinterface #129,  1          // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
     254: checkcast     #135                // class org/eclipse/elk/graph/ElkConnectableShape
     257: invokestatic  #237                // Method connectableShapeToNode:(Lorg/eclipse/elk/graph/ElkConnectableShape;)Lorg/eclipse/elk/graph/ElkNode;
     260: astore_2
     261: goto          327
     264: aload_1
     265: invokeinterface #129,  1          // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
     270: checkcast     #135                // class org/eclipse/elk/graph/ElkConnectableShape
     273: invokestatic  #237                // Method connectableShapeToNode:(Lorg/eclipse/elk/graph/ElkConnectableShape;)Lorg/eclipse/elk/graph/ElkNode;
     276: astore_3
     277: aload_3
     278: aload_2
     279: if_acmpeq     327
     282: aload_3
     283: aload_2
     284: invokestatic  #248                // Method isDescendant:(Lorg/eclipse/elk/graph/ElkNode;Lorg/eclipse/elk/graph/ElkNode;)Z
     287: ifne          327
     290: aload_3
     291: invokeinterface #241,  1          // InterfaceMethod org/eclipse/elk/graph/ElkNode.getParent:()Lorg/eclipse/elk/graph/ElkNode;
     296: aload_2
     297: invokeinterface #241,  1          // InterfaceMethod org/eclipse/elk/graph/ElkNode.getParent:()Lorg/eclipse/elk/graph/ElkNode;
     302: if_acmpne     315
     305: aload_3
     306: invokeinterface #241,  1          // InterfaceMethod org/eclipse/elk/graph/ElkNode.getParent:()Lorg/eclipse/elk/graph/ElkNode;

Shell cwd was reset to /home/david/plantuml
```


**Tool Call: cd /tmp/elkcls && javap -c -p org/eclipse/elk/graph/util/ElkGraphUtil.class | sed -n '/findBestEdgeContainment(org/,/^$/p' | sed -n '120,175p'; javap -c -p org/eclipse/elk/graph/util/ElkGraphUtil.class | sed -n '/ElkNode findLowestCommonAncestor/,/^$/p' | head -50**
Status: Completed

Terminal:
```
     316: aload_3
     317: invokestatic  #252                // Method findLowestCommonAncestor:(Lorg/eclipse/elk/graph/ElkNode;Lorg/eclipse/elk/graph/ElkNode;)Lorg/eclipse/elk/graph/ElkNode;
     320: astore_2
     321: aload_2
     322: ifnonnull     327
     325: aconst_null
     326: areturn
     327: aload_1
     328: invokeinterface #140,  1          // InterfaceMethod java/util/Iterator.hasNext:()Z
     333: ifne          264
     336: aload_2
     337: areturn

  public static org.eclipse.elk.graph.ElkNode findLowestCommonAncestor(org.eclipse.elk.graph.ElkNode, org.eclipse.elk.graph.ElkNode);
    Code:
       0: new           #263                // class org/eclipse/elk/graph/util/ElkGraphUtil$AncestorIterator
       3: dup
       4: aload_0
       5: iconst_1
       6: invokespecial #265                // Method org/eclipse/elk/graph/util/ElkGraphUtil$AncestorIterator."<init>":(Lorg/eclipse/elk/graph/ElkNode;Z)V
       9: invokestatic  #268                // Method com/google/common/collect/Lists.newArrayList:(Ljava/util/Iterator;)Ljava/util/ArrayList;
      12: astore_2
      13: aload_2
      14: aload_2
      15: invokeinterface #191,  1          // InterfaceMethod java/util/List.size:()I
      20: invokeinterface #274,  2          // InterfaceMethod java/util/List.listIterator:(I)Ljava/util/ListIterator;
      25: astore_3
      26: new           #263                // class org/eclipse/elk/graph/util/ElkGraphUtil$AncestorIterator
      29: dup
      30: aload_1
      31: iconst_1
      32: invokespecial #265                // Method org/eclipse/elk/graph/util/ElkGraphUtil$AncestorIterator."<init>":(Lorg/eclipse/elk/graph/ElkNode;Z)V
      35: invokestatic  #268                // Method com/google/common/collect/Lists.newArrayList:(Ljava/util/Iterator;)Ljava/util/ArrayList;
      38: astore        4
      40: aload         4
      42: aload         4
      44: invokeinterface #191,  1          // InterfaceMethod java/util/List.size:()I
      49: invokeinterface #274,  2          // InterfaceMethod java/util/List.listIterator:(I)Ljava/util/ListIterator;
      54: astore        5
      56: aconst_null
      57: astore        6
      59: goto          96
      62: aload_3
      63: invokeinterface #278,  1          // InterfaceMethod java/util/ListIterator.previous:()Ljava/lang/Object;
      68: checkcast     #24                 // class org/eclipse/elk/graph/ElkNode
      71: astore        7
      73: aload         5
      75: invokeinterface #278,  1          // InterfaceMethod java/util/ListIterator.previous:()Ljava/lang/Object;
      80: checkcast     #24                 // class org/eclipse/elk/graph/ElkNode
      83: astore        8
      85: aload         7
      87: aload         8
      89: if_acmpne     115
      92: aload         7
      94: astore        6
      96: aload_3
      97: invokeinterface #283,  1          // InterfaceMethod java/util/ListIterator.hasPrevious:()Z
     102: ifeq          115
     105: aload         5
     107: invokeinterface #283,  1          // InterfaceMethod java/util/ListIterator.hasPrevious:()Z
     112: ifne          62
     115: aload         6
     117: areturn

Shell cwd was reset to /home/david/plantuml
```


## Assistant

<thinking>
Containment is handled by the general case, with self-loops going to the parent. Everything except the final elkjs call stays plain Java, with a second JSON-backed layout backend that JVM tests can check against real ELK; next up is the browser script-loading plumbing.


</thinking>

**Tool Call: cd /home/david/plantuml/src/main/java/net/sourceforge/plantuml/teavm/js/browser && sed -n '/^package/,$p' PendingResources.java | grep -v "^import"**
Status: Completed

Terminal:
```
package net.sourceforge.plantuml.teavm.js.browser;




/**
 * The resources a rendering needs from the JavaScript side, which only arrive
 * asynchronously: support scripts (<code>emoji.js</code>, <code>themes.js</code>,
 * <code>&lt;lib&gt;.min.js</code>...), files of the host's
 * <code>PLANTUML_FILE_LOADER</code>, and the Viz.js instance.
 *
 * <h2>Why</h2>
 *
 * The Java code of PlantUML is synchronous: an <code>!include</code> reads its
 * file and goes on. Waiting for JavaScript in the middle of it would need
 * TeaVM's coroutines (threads, <code>Object.wait()</code>, <code>@Async</code>),
 * which turn every method above the waiting point into a state machine. The
 * engine uses none of them. Instead, a rendering is an <b>attempt</b> that can
 * be thrown away:
 * <ol>
 * <li>A loader that does not have its resource yet does not wait: it notes it
 * here as missing, and fails as it fails when the resource does not exist.</li>
 * <li>At the end of the attempt, {@link RenderQueue} looks at the missing
 * resources. None: the result is delivered. Otherwise the attempt is discarded,
 * everything missing is loaded with plain JavaScript callbacks
 * ({@link #loadMissing(ScriptLoaderJs.Ok)}), and the rendering starts again
 * from the beginning.</li>
 * </ol>
 * Each round discovers the next level: the includes of an included file, a
 * theme chosen by a variable, the bundle a stdlib library links to. Parsing and
 * rendering rebuild everything from the source lines, so a discarded attempt
 * leaves nothing behind but what is kept here.
 *
 * <h2>What is kept, and for how long</h2>
 *
 * What the loaders answered, success or failure, is kept for the current
 * request only ({@link #startRequest()}): the next request asks the host
 * again, so an edited file or a fixed network is seen at the next rendering.
 * A failure is kept too, so that a resource that cannot be loaded is not
 * loaded again at every round. Loaded scripts are global to the page anyway,
 * and so is the Viz.js instance.
 * <p>
 * A host that never answers does not leave the engine waiting forever: after
 * {@link #ROUND_TIMEOUT_MS} the resources still pending count as failed, with a
 * message saying so, and the rendering goes on (a late answer is ignored).
 * Nothing blocks meanwhile: the timer is a JavaScript one.
 * <p>
 * Outside an attempt (the synchronous exports of the headless build, such as
 * <code>checkSyntax</code>) nothing is noted: a resource that is not there yet
 * simply fails.
 */
public final class PendingResources {
	// ::remove file when JAVA8

	/**
	 * How many times a request may be rendered again because resources were
	 * missing. Each round loads every resource the attempt asked for, so this is
	 * a depth (nested includes, links between libraries), not a count of files.
	 */
	public static final int MAX_ROUNDS = 20;

	/**
	 * How long a round waits for the host and the network before the resources
	 * still pending count as failed.
	 */
	public static final int ROUND_TIMEOUT_MS = 60000;

	private static final String SCRIPT = "script";
	private static final String FILE = "file";
	private static final String VIZ = "viz";

	/**
	 * The answer of a loader. A successful one has no error; for a file, it also
	 * has the identifier and text the host delivered.
	 */
	public static final class Answer {
		private final String id;
		private final String text;
		private final String error;
		private final boolean declined;

		private Answer(String id, String text, String error, boolean declined) {
			this.id = id;
			this.text = text;
			this.error = error;
			this.declined = declined;
		}

		private static Answer ok(String id, String text) {
			return new Answer(id, text, null, false);
		}

		private static Answer failed(String error) {
			return new Answer(null, null, error == null ? "" : error, false);
		}

		private static Answer declinedByHost() {
			return new Answer(null, null, null, true);
		}

		public String getId() {
			return id;
		}

		public String getText() {
			return text;
		}

		/** The reason of a failure, or <code>null</code> when the resource is there. */
		public String getError() {
			return error;
		}

		/** Whether the host declined the file (its loader returned <code>false</code>). */
		public boolean isDeclined() {
			return declined;
		}
	}

	/** A resource an attempt asked for and did not get. */
	private static final class Wanted {
		private final String type;
		private final String key;
		private final String path;
		private final String from;
		private final String kind;

		private Wanted(String type, String key, String path, String from, String kind) {
			this.type = type;
			this.key = key;
			this.path = path;
			this.from = from;
			this.kind = kind;
		}

		@Override
		public String toString() {
			if (FILE.equals(type))
				return path + (from == null ? "" : " (from " + from + ")");
			return path;
		}
	}

	/**
	 * What the current request has obtained so far, by key. A new request gets a
	 * new map rather than a cleared one, so that a late answer for an older
	 * request lands in a map nobody reads any more.
	 */
	private static Map<String, Answer> answers = new HashMap<String, Answer>();

	/** What the running attempt needed and did not find, in the order asked. */
	private static final Map<String, Wanted> missing = new LinkedHashMap<String, Wanted>();

	/** Console warnings of the running attempt, written only if it is kept. */
	private static List<String> warnings;

	private static boolean attempting;

	private PendingResources() {
	}

	// =========================================================================
	// Attempts (driven by RenderQueue)
	// =========================================================================

	/**
	 * Forgets the answers of the previous request, and a failure to create the
	 * Viz.js instance, which is tried again.
	 */
	static void startRequest() {
		answers = new HashMap<String, Answer>();
		missing.clear();
		GraphVizEngineJs.forgetFailure();
	}

	static void startAttempt() {
		missing.clear();
		warnings = new ArrayList<String>();
		attempting = true;
	}

	/**
	 * Ends the running attempt. The console warnings it produced are written if
	 * it is the one whose result is delivered, and dropped if it is discarded.
	 */
	static void endAttempt(boolean kept) {
		final List<String> pending = warnings;
		warnings = null;
		attempting = false;
		if (kept)
			for (String message : pending)
				nativeWarn(message);
	}

	/**
	 * Whether the running attempt has missed a resource, and will therefore be
	 * discarded: what it computes from now on is wasted. The includes, themes and
	 * libraries are all resolved while the diagram is parsed, so a rendering
	 * checks this after parsing and stops there (see
	 * {@code PlantUMLBrowser.buildSvg}); only Viz.js is asked for later, by the
	 * layout.
	 */
	public static boolean hasMissing() {
		return missing.isEmpty() == false;
	}

	static String describeMissing() {
		final StringBuilder sb = new StringBuilder();
		for (Wanted wanted : missing.values()) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(wanted);
		}
		return sb.toString();
	}

	/**
	 * Writes a warning to the console. During an attempt, it is held until the
	 * attempt is known to be the one that is kept, so that a discarded attempt
	 * does not repeat it.
	 */
	public static void warn(String message) {
		if (warnings != null)
			warnings.add(message);
		else
			nativeWarn(message);
	}

	@JSBody(params = "message", script = "if (typeof console !== 'undefined' && console.warn) console.warn(message);")
	private static native void nativeWarn(String message);

	// =========================================================================
	// Lookups (called by the loaders, during an attempt or not)
	// =========================================================================

	/**
	 * What the request got for a script that is not loaded on the page: a failed
	 * answer, or <code>null</code> when it has not been loaded yet (it is then
	 * noted as missing).
	 */
	static Answer script(String url) {
		return lookup(new Wanted(SCRIPT, SCRIPT + "\n" + url, url, null, null));
	}

	/**
	 * What the host's file loader delivered for this file, or <code>null</code>
	 * when it has not been asked yet (it is then noted as missing).
	 */
	static Answer file(String path, String from, String kind) {
		return lookup(new Wanted(FILE, FILE + "\n" + path + "\n" + from + "\n" + kind, path, from, kind));
	}

	/** Notes that the Viz.js instance is needed and not ready yet. */
	public static void requireViz() {
		lookup(new Wanted(VIZ, VIZ, "Viz.js", null, null));
	}

	private static Answer lookup(Wanted wanted) {
		final Answer answer = answers.get(wanted.key);
		if (answer == null && attempting && missing.containsKey(wanted.key) == false)
			missing.put(wanted.key, wanted);
		return answer;
	}

	// =========================================================================
	// Loading (between two attempts)
	// =========================================================================

	/**
	 * Loads every resource the last attempt missed, with JavaScript callbacks,
	 * and calls <code>done</code> once all of them have answered (or after
	 * {@link #ROUND_TIMEOUT_MS}), always from a later JavaScript task, never from
	 * within this call.
	 */
	static void loadMissing(final ScriptLoaderJs.Ok done) {
		final Map<String, Answer> target = answers;
		final List<Wanted> list = new ArrayList<Wanted>(missing.values());
		missing.clear();
		final Round round = new Round(target, list.size(), done);

		for (Wanted wanted : list)
			start(round, wanted);

		round.timer = setTimeout(new ScriptLoaderJs.Ok() {
			@Override
			public void invoke() {
				round.timer = null;
				for (Wanted wanted : list)
					round.settle(wanted.key, Answer.failed("no answer within " + (ROUND_TIMEOUT_MS / 1000)
							+ " s while loading " + wanted.path));
			}
		}, ROUND_TIMEOUT_MS);
		round.armed = true;
		round.finishIfDone();
	}

	private static void start(final Round round, final Wanted wanted) {
		final String key = wanted.key;
		try {
			if (SCRIPT.equals(wanted.type)) {
				ScriptLoaderJs.loadOnce(wanted.path, () -> round.settle(key, Answer.ok(null, null)),
						message -> round.settle(key, Answer.failed(message)));
			} else if (FILE.equals(wanted.type)) {
				final boolean handled = FileLoaderJs.load(wanted.path, wanted.from, wanted.kind,
						(id, text) -> round.settle(key, Answer.ok(id, text)),
						reason -> round.settle(key, Answer.failed(reason)));
				if (handled == false)
					round.settle(key, Answer.declinedByHost());
			} else {
				GraphVizEngineJs.prepare(() -> round.settle(key, Answer.ok(null, null)));
			}
		} catch (Throwable t) {
			round.settle(key, Answer.failed(String.valueOf(t)));
		}
	}

	/** One loading round: the answers it waits for, and what to do after. */
	private static final class Round {
		private final Map<String, Answer> target;
		private final ScriptLoaderJs.Ok done;
		private int pending;
		private boolean armed;
		private boolean finished;
		private JSObject timer;

		private Round(Map<String, Answer> target, int pending, ScriptLoaderJs.Ok done) {
			this.target = target;
			this.pending = pending;
			this.done = done;
		}

		/** Records the first answer for a key; any later one is ignored. */
		private void settle(String key, Answer answer) {
			if (finished || target.containsKey(key))
				return;
			target.put(key, answer);
			pending--;
			finishIfDone();
		}

		private void finishIfDone() {
			// Not before every loader has been started and the timer armed: an answer
			// given synchronously must not end the round while it is being set up.
			if (finished || armed == false || pending > 0)
				return;
			finished = true;
			if (timer != null)
				clearTimeout(timer);
			timer = null;
			later(done);
		}
	}

	@JSBody(params = { "callback", "ms" }, script = "return setTimeout(function() { callback(); }, ms);")
	private static native JSObject setTimeout(ScriptLoaderJs.Ok callback, int ms);

	@JSBody(params = "timer", script = "clearTimeout(timer);")
	private static native void clearTimeout(JSObject timer);

	/** Runs the callback from a later task, never from the current one. */
	@JSBody(params = "callback", script = "Promise.resolve().then(function() { callback(); });")
	private static native void later(ScriptLoaderJs.Ok callback);

}
```


**Tool Call: cd /home/david/plantuml/src/main/java/net/sourceforge/plantuml && sed -n '/^public/,$p' teavm/js/browser/ScriptLoaderJs.java | head -150; grep -rn "PendingResources.script\|ScriptLoaderJs\.\(ensure\|require\|need\)" --include=*.java . | head**
Status: Completed

Terminal:
```
public final class ScriptLoaderJs {
	// ::remove file when JAVA8

	@JSFunctor
	public interface Ok extends JSObject {
		void invoke();
	}

	@JSFunctor
	public interface Err extends JSObject {
		void invoke(String message);
	}

	/**
	 * Loads a JS file once. Multiple concurrent calls are coalesced. The state is
	 * stored on the global object.
	 * <p>
	 * Every lazily loaded support script comes through here: the stdlib bundles
	 * ({@code <lib>.min.js}) and also {@code themes.js}, {@code emoji.js} and
	 * {@code openiconic.js}, which all have the same problem this method's two
	 * host-provided globals solve. They are checked in this order:
	 * <ul>
	 * <li>{@code PLANTUML_STDLIB_LOADER}: a function {@code (url, onOk, onErr)}
	 * that delivers the script's content itself, in whatever way fits the host.
	 * For a stdlib bundle it populates {@code PLANTUML_STDLIB} /
	 * {@code PLANTUML_STDLIB_JSON} / {@code PLANTUML_STDLIB_INFO} for the
	 * library before calling {@code onOk}, or calls {@code onErr} with a
	 * message. Returning {@code false} (strictly) declines the URL, and loading
	 * proceeds through the script tag as if no hook were set, so a host that
	 * only handles stdlib bundles can decline {@code themes.js} and friends.
	 * The hook is the only way to load these files where a script tag cannot
	 * work: a Web Worker (no document), a browser extension that may fetch
	 * remote data but not execute remote code, or a non-browser runtime.
	 * Mirrors how a host can pre-populate {@code PLANTUML_THEMES} for
	 * {@link #getTheme(String)}.</li>
	 * <li>{@code PLANTUML_STDLIB_BASE}: a URL prefix for the script tag, so a
	 * page that imports the engine from a CDN can point the loading of all of
	 * these files at wherever they are hosted. Without it the URL stays
	 * relative, which resolves against the consuming document, not the
	 * engine's location.</li>
	 * </ul>
	 * A host may instead pre-populate the globals a script would have set and
	 * mark {@code __pl_script_state[url] = { state: 'loaded' }}; the fast path
	 * then skips loading entirely. With neither global set, the behaviour is
	 * exactly what it always was.
	 */
	@JSBody(params = { "url", "onOk", "onErr" }, script = "var w = (typeof globalThis !== 'undefined') ? globalThis"
			+ " : ((typeof self !== 'undefined') ? self : window);"
			+ "w.__pl_script_state = w.__pl_script_state || Object.create(null);" + "var st = w.__pl_script_state[url];"
			+

			"if (st && st.state === 'loaded') { onOk(); return; }"
			+ "if (st && st.state === 'loading') { st.ok.push(onOk); st.err.push(onErr); return; }" +

			"st = w.__pl_script_state[url] = { state: 'loading', ok: [onOk], err: [onErr] };" +

			"var ok = function() {" + "  st.state = 'loaded';" + "  var list = st.ok; st.ok = []; st.err = [];"
			+ "  for (var i = 0; i < list.length; i++) list[i]();" + "};" +

			"var fail = function(message) {" + "  st.state = 'error';" + "  var list = st.err; st.ok = []; st.err = [];"
			+ "  for (var i = 0; i < list.length; i++) list[i](message);" + "};" +

			"if (typeof w.PLANTUML_STDLIB_LOADER === 'function') {"
			+ "  var handled = w.PLANTUML_STDLIB_LOADER(url, ok, function(message) { fail(message || ('Loader failed for ' + url)); });"
			+ "  if (handled !== false) return;" + "}" +

			"var full = (typeof w.PLANTUML_STDLIB_BASE === 'string') ? (w.PLANTUML_STDLIB_BASE + url) : url;" +

			"var s = document.createElement('script');" + "s.src = full;" + "s.async = true;" +

			"s.onload = ok;" +

			"s.onerror = function() { fail('Failed to load ' + full); };" +

			"document.head.appendChild(s);")
	public static native void loadOnce(String url, Ok onOk, Err onErr);

	/**
	 * Retrieves the raw lines array for a .puml file from a loaded stdlib library.
	 *
	 * @param namespace the library name (e.g. "aws", "c4")
	 * @param path      the relative path within the library (e.g. "compute/ec2")
	 * @return the JS array of lines, or null if not found
	 */
	@JSBody(params = { "namespace",
			"path" }, script = "var g = (typeof globalThis !== 'undefined') ? globalThis"
					+ " : ((typeof self !== 'undefined') ? self : this);"
					+ "var ns = g.PLANTUML_STDLIB && g.PLANTUML_STDLIB[namespace];"
					+ "return (ns && ns[path]) || null;")
	public static native JSObject getRaw_PLANTUML_STDLIB(String namespace, String path);

	/**
	 * Retrieves the raw text of a bundled theme, as published by themes.js into
	 * the PLANTUML_THEMES map.
	 * <p>
	 * Reads from the global object rather than from window, so that a host which
	 * pre-populates PLANTUML_THEMES (on globalThis) can use themes inside a Web
	 * Worker, where there is no document to append a script tag to, or in a
	 * non-browser JS runtime.
	 * @param name the theme name (e.g. "amiga")
	 * @return the whole .puml theme file as a string, or null if not present
	 */
	@JSBody(params = "name", script = "var g = (typeof globalThis !== 'undefined') ? globalThis"
			+ " : ((typeof self !== 'undefined') ? self : this);"
			+ "var t = g && g.PLANTUML_THEMES;" + "return (t && t[name]) ? t[name] : null;")
	public static native String getTheme(String name);

	/**
	 * Whether the PLANTUML_THEMES map exists at all, regardless of its content.
	 * <p>
	 * This is what distinguishes "themes.js was never loaded", which is a problem
	 * with the page's deployment, from "themes.js is loaded but has no theme of
	 * that name", which is a typo in the diagram text.
	 */
	@JSBody(params = {}, script = "var g = (typeof globalThis !== 'undefined') ? globalThis"
			+ " : ((typeof self !== 'undefined') ? self : this);"
			+ "return !!(g && g.PLANTUML_THEMES);")
	public static native boolean hasThemes();

	/**
	 * Writes a warning to the browser console, where the page author will see it.
	 * A warning of a rendering attempt that is discarded is not written (see
	 * {@link PendingResources#warn(String)}).
	 */
	public static void consoleWarn(String message) {
		PendingResources.warn(message);
	}

	/**
	 *
	 * @param namespace
	 * @param path
	 * @return
	 */
	@JSBody(params = { "namespace",
			"path" }, script = "var g = (typeof globalThis !== 'undefined') ? globalThis"
					+ " : ((typeof self !== 'undefined') ? self : this);"
					+ "var ns = g.PLANTUML_STDLIB_JSON && g.PLANTUML_STDLIB_JSON[namespace];"
					+ "return (ns && ns[path]) || null;")
	public static native JSObject getRaw_PLANTUML_STDLIB_JSON(String namespace, String path);

	/**
	 * Retrieves the JSON info object for a loaded stdlib library.
	 * <p>
	 * This reads from {@code window.PLANTUML_STDLIB_INFO[namespace]}, which is
	 * populated by the generated JS files with metadata from each library's
	 * README.md YAML header (name, version, etc.).
	 *
	 * @param namespace the library name (e.g. "aws", "c4")
	 * @return the JS info object, or null if not found
emoji/Emoji.java:177:		if (jsHasEmoji() == false && ScriptLoaderJs.require("emoji.js") == false)
teavm/js/browser/ScriptLoaderJs.java:220:		final PendingResources.Answer answer = PendingResources.script(url);
openiconic/OpenIconic.java:134:		if (jsHasOpeniconic() == false && ScriptLoaderJs.require("openiconic.js") == false)
math/MathResources.java:77:			if (JlatexmathResourcesJs.isAvailable() == false && ScriptLoaderJs.require("jlatexmath.js") == false)
nio/PathSystem.java:199:			return ScriptLoaderJs.require(libname + ".min.js");
theme/ThemeUtils.java:152:				ScriptLoaderJs.require(THEMES_JS);
```


**Tool Call: sed -n 195,229p teavm/js/browser/ScriptLoaderJs.java; sed -n 165,190p emoji/Emoji.java; sed -n 60,95p math/MathResources.java**
Status: Completed

Terminal:
```
	 */
	@JSBody(params = "obj", script = "return obj == null ? null : JSON.stringify(obj);")
	public static native String stringify(JSObject obj);

	@JSBody(params = "url", script = "var g = (typeof globalThis !== 'undefined') ? globalThis"
			+ " : ((typeof self !== 'undefined') ? self : this);"
			+ "var st = g.__pl_script_state && g.__pl_script_state[url];"
			+ "return !!(st && st.state === 'loaded');")
	private static native boolean isLoaded(String url);

	/**
	 * Whether the script is loaded on the page. It never waits: when the script
	 * is not loaded, it is noted as missing for the running attempt, which will be
	 * rendered again once it is (see {@link PendingResources}), and this returns
	 * <code>false</code>, which callers handle like a script that does not
	 * define what they look for.
	 *
	 * @throws RuntimeException with the reason, when loading the script already
	 *                          failed during the current request
	 */
	public static boolean require(String url) {
		// Fast path: already loaded
		if (isLoaded(url))
			return true;

		final PendingResources.Answer answer = PendingResources.script(url);
		if (answer != null && answer.getError() != null)
			throw new RuntimeException(answer.getError());

		return false;
	}

	private ScriptLoaderJs() {
	}
}

	private static volatile boolean emojiJsLoaded = false;

	private static void loadEmojiJsIfNeeded() {
		// ::comment when JAVA8
		if (emojiJsLoaded)
			return;
		// Same as themes (ThemeUtils.loadJsTheme): data that is already registered, by a
		// bundler that inlined emoji.js or by the host (inside a Web Worker, say), is
		// used as is, and emoji.js is only fetched when nothing is there.
		// Never waits: when emoji.js is not loaded yet, this rendering attempt goes on
		// without emoji and is rendered again once it is (PendingResources).
		if (jsHasEmoji() == false && ScriptLoaderJs.require("emoji.js") == false)
			return;
		emojiJsLoaded = true;
		// ::done
	}

	// ::comment when JAVA8
	// The data is read from the global object rather than from window, so that it
	// can be registered where there is no window (a Web Worker). Same lookup as
	// ScriptLoaderJs.getTheme().
	private static final String GLOBAL = "var g = (typeof globalThis !== 'undefined') ? globalThis"
			+ " : ((typeof self !== 'undefined') ? self : this);";

	@JSBody(script = GLOBAL + "return !!(g && g.PLANTUML_EMOJI);")

	static synchronized void ensure() {
		// Done once (and again if something took the provider away)
		if (TeXResources.getProvider() != null)
			return;

		// The marker is tested here, so that TeaVM prunes the class loader code
		// ::comment when JAVA8
		if (TeaVM.isC()) {
			TeXResources.setProvider(new JlatexmathResources());
			return;
		}
		// Browser: the resources come from jlatexmath.js (loaded on demand, like
		// emoji.js). It never waits: when the script is not there yet, this attempt
		// stops here (the formula is shown as text) and the diagram is rendered
		// again once the script is loaded (PendingResources).
		if (TeaVM.isTeaVM()) {
			if (JlatexmathResourcesJs.isAvailable() == false && ScriptLoaderJs.require("jlatexmath.js") == false)
				throw new IllegalStateException("jlatexmath.js is being loaded");
			TeXResources.setProvider(new JlatexmathResourcesJs());
			return;
		}
		// ::done
		TeXResources.setProvider(new TeXClassLoaderResources());
	}

}
```


## Assistant

<thinking>
Lazy loading fits here: elkjs can load on demand like jlatexmath.js and emoji.js, so its 1.6 MB is fetched only for `!pragma layout elk`. I'm checking the build task, the headless entry, and the sjpp Java 8 markers next.


</thinking>

**Tool Call: cd /home/david/plantuml && grep -n -i "headless\|viz-global\|jlatexmath.js\|emoji.js\|copyTeavm\|register<Copy>\|tasks.named(\"teavm\|generateJavaScript\|from(\"src/main/resources/teavm\|index.html\|node_modules\|npm" build.gradle.kts | head -40; ls src/main/resources/teavm* src/teavm* 2>/dev/null | head -30**
Status: Completed

Terminal:
```
155:val copyNativeSources = tasks.register<Copy>("copyNativeSources") {
235:tasks.matching { it.name == "generateJavaScript" || it.name == "generateWasmGC" || it.name == "generateC" }.configureEach {
703:		// Write the index.html file
704:		val indexHtml = file("$siteDir/index.html")
765:		println("Open: ${siteDir.absolutePath}/index.html")
920:	// The official plugin provides the 'generateJavaScript' task
921:	dependsOn("generateJavaScript")
929:		// FLAT: <lib>.min.js, themes.js, viz-global.js... sit next to
934:			from(teavmSrcDir.dir("generated"))  // themes.js, emoji.js, openiconic.js, jlatexmath.js (GPL: not in the other license variants)
937:				// viz-global.js is historically loaded from the root
938:				// (<script src="viz-global.js">); the rest stays under vendor/.
939:				include("viz-global.js")
942:				exclude("viz-global.js")
970:		println("TeaVM Ready!  --> ${outputDir.absolutePath}/index.html")
982:// companion files (viz-global.js, themes.js, <lib>.min.js...):
1059:// npm package - assemble a publishable npm package for the JS engine
1062:// Produces build/npm-plantuml/, a self-contained npm package exposing the
1063:// TeaVM-compiled PlantUML engine (issue #2715: publish js-plantuml on npm so
1067:// (viz-global.js) and the demo/playground pages, but NOT the heavy optional
1072://   gradlew npmPackage           # assemble build/npm-plantuml
1073://   cd build/npm-plantuml
1074://   npm publish --access public  # done manually by the maintainer
1076:tasks.register("npmPackage") {
1077:	description = "Assembles a publishable npm package for the TeaVM JS engine (issue #2715)."
1082:	val outputDir = layout.buildDirectory.dir("npm-plantuml")
1089:		// Determine the npm version (must be valid semver).
1093:		// with -PnpmVersion=... so the npm package can be released as stable
1096:		//   gradlew npmPackage -PnpmVersion=1.2026.5
1099:		val npmVersion = (project.findProperty("npmVersion") as String?)?.trim()?.takeIf { it.isNotEmpty() }
1104:		val isPrerelease = npmVersion.contains("-")
1106:		// npm package name. Defaults to the scoped name on the 'plantuml' org
1108:		// -PnpmName=... if needed.
1109:		val npmName = (project.findProperty("npmName") as String?)?.trim()?.takeIf { it.isNotEmpty() }
1114:		// zoom.js, and index.html loads vendor/fflate-*.min.js: without them the
1118:			"viz-global.js",
1119:			"emoji.js",
1120:			"jlatexmath.js",
1130:			"index.html",
1149:			  "name": "$npmName",
1150:			  "version": "$npmVersion",
src/teavm-c:
CMakeLists.txt
expected-hello.txt
native
README.md
```


**Tool Call: sed -n 900,1000p build.gradle.kts; grep -n "teavmSrcDir\s*=" build.gradle.kts; **
Status: Completed

Terminal:
```
}

// ============================================
// TeaVM tasks - build JS version with HTML file
// ============================================

// Sources of the files shipped next to plantuml.js (issue #2870):
//   web/        hand-written (HTML pages, main.js, CSS, preview/...)
//   generated/  generated by PlantUML itself (ThemesJsGenerator, Emoji, OpenIconic)
//   stdlib/     <lib>.min.js generated by plantuml-stdlib
//   vendor/     third-party code (Viz.js, fflate)
// Kept outside src/main/resources so that none of it lands on the Java
// classpath or is copied by processResources.
val teavmSrcDir = layout.projectDirectory.dir("src/main/teavm")

// Custom task to prepare TeaVM JS version with HTML file
tasks.register("teavm") {
	description = "Prepares TeaVM JS version with HTML file"
	group = "teavm"
	
	// The official plugin provides the 'generateJavaScript' task
	dependsOn("generateJavaScript")
	
	val outputDir = teavmJsOutputDir.get().asFile
	
	doLast {
		// Assemble the companion files next to plantuml.js (without erasing
		// existing files). The sources are split by origin under src/main/teavm
		// (see its README.md and issue #2870), but the deployed layout stays
		// FLAT: <lib>.min.js, themes.js, viz-global.js... sit next to
		// plantuml.js, which is what ScriptLoaderJs, PLANTUML_STDLIB_BASE,
		// PLANTUML_STDLIB_LOADER hooks and the integration docs rely on.
		copy {
			from(teavmSrcDir.dir("web"))        // hand-written pages/scripts (keeps preview/)
			from(teavmSrcDir.dir("generated"))  // themes.js, emoji.js, openiconic.js, jlatexmath.js (GPL: not in the other license variants)
			from(teavmSrcDir.dir("stdlib"))     // <lib>.min.js from plantuml-stdlib
			from(teavmSrcDir.dir("vendor")) {   // third-party code
				// viz-global.js is historically loaded from the root
				// (<script src="viz-global.js">); the rest stays under vendor/.
				include("viz-global.js")
			}
			from(teavmSrcDir.dir("vendor")) {
				exclude("viz-global.js")
				into("vendor")
			}
			into(outputDir)
		}

		// List all produced .js files with human-readable sizes
		fun sizeInMB(bytes: Long): String {
			return "%.2f MB".format(bytes.toDouble() / (1024.0 * 1024.0))
		}

		val jsFiles = outputDir.listFiles { f -> f.isFile && f.name.endsWith(".js") }
			?.sortedBy { it.name.lowercase() }
		if (jsFiles != null && jsFiles.isNotEmpty()) {
			val nameWidth = maxOf(jsFiles.maxOf { it.name.length }, "TOTAL".length)
			val totalSize = jsFiles.sumOf { it.length() }
			val sizeWidth = sizeInMB(totalSize).length

			println("")
			println("TeaVM JS files:")
			for (f in jsFiles) {
				println("  %-${nameWidth}s  %${sizeWidth}s".format(f.name, sizeInMB(f.length())))
			}
			println("  %-${nameWidth}s  %${sizeWidth}s".format("TOTAL", sizeInMB(totalSize)))
		}

		println("")
		println("======================")
		println("TeaVM Ready!  --> ${outputDir.absolutePath}/index.html")
		println("======================")
		println("")
	}
}

// ============================================
// TeaVM Wasm GC (experimental) - same engine, compiled to WebAssembly
// ============================================
//
// Assembles the Wasm GC build INTO the JS output directory, so that a single
// deployment folder offers both engines side by side, sharing the same
// companion files (viz-global.js, themes.js, <lib>.min.js...):
//
//   build/generated/teavm/js/
//     plantuml.js                 <- JS engine (unchanged)
//     plantuml.wasm               <- Wasm GC engine
//     plantuml.wasm-runtime.js    <- TeaVM Wasm GC runtime (ES module)
//     plantuml-wasm.js            <- same API as plantuml.js (render, renderToString)
//     index-wasm.html, bench.html <- demo page and JS-vs-Wasm benchmark
//
// Usage:
//   gradlew teavmWasm
//   cd build/generated/teavm/js && python3 -m http.server 8080
//   open http://localhost:8080/bench.html
val teavmWasmGcOutputDir = layout.buildDirectory.dir("generated/teavm/wasm-gc")

tasks.register("teavmWasm") {
	description = "Builds the experimental Wasm GC engine next to the TeaVM JS version"
	group = "teavm"

913:val teavmSrcDir = layout.projectDirectory.dir("src/main/teavm")
```


**Tool Call: ls src/main/teavm src/main/teavm/vendor; cat src/main/teavm/README.md 2>/dev/null | head -60; git log --oneline -3 -- src/main/teavm/vendor**
Status: Completed

Terminal:
```
src/main/teavm:
generated
README.md
stdlib
vendor
web
web-wasm

src/main/teavm/vendor:
fflate-0.8.2.min.js
fflate-LICENSE.txt
viz-global.js
# `src/main/teavm` — files shipped next to `plantuml.js`

The browser (TeaVM) build produces a single engine file, `plantuml.js`. This
folder holds everything that is deployed **next to it**: demo pages, companion
scripts loaded at runtime, stdlib bundles and third-party code.

The files are split by **origin**, so that it is obvious which ones can be edited
by hand and which ones must be regenerated (issue
[#2870](https://github.com/plantuml/plantuml/issues/2870)).

| Folder | Origin | Edit by hand? |
|---|---|---|
| [`web/`](web/) | Hand-written: HTML pages, `main.js`, CSS, favicons, `preview/`, `GITHUB_INTEGRATION.md` | **Yes** |
| [`generated/`](generated/) | Generated by PlantUML itself (see below) | No — re-run the generator |
| [`stdlib/`](stdlib/) | `<lib>.min.js` generated by [plantuml-stdlib](https://github.com/plantuml/plantuml-stdlib) | No — re-sync from plantuml-stdlib |
| [`vendor/`](vendor/) | Third-party code, kept with its license | No — replace with an upstream release |
| [`web-wasm/`](web-wasm/) | Hand-written, **experimental** Wasm GC build only: `plantuml-wasm.js`, `index-wasm.html`, `bench.html` | **Yes** |

## Deployed layout is flat

The source layout above is **not** the deployed layout. `gradlew teavm`
assembles everything into `build/generated/teavm/js/`, flat, exactly as before:

```
build/generated/teavm/js/
  plantuml.js              <- TeaVM output
  index.html, main.js...   <- web/        (web/preview/ stays preview/)
  themes.js, emoji.js...   <- generated/
  c4.min.js, ibm.min.js... <- stdlib/
  viz-global.js            <- vendor/viz-global.js (loaded from the root)
  vendor/fflate-*.min.js   <- vendor/     (everything else stays under vendor/)
```

This flat layout is a public contract: `ScriptLoaderJs` fetches
`<lib>.min.js`, `themes.js`, `emoji.js` and `openiconic.js` relative to the page
(or to `PLANTUML_STDLIB_BASE`), `PLANTUML_STDLIB_LOADER` hooks receive those bare
names, and the integration docs tell hosts to deploy the files side by side.
**Do not change it** when reorganizing this folder: only the Gradle copy rules
(`teavm` task in the root `build.gradle.kts`, `npmPackage` in
`license-variants/plantuml-mit/build.gradle.kts`) need to know about the subfolders.

This folder is deliberately **outside** `src/main/resources`: none of these files
belong on the Java classpath, and keeping them out spares `processResources`
from copying ~100 MB on every build.

## Regenerating

**`generated/`** — run from the repository root:

| File | Generator |
|---|---|
| `themes.js` | `java net.sourceforge.plantuml.theme.ThemesJsGenerator` (guarded by `ThemesJsTest`) |
| `emoji.js` | `Emoji.main()` (generator code is commented out in `net.sourceforge.plantuml.emoji.Emoji`) |
| `jlatexmath.js` | `java net.sourceforge.plantuml.math.JlatexmathJsGenerator` (guarded by `JlatexmathJsTest`; GPL, not in the MIT package) |
| `openiconic.js` | `OpenIconic.main()` (generator code is commented out in `net.sourceforge.plantuml.openiconic.OpenIconic`) |

**`stdlib/`** — in a plantuml-stdlib checkout, `gradlew run` (which chains
`runJs` and `minifyJavaScript`) writes the bundles to `output-js-min/`; copy the
`*.min.js` files from there into `stdlib/`. The Java counterpart of the same
libraries lives in `src/main/resources/stdlib/` (`.spm` files).
8f06f6053 ♻️ Split TeaVM companion files by origin under src/main/teavm (#2870)
```


**Tool Call: grep -n -i "elk\|viz-global\|vendor" license-variants/README.md license-variants/*/build.gradle.kts LICENSES.md 2>/dev/null | head -20**
Status: Completed

Terminal:
```
license-variants/plantuml-epl/build.gradle.kts:17:	implementation(libs.elk.core)
license-variants/plantuml-epl/build.gradle.kts:18:	implementation(libs.elk.alg.layered)
license-variants/plantuml-epl/build.gradle.kts:19:	implementation(libs.elk.alg.mrtree)
license-variants/plantuml-mit/build.gradle.kts:148:		// index.html loads vendor/fflate-*.min.js (copied below): without them
license-variants/plantuml-mit/build.gradle.kts:151:			"viz-global.js",
license-variants/plantuml-mit/build.gradle.kts:174:			for (sub in listOf("web", "generated", "vendor")) {
license-variants/plantuml-mit/build.gradle.kts:179:			// Same rule as the root `teavm` task: apart from viz-global.js
license-variants/plantuml-mit/build.gradle.kts:180:			// (loaded from the root), third-party code keeps its vendor/ folder,
license-variants/plantuml-mit/build.gradle.kts:182:			from(teavmSrcDir.dir("vendor")) {
license-variants/plantuml-mit/build.gradle.kts:183:				exclude("viz-global.js")
license-variants/plantuml-mit/build.gradle.kts:184:				into("vendor")
```


## Assistant

<thinking>
ELK is only in the root build and the EPL variant, so I'm checking that elkjs (EPL-2.0) doesn't leak into the MIT npm package via the rule that copies all of `vendor/`.


</thinking>

**Tool Call: sed -n 135,200p license-variants/plantuml-mit/build.gradle.kts**
Status: Completed

Terminal:
```
		// canonical "plantuml.js" expected by the bundle and by CDN consumers.
		copy {
			from(teavmJsOutputDir) {
				include("plantuml-mit.js")
				rename { "plantuml.js" }
			}
			into(pkgDir)
		}

		// Companion files that make up the package, copied verbatim from the
		// shared teavm sources. Everything the demo pages reference, minus the
		// heavy stdlib bundles (and minus plantuml.js, supplied above from the
		// MIT build). main.js imports plantuml-codec.js and zoom.js, and
		// index.html loads vendor/fflate-*.min.js (copied below): without them
		// the playground cannot start when opened from the package.
		val include = listOf(
			"viz-global.js",
			"emoji.js",
			"openiconic.js",
			"themes.js",
			"main.js",
			"plantuml-codec.js",
			"zoom.js",
			"main.css",
			"favicon.svg",
			"favicon.ico",
			"index.html",
			"index-basic.html",
			"index-basic-dark.html",
			"index-collection.html",
			"github-integration-poc.html",
			"github-integration-web-worker-poc.html",
			"GITHUB_INTEGRATION.md"
		)

		// The companion files are split by origin under the root
		// src/main/teavm tree (issue #2870); the package itself stays flat.
		val teavmSrcDir = rootProject.layout.projectDirectory.dir("src/main/teavm")
		copy {
			for (sub in listOf("web", "generated", "vendor")) {
				from(teavmSrcDir.dir(sub)) {
					include(*include.toTypedArray())
				}
			}
			// Same rule as the root `teavm` task: apart from viz-global.js
			// (loaded from the root), third-party code keeps its vendor/ folder,
			// license included.
			from(teavmSrcDir.dir("vendor")) {
				exclude("viz-global.js")
				into("vendor")
			}
			into(pkgDir)
		}

		// Wasm GC engine: the .wasm and its runtime come from this subproject, the
		// wrapper (same API as plantuml.js) from the shared teavm sources. The
		// wrapper imports "./plantuml.wasm-runtime.js" and fetches "./plantuml.wasm"
		// by relative URL, so the three files must stay side by side.
		if (npmWasm)
			copy {
				from(teavmWasmOutputDir) {
					include("plantuml.wasm", "plantuml.wasm-runtime.js")
				}
				from(teavmSrcDir.dir("web-wasm")) {
					include("plantuml-wasm.js")
				}
```


## Assistant

I'll put elkjs in `vendor/`, deploy it at the root like `viz-global.js`, and exclude it from the MIT npm package. Next, PlantUML's own JSON library, for the Java side of the round trip:

**Tool Call: cd /home/david/plantuml/src/main/java/net/sourceforge/plantuml && ls json/ | head -30; grep -n "public static .*parse\|public .* get(\|asDouble\|asArray\|asObject\|asString\|public JsonObject add(String name, \(double\|String\|boolean\|JsonValue\)" json/Json.java json/JsonValue.java json/JsonObject.java | head -30; grep -rln "net.sourceforge.plantuml.json" teavm | head -3**
Status: Completed

Terminal:
```
JsonArray.java
JsonHandler.java
Json.java
JsonLiteral.java
JsonNumber.java
JsonObject.java
JsonParser.java
JsonString.java
JsonValue.java
JsonWriter.java
Location.java
package-info.java
ParseException.java
PrettyPrint.java
readme.md
WriterConfig.java
WritingBuffer.java
json/JsonObject.java:147:	 * @deprecated Use {@link Json#parse(Reader)}{@link JsonValue#asObject()
json/JsonObject.java:148:	 *             .asObject()} instead
json/JsonObject.java:152:		return JsonValue.readFrom(reader).asObject();
json/JsonObject.java:163:	 * @deprecated Use {@link Json#parse(String)}{@link JsonValue#asObject()
json/JsonObject.java:164:	 *             .asObject()} instead
json/JsonObject.java:168:		return JsonValue.readFrom(string).asObject();
json/JsonObject.java:271:	public JsonObject add(String name, double value) {
json/JsonObject.java:293:	public JsonObject add(String name, boolean value) {
json/JsonObject.java:315:	public JsonObject add(String name, String value) {
json/JsonObject.java:337:	public JsonObject add(String name, JsonValue value) {
json/JsonObject.java:607:	public JsonValue get(String name) {
json/JsonObject.java:688:		return value != null ? value.asDouble() : defaultValue;
json/JsonObject.java:725:		return value != null ? value.asString() : defaultValue;
json/JsonObject.java:811:	public JsonObject asObject() {
json/JsonValue.java:60: * If the type of a JSON value is known, the methods {@link #asObject()},
json/JsonValue.java:61: * {@link #asArray()}, {@link #asString()}, {@link #asInt()}, etc. can be used
json/JsonValue.java:293:	public JsonObject asObject() {
json/JsonValue.java:304:	public JsonArray asArray() {
json/JsonValue.java:376:	public double asDouble() {
json/JsonValue.java:387:	public String asString() {
json/Json.java:38: * JsonObject object = Json.parse(string).asObject();
json/Json.java:288:	public static JsonValue parse(String string) {
json/Json.java:311:	public static JsonValue parse(Reader reader) throws IOException {
teavm/js/PSystemBuilderJs.java
teavm/c/PSystemBuilderC.java
```


## Assistant

<thinking>
The minimal-json library already in the tree is used by the TeaVM code, so the Java side can handle the serialization. I found that the EnumSet branch in ElkWithProperty.setProperty adds proxy enums rather than real ELK ones, which may be what the VERY_STRANGE_OFFSET hack compensates for; I'll leave JVM behaviour alone and test which encoding makes elkjs match it.


</thinking>

**Tool Call: cd /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy && sed -n 188,260p Reflect.java; sed -n 1,35p graph/ElkNode.java**
Status: Completed

Terminal:
```
		throw new IllegalArgumentException();
	}

	public static Object call(Object instance, String method) {
		try {
			final Method m = instance.getClass().getMethod(method);
			return m.invoke(instance);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object call(Object instance, String method, Object arg1) {
		try {
			final Method m = instance.getClass().getMethod(method, arg1.getClass());
			return m.invoke(instance, arg1);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object call(Object instance, String method, Object arg1, Object arg2) {
		try {
			final Method m = instance.getClass().getMethod(method, arg1.getClass(), arg2.getClass());
			return m.invoke(instance, arg1, arg2);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Object call2(Object instance, String method, Object arg1, Object arg2) {
		try {
			final Method m = getMethod(instance, method, 2);
			return m.invoke(instance, arg1, arg2);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Method getMethod(Object instance, String method, int nbArgs) {
		for (Method m : instance.getClass().getMethods()) {
			if (m.getName().equals(method) && m.getParameters().length == nbArgs) {
				return m;
			}
		}
		throw new IllegalArgumentException();
	}

}
package net.sourceforge.plantuml.elk.proxy.graph;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.plantuml.elk.proxy.Reflect;

public class ElkNode extends ElkWithProperty {

	public ElkNode(Object obj) {
		super(obj);
	}

	public ElkNode getParent() {
		final Object tmp = Reflect.call(obj, "getParent");
		if (tmp == null) {
			return null;
		}
		return new ElkNode(tmp);
	}

	public double getX() {
		return (Double) Reflect.call(obj, "getX");
	}

	public double getY() {
		return (Double) Reflect.call(obj, "getY");
	}

	public Collection<ElkLabel> getLabels() {
		final Collection<ElkLabel> result = new ArrayList<>();
		final Collection internal = (Collection) Reflect.call(obj, "getLabels");
		for (Object element : internal) {
			result.add(new ElkLabel(element));
		}
```


## Assistant

Before writing the Java side, I'll check how elkjs parses the option values I plan to send: padding strings, enum sets, and no explicit algorithm.

**Tool Call: cat > /tmp/elkspike/opts.js <<'EOF'
const ELK = require(process.argv[2]);
const elk = new ELK(); const fw = elk.worker.worker, d = fw.dispatcher;
function call(msg) { let out; const s = fw.onmessage; fw.onmessage = m => { out = m.data; };
  try { d.saveDispatch({ data: msg }); } finally { fw.onmessage = s; } if (out.error) throw out.error; return out.data; }
call({ cmd: 'register', id: -1, algorithms: ['layered'] });
function run(place, pad) {
  const g = { id: 'root', layoutOptions: { 'org.eclipse.elk.direction': 'DOWN', 'org.eclipse.elk.hierarchyHandling': 'INCLUDE_CHILDREN' },
    children: [
      { id: 'a', width: 80, height: 40, labels: [{ id: 'la', text: 'X', width: 80, height: 30, layoutOptions: place === null ? {} : { 'org.eclipse.elk.nodeLabels.placement': place } }] },
      { id: 'g', layoutOptions: { 'org.eclipse.elk.padding': pad, 'org.eclipse.elk.direction': 'DOWN' }, labels: [{ id: 'lg', text: 'C' }],
        children: [{ id: 'c', width: 60, height: 30 }, { id: 'b', width: 60, height: 30 }],
        edges: [{ id: 'e0', sources: ['c'], targets: ['b'] }] }],
    edges: [{ id: 'e1', sources: ['a'], targets: ['c'], layoutOptions: { 'org.eclipse.elk.edgeLabels.inline': true },
      labels: [{ id: 'l1', text: '1', width: 20, height: 12, layoutOptions: { 'org.eclipse.elk.edgeLabels.placement': 'TAIL' } }] }] };
  const r = call({ cmd: 'layout', id: -2, graph: g, layoutOptions: {} });
  const gg = r.children[1];
  return JSON.stringify({ a: [r.children[0].x, r.children[0].y, r.children[0].labels[0].x, r.children[0].labels[0].y], g: [gg.x, gg.y, gg.width, gg.height], c: [gg.children[0].x, gg.children[0].y], e0: gg.edges[0].sections[0], l1: [r.edges[0].labels[0].x, r.edges[0].labels[0].y] });
}
console.log('inside  ', run('[INSIDE, H_CENTER, V_CENTER]', '[top=55.0,left=15.0,bottom=15.0,right=15.0]'));
console.log('spaces  ', run('INSIDE H_CENTER V_CENTER', '[top=55.0,left=15.0,bottom=15.0,right=15.0]'));
console.log('empty   ', run('[]', '[top=55.0,left=15.0,bottom=15.0,right=15.0]'));
console.log('unset   ', run(null, '[top=55.0,left=15.0,bottom=15.0,right=15.0]'));
console.log('pad0    ', run(null, '[top=0.0,left=0.0,bottom=0.0,right=0.0]'));
EOF
node /tmp/elkspike/opts.js /tmp/elkjs-dl/package/lib/elk.bundled.js**
Status: Completed

Terminal:
```
inside   {"a":[17,12,0,5],"g":[12,91,90,150],"c":[15,55],"e0":{"id":"e0_s0","startPoint":{"x":45,"y":85},"endPoint":{"x":45,"y":105},"incomingShape":"c","outgoingShape":"b"},"l1":[60,54]}
spaces   {"a":[17,12,0,5],"g":[12,91,90,150],"c":[15,55],"e0":{"id":"e0_s0","startPoint":{"x":45,"y":85},"endPoint":{"x":45,"y":105},"incomingShape":"c","outgoingShape":"b"},"l1":[60,54]}
empty    {"a":[17,12,0,0],"g":[12,91,90,150],"c":[15,55],"e0":{"id":"e0_s0","startPoint":{"x":45,"y":85},"endPoint":{"x":45,"y":105},"incomingShape":"c","outgoingShape":"b"},"l1":[60,54]}
unset    {"a":[17,12,0,0],"g":[12,91,90,150],"c":[15,55],"e0":{"id":"e0_s0","startPoint":{"x":45,"y":85},"endPoint":{"x":45,"y":105},"incomingShape":"c","outgoingShape":"b"},"l1":[60,54]}
pad0     {"a":[12,12,0,0],"g":[22,91,60,80],"c":[0,0],"e0":{"id":"e0_s0","startPoint":{"x":30,"y":30},"endPoint":{"x":30,"y":50},"incomingShape":"c","outgoingShape":"b"},"l1":[55,54]}
```


## Assistant

<thinking>
Both enum-set formats and the padding format parse, and label placement only moves the node label in this small graph. I'm writing the pure-Java JSON backend next.


</thinking>

**Tool Call: cd /home/david/plantuml/src/main/java/net/sourceforge/plantuml && sed -n 1,35p teavm/js/GraphVizEngineJs.java**
Status: Completed

Terminal:
```
/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2025, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 *
 * If you like this project or if you find it useful, you can support us at:
 *
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 *
 */
package net.sourceforge.plantuml.teavm.js;
```


**Tool Call: cd /home/david/plantuml/src/main/java && grep -rh -A1 "Original Author:" --include=*.java . | grep -v "Original Author\|^--$\|^ \*$" | sort | uniq -c | sort -rn | head -8; grep -rl "David Fyfe" . | head -3**
Status: Completed

Terminal:
```
   1236  *
     17  * Contribution :  Hisashi Miyashita
     17  */
     10  * Contribution: The-Lum
      9  * With assistance from ChatGPT (OpenAI)
      5  * Modified by : Arno Peterson
      5  * Contribution   :  Serge Wenger
      3  * Contribution:  Miguel Esteves
net/sourceforge/plantuml/chart/ChartSeries.java
net/sourceforge/plantuml/chart/ChartDiagramFactory.java
net/sourceforge/plantuml/chart/ScatterRenderer.java
```


**Tool Call: sed -n 28,36p net/sourceforge/plantuml/chart/ChartSeries.java**
Status: Completed

Terminal:
```
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  David Fyfe
 *
 */
package net.sourceforge.plantuml.chart;
```


**Tool Call: cd /home/david/plantuml/src/main/java/net/sourceforge/plantuml && sed -n '/^package/,$p' teavm/TeaVM.java | grep -v "^import"; grep -rn "Class.forName\|getMethod(" --include=*.java . | grep -v "/elk/" | head -5**
Status: Completed

Terminal:
```
package net.sourceforge.plantuml.teavm;

// ::comment when JAVA8
// import org.teavm.interop.PlatformMarker;
// ::done

public class TeaVM {

	/**
	 * Platform marker method used to distinguish TeaVM compilation from standard
	 * JVM execution.
	 *
	 * When compiling with TeaVM, whatever the target (JavaScript or C), this
	 * method is resolved statically to {@code true}. Use {@link #isJavaScript()}
	 * or {@link #isC()} for code that only works on one target. As a result,
	 * conditional branches like:
	 *
	 * <pre>
	 * if (TeaVM.isTeaVM()) {
	 * 	// TeaVM-specific code
	 * } else {
	 * 	// JVM-specific code
	 * }
	 * </pre>
	 *
	 * are evaluated at compile time, and the unreachable branch is completely
	 * removed from the generated JavaScript output (dead code elimination).
	 *
	 * When running on the JVM, this method simply returns {@code false} and no code
	 * elimination occurs: both branches remain present in the bytecode.
	 *
	 * This mechanism is intended purely as a compile-time optimization hint for
	 * TeaVM, not as a runtime platform check.
	 */
	// ::comment when JAVA8
	@org.teavm.interop.PlatformMarker
	// ::done
	public static boolean isTeaVM() {
		return false;
	}

	/**
	 * Platform marker resolved to {@code true} only when TeaVM compiles to
	 * JavaScript, and {@code false} for the Wasm GC build. To guard code that needs
	 * a browser or a JavaScript host ({@code @JSBody}, DOM, canvas, viz.js...) and
	 * must also work in the Wasm GC build, write
	 * {@code TeaVM.isTeaVM() && TeaVM.isC() == false} directly in the condition:
	 * TeaVM only prunes the unreachable branch (and its classes, e.g.
	 * {@code java.security.MessageDigest}) when the markers appear in the method
	 * itself, not behind a helper method.
	 *
	 * Note: TeaVM requires platform markers to be static methods returning
	 * {@code boolean}; this is why there is one method per target rather than a
	 * single method returning an enum (an enum comparison would not be folded at
	 * compile time, so no dead code elimination would happen).
	 *
	 * @see #isTeaVM()
	 */
	// ::comment when JAVA8
	@org.teavm.interop.PlatformMarker(org.teavm.interop.Platforms.JAVASCRIPT)
	// ::done
	public static boolean isJavaScript() {
		return false;
	}

	/**
	 * Platform marker resolved to {@code true} only when TeaVM compiles to C
	 * (native executable, experimental).
	 *
	 * @see #isTeaVM()
	 * @see #isJavaScript()
	 */
	// ::comment when JAVA8
	@org.teavm.interop.PlatformMarker(org.teavm.interop.Platforms.C)
	// ::done
	public static boolean isC() {
		return false;
	}

	/**
	 * Guard method for assertions, ensuring they are removed from the
	 * generated JavaScript output.
	 *
	 * Returns {@code true} on the JVM (allowing the assertion to execute)
	 * and {@code false} under TeaVM (skipping it entirely via dead code
	 * elimination).
	 *
	 * <pre>
	 * if (TeaVM.a()) assert ...
	 * </pre>
	 */
	public static boolean a() {
		return !isTeaVM();
	}

}
brotli/Dictionary.java:42:					// Direct call to DictionaryData instead of using Class.forName.
xmi/XmiCucaDiagramCustom.java:97:            final Method builder = serviceType.getMethod(DIAGRAM_TO_XMI_METHOD_NAME, diagram.getClass());
xmi/XmiCucaDiagramCustom.java:107:            final Method builder = this.serviceType.getMethod(TRANSFORMER_XML_METHOD_NAME, OutputStream.class);
flashcode/FlashCodeFactory.java:46:				final Class cl = Class.forName(name);
vizjs/VizJsEngine.java:45:			final Class classVizJS = Class.forName("ch.braincell.viz.VizJS");
```


## Assistant

<thinking>
Reflection is the main TeaVM risk, since Reflect.java would be compiled into the browser build, so I'm guarding each Reflect method with TeaVM.isTeaVM() to strip it out.


</thinking>

**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonLayouter.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonLayouter.java
```
/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2025, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 *
 * If you like this project or if you find it useful, you can support us at:
 *
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  David Fyfe
 *
 */
package net.sourceforge.plantuml.elk.proxy.json;

/**
 * Something that lays out a graph given in the
 * <a href="https://eclipse.dev/elk/documentation/tooldevelopers/graphdatastructure/jsonformat.html">ELK
 * JSON format</a>, synchronously, and returns the same graph with its
 * coordinates.
 * <p>
 * In the browser, this is elkjs (the official JavaScript build of ELK), see
 * {@code net.sourceforge.plantuml.teavm.js.ElkEngineJs}. On the JVM, ELK itself
 * is called through reflection instead, and no layouter is registered.
 */
public interface ElkJsonLayouter {

	/**
	 * @param graphJson the root node, in the ELK JSON format
	 * @return the same graph with positions, sizes and edge sections, in the ELK
	 *         JSON format
	 * @throws RuntimeException when the layout fails
	 */
	String layout(String graphJson);

}

```


**Tool Call: cd /home/david/plantuml/src/main/java/net/sourceforge/plantuml/json && grep -n "class JsonArray\|public Iterator\|public int size\|public JsonValue get(int" JsonArray.java; grep -n "public boolean isNumber\|public boolean isObject\|public boolean isArray\|public boolean isString\|public String toString()" JsonValue.java**
Status: Completed

Terminal:
```
68:public class JsonArray extends JsonValue implements Iterable<JsonValue> {
387:	public int size() {
409:	public JsonValue get(int index) {
431:	public Iterator<JsonValue> iterator() {
214:	public boolean isObject() {
224:	public boolean isArray() {
233:	public boolean isNumber() {
242:	public boolean isString() {
453:	public String toString() {
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonGraph.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonGraph.java
```
/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2025, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 *
 * If you like this project or if you find it useful, you can support us at:
 *
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  David Fyfe
 *
 */
package net.sourceforge.plantuml.elk.proxy.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.json.Json;
import net.sourceforge.plantuml.json.JsonArray;
import net.sourceforge.plantuml.json.JsonObject;
import net.sourceforge.plantuml.json.JsonValue;

/**
 * A plain Java ELK graph, for the hosts where ELK itself cannot run (the
 * browser): the proxies of {@code net.sourceforge.plantuml.elk.proxy.graph}
 * wrap these objects instead of the real ELK ones. The graph is written in the
 * ELK JSON format, laid out by an {@link ElkJsonLayouter} (elkjs), and the
 * coordinates are read back into the same objects.
 * <p>
 * The coordinates follow the conventions of the ELK Java graph, which
 * {@code CucaDiagramFileMakerElk} relies on: a node is relative to its parent,
 * and an edge (sections and labels) to its containing node. Each edge is
 * written in the JSON node that ELK Java would choose as its container (see
 * {@link #findContainer(Node, Node)}), and with the default JSON coordinate
 * modes ELK writes the edge relative to that very node, so nothing has to be
 * converted.
 * <p>
 * Pure Java on purpose, with no TeaVM dependency, so that it can be tested on
 * the JVM.
 */
public final class ElkJsonGraph {

	private static ElkJsonLayouter layouter;

	private ElkJsonGraph() {
	}

	/**
	 * Registers the layouter. From then on, a new ELK graph is built from these
	 * objects rather than from the ELK Java classes; <code>null</code> goes back to
	 * ELK Java.
	 */
	public static void setLayouter(ElkJsonLayouter newLayouter) {
		layouter = newLayouter;
	}

	public static ElkJsonLayouter getLayouter() {
		return layouter;
	}

	public static Node createGraph() {
		return new Node(null, "root");
	}

	// =========================================================================
	// The graph
	// =========================================================================

	/** What nodes, edges and labels have in common: an id, options and labels. */
	public static abstract class Element {

		private final String id;
		private final Map<String, Object> options = new LinkedHashMap<String, Object>();
		private final List<Label> labels = new ArrayList<Label>();

		private Element(String id) {
			this.id = id;
		}

		public final String getId() {
			return id;
		}

		/**
		 * @param key   the full id of the ELK option (<code>org.eclipse.elk...</code>)
		 * @param value a {@link String}, a {@link Boolean} or a {@link Number}
		 */
		public final void setOption(String key, Object value) {
			if (value instanceof String || value instanceof Boolean || value instanceof Number)
				options.put(key, value);
			else
				throw new IllegalArgumentException(key + "=" + value);
		}

		public final Map<String, Object> getOptions() {
			return Collections.unmodifiableMap(options);
		}

		public final List<Label> getLabels() {
			return Collections.unmodifiableList(labels);
		}

		public final Label createLabel() {
			final Label label = new Label(getRoot().nextId("l"), this);
			labels.add(label);
			return label;
		}

		abstract Node getRoot();

	}

	public static final class Node extends Element {

		private final Node parent;
		private final List<Node> children = new ArrayList<Node>();
		private final List<Edge> edges = new ArrayList<Edge>();
		private int counter;
		private double x;
		private double y;
		private double width;
		private double height;

		private Node(Node parent, String id) {
			super(id);
			this.parent = parent;
		}

		private String nextId(String prefix) {
			return prefix + (counter++);
		}

		@Override
		Node getRoot() {
			Node result = this;
			while (result.parent != null)
				result = result.parent;
			return result;
		}

		public Node createNode() {
			final Node node = new Node(this, getRoot().nextId("n"));
			children.add(node);
			return node;
		}

		public Node getParent() {
			return parent;
		}

		public List<Node> getChildren() {
			return Collections.unmodifiableList(children);
		}

		/** The edges this node contains (not the ones it is connected to). */
		public List<Edge> getContainedEdges() {
			return Collections.unmodifiableList(edges);
		}

		public void setDimensions(double newWidth, double newHeight) {
			this.width = newWidth;
			this.height = newHeight;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}

		private boolean isAncestorOrSelfOf(Node node) {
			for (Node current = node; current != null; current = current.parent)
				if (current == this)
					return true;
			return false;
		}

	}

	public static final class Edge extends Element {

		private final Node source;
		private final Node target;
		private final Node container;
		private final List<Section> sections = new ArrayList<Section>();

		private Edge(String id, Node source, Node target, Node container) {
			super(id);
			this.source = source;
			this.target = target;
			this.container = container;
		}

		@Override
		Node getRoot() {
			return container.getRoot();
		}

		public Node getSource() {
			return source;
		}

		public Node getTarget() {
			return target;
		}

		public Node getContainingNode() {
			return container;
		}

		public List<Section> getSections() {
			return Collections.unmodifiableList(sections);
		}

	}

	public static final class Label extends Element {

		private final Element owner;
		private String text;
		private double x;
		private double y;
		private double width;
		private double height;

		private Label(String id, Element owner) {
			super(id);
			this.owner = owner;
		}

		@Override
		Node getRoot() {
			return owner.getRoot();
		}

		public void setText(String newText) {
			this.text = newText;
		}

		public String getText() {
			return text;
		}

		public void setDimensions(double newWidth, double newHeight) {
			this.width = newWidth;
			this.height = newHeight;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

	}

	public static final class Section {

		private final Point start;
		private final Point end;
		private final List<Point> bendPoints;

		private Section(Point start, Point end, List<Point> bendPoints) {
			this.start = start;
			this.end = end;
			this.bendPoints = Collections.unmodifiableList(bendPoints);
		}

		public Point getStart() {
			return start;
		}

		public Point getEnd() {
			return end;
		}

		public List<Point> getBendPoints() {
			return bendPoints;
		}

	}

	public static final class Point {

		private final double x;
		private final double y;

		private Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

	}

	/**
	 * Creates an edge between two nodes of the same graph, in the node ELK Java
	 * would put it (<code>ElkGraphUtil.createSimpleEdge</code>).
	 */
	public static Edge createSimpleEdge(Node source, Node target) {
		final Node container = findContainer(source, target);
		if (container == null)
			throw new IllegalArgumentException("The nodes are not in the same graph");
		final Edge edge = new Edge(container.getRoot().nextId("e"), source, target, container);
		container.edges.add(edge);
		return edge;
	}

	/**
	 * Same choice as <code>ElkGraphUtil.findBestEdgeContainment</code> of ELK
	 * 0.12 for an edge with one source and one target: a self loop belongs to the
	 * parent of its node, any other edge to the lowest common ancestor of its two
	 * nodes, a node counting as its own ancestor. So an edge between two siblings
	 * belongs to their parent, and an edge from a cluster to one of its
	 * descendants to the cluster.
	 */
	static Node findContainer(Node source, Node target) {
		if (source == target)
			return source.parent;
		for (Node current = source; current != null; current = current.parent)
			if (current.isAncestorOrSelfOf(target))
				return current;
		return null;
	}

	// =========================================================================
	// Layout
	// =========================================================================

	/**
	 * Lays the graph out with the registered layouter, and stores the coordinates
	 * in the nodes, edges and labels.
	 */
	public static void layout(Node root) {
		final ElkJsonLayouter current = layouter;
		if (current == null)
			throw new IllegalStateException("No ELK JSON layouter registered");
		if (root.parent != null)
			throw new IllegalArgumentException("Not a root node");
		applyLayout(root, current.layout(toJson(root)));
	}

	/** The graph, in the ELK JSON format. */
	public static String toJson(Node root) {
		return writeNode(root).toString();
	}

	private static JsonObject writeNode(Node node) {
		final JsonObject result = writeElement(node);
		// ELK Java nodes are 0 x 0 until they are given a size
		result.add("width", node.width);
		result.add("height", node.height);
		if (node.children.isEmpty() == false) {
			final JsonArray children = new JsonArray();
			for (Node child : node.children)
				children.add(writeNode(child));
			result.add("children", children);
		}
		if (node.edges.isEmpty() == false) {
			final JsonArray edges = new JsonArray();
			for (Edge edge : node.edges)
				edges.add(writeEdge(edge));
			result.add("edges", edges);
		}
		return result;
	}

	private static JsonObject writeEdge(Edge edge) {
		final JsonObject result = writeElement(edge);
		result.add("sources", new JsonArray().add(edge.source.getId()));
		result.add("targets", new JsonArray().add(edge.target.getId()));
		return result;
	}

	private static JsonObject writeLabel(Label label) {
		final JsonObject result = writeElement(label);
		if (label.text != null)
			result.add("text", label.text);
		result.add("width", label.width);
		result.add("height", label.height);
		return result;
	}

	private static JsonObject writeElement(Element element) {
		final JsonObject result = new JsonObject();
		result.add("id", element.getId());
		if (element.options.isEmpty() == false) {
			final JsonObject options = new JsonObject();
			for (Map.Entry<String, Object> ent : element.options.entrySet()) {
				final Object value = ent.getValue();
				if (value instanceof Boolean)
					options.add(ent.getKey(), (Boolean) value);
				else if (value instanceof Number)
					options.add(ent.getKey(), ((Number) value).doubleValue());
				else
					options.add(ent.getKey(), (String) value);
			}
			result.add("layoutOptions", options);
		}
		if (element.labels.isEmpty() == false) {
			final JsonArray labels = new JsonArray();
			for (Label label : element.labels)
				labels.add(writeLabel(label));
			result.add("labels", labels);
		}
		return result;
	}

	/**
	 * Reads the coordinates of a laid out graph (the answer of the layouter) into
	 * the graph it was written from. The elements are matched by id.
	 */
	public static void applyLayout(Node root, String layoutJson) {
		final Map<String, Element> byId = new HashMap<String, Element>();
		index(root, byId);
		readNode(Json.parse(layoutJson).asObject(), byId);
	}

	private static void index(Node node, Map<String, Element> byId) {
		byId.put(node.getId(), node);
		indexLabels(node, byId);
		for (Node child : node.children)
			index(child, byId);
		for (Edge edge : node.edges) {
			byId.put(edge.getId(), edge);
			indexLabels(edge, byId);
		}
	}

	private static void indexLabels(Element element, Map<String, Element> byId) {
		for (Label label : element.labels)
			byId.put(label.getId(), label);
	}

	private static <T extends Element> T find(JsonObject json, Map<String, Element> byId, Class<T> type) {
		final String id = json.getString("id", null);
		final Element element = byId.get(id);
		if (type.isInstance(element) == false)
			throw new IllegalArgumentException("Unknown " + type.getSimpleName() + " in the ELK layout: " + id);
		return type.cast(element);
	}

	private static void readNode(JsonObject json, Map<String, Element> byId) {
		final Node node = find(json, byId, Node.class);
		node.x = json.getDouble("x", 0);
		node.y = json.getDouble("y", 0);
		node.width = json.getDouble("width", node.width);
		node.height = json.getDouble("height", node.height);
		readLabels(json, byId);
		for (JsonObject child : objects(json, "children"))
			readNode(child, byId);
		for (JsonObject edge : objects(json, "edges"))
			readEdge(edge, byId);
	}

	private static void readEdge(JsonObject json, Map<String, Element> byId) {
		final Edge edge = find(json, byId, Edge.class);
		edge.sections.clear();
		for (JsonObject section : objects(json, "sections")) {
			final List<Point> bendPoints = new ArrayList<Point>();
			for (JsonObject point : objects(section, "bendPoints"))
				bendPoints.add(readPoint(point));
			edge.sections.add(new Section(readPoint(section.get("startPoint").asObject()),
					readPoint(section.get("endPoint").asObject()), bendPoints));
		}
		readLabels(json, byId);
	}

	private static void readLabels(JsonObject json, Map<String, Element> byId) {
		for (JsonObject labelJson : objects(json, "labels")) {
			final Label label = find(labelJson, byId, Label.class);
			label.x = labelJson.getDouble("x", 0);
			label.y = labelJson.getDouble("y", 0);
			label.width = labelJson.getDouble("width", label.width);
			label.height = labelJson.getDouble("height", label.height);
		}
	}

	private static Point readPoint(JsonObject json) {
		return new Point(json.getDouble("x", 0), json.getDouble("y", 0));
	}

	private static List<JsonObject> objects(JsonObject json, String name) {
		final JsonValue value = json.get(name);
		if (value == null || value.isArray() == false)
			return Collections.emptyList();
		final List<JsonObject> result = new ArrayList<JsonObject>();
		for (JsonValue element : value.asArray())
			result.add(element.asObject());
		return result;
	}

}

```


**Tool Call: grep -n "public JsonArray add(" JsonArray.java; grep -n "public double getDouble(String\|public String getString(String" JsonObject.java**
Status: Completed

Terminal:
```
173:	public JsonArray add(int value) {
185:	public JsonArray add(long value) {
197:	public JsonArray add(float value) {
209:	public JsonArray add(double value) {
221:	public JsonArray add(boolean value) {
233:	public JsonArray add(String value) {
244:	public JsonArray add(JsonValue value) {
686:	public double getDouble(String name, double defaultValue) {
723:	public String getString(String name, String defaultValue) {
```


## Assistant

To be safe under TeaVM, I'll avoid `Class.isInstance` and `Class.cast`:

**Tool Call: Edit src/main/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonGraph.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonGraph.java
```
      byId.put(label.getId(), label);
  }

  private static Element find(JsonObject json, Map<String, Element> byId) {
    final String id = json.getString("id", null);
    final Element element = byId.get(id);
    if (element == null)
      throw new IllegalArgumentException("Unknown id in the ELK layout: " + id);
    return element;
  }

  private static void readNode(JsonObject json, Map<String, Element> byId) {
    final Node node = (Node) find(json, byId);
    node.x = json.getDouble("x", 0);
    node.y = json.getDouble("y", 0);
    node.width = json.getDouble("width", node.width);
```


**Tool Call: Edit src/main/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonGraph.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonGraph.java
```
  }

  private static void readEdge(JsonObject json, Map<String, Element> byId) {
    final Edge edge = (Edge) find(json, byId);
    edge.sections.clear();
    for (JsonObject section : objects(json, "sections")) {
      final List<Point> bendPoints = new ArrayList<Point>();
```


**Tool Call: Edit src/main/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonGraph.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonGraph.java
```

  private static void readLabels(JsonObject json, Map<String, Element> byId) {
    for (JsonObject labelJson : objects(json, "labels")) {
      final Label label = (Label) find(labelJson, byId);
      label.x = labelJson.getDouble("x", 0);
      label.y = labelJson.getDouble("y", 0);
      label.width = labelJson.getDouble("width", label.width);
```


## Assistant

Now the proxy classes. Option keys become descriptors that know both their ELK Java field and their JSON id:

**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/ElkProperty.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/ElkProperty.java
```
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

```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/core/options/CoreOptions.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/core/options/CoreOptions.java
```
package net.sourceforge.plantuml.elk.proxy.core.options;

import net.sourceforge.plantuml.elk.proxy.ElkProperty;

public class CoreOptions {

  private static final String CORE_OPTIONS = "org.eclipse.elk.core.options.CoreOptions";

  public static final ElkProperty DIRECTION = new ElkProperty(CORE_OPTIONS, "DIRECTION", "org.eclipse.elk.direction");

  public static final ElkProperty EDGE_LABELS_INLINE = new ElkProperty(CORE_OPTIONS, "EDGE_LABELS_INLINE",
      "org.eclipse.elk.edgeLabels.inline");

  public static final ElkProperty NODE_SIZE_CONSTRAINTS = new ElkProperty(CORE_OPTIONS, "NODE_SIZE_CONSTRAINTS",
      "org.eclipse.elk.nodeSize.constraints");

  public static final ElkProperty HIERARCHY_HANDLING = new ElkProperty(CORE_OPTIONS, "HIERARCHY_HANDLING",
      "org.eclipse.elk.hierarchyHandling");

  public static final ElkProperty EDGE_LABELS_PLACEMENT = new ElkProperty(CORE_OPTIONS, "EDGE_LABELS_PLACEMENT",
      "org.eclipse.elk.edgeLabels.placement");

  public static final ElkProperty EDGE_TYPE = new ElkProperty(CORE_OPTIONS, "EDGE_TYPE", "org.eclipse.elk.edge.type");

  public static final ElkProperty NODE_LABELS_PLACEMENT = new ElkProperty(CORE_OPTIONS, "NODE_LABELS_PLACEMENT",
      "org.eclipse.elk.nodeLabels.placement");

  public static final ElkProperty NODE_SIZE_OPTIONS = new ElkProperty(CORE_OPTIONS, "NODE_SIZE_OPTIONS",
      "org.eclipse.elk.nodeSize.options");

  public static final ElkProperty PADDING = new ElkProperty(CORE_OPTIONS, "PADDING", "org.eclipse.elk.padding");

}
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/core/options/Direction.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/core/options/Direction.java
```
package net.sourceforge.plantuml.elk.proxy.core.options;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public enum Direction implements ElkObjectProxy {
  DOWN;

  @Override
  public Enum getTrueObject() {
    return Reflect.getEnum("org.eclipse.elk.core.options.Direction", name());
  }

}
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/core/options/HierarchyHandling.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/core/options/HierarchyHandling.java
```
package net.sourceforge.plantuml.elk.proxy.core.options;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public enum HierarchyHandling implements ElkObjectProxy {
  INCLUDE_CHILDREN;

  @Override
  public Enum getTrueObject() {
    return Reflect.getEnum("org.eclipse.elk.core.options.HierarchyHandling", name());
  }

}
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/core/math/ElkPadding.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/core/math/ElkPadding.java
```
import net.sourceforge.plantuml.elk.proxy.Reflect;

public class ElkPadding implements ElkObjectProxy {

  private final double top;
  private final double right;
  private final double bottom;
  private final double left;
  private Object obj;

  public ElkPadding(double top, double right, double bottom, double left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  @Override
  public synchronized Object getTrueObject() {
    if (obj == null)
      obj = Reflect.newInstance("org.eclipse.elk.core.math.ElkPadding", top, right, bottom, left);
    return obj;
  }

  /** The same text as <code>org.eclipse.elk.core.math.ElkPadding.toString()</code>, which ELK parses back. */
  public String toJsonValue() {
    return "[top=" + top + ",left=" + left + ",bottom=" + bottom + ",right=" + right + "]";
  }

}
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/core/util/NullElkProgressMonitor.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/core/util/NullElkProgressMonitor.java
```
package net.sourceforge.plantuml.elk.proxy.core.util;

import net.sourceforge.plantuml.elk.proxy.ElkObjectProxy;
import net.sourceforge.plantuml.elk.proxy.Reflect;

public class NullElkProgressMonitor implements ElkObjectProxy {

  @Override
  public Object getTrueObject() {
    return Reflect.newInstance("org.eclipse.elk.core.util.NullElkProgressMonitor");
  }

}
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/core/RecursiveGraphLayoutEngine.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/core/RecursiveGraphLayoutEngine.java
```
import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.core.util.NullElkProgressMonitor;
import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class RecursiveGraphLayoutEngine {

  public void layout(ElkNode root, NullElkProgressMonitor monitor) {
    if (root.obj instanceof ElkJsonGraph.Node) {
      ElkJsonGraph.layout((ElkJsonGraph.Node) root.obj);
      return;
    }
    final Object engine = Reflect.newInstance("org.eclipse.elk.core.RecursiveGraphLayoutEngine");
    Reflect.call2(engine, "layout", root.obj, monitor.getTrueObject());
  }

}
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/graph/util/ElkGraphUtil.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/util/ElkGraphUtil.java
```
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

/**
 * The graph is made of real ELK objects (through reflection) or, when an ELK
 * JSON layouter is registered, of {@link ElkJsonGraph} objects. Only
 * {@link #createGraph()} chooses; everything else is created next to what it
 * is attached to.
 */
public class ElkGraphUtil {

  private static final String ELK_GRAPH_UTIL = "org.eclipse.elk.graph.util.ElkGraphUtil";

  public static ElkLabel createLabel(ElkEdge edge) {
    if (edge.obj instanceof ElkJsonGraph.Edge)
      return new ElkLabel(((ElkJsonGraph.Edge) edge.obj).createLabel());
    return new ElkLabel(Reflect.callStatic2(ELK_GRAPH_UTIL, "createLabel", edge.obj));
  }

  public static ElkLabel createLabel(ElkNode node) {
    if (node.obj instanceof ElkJsonGraph.Node)
      return new ElkLabel(((ElkJsonGraph.Node) node.obj).createLabel());
    return new ElkLabel(Reflect.callStatic2(ELK_GRAPH_UTIL, "createLabel", node.obj));
  }

  public static ElkNode createNode(ElkNode root) {
    if (root.obj instanceof ElkJsonGraph.Node)
      return new ElkNode(((ElkJsonGraph.Node) root.obj).createNode());
    return new ElkNode(Reflect.callStatic2(ELK_GRAPH_UTIL, "createNode", root.obj));
  }

  public static ElkEdge createSimpleEdge(ElkNode node1, ElkNode node2) {
    if (node1.obj instanceof ElkJsonGraph.Node)
      return new ElkEdge(
          ElkJsonGraph.createSimpleEdge((ElkJsonGraph.Node) node1.obj, (ElkJsonGraph.Node) node2.obj));
    return new ElkEdge(Reflect.callStatic2(ELK_GRAPH_UTIL, "createSimpleEdge", node1.obj, node2.obj));
  }

  public static ElkNode createGraph() {
    if (ElkJsonGraph.getLayouter() != null)
      return new ElkNode(ElkJsonGraph.createGraph());
    return new ElkNode(Reflect.callStatic(ELK_GRAPH_UTIL, "createGraph"));
  }

}
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkWithProperty.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkWithProperty.java
```
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
```


Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkWithProperty.java
```
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
```


Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkWithProperty.java
```
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
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkNode.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkNode.java
```
import java.util.Collection;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class ElkNode extends ElkWithProperty {

```


Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkNode.java
```
    super(obj);
  }

  private ElkJsonGraph.Node json() {
    return obj instanceof ElkJsonGraph.Node ? (ElkJsonGraph.Node) obj : null;
  }

  public ElkNode getParent() {
    final ElkJsonGraph.Node json = json();
    final Object tmp = json != null ? json.getParent() : Reflect.call(obj, "getParent");
    if (tmp == null) {
      return null;
    }
```


Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkNode.java
```
  }

  public double getX() {
    final ElkJsonGraph.Node json = json();
    if (json != null)
      return json.getX();
    return (Double) Reflect.call(obj, "getX");
  }

  public double getY() {
    final ElkJsonGraph.Node json = json();
    if (json != null)
      return json.getY();
    return (Double) Reflect.call(obj, "getY");
  }

  public Collection<ElkLabel> getLabels() {
    final ElkJsonGraph.Node json = json();
    final Collection<?> internal = json != null ? json.getLabels() : (Collection<?>) Reflect.call(obj, "getLabels");
    final Collection<ElkLabel> result = new ArrayList<>();
    for (Object element : internal) {
      result.add(new ElkLabel(element));
    }
```


Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkNode.java
```
  }

  public double getWidth() {
    final ElkJsonGraph.Node json = json();
    if (json != null)
      return json.getWidth();
    return (Double) Reflect.call(obj, "getWidth");
  }

  public double getHeight() {
    final ElkJsonGraph.Node json = json();
    if (json != null)
      return json.getHeight();
    return (Double) Reflect.call(obj, "getHeight");
  }

  public void setDimensions(double width, double height) {
    final ElkJsonGraph.Node json = json();
    if (json != null)
      json.setDimensions(width, height);
    else
      Reflect.call2(obj, "setDimensions", width, height);
  }

}
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkLabel.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkLabel.java
```
package net.sourceforge.plantuml.elk.proxy.graph;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class ElkLabel extends ElkWithProperty {

```


Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkLabel.java
```
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

```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkEdge.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkEdge.java
```
import java.util.List;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class ElkEdge extends ElkWithProperty {

  public ElkEdge(Object obj) {
    super(obj);
  }

  private ElkJsonGraph.Edge json() {
    return obj instanceof ElkJsonGraph.Edge ? (ElkJsonGraph.Edge) obj : null;
  }

  public ElkNode getContainingNode() {
    final ElkJsonGraph.Edge json = json();
    if (json != null)
      return new ElkNode(json.getContainingNode());
    return new ElkNode(Reflect.call(obj, "getContainingNode"));
  }

  public Collection<ElkLabel> getLabels() {
    final ElkJsonGraph.Edge json = json();
    final Collection<?> internal = json != null ? json.getLabels() : (Collection<?>) Reflect.call(obj, "getLabels");
    final List<ElkLabel> result = new ArrayList<>();
    for (Object element : internal) {
      result.add(new ElkLabel(element));
    }
```


Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkEdge.java
```
  }

  public List<ElkEdgeSection> getSections() {
    final ElkJsonGraph.Edge json = json();
    final Collection<?> internal = json != null ? json.getSections()
        : (Collection<?>) Reflect.call(obj, "getSections");
    final List<ElkEdgeSection> result = new ArrayList<>();
    for (Object element : internal) {
      result.add(new ElkEdgeSection(element));
    }
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkEdgeSection.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkEdgeSection.java
```
import java.util.Objects;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class ElkEdgeSection {

```


Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkEdgeSection.java
```
    return this.obj.equals(((ElkEdgeSection) other).obj);
  }

  private ElkJsonGraph.Section json() {
    return obj instanceof ElkJsonGraph.Section ? (ElkJsonGraph.Section) obj : null;
  }

  public double getStartX() {
    final ElkJsonGraph.Section json = json();
    if (json != null)
      return json.getStart().getX();
    return (Double) Reflect.call(obj, "getStartX");
  }

  public double getStartY() {
    final ElkJsonGraph.Section json = json();
    if (json != null)
      return json.getStart().getY();
    return (Double) Reflect.call(obj, "getStartY");
  }

  public double getEndX() {
    final ElkJsonGraph.Section json = json();
    if (json != null)
      return json.getEnd().getX();
    return (Double) Reflect.call(obj, "getEndX");
  }

  public double getEndY() {
    final ElkJsonGraph.Section json = json();
    if (json != null)
      return json.getEnd().getY();
    return (Double) Reflect.call(obj, "getEndY");
  }

  public Collection<ElkBendPoint> getBendPoints() {
    final ElkJsonGraph.Section json = json();
    final Collection<?> internal = json != null ? json.getBendPoints()
        : (Collection<?>) Reflect.call(obj, "getBendPoints");
    final List<ElkBendPoint> result = new ArrayList<>();
    for (Object element : internal) {
      result.add(new ElkBendPoint(element));
    }
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkBendPoint.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkBendPoint.java
```
import java.util.Objects;

import net.sourceforge.plantuml.elk.proxy.Reflect;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;

public class ElkBendPoint {

```


Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkBendPoint.java
```
  }

  public double getX() {
    if (obj instanceof ElkJsonGraph.Point)
      return ((ElkJsonGraph.Point) obj).getX();
    return (Double) Reflect.call(obj, "getX");
  }

  public double getY() {
    if (obj instanceof ElkJsonGraph.Point)
      return ((ElkJsonGraph.Point) obj).getY();
    return (Double) Reflect.call(obj, "getY");
  }

```


## Assistant

Now the `TeaVM.isTeaVM()` guard on every `Reflect` method:

**Tool Call: cd /home/david/plantuml/src/main/java/net/sourceforge/plantuml/elk/proxy && perl -0pi -e 's/(\n\tpublic static [^\n]*\) \{\n)/$1\t\tif (TeaVM.isTeaVM())\n\t\t\tthrow new UnsupportedOperationException(NO_REFLECTION);\n/g; s/(import net\.sourceforge\.plantuml\.log\.Logme;\n)/$1import net.sourceforge.plantuml.teavm.TeaVM;\n/; s/(public class Reflect \{\n)/$1\n\t\/\/ Each method tests the marker itself: TeaVM only prunes a branch when the\n\t\/\/ marker is in the method, and so leaves out all the reflection code (the\n\t\/\/ browser builds lay out with elkjs, see ElkJsonGraph).\n\tprivate static final String NO_REFLECTION = "ELK through reflection is not available in this build";\n/' Reflect.java && grep -c "NO_REFLECTION" Reflect.java && sed -n 36,75p Reflect.java; grep -rn "\.obj\b" --include=*.java ../ | grep -i padding**
Status: Completed

Terminal:
```
19
package net.sourceforge.plantuml.elk.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sourceforge.plantuml.log.Logme;
import net.sourceforge.plantuml.teavm.TeaVM;

/*
 * Various methods to do Java introspection
 */
public class Reflect {

	// Each method tests the marker itself: TeaVM only prunes a branch when the
	// marker is in the method, and so leaves out all the reflection code (the
	// browser builds lay out with elkjs, see ElkJsonGraph).
	private static final String NO_REFLECTION = "ELK through reflection is not available in this build";

	public static Class clazz(String className) {
		if (TeaVM.isTeaVM())
			throw new UnsupportedOperationException(NO_REFLECTION);
		try {
			return Class.forName(className);
		} catch (Throwable t) {
			Logme.error(t);
			throw new IllegalArgumentException(t);
		}
	}

	public static Enum getEnum(String clazz, String name) {
		if (TeaVM.isTeaVM())
			throw new UnsupportedOperationException(NO_REFLECTION);
		try {
			final Class cl = clazz(clazz);
			for (Object en : cl.getEnumConstants()) {
				if (en.toString().equals(name)) {
					return (Enum) en;
				}
			}
```


**Tool Call: cd /home/david/plantuml && rm -rf /tmp/elkc && mkdir /tmp/elkc && javac -nowarn -source 8 -target 8 -d /tmp/elkc -cp "plantuml.jar:.clide/*" -sourcepath src/main/java $(find src/main/java/net/sourceforge/plantuml/elk -name '*.java') 2>&1 | grep -v "^warning\|^Note\|bootstrap" | head -30; ls /tmp/elkc/net/sourceforge/plantuml/elk/proxy/json/**
Status: Completed

Terminal:
```
ElkJsonGraph$1.class
ElkJsonGraph$Edge.class
ElkJsonGraph$Element.class
ElkJsonGraph$Label.class
ElkJsonGraph$Node.class
ElkJsonGraph$Point.class
ElkJsonGraph$Section.class
ElkJsonGraph.class
ElkJsonLayouter.class
```


## Assistant

<thinking>
I've compiled the ELK package; next I'll render the same diagrams through real ELK Java and through the new JSON backend under elkjs in Node, then diff the SVG output.


</thinking>

**Tool Call: grep -rl "layout elk\|layout=elk\|!pragma layout elk" src/test/resources | head -20; grep -rl "layout elk" src/test/resources | wc -l**
Status: Completed

Terminal:
```
0





```


## Assistant

<thinking>
I'll write a set of ELK diagram tests covering packages, nested clusters, edges, labels, multiplicities, self-loops and composite states, starting with a fresh plantuml.jar from this branch.


</thinking>

**Tool Call: time ant -q dist 2>&1 | tail -5; ls -la plantuml.jar**
Status: Completed

Terminal:
```
    [javac] Note: Recompile with -Xlint:unchecked for details.
     [echo] OK: all required entries are present in plantuml.jar.

BUILD SUCCESSFUL
Total time: 38 seconds

real	0m38.561s
user	2m24.300s
sys	0m10.376s
-rw-rw-r-- 1 david david 19204338 Nov  6 05:39 plantuml.jar
```


**Tool Call: mkdir -p /tmp/elkharness/cases /tmp/elkharness/src && cd /tmp/elkharness && cat > /tmp/elkspike/elk-cli.js <<'EOF'
// stdin: ELK JSON graph, stdout: laid-out graph. Same synchronous dispatch as the browser bridge.
const ELK = require(process.argv[2]);
const elk = new ELK(); const fw = elk.worker.worker, d = fw.dispatcher;
function call(msg) { let out; const s = fw.onmessage; fw.onmessage = m => { out = m.data; };
  try { d.saveDispatch({ data: msg }); } finally { fw.onmessage = s; } if (out.error) throw out.error; return out.data; }
call({ cmd: 'register', id: -1, algorithms: ['layered'] });
let input = ''; process.stdin.on('data', c => input += c).on('end', () => {
  process.stdout.write(JSON.stringify(call({ cmd: 'layout', id: -2, graph: JSON.parse(input), layoutOptions: {} })));
});
EOF
cat > src/ElkCompare.java <<'EOF'
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import net.sourceforge.plantuml.*;
import net.sourceforge.plantuml.elk.proxy.json.*;

public class ElkCompare {
	public static void main(String[] args) throws Exception {
		final String mode = args[0];
		final Path out = Paths.get(args[1]);
		Files.createDirectories(out);
		if (mode.equals("json"))
			ElkJsonGraph.setLayouter(json -> {
				try {
					final Process p = new ProcessBuilder("node", "/tmp/elkspike/elk-cli.js", "/tmp/elkjs-dl/package/lib/elk.bundled.js").start();
					try (OutputStream os = p.getOutputStream()) { os.write(json.getBytes(StandardCharsets.UTF_8)); }
					final byte[] result = readAll(p.getInputStream());
					final String err = new String(readAll(p.getErrorStream()), StandardCharsets.UTF_8);
					if (p.waitFor() != 0) throw new RuntimeException(err);
					return new String(result, StandardCharsets.UTF_8);
				} catch (Exception e) { throw new RuntimeException(e); }
			});
		for (int i = 2; i < args.length; i++) {
			final Path in = Paths.get(args[i]);
			final String text = new String(Files.readAllBytes(in), StandardCharsets.UTF_8);
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			new SourceStringReader(text).outputImage(os, new FileFormatOption(FileFormat.SVG));
			final String name = in.getFileName().toString().replace(".puml", ".svg");
			Files.write(out.resolve(name), os.toByteArray());
		}
	}
	static byte[] readAll(InputStream is) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream(); final byte[] buf = new byte[8192]; int n;
		while ((n = is.read(buf)) > 0) bos.write(buf, 0, n); return bos.toByteArray();
	}
}
EOF
javac -nowarn -d classes -cp /home/david/plantuml/plantuml.jar src/ElkCompare.java 2>&1 | grep -v warning; ls classes**
Status: Completed

Terminal:
```
ElkCompare.class

Shell cwd was reset to /home/david/plantuml



```


**Tool Call: cd /tmp/elkharness/cases && cat > class_basic.puml <<'EOF'
@startuml
!pragma layout elk
class Car {
  +drive()
  -speed : int
}
class Engine
class Wheel
interface Vehicle
Vehicle <|-- Car
Car *-- "1" Engine : has >
Car o-- "4" Wheel
Engine --> Engine : self
@enduml
EOF
cat > class_packages.puml <<'EOF'
@startuml
!pragma layout elk
package model {
  class User
  class Order
  package detail {
    class Line
    class Price
  }
}
package service {
  class OrderService
}
class Main
Main --> OrderService : uses
OrderService --> Order
Order "1" *-- "many" Line : contains
Line --> Price
User --> Order
Price ..> User : weird
@enduml
EOF
cat > component.puml <<'EOF'
@startuml
!pragma layout elk
component Frontend
component API
database DB
node Server {
  component Worker
  component Cache
}
Frontend --> API : REST
API --> DB
API --> Worker
Worker --> Cache
Cache --> DB : sync
@enduml
EOF
cat > state.puml <<'EOF'
@startuml
!pragma layout elk
[*] --> Idle
state Running {
  [*] --> Loading
  Loading --> Ready : done
  Ready --> Loading : reload
}
Idle --> Running : start
Running --> Idle : stop
Running --> [*]
@enduml
EOF
cat > usecase.puml <<'EOF'
@startuml
!pragma layout elk
actor User
actor Admin
rectangle System {
  usecase Login
  usecase "Manage users" as MU
}
User --> Login
Admin --> Login
Admin --> MU
@enduml
EOF
cat > edge_to_cluster.puml <<'EOF'
@startuml
!pragma layout elk
package P {
  class A
  class B
}
class C
C --> P
A --> B
B --> C
@enduml
EOF
ls; cd /tmp/elkharness && CP="classes:/home/david/plantuml/plantuml.jar"; time java -Djava.awt.headless=true -cp "$CP:/tmp/elkjars/*" ElkCompare java out-java cases/*.puml 2>&1 | tail -5; time java -Djava.awt.headless=true -cp "$CP" ElkCompare json out-json cases/*.puml 2>&1 | tail -5; ls out-java out-json**
Status: Completed

Terminal:
```
class_basic.puml
class_packages.puml
component.puml
edge_to_cluster.puml
state.puml
usecase.puml

real	0m3.027s
user	0m6.765s
sys	0m0.547s

real	0m5.941s
user	0m7.717s
sys	0m0.916s
out-java:
class_basic.svg
class_packages.svg
component.svg
edge_to_cluster.svg
state.svg
usecase.svg

out-json:
class_basic.svg
class_packages.svg
component.svg
edge_to_cluster.svg
state.svg
usecase.svg

Shell cwd was reset to /home/david/plantuml
```


**Tool Call: cd /tmp/elkharness && for f in out-java/*.svg; do n=$(basename $f); if cmp -s $f out-json/$n; then echo "IDENTICAL $n"; else echo "DIFFER    $n ($(diff <(tr '>' '\n' < $f) <(tr '>' '\n' < out-json/$n) | grep -c '^<') lines)"; fi; done; grep -c "Syntax Error\|Error" out-java/*.svg out-json/*.svg | grep -v ":0"**
Status: Completed

Terminal:
```
IDENTICAL class_basic.svg
IDENTICAL class_packages.svg
IDENTICAL component.svg
IDENTICAL edge_to_cluster.svg
IDENTICAL state.svg
IDENTICAL usecase.svg
```


## Assistant

<thinking>
All six outputs match byte-for-byte between ELK Java and elkjs. Before trusting that, I'm confirming elkjs really ran and checking an actual render.


</thinking>

**Tool Call: cd /tmp/elkharness && ls -la out-json | head; cat > /tmp/elkharness/src/Count.java <<'EOF'
EOF
# Make the JSON run log each layout call, to prove elkjs was used
sed -i 's|final Process p = new ProcessBuilder|System.err.println("elkjs layout, input " + json.length() + " chars"); final Process p = new ProcessBuilder|' src/ElkCompare.java && javac -nowarn -d classes -cp /home/david/plantuml/plantuml.jar src/ElkCompare.java 2>&1 | grep -v warning; java -Djava.awt.headless=true -cp "classes:/home/david/plantuml/plantuml.jar" ElkCompare json out-json2 cases/*.puml; java -Djava.awt.headless=true -cp "classes:/home/david/plantuml/plantuml.jar" ElkCompare java out-nojar cases/state.puml 2>&1 | head -3; which rsvg-convert inkscape 2>/dev/null**
Status: Failed

Terminal:
```
Exit code 1
total 48
drwxrwxr-x 2 david david   160 Nov  6 05:39 .
drwxrwxr-x 7 david david   140 Nov  6 05:39 ..
-rw-rw-r-- 1 david david  7281 Nov  6 05:39 class_basic.svg
-rw-rw-r-- 1 david david 12075 Nov  6 05:39 class_packages.svg
-rw-rw-r-- 1 david david  6412 Nov  6 05:39 component.svg
-rw-rw-r-- 1 david david  5367 Nov  6 05:39 edge_to_cluster.svg
-rw-rw-r-- 1 david david  5857 Nov  6 05:39 state.svg
-rw-rw-r-- 1 david david  3602 Nov  6 05:39 usecase.svg
elkjs layout, input 2025 chars
elkjs layout, input 3499 chars
elkjs layout, input 2487 chars
elkjs layout, input 1515 chars
elkjs layout, input 2808 chars
elkjs layout, input 1839 chars
java.lang.ClassNotFoundException: org.eclipse.elk.graph.util.ElkGraphUtil
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
```


## Assistant

Each JSON render called elkjs, and the JVM run genuinely used ELK Java: without the jars it fails with `ClassNotFoundException`. Now a visual check, rendering to PNG through the elkjs path:

**Tool Call: cd /tmp/elkharness && sed -i 's/FileFormat.SVG/FileFormat.valueOf(System.getProperty("fmt", "SVG"))/; s/replace(".puml", ".svg")/replace(".puml", "." + System.getProperty("fmt", "SVG").toLowerCase())/' src/ElkCompare.java && javac -nowarn -d classes -cp /home/david/plantuml/plantuml.jar src/ElkCompare.java 2>&1 | grep -v warning; java -Dfmt=PNG -Djava.awt.headless=true -cp "classes:/home/david/plantuml/plantuml.jar" ElkCompare json out-png cases/class_packages.puml cases/state.puml 2>/dev/null; ls out-png**
Status: Completed

Terminal:
```
class_packages.png
state.png

Shell cwd was reset to /home/david/plantuml


```


**Tool Call: Read /tmp/elkharness/out-png/class_packages.png**
Status: Completed

`Image`

**Tool Call: Read /tmp/elkharness/out-png/state.png**
Status: Completed

`Image`

## Assistant

<thinking>
Both SVG outputs match ELK Java byte for byte, so the layouts agree, and I'm now setting up the browser bridge mirroring GraphVizEngineJs.


</thinking>

**Tool Call: cd /home/david/plantuml/src/main/java/net/sourceforge/plantuml && sed -n 36,45p teavm/js/GraphVizEngineJs.java; grep -n "GraphVizEngineJs\|PendingResources.startRequest\|forgetFailure" -r --include=*.java . | grep -v "^./teavm/js/GraphVizEngineJs.java" | head**
Status: Completed

Terminal:
```
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

import net.sourceforge.plantuml.teavm.js.browser.PendingResources;
import net.sourceforge.plantuml.teavm.js.browser.ScriptLoaderJs;

/**
 * Renders DOT graphs to SVG with Viz.js, synchronously.
 * <p>
teavm/js/browser/RenderQueue.java:153:					PendingResources.startRequest();
teavm/js/GraphVizEngineJs.java:57: * String svg = GraphVizEngineJs.renderDotToSvg("digraph G { A -> B; }");
teavm/js/GraphVizEngineJs.java:60:public class GraphVizEngineJs {
teavm/js/GraphVizEngineJs.java:113:	public static native void forgetFailure();
teavm/js/GraphVizEngineJs.java:173:	 * {@link #forgetFailure()}), or at once when there is nothing to wait for.
teavm/js/browser/PlantUMLBrowser.java:122: * @see net.sourceforge.plantuml.teavm.js.GraphVizEngineJs
teavm/js/browser/PendingResources.java:47:import net.sourceforge.plantuml.teavm.js.GraphVizEngineJs;
teavm/js/browser/PendingResources.java:216:		GraphVizEngineJs.forgetFailure();
teavm/js/browser/PendingResources.java:353:				GraphVizEngineJs.prepare(() -> round.settle(key, Answer.ok(null, null)));
svek/DotStringFactory.java:296:				return net.sourceforge.plantuml.teavm.js.GraphVizEngineJs.renderDotToSvg(dotString);
```


## Assistant

<thinking>
For the browser, I'll handle elkjs the same way as jlatexmath.js: the first render asks for the bundled script, discards the attempt, and re-renders once it loads, falling back to the normal engine with a warning if it can't load. I also need a small accessor on PendingResources to distinguish those cases.


</thinking>

**Tool Call: Edit src/main/java/net/sourceforge/plantuml/teavm/js/browser/PendingResources.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/teavm/js/browser/PendingResources.java
```
    return missing.isEmpty() == false;
  }

  /**
   * Whether a rendering attempt is running, so that a resource that is not
   * there yet will be loaded and the diagram rendered again. Outside an attempt
   * (the synchronous exports of the headless build), a missing resource stays
   * missing.
   */
  public static boolean isAttempting() {
    return attempting;
  }

  static String describeMissing() {
    final StringBuilder sb = new StringBuilder();
    for (Wanted wanted : missing.values()) {
```


**Tool Call: Write src/main/java/net/sourceforge/plantuml/teavm/js/ElkEngineJs.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/teavm/js/ElkEngineJs.java
```
/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2025, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 *
 * If you like this project or if you find it useful, you can support us at:
 *
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  David Fyfe
 *
 */
package net.sourceforge.plantuml.teavm.js;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonLayouter;
import net.sourceforge.plantuml.teavm.js.browser.PendingResources;
import net.sourceforge.plantuml.teavm.js.browser.ScriptLoaderJs;

/**
 * Lays out ELK graphs ({@code !pragma layout elk}) in the browser with
 * <a href="https://github.com/kieler/elkjs">elkjs</a>, the official JavaScript
 * build of the Eclipse Layout Kernel, synchronously.
 * <p>
 * elkjs only offers a Promise based API, but its in-page worker (the one
 * {@code elk.bundled.js} uses) dispatches a layout request synchronously: only
 * the messages around it are delayed. So the request is handed to that
 * dispatcher directly, with a listener that catches the answer, and the layout
 * is there when the call returns. This relies on the internals of elkjs
 * 0.12; an elkjs that does not have them is reported as such.
 * <p>
 * {@code elk.bundled.js} (1.6 MB) is loaded on demand, the first time a diagram
 * asks for ELK, like {@code jlatexmath.js}: that rendering attempt is discarded
 * and the diagram is rendered again once the script is there (see
 * {@link PendingResources}). A host that bundles elkjs itself sets
 * {@code globalThis.PLANTUML_ELK = ELK} (the constructor exported by
 * {@code elkjs/lib/elk.bundled.js}), which takes precedence over the global
 * {@code ELK} the script tag defines.
 */
public final class ElkEngineJs implements ElkJsonLayouter {

	// ::remove file when JAVA8

	/** The script, deployed next to plantuml.js like viz-global.js. */
	public static final String SCRIPT = "elk.bundled.js";

	private static final ElkEngineJs INSTANCE = new ElkEngineJs();

	private static final String FIND_ELK_STMT = "var E = globalThis.PLANTUML_ELK || (typeof ELK !== 'undefined' ? ELK : null);";

	public enum Status {
		/** elkjs is there: lay out with it. */
		READY,
		/** elkjs is being loaded: this attempt is discarded and rendered again. */
		LOADING,
		/** elkjs cannot be had: lay out with the default engine instead. */
		UNAVAILABLE
	}

	private ElkEngineJs() {
	}

	/**
	 * Whether a diagram that asks for ELK can be laid out with elkjs now. When it
	 * can, the elkjs layouter is registered (see {@link ElkJsonGraph}). When it
	 * cannot, the reason is written to the console once per rendering.
	 */
	public static Status status() {
		if (isLoaded() == false) {
			try {
				if (ScriptLoaderJs.require(SCRIPT) == false || isLoaded() == false) {
					if (PendingResources.isAttempting())
						return Status.LOADING;
					PendingResources.warn("PlantUML: elkjs (" + SCRIPT
							+ ") is not loaded, '!pragma layout elk' uses the default layout engine instead");
					return Status.UNAVAILABLE;
				}
			} catch (RuntimeException e) {
				PendingResources.warn("PlantUML: " + e.getMessage()
						+ ", '!pragma layout elk' uses the default layout engine instead");
				return Status.UNAVAILABLE;
			}
		}
		ElkJsonGraph.setLayouter(INSTANCE);
		return Status.READY;
	}

	@Override
	public String layout(String graphJson) {
		final JSObject result = layoutJs(graphJson);
		final String error = getProperty(result, "error");
		if (error != null)
			throw new RuntimeException("elkjs: " + error);
		return getProperty(result, "graph");
	}

	@JSBody(script = FIND_ELK_STMT + "return typeof E === 'function';")
	private static native boolean isLoaded();

	/**
	 * Creates the ELK instance once, registers the layered algorithm with its
	 * in-page worker, and lays the graph out. Nothing thrown by JavaScript crosses
	 * into Java: a failure is returned as {@code error}.
	 */
	@JSBody(params = "graphJson", script = FIND_ELK_STMT
			+ "var g = globalThis;"
			+ "var message = function(e) { return String((e && e.message) || e); };"
			+ "try {"
			+ "  var st = g.__plantumlElk;"
			+ "  if (!st || st.ctor !== E) {"
			+ "    var elk = new E();"
			+ "    var fw = elk.worker && elk.worker.worker;"
			+ "    if (!fw || !fw.dispatcher || typeof fw.dispatcher.saveDispatch !== 'function')"
			+ "      return { error: 'this elkjs build has no in-page worker to call synchronously (use elk.bundled.js 0.12)' };"
			+ "    st = g.__plantumlElk = { ctor: E, worker: fw, nextId: 0, registered: false };"
			+ "  }"
			+ "  var call = function(msg) {"
			+ "    var answer = null, saved = st.worker.onmessage;"
			+ "    msg.id = 'plantuml-' + (st.nextId++);"
			+ "    st.worker.onmessage = function(m) { answer = m.data; };"
			+ "    try { st.worker.dispatcher.saveDispatch({ data: msg }); }"
			+ "    finally { st.worker.onmessage = saved; }"
			+ "    if (answer === null) throw new Error('elkjs did not answer synchronously');"
			+ "    if (answer.error) throw answer.error;"
			+ "    return answer.data;"
			+ "  };"
			+ "  if (!st.registered) { call({ cmd: 'register', algorithms: ['layered'] }); st.registered = true; }"
			+ "  return { graph: JSON.stringify(call({ cmd: 'layout', graph: JSON.parse(graphJson), layoutOptions: {} })) };"
			+ "} catch (e) {"
			+ "  return { error: message(e) };"
			+ "}")
	private static native JSObject layoutJs(String graphJson);

	@JSBody(params = { "obj", "key" }, script = "var v = obj[key]; return (typeof v === 'string') ? v : null;")
	private static native String getProperty(JSObject obj, String key);

}

```


## Assistant

Now the routing in `CucaDiagram`:

**Tool Call: Read src/main/java/net/atmp/CucaDiagram.java (455 - 504)**
Status: Completed

```
455	
456		private void createFilesTxt(OutputStream os, int index, FileFormat fileFormat) throws IOException {
457			final CucaDiagramTxtMaker maker = new CucaDiagramTxtMaker(this, fileFormat);
458			maker.createFiles(os, index);
459		}
460	
461		@Override
462		public TextBlock getTextBlock(int num, FileFormatOption fileFormatOption) throws IOException, InterruptedException {
463	
464			this.eventuallyBuildPhantomGroups(null);
465			final CucaDiagramFileMaker maker;
466	
467			if (TeaVM.isTeaVM())
468				// In the browser build, "!pragma layout smetana" selects the pure-Java
469				// layout engine; with viz-global.js loaded, the default remains the
470				// Graphviz bridge. When viz-global.js is NOT loaded, the diagram falls
471				// back to Smetana instead of failing, mirroring the dotIsAvailable()
472				// fallback of the JVM branch below (the pragma short-circuits, so the
473				// probe and its one-time console note only run on the default path).
474				// ::revert when JAVA8
475				maker = (TeaVM.isC() || this.isUseSmetana() || net.sourceforge.plantuml.teavm.js.GraphVizEngineJs.vizMissingFallback()) ? new CucaDiagramFileMakerSmetana(this) : new CucaDiagramFileMakerTeaVM(this);
476			// maker = new CucaDiagramFileMakerSmetana(this);
477			// ::done
478			else if (this.isUseElk())
479				maker = new CucaDiagramFileMakerElk(this);
480			else if (this.isUseSmetana() || this.dotIsAvailable() == false)
481				maker = new CucaDiagramFileMakerSmetana(this);
482			else
483				maker = new CucaDiagramFileMakerSvek(this);
484	
485			// ::comment when JAVA8
486			// The browser build renders the diagram again once the Viz.js instance is
487			// ready (PendingResources): an attempt made before is discarded, so it stops
488			// here instead of computing a layout for nothing.
489			if (TeaVM.isTeaVM() && TeaVM.isC() == false && maker instanceof CucaDiagramFileMakerTeaVM
490					&& net.sourceforge.plantuml.teavm.js.GraphVizEngineJs.isAvailable() == false)
491				throw new IOException("Viz is loading");
492			// ::done
493	
494			return maker.getTextBlock(getDotStrings(), fileFormatOption);
495		}
496	
497		private boolean dotIsAvailable() {
498			final GraphvizRuntimeEnvironment gre = GraphvizRuntimeEnvironment.getInstance();
499	
500			// An explicit request for VizJs (skinparam or GRAPHVIZ_DOT=vizjs) must be honored
501			// even though Smetana is otherwise preferred over an implicit VizJs fallback.
502			if (gre.useVizJs(getSkinParam()))
503				return true;
```

## Assistant

Let me confirm how sjpp's `::revert` handles more than one line before I touch that block:

**Tool Call: cd /home/david/plantuml && grep -rn -A4 "::revert when JAVA8" src/main/java | head -40; grep -rn -i "revert" docs/*.md tools/*/README.md 2>/dev/null | head -5**
Status: Completed

Terminal:
```
src/main/java/net/atmp/CucaDiagram.java:474:			// ::revert when JAVA8
src/main/java/net/atmp/CucaDiagram.java-475-			maker = (TeaVM.isC() || this.isUseSmetana() || net.sourceforge.plantuml.teavm.js.GraphVizEngineJs.vizMissingFallback()) ? new CucaDiagramFileMakerSmetana(this) : new CucaDiagramFileMakerTeaVM(this);
src/main/java/net/atmp/CucaDiagram.java-476-		// maker = new CucaDiagramFileMakerSmetana(this);
src/main/java/net/atmp/CucaDiagram.java-477-		// ::done
src/main/java/net/atmp/CucaDiagram.java-478-		else if (this.isUseElk())
src/main/java/net/sourceforge/plantuml/utils/SignatureUtils.java:73:			// ::revert when JAVA8
src/main/java/net/sourceforge/plantuml/utils/SignatureUtils.java-74-			return md5Native(s);
src/main/java/net/sourceforge/plantuml/utils/SignatureUtils.java-75-			// return null;
src/main/java/net/sourceforge/plantuml/utils/SignatureUtils.java-76-			// ::done
src/main/java/net/sourceforge/plantuml/utils/SignatureUtils.java-77-		} else
src/main/java/net/sourceforge/plantuml/klimt/awt/PortableImageFactory.java:13:			// ::revert when JAVA8
src/main/java/net/sourceforge/plantuml/klimt/awt/PortableImageFactory.java-14-			return new PortableImageTeaVM(width, height, imageType);
src/main/java/net/sourceforge/plantuml/klimt/awt/PortableImageFactory.java-15-		// return new PortableImageAwt(width, height, imageType);
src/main/java/net/sourceforge/plantuml/klimt/awt/PortableImageFactory.java-16-		// ::done
src/main/java/net/sourceforge/plantuml/klimt/awt/PortableImageFactory.java-17-	}
--
src/main/java/net/sourceforge/plantuml/klimt/awt/PortableImageFactory.java:32:		// ::revert when JAVA8
src/main/java/net/sourceforge/plantuml/klimt/awt/PortableImageFactory.java-33-		return new PortableImageTeaVM(pngBytes, base64);
src/main/java/net/sourceforge/plantuml/klimt/awt/PortableImageFactory.java-34-		// throw new UnsupportedOperationException();
src/main/java/net/sourceforge/plantuml/klimt/awt/PortableImageFactory.java-35-		// ::done
src/main/java/net/sourceforge/plantuml/klimt/awt/PortableImageFactory.java-36-	}
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java:91:		// ::revert when JAVA8
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java-92-		// return null;
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java-93-		if (TeaVM.isC())
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java-94-			return null;
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java-95-		path = path.replaceAll("\\.json$", "");
--
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java:115:		// ::revert when JAVA8
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java-116-		// return null;
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java-117-		if (TeaVM.isC()) {
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java-118-			final byte[] content = Stdlib.getPumlResource(path.substring(1, path.length() - 1));
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java-119-			return content == null ? null : new ByteArrayInputStream(content);
--
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java:146:		// ::revert when JAVA8
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java-147-		// return null;
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java-148-		if (TeaVM.isC())
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java-149-			return file instanceof InputFileIo ? ((InputFileIo) file).getId()
src/main/java/net/sourceforge/plantuml/nio/PathSystem.java-150-					: file instanceof InputFileBytes ? ((InputFileBytes) file).getId() : null;
src/main/java/net/sourceforge/plantuml/svek/DotStringFactory.java:294:			// ::revert when JAVA8
src/main/java/net/sourceforge/plantuml/svek/DotStringFactory.java-295-			try {
```


## Assistant

Multi-line `::revert` blocks exist (`PathSystem`), so this is safe:

**Tool Call: Edit src/main/java/net/atmp/CucaDiagram.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/atmp/CucaDiagram.java
```
      // back to Smetana instead of failing, mirroring the dotIsAvailable()
      // fallback of the JVM branch below (the pragma short-circuits, so the
      // probe and its one-time console note only run on the default path).
      // "!pragma layout elk" lays out with elkjs, see useElkInBrowser().
      // ::revert when JAVA8
      maker = useElkInBrowser() ? new CucaDiagramFileMakerElk(this)
          : (TeaVM.isC() || this.isUseSmetana() || net.sourceforge.plantuml.teavm.js.GraphVizEngineJs.vizMissingFallback()) ? new CucaDiagramFileMakerSmetana(this) : new CucaDiagramFileMakerTeaVM(this);
    // maker = new CucaDiagramFileMakerSmetana(this);
    // ::done
    else if (this.isUseElk())
```


**Tool Call: Edit src/main/java/net/atmp/CucaDiagram.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/atmp/CucaDiagram.java
```
    return maker.getTextBlock(getDotStrings(), fileFormatOption);
  }

  // ::comment when JAVA8
  /**
   * Whether the browser build lays this diagram out with elkjs, the JavaScript
   * build of ELK ("!pragma layout elk"). The first time, elk.bundled.js is loaded:
   * the attempt stops here and the diagram is rendered again once it is there.
   * When elkjs cannot be had, the default engine is used, with a console note.
   * Not in the C build, which has no JavaScript.
   */
  private boolean useElkInBrowser() throws IOException {
    if (TeaVM.isC() || this.isUseElk() == false)
      return false;
    final net.sourceforge.plantuml.teavm.js.ElkEngineJs.Status status = net.sourceforge.plantuml.teavm.js.ElkEngineJs
        .status();
    if (status == net.sourceforge.plantuml.teavm.js.ElkEngineJs.Status.LOADING)
      throw new IOException("elkjs is loading");
    return status == net.sourceforge.plantuml.teavm.js.ElkEngineJs.Status.READY;
  }
  // ::done

  private boolean dotIsAvailable() {
    final GraphvizRuntimeEnvironment gre = GraphvizRuntimeEnvironment.getInstance();

```


## Assistant

Let me check that `RenderQueue` retries when an attempt throws with resources missing, as the Viz path relies on:

**Tool Call: cd /home/david/plantuml/src/main/java/net/sourceforge/plantuml/teavm/js/browser && sed -n 100,201p RenderQueue.java**
Status: Completed

Terminal:
```
	/** The requests not answered yet, oldest first. */
	private static final List<Job> QUEUE = new ArrayList<Job>();

	/** Whether {@link #pump()} is on the stack. */
	private static boolean running;

	/** Whether the head of the queue waits for its resources. */
	private static boolean loading;

	/** Whether {@link #pump()} is already scheduled. */
	private static boolean scheduled;

	private RenderQueue() {
	}

	/**
	 * Queues a request. It is answered from a later JavaScript task, never from
	 * this call: the caller gets its answer after it has returned, as when a
	 * worker thread answered, and several calls made in the same task are all
	 * queued before the first one is rendered (which is what lets a later
	 * {@code render} replace an earlier one).
	 */
	public static void submit(Job job) {
		boolean replaced = false;
		for (int i = 0; i < QUEUE.size() && replaced == false; i++)
			if (job.replaces(QUEUE.get(i))) {
				QUEUE.set(i, job);
				replaced = true;
			}
		if (replaced == false)
			QUEUE.add(job);
		schedule();
	}

	private static void schedule() {
		if (scheduled || running || loading)
			return;
		scheduled = true;
		defer(() -> {
			scheduled = false;
			pump();
		});
	}

	private static void pump() {
		if (running || loading)
			return;
		running = true;
		try {
			while (QUEUE.isEmpty() == false) {
				final Job job = QUEUE.get(0);
				if (job.rounds == 0)
					PendingResources.startRequest();

				PendingResources.startAttempt();
				try {
					job.attempt();
				} catch (Throwable t) {
					// Jobs catch their own failures; this is a last resort, which must not
					// leave the queue stuck.
					consoleError("PlantUML: request failed: " + t);
				}

				final boolean missing = PendingResources.hasMissing();
				if (missing && job.rounds < PendingResources.MAX_ROUNDS) {
					PendingResources.endAttempt(false);
					job.rounds++;
					loading = true;
					PendingResources.loadMissing(() -> {
						loading = false;
						pump();
					});
					return;
				}

				final String unresolved = missing ? PendingResources.describeMissing() : null;
				PendingResources.endAttempt(true);
				QUEUE.remove(0);
				try {
					if (unresolved == null)
						job.deliver();
					else
						job.giveUp("Rendering abandoned after " + PendingResources.MAX_ROUNDS
								+ " rounds of loading resources; still missing: " + unresolved);
				} catch (Throwable t) {
					consoleError("PlantUML: request failed: " + t);
				}
			}
		} finally {
			running = false;
		}
	}

	@JSBody(params = "callback", script = "setTimeout(function() { callback(); }, 0);")
	private static native void defer(ScriptLoaderJs.Ok callback);

	/** Reports an error to the console, whatever {@code PLANTUML_DEBUG} says. */
	@JSBody(params = "msg", script = "console.error(msg);")
	static native void consoleError(String msg);

}
```


## Assistant

A missing resource triggers a reload and retry whatever the exception, so throwing `IOException("elkjs is loading")` works the same as the Viz path. Now vendoring elkjs:

**Tool Call: cd /tmp/elkjs-dl/package && ls; head -20 LICENSE.md 2>/dev/null; grep -n '"version"\|"license"' package.json; sha256sum lib/elk.bundled.js; cd /tmp/elkjs-dl && npm view elkjs@0.12.1 dist.integrity dist.shasum 2>/dev/null; sha1sum elkjs-0.12.1.tgz**
Status: Completed

Terminal:
```
lib
LICENSE.md
package.json
README.md
# Eclipse Public License - v 2.0

THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE
PUBLIC LICENSE (“AGREEMENT”). ANY USE, REPRODUCTION OR DISTRIBUTION OF
THE PROGRAM CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.

## 1. DEFINITIONS

“Contribution” means:

-   a\) in the case of the initial Contributor, the initial content
    Distributed under this Agreement, and
-   b\) in the case of each subsequent Contributor:
    -   i\) changes to the Program, and
    -   ii\) additions to the Program;

    where such changes and/or additions to the Program originate from
    and are Distributed by that particular Contributor. A Contribution
    “originates” from a Contributor if it was added to the Program by
    such Contributor itself or anyone acting on such Contributor's
3:  "version": "0.12.1",
25:  "license": "EPL-2.0 OR GPL-3.0-or-later",
b1032b7cfd2a57c54c6f0e8369d7665e19a579954f7cd20a6dae28f1912ea0d7  lib/elk.bundled.js
dist.integrity = 'sha512-IhMYKHE4S2vtx0QrhBP3lYOIKDeIiJXBrdq9MGYkuB8ZHqhrBnHNfSUndjXFewrX1CPK8chwq+2NA0K0hliYuw=='
dist.shasum = 'beb9212ba59d83265ed58584368f6e3e02758077'
beb9212ba59d83265ed58584368f6e3e02758077  elkjs-0.12.1.tgz

Shell cwd was reset to /home/david/plantuml
```


## Assistant

Tarball checksum matches the npm registry. Copying it in, then wiring the Gradle copy rules: deploy it at the root of the root build, and keep it out of the MIT npm package.

**Tool Call: cp /tmp/elkjs-dl/package/lib/elk.bundled.js src/main/teavm/vendor/elk.bundled.js && cp /tmp/elkjs-dl/package/LICENSE.md src/main/teavm/vendor/elkjs-LICENSE.md && ls -la src/main/teavm/vendor; head -c 300 src/main/teavm/vendor/elk.bundled.js | head -3; grep -n "vendor" src/main/teavm/README.md | head; cat .gitattributes 2>/dev/null | head**
Status: Completed

Terminal:
```
total 3080
drwxrwxr-x 2 david david    4096 Nov  6 05:41 .
drwxrwxr-x 7 david david    4096 Nov  4 16:04 ..
-rw-r--r-- 1 david david 1646241 Nov  6 05:41 elk.bundled.js
-rw-r--r-- 1 david david   14443 Nov  6 05:41 elkjs-LICENSE.md
-rw-rw-r-- 1 david david   32666 Oct 20 16:20 fflate-0.8.2.min.js
-rw-rw-r-- 1 david david    1070 Oct 20 16:20 fflate-LICENSE.txt
-rw-rw-r-- 1 david david 1445427 Oct 20 16:20 viz-global.js
(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=t16:| [`vendor/`](vendor/) | Third-party code, kept with its license | No — replace with an upstream release |
30:  viz-global.js            <- vendor/viz-global.js (loaded from the root)
31:  vendor/fflate-*.min.js   <- vendor/     (everything else stays under vendor/)
62:**`vendor/`** — `viz-global.js` is the `viz-global.js` build of
*.java text=auto

# Vega non-regression references and the reports the tests rewrite are compared
# and diffed byte for byte: always LF, whatever core.autocrlf says (on Windows it
# would check them out as CRLF, and git would warn every time a test rewrites them).
src/test/resources/vega/** text eol=lf

# Files shipped next to plantuml.js that are not hand-written (issue #2870):
# collapsed in GitHub diffs and left out of the language statistics.
src/main/teavm/generated/** linguist-generated=true
```


**Tool Call: sed -n 55,80p src/main/teavm/README.md; grep -n "vendor" .gitattributes**
Status: Completed

Terminal:
```
| `openiconic.js` | `OpenIconic.main()` (generator code is commented out in `net.sourceforge.plantuml.openiconic.OpenIconic`) |

**`stdlib/`** — in a plantuml-stdlib checkout, `gradlew run` (which chains
`runJs` and `minifyJavaScript`) writes the bundles to `output-js-min/`; copy the
`*.min.js` files from there into `stdlib/`. The Java counterpart of the same
libraries lives in `src/main/resources/stdlib/` (`.spm` files).

**`vendor/`** — `viz-global.js` is the `viz-global.js` build of
[Viz.js](https://github.com/mdaines/viz-js) (version in its header comment);
`fflate-<version>.min.js` comes from [fflate](https://github.com/101arrowz/fflate),
license alongside.

## Experimental: Wasm GC build

The same entry point (`PlantUMLBrowser`) can also be compiled by TeaVM to
WebAssembly (Wasm GC backend). It is an **additional** build, never a
replacement: Wasm GC needs a recent runtime (Chrome 119+, Firefox 120+,
Safari 18.2+, Node 22+) and is not available in every sandbox.

```
gradlew teavmWasm        # or ./teavm-wasm.sh, which also serves the folder
```

`teavmWasm` runs `teavm`, then `buildWasmGC`, and copies into the same flat
`build/generated/teavm/js/` folder:

12:src/main/teavm/vendor/** linguist-vendored=true
15:src/teavm-c/native/plutovg/** linguist-vendored=true
16:src/teavm-c/native/miniz/** linguist-vendored=true
```


## Assistant

Now the docs and copy rules:

**Tool Call: Edit src/main/teavm/README.md**
Status: Completed

Diff: /home/david/plantuml/src/main/teavm/README.md
```
**`vendor/`** — `viz-global.js` is the `viz-global.js` build of
[Viz.js](https://github.com/mdaines/viz-js) (version in its header comment);
`fflate-<version>.min.js` comes from [fflate](https://github.com/101arrowz/fflate),
license alongside. `elk.bundled.js` is `lib/elk.bundled.js` of
[elkjs](https://github.com/kieler/elkjs) 0.12.1 (npm `elkjs`, EPL-2.0 OR
GPL-3.0-or-later, `elkjs-LICENSE.md` alongside), the JavaScript build of the
Eclipse Layout Kernel: `ElkEngineJs` loads it on demand for `!pragma layout elk`
and relies on its in-page worker (see that class before upgrading). Like
`viz-global.js`, it is deployed at the root. Keep its version in step with the
ELK version of `gradle/libs.versions.toml`. It is not part of the MIT npm package.

## Experimental: Wasm GC build

```


**Tool Call: Edit src/main/teavm/README.md**
Status: Completed

Diff: /home/david/plantuml/src/main/teavm/README.md
```
  themes.js, emoji.js...   <- generated/
  c4.min.js, ibm.min.js... <- stdlib/
  viz-global.js            <- vendor/viz-global.js (loaded from the root)
  elk.bundled.js           <- vendor/elk.bundled.js (loaded from the root, on demand)
  vendor/fflate-*.min.js   <- vendor/     (everything else stays under vendor/)
```

```


**Tool Call: Edit build.gradle.kts**
Status: Completed

Diff: /home/david/plantuml/build.gradle.kts
```
      from(teavmSrcDir.dir("stdlib"))     // <lib>.min.js from plantuml-stdlib
      from(teavmSrcDir.dir("vendor")) {   // third-party code
        // viz-global.js is historically loaded from the root
        // (<script src="viz-global.js">), and elk.bundled.js (elkjs, loaded
        // on demand by ElkEngineJs) sits next to it; the rest stays under vendor/.
        include("viz-global.js", "elk.bundled.js")
      }
      from(teavmSrcDir.dir("vendor")) {
        exclude("viz-global.js", "elk.bundled.js")
        into("vendor")
      }
      into(outputDir)
```


**Tool Call: Edit license\-variants/plantuml\-mit/build.gradle.kts**
Status: Completed

Diff: /home/david/plantuml/license-variants/plantuml-mit/build.gradle.kts
```
      // (loaded from the root), third-party code keeps its vendor/ folder,
      // license included.
      from(teavmSrcDir.dir("vendor")) {
        // elkjs (EPL-2.0 OR GPL-3.0) is not shipped in the MIT package, like the
        // ELK jars, which only the EPL variant depends on: there,
        // "!pragma layout elk" falls back to the default engine unless the
        // host provides elkjs itself (PLANTUML_ELK)
        exclude("viz-global.js", "elk.bundled.js", "elkjs-LICENSE.md")
        into("vendor")
      }
      into(pkgDir)
```


## Assistant

Now a committed unit test. CI shouldn't need Node, so it uses a recorded elkjs answer. I'll build the test graph, run it through elkjs once, and embed the result:

**Tool Call: ls src/test/java/net/sourceforge/plantuml | head -30; grep -rl "@Test" src/test/java/net/sourceforge/plantuml/json 2>/dev/null | head -2; grep -rn "import static org.assertj\|import static org.junit.jupiter.api.Assertions" src/test/java | awk -F: '{print $3}' | sort | uniq -c | sort -rn | head -3**
Status: Completed

Terminal:
```
asciiverse
cheneer
cli
code
command
core
cucadiagram
elk
explain
gantt
help
json
klimt
licensing
LoadJsonTest.java
math
mcp
nio
openiconic
picoweb
PipeTest.java
png
preproc
ProgressBarTest.java
PSystemUtilsTest.java
real
regex
ScaleHeightTest.java
security
SkinParamTest.java
src/test/java/net/sourceforge/plantuml/json/JsonObjectTest.java
    209 import static org.junit.jupiter.api.Assertions.assertEquals;
    166 import static org.junit.jupiter.api.Assertions.assertTrue;
     76 import static org.junit.jupiter.api.Assertions.assertFalse;
```


**Tool Call: grep -rhn "^import static" src/test/java | sort | uniq -c | sort -rn | head -5; ls .clide/tmp/jar-junit/ 2>/dev/null | head**
Status: Completed

Terminal:
```
    173 3:import static org.junit.jupiter.api.Assertions.assertEquals;
     49 4:import static org.junit.jupiter.api.Assertions.assertTrue;
     45 3:import static org.junit.jupiter.api.Assertions.assertTrue;
     32 4:import static org.junit.jupiter.api.Assertions.assertFalse;
     31 3:import static org.junit.jupiter.api.Assertions.assertFalse;
junit-platform-console-standalone-1.11.3.jar
```


**Tool Call: ls src/test/java/net/sourceforge/plantuml/elk; head -50 $(ls src/test/java/net/sourceforge/plantuml/elk/*.java | head -1)**
Status: Completed

Terminal:
```
ElkPortTest.java
package net.sourceforge.plantuml.elk;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.atmp.CucaDiagram;
import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.klimt.shape.TextBlock;

/**
 * Regression test for issue #2277: NPE when rendering diagrams with ports using ELK layouter.
 *
 * <p>These tests call {@link CucaDiagramFileMakerElk#getTextBlock} directly so that
 * a {@link NullPointerException} from {@code EntityImagePort.upPosition()} propagates
 * without being swallowed by the higher-level error handling in PlantUML's export pipeline.</p>
 *
 * <p>ELK itself ({@code org.eclipse.elk.*}) is an optional runtime dependency: PlantUML compiles
 * against its own reflective proxy ({@link net.sourceforge.plantuml.elk.proxy}) so the project
 * builds without ELK on the classpath (see {@link CucaDiagramFileMakerElk}'s header comment), and
 * only needs the real ELK jars present when a diagram actually asks for {@code !pragma layout elk}.
 * On a build that does not pull it in (e.g. an Ant/non-Maven build), these tests are skipped rather
 * than failed, since a {@code ClassNotFoundException} there would not be this test's regression.</p>
 */
class ElkPortTest {

	@BeforeEach
	void requireElkOnClasspath() {
		Assumptions.assumeTrue(isElkAvailable(),
				"ELK (org.eclipse.elk.graph.util.ElkGraphUtil) is not on the classpath - skipping ELK-dependent test");
	}

	private static boolean isElkAvailable() {
		try {
			Class.forName("org.eclipse.elk.graph.util.ElkGraphUtil");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
```


## Assistant

I'll build the test graph with the new API, then record elkjs's answer for it:

**Tool Call: mkdir -p /tmp/elkharness/gen && cd /tmp/elkharness/gen && cat > Gen.java <<'EOF'
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph;
import net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraph.*;
public class Gen {
	public static void main(String[] args) {
		final Node root = ElkJsonGraph.createGraph();
		root.setOption("org.eclipse.elk.direction", "DOWN");
		root.setOption("org.eclipse.elk.hierarchyHandling", "INCLUDE_CHILDREN");
		final Node a = root.createNode(); a.setDimensions(80, 40);
		final Node g = root.createNode(); g.setOption("org.eclipse.elk.padding", "[top=40.0,left=15.0,bottom=15.0,right=15.0]"); g.createLabel().setText("C");
		final Node c = g.createNode(); c.setDimensions(60, 30);
		final Node d = g.createNode(); d.setDimensions(60, 30);
		final Edge ac = ElkJsonGraph.createSimpleEdge(a, c);
		final Label l = ac.createLabel(); l.setText("1"); l.setDimensions(20, 12); l.setOption("org.eclipse.elk.edgeLabels.placement", "TAIL");
		ac.setOption("org.eclipse.elk.edgeLabels.inline", true);
		ElkJsonGraph.createSimpleEdge(c, d);
		ElkJsonGraph.createSimpleEdge(g, c);
		System.out.println(ElkJsonGraph.toJson(root));
	}
}
EOF
javac -nowarn -d . -cp /home/david/plantuml/plantuml.jar Gen.java && java -cp .:/home/david/plantuml/plantuml.jar Gen > in.json && cat in.json; echo; node /tmp/elkspike/elk-cli.js /tmp/elkjs-dl/package/lib/elk.bundled.js < in.json > out.json && cat out.json**
Status: Completed

Terminal:
```
{"id":"root","layoutOptions":{"org.eclipse.elk.direction":"DOWN","org.eclipse.elk.hierarchyHandling":"INCLUDE_CHILDREN"},"width":0,"height":0,"children":[{"id":"n0","width":80,"height":40},{"id":"n1","layoutOptions":{"org.eclipse.elk.padding":"[top=40.0,left=15.0,bottom=15.0,right=15.0]"},"labels":[{"id":"l2","text":"C","width":0,"height":0}],"width":0,"height":0,"children":[{"id":"n3","width":60,"height":30},{"id":"n4","width":60,"height":30}],"edges":[{"id":"e7","sources":["n3"],"targets":["n4"]},{"id":"e8","sources":["n1"],"targets":["n3"]}]}],"edges":[{"id":"e5","layoutOptions":{"org.eclipse.elk.edgeLabels.inline":true},"labels":[{"id":"l6","layoutOptions":{"org.eclipse.elk.edgeLabels.placement":"TAIL"},"text":"1","width":20,"height":12}],"sources":["n0"],"targets":["n3"]}]}

{"id":"root","layoutOptions":{"org.eclipse.elk.direction":"DOWN","org.eclipse.elk.hierarchyHandling":"INCLUDE_CHILDREN"},"width":119,"height":238,"children":[{"id":"n0","width":80,"height":40,"$H":277,"x":27,"y":12},{"id":"n1","layoutOptions":{"org.eclipse.elk.padding":"[top=40.0,left=15.0,bottom=15.0,right=15.0]"},"labels":[{"id":"l2","text":"C","width":0,"height":0,"x":0,"y":0}],"width":90,"height":135,"children":[{"id":"n3","width":60,"height":30,"$H":283,"x":15,"y":40},{"id":"n4","width":60,"height":30,"$H":285,"x":15,"y":90}],"edges":[{"id":"e7","sources":["n3"],"targets":["n4"],"sections":[{"id":"e7_s0","startPoint":{"x":45,"y":70},"endPoint":{"x":45,"y":90},"incomingShape":"n3","outgoingShape":"n4"}],"container":"n1"},{"id":"e8","sources":["n1"],"targets":["n3"],"sections":[{"id":"e8_s0","startPoint":{"x":35,"y":0},"endPoint":{"x":35,"y":40},"incomingShape":"n1","outgoingShape":"n3"}],"container":"n1"}],"$H":279,"x":12,"y":91}],"edges":[{"id":"e5","layoutOptions":{"org.eclipse.elk.edgeLabels.inline":true},"labels":[{"id":"l6","layoutOptions":{"org.eclipse.elk.edgeLabels.placement":"TAIL"},"text":"1","width":20,"height":12,"x":70,"y":54}],"sources":["n0"],"targets":["n3"],"sections":[{"id":"e5_s0","startPoint":{"x":67,"y":52},"endPoint":{"x":67,"y":131},"incomingShape":"n0","outgoingShape":"n3"}],"container":"root"}],"$H":13,"x":0,"y":0}

Shell cwd was reset to /home/david/plantuml
```


## Assistant

elkjs's own `container` field in the answer (`e7`/`e8` → `n1`, `e5` → `root`) matches what my containment rule chose. Writing the test with this recorded answer:

**Tool Call: Write src/test/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonGraphTest.java**
Status: Completed

Diff: /home/david/plantuml/src/test/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonGraphTest.java
```
package net.sourceforge.plantuml.elk.proxy.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.plantuml.elk.proxy.core.RecursiveGraphLayoutEngine;
import net.sourceforge.plantuml.elk.proxy.core.math.ElkPadding;
import net.sourceforge.plantuml.elk.proxy.core.options.CoreOptions;
import net.sourceforge.plantuml.elk.proxy.core.options.Direction;
import net.sourceforge.plantuml.elk.proxy.core.options.EdgeLabelPlacement;
import net.sourceforge.plantuml.elk.proxy.core.options.HierarchyHandling;
import net.sourceforge.plantuml.elk.proxy.core.options.NodeLabelPlacement;
import net.sourceforge.plantuml.elk.proxy.core.util.NullElkProgressMonitor;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdge;
import net.sourceforge.plantuml.elk.proxy.graph.ElkEdgeSection;
import net.sourceforge.plantuml.elk.proxy.graph.ElkLabel;
import net.sourceforge.plantuml.elk.proxy.graph.ElkNode;
import net.sourceforge.plantuml.elk.proxy.graph.util.ElkGraphUtil;
import net.sourceforge.plantuml.json.Json;
import net.sourceforge.plantuml.json.JsonObject;

/**
 * The ELK graph written for elkjs (the browser build of
 * {@code !pragma layout elk}), and the layout read back. No JavaScript runs
 * here: the answer of elkjs 0.12.1 to {@link #GRAPH} is recorded in
 * {@link #LAYOUT}.
 */
class ElkJsonGraphTest {

	/**
	 * A node, a cluster (padding, label) holding two nodes, an edge from the node
	 * into the cluster with a tail label, an edge inside the cluster, and an edge
	 * from the cluster to one of its children. What {@link #buildGraph()} writes.
	 */
	private static final String GRAPH = "{\"id\":\"root\",\"layoutOptions\":{\"org.eclipse.elk.direction\":\"DOWN\","
			+ "\"org.eclipse.elk.hierarchyHandling\":\"INCLUDE_CHILDREN\"},\"width\":0,\"height\":0,\"children\":["
			+ "{\"id\":\"n0\",\"width\":80,\"height\":40},"
			+ "{\"id\":\"n1\",\"layoutOptions\":{\"org.eclipse.elk.direction\":\"DOWN\","
			+ "\"org.eclipse.elk.padding\":\"[top=40.0,left=15.0,bottom=15.0,right=15.0]\"},"
			+ "\"labels\":[{\"id\":\"l2\",\"text\":\"C\",\"width\":0,\"height\":0}],\"width\":0,\"height\":0,\"children\":["
			+ "{\"id\":\"n3\",\"width\":60,\"height\":30},{\"id\":\"n4\",\"width\":60,\"height\":30}],\"edges\":["
			+ "{\"id\":\"e7\",\"sources\":[\"n3\"],\"targets\":[\"n4\"]},"
			+ "{\"id\":\"e8\",\"sources\":[\"n1\"],\"targets\":[\"n3\"]}]}],\"edges\":["
			+ "{\"id\":\"e5\",\"layoutOptions\":{\"org.eclipse.elk.edgeLabels.inline\":true},\"labels\":["
			+ "{\"id\":\"l6\",\"layoutOptions\":{\"org.eclipse.elk.edgeLabels.placement\":\"TAIL\"},"
			+ "\"text\":\"1\",\"width\":20,\"height\":12}],\"sources\":[\"n0\"],\"targets\":[\"n3\"]}]}";

	/** The answer of elkjs 0.12.1 to {@link #GRAPH}, a bend point added to e7. */
	private static final String LAYOUT = "{\"id\":\"root\",\"width\":119,\"height\":238,\"children\":["
			+ "{\"id\":\"n0\",\"width\":80,\"height\":40,\"$H\":277,\"x\":27,\"y\":12},"
			+ "{\"id\":\"n1\",\"labels\":[{\"id\":\"l2\",\"text\":\"C\",\"width\":0,\"height\":0,\"x\":0,\"y\":0}],"
			+ "\"width\":90,\"height\":135,\"children\":["
			+ "{\"id\":\"n3\",\"width\":60,\"height\":30,\"$H\":283,\"x\":15,\"y\":40},"
			+ "{\"id\":\"n4\",\"width\":60,\"height\":30,\"$H\":285,\"x\":15,\"y\":90}],\"edges\":["
			+ "{\"id\":\"e7\",\"sources\":[\"n3\"],\"targets\":[\"n4\"],\"sections\":[{\"id\":\"e7_s0\","
			+ "\"startPoint\":{\"x\":45,\"y\":70},\"endPoint\":{\"x\":45,\"y\":90},"
			+ "\"bendPoints\":[{\"x\":45,\"y\":80}]}],\"container\":\"n1\"},"
			+ "{\"id\":\"e8\",\"sources\":[\"n1\"],\"targets\":[\"n3\"],\"sections\":[{\"id\":\"e8_s0\","
			+ "\"startPoint\":{\"x\":35,\"y\":0},\"endPoint\":{\"x\":35,\"y\":40}}],\"container\":\"n1\"}],"
			+ "\"x\":12,\"y\":91}],\"edges\":["
			+ "{\"id\":\"e5\",\"labels\":[{\"id\":\"l6\",\"text\":\"1\",\"width\":20,\"height\":12,\"x\":70,\"y\":54}],"
			+ "\"sources\":[\"n0\"],\"targets\":[\"n3\"],\"sections\":[{\"id\":\"e5_s0\","
			+ "\"startPoint\":{\"x\":67,\"y\":52},\"endPoint\":{\"x\":67,\"y\":131}}],\"container\":\"root\"}],"
			+ "\"x\":0,\"y\":0}";

	private String received;

	private ElkNode root;
	private ElkNode a;
	private ElkNode cluster;
	private ElkNode c;
	private ElkNode d;
	private ElkEdge ac;
	private ElkEdge cd;
	private ElkEdge clusterToC;

	@BeforeEach
	void registerLayouter() {
		ElkJsonGraph.setLayouter(new ElkJsonLayouter() {
			@Override
			public String layout(String graphJson) {
				received = graphJson;
				return LAYOUT;
			}
		});
	}

	@AfterEach
	void unregisterLayouter() {
		ElkJsonGraph.setLayouter(null);
	}

	/** Built through the proxies, as CucaDiagramFileMakerElk does. */
	private void buildGraph() {
		root = ElkGraphUtil.createGraph();
		root.setProperty(CoreOptions.DIRECTION, Direction.DOWN);
		root.setProperty(CoreOptions.HIERARCHY_HANDLING, HierarchyHandling.INCLUDE_CHILDREN);

		a = ElkGraphUtil.createNode(root);
		a.setDimensions(80, 40);

		cluster = ElkGraphUtil.createNode(root);
		cluster.setProperty(CoreOptions.DIRECTION, Direction.DOWN);
		cluster.setProperty(CoreOptions.PADDING, new ElkPadding(40, 15, 15, 15));
		ElkGraphUtil.createLabel(cluster).setText("C");

		c = ElkGraphUtil.createNode(cluster);
		c.setDimensions(60, 30);
		d = ElkGraphUtil.createNode(cluster);
		d.setDimensions(60, 30);

		ac = ElkGraphUtil.createSimpleEdge(a, c);
		final ElkLabel tail = ElkGraphUtil.createLabel(ac);
		tail.setText("1");
		tail.setDimensions(20, 12);
		tail.setProperty(CoreOptions.EDGE_LABELS_PLACEMENT, EdgeLabelPlacement.TAIL);
		ac.setProperty(CoreOptions.EDGE_LABELS_INLINE, true);

		cd = ElkGraphUtil.createSimpleEdge(c, d);
		clusterToC = ElkGraphUtil.createSimpleEdge(cluster, c);
	}

	@Test
	void writesTheElkJsonFormat() {
		buildGraph();
		assertEquals(Json.parse(GRAPH), Json.parse(ElkJsonGraph.toJson((ElkJsonGraph.Node) root.obj)));
	}

	@Test
	void writesOptionValuesAsElkParsesThem() {
		final ElkNode graph = ElkGraphUtil.createGraph();
		final ElkLabel label = ElkGraphUtil.createLabel(ElkGraphUtil.createNode(graph));
		label.setProperty(CoreOptions.NODE_LABELS_PLACEMENT, java.util.EnumSet.of(NodeLabelPlacement.INSIDE,
				NodeLabelPlacement.H_CENTER, NodeLabelPlacement.V_CENTER));
		final JsonObject json = Json.parse(ElkJsonGraph.toJson((ElkJsonGraph.Node) graph.obj)).asObject();
		final JsonObject options = json.get("children").asArray().get(0).asObject().get("labels").asArray().get(0)
				.asObject().get("layoutOptions").asObject();
		// EnumSet order: the declaration order of the proxy enum
		assertEquals("[INSIDE, V_CENTER, H_CENTER]", options.getString("org.eclipse.elk.nodeLabels.placement", null));
	}

	@Test
	void putsEachEdgeInTheNodeElkJavaWould() {
		buildGraph();
		// Lowest common ancestor, a node counting as its own ancestor
		assertSame(root.obj, ac.getContainingNode().obj);
		assertSame(cluster.obj, cd.getContainingNode().obj);
		assertSame(cluster.obj, clusterToC.getContainingNode().obj);
		// A self loop belongs to the parent of its node
		assertSame(cluster.obj, ElkGraphUtil.createSimpleEdge(d, d).getContainingNode().obj);
		assertSame(root.obj, ElkGraphUtil.createSimpleEdge(a, a).getContainingNode().obj);
	}

	@Test
	void readsTheLayoutBack() {
		buildGraph();
		new RecursiveGraphLayoutEngine().layout(root, new NullElkProgressMonitor());
		assertEquals(Json.parse(GRAPH), Json.parse(received));

		// Nodes: relative to their parent
		assertEquals(27, a.getX());
		assertEquals(12, a.getY());
		assertEquals(12, cluster.getX());
		assertEquals(91, cluster.getY());
		assertEquals(90, cluster.getWidth());
		assertEquals(135, cluster.getHeight());
		assertEquals(15, c.getX());
		assertEquals(40, c.getY());
		assertSame(cluster.obj, c.getParent().obj);
		assertNull(root.getParent());

		// Edges: relative to their containing node
		final List<ElkEdgeSection> sections = cd.getSections();
		assertEquals(1, sections.size());
		assertEquals(45, sections.get(0).getStartX());
		assertEquals(70, sections.get(0).getStartY());
		assertEquals(45, sections.get(0).getEndX());
		assertEquals(90, sections.get(0).getEndY());
		assertEquals(1, sections.get(0).getBendPoints().size());
		assertEquals(80, sections.get(0).getBendPoints().iterator().next().getY());
		assertEquals(131, ac.getSections().get(0).getEndY());

		final ElkLabel tail = ac.getLabels().iterator().next();
		assertEquals("1", tail.getText());
		assertEquals(70, tail.getX());
		assertEquals(54, tail.getY());
	}

	@Test
	void reportsAnAnswerForAnotherGraph() {
		buildGraph();
		ElkJsonGraph.setLayouter(new ElkJsonLayouter() {
			@Override
			public String layout(String graphJson) {
				return "{\"id\":\"root\",\"children\":[{\"id\":\"n99\"}]}";
			}
		});
		assertThrows(IllegalArgumentException.class,
				() -> new RecursiveGraphLayoutEngine().layout(root, new NullElkProgressMonitor()));
	}

}

```


**Tool Call: ant -q dist 2>&1 | grep -v "^\s*\[javac\] Note" | tail -4 && rm -rf /tmp/testclasses && javac -nowarn -d /tmp/testclasses -cp "plantuml.jar:.clide/*:.clide/tmp/jar-junit/*" -sourcepath src/test/java src/test/java/net/sourceforge/plantuml/elk/proxy/json/ElkJsonGraphTest.java src/test/java/net/sourceforge/plantuml/elk/ElkPortTest.java 2>&1 | grep -v "^Note" ; java -jar .clide/tmp/jar-junit/junit-platform-console-standalone-*.jar execute -cp "plantuml.jar:/tmp/testclasses:src/test/resources" --select-class net.sourceforge.plantuml.elk.proxy.json.ElkJsonGraphTest --details=tree 2>&1 | tail -25**
Status: Completed

Terminal:
```
     [echo] OK: all required entries are present in plantuml.jar.

BUILD SUCCESSFUL
Total time: 33 seconds
╷
├─ JUnit Platform Suite ✔
├─ JUnit Jupiter ✔
│  └─ ElkJsonGraphTest ✔
│     ├─ reportsAnAnswerForAnotherGraph() ✔
│     ├─ putsEachEdgeInTheNodeElkJavaWould() ✔
│     ├─ writesTheElkJsonFormat() ✔
│     ├─ readsTheLayoutBack() ✔
│     └─ writesOptionValuesAsElkParsesThem() ✔
└─ JUnit Vintage ✔

Test run finished after 187 ms
[         4 containers found      ]
[         0 containers skipped    ]
[         4 containers started    ]
[         0 containers aborted    ]
[         4 containers successful ]
[         0 containers failed     ]
[         5 tests found           ]
[         0 tests skipped         ]
[         5 tests started         ]
[         0 tests aborted         ]
[         5 tests successful      ]
[         0 tests failed          ]
```


## Assistant

<thinking>
All five tests pass; next I'll run two JVM regression checks on the reflective ELK path, using the existing ElkPortTest and a render comparison against develop's jar.


</thinking>

**Tool Call: java -jar .clide/tmp/jar-junit/junit-platform-console-standalone-*.jar execute -cp "plantuml.jar:/tmp/testclasses:src/test/resources:/tmp/elkjars/*" --select-class net.sourceforge.plantuml.elk.ElkPortTest --details=summary 2>&1 | grep -E "tests (successful|failed|skipped)"; rm -rf /tmp/plantuml-develop && git worktree add -q /tmp/plantuml-develop develop && cd /tmp/plantuml-develop && ant -q dist 2>&1 | grep -E "BUILD"**
Status: Completed

Terminal:
```
[         0 tests skipped         ]
[         0 tests successful      ]
[         0 tests failed          ]
BUILD SUCCESSFUL

Shell cwd was reset to /home/david/plantuml
```


## Assistant

`ElkPortTest` ran zero tests. Let me see why:

**Tool Call: ls /tmp/testclasses/net/sourceforge/plantuml/elk/; java -jar .clide/tmp/jar-junit/junit-platform-console-standalone-*.jar execute -cp "plantuml.jar:/tmp/testclasses:src/test/resources:/tmp/elkjars/*" --select-class net.sourceforge.plantuml.elk.ElkPortTest --details=tree 2>&1 | head -30; grep -n "@Test\|@Disabled\|void " src/test/java/net/sourceforge/plantuml/elk/ElkPortTest.java | head**
Status: Completed

Terminal:
```
ElkPortTest.class
proxy

Thanks for using JUnit! Support its development at https://junit.org/sponsoring

╷
├─ JUnit Platform Suite ✔
├─ JUnit Jupiter ✔
│  └─ ElkPortTest ✔
│     ├─ testFullDiagramFromIssue2277() ■ Assumption failed: ELK (org.eclipse.elk.graph.util.ElkGraphUtil) is not on the classpath - skipping ELK-dependent test
│     ├─ testPortInPortOutWithElkDoesNotThrowNPE() ■ Assumption failed: ELK (org.eclipse.elk.graph.util.ElkGraphUtil) is not on the classpath - skipping ELK-dependent test
│     └─ testPortWithElkDoesNotThrowNPE() ■ Assumption failed: ELK (org.eclipse.elk.graph.util.ElkGraphUtil) is not on the classpath - skipping ELK-dependent test
└─ JUnit Vintage ✔

Test run finished after 145 ms
[         4 containers found      ]
[         0 containers skipped    ]
[         4 containers started    ]
[         0 containers aborted    ]
[         4 containers successful ]
[         0 containers failed     ]
[         3 tests found           ]
[         0 tests skipped         ]
[         3 tests started         ]
[         3 tests aborted         ]
[         0 tests successful      ]
[         0 tests failed          ]

37:	void requireElkOnClasspath() {
51:	@Test
52:	void testPortWithElkDoesNotThrowNPE() {
63:	@Test
64:	void testPortInPortOutWithElkDoesNotThrowNPE() {
79:	@Test
80:	void testFullDiagramFromIssue2277() {
```


## Assistant

The JUnit launcher's own `-cp` doesn't expand `*` wildcards, so the ELK jars weren't on the path. Listing them explicitly:

**Tool Call: ELK=$(ls /tmp/elkjars/*.jar | tr '\n' ':'); java -jar .clide/tmp/jar-junit/junit-platform-console-standalone-*.jar execute -cp "plantuml.jar:/tmp/testclasses:src/test/resources:$ELK" --select-class net.sourceforge.plantuml.elk.ElkPortTest --details=tree 2>&1 | grep -E "✔|✘|■|tests (successful|failed|aborted)"**
Status: Completed

Terminal:
```
├─ JUnit Platform Suite ✔
├─ JUnit Jupiter ✔
│  └─ ElkPortTest ✔
│     ├─ testFullDiagramFromIssue2277() ✔
│     ├─ testPortInPortOutWithElkDoesNotThrowNPE() ✔
│     └─ testPortWithElkDoesNotThrowNPE() ✔
└─ JUnit Vintage ✔
[         0 tests aborted         ]
[         3 tests successful      ]
[         0 tests failed          ]
```


## Assistant

Now `develop`'s desktop ELK output against this branch's:

**Tool Call: cd /tmp/elkharness && java -Djava.awt.headless=true -cp "classes:/tmp/plantuml-develop/plantuml.jar:/tmp/elkjars/*" ElkCompare java out-develop cases/*.puml 2>&1 | tail -3; java -Djava.awt.headless=true -cp "classes:/home/david/plantuml/plantuml.jar:/tmp/elkjars/*" ElkCompare java out-java3 cases/*.puml 2>&1 | tail -3; java -Djava.awt.headless=true -cp "classes:/home/david/plantuml/plantuml.jar" ElkCompare json out-json3 cases/*.puml 2>/dev/null; for f in out-develop/*.svg; do n=$(basename $f); a=$(cmp -s $f out-java3/$n && echo same || echo DIFF); b=$(cmp -s $f out-json3/$n && echo same || echo DIFF); echo "$n  develop-vs-branch(ELK Java): $a   develop-vs-branch(elkjs): $b"; done**
Status: Completed

Terminal:
```
class_basic.svg  develop-vs-branch(ELK Java): same   develop-vs-branch(elkjs): same
class_packages.svg  develop-vs-branch(ELK Java): same   develop-vs-branch(elkjs): same
component.svg  develop-vs-branch(ELK Java): same   develop-vs-branch(elkjs): same
edge_to_cluster.svg  develop-vs-branch(ELK Java): same   develop-vs-branch(elkjs): same
state.svg  develop-vs-branch(ELK Java): same   develop-vs-branch(elkjs): same
usecase.svg  develop-vs-branch(ELK Java): same   develop-vs-branch(elkjs): same

Shell cwd was reset to /home/david/plantuml
```


## Assistant

<thinking>
The desktop path is unchanged and elkjs matches it on all six diagrams. Next I'm compiling the browser build with TeaVM, which will take a few minutes while Gradle fetches dependencies.


</thinking>

**Tool Call: timeout 590 ./gradlew teavm -Pfast --console=plain 2>&1 | grep -v "^> Task\|^$" | tail -30**
Status: Completed

Terminal:
```
  k8s.min.js                0.05 MB
  kubernetes.min.js         0.29 MB
  logos.min.js              5.20 MB
  main.js                   0.01 MB
  material.min.js           0.00 MB
  material2.1.19.min.js     6.66 MB
  material2.min.js          0.00 MB
  material7.4.47.min.js    16.36 MB
  material7.min.js          0.00 MB
  office.min.js             1.36 MB
  openiconic.js             0.05 MB
  osa.min.js                1.85 MB
  osa2.min.js               0.10 MB
  plantuml-codec.js         0.00 MB
  plantuml.js               3.35 MB
  themes.js                 0.31 MB
  tupadr3.min.js           19.29 MB
  viz-global.js             1.38 MB
  zoom.js                   0.00 MB
  TOTAL                   107.93 MB
======================
TeaVM Ready!  --> /home/david/plantuml/build/generated/teavm/js/index.html
======================
[Incubating] Problems report is available at: file:///home/david/plantuml/build/reports/problems/problems-report.html
Deprecated Gradle features were used in this build, making it incompatible with Gradle 10.
You can use '--warning-mode all' to show the individual deprecation warnings and determine if they come from your own scripts or plugins.
For more on this, please refer to https://docs.gradle.org/9.3.1/userguide/command_line_interface.html#sec:command_line_warnings in the Gradle documentation.
BUILD SUCCESSFUL in 4m 3s
5 actionable tasks: 5 executed
Consider enabling configuration cache to speed up this build: https://docs.gradle.org/9.3.1/userguide/configuration_cache_enabling.html
```


## Assistant

The TeaVM build succeeds. Now checking what landed in the bundle: elkjs deployed at the root, the bridge compiled in, and the reflection code pruned.

**Tool Call: cd /home/david/plantuml/build/generated/teavm/js && ls -la elk.bundled.js vendor/ plantuml.js; echo "bridge script name present: $(grep -c 'elk.bundled.js' plantuml.js)"; echo "dispatcher code present: $(grep -c 'saveDispatch' plantuml.js)"; echo "reflective ELK class names present (want 0): $(grep -o 'org.eclipse.elk.graph.util.ElkGraphUtil\|org.eclipse.elk.core.RecursiveGraphLayoutEngine' plantuml.js | wc -l)"; echo "JSON option ids present: $(grep -o 'org.eclipse.elk.hierarchyHandling' plantuml.js | wc -l)"**
Status: Completed

Terminal:
```
-rw-r--r-- 1 david david 1646241 Nov  6 05:49 elk.bundled.js
-rw-rw-r-- 1 david david 3513232 Nov  6 05:49 plantuml.js

vendor/:
total 60
drwxr-xr-x 2 david david  4096 Nov  6 05:49 .
drwxrwxr-x 4 david david  4096 Nov  6 05:49 ..
-rw-r--r-- 1 david david 14443 Nov  6 05:49 elkjs-LICENSE.md
-rw-rw-r-- 1 david david 32666 Nov  6 05:49 fflate-0.8.2.min.js
-rw-rw-r-- 1 david david  1070 Nov  6 05:49 fflate-LICENSE.txt
bridge script name present: 2
dispatcher code present: 2
reflective ELK class names present (want 0): 2
JSON option ids present: 1
```


## Assistant

Two ELK Java class names survive. Let me check whether that's reflection code or just string constants at the call sites:

**Tool Call: grep -o '.\{120\}org.eclipse.elk.graph.util.ElkGraphUtil.\{60\}' plantuml.js | head -2; grep -o '.\{100\}org.eclipse.elk.core.RecursiveGraphLayoutEngine.\{40\}' plantuml.js | head -1; echo; echo "NO_REFLECTION message present: $(grep -c 'ELK through reflection is not available' plantuml.js)"; echo "getEnumConstants/getMethods present: $(grep -c 'getEnumConstants\|getMethods' plantuml.js)"**
Status: Completed

Terminal:
```
OLLOW"," visible=","getParent","getX","getY","getLabels","NOT YET IMPLEMENTED","setDimensions","X","org.eclipse.elk.core.RecursiveGraphLayoutEngine","org.eclipse.elk.core.util.NullElkProg

NO_REFLECTION message present: 1
getEnumConstants/getMethods present: 0

```


## Assistant

<thinking>
The bundle no longer contains the reflection machinery; only leftover string literals at call sites remain, which now hit the guard. Next I'll run an end-to-end test of the actual plantuml.js using the repo's browser-test tool.


</thinking>

**Tool Call: cd /home/david/plantuml && sed -n 1,80p tools/browser-test/README.md; ls tools/browser-test; which chromium chromium-browser google-chrome firefox 2>/dev/null; ls ~/.cache/ms-playwright 2>/dev/null**
Status: Failed

Terminal:
```
Exit code 2
# browser-test

Functional checks for the PlantUML browser (TeaVM) engine, run in headless Chromium against a
real build. Unlike `perf-bench`, which measures speed and never fails a build, these assert
behaviour and exit non-zero when it is wrong.

## check-themes.js

Checks that `!theme <name>` works in the browser.

Themes live in `src/main/resources/themes` as `.puml` files, which the browser build cannot read
because it has no classpath. They are shipped to it instead through
`src/main/teavm/generated/themes.js` (written by `ThemesJsGenerator`, deployed next to
`plantuml.js`), loaded on demand the same way `emoji.js` is. This check covers that path end
to end:

- a theme changes the drawing, and changes it to that theme's own colours;
- loading a theme by name produces exactly what pasting the theme's body into the diagram does,
  which is the real correctness property (both sides are rendered by the same engine, so only
  the loading path differs);
- an unknown theme name is reported rather than ignored;
- `%get_current_theme()` returns the metadata from the theme's YAML header;
- `%get_all_theme()` does not advertise more themes than the engine ships;
- every bundled theme loads and changes the drawing, except ones with an empty body
  (`_none_` is deliberately empty), which must leave it unchanged;
- a host that populates `globalThis.PLANTUML_THEMES` itself works without `themes.js` being fetchable,
  which is what lets themes work inside a Web Worker, where the script loader has no document
  to append a script tag to;
- with neither, the directive fails loudly instead of rendering unthemed.

There are no golden files. The expected rendering for a theme is derived from that theme's own
text, read out of the `themes.js` being tested, so adding or editing a theme needs no fixture
update.

## check-background.js

Checks that the document background is painted the way the Java build paints it: a
background style on the svg element plus a rectangle covering the whole viewBox, for
`skinparam backgroundColor`, the `<style>` document form, and themes. The deliberate
skips are pinned too: transparent and the default white paint nothing, in dark mode as
well. Run the same way with `node check-background.js target=...`.

## check-smetana.js

Checks that `!pragma layout smetana` works in the browser. On a page without `viz-global.js`,
each Graphviz-family diagram type (class, component, deployment, state, usecase) declaring the
pragma must render a real SVG with zero WebAssembly involvement, observed through a hook on
`WebAssembly.instantiate`. On a control page with `viz-global.js`, the same diagram without the
pragma must still go through the Graphviz bridge (the default path is unchanged), and with the
pragma must not touch WebAssembly even though the bridge is available. Run the same way with
`node check-smetana.js target=...`.

## check-viz-missing.js

Checks how the engine behaves when `viz-global.js` is missing or broken. With viz absent,
Graphviz-family diagram types fall back to the Smetana layout engine and render normally
(check-viz-fallback.js pins the details), and native-layout types (sequence, activity) keep
rendering as always. With a Viz that is present but broken (its `instance()` rejects, the shape
of a failed load), the render must produce a visible crash report naming Viz instead of throwing
an unhandled exception and leaving the target element empty. A control page with a working
`viz-global.js` pins that the normal path is unchanged. Run the same
way with `node check-viz-missing.js target=...`.

## check-stdlib-loader.js

Checks how the engine loads stdlib bundles for `!include <lib/...>`, and the two host-provided
globals that customize it:

- with neither global set, behaviour is unchanged: a bundle next to the page loads through a
  relative script tag, a missing bundle fails the include visibly instead of hanging, and
  diagrams without stdlib includes are untouched;
- `PLANTUML_STDLIB_BASE` prefixes the script URL, so a page importing the engine from a CDN can
  point bundle loading at the project site or its own assets with one line (relative URLs
  resolve against the consuming document, not the engine, so without it stdlib only works for
  pages hosted next to the bundles);
- `PLANTUML_STDLIB_LOADER` replaces the script tag with a host callback that delivers the
  bundle as data, the only viable path for hosts that cannot execute remote code (browser
  extensions under the Chrome MV3 and Mozilla AMO rules) or that have no document (Web
  Workers). The check asserts no `.min.js` is fetched at all, that concurrent includes of one
  library coalesce into a single loader call, that a library whose info carries a `link` to
check-background.js
check-file-loader.js
check-render-queue.js
check-resource-rounds.js
check-smetana.js
check-stdlib-loader.js
check-themes.js
check-viz-fallback.js
check-viz-missing.js
package.json
package-lock.json
README.md
/usr/bin/firefox
```


## Assistant

`check-smetana.js` is close to what I need for an `elk` check. Let me see how these scripts drive the browser:

**Tool Call: cd /home/david/plantuml/tools/browser-test && cat package.json; ls node_modules 2>/dev/null | head; sed -n 1,200p check-smetana.js**
Status: Completed

Terminal:
```
{
  "name": "plantuml-browser-test",
  "private": true,
  "description": "Functional browser checks for the PlantUML browser (TeaVM) engine",
  "dependencies": {
    "playwright": "1.62.1"
  }
}
'use strict';
// Functional check that "!pragma layout smetana" works in the browser (TeaVM) engine.
//
// usage: node check-smetana.js target=<dir-or-js>
//   target   a directory containing plantuml.js (viz-global.js is served from there
//            too for the default-path control), or a path to the engine .js file itself
//
// Before the fix this check pins, the browser build parsed the pragma and then
// silently ignored it: CommandPragma skipped all layout handling under TeaVM and
// the maker selection took CucaDiagramFileMakerTeaVM unconditionally, so
// CucaDiagramFileMakerSmetana was dead-code-eliminated from the generated
// JavaScript and every Graphviz-family diagram required viz-global.js and
// WebAssembly.
//
// The contract checked here, on a page that does NOT load viz-global.js: each
// Graphviz-family diagram type (class, component, deployment, state, usecase)
// declaring "!pragma layout smetana" renders a real SVG with pure-Java layout and
// zero WebAssembly involvement. On a control page WITH viz-global.js, the same
// diagrams without the pragma still render through the Graphviz bridge (observed
// via a WebAssembly.instantiate hook), so the default path is unchanged, and with
// the pragma they render without touching WebAssembly at all.
const fs = require('fs');
const path = require('path');
const { createCheckReporter, isErrorImage } = require('../lib/browser-check');
const { parseTargetArg } = require('../lib/browser-cli');
const { createMountedServer, startServer } = require('../lib/browser-http');
const { createModulePageHtml, loadPlaywright, makeRenderModuleBody, maybeScriptTag, openReadyPage, renderOn } = require('../lib/browser-page');

const scriptName = path.basename(__filename, '.js');
const { dir, file } = parseTargetArg(process.argv, `node ${scriptName}.js target=<dir-or-js>`);
const pw = loadPlaywright();

if (!fs.existsSync(path.join(dir, 'viz-global.js'))) {
  console.error('viz-global.js not found next to the engine in ' + dir + ' (needed for the control page)');
  process.exit(2);
}

// The WebAssembly hook counts instantiations so a check can assert whether a
// render used the Graphviz bridge (viz-global.js is WebAssembly) or not.
const hook = `<script>
window.__wasm = 0;
['compile','instantiate','instantiateStreaming','compileStreaming'].forEach(function (k) {
  var o = WebAssembly[k];
  if (o) WebAssembly[k] = function () { window.__wasm++; return o.apply(WebAssembly, arguments); };
});
</script>`;

const pageHtml = withViz => createModulePageHtml({
  headHtml: hook,
  bodyHtml: withViz ? maybeScriptTag(dir, 'viz-global.js', '/viz-global.js') : '',
  modulePath: `/${file}`,
  moduleBody: makeRenderModuleBody({ maxSvgSize: 98304 }),
});

const server = createMountedServer({
  routes: {
    '/index.html': { contentType: 'text/html', body: pageHtml(false) },
    '/index-viz.html': { contentType: 'text/html', body: pageHtml(true) },
  },
  mounts: [{ prefix: '/', dir }],
});

const { check, finish } = createCheckReporter();

const FAMILIES = [
  ['class', ['class Car {', '  +drive(): void', '}', 'class Engine', 'class Wheel', 'Car *-- Engine', 'Car *-- "4" Wheel']],
  ['component', ['[Web UI] --> [API Gateway]', '[Mobile App] --> [API Gateway]', '[API Gateway] --> [Orders]']],
  ['deployment', ['node "Load Balancer" as lb', 'node "App Server" as app', 'database "Primary" as db', 'lb --> app', 'app --> db']],
  ['state', ['[*] --> Idle', 'Idle --> Running : start', 'Running --> Idle : stop', 'Running --> [*]']],
  ['usecase', ['actor User', 'User --> (Login)', 'User --> (Browse)', '(Browse) --> (Checkout)']],
  // The paths below have their own machinery on the Smetana side and are pinned separately:
  // composite states go through CucaDiagramSimplifierStateSmetana and a nested sub-layout,
  // packages go through the cluster export, a note on a link goes through the opale path,
  // and left to right direction goes through the rankdir attribute.
  ['composite state', ['[*] --> Working', 'state Working {', '  [*] --> Fetching', '  Fetching --> Parsing : done', '}', 'Working --> [*] : shutdown']],
  ['packaged component', ['package "Frontend" {', '  [Web UI]', '}', 'package "Backend" {', '  [API Gateway]', '}', '[Web UI] --> [API Gateway]']],
  ['link note class', ['class Car', 'class Engine', 'Car *-- Engine', 'note on link: fitted at the factory']],
  ['left to right class', ['left to right direction', 'class Car', 'class Engine', 'class Wheel', 'Car *-- Engine', 'Car *-- Wheel']],
];
const diagram = (body, pragma) => ['@startuml', ...(pragma ? ['!pragma layout smetana'] : []), ...body, '@enduml'];

(async () => {
  const port = await startServer(server);
  const browser = await pw.chromium.launch({ headless: true });

  // Page 1: engine only, no viz-global.js. The pragma must be enough.
  const bareReady = await openReadyPage(browser, `http://127.0.0.1:${port}/index.html`, { polling: 200 });
  const bare = bareReady.page;
  const bareErrors = bareReady.errors;

  for (const [label, body] of FAMILIES) {
    const r = await renderOn(bare, diagram(body, true), {
      includeWasmCount: true,
      includeShapeCounts: true,
      maxTextLength: 120,
    });
    const ok = !r.thrown && !!r.svg && !isErrorImage(r.svg) && r.shapes > 0 && r.texts > 0 && r.wasm === 0;
    check(`smetana ${label} diagram renders without \`viz-global.js\``, ok,
      r.thrown || (!r.svg ? 'no svg: ' + r.text.slice(0, 120)
        : isErrorImage(r.svg) ? 'error image'
        : r.wasm !== 0 ? 'unexpected WebAssembly use (' + r.wasm + ')'
        : 'svg but no drawn content (shapes=' + r.shapes + ' texts=' + r.texts + ')'));
  }
  check('no unhandled page errors on the viz-less page', bareErrors.length === 0, bareErrors.join(' | '));

  // Page 2 (control): viz-global.js loaded. Without the pragma the Graphviz
  // bridge must still be used (default path unchanged); with the pragma the
  // render must not touch WebAssembly.
  const ctrlReady = await openReadyPage(browser, `http://127.0.0.1:${port}/index-viz.html`, { polling: 200 });
  const ctrl = ctrlReady.page;
  const ctrlErrors = ctrlReady.errors;

  const viaViz = await renderOn(ctrl, diagram(FAMILIES[0][1], false), {
    includeWasmCount: true,
    maxTextLength: 120,
  });
  check('control: class diagram without the pragma still uses the Graphviz bridge',
    !viaViz.thrown && !!viaViz.svg && !isErrorImage(viaViz.svg) && viaViz.wasm > 0,
    viaViz.thrown || (!viaViz.svg ? 'no svg: ' + viaViz.text.slice(0, 120)
      : viaViz.wasm === 0 ? 'render used no WebAssembly, default path changed' : 'error image'));

  const viaSmetana = await renderOn(ctrl, diagram(FAMILIES[0][1], true), {
    includeWasmCount: true,
    maxTextLength: 120,
  });
  check('control: class diagram with the pragma ignores `viz-global.js` even when loaded',
    !viaSmetana.thrown && !!viaSmetana.svg && !isErrorImage(viaSmetana.svg) && viaSmetana.wasm === 0,
    viaSmetana.thrown || (!viaSmetana.svg ? 'no svg: ' + viaSmetana.text.slice(0, 120)
      : viaSmetana.wasm !== 0 ? 'WebAssembly used (' + viaSmetana.wasm + ')' : 'error image'));

  check('no unhandled page errors on the control page', ctrlErrors.length === 0, ctrlErrors.join(' | '));

  await browser.close();
  server.close();
  finish(scriptName, { uppercase: true });
})().catch(e => { console.error(e); process.exit(2); });
```


## Assistant

<thinking>
I'm writing a check-elk.js for the full browser flow, and installing the repo's pinned Playwright 1.62.1 and its headless Chromium to run it.


</thinking>

**Tool Call: npm ci --no-audit --no-fund 2>&1 | tail -2 && npx playwright install chromium 2>&1 | tail -3**
Status: Completed

Terminal:
```
added 2 packages in 2s
|■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■        |  90% of 114.7 MiB
|■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■| 100% of 114.7 MiB
Chrome Headless Shell 151.0.7922.34 (playwright chromium-headless-shell v1234) downloaded to /home/david/.cache/ms-playwright/chromium_headless_shell-1234

```


**Tool Call: cd /home/david/plantuml/tools && ls lib; grep -n "^function\|^const .* = (\|^async function\|module.exports" lib/browser-page.js | head -30; sed -n '/function makeRenderModuleBody/,/^}/p' lib/browser-page.js | head -60**
Status: Completed

Terminal:
```
browser-check.js
browser-cli.js
browser-http.js
browser-page.js
6:function loadPlaywright() {
23:function maybeScriptTag(dir, fileName, requestPath) {
27:function createModulePageHtml(options) {
36:function makeRenderModuleBody(options) {
50:function shortPageError(err) {
54:async function openReadyPage(browser, url, options) {
77:async function renderOn(page, lines, options) {
143:function makeRenderer(page, options) {
153:async function openRenderer(browser, url, openOptions, renderOptions) {
161:function delay(ms) {
165:module.exports = {
function makeRenderModuleBody(options) {
  const opt = options || {};
  const baseOptions = {};
  if (typeof opt.maxSvgSize !== 'undefined')
    baseOptions.maxSvgSize = opt.maxSvgSize;
  const baseOptionsJson = JSON.stringify(baseOptions);
  if (opt.allowOverrides) {
    return `window.__render=(lines,id,opts)=>render(lines,id,Object.assign(${baseOptionsJson},opts||{}));
window.__ready=1;`;
  }
  return `window.__render=(lines,id)=>render(lines,id,${baseOptionsJson});
window.__ready=1;`;
}
```


**Tool Call: sed -n 1,165p lib/browser-page.js | sed -n 20,160p; sed -n 1,60p lib/browser-http.js | grep -n "function\|onRequest\|log" | head**
Status: Completed

Terminal:
```
  }
}

function maybeScriptTag(dir, fileName, requestPath) {
  return fs.existsSync(path.join(dir, fileName)) ? `<script src="${requestPath || '/' + fileName}"></script>` : '';
}

function createModulePageHtml(options) {
  return `<!doctype html><html><head>${options.headHtml || ''}</head><body><div id="out"></div>
${options.bodyHtml || ''}
<script type="module">
import {render} from '${options.modulePath}';
${options.moduleBody}
</script></body></html>`;
}

function makeRenderModuleBody(options) {
  const opt = options || {};
  const baseOptions = {};
  if (typeof opt.maxSvgSize !== 'undefined')
    baseOptions.maxSvgSize = opt.maxSvgSize;
  const baseOptionsJson = JSON.stringify(baseOptions);
  if (opt.allowOverrides) {
    return `window.__render=(lines,id,opts)=>render(lines,id,Object.assign(${baseOptionsJson},opts||{}));
window.__ready=1;`;
  }
  return `window.__render=(lines,id)=>render(lines,id,${baseOptionsJson});
window.__ready=1;`;
}

function shortPageError(err) {
  return String(err && err.message || err).split('\n')[0];
}

async function openReadyPage(browser, url, options) {
  const opt = options || {};
  const page = await browser.newPage();
  const errors = [];
  try {
    if (opt.trackErrors !== false)
      page.on('pageerror', err => errors.push(shortPageError(err)));
    if (typeof opt.onConsole === 'function')
      page.on('console', opt.onConsole);
    if (typeof opt.beforeGoto === 'function')
      await opt.beforeGoto(page);
    await page.goto(url, { waitUntil: opt.waitUntil || 'load' });
    await page.waitForFunction(opt.readyExpression || 'window.__ready && window.__render', null, {
      timeout: opt.timeout || 120000,
      polling: opt.polling,
    });
  } catch (e) {
    await page.close().catch(() => {});
    throw e;
  }
  return { page, errors };
}

async function renderOn(page, lines, options) {
  const opt = options || {};
  const timeoutMs = opt.timeoutMs || 30000;
  const targetId = opt.targetId || 'out';
  return page.evaluate(async ({ lines, timeoutMs, targetId, renderOptions, includeTiming, includeHash, includeTruncation, includeLoaderCalls, includeShapeCounts, includeWasmCount, maxTextLength }) => {
    const out = document.getElementById(targetId);
    out.innerHTML = '';
    const t0 = includeTiming ? performance.now() : 0;
    const w0 = includeWasmCount && typeof window.__wasm === 'number' ? window.__wasm : null;
    const done = new Promise(res => {
      const mo = new MutationObserver(() => {
        if (out.querySelector('svg') || out.textContent) {
          mo.disconnect();
          res();
        }
      });
      mo.observe(out, { childList: true, subtree: true });
    });
    let thrown = null;
    try {
      window.__render(lines, targetId, renderOptions);
    } catch (e) {
      thrown = String((e && e.message) || e);
    }
    if (!thrown)
      await Promise.race([done, new Promise(r => setTimeout(r, timeoutMs))]);
    const svg = out.querySelector('svg');
    const svgHtml = svg ? svg.outerHTML : null;
    let sha = null;
    if (includeHash && svgHtml) {
      const buf = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(svgHtml));
      sha = [...new Uint8Array(buf)].map(b => b.toString(16).padStart(2, '0')).join('');
    }
    let text = out.textContent || '';
    if (maxTextLength && text.length > maxTextLength)
      text = text.slice(0, maxTextLength);
    const wasmNow = includeWasmCount && typeof window.__wasm === 'number' ? window.__wasm : null;
    return {
      err: thrown,
      thrown,
      svg: svgHtml,
      text,
      wasm: w0 === null || wasmNow === null ? null : wasmNow - w0,
      shapes: includeShapeCounts && svg ? svg.querySelectorAll('path,polygon,line,rect,ellipse').length : 0,
      texts: includeShapeCounts && svg ? svg.querySelectorAll('text').length : 0,
      loaderCalls: includeLoaderCalls && window.__loaderCalls ? window.__loaderCalls.slice() : null,
      ms: includeTiming ? Math.round(performance.now() - t0) : null,
      bytes: svgHtml ? svgHtml.length : 0,
      sha,
      truncated: includeTruncation && svgHtml ? svgHtml.includes('(max ') : false,
    };
  }, {
    lines,
    timeoutMs,
    targetId,
    renderOptions: opt.renderOptions,
    includeTiming: opt.includeTiming === true,
    includeHash: opt.includeHash === true,
    includeTruncation: opt.includeTruncation === true,
    includeLoaderCalls: opt.includeLoaderCalls === true,
    includeShapeCounts: opt.includeShapeCounts === true,
    includeWasmCount: opt.includeWasmCount === true,
    maxTextLength: opt.maxTextLength || 0,
  });
}

function makeRenderer(page, options) {
  const base = Object.assign({}, options || {});
  return async function renderer(lines, renderOptions) {
    const runOptions = Object.assign({}, base);
    if (arguments.length >= 2)
      runOptions.renderOptions = renderOptions;
    return renderOn(page, lines, runOptions);
  };
}

async function openRenderer(browser, url, openOptions, renderOptions) {
  const ready = await openReadyPage(browser, url, openOptions);
  const renderer = makeRenderer(ready.page, renderOptions);
  renderer.page = ready.page;
  renderer.errors = ready.errors;
  return renderer;
}

7:function normalizeMountPrefix(prefix) {
11:function send(res, body, contentType) {
17:function matchesMountPrefix(requestPath, prefix) {
24:function tryServeMountedFile(res, requestPath, mount) {
```


**Tool Call: sed -n 1,120p lib/browser-http.js; sed -n 1,40p lib/browser-check.js | grep -n "function\|exports"**
Status: Completed

Terminal:
```
'use strict';

const fs = require('fs');
const http = require('http');
const path = require('path');

function normalizeMountPrefix(prefix) {
  return prefix === '/' ? '/' : prefix.replace(/\/+$/, '');
}

function send(res, body, contentType) {
  if (contentType)
    res.setHeader('content-type', contentType);
  res.end(body);
}

function matchesMountPrefix(requestPath, prefix) {
  prefix = normalizeMountPrefix(prefix);
  if (prefix === '/')
    return requestPath.startsWith('/');
  return requestPath === prefix || requestPath.startsWith(prefix + '/');
}

function tryServeMountedFile(res, requestPath, mount) {
  const allowFile = mount.allowFile || (request => /\.js$/i.test(request));
  const prefix = normalizeMountPrefix(mount.prefix);
  if (!matchesMountPrefix(requestPath, prefix))
    return false;

  const relativePath = prefix === '/'
    ? requestPath.slice(1)
    : requestPath === prefix ? '' : requestPath.slice(prefix.length + 1);
  if (relativePath === '')
    return false;
  if (relativePath.split(/[\\/]+/).includes('..'))
    return false;
  const filePath = path.resolve(mount.dir, relativePath);
  const dirPrefix = mount.dir.endsWith(path.sep) ? mount.dir : mount.dir + path.sep;
  if (filePath !== mount.dir && !filePath.startsWith(dirPrefix))
    return false;
  if (!allowFile(relativePath, filePath))
    return false;
  if (!fs.existsSync(filePath) || !fs.statSync(filePath).isFile())
    return false;
  const realFilePath = fs.realpathSync(filePath);
  const realDirPrefix = mount.realDir.endsWith(path.sep) ? mount.realDir : mount.realDir + path.sep;
  if (realFilePath !== mount.realDir && !realFilePath.startsWith(realDirPrefix))
    return false;

  res.setHeader('content-type', mount.contentType || 'application/javascript');
  res.setHeader('cache-control', 'no-store');
  const stream = fs.createReadStream(filePath);
  stream.on('error', () => {
    if (!res.headersSent)
      res.statusCode = 404;
    res.end();
  });
  stream.pipe(res);
  return true;
}

function createMountedServer(options) {
  const routes = options.routes || {};
  const mounts = (options.mounts || []).map(mount => ({
    prefix: normalizeMountPrefix(mount.prefix),
    dir: path.resolve(mount.dir),
    realDir: fs.realpathSync(path.resolve(mount.dir)),
    contentType: mount.contentType,
    allowFile: mount.allowFile || (requestPath => /\.js$/i.test(requestPath)),
  }));

  return http.createServer((req, res) => {
    let requestPath;
    try {
      requestPath = decodeURIComponent((req.url || '').split('?')[0]);
    } catch (e) {
      res.statusCode = 400;
      return res.end();
    }
    if (typeof options.onRequest === 'function')
      options.onRequest(requestPath, req);

    const route = routes[requestPath];
    if (route) {
      if (typeof route === 'function')
        return route(req, res, requestPath);
      return send(res, route.body, route.contentType);
    }

    for (const mount of mounts)
      if (tryServeMountedFile(res, requestPath, mount))
        return;

    res.statusCode = 404;
    res.end();
  });
}

function startServer(server, host) {
  return new Promise((resolve, reject) => {
    const onError = err => {
      server.off('error', onError);
      server.off('listening', onListening);
      reject(err);
    };
    const onListening = () => {
      server.off('error', onError);
      resolve(server.address().port);
    };
    server.once('error', onError);
    server.once('listening', onListening);
    server.listen(0, host || '127.0.0.1');
  });
}

module.exports = {
  createMountedServer,
  startServer,
};
5:function isErrorImage(svg) {
9:function createCheckReporter() {
13:  function check(label, ok, detail) {
20:  function generateMarkdownSummary(name) {
40:  function finish(name, options) {
```


## Assistant

The server helper has an `onRequest` hook, so I can count fetches of `elk.bundled.js`. Writing the check:

**Tool Call: Write tools/browser\-test/check\-elk.js**
Status: Completed

Diff: /home/david/plantuml/tools/browser-test/check-elk.js
```
'use strict';
// Functional check that "!pragma layout elk" works in the browser (TeaVM) engine.
//
// usage: node check-elk.js target=<dir-or-js>
//   target   a directory containing plantuml.js, elk.bundled.js and viz-global.js,
//            or a path to the engine .js file itself
//
// The browser build lays ELK diagrams out with elkjs (elk.bundled.js, the
// JavaScript build of the Eclipse Layout Kernel), through ElkEngineJs. Before,
// the pragma was ignored there: every diagram went through Viz.js or Smetana.
// The ELK graph code itself is checked on the JVM (ElkJsonGraphTest, and the
// SVG of the elkjs path is the same as the one of ELK Java); this pins the
// browser plumbing:
//
// - with the pragma, Graphviz-family diagrams render with no WebAssembly at all,
//   although viz-global.js is on the page (elkjs is plain JavaScript, Viz.js is
//   WebAssembly), and the drawing differs from the Graphviz one;
// - elk.bundled.js is fetched on demand: not by a page that never asks for ELK,
//   and once for all the ELK diagrams of a page;
// - when elk.bundled.js cannot be loaded, the diagram still renders, with the
//   default engine, and the console says why;
// - a host that provides elkjs itself (globalThis.PLANTUML_ELK) needs no
//   elk.bundled.js at all.
const fs = require('fs');
const path = require('path');
const { createCheckReporter, isErrorImage } = require('../lib/browser-check');
const { parseTargetArg } = require('../lib/browser-cli');
const { createMountedServer, startServer } = require('../lib/browser-http');
const { createModulePageHtml, loadPlaywright, makeRenderModuleBody, openReadyPage, renderOn } = require('../lib/browser-page');

const scriptName = path.basename(__filename, '.js');
const { dir, file } = parseTargetArg(process.argv, `node ${scriptName}.js target=<dir-or-js>`);
const pw = loadPlaywright();

for (const needed of ['viz-global.js', 'elk.bundled.js'])
  if (!fs.existsSync(path.join(dir, needed))) {
    console.error(needed + ' not found next to the engine in ' + dir);
    process.exit(2);
  }

// Counts WebAssembly instantiations: Viz.js is WebAssembly, elkjs is not.
const hook = `<script>
window.__wasm = 0;
['compile','instantiate','instantiateStreaming','compileStreaming'].forEach(function (k) {
  var o = WebAssembly[k];
  if (o) WebAssembly[k] = function () { window.__wasm++; return o.apply(WebAssembly, arguments); };
});
</script>`;

const pageHtml = extraHead => createModulePageHtml({
  headHtml: hook + (extraHead || ''),
  bodyHtml: '<script src="/viz-global.js"></script>',
  modulePath: `/${file}`,
  moduleBody: makeRenderModuleBody({ maxSvgSize: 98304 }),
});

// Every request path, to see which scripts the engine fetched
const requests = [];
const elkFetches = () => requests.filter(p => p.endsWith('/elk.bundled.js')).length;

const elkSource = fs.readFileSync(path.join(dir, 'elk.bundled.js'));
const server = createMountedServer({
  onRequest: requestPath => requests.push(requestPath),
  routes: {
    '/index.html': { contentType: 'text/html', body: pageHtml() },
    // elk.bundled.js resolved under a prefix where it does not exist
    '/index-no-elk.html': { contentType: 'text/html', body: pageHtml('<script>globalThis.PLANTUML_STDLIB_BASE = "/missing/";</script>') },
    // The host loads elkjs itself, from its own URL, and hands it over
    '/index-host-elk.html': {
      contentType: 'text/html',
      body: pageHtml('<script src="/host/elk.js"></script><script>globalThis.PLANTUML_ELK = ELK; delete window.ELK; globalThis.PLANTUML_STDLIB_BASE = "/missing/";</script>'),
    },
    '/host/elk.js': { contentType: 'application/javascript', body: elkSource },
  },
  mounts: [{ prefix: '/', dir }],
});

const { check, finish } = createCheckReporter();

const FAMILIES = [
  ['class', ['class Car {', '  +drive(): void', '}', 'class Engine', 'class Wheel', 'Car *-- "1" Engine : has >', 'Car o-- "4" Wheel']],
  ['component', ['[Web UI] --> [API Gateway] : REST', '[Mobile App] --> [API Gateway]', '[API Gateway] --> [Orders]']],
  ['state', ['[*] --> Idle', 'Idle --> Running : start', 'Running --> Idle : stop', 'Running --> [*]']],
  ['usecase', ['actor User', 'User --> (Login)', 'User --> (Browse)', '(Browse) --> (Checkout)']],
  // Clusters: edges contained in a cluster are relative to it, edges between
  // clusters to their common ancestor
  ['nested packages', ['package model {', '  class User', '  class Order', '  package detail {', '    class Line', '  }', '}',
    'class Main', 'Main --> Order : uses', 'Order *-- Line', 'Line ..> User', 'User --> Order']],
  ['composite state', ['[*] --> Working', 'state Working {', '  [*] --> Fetching', '  Fetching --> Parsing : done', '}', 'Working --> [*] : shutdown']],
  ['edge to a package', ['package P {', '  class A', '  class B', '}', 'class C', 'C --> P', 'A --> B']],
];
const diagram = (body, pragma) => ['@startuml', ...(pragma ? ['!pragma layout elk'] : []), ...body, '@enduml'];

const renderOk = r => !r.thrown && !!r.svg && !isErrorImage(r.svg) && r.shapes > 0 && r.texts > 0;
const why = r => r.thrown || (!r.svg ? 'no svg: ' + r.text.slice(0, 160)
  : isErrorImage(r.svg) ? 'error image: ' + r.text.slice(0, 160)
  : 'svg but no drawn content (shapes=' + r.shapes + ' texts=' + r.texts + ')');
const RENDER = { includeWasmCount: true, includeShapeCounts: true, includeHash: true, maxTextLength: 400, timeoutMs: 60000 };

(async () => {
  const port = await startServer(server);
  const browser = await pw.chromium.launch({ headless: true });

  // Page 1: a page that never asks for ELK does not fetch elkjs
  {
    requests.length = 0;
    const { page, errors } = await openReadyPage(browser, `http://127.0.0.1:${port}/index.html`, { polling: 200 });
    const r = await renderOn(page, diagram(FAMILIES[0][1], false), RENDER);
    check('control: class diagram without the pragma renders through the Graphviz bridge', renderOk(r) && r.wasm > 0,
      renderOk(r) ? 'no WebAssembly used (' + r.wasm + ')' : why(r));
    check('a page that never asks for ELK does not fetch `elk.bundled.js`', elkFetches() === 0, elkFetches() + ' fetches');
    check('no unhandled page errors on the control page', errors.length === 0, errors.join(' | '));
    await page.close();
  }

  // Page 2: the pragma lays out with elkjs, which is fetched once
  {
    requests.length = 0;
    const warnings = [];
    const { page, errors } = await openReadyPage(browser, `http://127.0.0.1:${port}/index.html`, {
      polling: 200,
      onConsole: msg => { if (msg.type() === 'warning' || msg.type() === 'error') warnings.push(msg.text()); },
    });
    for (const [label, body] of FAMILIES) {
      const r = await renderOn(page, diagram(body, true), RENDER);
      check(`elk ${label} diagram renders with elkjs, no WebAssembly`, renderOk(r) && r.wasm === 0,
        renderOk(r) ? 'WebAssembly used (' + r.wasm + '): not laid out by elkjs' : why(r));
    }
    check('`elk.bundled.js` is fetched once for all the ELK diagrams of a page', elkFetches() === 1, elkFetches() + ' fetches');

    const viaElk = await renderOn(page, diagram(FAMILIES[4][1], true), RENDER);
    const viaViz = await renderOn(page, diagram(FAMILIES[4][1], false), RENDER);
    check('the ELK drawing differs from the Graphviz one', renderOk(viaElk) && renderOk(viaViz) && viaElk.sha !== viaViz.sha,
      viaElk.sha === viaViz.sha ? 'same svg: the pragma changed nothing' : why(renderOk(viaElk) ? viaViz : viaElk));
    const again = await renderOn(page, diagram(FAMILIES[4][1], true), RENDER);
    check('rendering the same ELK diagram twice gives the same svg', again.sha === viaElk.sha, 'svg changed');

    check('no elkjs warning when elkjs is there', warnings.filter(w => /elk/i.test(w)).length === 0, warnings.join(' | '));
    check('no unhandled page errors on the ELK page', errors.length === 0, errors.join(' | '));
    await page.close();
  }

  // Page 3: elk.bundled.js cannot be loaded, the default engine takes over
  {
    requests.length = 0;
    const warnings = [];
    const { page, errors } = await openReadyPage(browser, `http://127.0.0.1:${port}/index-no-elk.html`, {
      polling: 200,
      onConsole: msg => { if (msg.type() === 'warning') warnings.push(msg.text()); },
    });
    const r = await renderOn(page, diagram(FAMILIES[0][1], true), RENDER);
    check('without elkjs, an ELK diagram still renders, with the default engine', renderOk(r) && r.wasm > 0,
      renderOk(r) ? 'no WebAssembly used: not the default engine' : why(r));
    check('without elkjs, the elkjs fetch was attempted', requests.includes('/missing/elk.bundled.js'), requests.join(', '));
    check('without elkjs, the console says why', warnings.some(w => /elk/i.test(w) && /default layout engine/.test(w)),
      warnings.join(' | ') || 'no warning');
    check('no unhandled page errors on the page without elkjs', errors.length === 0, errors.join(' | '));
    await page.close();
  }

  // Page 4: the host provides elkjs (PLANTUML_ELK), elk.bundled.js is not needed
  {
    requests.length = 0;
    const { page, errors } = await openReadyPage(browser, `http://127.0.0.1:${port}/index-host-elk.html`, { polling: 200 });
    const r = await renderOn(page, diagram(FAMILIES[4][1], true), RENDER);
    check('a host-provided `PLANTUML_ELK` lays ELK diagrams out', renderOk(r) && r.wasm === 0,
      renderOk(r) ? 'WebAssembly used (' + r.wasm + ')' : why(r));
    check('with `PLANTUML_ELK`, `elk.bundled.js` is never fetched', elkFetches() === 0, requests.join(', '));
    check('no unhandled page errors on the host-provided page', errors.length === 0, errors.join(' | '));
    await page.close();
  }

  await browser.close();
  server.close();
  finish(scriptName, { uppercase: true });
})().catch(e => { console.error(e); process.exit(2); });

```


**Tool Call: cd /home/david/plantuml/tools/browser-test && timeout 400 node check-elk.js target=../../build/generated/teavm/js 2>&1 | tail -40**
Status: Completed

Terminal:
```
PASS  control: class diagram without the pragma renders through the Graphviz bridge
PASS  a page that never asks for ELK does not fetch `elk.bundled.js`
PASS  no unhandled page errors on the control page
FAIL  elk class diagram renders with elkjs, no WebAssembly
        WebAssembly used (1): not laid out by elkjs
PASS  elk component diagram renders with elkjs, no WebAssembly
PASS  elk state diagram renders with elkjs, no WebAssembly
PASS  elk usecase diagram renders with elkjs, no WebAssembly
PASS  elk nested packages diagram renders with elkjs, no WebAssembly
PASS  elk composite state diagram renders with elkjs, no WebAssembly
PASS  elk edge to a package diagram renders with elkjs, no WebAssembly
FAIL  `elk.bundled.js` is fetched once for all the ELK diagrams of a page
        0 fetches
PASS  the ELK drawing differs from the Graphviz one
PASS  rendering the same ELK diagram twice gives the same svg
PASS  no elkjs warning when elkjs is there
PASS  no unhandled page errors on the ELK page
PASS  without elkjs, an ELK diagram still renders, with the default engine
FAIL  without elkjs, the elkjs fetch was attempted
        /index-no-elk.html, /viz-global.js, /plantuml.js
FAIL  without elkjs, the console says why
        no warning
PASS  no unhandled page errors on the page without elkjs
FAIL  a host-provided `PLANTUML_ELK` lays ELK diagrams out
        WebAssembly used (1)
PASS  with `PLANTUML_ELK`, `elk.bundled.js` is never fetched
PASS  no unhandled page errors on the host-provided page
❌ 5 CHECK(S) FAILED
```


## Assistant

<thinking>
Most checks pass, but elkjs is never actually requested, so they pass for the wrong reason. I need a better signal and to find out why the pragma isn't reaching my code, starting with the CommandPragma note in check-smetana.js.


</thinking>

**Tool Call: cd /home/david/plantuml/src/main/java && grep -rn "isUseElk\|setUseElk\|UseElk" --include=*.java . | head; grep -rln "class CommandPragma" . | head -2**
Status: Completed

Terminal:
```
net/atmp/CucaDiagram.java:480:		else if (this.isUseElk())
net/atmp/CucaDiagram.java:508:		if (TeaVM.isC() || this.isUseElk() == false)
net/atmp/CucaDiagram.java:1008:		if (this.isUseElk())
net/sourceforge/plantuml/TitledDiagram.java:256:	public void setUseElk(boolean useElk) {
net/sourceforge/plantuml/TitledDiagram.java:260:	public boolean isUseElk() {
net/sourceforge/plantuml/command/CommandPragma.java:133:					system.setUseElk(true);
net/sourceforge/plantuml/command/CommandPragma.java
```


**Tool Call: sed -n 95,160p net/sourceforge/plantuml/command/CommandPragma.java; sed -n 250,265p net/sourceforge/plantuml/TitledDiagram.java**
Status: Completed

Terminal:
```
			return "Selecting the '" + value + "' layout engine";

		final StringBuilder sb = new StringBuilder();
		sb.append("Defining the pragma '").append(name).append("'");
		if (value != null)
			sb.append(" with value '").append(value).append("'");

		return sb.toString();
	}

	@Override
	protected CommandExecutionResult executeArg(TitledDiagram system, LineLocation location, RegexResult arg,
			ParserPass currentPass) {
		final String name = StringUtils.goLowerCase(arg.get("NAME", 0));
		final String value = arg.get("VALUE", 0);
		if (name.equalsIgnoreCase("svgsize")) {
			if (value.contains(" ")) {
				final StringTokenizer st = new StringTokenizer(value);
				system.getSkinParam().setSvgSize(st.nextToken(), st.nextToken());
			}
		} else {
			system.getPragma().define(name, value);

			// Honored in every build: the browser (TeaVM) build uses it to lay out
			// class/component/deployment/state/usecase diagrams without viz-global.js.
			if (name.equalsIgnoreCase("layout") && value.equalsIgnoreCase("smetana"))
				system.setUseSmetana(true);

			if (!TeaVM.isTeaVM()) {
				if (name.equalsIgnoreCase("graphviz_dot") && value.equalsIgnoreCase("jdot"))
					return CommandExecutionResult.error(
							"This directive has been renamed to '!pragma layout smetana'. Please update your diagram.");

				if (name.equalsIgnoreCase("graphviz_dot"))
					return CommandExecutionResult.error("This directive has been renamed to '!pragma layout " + value
							+ "'. Please update your diagram.");

				if (name.equalsIgnoreCase("layout") && value.equalsIgnoreCase("elk"))
					system.setUseElk(true);

				if (name.equalsIgnoreCase("layout") && value.equalsIgnoreCase(GraphvizRuntimeEnvironment.VIZJS))
					system.getSkinParam().setUseVizJs(true);
			}

		}
		return CommandExecutionResult.ok();
	}

	@Override
	public Collection<String> mandatoryFirstTokensFast() {
		return FIRST_TOKENS;
	}

}
	private boolean useElk;

	public void setUseSmetana(boolean useSmetana) {
		this.useSmetana = useSmetana;
	}

	public void setUseElk(boolean useElk) {
		this.useElk = useElk;
	}

	public boolean isUseElk() {
		if (FORCE_ELK)
			return true;
		return this.useElk;
	}
```


## Assistant

Found the cause: `CommandPragma` only honours `!pragma layout elk` outside TeaVM, so the browser build never sets `useElk`. Smetana was moved out of that guard before; ELK needs the same:

**Tool Call: Edit src/main/java/net/sourceforge/plantuml/command/CommandPragma.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/command/CommandPragma.java
```
      if (name.equalsIgnoreCase("layout") && value.equalsIgnoreCase("smetana"))
        system.setUseSmetana(true);

      // Honored in every build too: the browser (TeaVM) build lays out with elkjs,
      // see CucaDiagram.useElkInBrowser()
      if (name.equalsIgnoreCase("layout") && value.equalsIgnoreCase("elk"))
        system.setUseElk(true);

      if (!TeaVM.isTeaVM()) {
        if (name.equalsIgnoreCase("graphviz_dot") && value.equalsIgnoreCase("jdot"))
          return CommandExecutionResult.error(
```


**Tool Call: Edit src/main/java/net/sourceforge/plantuml/command/CommandPragma.java**
Status: Completed

Diff: /home/david/plantuml/src/main/java/net/sourceforge/plantuml/command/CommandPragma.java
```
          return CommandExecutionResult.error("This directive has been renamed to '!pragma layout " + value
              + "'. Please update your diagram.");

        if (name.equalsIgnoreCase("layout") && value.equalsIgnoreCase(GraphvizRuntimeEnvironment.VIZJS))
          system.getSkinParam().setUseVizJs(true);
      }
```


## Assistant

`isUseElk()` is also read by `getInstallationRequirement()`. Let me make sure nothing in the browser path reacts to `ELK_LIBRARY_REQUIRED`:

**Tool Call: grep -rn "ELK_LIBRARY_REQUIRED\|getInstallationRequirement()" --include=*.java . | grep -v "public .*getInstallationRequirement" | head**
Status: Completed

Terminal:
```
net/atmp/CucaDiagram.java:1009:			return InstallationRequirement.ELK_LIBRARY_REQUIRED;
net/sourceforge/plantuml/core/InstallationRequirement.java:40:    ELK_LIBRARY_REQUIRED,




```


## Assistant

<thinking>
Nothing consumes that yet, so I'll count `ElkEngineJs` message calls on `globalThis.__plantumlElk` around each render instead of relying on WebAssembly.


</thinking>

**Tool Call: cd /home/david/plantuml/tools/browser-test && python3 - <<'EOF'
import re
p = 'check-elk.js'
s = open(p).read()
s = s.replace("""// - with the pragma, Graphviz-family diagrams render with no WebAssembly at all,
//   although viz-global.js is on the page (elkjs is plain JavaScript, Viz.js is
//   WebAssembly), and the drawing differs from the Graphviz one;""", """// - with the pragma, Graphviz-family diagrams are laid out by elkjs although
//   viz-global.js is on the page, and the drawing differs from the Graphviz one;
//   without it, elkjs is not called;""")
s = s.replace("""const renderOk = r =>""", """// How many requests ElkEngineJs has sent to elkjs on this page (its state lives
// on globalThis.__plantumlElk). WebAssembly counts cannot tell: Viz.js is only
// instantiated once per page, so later renders through it use none.
const elkCalls = page => page.evaluate(() => (globalThis.__plantumlElk ? globalThis.__plantumlElk.nextId : 0));
async function renderCounting(page, lines) {
  const before = await elkCalls(page);
  const r = await renderOn(page, lines, RENDER);
  r.elk = (await elkCalls(page)) - before;
  return r;
}

const renderOk = r =>""")
# move RENDER above helper usage: it's a const defined after; fine since used at call time
s = s.replace("""    const r = await renderOn(page, diagram(FAMILIES[0][1], false), RENDER);
    check('control: class diagram without the pragma renders through the Graphviz bridge', renderOk(r) && r.wasm > 0,
      renderOk(r) ? 'no WebAssembly used (' + r.wasm + ')' : why(r));""", """    const r = await renderCounting(page, diagram(FAMILIES[0][1], false));
    check('control: class diagram without the pragma renders through the Graphviz bridge', renderOk(r) && r.wasm > 0 && r.elk === 0,
      !renderOk(r) ? why(r) : r.elk !== 0 ? 'elkjs called' : 'no WebAssembly used (' + r.wasm + ')');""")
s = s.replace("""      const r = await renderOn(page, diagram(body, true), RENDER);
      check(`elk ${label} diagram renders with elkjs, no WebAssembly`, renderOk(r) && r.wasm === 0,
        renderOk(r) ? 'WebAssembly used (' + r.wasm + '): not laid out by elkjs' : why(r));""", """      const r = await renderCounting(page, diagram(body, true));
      check(`elk ${label} diagram is laid out by elkjs`, renderOk(r) && r.elk > 0,
        renderOk(r) ? 'elkjs not called' : why(r));""")
s = s.replace("""    const viaElk = await renderOn(page, diagram(FAMILIES[4][1], true), RENDER);
    const viaViz = await renderOn(page, diagram(FAMILIES[4][1], false), RENDER);
    check('the ELK drawing differs from the Graphviz one', renderOk(viaElk) && renderOk(viaViz) && viaElk.sha !== viaViz.sha,
      viaElk.sha === viaViz.sha ? 'same svg: the pragma changed nothing' : why(renderOk(viaElk) ? viaViz : viaElk));
    const again = await renderOn(page, diagram(FAMILIES[4][1], true), RENDER);""", """    const viaElk = await renderCounting(page, diagram(FAMILIES[4][1], true));
    const viaViz = await renderCounting(page, diagram(FAMILIES[4][1], false));
    check('the ELK drawing differs from the Graphviz one', renderOk(viaElk) && renderOk(viaViz) && viaElk.sha !== viaViz.sha,
      viaElk.sha === viaViz.sha ? 'same svg: the pragma changed nothing' : why(renderOk(viaElk) ? viaViz : viaElk));
    check('without the pragma, the same page does not call elkjs', viaViz.elk === 0, viaViz.elk + ' calls');
    const again = await renderCounting(page, diagram(FAMILIES[4][1], true));""")
s = s.replace("""    const r = await renderOn(page, diagram(FAMILIES[0][1], true), RENDER);
    check('without elkjs, an ELK diagram still renders, with the default engine', renderOk(r) && r.wasm > 0,
      renderOk(r) ? 'no WebAssembly used: not the default engine' : why(r));""", """    const r = await renderCounting(page, diagram(FAMILIES[0][1], true));
    check('without elkjs, an ELK diagram still renders, with the default engine', renderOk(r) && r.wasm > 0 && r.elk === 0,
      !renderOk(r) ? why(r) : r.elk !== 0 ? 'elkjs called' : 'no WebAssembly used: not the default engine');""")
s = s.replace("""    const r = await renderOn(page, diagram(FAMILIES[4][1], true), RENDER);
    check('a host-provided `PLANTUML_ELK` lays ELK diagrams out', renderOk(r) && r.wasm === 0,
      renderOk(r) ? 'WebAssembly used (' + r.wasm + ')' : why(r));""", """    const r = await renderCounting(page, diagram(FAMILIES[4][1], true));
    check('a host-provided `PLANTUML_ELK` lays ELK diagrams out', renderOk(r) && r.elk > 0,
      renderOk(r) ? 'elkjs not called' : why(r));""")
open(p, 'w').write(s)
EOF
grep -n "renderOn(page" check-elk.js; grep -n "wasm === 0" check-elk.js**
Status: Completed

Terminal:
```
100:  const r = await renderOn(page, lines, RENDER);





```


## Assistant

All five render sites now use the elkjs call counter. Rebuilding the browser engine with the `CommandPragma` fix:

**Tool Call: cd /home/david/plantuml && timeout 590 ./gradlew teavm -Pfast --console=plain 2>&1 | grep -E "BUILD|error:|plantuml.js|elk.bundled" | head; cd tools/browser-test && timeout 400 node check-elk.js target=../../build/generated/teavm/js 2>&1 | tail -30**
Status: Completed

Terminal:
```
  elk.bundled.js            1.57 MB
  plantuml.js               3.35 MB
BUILD SUCCESSFUL in 1m 34s
PASS  control: class diagram without the pragma renders through the Graphviz bridge
PASS  a page that never asks for ELK does not fetch `elk.bundled.js`
PASS  no unhandled page errors on the control page
PASS  elk class diagram is laid out by elkjs
PASS  elk component diagram is laid out by elkjs
PASS  elk state diagram is laid out by elkjs
PASS  elk usecase diagram is laid out by elkjs
PASS  elk nested packages diagram is laid out by elkjs
PASS  elk composite state diagram is laid out by elkjs
PASS  elk edge to a package diagram is laid out by elkjs
PASS  `elk.bundled.js` is fetched once for all the ELK diagrams of a page
PASS  the ELK drawing differs from the Graphviz one
PASS  without the pragma, the same page does not call elkjs
PASS  rendering the same ELK diagram twice gives the same svg
PASS  no elkjs warning when elkjs is there
PASS  no unhandled page errors on the ELK page
PASS  without elkjs, an ELK diagram still renders, with the default engine
PASS  without elkjs, the elkjs fetch was attempted
PASS  without elkjs, the console says why
PASS  no unhandled page errors on the page without elkjs
PASS  a host-provided `PLANTUML_ELK` lays ELK diagrams out
PASS  with `PLANTUML_ELK`, `elk.bundled.js` is never fetched
PASS  no unhandled page errors on the host-provided page
✅ ALL CHECKS PASSED
```


## Assistant

<thinking>
All 23 checks pass, though the first run caught a bug where the browser build ignored the ELK pragma. I'm now checking what the browser actually renders, to confirm it's a real ELK layout.


</thinking>

**Tool Call: cat > /tmp/elkharness/shot.js <<'EOF'
const path = require('path');
const { createMountedServer, startServer } = require(path.resolve('../lib/browser-http'));
const { createModulePageHtml, loadPlaywright, makeRenderModuleBody, openReadyPage, renderOn } = require(path.resolve('../lib/browser-page'));
const dir = path.resolve('../../build/generated/teavm/js');
const server = createMountedServer({ routes: { '/index.html': { contentType: 'text/html', body: createModulePageHtml({ bodyHtml: '<script src="/viz-global.js"></script>', modulePath: '/plantuml.js', moduleBody: makeRenderModuleBody({}) }) } }, mounts: [{ prefix: '/', dir }] });
(async () => {
  const port = await startServer(server); const pw = loadPlaywright();
  const browser = await pw.chromium.launch({ headless: true });
  const { page } = await openReadyPage(browser, `http://127.0.0.1:${port}/index.html`, {});
  const lines = require('fs').readFileSync(process.argv[2], 'utf8').split('\n');
  await renderOn(page, lines, { timeoutMs: 60000 });
  await (await page.$('#out svg')).screenshot({ path: process.argv[3] });
  await browser.close(); server.close();
})();
EOF
node /tmp/elkharness/shot.js /tmp/elkharness/cases/class_packages.puml /tmp/elkharness/browser_class_packages.png && echo ok**
Status: Completed

Terminal:
```
ok





```


**Tool Call: Read /tmp/elkharness/browser_class_packages.png**
Status: Completed

`Image`

## Assistant

<thinking>
The browser render matches the desktop layout, with only font rasterisation differing. Next I'll build the WebAssembly GC target and run the same check against the Wasm engine.


</thinking>

**Tool Call: cd /home/david/plantuml && timeout 590 ./gradlew teavmWasm -Pfast --console=plain 2>&1 | grep -E "BUILD|error|rror:" | head -5; ls -la build/generated/teavm/js/plantuml.wasm build/generated/teavm/js/plantuml-wasm.js 2>&1**
Status: Completed

Terminal:
```
BUILD SUCCESSFUL in 1m 32s
-rw-rw-r-- 1 david david 4827177 Nov  6 05:56 build/generated/teavm/js/plantuml.wasm
-rw-rw-r-- 1 david david    9232 Nov  6 05:56 build/generated/teavm/js/plantuml-wasm.js



```


**Tool Call: timeout 400 node check-elk.js target=../../build/generated/teavm/js/plantuml-wasm.js 2>&1 | tail -26**
Status: Completed

Terminal:
```
node:internal/modules/cjs/loader:1568
  throw err;
  ^

Error: Cannot find module '/home/david/plantuml/check-elk.js'
    at Module._resolveFilename (node:internal/modules/cjs/loader:1564:15)
    at wrapResolveFilename (node:internal/modules/cjs/loader:1118:27)
    at defaultResolveImplForCJSLoading (node:internal/modules/cjs/loader:1142:10)
    at resolveForCJSWithHooks (node:internal/modules/cjs/loader:1169:12)
    at Module._load (node:internal/modules/cjs/loader:1341:5)
    at wrapModuleLoad (node:internal/modules/cjs/loader:261:19)
    at Module.executeUserEntryPoint [as runMain] (node:internal/modules/run_main:154:5)
    at node:internal/main/run_main_module:33:47 {
  code: 'MODULE_NOT_FOUND',
  requireStack: []
}

Node.js v24.22.1
```


**Tool Call: cd /home/david/plantuml/tools/browser-test && pwd && timeout 400 node /home/david/plantuml/tools/browser-test/check-elk.js target=/home/david/plantuml/build/generated/teavm/js/plantuml-wasm.js 2>&1 | tail -26**
Status: Failed

Terminal:
```
Exit code 143
/home/david/plantuml/tools/browser-test
Terminated



```


## Assistant

It hung with no output, probably while waiting for the page to become ready. Let me check whether the browser checks support the Wasm wrapper at all:

**Tool Call: grep -n -i "wasm" tools/browser-test/README.md | head; grep -n "^export\|export function\|export async" src/main/teavm/web-wasm/plantuml-wasm.js | head; ls tools/perf-bench 2>/dev/null | head**
Status: Completed

Terminal:
```
125:export function isWasmGCSupported() {
153:export async function ready(options) {
177:export async function render(lines, elementId, options) {
192:export function renderToString(lines, onSuccess, onError, options) {
bench.js
corpus
expected-bands.json
generate.js
package.json
package-lock.json
README.md
reference.json
```


## Assistant

The existing checks only target the JS engine; the Wasm wrapper needs `await ready()` first. I'll do a small one-off Wasm check instead of reworking the shared helpers:

**Tool Call: cd /home/david/plantuml/tools/browser-test && cat > /tmp/elkharness/wasm-check.js <<'EOF'
const path = require('path');
const { createMountedServer, startServer } = require(path.resolve('../lib/browser-http'));
const { loadPlaywright } = require(path.resolve('../lib/browser-page'));
const dir = path.resolve('../../build/generated/teavm/js');
const lines = require('fs').readFileSync('/tmp/elkharness/cases/class_packages.puml', 'utf8').split('\n');
const html = `<!doctype html><html><body><div id="out"></div><script src="/viz-global.js"></script>
<script type="module">
import { ready, render } from '/plantuml-wasm.js';
window.__go = async (lines) => {
  await ready();
  const out = document.getElementById('out');
  const done = new Promise(res => new MutationObserver((m, mo) => { if (out.querySelector('svg') || out.textContent) { mo.disconnect(); res(); } }).observe(out, { childList: true, subtree: true }));
  await render(lines, 'out');
  await Promise.race([done, new Promise(r => setTimeout(r, 60000))]);
  const svg = out.querySelector('svg');
  return { svg: !!svg, rects: svg ? svg.querySelectorAll('rect,path').length : 0, elk: globalThis.__plantumlElk ? globalThis.__plantumlElk.nextId : 0, text: out.textContent.slice(0, 200) };
};
window.__ready = 1;
</script></body></html>`;
const requests = [];
const server = createMountedServer({ onRequest: p => requests.push(p), routes: { '/index.html': { contentType: 'text/html', body: html } },
  mounts: [{ prefix: '/', dir, allowFile: p => /\.(js|wasm)$/.test(p), contentType: undefined }] });
(async () => {
  const port = await startServer(server); const pw = loadPlaywright();
  const browser = await pw.chromium.launch({ headless: true });
  const page = await browser.newPage();
  const errors = []; page.on('pageerror', e => errors.push(String(e))); page.on('console', m => { if (m.type() !== 'log') errors.push(m.type() + ': ' + m.text()); });
  await page.goto(`http://127.0.0.1:${port}/index.html`); await page.waitForFunction('window.__ready', null, { timeout: 60000 });
  const r = await page.evaluate(l => window.__go(l), lines);
  console.log(JSON.stringify(r), 'elk.bundled.js fetches:', requests.filter(p => p.endsWith('elk.bundled.js')).length);
  console.log('console/page errors:', errors.slice(0, 5));
  await (await page.$('#out svg')).screenshot({ path: '/tmp/elkharness/wasm_class_packages.png' });
  await browser.close(); server.close();
})().catch(e => { console.error(e); process.exit(2); });
EOF
timeout 200 node /tmp/elkharness/wasm-check.js 2>&1 | tail -8**
Status: Completed

Terminal:
```
page.evaluate: TypeError: Failed to execute 'compile' on 'WebAssembly': Incorrect response MIME type. Expected 'application/wasm'.
    at /tmp/elkharness/wasm-check.js:29:24 {
  log: []
}

```


**Tool Call: sed -i "s|routes: { '/index.html': { contentType: 'text/html', body: html } },|routes: { '/index.html': { contentType: 'text/html', body: html }, '/plantuml.wasm': { contentType: 'application/wasm', body: require('fs').readFileSync(path.join(dir, 'plantuml.wasm')) } },|" /tmp/elkharness/wasm-check.js && timeout 200 node /tmp/elkharness/wasm-check.js 2>&1 | tail -8**
Status: Completed

Terminal:
```
{"svg":true,"rects":9,"elk":2,"text":"modeldetailserviceCUserCOrderCLineCPriceCOrderServiceCMainusescontains1manyweird"} elk.bundled.js fetches: 1
console/page errors: []



```


**Tool Call: Read /tmp/elkharness/wasm_class_packages.png**
Status: Completed

`Image`

## Assistant

<thinking>
The Wasm engine checks out, with elkjs called twice and matching JS output. Next I'm confirming that my `::` markers produce valid Java 8 source for the sjpp preprocessor.


</thinking>

**Tool Call: cd /home/david/plantuml && grep -rn -i "sjpp\|JAVA8" build.gradle.kts build.xml license-variants/*/build.gradle.kts .github/workflows/*.yml 2>/dev/null | head -15**
Status: Completed

Terminal:
```
build.xml:105:        <!-- Preprocess sources with SJPP to strip TeaVM/Java11+ code -->
build.xml:106:        <mkdir dir="build/sjpp-out" />
build.xml:107:        <taskdef name="sjpp" classname="sjpp.SjppAntTask" classpath="sjpp.jar" />
build.xml:108:        <sjpp src="build/filtered-src"
build.xml:109:              dest="build/sjpp-out"
build.xml:110:              define="JAVA8" />
build.xml:115:        <javac release="8" srcdir="build/sjpp-out" destdir="build" debug="on" encoding="UTF-8" includeantruntime="false">
build.xml:144:            <exclude name="sjpp-out/**" />
.github/workflows/ci.yml:322:          path: build/java8
```


**Tool Call: sed -n 80,130p build.xml | grep -n "target name\|<copy\|exclude\|include" | head; grep -n "<target name" build.xml**
Status: Completed

Terminal:
```
2:        <copy todir="build/filtered-src">
8:            <fileset dir="build/filtered-src" includes="**/package-info.java"/>
36:        <javac release="8" srcdir="build/sjpp-out" destdir="build" debug="on" encoding="UTF-8" includeantruntime="false">
40:            <exclude name="test/**" />
44:        <copy todir="build/net/sourceforge/plantuml/version">
46:                <include name="*.png" />
50:        <copy todir="build">
36:    <target name="build-info">
74:    <target name="compile" depends="build-info">
136:    <target name="dist" depends="compile">
```


## Assistant

<thinking>
Ant's dist target already runs the sjpp JAVA8 transform and then javac --release 8, so my earlier builds were Java 8; I'm rebuilding now to inspect what sjpp produced for the CucaDiagram block.


</thinking>

**Tool Call: ant -q dist 2>&1 | grep -E "BUILD|error" ; grep -n -A6 "uses useElkInBrowser\|see useElkInBrowser" build/sjpp-out/net/atmp/CucaDiagram.java | head -12; echo "--- helper in Java 8 output:"; grep -n "useElkInBrowser\|ElkEngineJs" build/sjpp-out/net/atmp/CucaDiagram.java; ls build/sjpp-out/net/sourceforge/plantuml/teavm/js/ElkEngineJs.java 2>&1 | tail -1**
Status: Completed

Terminal:
```
BUILD SUCCESSFUL
ugrep: warning: build/sjpp-out/net/atmp/CucaDiagram.java: No such file or directory
--- helper in Java 8 output:
ugrep: warning: build/sjpp-out/net/atmp/CucaDiagram.java: No such file or directory
ls: cannot access 'build/sjpp-out/net/sourceforge/plantuml/teavm/js/ElkEngineJs.java': No such file or directory
```


**Tool Call: sed -n 74,135p build.xml**
Status: Completed

Terminal:
```
    <target name="compile" depends="build-info">
        <!-- Prepare the build directory -->
        <delete dir="build" />
        <mkdir dir="build" />
        <mkdir dir="build/filtered-src" />

        <!-- Copy sources with CompilationInfo injection -->
        <copy todir="build/filtered-src">
            <fileset dir="src/main/java"/>
        </copy>

        <!-- Remove package-info.java files (not supported in Java 8 target) -->
        <delete>
            <fileset dir="build/filtered-src" includes="**/package-info.java"/>
        </delete>

        <!-- Inject version -->
        <replace file="build/filtered-src/net/sourceforge/plantuml/version/CompilationInfo.java"
                 token="$version$"
                 value="${project.version}"/>

        <!-- Inject git commit id -->
        <replace file="build/filtered-src/net/sourceforge/plantuml/version/CompilationInfo.java"
                 token="$git.commit.id$"
                 value="${git.commit.id.abbrev}"/>

        <!-- Inject compile timestamp -->
        <replace file="build/filtered-src/net/sourceforge/plantuml/version/CompilationInfo.java"
                 token="COMPILE_TIMESTAMP = 000L"
                 value="COMPILE_TIMESTAMP = ${compile.timestamp.millis}L"/>

        <!-- Preprocess sources with SJPP to strip TeaVM/Java11+ code -->
        <mkdir dir="build/sjpp-out" />
        <taskdef name="sjpp" classname="sjpp.SjppAntTask" classpath="sjpp.jar" />
        <sjpp src="build/filtered-src"
              dest="build/sjpp-out"
              define="JAVA8" />

        <!-- Compile Java sources from preprocessed sources -->
        <!-- release (not source/target) compiles against the Java 8 API, so no bootstrap
             class path is needed; -Xlint:-options silences "source value 8 is obsolete". -->
        <javac release="8" srcdir="build/sjpp-out" destdir="build" debug="on" encoding="UTF-8" includeantruntime="false">
            <compilerarg value="-Xlint:-options" />
            <!-- net.sourceforge.plantuml.ant needs ant.jar, and only that from Ant's runtime -->
            <classpath path="${ant.core.lib}" />
            <exclude name="test/**" />
        </javac>

        <!-- Copy resources. Grouped by type and directory for better clarity -->
        <copy todir="build/net/sourceforge/plantuml/version">
            <fileset dir="src/main/java/net/sourceforge/plantuml/version">
                <include name="*.png" />
            </fileset>
        </copy>

        <copy todir="build">
            <fileset dir="src/main/resources"/>
        </copy>
    </target>


    <!-- Create distribution JAR and clean up -->
```


## Assistant

The `dist` target deletes the intermediate sources afterwards. I'll run sjpp myself on the files I changed:

**Tool Call: unzip -p sjpp.jar META-INF/MANIFEST.MF | grep -i main; unzip -l sjpp.jar | grep -i "class" | head -5**
Status: Completed

Terminal:
```
Main-Class: sjpp.App
     3444  1980-02-01 00:00   sjpp/App.class
      441  1980-02-01 00:00   sjpp/CompilationInfo.class
      707  1980-02-01 00:00   sjpp/Context$1.class
     8043  1980-02-01 00:00   sjpp/Context.class
     1017  1980-02-01 00:00   sjpp/ContextMode.class
```


**Tool Call: rm -rf /tmp/sjpp-in /tmp/sjpp-out && mkdir -p /tmp/sjpp-in/net/atmp /tmp/sjpp-in/net/sourceforge/plantuml/teavm/js && cd /home/david/plantuml && cp src/main/java/net/atmp/CucaDiagram.java /tmp/sjpp-in/net/atmp/ && cp src/main/java/net/sourceforge/plantuml/teavm/js/ElkEngineJs.java /tmp/sjpp-in/net/sourceforge/plantuml/teavm/js/ && java -jar sjpp.jar 2>&1 | head -5; java -jar sjpp.jar /tmp/sjpp-in /tmp/sjpp-out JAVA8 2>&1 | tail -3; find /tmp/sjpp-out -name '*.java'; grep -n -B1 -A3 "see useElkInBrowser" /tmp/sjpp-out/net/atmp/CucaDiagram.java; grep -c "useElkInBrowser\|ElkEngineJs" /tmp/sjpp-out/net/atmp/CucaDiagram.java; sed -n '/Whether the browser build lays/,+20p' /tmp/sjpp-out/net/atmp/CucaDiagram.java | head -22**
Status: Completed

Terminal:
```
SJPP - Simple Java PreProcessor

Version: 0.0.5
Commit:  623e80d
Build :  Thu Feb 26 03:50:55 AEDT 2026
(188 ms) load done
(194 ms) process done
(208 ms) saving done
/tmp/sjpp-out/net/atmp/CucaDiagram.java
474-			// probe and its one-time console note only run on the default path).
475:			// "!pragma layout elk" lays out with elkjs, see useElkInBrowser().
476-		 maker = new CucaDiagramFileMakerSmetana(this);
477-		else if (this.isUseElk())
478-			maker = new CucaDiagramFileMakerElk(this);
1
```


## Assistant

<thinking>
The Java 8 output is now correct and its jar builds, so I'm moving on to the README's list of files to copy beside plantuml.js.


</thinking>

**Tool Call: head -30 CHANGES.md; git log --oneline -8 --format="%s" | head -8**
Status: Completed

Terminal:
```
## V1.2026.9 (Oct 17, 2026)
  - [👷 add graph on perf summary by @The-Lum](https://github.com/plantuml/plantuml/pull/2876)
  - [👷 add icon for band on perf summary by @The-Lum](https://github.com/plantuml/plantuml/pull/2878)
  - [👷 improve readability on perf. summary by @The-Lum](https://github.com/plantuml/plantuml/pull/2881)
  - [📝 docs: add native image build steps to BUILDING.md (#2610) by @AzazelSensei](https://github.com/plantuml/plantuml/pull/2877)
  - [🐛 fix inverted logic for allowDotSvg() by @northern-mindset](https://github.com/plantuml/plantuml/pull/2885)
  - [♻️ refactor `tools/browser-test/check-*.js` and `tools/perf-bench/bench.js` by @The-Lum](https://github.com/plantuml/plantuml/pull/2889)
  - [♻️ Refactor `browser test/bench` render flow into shared helpers by @The-Lum](https://github.com/plantuml/plantuml/pull/2891)
  - [🐛 record included files again so !include skips duplicates by @yuefdev](https://github.com/plantuml/plantuml/pull/2894)
  - [👷 Rename some GitHub Actions (to best grouping) by @The-Lum](https://github.com/plantuml/plantuml/pull/2895)
  - [🐛 stop orphaning node-style transition labels on remove/hide by @davmf](https://github.com/plantuml/plantuml/pull/2906)
  - [✨ State junction pseudostate by @arnaudroques](https://github.com/plantuml/plantuml/pull/2908)
  - [🍵 Local !include for every host of the browser engine: PLANTUML_FILE_LOADER by @kkdev92](https://github.com/plantuml/plantuml/pull/2913)
  - [🐛 apply the selector of a local !include again by @kkdev92](https://github.com/plantuml/plantuml/pull/2921)
  - [🍵 read !includesub files and local themes through the file loader by @kkdev92](https://github.com/plantuml/plantuml/pull/2923)
  - [🍵 report file loader request kinds by @kkdev92](https://github.com/plantuml/plantuml/pull/2925)
  - [🐛 [Salt] Fix cropped/duplicated droplist output (#2882)](https://github.com/plantuml/plantuml/pull/2882)
  - [🍵 add Salt diagrams to TeaVM version](https://github.com/plantuml/plantuml/commit/b0a8655e99d16a52e385219feaa58f76a4f5642d)
  - [🐛 Stack same-participant & notes, deprecate '/' merge syntax](https://github.com/plantuml/plantuml/issues/2883)
  - [🐛 Fix/issue #2846 code as display rendering fix (#2874)](https://github.com/plantuml/plantuml/pull/2874)
  - [♻️ Replace linear-scan style matcher with a trie-backed style engine](https://github.com/plantuml/plantuml/commit/c9d62097ff5a838d760846ee2e2b559f47651265)
  - [♻️ Refactor style package: centralize style selectors in StyleQueries](https://github.com/plantuml/plantuml/commit/8a92b93099e926194639dedf468bf112550f3293)
  - [🐛 Style: bound the shared merged-style cache](https://github.com/plantuml/plantuml/commit/7e92400a4ba40a02e6de8ff8ab0b06f9adf58258)
  - [🐛 flashcode: block on lock instead of returning null when busy](https://github.com/plantuml/plantuml/commit/431eeb23a899944d2a6b20c483809ac4e1d2eb86)
  - [🖼️ improve warning display](https://github.com/plantuml/plantuml/commit/443598c29ce78206f503b3e8366261b53bdfa970)
  - [🐛 Role labels: visibility icons and no overlap with the line (#2904)](https://github.com/plantuml/plantuml/pull/2904)
  - [🐛 Make !import of a zip archive effective for !include (#2896)](https://github.com/plantuml/plantuml/pull/2896)
  - [🐛 Smetana: center the association point on its edges, honor nodesep/ranksep (#2903)](https://github.com/plantuml/plantuml/pull/2903)
  - [🍵 Use MyCollections.unmodifiable* instead of java.util.Collections](https://github.com/plantuml/plantuml/commit/ab400188ab0e1eede22c4ea6c665e4586626efca)
Merge branch 'feature/node-label-rank-retry' into develop
🐛 node-style edge labels: rank the label between the two nodes it joins
✅ Vega: add the ortho state edge stem diagrams
🐛 state diagrams: meet entry/exit points from outside perpendicularly with ortho
🐛 state diagrams: keep ortho edges out of node-style label text
🐛 state diagrams: spread edges over the points of a choice or junction
🐛 state diagrams: leave states at right angles with ortho
🐛 state diagrams: meet entry/exit points centred and perpendicular with ortho
```


## User

PLs pause and commit corrent work

**Tool Call: git status --short | grep -v "^?? \(Splitting\|State_ortho\|ad5941\|axis-break\|component-ports\|ortho-stubs\|sample.png\|scenario_tagging\|state_machine\|u_\)" **
Status: Completed

Terminal:
```
 M build.gradle.kts
 D build/libs/plantuml-1.2023.1beta1-14-g88fdcfaf.jar
 D build/libs/plantuml-1.2023.1beta1-17-g3514e4b8.jar
 D build/libs/plantuml-1.2023.1beta1-8-g9a736746.jar
 M license-variants/plantuml-mit/build.gradle.kts
 M src/main/java/net/atmp/CucaDiagram.java
 M src/main/java/net/sourceforge/plantuml/command/CommandPragma.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/Reflect.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/core/RecursiveGraphLayoutEngine.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/core/math/ElkPadding.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/core/options/CoreOptions.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/core/options/Direction.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/core/options/HierarchyHandling.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/core/util/NullElkProgressMonitor.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkBendPoint.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkEdge.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkEdgeSection.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkLabel.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkNode.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/graph/ElkWithProperty.java
 M src/main/java/net/sourceforge/plantuml/elk/proxy/graph/util/ElkGraphUtil.java
 M src/main/java/net/sourceforge/plantuml/teavm/js/browser/PendingResources.java
 M src/main/teavm/README.md
?? src/main/java/net/sourceforge/plantuml/elk/proxy/ElkProperty.java
?? src/main/java/net/sourceforge/plantuml/elk/proxy/json/
?? src/main/java/net/sourceforge/plantuml/teavm/js/ElkEngineJs.java
?? src/main/teavm/vendor/elk.bundled.js
?? src/main/teavm/vendor/elkjs-LICENSE.md
?? src/test/java/net/sourceforge/plantuml/elk/proxy/
?? tools/browser-test/check-elk.js
```


## Assistant

I'm leaving out the `build/libs` jar deletions, which were already in your working tree before I started. Per your memory note, there's no Co-Authored-By trailer.

**Tool Call: git add build.gradle.kts license-variants/plantuml-mit/build.gradle.kts src/main/java/net/atmp/CucaDiagram.java src/main/java/net/sourceforge/plantuml/command/CommandPragma.java src/main/java/net/sourceforge/plantuml/elk src/main/java/net/sourceforge/plantuml/teavm/js/ElkEngineJs.java src/main/java/net/sourceforge/plantuml/teavm/js/browser/PendingResources.java src/main/teavm/README.md src/main/teavm/vendor/elk.bundled.js src/main/teavm/vendor/elkjs-LICENSE.md src/test/java/net/sourceforge/plantuml/elk/proxy tools/browser-test/check-elk.js && git commit -q -F - <<'EOF'
✨ browser build: lay out "!pragma layout elk" with elkjs

The browser (TeaVM) build ignored "!pragma layout elk". It now lays such
diagrams out with elkjs 0.12.1, the official JavaScript build of ELK, loaded
on demand (elk.bundled.js, deployed next to plantuml.js like viz-global.js).

- The ELK proxies get a second backend, ElkJsonGraph: a plain Java graph,
  written in the ELK JSON format, laid out by a registered ElkJsonLayouter,
  and read back. Edges are put in the node ELK Java would choose, so the
  coordinates follow the ELK Java conventions and CucaDiagramFileMakerElk
  is unchanged. On the JVM, with no layouter, ELK Java is still used.
- ElkEngineJs calls the in-page worker of elkjs synchronously. The first
  ELK diagram loads elk.bundled.js and is rendered again (PendingResources);
  when elkjs cannot be loaded, the default engine is used, with a console
  note. A host can provide elkjs itself through PLANTUML_ELK.
- Reflect is pruned from the TeaVM builds (TeaVM.isTeaVM() in each method).
- CommandPragma honours "layout elk" in every build.
- elkjs (EPL-2.0 OR GPL-3.0) is left out of the MIT npm package.

Checked: on the JVM, the SVG of six ELK diagrams (class, packages, component,
state, usecase, edge to a cluster) is byte for byte the same through elkjs
as through ELK Java, and the same as on develop. ElkJsonGraphTest covers the
JSON graph; tools/browser-test/check-elk.js the browser plumbing (23 checks,
JS engine). The Wasm GC engine renders ELK diagrams too.
EOF
git log --oneline -1 && git status --short | grep -v "^??" **
Status: Completed

Terminal:
```
7f1d643fe ✨ browser build: lay out "!pragma layout elk" with elkjs
 D build/libs/plantuml-1.2023.1beta1-14-g88fdcfaf.jar
 D build/libs/plantuml-1.2023.1beta1-17-g3514e4b8.jar
 D build/libs/plantuml-1.2023.1beta1-8-g9a736746.jar


```


**Tool Call: Write /home/david/.claude/projects/\-home\-david\-plantuml/memory/teavm\-elk.md**
Status: Completed

Diff: /home/david/.claude/projects/-home-david-plantuml/memory/teavm-elk.md
```
---
name: teavm-elk
description: feature/teavm-elk branch — client-side ELK layout in the browser build via elkjs; status and how it was verified
metadata:
  type: project
---
Branch `feature/teavm-elk` (off develop, first commit 7f1d643fe, 2026-11-06) makes `!pragma layout elk` work in the TeaVM browser build by bridging to elkjs 0.12.1 (`src/main/teavm/vendor/elk.bundled.js`, loaded on demand), via a JSON backend behind the `elk/proxy` classes (`ElkJsonGraph`) and `teavm/js/ElkEngineJs`.

**Why:** the user wanted a client-side PlantUML that uses ELK; WebAssembly was considered and rejected (no official ELK Wasm build; the elkjs bridge serves both the JS and Wasm GC engines).

**How to apply:** verification recipe: render cases on the JVM with ELK jars (`/tmp/elkjars`) vs with a Node elkjs layouter and diff the SVGs (they were byte-identical); `tools/browser-test/check-elk.js target=build/generated/teavm/js`. Paused before: README "Building for the Browser" note about elk.bundled.js, CHANGES.md entry, PR.

```


**Tool Call: cd /home/david/.claude/projects/-home-david-plantuml/memory && echo "- [TeaVM ELK](teavm-elk.md) — feature/teavm-elk: browser ELK via elkjs; committed, docs/PR pending" >> MEMORY.md**
Status: Completed

Terminal:
```

Shell cwd was reset to /home/david/plantuml




```


## Assistant

I've committed the work as `7f1d643fe` on `feature/teavm-elk`, with no Co-Authored-By trailer per your saved preference. With it, the browser build lays out `!pragma layout elk` diagrams client-side using elkjs. Before, the browser build silently ignored that pragma.

**What changed:**
- **Parallel JSON route to ELK:** the ELK wrapper classes can now build the layout graph as plain Java and send it to elkjs as JSON, instead of calling ELK Java through reflection. The code that draws the diagram is unchanged, and the desktop build still uses ELK Java.
- **Browser bridge (`ElkEngineJs`):** calls elkjs synchronously. `elk.bundled.js` (1.6 MB) is only downloaded the first time a diagram asks for ELK. If it can't be loaded, the diagram falls back to the normal engine and a console warning says why. A host page can also supply elkjs itself through `PLANTUML_ELK`.
- **Pragma fix:** `CommandPragma` was dropping `layout elk` in the browser build. That was the reason the first browser check failed.
- **Licensing:** elkjs is EPL-2.0 or GPL-3.0, so it is left out of the MIT npm package, matching how the ELK jars are only in the EPL variant.

**How it was checked:**
- **Same output as desktop:** on the JVM, six ELK diagrams (class, nested packages, component, state, usecase, edge to a package) produce byte-identical SVG through elkjs and through ELK Java. That output is also identical to `develop`'s.
- **Unit and regression tests:** the new `ElkJsonGraphTest` passes (5 tests), and the existing `ElkPortTest` passes with the ELK jars on the classpath.
- **Browser:** the new `tools/browser-test/check-elk.js` passes all 23 checks in headless Chromium with the JS engine.
- **Other builds:** I rendered one diagram with the Wasm engine and its output matches the JS engine's. The Java 8 jar builds, and its preprocessed source correctly strips the browser-only code.

**One thing I didn't change:** on the desktop, the ELK wrapper passes the node-label placement setting to ELK in a form ELK can't read. It doesn't affect any of the six test layouts, so I left it alone.

**Paused before:**
- a note in the main README's "Building for the Browser" section about copying `elk.bundled.js`
- a `CHANGES.md` entry
- opening a PR

**Left behind for you to remove or keep:**
- A temporary worktree of `develop` at `/tmp/plantuml-develop`, used for the comparison (`git worktree remove /tmp/plantuml-develop`).
- `tools/browser-test/node_modules` and a Playwright Chromium in `~/.cache/ms-playwright`, from installing the browser test tooling.
