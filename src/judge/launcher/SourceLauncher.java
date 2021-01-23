package judge.launcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

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

  private static final String TEMP_DIR_PREFIX = "submission"; // At least 3 chars long

  private final Submission submission;

  private File source;
  private File rootDirectory;
  private File programDirectory;
  
  public SourceLauncher(Submission submission, File tempFileDirectory) {
    this.submission = submission;
    this.rootDirectory = tempFileDirectory;
    if (!this.rootDirectory.exists()) {
      this.rootDirectory.mkdirs();
    }
  }

  public abstract Process launch() throws InternalErrorException;

  public abstract String getTempFileExtension();

  public abstract String getTempFileName();

  public abstract Language getLanguage();
  
  @Override
  public void close() {
    this.deleteTempDirectory();
  }

  public void setup() throws InternalErrorException, CompileErrorException {
    try {
      this.programDirectory = this.createTempDirectory();
      this.source = this.createTempFile(programDirectory);
    } catch (IOException ioException) {
      throw new InternalErrorException(ioException);
    }
  }

  public File getSource() {
    return this.source;
  }

  private File createTempDirectory() throws IOException {
    return Files.createTempDirectory(
      rootDirectory.toPath(),
      SourceLauncher.TEMP_DIR_PREFIX
    ).toFile();
  }

  private File createTempFile(File directory) throws IOException {
    File tempFile = new File(
      directory.toString() + File.separator + this.getTempFileName() + this.getTempFileExtension()
    );
    
    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
    writer.write(this.submission.getCode());
    writer.close();

    return tempFile;
  }

  private boolean deleteTempDirectory() {
    if (this.programDirectory == null) {
      return false;
    }
    // files in the directory needs to be deleted first
    File[] files = this.programDirectory.listFiles();
    if (files != null) {
      for (File f : files) {
        f.delete();
      }
    }
    return this.programDirectory.delete();
  }
}
