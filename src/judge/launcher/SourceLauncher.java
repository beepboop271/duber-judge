package judge.launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

public abstract class SourceLauncher implements AutoCloseable {

  private static final String TEMP_FILE_PREFIX = "submission"; // At least 3 chars long

  private final Submission submission;

  private File source;
  private File directory;
  
  public SourceLauncher(Submission submission, File tempFileDirectory) {
    this.submission = submission;
    this.directory = tempFileDirectory;
    if (!this.directory.exists()) {
      this.directory.mkdirs();
    }
  }

  public abstract Process launch() throws InternalErrorException;

  public abstract String getTempFileExtension();

  public abstract Language getLanguage();
  
  @Override
  public void close() {
    this.deleteTempFile();
  }

  public void setup() throws InternalErrorException, CompileErrorException {
    try {
      this.source = this.createTempFile();
    } catch (IOException ioException) {
      throw new InternalErrorException(ioException);
    }
  }

  public File getSource() {
    return this.source;
  }

  private File createTempFile() throws IOException {
    File tempFile = File.createTempFile(
      SourceLauncher.TEMP_FILE_PREFIX,
      this.getTempFileExtension(),
      this.directory
    );

    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
    writer.write(this.submission.getCode());
    writer.close();

    return tempFile;
  }

  private boolean deleteTempFile() {
    if (this.source == null) {
      return false;
    }
    return this.source.delete();
  }

}
