package templater.compiler.parser;

import java.util.ArrayDeque;
import java.util.Collection;

import templater.compiler.ArrayListQueue;
import templater.compiler.TextFilePosition;
import templater.language.Token;

public class TokenQueue extends ArrayListQueue<Token> {
  public TokenQueue(Collection<Token> tokens) {
    super(tokens);
  }

  @Override
  public Iterator iterator() {
    return this.new Iterator();
  }

  public class Iterator extends ArrayListQueue<Token>.Iterator {
    private final ArrayDeque<Integer> marks;

    public Iterator() {
      super();
      this.marks = new ArrayDeque<>();
    }

    public void mark() {
      this.marks.push(this.getIndex());
    }

    public void reset() {
      this.setIndex(this.marks.pop());
    }

    public void pop() {
      this.marks.pop();
    }

    public TextFilePosition getPosition() {
      try {
        return TokenQueue.this.get(this.getIndex()).getPosition();
      } catch (IndexOutOfBoundsException e) {
        return new TextFilePosition.Eof();
      }
    }
  }
}
