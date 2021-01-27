package templater.language;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a series of {@code StringResolvable}s
 * that can be resolved into a single String by processing
 * each StringResolvable.
 *
 * @author Kevin Qiao
 * @version 1.0
 */
public class StringResolvables extends LanguageElement implements
  Iterable<StringResolvable> {
  /**
   * The sequence of {@code StringResolvable}s holding the
   * content of this StringResolvables.
   */
  private final List<StringResolvable> resolvables;

  /**
   * Creates a new {@code StringResolvables}, given a list of
   * tokens.
   *
   * @param tokens The {@code List} of tokens that this
   *               {@code StringResolvables}'s content will be
   *               derived from.
   */
  public StringResolvables(List<Token> tokens) {
    this.resolvables = new ArrayList<>();
    for (Token t : tokens) {
      this.resolvables.add(new StringResolvable(t));
    }
  }

  @Override
  public ReadOnlyIterator<StringResolvable> iterator() {
    return new ReadOnlyIterator<>(this.resolvables.iterator());
  }
}
