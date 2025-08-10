package software.leonov.system.monitor;

import static java.util.Objects.requireNonNull;

import java.time.Duration;

/**
 * A thread-safe {@link SystemMonitor} implementation that updates usage metrics on demand.
 * <p>
 * This monitor employs a debouncing mechanism to prevent excessive resource consumption. Usage metrics are updated when
 * either {@link #getCpuUsage()} or {@link #getMemoryUsage()} is called, but only if a update threshold has elapsed
 * since the last update. A {@link #withUpdateThreshold(Duration) custom} threshold can be specified when this class is
 * created.
 * <p>
 * No underlying resources are managed by this monitor. The {@link #close()} method is a no-op and can be safely
 * ignored.
 *
 * @author Zhenya Leonov
 */
public final class LazySystemMonitor extends AbstractSystemMonitor {

    private static final Duration DEFAULT_UPDATE_THRESHOLD = Duration.ofMillis(250);

    private final long updateThresholdMillis;
    private long       lastUpdateTimeMillis = -1;

    LazySystemMonitor() {
        this(DEFAULT_UPDATE_THRESHOLD);
    }

    LazySystemMonitor(final Duration updateThreshold) {
        this.updateThresholdMillis = updateThreshold.toMillis();
    }

    /**
     * Creates a new {@link LazySystemMonitor} configured with the default update threshold.
     * 
     * @return a new {@link LazySystemMonitor} configured with the default update threshold
     */
    public static LazySystemMonitor withDefaultUpdateThreshold() {
        return new LazySystemMonitor();
    }

    /**
     * Creates a new {@link LazySystemMonitor} configured with the specified update threshold.
     * <p>
     * Usage metrics are updated when either {@link #getCpuUsage()} or {@link #getMemoryUsage()} is called, unless the
     * specified {@code updateThreshold} time has not elapsed since the last update, in which case the most recently cached
     * values are returned.
     *
     * @param updateThreshold the minimum time interval that must elapse between updates
     * @return a new {@link LazySystemMonitor} configured with the specified update threshold
     */
    public static LazySystemMonitor withUpdateThreshold(final Duration updateThreshold) {
        requireNonNull(updateThreshold, "updateThreshold == null");
        if (updateThreshold.isNegative() || updateThreshold.isZero())
            throw new IllegalArgumentException("updateThreshold <= 0");
        return new LazySystemMonitor(updateThreshold);
    }

    @Override
    public CpuUsage getCpuUsage() {
        updateMetrics();
        return super.getCpuUsage();
    }

    @Override
    public MemoryUsage getMemoryUsage() {
        updateMetrics();
        return super.getMemoryUsage();
    }

    @Override
    protected synchronized void updateMetrics() {
        final long currentTimeMillis = System.currentTimeMillis();

        if (lastUpdateTimeMillis == -1 || (currentTimeMillis - lastUpdateTimeMillis) >= updateThresholdMillis) {
            super.updateMetrics();
            lastUpdateTimeMillis = currentTimeMillis;
        }
    }

}