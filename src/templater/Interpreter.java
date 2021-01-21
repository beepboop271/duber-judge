package templater;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Interpreter {
  private HashMap<String, Object> namespace;

  public Interpreter(HashMap<String, Object> namespace) {
    this.namespace = namespace;
  }

  public String resolveString(StringResolvable s) {
    if (s.isTemplate()) {
      Object content = this.namespace.get(s.getContent());
      if (content instanceof String) {
        return (String) content;
      }
      // TODO: else exception???
    }
    return s.getContent();
  }

  public String interpret(Template syntaxTree) {
    return interpretHelper(syntaxTree.getSyntaxTree(), "");
  }

  public String interpretHelper(Node curNode, String interpreted) {
    if (curNode instanceof Root) {
      Iterator<Node> children = curNode.getChildren();
      // I mean it technically should only have one child so ashfdasf idk TODO
      while (children.hasNext()) {
        interpreted = interpretHelper(children.next(), interpreted);
      }
      return interpreted;
    }

    if (curNode instanceof LiteralContent) {
      return (interpreted += ((LiteralContent) curNode).getContent());
    } 

    if (curNode instanceof TemplatedContent) {
      // TODO: what if it doesn't map to a string
      return (interpreted += this.namespace.get(((TemplatedContent) curNode).getExpression()));
    }
    
    if (curNode instanceof Loop) {
      Loop loop = (Loop) curNode;
      Object loopTarget = this.namespace.get(resolveString(loop.getTarget()));
      if (loopTarget instanceof Iterable<?>) {
        for (Object item : (Iterable<Object>)loopTarget) {
          this.namespace.put(loop.getLoopVariable(), item);
          Iterator<Node> children = curNode.getChildren();
          while (children.hasNext()) {
            interpreted = interpretHelper(children.next(), interpreted);
          }
        }
        return interpreted;
      }
      // TODO: else throw exception? also arrays?????
    }

    // only option left is element
    Element elem = (Element) curNode;
    interpreted += "<" + resolveString(elem.getName());
    if (elem.getId() != null) {
      interpreted += " id=" + resolveString(elem.getId());
    }

    Iterator<StringResolvable> classes = elem.getClasses();
    if (classes.hasNext()) {
      interpreted += " class='";
      while (classes.hasNext()) {
        interpreted += resolveString(classes.next())+" ";
      }
      interpreted.trim();
      interpreted += "'";
    }

    Iterator<Map.Entry<StringResolvable, StringResolvable>> attributes =
      elem.getAttributes();
    while (attributes.hasNext()) {
      Map.Entry<StringResolvable, StringResolvable> attribute =
        attributes.next();
      interpreted +=
        " "
          +resolveString(attribute.getKey())
          +"='"
          +resolveString(attribute.getValue())
          +"'";
    }

    if (elem.isEmpty()) {
      interpreted += "/>";
      return interpreted;
    }

    interpreted += ">";

    Iterator<Node> children = curNode.getChildren();
    while (children.hasNext()) {
      interpreted = interpretHelper(children.next(), interpreted);
    }
    interpreted += "</"+resolveString(elem.getName())+">";
    return interpreted;
  }
}
