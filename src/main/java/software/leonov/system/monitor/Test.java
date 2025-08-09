package software.leonov.system.monitor;

import static software.leonov.system.monitor.util.Formatter.formatDecimalBytes;
import static software.leonov.system.monitor.util.Formatter.formatPercent;

import java.time.Duration;

import software.leonov.system.monitor.util.Formatter;

public class Test {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Available processors: " + SystemMonitor.getAvailableProcessors());

        final String availableMemory = formatDecimalBytes(SystemMonitor.getAvailableMemory());

        System.out.println("Available memory: " + availableMemory);

        try (final SystemMonitor monitor = BackgroundSystemMonitor.refreshEvery(Duration.ofSeconds(1))
                                                                  .start()) { // Don't forget to start() the monitor

            CpuUsage    cpu    = monitor.getCpuUsage();
            MemoryUsage memory = monitor.getMemoryUsage();

            System.out.println("Current CPU load: " + formatPercent(cpu.getSystemCpuLoad()));
            System.out.println("Currently using memory: " + formatDecimalBytes(memory.getUsedMemory()) + " out of " + availableMemory);

            Thread.sleep(2000);
            
            // do work

            cpu    = monitor.getCpuUsage();
            memory = monitor.getMemoryUsage();

            System.out.println("Average CPU load: " + formatPercent(cpu.getAverageSystemCpuLoad()));
            System.out.println("Maximum used memory: " + formatDecimalBytes(memory.getMaxUsedMemory()));
            
        }

    }

}
