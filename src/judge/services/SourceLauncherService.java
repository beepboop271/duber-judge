package judge.services;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import entities.Submission;
import judge.entities.JavaLauncher;
import judge.entities.PythonLauncher;
import judge.entities.SourceLauncher;
import judge.entities.UnknownLanguageException;

/**
 * [description]
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class SourceLauncherService {

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
      
      if (launcher != null) {
        this.f.complete(launcher);
      } else { // cannot find corresponding launcher
        this.f.completeExceptionally(new UnknownLanguageException(this.submission.getLanguage()));
      }
    }
  }

}
