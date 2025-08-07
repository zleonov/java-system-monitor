package software.leonov.system.monitor;

/**
 * Monitors Java Virtual Machine (JVM) and system-level resource usage such as CPU and memory.
 * <p>
 * Implementations of this interface do not throw exceptions. Instead, methods that return a numeric metric will return
 * -1.0 or -1 if the requested information is unavailable, uninitialized, or are unsupported on the underlying platform.
 * <p>
 * This interface extends {@link AutoCloseable}, but implementations may or may not require a resource to be closed. It
 * is the responsibility of the concrete implementation to document its resource management and the behavior of its
 * {@link #close()} method.
 *
 * @author Zhenya Leonov
 */
public interface SystemMonitor extends AutoCloseable {

    /**
     * Returns the maximum amount of heap memory (in bytes) the JVM will attempt to use or -1 if the information is
     * unavailable. This value typically corresponds to the <i>-Xmx</i> JVM argument.
     *
     * @return the maximum amount of heap memory (in bytes) the JVM will attempt to use or -1 if the information is
     *         unavailable
     */
    public static long getAvailableMemory() {
        return AbstractSystemMonitor.getAvailableMemory();
    }

    /**
     * Returns the number of processors available to the JVM.
     * <p>
     * This call is equivalent to {@link Runtime#getRuntime()}{@link Runtime#availableProcessors() .availableProcessors()}.
     * 
     * @return the number of processors available to the JVM
     */
    public static int getAvailableProcessors() {
        return AbstractSystemMonitor.getAvailableProcessors();
    }

    /**
     * Returns whether or not {@link CpuUsage#getSystemCpuLoad()} and {@link CpuUsage#getAverageSystemCpuLoad()} are
     * supported.
     * 
     * @return whether or not {@link CpuUsage#getSystemCpuLoad()} and {@link CpuUsage#getAverageSystemCpuLoad()} are
     *         supported
     */
    public static boolean isSystemCpuUsageSupported() {
        return AbstractSystemMonitor.isSystemCpuUsageSupported();
    }

    /**
     * Returns the Java Virtual Machine (JVM) name.
     * 
     * @return the Java Virtual Machine (JVM) name
     */
    public static String getJVMName() {
        return AbstractSystemMonitor.getJVMName();
    }

    /**
     * Returns the Java Virtual Machine (JVM) vendor.
     * 
     * @return the Java Virtual Machine (JVM) vendor
     */
    public static String getJVMVendor() {
        return AbstractSystemMonitor.getJVMVendor();
    }

    /**
     * Returns the Java version supported by the Java Virtual Machine (JVM).
     * 
     * @return the Java version supported by the Java Virtual Machine (JVM)
     */
    public static String getSupportedJavaVersion() {
        return AbstractSystemMonitor.getSupportedJavaVersion();
    }

    /**
     * Returns the Central Processing Unit (CPU) usage metrics.
     * 
     * @return the Central Processing Unit (CPU) usage metrics
     */
    public CpuUsage getCpuUsage();

    /**
     * Returns the physical memory (RAM) usage metrics.
     * 
     * @return the physical memory (RAM) usage metrics
     */
    public MemoryUsage getMemoryUsage();

}
