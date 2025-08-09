package software.leonov.system.monitor;

public class GetSystemInfo {

    public static void main(String... strings) {
        System.out.println("Available processors: "              + SystemMonitor.getAvailableProcessors());
        System.out.println("Available memory: "                  + SystemMonitor.getAvailableMemory());
        System.out.println("OS name: "                           + SystemMonitor.getOperatingSystemName());
        System.out.println("OS version: "                        + SystemMonitor.getOperatingSystemVersion());
        System.out.println("JVM name: "                          + SystemMonitor.getJVMName());
        System.out.println("JVM vendor: "                        + SystemMonitor.getJVMVendor());
        System.out.println("Supported Java version: "            + SystemMonitor.getSupportedJavaVersion());
        System.out.println("Supported system-wide CPU metrics: " + SystemMonitor.isSystemCpuUsageSupported());
    }

}
