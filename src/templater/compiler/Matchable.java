package templater.compiler;

public interface Matchable<I, O> {
  public O tryMatch(I input);
}
