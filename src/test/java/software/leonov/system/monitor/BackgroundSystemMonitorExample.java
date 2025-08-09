package software.leonov.system.monitor;

import static java.lang.System.out;

import java.time.Duration;

public class BackgroundSystemMonitorExample {

    public static void main(String... strings) throws InterruptedException {
        
        try (final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.refreshEvery(Duration.ofSeconds(1))) {
            
            monitor.start(); // Don't forget to start the monitor
            
            CpuUsage cpu = monitor.getCpuUsage();

            if (cpu.getSystemCpuLoad() < 0 && SystemMonitor.isSystemCpuUsageSupported())
                System.out.println("System CPU load not ready"); // try again in a few
            else if (cpu.getSystemCpuLoad() < 0)
                System.out.println("System CPU load is not available on this platform");
            else
                System.out.println("System CPU is: " + cpu.getProcessCpuLoad() + "%");
            
            
        }
    }

}
