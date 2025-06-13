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
