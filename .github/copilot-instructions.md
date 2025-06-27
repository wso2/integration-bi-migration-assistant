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
- If we need to pass in "context" it should be the first argument to the method.
