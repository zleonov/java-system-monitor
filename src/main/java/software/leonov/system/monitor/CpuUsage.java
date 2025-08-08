package software.leonov.system.monitor;

import java.lang.management.OperatingSystemMXBean;

/**
 * The Central Processing Unit (CPU) usage metrics.
 * 
 * @author Zhenya Leonov
 */
public interface CpuUsage {

    /**
     * Returns the JVM process CPU usage or -1.0 if it is not supported.
     * 
     * @return the JVM process CPU usage or -1.0 if it is not supported
     */
    public double getProcessCpuLoad();

    /**
     * Returns the system-wide CPU usage or -1.0 if it is not supported.
     * 
     * @return the system-wide CPU usage or -1.0 if it is not supported
     */
    public double getSystemCpuLoad();

    /**
     * Returns the system load average for the last minute (only supported on Unix/Linux systems) or -1.0 if it is not
     * supported.
     * <p>
     * For more details see {@link OperatingSystemMXBean#getSystemLoadAverage()}.
     * 
     * @return the system load average for the last minute or -1.0 if it is not supported
     */
    public double getSystemLoadAverage();

    /**
     * Returns the average JVM process CPU usage or -1.0 if it is not supported or not ready.
     * 
     * @return the average JVM process CPU usage or -1.0 if it is not supported or not ready
     */
    public double getAverageProcessCpuLoad();

    /**
     * Returns average system-wide CPU usage or -1.0 if it is not supported or not ready.
     * 
     * @return average system-wide CPU usage or -1.0 if it is not supported or not ready
     */
    public double getAverageSystemCpuLoad();

    /**
     * Returns the maximum observed JVM process CPU usage (0.0 to 100.0) or -1.0 if it is not supported or not ready.
     *
     * @return the maximum observed JVM process CPU usage (0.0 to 100.0) or -1.0 if it is not supported or not ready
     */
    public double getMaxProcessCpuLoad();

    /**
     * Returns the maximum observed system-wide CPU usage (0.0 to 100.0) or -1.0 if it is not supported or not ready.
     *
     * @return the maximum observed system-wide CPU usage (0.0 to 100.0) or -1.0 if it is not supported or not ready
     */
    public double getMaxSystemCpuLoad();

}
