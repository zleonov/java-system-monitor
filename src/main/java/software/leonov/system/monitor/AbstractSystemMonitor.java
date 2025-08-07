package software.leonov.system.monitor;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

@SuppressWarnings("restriction")
abstract class AbstractSystemMonitor implements SystemMonitor {

    private static final OperatingSystemMXBean                    OS_BEAN = ManagementFactory.getOperatingSystemMXBean();
    private static final com.sun.management.OperatingSystemMXBean SUN_OS_BEAN;

    private static final ThreadMXBean  THREAD_BEAN  = ManagementFactory.getThreadMXBean();
    private static final MemoryMXBean  MEMORY_BEAN  = ManagementFactory.getMemoryMXBean();
    private static final RuntimeMXBean RUNTIME_BEAN = ManagementFactory.getRuntimeMXBean();

    static {
        if (OS_BEAN instanceof com.sun.management.OperatingSystemMXBean)
            SUN_OS_BEAN = (com.sun.management.OperatingSystemMXBean) OS_BEAN;
        else
            SUN_OS_BEAN = null;
    }

    // State for tracking current CPU metrics
    private double processCpu;
    private double systemCpu;
    private double systemLoadAverage;

    // State for tracking current memory metrics
    private long usedMemory;
    private long totalMemory;

    // State for tracking CPU averages
    private double avgProcessCpuLoad;
    private double avgSystemCpuLoad;

    // State for tracking CPU maximums
    private double maxProcessCpu = -1.0;
    private double maxSystemCpu  = -1.0;

    // State for tracking memory maximums
    private long maxUsedMemory = -1;

    // State for current CPU calculations using ThreadMxBean
    private long lastCpuTime = -1;
    private long lastTime    = -1;

    // State for average JVM process CPU calculations (time-weighted)
    private long   processCpuStartTime     = -1;
    private double totalWeightedProcessCpu = 0.0;
    private long   lastProcessCpuTime      = -1;
    private double lastProcessCpuReading   = 0.0;

    // State for tracking average system-wide CPU calculations (time-weighted)
    private long   systemCpuStartTime     = -1;
    private double totalWeightedSystemCpu = 0.0;
    private long   lastSystemCpuTime      = -1;
    private double lastSystemCpuReading   = 0.0;

    private volatile CpuUsage    cpu;
    private volatile MemoryUsage memory;

    static long getAvailableMemory() {
        return MEMORY_BEAN.getHeapMemoryUsage().getMax();
    }

    static int getAvailableProcessors() {
        return OS_BEAN.getAvailableProcessors();
    }

    static boolean isSystemCpuUsageSupported() {
        return SUN_OS_BEAN != null;
    }

    static String getJVMName() {
        return RUNTIME_BEAN.getVmName();
    }

    static String getJVMVendor() {
        return RUNTIME_BEAN.getVmVendor();
    }

    static String getSupportedJavaVersion() {
        return RUNTIME_BEAN.getSpecVersion();
    }

    @Override
    public CpuUsage getCpuUsage() {
        return cpu;
    }

    @Override
    public MemoryUsage getMemoryUsage() {
        return memory;
    }

    private double calculateProcessCpuUsage() {
        final double cpuUsage = SUN_OS_BEAN == null ? calculateProcessCpuUsageManually() : SUN_OS_BEAN.getProcessCpuLoad();
        return cpuUsage < 0 ? -1.0 : Math.min(cpuUsage * 100.0, 100.0);
    }

    private double calculateProcessCpuUsageManually() {
        final long currentTime    = System.nanoTime();
        final long currentCpuTime = getTotalThreadCpuTime();

        if (currentCpuTime < 0)
            return -1.0;

        if (lastTime == -1) {
            lastTime    = currentTime;
            lastCpuTime = currentCpuTime;
            return -1.0;
        }

        final long timeDiff    = currentTime - lastTime;
        final long cpuTimeDiff = currentCpuTime - lastCpuTime;

        lastTime    = currentTime;
        lastCpuTime = currentCpuTime;

        // Handle edge cases
        if (timeDiff <= 0 || cpuTimeDiff < 0)
            return 0.0;

        return cpuTimeDiff / timeDiff;
    }

