package judge.launcher;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import entities.Submission;
import judge.CompileErrorException;
import judge.InternalErrorException;
import judge.UnknownLanguageException;

/**
 * [description]
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ProgramLauncher {

  private ProgramLauncher() {
  }
  
  public static CompletableFuture<SourceLauncher> getSourceLauncher(
    Submission submission,
    File tempFileDirectory,
    ExecutorService pool
  ) {
    CompletableFuture<SourceLauncher> f = new CompletableFuture<SourceLauncher>();
    pool.submit(new SourceLauncherGetter(f, submission, tempFileDirectory));
    return f;
  }


  private static class SourceLauncherGetter implements Runnable {

    private final Submission submission;
    private final File tempFileDirectory;

    private CompletableFuture<SourceLauncher> f;

    public SourceLauncherGetter(
      CompletableFuture<SourceLauncher> f,
      Submission submission,
      File tempFileDirectory
    ) {
      this.f = f;
      this.submission = submission;
      this.tempFileDirectory = tempFileDirectory;
    }

    @Override
    public void run() {
      SourceLauncher launcher = null;

      switch (submission.getLanguage()) {
        case PYTHON:
          launcher = new PythonLauncher(submission, tempFileDirectory);
          break;

        case JAVA:
          launcher = new JavaLauncher(submission, tempFileDirectory);
          break;
      }

      if (launcher == null) { // cannot find corresponding launcher
        this.f.completeExceptionally(new UnknownLanguageException(this.submission.getLanguage()));
        return;
      }

      // setup the launcher
      try {
        launcher.setup();
        this.f.complete(launcher);
      } catch (InternalErrorException | CompileErrorException e) {
        this.f.completeExceptionally(e);
      }
    }
  }
}
