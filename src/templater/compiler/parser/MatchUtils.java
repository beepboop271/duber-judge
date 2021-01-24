package templater.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import templater.compiler.LanguageElement;

class MatchUtils {
  static class OneOf<T> extends TokenMatchable<T> {
    private TokenMatchable<? extends T>[] matchers;

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

  static class ZeroOrMore<T> extends TokenMatchable<List<T>> {
    private TokenMatchable<T> matcher;

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

  static class OneOrMore<T> extends TokenMatchable<List<T>> {
    private TokenMatchable<T> matcher;

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
