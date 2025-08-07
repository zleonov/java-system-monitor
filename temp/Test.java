package software.leonov.system.monitor;

import static software.leonov.system.monitor.util.Formatter.formatDecimalBytes;
import static software.leonov.system.monitor.util.Formatter.formatPercent;

class Test {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Available Processors: " + SystemMonitor.getAvailableProcessors());
        System.out.println("Available Memory: " + formatDecimalBytes(SystemMonitor.getAvailableMemory()));
        System.out.println("Supports system-wide CPU usage metrics: " + SystemMonitor.isSystemCpuUsageSupported());

        System.out.println("JVM name: " + SystemMonitor.getJVMName());
        System.out.println("JVM vendor: " + SystemMonitor.getJVMVendor());
        System.out.println("Supported Java version: " + SystemMonitor.getSupportedJavaVersion());
        System.out.println();

        testMonitor(new LazySystemMonitor());

        System.out.println();

        try (final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultRefreshInterval().start()) {
            testMonitor(monitor);
        }

    }

    public static void testMonitor(final SystemMonitor monitor) throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            System.out.println(toString(monitor));
            System.out.println();

            // Do some work to generate CPU usage
            doWork();

            System.gc();
        }
    }

    private static void doWork() throws InterruptedException {
        // Generate some CPU load
        double result = 0;
        for (int i = 0; i < 100000; i++) {
            result += Math.sqrt(i) * Math.sin(i);
            Double.toString(result);
        }
        Thread.sleep(1000);
    }

    public static String toString(final SystemMonitor monitor) {
        // @formatter:off
        return monitor.getClass().getSimpleName()         + "\n    "
            + "Process CPU Load"      + ": " + formatPercent(monitor.getCpuUsage().getProcessCpuLoad())         + "\n    "
            + "System CPU Load"       + ": " + formatPercent(monitor.getCpuUsage().getSystemCpuLoad())          + "\n    "
            + "System Load Average"   + ": " + formatPercent(monitor.getCpuUsage().getSystemLoadAverage())      + "\n    "                 
            + "Avg. Process CPU Load" + ": " + formatPercent(monitor.getCpuUsage().getAverageProcessCpuLoad())  + "\n    "
            + "Avg. System CPU Load"  + ": " + formatPercent(monitor.getCpuUsage().getAverageSystemCpuLoad())   + "\n    "
            + "Total Memory"          + ": " + formatDecimalBytes(monitor.getMemoryUsage().getTotalMemory())       + "\n    "            
            + "Used Memory"           + ": " + formatDecimalBytes(monitor.getMemoryUsage().getUsedMemory())        + "\n    "                    
            + "Max. Used Memory"      + ": " + formatDecimalBytes(monitor.getMemoryUsage().getMaxUsedMemory())
            ;
        // @formatter:on
    }
}