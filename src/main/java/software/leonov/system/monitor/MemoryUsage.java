package software.leonov.system.monitor;

/**
 * The system physical memory (RAM) usage metrics.
 * 
 * @author Zhenya Leonov
 */
public interface MemoryUsage {

    /**
     * Returns the amount of heap memory (in bytes) currently used by the JVM or -1 if the information is unavailable.
     *
     * @return the amount of heap memory (in bytes) currently used by the JVM or -1 if the information is unavailable
     */
    public long getUsedMemory();

    /**
     * Returns the amount of heap memory (in bytes) that is currently committed for the JVM's use or -1 if the information
     * is unavailable. This is the amount of memory guaranteed to be available for the heap, which may be less than the
     * maximum but more than the currently used memory.
     *
     * @return the amount of heap memory (in bytes) that is currently committed for the JVM's use or -1 if the information
     *         is unavailable
     */
    public long getTotalMemory();

    /**
     * Returns the maximum observed amount of heap memory (in bytes) that has been used by the JVM or -1 if the information
     * is unavailable.
     *
     * @return the maximum observed amount of heap memory (in bytes) that has been used by the JVM or -1 if the information
     *         is unavailable
     */
    public long getMaxUsedMemory();

}
