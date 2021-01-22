package judge;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.OsProcess;
import com.jezhumble.javasysmon.ProcessInfo;

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
  private static final int UPDATE_INTERVAL_MILLIS = 1000*1;
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
    SYSTEM_MONITOR.killProcessTree(CURRENT_PID, true); 
  }

  @SuppressWarnings("unchecked")
  public static synchronized ChildProcess launchChildProcess(
    SourceLauncher launcher,
    int timeLimitMillis,
    int memoryLimitKb
  ) throws InternalErrorException {
    validateActiveChildProcesses();
    Process process = launcher.launch();
    List<OsProcess> updatedProcesses = SYSTEM_MONITOR.processTree().find(CURRENT_PID).children();

    LinkedHashSet<Integer> updatedProcessPids = new LinkedHashSet<Integer>();
    for (OsProcess p : updatedProcesses) {
      updatedProcessPids.add(p.processInfo().getPid());
    }
    int childProcessPid = ChildProcesses.getNewPid(updatedProcessPids);
    ChildProcess childProcess = new ChildProcess(
      childProcessPid,
      process,
      timeLimitMillis,
      memoryLimitKb,
      0,
      0
    );
    activeChildProcesses.put(childProcessPid, childProcess);
    validateActiveChildProcesses();
    return childProcess;    
  }

  private static int getNewPid(LinkedHashSet<Integer> updatedPids)
    throws InternalErrorException {
    int newPid = -1;
    int count = 0;
    for (int pid : updatedPids) {
      if (!activeChildProcesses.containsKey(pid)) {
        newPid = pid;
        count++;
        if (count > 1) {
          throw new InternalErrorException("Failed to track child pid");
        }
      }
    }
    if (count == 0) {
      throw new InternalErrorException("Failed to track child pid");
    }
    return newPid;
  }

  public static void validateActiveChildProcesses() {
    OsProcess parentProcessTree = SYSTEM_MONITOR.processTree();
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
        //   "pid: %d, memory usage: %.2f\n", childInfo.getPid(), childProcess.getMemoryUsedKb()
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

  private static class ChildProcessMonitor implements Runnable {
    private boolean running;

    public ChildProcessMonitor() {
      this.running = true;
    }

    @Override
    public void run() {
      while (running) {
        if (activeChildProcesses.size() > 0) {
          ChildProcesses.validateActiveChildProcesses();
        }
        try {
          Thread.sleep(UPDATE_INTERVAL_MILLIS);
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
