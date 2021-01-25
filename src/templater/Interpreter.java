package templater;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import templater.compiler.LanguageElement;

/**
 * A class for a {@code Interpreter} that can read templates
 * and translate them into their string HTML representation.
 *
 * @author Paula Yuan
 * @version 1.0
 */
public class Interpreter {
  /** The wrapped iterator to read from. */
  private Map<String, Object> namespace;

  /**
   * Creates a new {@code Interpreter}, given the required
   * namespace.
   *
   * @param namespace The {@code Map} dictating variable-value
   *                  associations for this page.
   */
  public Interpreter(Map<String, Object> namespace) {
    this.namespace = namespace;
  }

  /**
   * Translates a {@code StringResolvables} into the string
   * containing its contents.
   *
   * @param s The {@code StringResolvables} to translate.
   * @return String, the translated string
   */
  public String resolveStrings(StringResolvables s) {
    StringBuilder sb = new StringBuilder();
    Iterator<StringResolvable> itr = s.iterator();
    while (itr.hasNext()) {
      StringResolvable toResolve = itr.next();
      if (toResolve.isTemplate()) {
        String[] content = toResolve.getContent().split(".");
        Object o = this.namespace.get(toResolve.getContent());
        if (content.length > 1) {
          for (int i = 1; i < content.length; i++) {
            try {
              Method method = o.getClass().getMethod(content[i]);
              o = method.invoke(o);
            } catch (NoSuchMethodException | SecurityException e) {
              e.printStackTrace();
            }
          }
        }
        sb.append(o.toString());
      } else {
        sb.append(toResolve.getContent());
      }
    }
    return sb.toString();
  }

  /**
   * Creates the HTML string associated with the given {@code Template}
   * through a helper method. The wrapping exists to eliminate unnecessary
   * concerns for the user.
   *
   * @param syntaxTree The {@code Template} to translate.
   * @return String, the HTML string derived from this template.
   */
  public String interpret(Template syntaxTree) {
    return interpretHelper(syntaxTree.getSyntaxTree(), new StringBuilder())
      .toString();
  }

  /**
   * Appends to the given HTML string associated with the {@code Template} 
   * by recursively walking through the syntax tree and translating each
   * {@code LanguageElement}.
   *
   * @param curElem The {@code LanguageElement} the walk is currently on.
   * @param interpreted The {@code StringBuilder} being added to.
   */
  public StringBuilder interpretHelper(
    LanguageElement curElem,
    StringBuilder interpreted
  ) {
    if (curElem instanceof Root) {
      Iterator<LanguageElement> children = ((Root)curElem).getChildren();
      while (children.hasNext()) {
        interpreted = interpretHelper(children.next(), interpreted);
      }
      return interpreted;
    }

    if (curElem instanceof StringResolvables) {
      return (interpreted.append(resolveStrings((StringResolvables)curElem)));
    }

    if (curElem instanceof Loop) {
      Loop loop = (Loop)curElem;
      Object loopTarget = this.namespace.get(resolveStrings(loop.getTarget()));
      if (loopTarget instanceof Iterable<?>) {
        for (Object item : (Iterable<Object>)loopTarget) {
          this.namespace.put(loop.getLoopVariable(), item);
          Iterator<LanguageElement> children = loop.getChildren();
          while (children.hasNext()) {
            interpreted = interpretHelper(children.next(), interpreted);
          }
        }
        return interpreted;
      }
      // TODO: else throw exception? also arrays?????
    }

    // only option left is element
    Element elem = (Element)curElem;
    interpreted.append("<");
    interpreted.append(resolveStrings(elem.getName()));
    if (elem.getId() != null) {
      interpreted.append(" id=");
      interpreted.append(resolveStrings(elem.getId()));
    }

    Iterator<StringResolvables> classes = elem.getClasses();
    if (classes.hasNext()) {
      interpreted.append(" class='");
      while (classes.hasNext()) {
        interpreted.append(resolveStrings(classes.next()));
        interpreted.append(" ");
      }
      // get rid of trailing whitespace
      interpreted.deleteCharAt(interpreted.length() - 1);
      interpreted.append("'");
    }

    Iterator<Map.Entry<String, StringResolvables>> attributes =
      elem.getAttributes();
    while (attributes.hasNext()) {
      Map.Entry<String, StringResolvables> attribute = attributes.next();
      interpreted.append(" ");
      interpreted.append(attribute.getKey());
      interpreted.append("='");
      interpreted.append(resolveStrings(attribute.getValue()));
      interpreted.append("'");
    }

    interpreted.append(">");
    
    if (elem.isEmpty()) {
      return interpreted;
    }

    Iterator<LanguageElement> children = elem.getChildren();
    while (children.hasNext()) {
      interpreted = interpretHelper(children.next(), interpreted);
    }
    interpreted.append("</");
    interpreted.append(resolveStrings(elem.getName()));
    interpreted.append(">");
    return interpreted;
  }
}
