package templater.language;

/**
 * The different kinds of {@code Token}s that compose the
 * templating language.
 */
public enum TokenKind {
  /** Names like 'div', 'for', attributes like 'src'. */
  IDENTIFIER,
  /** Language names like 'for' (current only keyword). */
  KEYWORD,
  /** Single character symbols like ',', '(', '#'. */
  PUNCTUATION,
  /** String literals typed as '"hello"'. */
  STRING_LITERAL,
  /** Template literals typed as '${sampleText}'. */
  TEMPLATE_LITERAL,
}
