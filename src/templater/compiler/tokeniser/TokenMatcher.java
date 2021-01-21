package templater.compiler.tokeniser;

import templater.compiler.Matchable;

abstract class TokenMatcher implements Matchable<CharArrayQueue, Token> {
  protected static void consumeInput(CharArrayQueue input, int numCharacters) {
    for (int i = 0; i < numCharacters; ++i) {
      input.remove();
    }
  }
}
