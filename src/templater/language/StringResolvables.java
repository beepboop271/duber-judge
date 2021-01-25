package templater.language;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a series of
 * {@code StringResolvables}--items with string content,
 * whether that content be a literal string or a variable
 * with a string value.
 *
 * @author Kevin Qiao
 * @version 1.0
 */
public class StringResolvables extends LanguageElement implements
  Iterable<StringResolvable> {
  private final List<Token> tokens;

  /**
   * Creates a new {@code StringResolvables}, given a list of
   * tokens.
   *
   * @param tokens The {@code List} of tokens that this
   *               {@code StringResolvables}'s content will be
   *               derived from.
   */
  public StringResolvables(List<Token> tokens) {
    this.tokens = new ArrayList<>(tokens);
  }

  @Override
  public Iterator iterator() {
    return this.new Iterator();
  }

  public class Iterator implements java.util.Iterator<StringResolvable> {
    private final java.util.Iterator<Token> it;

    private Iterator() {
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
