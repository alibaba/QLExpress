# PRD: Remove ANTLR Dependency and Implement Hand-written Parser

## Introduction

QLExpress currently depends on ANTLR 4.9.3 for lexical analysis and parsing. This dependency introduces several issues:
- Increases JAR size (~350KB for antlr4-runtime)
- ANTLR-generated AST is complex, memory-intensive, and difficult to debug
- Infinite loop issues in certain parsing scenarios
- Poor human readability of the generated syntax tree

This PRD describes the replacement of ANTLR with a hand-written recursive descent parser (top-down) using only Java standard library, resulting in a simplified, more maintainable, and more efficient parsing architecture.

## Goals

- Remove antlr4-runtime dependency completely
- Reduce final JAR size by at least 300KB
- Implement hand-written lexer using Java standard library only
- Implement hand-written top-down (recursive descent) parser
- Design simplified, human-readable AST structure
- Improve parsing performance and reduce memory usage
- Fix ANTLR-related infinite loop bugs
- Maintain Express4Runner core execution API compatibility (script execution results unchanged)
- Ensure all existing unit tests pass (tests may need updates for new AST APIs)
- Keep .g4 grammar files as documentation/reference

## User Stories

### US-001: Design and implement hand-written Token type system
**Description:** As a developer, I need a token type system to replace ANTLR's token definitions so that the lexer can categorize input characters.

**Acceptance Criteria:**
- [ ] Create `TokenType` enum with all necessary token types (IDENTIFIER, LITERAL, OPERATOR, KEYWORD, etc.)
- [ ] Token types cover all grammar rules from existing QLParser.g4
- [ ] Token types are well-documented with clear naming
- [ ] Unit tests for token type validation

### US-002: Implement hand-written Lexer
**Description:** As a developer, I need a lexer that converts source code strings into token streams without using ANTLR.

