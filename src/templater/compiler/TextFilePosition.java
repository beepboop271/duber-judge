package templater.compiler;

public class TextFilePosition implements Cloneable {
  private int line;
  private int column;

  public TextFilePosition() {
    this(1, 1);
  }

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

  public String toDisplayString() {
    return "line "+this.line+", column "+this.column;
  }

  public void advanceLine() {
    ++this.line;
    this.column = 1;
  }

  public void advanceCharacter() {
    ++this.column;
  }

  public int getLine() {
    return this.line;
  }

  public int getColumnn() {
    return this.column;
  }
}
