package templater.compiler.tokeniser;

import java.util.NoSuchElementException;

import templater.compiler.ArrayListQueue;
import templater.compiler.TextFilePosition;

/**
 * An ArrayListQueue of {@code Character}s that also
 * implements {@link java.lang.CharSequence}.
 */
public class CharListQueue extends ArrayListQueue<Character> implements
  CharSequence {

  /** The position the head of this queue is at. */
  private final TextFilePosition position = new TextFilePosition();

  /**
   * Constructs a new CharListQueue containing the elements of
   * the given CharSequence.
   *
   * @param c The CharSequence to initialize the queue with.
   */
  public CharListQueue(CharSequence c) {
    super(CharListQueue.charsToCharacters(c.toString().toCharArray()));
  }

  /**
   * Converts a {@code char[]} into a {@code Character[]}.
   *
   * @param charArray The {@code char[]} to convert.
   * @return A new {@code Character[]} containing a copy of
   *         the given {@code char[]}.
   */
  private static Character[] charsToCharacters(char[] charArray) {
    Character[] c = new Character[charArray.length];
    for (int i = 0; i < charArray.length; ++i) {
      c[i] = charArray[i];
    }
    return c;
  }

  /**
   * Converts a {@code Character[]} into a {@code String}.
   *
   * @param characters The {@code Character[]} to convert.
   * @return A new {@code String} containing a copy of the
   *         given {@code Character[]}.
   */
  private static String charactersToString(Character[] characters) {
    StringBuilder sb = new StringBuilder(characters.length);
    for (char c : characters) {
      sb.append(c);
    }
    return sb.toString();
  }

  /**
   * {@inheritDoc} In addition, the TextFilePosition of this
   * queue is advanced according to the character that was
   * removed.
   */
  @Override
  public Character remove() throws NoSuchElementException {
    char c = super.remove();
    this.position.advanceCharacter();
    if (c == '\n') {
      this.position.advanceLine();
    }
    return c;
  }

  /**
   * Gets the current position of the front of the queue.
   *
   * @return A copy of the TextFilePosition this queue's head
   *         is at.
   */
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
