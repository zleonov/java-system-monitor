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
                // Thread is terminating, no need to restore interrupt status because it's "our" thread
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
    public CpuUsage getCpuUsage() {
        return t.isAlive() ? super.getCpuUsage() : UnsupportedSystemMonitor.getInstance().getCpuUsage();
    }

    @Override
    public MemoryUsage getMemoryUsage() {
        return t.isAlive() ? super.getMemoryUsage() : UnsupportedSystemMonitor.getInstance().getMemoryUsage();
    }

    /**
     * Starts this monitor's background thread.
     * 
     * @return this monitor instance
     */
    public BackgroundSystemMonitor start() {
        t.start();
        return this;
    }

    /**
     * Stops this monitor's background thread and waits for it to finish running.
     * <p>
     * This method delegates to {@link #close()}.
     */
    public void stop() {
        close();
    }

    /**
     * Stops this monitor's background thread and waits for it to finish running.
     */
    @Override
    public void close() {
        t.interrupt();

        try {
            t.join();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}