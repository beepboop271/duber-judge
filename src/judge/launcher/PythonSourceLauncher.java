package judge.launcher;

import java.io.File;
import java.io.IOException;

import entities.Language;
import entities.Submission;
import judge.InternalErrorException;

/**
 * A {@code SourceLauncher} for launching python programs.
 * Temporary files and directories are created when necessary, and are destroyed
 * upon closing.
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class PythonSourceLauncher extends SourceLauncher {
  /** The file extension for program files. */
  private static final String FILE_EXTENSION = ".py";
  /** The default name for program files. */
  private static final String FILE_NAME = "main";

  /**
   * Creates a new {@code PythonSourceLauncher} instance with the given submission
   * and parent directory.
   *
   * @param submission      The submission that contains the program.
   * @param parentDirectory The parent directory of the program's temporary directory.
   */
  public PythonSourceLauncher(Submission submission, File parentDirectory) {
    super(submission, parentDirectory);
  }

  @Override
  public Process launch() throws InternalErrorException {
    ProcessBuilder builder = new ProcessBuilder("python", this.getSource().getAbsolutePath());
    try {
      return builder.start();
    } catch (IOException ioException) {
      ioException.printStackTrace();
      throw new InternalErrorException(ioException);
    }
  }

  @Override
  public String getTempFileExtension() {
    return PythonSourceLauncher.FILE_EXTENSION;
  }

  @Override
  public String getTempFileName() {
    return PythonSourceLauncher.FILE_NAME;
  }

  @Override
  public Language getLanguage() {
    return Language.PYTHON;
  }
}
