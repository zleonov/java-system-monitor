package software.leonov.system.monitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

public class LazySystemMonitorTest {

    @Test
    public void test_withDefaultRefreshThreshold_not_null() {
        final LazySystemMonitor monitor = LazySystemMonitor.withDefaultRefreshThreshold();
        assertNotNull(monitor);
    }

    @Test
    public void test_withRefreshThreshold_not_null() {
        final LazySystemMonitor monitor = LazySystemMonitor.withRefreshThreshold(Duration.ofMillis(500));
        assertNotNull(monitor);
    }

    @Test
    public void test_withRefreshThreshold_null_throws_exception() {
        final String message = assertThrows(NullPointerException.class, () -> {
            LazySystemMonitor.withRefreshThreshold(null);
        }).getMessage();

        assertEquals("refreshThreshold == null", message);
    }

    @Test
    public void test_withRefreshThreshold_negative_throws_exception() {
        final String message = assertThrows(IllegalArgumentException.class, () -> {
            LazySystemMonitor.withRefreshThreshold(Duration.ofMillis(-100));
        }).getMessage();

        assertEquals("refreshThreshold <= 0", message);
    }

    @Test
    public void test_withRefreshThreshold_zero_throws_exception() {
        final String message = assertThrows(IllegalArgumentException.class, () -> {
            LazySystemMonitor.withRefreshThreshold(Duration.ZERO);
        }).getMessage();

        assertEquals("refreshThreshold <= 0", message);
    }

    @Test
    public void test_caching_behavior_within_threshold() {
        final LazySystemMonitor monitor = LazySystemMonitor.withRefreshThreshold(Duration.ofSeconds(1));

        // Get initial values
        CpuUsage    cpu1    = monitor.getCpuUsage();
        MemoryUsage memory1 = monitor.getMemoryUsage();

        // Get values again immediately (should be cached)
        CpuUsage    cpu2    = monitor.getCpuUsage();
        MemoryUsage memory2 = monitor.getMemoryUsage();

        // Should return same object instances due to caching
        assertSame(cpu1, cpu2);
        assertSame(memory1, memory2);
    }

    @Test
    public void test_refresh_after_threshold_elapsed() throws InterruptedException {
        final LazySystemMonitor monitor = LazySystemMonitor.withRefreshThreshold(Duration.ofMillis(100));

        // Get initial values
        CpuUsage    cpu1    = monitor.getCpuUsage();
        MemoryUsage memory1 = monitor.getMemoryUsage();

        // Wait for threshold to elapse
        Thread.sleep(150);

        // Get values again (should be refreshed)
        CpuUsage    cpu2    = monitor.getCpuUsage();
        MemoryUsage memory2 = monitor.getMemoryUsage();

        // Should return different object instances after refresh
        assertTrue(cpu1 != cpu2);
        assertTrue(memory1 != memory2);
    }

    @Test
    public void test_concurrent_access_thread_safety() throws InterruptedException {
        final LazySystemMonitor monitor = LazySystemMonitor.withRefreshThreshold(Duration.ofMillis(100));

        final int         threadCount = 10;
        final Thread[]    threads     = new Thread[threadCount];
        final Exception[] exceptions  = new Exception[threadCount];

        // Create multiple threads accessing the monitor concurrently
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 50; j++) {
                        final CpuUsage    cpu    = monitor.getCpuUsage();
                        final MemoryUsage memory = monitor.getMemoryUsage();

                        assertNotNull(cpu);
                        assertNotNull(memory);

                        // Small delay to increase chance of concurrent access
                        Thread.sleep(1);
                    }
                } catch (Exception e) {
                    exceptions[threadIndex] = e;
                }
            });
        }

        // Start all threads
        for (final Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (final Thread thread : threads) {
            thread.join();
        }

        // Check that no exceptions occurred
        for (int i = 0; i < threadCount; i++) {
            if (exceptions[i] != null) {
                throw new AssertionError("Thread " + i + " threw exception", exceptions[i]);
            }
        }
    }

    @Test
    public void test_close_does_not_throw_exception() {
        final LazySystemMonitor monitor = LazySystemMonitor.withDefaultRefreshThreshold();

        // close() should be a no-op and not throw
        try {
            monitor.close();
        } catch (final Exception e) {
            throw new AssertionError("close() should not throw exception", e);
        }
    }

    @Test
    public void test_usage_after_close_still_works() {
        final LazySystemMonitor monitor = LazySystemMonitor.withDefaultRefreshThreshold();

        try {
            monitor.close();
        } catch (Exception e) {
            // Should not happen, but handle gracefully
        }

        // Should still work after close since it's a no-op
        final CpuUsage    cpu    = monitor.getCpuUsage();
        final MemoryUsage memory = monitor.getMemoryUsage();

        assertNotNull(cpu);
        assertNotNull(memory);
    }

    @Test
    public void test_mixed_cpu_and_memory_calls() {
        final LazySystemMonitor monitor = LazySystemMonitor.withRefreshThreshold(Duration.ofSeconds(1));

        CpuUsage    cpu1    = monitor.getCpuUsage();
        MemoryUsage memory1 = monitor.getMemoryUsage();
        CpuUsage    cpu2    = monitor.getCpuUsage();
        MemoryUsage memory2 = monitor.getMemoryUsage();

        // All calls within threshold should return same instances
        assertSame(cpu1, cpu2);
        assertSame(memory1, memory2);
    }

    @Test
    public void test_very_short_threshold_still_works() {
        final LazySystemMonitor monitor = LazySystemMonitor.withRefreshThreshold(Duration.ofMillis(1));

        // Should not crash with very short threshold
        CpuUsage    cpu    = monitor.getCpuUsage();
        MemoryUsage memory = monitor.getMemoryUsage();

        assertNotNull(cpu);
        assertNotNull(memory);
    }
}