package software.leonov.system.monitor;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.function.BiConsumer;

/**
 * A thread-safe {@link SystemMonitor} implementation that asynchronously updates usage metrics using a dedicated
 * background daemon thread.
 * <p>
 * The monitor can be instantiated using a {@link #withDefaultUpdateInterval() default} or a
 * {@link #updateEvery(Duration) custom} update interval. The background thread must be explicitly started with the
 * {@link #start()} method. To shut down the monitor call {@link #stop()} or {@link #close()}.
 *
 * @author Zhenya Leonov
 */
public final class BackgroundSystemMonitor extends AbstractSystemMonitor {

    private static final Duration DEFAULT_UPDATE_INTERVAL = Duration.ofSeconds(1);

    private final long updateIntervalMillis;

    private final Thread t;

    private volatile BiConsumer<CpuUsage, MemoryUsage> listener = null;

    BackgroundSystemMonitor() {
        this(DEFAULT_UPDATE_INTERVAL);
    }

    BackgroundSystemMonitor(final Duration updateInterval) {
        updateIntervalMillis = updateInterval.toMillis();

        t = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(updateIntervalMillis);
                    updateMetrics();
                }
            } catch (final InterruptedException e) {
                // Thread is terminating, no need to restore interrupt status because it's "our" thread
            }
        });

        t.setDaemon(true);

        super.updateMetrics();
    }

    /**
     * Creates a new {@link BackgroundSystemMonitor} configured with the default update interval of 1 second.
     *
     * @return a new {@link BackgroundSystemMonitor} configured with the default update interval of 1 second
     */
    public static BackgroundSystemMonitor withDefaultUpdateInterval() {
        return new BackgroundSystemMonitor();
    }

    /**
     * Creates a new {@link BackgroundSystemMonitor} configured with the specified update interval.
     * <p>
     * The monitor's background thread will update all usage metrics after every {@code updateInterval}.
     *
     * @param updateInterval the time interval between consecutive metric updates
     * @return a new {@link BackgroundSystemMonitor} configured with the specified update interval
     */
    public static BackgroundSystemMonitor updateEvery(final Duration updateInterval) {
        requireNonNull(updateInterval, "updateInterval == null");
        if (updateInterval.isNegative() || updateInterval.isZero())
            throw new IllegalArgumentException("updateInterval <= 0");
        return new BackgroundSystemMonitor(updateInterval);
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
     * Registers a listener which will be invoked each time the CPU and memory usage metrics are updated.
     * 
     * @param listener the specified listener
     * @return this {@link BackgroundSystemMonitor} instance
     * @throws IllegalStateException if the monitor has already started
     */
    public BackgroundSystemMonitor onUpdate(final BiConsumer<CpuUsage, MemoryUsage> listener) {
        requireNonNull(listener, "listener == null");
        if (t.isAlive())
            throw new IllegalStateException("monitor has already started");
        this.listener = listener;
        return this;
    }

    @Override
    protected void updateMetrics() {
        super.updateMetrics();
        if (listener != null)
            listener.accept(getCpuUsage(), getMemoryUsage());
    }

    /**
     * Starts this monitor's background thread.
     * 
     * @return this monitor instance
     */
    public BackgroundSystemMonitor start() {
        if (!t.isAlive()) {
            t.start();
            if (listener != null)
                listener.accept(getCpuUsage(), getMemoryUsage());
        }
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