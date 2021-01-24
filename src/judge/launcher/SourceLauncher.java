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
 * An object that prepares for the program setup and launches the program.
 * Temporary files and directories are created when necessary, and are destroyed
 * upon closing.
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class SourceLauncher implements AutoCloseable {
  /** The default prefix of temporary directories. */
  private static final String TEMP_DIR_PREFIX = "submission"; // At least 3 chars long

  /** The submission that contains the program information. */
  private final Submission submission;

  /** The file that contains the program. */
  private File source;
  /** The directory to the program. */
  private File programDirectory;
  /** The parent directory of the program's temporary directory. */
  private File parentDirectory;

  /**
   * Creates a new {@code SourceLauncher} instance with the given submission
   * and parent directory.
   *
   * @param submission      The submission that contains the program.
   * @param parentDirectory The parent directory of the program's temporary directory.
   */
  public SourceLauncher(Submission submission, File parentDirectory) {
    this.submission = submission;
    this.parentDirectory = parentDirectory;
    if (!this.parentDirectory.exists()) {
      this.parentDirectory.mkdirs();
    }
  }

  /**
   * Starts the program and returns A {@code Process} object representing
   * the launched native process.
   *
   * @return A {@code Process} object containing the data of the native process.
   * @throws InternalErrorException if an internal error occurs.
   */
  public abstract Process launch() throws InternalErrorException;

  /**
   * Returns the file extension of the program, which should be starting with a ".".
   *
   * @return The file extension of the program.
   */
  public abstract String getTempFileExtension();

  /**
   * Returns the file name of the program.
   *
   * @return The file name of the program.
   */
  public abstract String getTempFileName();

  /**
   * Returns the language of the program.
   *
   * @return The language of the program.
   */
  public abstract Language getLanguage();


  @Override
  public void close() {
    this.deleteTempDirectory();
  }

  /**
   * Prepares for the program setup, and compile the program if needed.
   * Temporary files and directories are created when necessary.
   *
   * @throws InternalErrorException if an internal error occurs.
   * @throws CompileErrorException  if the program fails to compile.
   */
  public void setup() throws InternalErrorException, CompileErrorException {
    try {
      this.programDirectory = this.createTempDirectory();
      this.source = this.createTempFile(programDirectory);
    } catch (IOException ioException) {
      throw new InternalErrorException(ioException);
    }
  }

  /**
   * Returns the file that contains the program.
   *
   * @return The file that contains the program.
   */
  public File getSource() {
    return this.source;
  }

  /**
   * Creates and returns the directory for the program file.
   *
   * @return A {@code File} object representing the directory for the program file.
   * @throws IOException if an I/O error occurs.
   */
  private File createTempDirectory() throws IOException {
    return Files.createTempDirectory(
      parentDirectory.toPath(),
      SourceLauncher.TEMP_DIR_PREFIX
    ).toFile();
  }

  /**
   * Creates and returns the file containing the program.
   *
   * @param directory The {@code File} object representing the directory of the file.
   * @return          A {@code File} object representing the program file.
   * @throws IOException if an I/O error occurs.
   */
  private File createTempFile(File directory) throws IOException {
    File tempFile = new File(
      directory.toString() + File.separator + this.getTempFileName() + this.getTempFileExtension()
    );

    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
    writer.write(this.submission.getCode());
    writer.close();

    return tempFile;
  }

  /**
   * Deletes the temporary directory, as well as all the files inside it.
   *
   * @return true if the directory, along with all files inside it,
   *         is successfully deleted; false otherwise.
   */
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
