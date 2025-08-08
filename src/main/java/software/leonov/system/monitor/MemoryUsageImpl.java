package software.leonov.system.monitor;

import static software.leonov.system.monitor.util.Formatter.formatDecimalBytes;

/**
 * An implementation of the {@link MemoryUsage} interface.
 * 
 * @author Zhenya Leonov
 */
final class MemoryUsageImpl implements MemoryUsage {

    private final long usedMemory;
    private final long totalMemory;
    private final long maxUsedMemory;

    MemoryUsageImpl(final long usedMemory, final long totalMemory, final long maxUsedMemory) {
        this.usedMemory    = usedMemory;
        this.totalMemory   = totalMemory;
        this.maxUsedMemory = maxUsedMemory;
    }

    @Override
    public long getUsedMemory() {
        return usedMemory;
    }

    @Override
    public long getTotalMemory() {
        return totalMemory;
    }

    @Override
    public long getMaxUsedMemory() {
        return maxUsedMemory;
    }

    @Override
    public String toString() {
        return String.format("[usedMemory=%s, totalMemory=%s, maxUsedMemory=%s]", formatDecimalBytes(usedMemory), formatDecimalBytes(totalMemory), formatDecimalBytes(maxUsedMemory));
    }

}
