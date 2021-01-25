package templater.compiler.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import templater.language.AttributeElement;
import templater.language.Element;
import templater.language.LanguageElement;
import templater.language.StringResolvables;
import templater.language.Token;
import templater.language.TokenKind;

class ElementMatcher extends TokenMatchable<Element> {
  @Override
  @SuppressWarnings("unchecked")
  protected Element tryMatchInternal(TokenQueue.Iterator input) {
    Token nameStart = new TokenMatcher(TokenKind.IDENTIFIER).tryMatch(input);
    if (nameStart == null) {
      return null;
    }
    // like AttributeContentList, except there must be an
    // identifier as the start
    List<Token> nameList = new ArrayList<>();
    nameList.add(nameStart);
    nameList.addAll(
      new MatchUtils.ZeroOrMore<>(
        new MatchUtils.OneOf<>(
          new TokenMatcher(TokenKind.IDENTIFIER),
          new TokenMatcher(TokenKind.TEMPLATE_LITERAL)
        )
      ).tryMatch(input)
    );
    StringResolvables name = new StringResolvables(nameList);

    List<StringResolvables> classes = new MatchUtils.ZeroOrMore<>(
      new ClassAttributeMatcher()
    ).tryMatch(input);

    // optional
    StringResolvables id = new IdAttributeMatcher().tryMatch(input);
    List<AttributeElement> attributes = new AttributeListMatcher().tryMatch(input);

    Map<String, StringResolvables> attrMap = new HashMap<>();
    if (attributes != null) {
      for (AttributeElement attr : attributes) {
        attrMap.put(attr.getKey(), attr.getValue());
      }
    }

    // the contrasting choices of a semicolon (Token) or an
    // entire block (List<LanguageElement>) force us into
    // picking one into an Object, even if we know if must
    // be either Token | List<LanguageElement>
    Object terminator = new MatchUtils.OneOf<>(
      new TokenMatcher(';'),
      new BlockMatcher()
    ).tryMatch(input);
    if (terminator == null) {
      return null;
    }

    if (terminator instanceof Token) {
      return new Element(new ArrayList<>(), name, classes, id, attrMap, true);
    }
    if (terminator instanceof List<?>) {
      List<?> block = (List<?>)terminator;
      if ((block.size() > 0) && !(block.get(0) instanceof LanguageElement)) {
        // BlockMatcher must return a List<LanguageElement>
        throw new AssertionError();
      }

      return new Element(
        (List<LanguageElement>)block, name, classes, id, attrMap, false
      );
    }

    // the match on terminator must be a token or list, and
    // something is terribly wrong if it isn't
    throw new AssertionError();
  }
}
