package judge.launcher;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import entities.Submission;
import judge.CompileErrorException;
import judge.InternalErrorException;
import judge.UnknownLanguageException;

/**
 * This class consists exclusively of a static method that sets up a source launcher
 * for a given submission and returns a {@code CompletableFuture} of the launcher.
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class SourceLauncherService {
  // prevent instantiation of class
  private SourceLauncherService() {
  }

  /**
   * Sets up a source launcher for a given submission and returns a
   * {@code CompletableFuture} of the launcher.
   *
   * @param submission        The submission that contains the program.
   * @param tempFileDirectory The parent directory of the program's temporary directory.
   * @param pool              The {@code ExecutorService} to submit the task.
   * @return                  A {@code CompletableFuture} of the launcher.
   */
  public static CompletableFuture<SourceLauncher> getSourceLauncher(
    Submission submission,
    File tempFileDirectory,
    ExecutorService pool
  ) {
    CompletableFuture<SourceLauncher> f = new CompletableFuture<SourceLauncher>();
    pool.submit(new SourceLauncherGetter(f, submission, tempFileDirectory));
    return f;
  }


  /**
   * A {@Runnable} object that sets up a source launcher for a given submission
   * and completes the {@code CompletableFuture} of the launcher.
   */
  private static class SourceLauncherGetter implements Runnable {
    /** The submission that contains the program. */
    private final Submission submission;
    /** The parent directory of the program's temporary directory. */
    private final File tempFileDirectory;

    /** The {@code CompletableFuture} of the launcher. */
    private CompletableFuture<SourceLauncher> f;

    /**
     * Creates a new {@code SourceLauncherGetter} instance with a given
     * {@code CompletableFuture} of the launcher, a submission that contains
     * the program, and the parent directory of the program's temporary directory.
     *
     * @param f                 The {@code CompletableFuture} of the launcher.
     * @param submission        The submission that contains the program.
     * @param tempFileDirectory The parent directory of the program's temporary directory.
     */
    public SourceLauncherGetter(
      CompletableFuture<SourceLauncher> f,
      Submission submission,
      File tempFileDirectory
    ) {
      this.f = f;
      this.submission = submission;
      this.tempFileDirectory = tempFileDirectory;
    }

    /**
     * Sets up a source launcher for the given submission and completes
     * the {@code CompletableFuture} of the launcher. If the getter cannot
     * find corresponding launcher, the {@code CompletableFuture} will be
     * completed exceptionally with an {@code UnknownLanguageException}.
     */
    @Override
    public void run() {
      SourceLauncher launcher = null;

      switch (submission.getLanguage()) {
        case PYTHON:
          launcher = new PythonSourceLauncher(submission, tempFileDirectory);
          break;

        case JAVA:
          launcher = new JavaSourceLauncher(submission, tempFileDirectory);
          break;
      }

      if (launcher == null) { // cannot find corresponding launcher
        this.f.completeExceptionally(new UnknownLanguageException(this.submission.getLanguage()));
        return;
      }

      // set up the launcher
      try {
        launcher.setup();
        this.f.complete(launcher);
      } catch (InternalErrorException | CompileErrorException e) {
        launcher.close();
        this.f.completeExceptionally(e);
      }
    }
  }
}