    private double calculateSystemCpuUsage() {
        if (SUN_OS_BEAN == null)
            return -1.0;

        double cpuUsage = SUN_OS_BEAN.getSystemCpuLoad();
        return cpuUsage < 0 ? -1.0 : cpuUsage * 100.0;
    }

    private double calculateAverageProcessCpuLoad() {
        if (processCpu < 0)
            return -1.0;

        final long currentTime = System.nanoTime();

        if (processCpuStartTime == -1) {
            processCpuStartTime   = currentTime;
            lastProcessCpuTime    = currentTime;
            lastProcessCpuReading = processCpu;
            return processCpu;
        }

        final long timeDiff = currentTime - lastProcessCpuTime;
        totalWeightedProcessCpu += lastProcessCpuReading * timeDiff;

        final long   totalTime = currentTime - processCpuStartTime;
        final double average   = totalTime > 0 ? (totalWeightedProcessCpu + processCpu * timeDiff) / (totalTime + timeDiff) : processCpu;

        lastProcessCpuTime    = currentTime;
        lastProcessCpuReading = processCpu;

        return average;
    }

    private double calculateAverageSystemCpuLoad() {
        if (systemCpu < 0)
            return -1.0;

        final long currentTime = System.nanoTime();

        if (systemCpuStartTime == -1) {
            systemCpuStartTime   = currentTime;
            lastSystemCpuTime    = currentTime;
            lastSystemCpuReading = systemCpu;
            return systemCpu;
        }

        final long timeDiff = currentTime - lastSystemCpuTime;
        totalWeightedSystemCpu += lastSystemCpuReading * timeDiff;

        final long   totalTime = currentTime - systemCpuStartTime;
        final double average   = totalTime > 0 ? (totalWeightedSystemCpu + systemCpu * timeDiff) / (totalTime + timeDiff) : systemCpu;

        lastSystemCpuTime    = currentTime;
        lastSystemCpuReading = systemCpu;

        return average;
    }

    private static long getTotalThreadCpuTime() {
        long totalCpuTime = 0;

        for (final long threadId : THREAD_BEAN.getAllThreadIds())
            try {
                final long threadCpuTime = THREAD_BEAN.getThreadCpuTime(threadId);
                if (threadCpuTime > 0)
                    totalCpuTime += threadCpuTime;
            } catch (final UnsupportedOperationException e) {
                return -1;
            }

        return totalCpuTime > 0 ? totalCpuTime : -1;
    }

    protected synchronized void refreshMetrics() {
        // Update total memory
        totalMemory = MEMORY_BEAN.getHeapMemoryUsage().getCommitted();

        // Update used memory
        usedMemory    = MEMORY_BEAN.getHeapMemoryUsage().getUsed();
        maxUsedMemory = Math.max(usedMemory, maxUsedMemory);

        // Update process CPU metrics
        processCpu    = calculateProcessCpuUsage();
        maxProcessCpu = Math.max(processCpu, maxProcessCpu);

        // Update system CPU metrics
        systemCpu    = calculateSystemCpuUsage();
        maxSystemCpu = Math.max(systemCpu, maxSystemCpu);

        // Update system load average
        systemLoadAverage = OS_BEAN.getSystemLoadAverage();

        // Update average cpu metrics
        avgProcessCpuLoad = calculateAverageProcessCpuLoad();
        avgSystemCpuLoad  = calculateAverageSystemCpuLoad();

        cpu    = new CpuUsageImpl(processCpu, systemCpu, systemLoadAverage, avgProcessCpuLoad, avgSystemCpuLoad, maxProcessCpu, maxSystemCpu);
        memory = new MemoryUsageImpl(usedMemory, totalMemory, maxUsedMemory);
    }

}
