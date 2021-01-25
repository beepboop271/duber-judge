package templater.compiler.parser;

import templater.compiler.Matchable;

abstract class TokenMatchable<T> implements Matchable<TokenQueue.Iterator, T> {
  @Override
  public final T tryMatch(TokenQueue.Iterator input) {
    input.mark();
    T result = this.tryMatchInternal(input);
    if (result == null) {
      input.reset();
      return null;
    }
    input.pop();
    return result;
  }

  protected abstract T tryMatchInternal(TokenQueue.Iterator input);
}
