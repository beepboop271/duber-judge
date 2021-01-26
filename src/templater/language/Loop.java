package templater.language;

import java.util.List;

/**
 * A class representing an interable, to facilitate easy
 * creation of clusters of similar elements.
 *
 * @author Paula Yuan, Kevin Qiao
 * @version 1.0
 */
public class Loop extends Node {
  /** The name of the iterable object or array to loop over. */
  private final StringResolvables target;
  /** The name of the variable to update in each iteration. */
  private final String loopVariable;

  /**
   * Creates a new {@code Loop}, given a list of children, the
   * loop variable, and the target associated with the object
   * on which to loop.
   *
   * @param children     The {@code List} of this loop's
   *                     children.
   * @param loopVariable The name of the variable to update in
   *                     each iteration.
   * @param target       The name of the iterable object or
   *                     array to loop over.
   */
  public Loop(
    List<LanguageElement> children,
    String loopVariable,
    StringResolvables target
  ) {
    super(children);
    this.target = target;
    this.loopVariable = loopVariable;
  }

  /**
   * Gets the name of the iterable object or array to loop
   * over.
   *
   * @return The name of the iterable object or array to loop
   *         over.
   */
  public StringResolvables getTarget() {
    return this.target;
  }

  /**
   * Gets the name of the variable to update in each
   * iteration.
   *
   * @return The name of the variable to update in each
   *         iteration.
   */
  public String getLoopVariable() {
    return this.loopVariable;
  }
}
