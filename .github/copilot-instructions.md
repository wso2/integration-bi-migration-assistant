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

# mule migrator architectural overview
- mule.v4 package is for mule 4 to ballerina conversion specific logic. 
- mule.v3 package is for mule 3 to ballerina conversion specific logic.
- mule.common package is for common logic that can be used in both mule 3 and mule.
- For conversion from dataweave to ballerina expression/statement an antlr parser and visitors are used.
- mule.v4.dataweave.converter.TestDWConversion's testDWConversion() prints equivalent ballerina mapping for the 
  dataweave. At the moment this is avialable for Dataweave 2.0 only.

