package judge.launcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import entities.Language;
import entities.Submission;
import judge.CompileErrorException;
import judge.InternalErrorException;

/**
 * A {@code SourceLauncher} for compiling and launching java programs.
 * Temporary files and directories are created when necessary, and are destroyed
 * upon closing.
 * <p>
 * Note: in order for the launcher to compile, all submitted programs should have
 * "Main" as the main class's name.
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class JavaSourceLauncher extends SourceLauncher {
  /** The file extension for program files. */
  private static final String FILE_EXTENSION = ".java";
  /** The default name for program files. */
  private static final String FILE_NAME = "Main";

  /**
   * Creates a new {@code JavaSourceLauncher} instance with the given submission
   * and parent directory.
   * <P>
   * Note: in order for the launcher to compile, all submitted programs should have
   * "Main" as the main class's name.
   *
   * @param submission      The submission that contains the program.
   * @param parentDirectory The parent directory of the program's temporary directory.
   */
  public JavaSourceLauncher(Submission submission, File parentDirectory) {
    super(submission, parentDirectory);
  }

  @Override
  public void setup() throws InternalErrorException, CompileErrorException {
    super.setup();
    try {
      this.compileSource();
    } catch (InterruptedException | IOException e) {
      throw new InternalErrorException(e);
    }
  }

  @Override
  public Process launch() throws InternalErrorException {
    ProcessBuilder builder = new ProcessBuilder("java", "Main");
    builder.directory(this.getSource().getParentFile());
    try {
      return builder.start();
    } catch (IOException ioException) {
      throw new InternalErrorException(ioException);
    }
  }

  @Override
  public String getTempFileExtension() {
    return JavaSourceLauncher.FILE_EXTENSION;
  }

  @Override
  public String getTempFileName() {
    return JavaSourceLauncher.FILE_NAME;
  }

  @Override
  public Language getLanguage() {
    return Language.PYTHON;
  }

  /**
   * Compiles the program file.
   *
   * @throws IOException           if an I/O error occurs while checking
   *                               if there is an error message.
   * @throws InterruptedException  if the program process's thread is interrupted.
   * @throws CompileErrorException if a compile error occurs.
   */
  private void compileSource() throws IOException, InterruptedException, CompileErrorException {
    ProcessBuilder builder = new ProcessBuilder("javac", this.getSource().getAbsolutePath());
    Process process = builder.start();
    process.waitFor();

    String errorMsg = this.readStream(new BufferedInputStream(process.getErrorStream()));
    if (errorMsg.length() > 0) {
      throw new CompileErrorException(errorMsg);
    }
  }

  /**
   * Reads the content in an {@code BufferedInputStream} and returns the result as a String.
   *
   * @param stream The {@code BufferedInputStream} to read.
   * @throws IOException if an I/O error occurs while reading the stream.
   */
  private String readStream(BufferedInputStream stream) throws IOException {
    StringBuilder sb = new StringBuilder();
    int curByte = stream.read();
    while (curByte != -1) {
      sb.append((char)(curByte));
      curByte = stream.read();
    }
    return sb.toString();
  }

}
