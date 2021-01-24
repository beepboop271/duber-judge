package templater;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import templater.compiler.LanguageElement;

public class Interpreter {
  private HashMap<String, Object> namespace;

  public Interpreter(HashMap<String, Object> namespace) {
    this.namespace = namespace;
  }

  public String resolveStrings(StringResolvables s) {
    StringBuilder sb = new StringBuilder();
    Iterator<StringResolvable> itr = s.iterator();
    while (itr.hasNext()) {
      StringResolvable toResolve = itr.next();
      if (toResolve.isTemplate()) {
        sb.append(this.namespace.get(toResolve.getContent()).toString());
      } else {
        sb.append(toResolve.getContent());
      }
    }
    return sb.toString();
  }

  public String interpret(Template syntaxTree) {
    return interpretHelper(syntaxTree.getSyntaxTree(), new StringBuilder()).toString();
  }

  public StringBuilder interpretHelper(LanguageElement curElem, StringBuilder interpreted) {
    if (curElem instanceof Root) {
      Iterator<LanguageElement> children = ((Root)curElem).getChildren();
      // I mean it technically should only have one child so ashfdasf idk TODO
      while (children.hasNext()) {
        interpreted = interpretHelper(children.next(), interpreted);
      }
      return interpreted;
    }

    if (curElem instanceof StringResolvables) {
      return (interpreted.append(resolveStrings((StringResolvables)curElem)));
    } 

    if (curElem instanceof Loop) {
      Loop loop = (Loop) curElem;
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
    Element elem = (Element) curElem;
    interpreted.append("<" + resolveStrings(elem.getName()));
    if (elem.getId() != null) {
      interpreted.append(" id=" + resolveStrings(elem.getId()));
    }

    Iterator<StringResolvables> classes = elem.getClasses();
    if (classes.hasNext()) {
      interpreted.append(" class='");
      while (classes.hasNext()) {
        interpreted.append(resolveStrings(classes.next())+" ");
      }
      interpreted.append("'");
    }

    Iterator<Map.Entry<String, StringResolvables>> attributes =
      elem.getAttributes();
    while (attributes.hasNext()) {
      Map.Entry<String, StringResolvables> attribute = attributes.next();
      interpreted.append(
        " "+attribute.getKey()+"='"+resolveStrings(attribute.getValue())+"'"
      );
    }

    if (elem.isEmpty()) {
      interpreted.append("/>");
      return interpreted;
    }

    interpreted.append(">");

    Iterator<LanguageElement> children = elem.getChildren();
    while (children.hasNext()) {
      interpreted = interpretHelper(children.next(), interpreted);
    }
    interpreted.append("</"+resolveStrings(elem.getName())+">");
    return interpreted;
  }
}
