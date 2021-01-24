package judge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import entities.Language;
import judge.launcher.SourceLauncher;

/**
 * [description]
 * <p>
 * Created on 2021.01.17.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ChildProcesses {
  private static final OperatingSystem OS = new SystemInfo().getOperatingSystem();
  private static final int CURRENT_PID = ChildProcesses.OS.getProcessId();

  private static final ChildProcessMonitor childProcessMonitor = new ChildProcessMonitor();
  private static final ConcurrentHashMap<Integer, ChildProcess> activeChildProcesses
    = new ConcurrentHashMap<>();


  private ChildProcesses() {
  }

  public static void initialize() {
    Thread processMonitorThread = new Thread(ChildProcesses.childProcessMonitor);
    processMonitorThread.start();
  }

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

  private static class ChildProcessMonitor implements Runnable {
    private boolean running;

    public ChildProcessMonitor() {
      this.running = true;
    }

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

    public void stop() {
      this.running = false;
    }

  }
}
