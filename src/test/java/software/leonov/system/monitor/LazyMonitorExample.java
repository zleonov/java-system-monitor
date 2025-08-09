package software.leonov.system.monitor;

import static java.lang.System.out;

import java.time.Duration;

public class LazyMonitorExample {

    public static void main(String... strings) throws InterruptedException {
        try (final SystemMonitor monitor = LazySystemMonitor.withRefreshThreshold(Duration.ofSeconds(1))) {
            
            Thread.sleep(1000);
            
            // Get current metrics (will refresh on first call or if threshold elapsed)
            final CpuUsage cpu = monitor.getCpuUsage();
            final MemoryUsage memory = monitor.getMemoryUsage();

            out.printf("Process CPU: %.2f%%%n",   cpu.getProcessCpuLoad());
            out.printf("System CPU: %.2f%%%n",    cpu.getSystemCpuLoad());
            out.printf("Used memory: %d bytes%n", memory.getUsedMemory());
        }
    }

}
