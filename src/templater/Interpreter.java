package templater;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import templater.language.Element;
import templater.language.LanguageElement;
import templater.language.Loop;
import templater.language.Root;
import templater.language.StringResolvable;
import templater.language.StringResolvables;

/**
 * A class for a {@code Interpreter} that can read templates
 * and translate them into their string HTML representation.
 *
 * @author Paula Yuan
 * @version 1.0
 */
public class Interpreter {
  /**
   * The namespace map whence template-mentioned objects are
   * retrieved.
   */
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
        sb.append(this.resolveObject(toResolve.getContent()).toString());
      } else {
        sb.append(toResolve.getContent());
      }
    }
    return sb.toString();
  }

  /**
   * Gets the appropriate object from namespace and fetches
   * the necessary attribute based on its templated string.
   *
   * @param s The templated string
   * @return Object, the required attribute
   */
  public Object resolveObject(String s) {
    String[] content = s.split("\\.");
    Object o = this.namespace.get(content[0]);
    if (content.length > 1) {
      o = this.resolveAttributes(o, content);
    }
    return o;
  }

  /**
   * Determines the correct attribute of an object required
   * according to the array of methods that need to be called
   * on it to retrieve said attribute.
   *
   * @param o           The {@code Object} to operate on.
   * @param methodsList The {@code String[]} of method names.
   * @return Object, the required attribute
   */
  public Object resolveAttributes(Object o, String[] methodsList) {
    for (int i = 1; i < methodsList.length; i++) {
      try {
        Method method = o.getClass().getMethod(methodsList[i]);
        o = method.invoke(o);
      } catch (
        NoSuchMethodException
        | SecurityException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException e
      ) {
        e.printStackTrace();
      }
    }
    return o;
  }

  /**
   * Handles one round of interpretation for an item that's
   * part of an array or a collection being looped over. This
   * helper exists purely for aesthetic purposes.
   *
   * @param loop        The {@code Loop} the interpretation
   *                    walk is currently in.
   * @param interpreted The {@code StringBuilder} of the
   *                    interpreted HTML string thus far.
   * @param curItem     The {@code Object} part of the item
   *                    being looped over that this walk is
   *                    currently processing.
   * @return StringBuilder, the updated HTML string's
   *         StringBuilder.
   */
  public StringBuilder handleIteration(
    Loop loop,
    StringBuilder interpreted,
    Object curItem
  ) {
    this.namespace.put(loop.getLoopVariable(), curItem);
    Iterator<LanguageElement> children = loop.getChildren();
    while (children.hasNext()) {
      interpreted = interpretHelper(children.next(), interpreted);
    }
    return interpreted;
  }

  /**
   * Handles interpretation as it occurs on loops. This helper
   * exists solely for aesthetic purposes, to maintain overall
   * readability.
   *
   * @param loopTarget  The target array or collection to be
   *                    looped over.
   * @param loop        The {@code Loop} the walk is currently
   *                    on.
   * @param interpreted The {@code StringBuilder} of the
   *                    interpreted HTML string thus far.
   * @return StringBuilder, the updated HTML string's
   *         StringBuilder.
   */
  public StringBuilder handleLoop(
    Object loopTarget,
    Loop loop,
    StringBuilder interpreted
  ) {
    if (loopTarget instanceof Iterable<?>) {
      for (Object item : (Iterable<?>)loopTarget) {
        interpreted = this.handleIteration(loop, interpreted, item);
      }
      return interpreted;
    }
    if (!loopTarget.getClass().isArray()) {
      throw new IllegalArgumentException(
        "Attempting to loop over a non-array, non-iterable object."
      );
    }
    if (loopTarget instanceof boolean[]) {
      for (Object item : (boolean[])loopTarget) {
        interpreted = this.handleIteration(loop, interpreted, item);
      }
      return interpreted;
    }
    if (loopTarget instanceof byte[]) {
      for (Object item : (byte[])loopTarget) {
        interpreted = this.handleIteration(loop, interpreted, item);
      }
      return interpreted;
    }
    if (loopTarget instanceof short[]) {
      for (Object item : (short[])loopTarget) {
        interpreted = this.handleIteration(loop, interpreted, item);
      }
      return interpreted;
    }
    if (loopTarget instanceof char[]) {
      for (Object item : (char[])loopTarget) {
        interpreted = this.handleIteration(loop, interpreted, item);
      }
      return interpreted;
    }
    if (loopTarget instanceof int[]) {
      for (Object item : (int[])loopTarget) {
        interpreted = this.handleIteration(loop, interpreted, item);
      }
      return interpreted;
    }
    if (loopTarget instanceof long[]) {
      for (Object item : (float[])loopTarget) {
        interpreted = this.handleIteration(loop, interpreted, item);
      }
      return interpreted;
    }
    if (loopTarget instanceof float[]) {
      for (Object item : (float[])loopTarget) {
        interpreted = this.handleIteration(loop, interpreted, item);
      }
      return interpreted;
    }
    if (loopTarget instanceof double[]) {
      for (Object item : (double[])loopTarget) {
        interpreted = this.handleIteration(loop, interpreted, item);
      }
      return interpreted;
    }
    if (loopTarget instanceof Object[]) {
      for (Object item : (Object[])loopTarget) {
        interpreted = this.handleIteration(loop, interpreted, item);
      }
      return interpreted;
    }
    throw new AssertionError(
      "Unreachable line indicative of new array type previously unknown."
    );
  }

  /**
   * Creates the HTML string associated with the given
   * {@code Template} through a helper method. The wrapping
   * exists to eliminate unnecessary concerns for the user.
   *
   * @param syntaxTree The {@code Template} to translate.
   * @return String, the HTML string derived from this
   *         template.
   */
  public String interpret(Template syntaxTree) {
    return interpretHelper(syntaxTree.getSyntaxTree(), new StringBuilder())
      .toString();
  }

  /**
   * Appends to the given HTML string associated with the
   * {@code Template} by recursively walking through the
   * syntax tree and translating each {@code LanguageElement}.
   *
   * @param curElem     The {@code LanguageElement} the walk
   *                    is currently on.
   * @param interpreted The {@code StringBuilder} being added
   *                    to.
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
      Object loopTarget =
        this.resolveObject(this.resolveStrings(loop.getTarget()));
          return handleLoop(loopTarget, loop, interpreted);
    }

    // only option left is element
    Element elem = (Element)curElem;
    interpreted.append("<");
    interpreted.append(resolveStrings(elem.getName()));
    if (elem.getId() != null) {
      interpreted.append("id=\"");
      interpreted.append(resolveStrings(elem.getId()));
      interpreted.append("\"");
    }

    Iterator<StringResolvables> classes = elem.getClasses();
    if (classes.hasNext()) {
      interpreted.append(" class=\"");
      while (classes.hasNext()) {
        interpreted.append(resolveStrings(classes.next()));
        interpreted.append(" ");
      }
      // get rid of trailing whitespace
      interpreted.deleteCharAt(interpreted.length() - 1);
      interpreted.append("\"");
    }

    Iterator<Map.Entry<String, StringResolvables>> attributes =
      elem.getAttributes();
    while (attributes.hasNext()) {
      Map.Entry<String, StringResolvables> attribute = attributes.next();
      interpreted.append(" ");
      interpreted.append(attribute.getKey());
      interpreted.append("=\"");
      interpreted.append(resolveStrings(attribute.getValue()));
      interpreted.append("\"");
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
