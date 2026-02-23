# General coding guidelines
- Don't leave trailing whitespace.

# Java coding guidelines
- Don't add doc comments to private methods.
- When declaring new helper methods, they should be private and adjacent to the method that calls them.
- Maximum line length is 120 characters.
- Keep commit messages short.
- Wrap single line if statements in parentheses.
- If the method don't return null add annotation `@NotNull` to the method signature.
    - Prefer Optionals over null values.
- If a method (or constructor) has implicit assumptions about state (such as a non-null argument), add assertions.
- Don't add comments to describe statements, code should be self-explanatory. If not change the code.
- Don't declare single use variables. Directly create the variable in the place where it is used.

# Mule-to-Ballerina Migration Assistant: Architectural Overview
- The `mule.v4` package contains logic specific to converting Mule 4 projects to Ballerina.
- The `mule.v3` package contains logic specific to converting Mule 3 projects to Ballerina.
- The `mule.common` package provides shared logic and utilities used by both Mule 3 and Mule 4 conversions.
- DataWeave-to-Ballerina expression and statement conversion is handled using an ANTLR parser and visitor pattern.
- The class `mule.v4.dataweave.converter.TestDWConversion` (method `testDWConversion()`) prints the equivalent Ballerina mapping for a given DataWeave script. 
  Currently, this supports DataWeave 2.0 only.
