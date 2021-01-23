package judge.launcher;

import java.io.File;
import java.io.IOException;

import entities.Language;
import entities.Submission;
import judge.InternalErrorException;

/**
 * [description]
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class PythonLauncher extends SourceLauncher {

  private static final String FILE_EXTENSION = ".py";

  public PythonLauncher(Submission submission, File tempFileDirectory) {
    super(submission, tempFileDirectory);
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
    return PythonLauncher.FILE_EXTENSION;
  }
  
  @Override
  public Language getLanguage() {
    return Language.PYTHON;
  }
}
