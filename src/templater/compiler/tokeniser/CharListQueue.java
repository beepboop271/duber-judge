package templater.compiler.tokeniser;

import java.util.Collection;
import java.util.NoSuchElementException;

import templater.compiler.ArrayListQueue;
import templater.compiler.TextFilePosition;

public class CharListQueue extends ArrayListQueue<Character> implements
  CharSequence {

  private final TextFilePosition position = new TextFilePosition();

  public CharListQueue(int requestedCapacity) {
    super(requestedCapacity);
  }

  public CharListQueue() {
    super();
  }

  public CharListQueue(Collection<Character> c) {
    super(c);
  }

  public CharListQueue(Collection<Character> c, int requestedCapacity) {
    super(c, requestedCapacity);
  }

  public CharListQueue(Character[] c) {
    super(c);
  }

  public CharListQueue(Character[] c, int requestedCapacity) {
    super(c, requestedCapacity);
  }

  public CharListQueue(CharSequence c) {
    super(CharListQueue.charsToCharacters(c.toString().toCharArray()));
  }

  public CharListQueue(CharSequence c, int requestedCapacity) {
    super(
      CharListQueue.charsToCharacters(c.toString().toCharArray()),
      requestedCapacity
    );
  }

  private static Character[] charsToCharacters(char[] charArray) {
    Character[] c = new Character[charArray.length];
    for (int i = 0; i < charArray.length; ++i) {
      c[i] = charArray[i];
    }
    return c;
  }

  private static String charactersToString(Character[] characters) {
    StringBuilder sb = new StringBuilder(characters.length);
    for (char c : characters) {
      sb.append(c);
    }
    return sb.toString();
  }

  public void add(CharSequence c) {
    this.add(c.toString().toCharArray());
  }

  public void add(char[] c) {
    this.add(CharListQueue.charsToCharacters(c));
  }

  @Override
  public Character remove() throws NoSuchElementException {
    char c = super.remove();
    this.position.advanceCharacter();
    if (c == '\n') {
      this.position.advanceLine();
    }
    return c;
  }

  public TextFilePosition getPosition() {
    return this.position.clone();
  }

  // CharSequence methods

  @Override
  public int length() {
    return this.size();
  }

  @Override
  public char charAt(int index) {
    return this.get(index);
  }

  @Override
  public String toString() {
    return CharListQueue.charactersToString(
      this.toArray(new Character[this.size()])
    );
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    int length = end-start;
    return CharListQueue.charactersToString(
      this.toArray(new Character[length], start, length)
    );
  }
}
