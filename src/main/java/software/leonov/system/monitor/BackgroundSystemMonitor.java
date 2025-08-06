package software.leonov.system.monitor;

import static java.util.Objects.requireNonNull;

import java.time.Duration;

/**
 * A thread-safe {@link SystemMonitor} implementation that asynchronously refreshes usage metrics using a dedicated
 * background daemon thread.
 * <p>
 * The monitor can be instantiated using a {@link #withDefaultRefreshInterval() default} or a
 * {@link #refreshEvery(Duration) custom} refresh interval. The background thread must be explicitly started with the
 * {@link #start()} method. To shut down the monitor call {@link #stop()} or {@link #close()}.
 *
 * @author Zhenya Leonov
 */
public final class BackgroundSystemMonitor extends AbstractSystemMonitor {

    private static final Duration DEFAULT_REFRESH_INTERVAL = Duration.ofMillis(250);

    private final long refreshIntervalMillis;

    private final Thread t;

    private volatile boolean started = false;

    BackgroundSystemMonitor() {
        this(DEFAULT_REFRESH_INTERVAL);
    }

    BackgroundSystemMonitor(final Duration refreshInterval) {
        refreshIntervalMillis = refreshInterval.toMillis();

        t = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(refreshIntervalMillis);
                    refreshMetrics();
                }
            } catch (final InterruptedException e) {
                // This is our own thread so we don't need to re-interrupt
            }
        });

        t.setDaemon(true);

        refreshMetrics();
    }

    /**
     * Creates a new {@link BackgroundSystemMonitor} configured with the default refresh interval.
     *
     * @return a new {@link BackgroundSystemMonitor} configured with the default refresh interval
     */
    public static BackgroundSystemMonitor withDefaultRefreshInterval() {
        return new BackgroundSystemMonitor();
    }

    /**
     * Creates a new {@link BackgroundSystemMonitor} configured with the specified refresh interval.
     * <p>
     * The monitor's background thread will refresh all usage metrics after every {@code refreshInterval}.
     *
     * @param refreshInterval the time interval between consecutive metric refreshes
     * @return a new {@link BackgroundSystemMonitor} configured with the specified refresh interval
     */
    public static BackgroundSystemMonitor refreshEvery(final Duration refreshInterval) {
        requireNonNull(refreshInterval, "refreshInterval == null");
        if (refreshInterval.isNegative() || refreshInterval.isZero())
            throw new IllegalArgumentException("refreshInterval <= 0");
        return new BackgroundSystemMonitor(refreshInterval);
    }

    @Override
    public CpuUsage cpuUsage() {
        return started ? super.cpuUsage() : UnsupportedSystemMonitor.getInstance().cpuUsage();
    }

    @Override
    public MemoryUsage memoryUsage() {
        return started ? super.memoryUsage() : UnsupportedSystemMonitor.getInstance().memoryUsage();
    }

    /**
     * Starts this monitor's background thread.
     * 
     * @return this monitor instance
     */
    public BackgroundSystemMonitor start() {
        t.start();
        started = true;
        return this;
    }

    /**
     * Stops this monitor's background thread.
     * <p>
     * This method delegates to {@link #close()}.
     */
    public void stop() {
        close();
    }

    @Override
    public void close() {
        t.interrupt();
    }

}