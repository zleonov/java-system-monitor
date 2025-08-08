package software.leonov.system.monitor;

/**
 * A {@link SystemMonitor} that does nothing. All metrics return -1.
 */
public final class UnsupportedSystemMonitor implements SystemMonitor {

    private static UnsupportedSystemMonitor INSTANCE = new UnsupportedSystemMonitor();

    final static CpuUsage    NEGATIVE_CPU_USAGE    = new CpuUsageImpl(-1, -1, -1, -1, -1, -1, -1);
    final static MemoryUsage NEGATIVE_MEMORY_USAGE = new MemoryUsageImpl(-1, -1, -1);

    /**
     * Returns a singleton instance of {@link UnsupportedSystemMonitor}.
     * 
     * @return a singleton instance of {@link UnsupportedSystemMonitor}
     */
    public static UnsupportedSystemMonitor getInstance() {
        return INSTANCE;
    }

    private UnsupportedSystemMonitor() {
    }

    @Override
    public CpuUsage getCpuUsage() {
        return NEGATIVE_CPU_USAGE;
    }

    @Override
    public MemoryUsage getMemoryUsage() {
        return NEGATIVE_MEMORY_USAGE;
    }

}
