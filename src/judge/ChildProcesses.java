package judge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.OsProcess;
import com.jezhumble.javasysmon.ProcessInfo;

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
  private static final JavaSysMon SYSTEM_MONITOR = new JavaSysMon();
  private static final int CURRENT_PID = SYSTEM_MONITOR.currentPid();

  private static ChildProcessMonitor childProcessMonitor = new ChildProcessMonitor();
  private static ConcurrentHashMap<Integer, ChildProcess> activeChildProcesses
    = new ConcurrentHashMap<>();
  

  private ChildProcesses() {
  }

  public static void initialize() {
    Thread processMonitorThread = new Thread(childProcessMonitor);
    processMonitorThread.start();
  }

  public static void shutdown() {
    childProcessMonitor.stop();
    SYSTEM_MONITOR.killProcessTree(ChildProcesses.CURRENT_PID, true); 
  }

  
  public static synchronized ChildProcess launchChildProcess(
    SourceLauncher launcher,
    int timeLimitMillis,
    int memoryLimitKb
  ) throws InternalErrorException, ProcessNotFoundException {
    validateActiveChildProcesses();
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
    activeChildProcesses.put(childProcessPid, childProcess);
    validateActiveChildProcesses();
    synchronized (ChildProcesses.childProcessMonitor) {
      ChildProcesses.childProcessMonitor.notify();
    }
    return childProcess;
  }

  public static void validateActiveChildProcesses() {
    OsProcess parentProcessTree = SYSTEM_MONITOR.processTree().find(ChildProcesses.CURRENT_PID);
    Iterator<Map.Entry<Integer, ChildProcess>> i = activeChildProcesses.entrySet().iterator();
    while (i.hasNext()) {
      Map.Entry<Integer, ChildProcess> entry = i.next();
      int pid = entry.getKey();
      ChildProcess childProcess = entry.getValue();

      OsProcess childProcessTree = parentProcessTree.find(pid);
      if (childProcessTree == null) {
        i.remove();

      } else {
        ProcessInfo childInfo = childProcessTree.processInfo();
        long memoryUsedBytes = childInfo.getResidentBytes();
        childProcess.updateMemoryUsedBytes(memoryUsedBytes);
        // System.out.printf(
        //   "pid: %d, name: %s, memory usage: %d bytes\n", childInfo.getPid(), childInfo.getName(), childProcess.getMemoryUsedBytes()
        // );
        // process already terminated
        if (!childProcess.getProcess().isAlive()) {
          i.remove();
        // memory limit exceeded
        } else if (memoryUsedBytes > childProcess.getMemoryLimitKb()*1024) {
          childProcess.getProcess().destroyForcibly();
          i.remove();
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static int getNewPid(SourceLauncher launcher)
    throws InternalErrorException, ProcessNotFoundException {
    OsProcess parentProcessTree = SYSTEM_MONITOR.processTree().find(ChildProcesses.CURRENT_PID);
    // get current process ids
    List<OsProcess> updatedProcesses = parentProcessTree.children();
    int[] updatedPids = new int[updatedProcesses.size()];
    for (int i = 0; i < updatedProcesses.size(); i++) {
      OsProcess p = updatedProcesses.get(i);
      updatedPids[i] = p.processInfo().getPid();
    }
    // filter out the new ones
    String targetProcessName = ChildProcesses.getProcessName(launcher.getLanguage());
    ArrayList<Integer> pids = new ArrayList<Integer>();
    for (int pid : updatedPids) {
      if ((!activeChildProcesses.containsKey(pid))) {
        pids.add(pid);
      }
    }
    int count = pids.size();
    if (count == 0) {
      throw new ProcessNotFoundException("Failed to track child pid");
    // if there is only one new pid, assume it is the one we want,
    // since some of the times the system would give unknown for names
    } else if (count == 1) {
      return pids.get(0);
    // if there is more than one, return the first one one that matches the target name
    } else {
      for (int curPid : pids) {
        String processName = parentProcessTree.find(curPid).processInfo().getName();
        System.out.println(processName + " " + targetProcessName + " " + activeChildProcesses.containsKey(curPid));
        if (processName.equals(targetProcessName)) {
          return curPid;
        }
      }
    }
    throw new ProcessNotFoundException("Failed to track child pid");
  }

  private static String getProcessName(Language language) throws InternalErrorException {
    switch (language) {
      case PYTHON:
        return "python.exe";
      case JAVA:
        return "java.exe";
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
          if (activeChildProcesses.size() == 0) {
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
