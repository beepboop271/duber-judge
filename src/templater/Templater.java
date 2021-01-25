package templater;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import templater.compiler.tokeniser.UnknownTokenException;

/**
 * The static public class to perform all templating
 * operations with.
 *
 * @author Kevin QIao
 * @version 1.0
 */
public class Templater {
  /** A map from names to loaded {@code Template}s. */
  private static Map<String, Template> templates = new HashMap<>();

  private Templater() {
  }

  /**
   * Loads a template from the given template language source
   * code and stores it under the provided name.
   *
   * @param name   The name to store the template with.
   * @param source The source code of the template.
   * @throws UnknownTokenException When an unknown string is
   *                               found in the source.
   */
  public static void prepareTemplate(String name, String source)
    throws UnknownTokenException {
    Templater.templates.put(name, new Template(source));
  }

  /**
   * Loads a template from the given file path and stores it
   * under the provided name. Callers can use
   * {@link java.nio.file.Paths#get(String, String...)} to
   * produce the required {@code Path} object.
   *
   * @param name The name to store the template with.
   * @param path The path to read a string containing template
   *             source code from.
   * @throws IOException           When an error occurs
   *                               reading the file.
   * @throws UnknownTokenException When an unknown string is
   *                               found in the source.
   */
  public static void prepareTemplate(String name, Path path)
    throws IOException, UnknownTokenException {
    String s = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    Templater.templates.put(name, new Template(s));
  }

  /**
   * Evaluates a loaded template with the provided namespace
   * and returns the resulting HTML.
   *
   * @param name      The name of the stored template to use.
   * @param namespace The mapping of variable names used
   *                  within the template source to objects to
   *                  be read while filling in the template.
   * @return The HTML produced by filling the requested
   *         template with the given variables.
   */
  public static String fillTemplate(
    String name,
    Map<String, Object> namespace
  ) {
    return new Interpreter(namespace).interpret(Templater.templates.get(name));
  }
}
