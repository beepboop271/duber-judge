package templater.compiler.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Several classes that act as utility higher order matchers.
 */
class MatchUtils {
  /**
   * A matcher that matches if one of the provided matchers
   * match, yielding the first match. The matches are
   * attempted in the order they are provided in the
   * constructor.
   *
   * @param <T> The type of object to produce.
   */
  static class OneOf<T> extends TokenMatchable<T> {
    /** The matchers to attempt matches with. */
    private final TokenMatchable<? extends T>[] matchers;

    /**
     * Creates a new OneOf matcher from the given matchers. The
     * matcher matches if one of hte provided matchers match,
     * yielding the first match. Matches are attempted in the
     * order they are provided.
     *
     * @param matchers The matchers to use.
     */
    @SafeVarargs  // array is only read from-> never perform operations-> safe
    OneOf(TokenMatchable<? extends T>... matchers) {
      this.matchers = matchers;
    }

    @Override
    protected T tryMatchInternal(TokenQueue.Iterator input) {
      for (TokenMatchable<? extends T> matcher : this.matchers) {
        T match = matcher.tryMatch(input);
        if (match != null) {
          return match;
        }
      }
      return null;
    }
  }

  /**
   * A matcher that greedily matches any amount of the given
   * matcher. This matcher never fails because zero matches
   * still counts as a match.
   *
   * @param <T> The type of object to produce inside a List.
   */
  static class ZeroOrMore<T> extends TokenMatchable<List<T>> {
    /** The matcher to attempt matches with. */
    private final TokenMatchable<T> matcher;

    /**
     * Creates a new ZeroOrMore matcher from the given matcher.
     * Greedily matches zero or more instances of the given
     * matcher.
     *
     * @param matcher The matcher to use.
     */
    ZeroOrMore(TokenMatchable<T> matcher) {
      this.matcher = matcher;
    }

    @Override
    protected List<T> tryMatchInternal(TokenQueue.Iterator input) {
      // never return null because zero still counts as a match
      List<T> matches = new ArrayList<>();

      T match = this.matcher.tryMatch(input);
      while (match != null) {
        matches.add(match);
        match = this.matcher.tryMatch(input);
      }

      return matches;
    }
  }

  /**
   * A matcher that greedily matches one or more of the given
   * matcher.
   *
   * @param <T> The type of object to produce inside a List.
   */
  static class OneOrMore<T> extends TokenMatchable<List<T>> {
    /** The matcher to attempt matches with. */
    private final TokenMatchable<T> matcher;

    /**
     * Creates a new OneOrMore matcher from the given matcher.
     * Greedily matches one or more instances of the given
     * matcher.
     *
     * @param matcher The matcher to use.
     */
    OneOrMore(TokenMatchable<T> matcher) {
      this.matcher = matcher;
    }

    @Override
    protected List<T> tryMatchInternal(TokenQueue.Iterator input) {
      T match = this.matcher.tryMatch(input);
      if (match == null) {
        return null;
      }

      List<T> matches = new ArrayList<>();
      do {
        matches.add(match);
        match = this.matcher.tryMatch(input);
      } while (match != null);

      return matches;
    }
  }
}
