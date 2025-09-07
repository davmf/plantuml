# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build System

PlantUML uses Gradle as the primary build system:

- **Build**: `gradle build` - Builds the project and creates JAR in `build/libs`
- **Test**: `gradle test` - Runs JUnit tests
- **Clean**: `gradle clean` - Cleans build artifacts
- **JAR**: `gradle jar` - Creates the main PlantUML JAR file
- **PDF JAR**: `gradle pdfJar` - Creates JAR with PDF generation dependencies

Alternative Ant build is available: `ant` (uses `build.xml`)

### Java Version Support

- **Runtime**: Java 8 compatible (minimum requirement)
- **Development**: Java 17+ recommended for testing
- **Build parameter**: Use `-PjavacRelease=8` or `-PjavacRelease=17` to target specific versions
- **Testing**: Can build with Java 17 then test with Java 8

## Architecture Overview

### Core Structure

- **Main Class**: `net.sourceforge.plantuml.Run` - CLI entry point
- **Version Management**: Version string must be synchronized between `gradle.properties` and `src/net/sourceforge/plantuml/version/Version.java`
- **Source Layout**: Main source in `src/`, tests in `test/`

### Key Packages

- `net.sourceforge.plantuml.activitydiagram*` - Activity diagram implementations (legacy and v3)
- `net.sourceforge.plantuml.classdiagram` - Class diagram support
- `net.sourceforge.plantuml.sequencediagram` - Sequence diagram functionality
- `net.sourceforge.plantuml.cucadiagram` - Core UML diagram abstractions
- `net.sourceforge.plantuml.dot` - GraphViz DOT integration
- `net.sourceforge.plantuml.svek` - SVG-based rendering engine
- `net.sourceforge.plantuml.tim` - Template and macro processing
- `net.sourceforge.plantuml.security` - Security framework and authentication
- `net.sourceforge.plantuml.code` - Text encoding/compression utilities
- `gen/` - Generated code from C GraphViz libraries (read-only)

### Diagram Processing Flow

1. **Input**: Text parsed by `BlockUmlBuilder` into `BlockUml` objects
2. **Processing**: Diagram-specific factories create `PSystem` implementations
3. **Rendering**: Output via `ImageBuilder` to various formats (PNG, SVG, PDF)

### External Dependencies

- **GraphViz Integration**: Embedded Windows version in `graphviz.dat` (compressed with Brotli)
- **PDF Generation**: Apache FOP and Batik (runtime dependencies for PDF feature)
- **Test Dependencies**: JUnit 5, AssertJ, Mockito, XMLUnit

## Development Notes

- Main library has minimal external dependencies to remain lightweight
- Test dependencies are more extensive for comprehensive testing
- GraphViz integration handles both embedded (Windows) and system-installed versions
- Security framework supports various authentication methods (basic auth, OAuth2, tokens)
- Multiple output formats supported through pluggable renderer system