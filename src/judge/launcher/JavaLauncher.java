package judge.launcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import entities.Language;
import entities.Submission;
import judge.CompileErrorException;
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

public class JavaLauncher extends SourceLauncher {

  private static final String FILE_EXTENSION = ".java";
  private static final String FILE_NAME = "Main";

  public JavaLauncher(Submission submission, File tempFileDirectory) {
    super(submission, tempFileDirectory);
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
    return JavaLauncher.FILE_EXTENSION;
  }

  @Override
  public String getTempFileName() {
    return JavaLauncher.FILE_NAME;
  }

  @Override
  public Language getLanguage() {
    return Language.PYTHON;
  }

  private void compileSource() throws IOException, InterruptedException, CompileErrorException {
    ProcessBuilder builder = new ProcessBuilder("javac", this.getSource().getAbsolutePath());
    Process process = builder.start();
    process.waitFor();

    String errorMsg = this.readStream(new BufferedInputStream(process.getErrorStream()));
    if (errorMsg.length() > 0) {
      throw new CompileErrorException(errorMsg);
    }
  }

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
