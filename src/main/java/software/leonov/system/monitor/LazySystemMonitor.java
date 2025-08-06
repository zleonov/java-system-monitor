package software.leonov.system.monitor;

import static java.util.Objects.requireNonNull;

import java.time.Duration;

/**
 * A {@link SystemMonitor} that refreshes usage metrics on demand.
 * <p>
 * This class is created with a {@link #withDefaultRefreshThreshold() default} or a
 * {@link #withRefreshThreshold(Duration) specified} refresh threshold. Usage metrics are updated when either
 * {@link #cpuUsage()} or {@link #memoryUsage()} is called, unless refresh threshold time has not elapsed since the last
 * update, in which case the most recently cached values are returned.
 * <p>
 * This class does not make use of any underlying closeable resources; it's {@link #close()} method is a no-op.
 * 
 * @author Zhenya Leonov
 */

/**
 * A thread-safe {@link SystemMonitor} implementation that refreshes usage metrics on demand.
 * <p>
 * This monitor employs a debouncing mechanism to prevent excessive resource consumption. Usage metrics are updated when
 * either {@link #cpuUsage()} or {@link #memoryUsage()} is called, but only if a refresh threshold has elapsed since the
 * last update. A {@link #withRefreshThreshold(Duration) custom} threshold can be specified when this class is created.
 * <p>
 * No underlying resources are managed by this monitor. The {@link #close()} method is a no-op and can be safely
 * ignored.
 *
 * @author Zhenya Leonov
 */
public final class LazySystemMonitor extends AbstractSystemMonitor {

    private static final Duration DEFAULT_REFRESH_THRESHOLD = Duration.ofMillis(250);

    private final long    refreshThresholdMillis;
    private volatile long lastRefreshTimeMillis = -1;

    LazySystemMonitor() {
        this(DEFAULT_REFRESH_THRESHOLD);
    }

    LazySystemMonitor(final Duration refreshThreshold) {
        this.refreshThresholdMillis = refreshThreshold.toMillis();
    }

    /**
     * Creates a new {@link LazySystemMonitor} configured with the default refresh threshold.
     * 
     * @return a new {@link LazySystemMonitor} configured with the default refresh threshold
     */
    public static LazySystemMonitor withDefaultRefreshThreshold() {
        return new LazySystemMonitor();
    }

    /**
     * Creates a new {@link LazySystemMonitor} configured with the specified refresh threshold.
     * <p>
     * Usage metrics are updated when either {@link #cpuUsage()} or {@link #memoryUsage()} is called, unless the specified
     * {@code refreshThreshold} time has not elapsed since the last update, in which case the most recently cached values
     * are returned.
     *
     * @param refreshThreshold the minimum time interval that must elapse between updates
     * @return a new {@link LazySystemMonitor} configured with the specified refresh threshold
     */
    public static LazySystemMonitor withRefreshThreshold(final Duration refreshThreshold) {
        requireNonNull(refreshThreshold, "refreshThreshold == null");
        if (refreshThreshold.isNegative() || refreshThreshold.isZero())
            throw new IllegalArgumentException("refreshThreshold <= 0");
        return new LazySystemMonitor(refreshThreshold);
    }

    @Override
    public CpuUsage cpuUsage() {
        refreshMetrics();
        return super.cpuUsage();
    }

    @Override
    public MemoryUsage memoryUsage() {
        refreshMetrics();
        return super.memoryUsage();
    }

    @Override
    protected synchronized void refreshMetrics() {
        final long currentTimeMillis = System.currentTimeMillis();

        if (lastRefreshTimeMillis == -1 || (currentTimeMillis - lastRefreshTimeMillis) >= refreshThresholdMillis) {
            super.refreshMetrics();
            lastRefreshTimeMillis = currentTimeMillis;
        }
    }

    @Override
    public void close() throws Exception {
        // nothing to do
    }

}