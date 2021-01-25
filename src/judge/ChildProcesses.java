package judge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import entities.Language;
import judge.launcher.SourceLauncher;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

/**
 * This class consists exclusively of static methods that
 * initializes and shuts down the child process pool, and
 * launches child processes.
 * <p>
 * Created on 2021.01.17.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ChildProcesses {
  /** The operating system of this computer. */
  private static final OperatingSystem OS = new SystemInfo().getOperatingSystem();
  /** The process id of the current process. */
  private static final int CURRENT_PID = ChildProcesses.OS.getProcessId();

  /**
   * The {@code ChildProcessMonitor} that tracks active child
   * processes.
   */
  private static final ChildProcessMonitor childProcessMonitor = new ChildProcessMonitor();
  /**
   * A {@code ConcurrentHashMap} that maps process ids of
   * active child processes to the corresponding
   * {@code ChildProcess} object.
   */
  private static final ConcurrentHashMap<Integer, ChildProcess> activeChildProcesses
    = new ConcurrentHashMap<>();


  // prevent instantiation of class
  private ChildProcesses() {
  }

  /**
   * Initializes the {@code ChildProcessMonitor} that tracks
   * active child processes.
   */
  public static void initialize() {
    Thread processMonitorThread = new Thread(ChildProcesses.childProcessMonitor);
    processMonitorThread.start();
  }

  /**
   * Shuts down the {@code ChildProcessMonitor} and destroys
   * all active child processes.
   */
  public static void shutdown() {
    synchronized (ChildProcesses.childProcessMonitor) {
      ChildProcesses.childProcessMonitor.notify();
    }
    ChildProcesses.childProcessMonitor.stop();
    // kill all active child processes
    for (ChildProcess p : ChildProcesses.activeChildProcesses.values()) {
      p.getProcess().destroyForcibly();
    }
  }


  /**
   * Launches a child process and returns a corresponding
   * {@code ChildProcess} object.
   *
   * @param launcher        The {@code SourceLauncher} used to
   *                        launch the process.
   * @param timeLimitMillis The maximum duration the process
   *                        is allowed to run for, in
   *                        milliseconds.
   * @param memoryLimitKb   The maximum amount of memory the
   *                        process is allowed to use, in
   *                        kilobytes.
   * @throws InternalErrorException   if an internal error
   *                                  occurs.
   * @throws ProcessNotFoundException if the process cannot be
   *                                  found in after launching.
   */
  public static synchronized ChildProcess launchChildProcess(
    SourceLauncher launcher,
    int timeLimitMillis,
    int memoryLimitKb
  ) throws InternalErrorException, ProcessNotFoundException {
    ChildProcesses.validateActiveChildProcesses();
    Process process = launcher.launch();

    // for java 9 and above: int childProcessPid = process.pid();
    int childProcessPid = ChildProcesses.getNewPid(launcher);

    ChildProcess childProcess = new ChildProcess(
      childProcessPid,
      process,
      timeLimitMillis,
      memoryLimitKb,
      0
    );
    ChildProcesses.activeChildProcesses.put(childProcessPid, childProcess);
    ChildProcesses.validateActiveChildProcesses();
    synchronized (ChildProcesses.childProcessMonitor) {
      ChildProcesses.childProcessMonitor.notify();
    }
    return childProcess;
  }

  /**
   * Checks each of the activate child processes, updates the
   * maximum amount of memory it has used, and terminates the
   * process if it uses more memory than the maximum amount it
   * is allowed to use.
   */
  public static void validateActiveChildProcesses() {
    List<OSProcess> children =
      ChildProcesses.OS.getChildProcesses(ChildProcesses.CURRENT_PID, 0, null);
    for (OSProcess child : children) {
      int pid = child.getProcessID();
      ChildProcess childProcess = ChildProcesses.activeChildProcesses.get(pid);
      if (childProcess != null) {
        long memoryUsedBytes = child.getResidentSetSize();
        childProcess.updateMemoryUsedBytes(memoryUsedBytes);

        // process already terminated
        if (!childProcess.getProcess().isAlive()) {
          ChildProcesses.activeChildProcesses.remove(pid);
        // memory limit exceeded
        } else if (memoryUsedBytes > childProcess.getMemoryLimitKb()*1024) {
          childProcess.getProcess().destroyForcibly();
          ChildProcesses.activeChildProcesses.remove(pid);
        }
      }
    }
  }

  /**
   * Returns the process id of the newly launched child
   * process.
   *
   * @param launcher The {@code SourceLauncher} used to launch
   *                 the process.
   * @return The process id of the newly launched child
   *         process.
   * @throws InternalErrorException   if an internal error
   *                                  occurs.
   * @throws ProcessNotFoundException if the process cannot be
   *                                  found after launching.
   */
  private static int getNewPid(SourceLauncher launcher)
    throws InternalErrorException, ProcessNotFoundException {
    List<OSProcess> updatedProcesses =
      ChildProcesses.OS.getChildProcesses(ChildProcesses.CURRENT_PID, 0, null);

    // filter out the new ones
    String targetProcessName = ChildProcesses.getProcessName(launcher.getLanguage());
    ArrayList<OSProcess> newProcesses = new ArrayList<>();
    for (OSProcess p : updatedProcesses) {
      if ((!ChildProcesses.activeChildProcesses.containsKey(p.getProcessID()))) {
        newProcesses.add(p);
      }
    }

    int count = newProcesses.size();
    if (count == 0) {
      throw new ProcessNotFoundException("Failed to track child pid");
    // if there is only one new pid, assume it is the one we want,
    // since some of the times the system would give unknown for names
    } else if (count == 1) {
      return newProcesses.get(0).getProcessID();
    // if there is more than one, return the first one one that matches the target name
    } else {
      for (OSProcess p : newProcesses) {
        String processName = p.getName();
        // System.out.println(processName + " " + targetProcessName + " " + activeChildProcesses.containsKey(p.getProcessID()));
        if (processName.equals(targetProcessName)) {
          return p.getProcessID();
        }
      }
    }
    throw new ProcessNotFoundException("Failed to track child pid");
  }

  /**
   * Returns the name of the process depending on the
   * programming language it uses.
   *
   * @param language The programming language of the launched
   *                 program.
   * @throws InternalErrorException if an internal error
   *                                occurs.
   */
  private static String getProcessName(Language language) throws InternalErrorException {
    switch (language) {
      case PYTHON:
        return "python";
      case JAVA:
        return "java";
      default:
        throw new InternalErrorException("Language not found: " + language);
    }
  }

  /**
   * A {@code Runnable} object that repeatedly validates
   * active child processes with a fixed interval.
   */
  private static class ChildProcessMonitor implements Runnable {
    /** Whether or not the {@code ChildProcessMonitor} should keep running.  */
    private boolean running;

    /**
     * Creates a new {@code ChildProcessMonitor} instance.
     */
    public ChildProcessMonitor() {
      this.running = true;
    }

    /**
     * Validates active child processes with a fixed interval.
     * If there are no active child processes, makes the thread
     * wait to be notified.
     */
    @Override
    public void run() {
      while (running) {
        try {
          ChildProcesses.validateActiveChildProcesses();
          if (ChildProcesses.activeChildProcesses.size() == 0) {
            synchronized (this) {
              this.wait();
            }
          }
          Thread.sleep(500);

        } catch (InterruptedException e) {
          e.printStackTrace();
          this.stop();
        }
      }
    }

    /**
     * Stops this {@code ChildProcessMonitor} from running.
     */
    public void stop() {
      this.running = false;
    }

  }
}
