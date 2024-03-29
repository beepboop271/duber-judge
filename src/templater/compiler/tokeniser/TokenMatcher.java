package templater.compiler.tokeniser;

import templater.compiler.Matchable;
import templater.language.Token;

/**
 * Matchs a Token from a CharListQueue.
 */
abstract class TokenMatcher implements Matchable<CharListQueue, Token> {
}
