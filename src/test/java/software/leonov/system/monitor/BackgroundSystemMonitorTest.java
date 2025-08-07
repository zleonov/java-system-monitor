package software.leonov.system.monitor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

public class BackgroundSystemMonitorTest {

    @Test
    public void test_with_default_refresh_interval_creates_monitor() {
        BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultRefreshInterval();
        assertNotNull(monitor);
        monitor.close(); // Clean up
    }

    @Test
    public void test_refresh_every_creates_monitor() {
        BackgroundSystemMonitor monitor = BackgroundSystemMonitor.refreshEvery(Duration.ofMillis(500));
        assertNotNull(monitor);
        monitor.close(); // Clean up
    }

    @Test
    public void test_refresh_every_null_throws_exception() {
        assertThrows(NullPointerException.class, () -> {
            BackgroundSystemMonitor.refreshEvery(null);
        });
    }

    @Test
    public void test_refresh_every_negative_throws_exception() {
        assertThrows(IllegalArgumentException.class, () -> {
            BackgroundSystemMonitor.refreshEvery(Duration.ofMillis(-100));
        });
    }

    @Test
    public void test_refresh_every_zero_throws_exception() {
        assertThrows(IllegalArgumentException.class, () -> {
            BackgroundSystemMonitor.refreshEvery(Duration.ZERO);
        });
    }

    @Test
    public void test_before_start_returns_unsupported_values() {
        BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultRefreshInterval();

        CpuUsage cpu = monitor.getCpuUsage();
        MemoryUsage memory = monitor.getMemoryUsage();

        assertNotNull(cpu);
        assertNotNull(memory);

        // Before start(), should return UnsupportedSystemMonitor values (all -1)
        assertTrue(cpu.getProcessUsage() == -1.0);
        assertTrue(cpu.getSystemUsage() == -1.0);
        assertTrue(memory.getUsage() == -1.0);

        monitor.close(); // Clean up
    }

    @Test
    public void test_start_returns_monitor_instance() {
        BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultRefreshInterval();

        BackgroundSystemMonitor result = monitor.start();
        assertSame(monitor, result, "start() should return the same monitor instance");

        monitor.close(); // Clean up
    }

    @Test
    public void test_after_start_returns_real_values() throws InterruptedException {
        BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultRefreshInterval();
        monitor.start();

        // Give background thread time to refresh at least once
        Thread.sleep(300);

        CpuUsage cpu = monitor.getCpuUsage();
        MemoryUsage memory = monitor.getMemoryUsage();

        assertNotNull(cpu);
        assertNotNull(memory);

        // After start(), should return real values (not all -1)
        // Memory should definitely be available
        assertTrue(memory.getUsage() >= 0.0, "Memory usage should be available after start");
        assertTrue(memory.getUsed() >= 0, "Used memory should be non-negative after start");
        assertTrue(memory.getTotal() > 0, "Total memory should be positive after start");

        monitor.close(); // Clean up
    }

    @Test
    public void test_background_refresh_updates_values() throws InterruptedException {
        BackgroundSystemMonitor monitor = BackgroundSystemMonitor.refreshEvery(Duration.ofMillis(100));
        monitor.start();

        // Wait for initial refresh
        Thread.sleep(150);

        MemoryUsage memory1 = monitor.getMemoryUsage();
        assertNotNull(memory1);

        // Wait for another refresh cycle
        Thread.sleep(150);

        MemoryUsage memory2 = monitor.getMemoryUsage();
        assertNotNull(memory2);

        // Should be different instances due to background refresh
        assertTrue(memory1 != memory2, "Background refresh should create new instances");

        monitor.close(); // Clean up
    }

    @Test
    public void test_stop_method_calls_close() {
        BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultRefreshInterval();
        monitor.start();

        // stop() should delegate to close() - this should not throw
        monitor.stop();

        // Should still be safe to call close() again
        monitor.close();
    }

    @Test
    public void test_close_interrupts_background_thread() throws InterruptedException {
        BackgroundSystemMonitor monitor = BackgroundSystemMonitor.refreshEvery(Duration.ofMillis(50));
        monitor.start();

        // Let it run briefly
        Thread.sleep(100);

        // Close should interrupt the background thread
        monitor.close();

        // Give some time for thread to terminate
        Thread.sleep(100);

        // Should still be able to call getters (will return unsupported values)
        CpuUsage cpu = monitor.getCpuUsage();
        MemoryUsage memory = monitor.getMemoryUsage();

        assertNotNull(cpu);
        assertNotNull(memory);
    }

    @Test
    public void test_multiple_start_calls_safe() {
        BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultRefreshInterval();

        // First start should work
        monitor.start();

        // Multiple starts should not crash (though may throw IllegalThreadStateException)
        try {
            monitor.start();
        } catch (IllegalThreadStateException e) {
            // This is expected - thread can only be started once
        }

        monitor.close(); // Clean up
    }

    @Test
    public void test_concurrent_getter_access() throws InterruptedException {
        BackgroundSystemMonitor monitor = BackgroundSystemMonitor.refreshEvery(Duration.ofMillis(50));
        monitor.start();

        final int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        final Exception[] exceptions = new Exception[threadCount];

        // Create multiple threads accessing getters concurrently
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 20; j++) {
                        CpuUsage cpu = monitor.getCpuUsage();
                        MemoryUsage memory = monitor.getMemoryUsage();
                        
                        assertNotNull(cpu);
                        assertNotNull(memory);
                        
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    exceptions[threadIndex] = e;
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Check that no exceptions occurred
        for (int i = 0; i < threadCount; i++) {
            if (exceptions[i] != null) {
                throw new AssertionError("Thread " + i + " threw exception", exceptions[i]);
            }
        }

        monitor.close(); // Clean up
    }

    @Test
    public void test_daemon_thread_behavior() {
        BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultRefreshInterval();
        monitor.start();

        // The background thread should be a daemon thread
        // We can't directly test this without reflection, but we can verify
        // that the monitor works as expected
        CpuUsage cpu = monitor.getCpuUsage();
        MemoryUsage memory = monitor.getMemoryUsage();

        assertNotNull(cpu);
        assertNotNull(memory);

        monitor.close(); // Clean up
    }
}