package templater;

import java.util.ArrayList;
import java.util.List;

import templater.compiler.LanguageElement;
import templater.compiler.tokeniser.Token;

public class StringResolvables extends LanguageElement implements
  Iterable<StringResolvable> {
  private final List<Token> tokens;

  public StringResolvables(List<Token> tokens) {
    this.tokens = new ArrayList<>(tokens);
  }

  @Override
  public Iterator iterator() {
    return this.new Iterator();
  }

  class Iterator implements java.util.Iterator<StringResolvable> {
    private final java.util.Iterator<Token> it;

    Iterator() {
      this.it = StringResolvables.this.tokens.iterator();
    }

    @Override
    public boolean hasNext() {
      return it.hasNext();
    }

    @Override
    public StringResolvable next() {
      return new StringResolvable(it.next());
    }
  }
}
