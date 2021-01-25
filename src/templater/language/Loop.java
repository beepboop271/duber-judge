package templater.language;

import java.util.List;

import templater.compiler.LanguageElement;

/**
 * A class representing an interable, to facilitate easy
 * creation of clusters of similar elements.
 *
 * @author Paula Yuan, Kevin Qiao
 * @version 1.0
 */
public class Loop extends Node {
  private final StringResolvables target;
  private final String loopVariable;

  /**
   * Creates a new {@code Loop}, given a list of children, the
   * loop variable, and the target associated with the object
   * on which to loop.
   *
   * @param children     The {@code List} of this loop's
   *                     children.
   * @param loopVariable The loop variable.
   * @param target       The {@code StringResolvables}
   *                     associated with the item to loop
   *                     over.
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
   * Retrieves the variable associated with this loop's target.
   *
   * @return The target variable.
   */
  public StringResolvables getTarget() {
    return this.target;
  }

  /**
   * Retrieves this loop's loop variable.
   *
   * @return The loop variable.
   */
  public String getLoopVariable() {
    return this.loopVariable;
  }
}
