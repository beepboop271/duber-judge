package templater.compiler.tokeniser;

/**
 * Represents a position within a text file with a line and
 * column number.
 */
public class TextFilePosition implements Cloneable {
  /** The line number of this position. */
  private int line;
  /** The column number of this position. */
  private int column;

  /**
   * Creates a new TextFilePosition at line 1, column 1.
   */
  public TextFilePosition() {
    this(1, 1);
  }

  /**
   * Creates a new TextFilePosition at the given position.
   *
   * @param line   The line number to start at.
   * @param column The column number to start at.
   */
  public TextFilePosition(int line, int column) {
    this.line = line;
    this.column = column;
  }

  @Override
  public TextFilePosition clone() {
    try {
      return (TextFilePosition)super.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }
  }

  @Override
  public String toString() {
    return super.toString()+"[line:"+this.line+",column:"+this.column+"]";
  }

  /**
   * Produces a string formatted like "line 5, column 4".
   *
   * @return A presentable string describing this
   *         TextFilePosition.
   */
  public String toDisplayString() {
    return "line "+this.line+", column "+this.column;
  }

  /**
   * Advances this position to the start of a new line. The
   * line number is incremented and column number is reset to
   * 1.
   */
  public void advanceLine() {
    ++this.line;
    this.column = 1;
  }

  /**
   * Advances this position by one column.
   */
  public void advanceCharacter() {
    ++this.column;
  }

  /**
   * Gets the current line number of this position.
   *
   * @return The line number.
   */
  public int getLine() {
    return this.line;
  }

  /**
   * Gets the current column number of this position.
   *
   * @return The column number.
   */
  public int getColumnn() {
    return this.column;
  }
}