**Acceptance Criteria:**
- [ ] Create `QLexpressLexer` class in `parser/` package (new package)
- [ ] Lexer handles all token types: identifiers, literals (int, float, string, boolean), operators, keywords
- [ ] Lexer tracks line/column numbers for error reporting
- [ ] Lexer supports interpolation mode configuration (${}, #{})
- [ ] Lexer supports custom selector start/end tokens
- [ ] Lexer handles newlines correctly (strict/non-strict modes)
- [ ] Unit tests for all lexical patterns
- [ ] Performance comparison with ANTLR lexer shows improvement or parity

### US-003: Design simplified AST node structure
**Description:** As a developer, I need a simplified AST structure that is human-readable and memory-efficient, unlike ANTLR's complex ParseTree.

**Acceptance Criteria:**
- [ ] Create base `ASTNode` class with common properties (line, column, source)
- [ ] Create specific node types: `ProgramNode`, `BlockNode`, `ExpressionNode`, `StatementNode`, etc.
- [ ] AST nodes use composition over inheritance where appropriate
- [ ] AST nodes support visitor pattern for code generation
- [ ] AST nodes implement `toString()` for human-readable debugging output
- [ ] Memory footprint is smaller than ANTLR ParseTree
- [ ] Unit tests for AST node construction and validation

### US-004: Implement hand-written recursive descent parser
**Description:** As a developer, I need a top-down parser that builds the new AST from token streams without ANTLR.

**Acceptance Criteria:**
- [ ] Create `QLexpressParser` class using recursive descent algorithm
- [ ] Parser implements all grammar rules from QLParser.g4
- [ ] Parser handles operator precedence correctly
- [ ] Parser handles all statement types: if, while, for, switch, try-catch, return, break, continue
- [ ] Parser handles all expression types: binary, unary, ternary, lambda, method invocation
- [ ] Parser handles type declarations and casts
- [ ] Parser handles import statements
- [ ] Parser supports alias token substitution (operator aliases)
- [ ] Error messages include line/column information
- [ ] Unit tests for all grammar constructs
- [ ] All testsuite/*.ql scripts parse successfully

### US-005: Implement ParserOperatorManager integration
**Description:** As a developer, I need the new parser to integrate with ParserOperatorManager for custom operator support.

**Acceptance Criteria:**
- [ ] Parser uses ParserOperatorManager to determine operator types (PREFIX, SUFFIX, MIDDLE)
- [ ] Parser uses ParserOperatorManager to get operator precedence
- [ ] Parser supports operator alias resolution
- [ ] Integration tests with custom operators

### US-006: Implement SyntaxTreeFactory replacement
**Description:** As a developer, I need to replace SyntaxTreeFactory to use the new parser instead of ANTLR.

**Acceptance Criteria:**
- [ ] Create `SyntaxTreeFactory` in new `parser/` package (or replace existing)
- [ ] Factory method creates new AST (ProgramNode) instead of ANTLR ProgramContext
- [ ] Factory supports all existing options: printTree, profile, interpolationMode
- [ ] Factory maintains backward compatibility for all parameters
- [ ] Unit tests for factory methods

### US-007: Implement InstructionGenerator for new AST
**Description:** As a developer, I need an instruction generator that converts the new AST to QVM instructions.

**Acceptance Criteria:**
- [ ] Create `InstructionGenerator` class for traversing new AST
- [ ] Generator produces correct QVM instruction sequences
- [ ] All instruction types are supported (const, load, operator, call, jump, etc.)
- [ ] Error handling provides clear error messages with source locations
- [ ] Integration tests verify correct instruction output for all language constructs

### US-008: Implement AST visitors for analysis features
**Description:** As a developer, I need visitors for AST analysis (function extraction, variable detection, scope tracking, trace generation).

**Acceptance Criteria:**
- [ ] `FunctionExtractor` visitor - extracts function calls from AST
- [ ] `VariableDetector` visitor - detects variable reads/writes
- [ ] `ScopeAnalyzer` visitor - analyzes variable scopes
- [ ] `TraceGenerator` visitor - generates execution trace information
- [ ] Each visitor works with new AST node types
- [ ] Unit tests for each visitor

### US-009: Update QCompileCache
**Description:** As a developer, I need to update QCompileCache to cache the new AST structure instead of ANTLR ProgramContext.

**Acceptance Criteria:**
- [ ] QCompileCache stores new AST nodes
- [ ] Cache key generation remains compatible
- [ ] Cache invalidation logic works correctly
- [ ] Unit tests for cache functionality

### US-010: Remove ANTLR dependencies from pom.xml
**Description:** As a developer, I need to remove ANTLR dependencies from the Maven build configuration.

**Acceptance Criteria:**
- [ ] Remove antlr4-runtime dependency
- [ ] Remove antlr4-maven-plugin
- [ ] Remove maven-shade-plugin ANTLR relocation configuration
- [ ] Build completes successfully
- [ ] Final JAR size is reduced by at least 300KB

### US-011: Remove ANTLR-generated files and old aparser package
**Description:** As a developer, I need to remove ANTLR-generated code and the old aparser package.

**Acceptance Criteria:**
- [ ] Move .g4 files to `docs/grammar/` directory with documentation header explaining they are reference only
- [ ] Delete target/generated-sources/antlr4/ directory contents
- [ ] Delete entire `com.alibaba.qlexpress4.aparser` package (all old visitors, lexer, parser)
- [ ] No references to ANTLR classes remain in source code
- [ ] New parser package replaces all functionality

### US-012: Integrate new parser with Express4Runner
**Description:** As a developer, I need to update Express4Runner to use the new parser for script execution.

**Acceptance Criteria:**
- [ ] Express4Runner core execution methods (execute, executeExpr) work correctly
- [ ] Internal implementation uses new parser package
- [ ] Compile-time function support maintained
- [ ] All execution tests pass (Express4RunnerTest)
- [ ] Script execution results identical to old implementation

### US-013: Update and rewrite unit tests
**Description:** As a developer, I need to update unit tests to work with the new parser and rewrite tests that depend on ANTLR-specific APIs.

**Acceptance Criteria:**
- [ ] Tests for lexer functionality pass
- [ ] Tests for parser functionality pass
- [ ] Tests for AST construction pass
- [ ] Tests for instruction generation pass
- [ ] CompileTimeFunctionTest updated or rewritten
- [ ] ImportManagerTest updated or rewritten
- [ ] All testsuite/*.ql scripts execute successfully with correct results
- [ ] TestSuiteRunner.suiteTest passes 100%

### US-014: Update documentation
**Description:** As a user, I need accurate documentation reflecting the new parser implementation.

**Acceptance Criteria:**
- [ ] README-source.adoc updated with parser architecture description
- [ ] README-EN-source.adoc updated with parser architecture description
- [ ] CLAUDE.md updated with new architecture diagram
- [ ] Add architecture diagram showing new parsing flow
- [ ] Grammar reference documentation points to docs/grammar/

## Functional Requirements

- FR-1: The system must provide a hand-written lexer that tokenizes QLExpress source code
- FR-2: The system must provide a hand-written recursive descent parser that builds an AST
- FR-3: The system must define a simplified AST node hierarchy
- FR-4: The system must generate QVM instructions from the new AST
- FR-5: The system must maintain Express4Runner core execution API behavior (execute, executeExpr produce same results)
- FR-6: The system must support all existing QLExpress language features
- FR-7: The system must support custom operators via ParserOperatorManager
- FR-8: The system must support interpolation modes (SCRIPT, VARIABLE, DISABLE)
- FR-9: The system must support strict/non-strict newline modes
- FR-10: The system must provide error messages with line/column information
- FR-11: The system must cache compiled code for performance
- FR-12: The system must not depend on ANTLR libraries

## Non-Goals (Out of Scope)

- No changes to QVM (virtual machine) architecture
- No changes to instruction set
- No changes to runtime behavior (unless fixing ANTLR bugs)
- No new language features
- No changes to Express4Runner core execution API (execute, executeExpr, etc.)
- No changes to security strategies
- No changes to function/operator registration
- No IDE integration updates
- No syntax highlighting support

**Breaking Changes Allowed:**
- AST-related APIs (antlr ParseTree → new AST node types)
- Parser-internal APIs
- Visitor interfaces and implementations
- aparser package content can be completely redesigned
- Tests that depend on ANTLR-specific APIs can be rewritten

## Design Considerations

### New Package Structure
```
com.alibaba.qlexpress4.parser/
├── token/
│   ├── TokenType.java           # Token type enum
│   └── Token.java                # Token class with line/column
├── lexer/
│   └── QLexpressLexer.java      # Hand-written lexer
├── ast/
│   ├── ASTNode.java              # Base AST node
│   ├── ProgramNode.java          # Root node
│   ├── StatementNode.java        # Statement nodes
│   ├── ExpressionNode.java       # Expression nodes
│   └── ...                       # Other node types
├── parser/
│   └── QLexpressParser.java      # Recursive descent parser
└── visitor/
    ├── InstructionGenerator.java # Generates QVM instructions
    ├── SyntaxChecker.java        # Validation checks
    └── ...                       # Other visitors
```

### AST Node Design Principles
1. **Immutability**: AST nodes should be immutable after creation
2. **Composition**: Use composition over deep inheritance
3. **Visitor Pattern**: Support visitor pattern for traversals
4. **Source Location**: Every node tracks its source location
5. **toString()**: Human-readable string representation

### Error Handling
- Use `QLException` for parsing errors
- Include line/column numbers in error messages
- Provide context (relevant source snippet) in errors

## Technical Considerations

### Lexer Implementation
- Use Java `StringReader` or `CharSequence` for input
- Single-pass tokenization with lookahead buffer
- Support for nested interpolation expressions
- Efficient string pooling for identifiers

### Parser Implementation
- Recursive descent with one-token lookahead
- Operator precedence parsing for expressions
- Error recovery with synchronization points
- Support for ambiguous grammar resolution

### Performance Targets
- Parsing speed: within 20% of ANTLR or faster
- Memory usage: 30-50% reduction vs ANTLR
- No infinite loops in error cases

### Migration Strategy
- Single PR approach (as requested)
- Keep ANTLR code in separate branch for reference
- Use feature flags only if absolutely necessary

### Testing Strategy
1. Unit tests for lexer (token patterns)
2. Unit tests for parser (grammar rules)
3. Integration tests for end-to-end compilation
4. Comparison tests: new vs old AST instruction output
5. Full testsuite execution

## Success Metrics

- All testsuite/*.ql scripts execute successfully with correct results
- JAR size reduced by at least 300KB
- Parsing performance within 20% of ANTLR or better
- Memory usage reduced by at least 30% compared to ANTLR
- No ANTLR imports remain in source code
- Core execution functionality produces identical results

## Open Questions

1. Should the new AST support serialization/deserialization for caching across JVM restarts?
2. What is the expected timeline for this migration?
3. Should we keep ANTLR in a separate maintenance branch for emergency rollback?
4. Are there any performance benchmarks we should use for comparison?
