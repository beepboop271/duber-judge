package templater;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import templater.compiler.tokeniser.UnknownTokenException;

public class Templater {
  private static Map<String, Template> templates = new HashMap<>();

  private Templater() {
  }

  public static void prepareTemplate(String name, String source)
    throws UnknownTokenException {
    Templater.templates.put(name, new Template(source));
  }

  public static void prepareTemplate(String name, Path path)
    throws IOException, UnknownTokenException {
    String s = new String(Files.readAllBytes(path), Charset.forName("UTF-8"));
    Templater.templates.put(name, new Template(s));
  }

  public static String fillTemplate(
    String name,
    Map<String, Object> namespace
  ) {
    return new Interpreter(namespace).interpret(Templater.templates.get(name));
  }
}
