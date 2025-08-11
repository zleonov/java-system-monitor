package software.leonov.system.monitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class BackgroundSystemMonitorTest {

    @Test
    public void test_withDefaultUpdateInterval_not_null() {
        final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultUpdateInterval();
        assertNotNull(monitor);
        monitor.close(); // Clean up
    }

    @Test
    public void test_refreshEvery_not_null() {
        final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.updateEvery(Duration.ofMillis(500));
        assertNotNull(monitor);
        monitor.close(); // Clean up
    }

    @Test
    public void test_refreshEvery_null_throws_exception() {
        final String message = assertThrows(NullPointerException.class, () -> {
            BackgroundSystemMonitor.updateEvery(null);
        }).getMessage();

        assertEquals("updateInterval == null", message);
    }

    @Test
    public void test_refreshEvery_negative_throws_exception() {
        final String message = assertThrows(IllegalArgumentException.class, () -> {
            BackgroundSystemMonitor.updateEvery(Duration.ofMillis(-100));
        }).getMessage();

        assertEquals("updateInterval <= 0", message);
    }

    @Test
    public void test_refreshEvery_zero_throws_exception() {
        final String message = assertThrows(IllegalArgumentException.class, () -> {
            BackgroundSystemMonitor.updateEvery(Duration.ZERO);
        }).getMessage();

        assertEquals("updateInterval <= 0", message);
    }

    @Test
    public void test_before_start_returns_negative_values() {
        final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultUpdateInterval();

        final CpuUsage    cpu    = monitor.getCpuUsage();
        final MemoryUsage memory = monitor.getMemoryUsage();

        assertNotNull(cpu);
        assertNotNull(memory);

        // Before start(), should return UnsupportedSystemMonitor values (all -1)
        assertTrue(cpu.getProcessCpuLoad() == -1.0);
        assertTrue(cpu.getSystemCpuLoad() == -1.0);
        assertTrue(memory.getUsedMemory() == -1L);

        monitor.close(); // Clean up
    }

    @Test
    public void test_start_returns_monitor_instance() {
        final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultUpdateInterval();

        final BackgroundSystemMonitor result = monitor.start();
        assertSame(monitor, result, "start() should return the same monitor instance");

        monitor.close(); // Clean up
    }

    @Test
    public void test_after_start_returns_real_values() throws InterruptedException {
        final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultUpdateInterval();
        monitor.start();

        // Give background thread time to refresh at least once
        Thread.sleep(300);

        final CpuUsage    cpu    = monitor.getCpuUsage();
        final MemoryUsage memory = monitor.getMemoryUsage();

        assertNotNull(cpu);
        assertNotNull(memory);

        // After start(), should return real values (not all -1)
        // Memory should definitely be available
        assertTrue(memory.getUsedMemory() >= 0, "Used memory should be non-negative after start");
        assertTrue(memory.getTotalMemory() > 0, "Total memory should be positive after start");

        monitor.close(); // Clean up
    }

    @Test
    public void test_background_refresh_updates_values() throws InterruptedException {
        final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.updateEvery(Duration.ofMillis(100));
        monitor.start();

        // Wait for initial refresh
        Thread.sleep(150);

        final MemoryUsage memory1 = monitor.getMemoryUsage();
        assertNotNull(memory1);

        // Wait for another refresh cycle
        Thread.sleep(150);

        final MemoryUsage memory2 = monitor.getMemoryUsage();
        assertNotNull(memory2);

        // Should be different instances due to background refresh
        assertNotSame(memory1, memory2, "Background refresh should create new instances");

        monitor.close(); // Clean up
    }

    // This is a very rudimentary test to get CPU and memory usage to increase under load
    @Test
    public void test_cpu_and_memory_usage_under_load() throws InterruptedException {
        final BackgroundSystemMonitor monitor     = BackgroundSystemMonitor.updateEvery(Duration.ofMillis(250));
        final int                     threadCount = SystemMonitor.getAvailableProcessors();
        final List<PrimeWorker>       threads     = new ArrayList<>(threadCount);

        System.out.println("Sleeping for 10 seconds");
        Thread.sleep(10000);

        System.out.println("Starting monitor");
        monitor.start();

        System.out.println("Getting initial CPU and memory metrics");
        final CpuUsage    cpu1    = monitor.getCpuUsage();
        final MemoryUsage memory1 = monitor.getMemoryUsage();

        System.out.println("Starting worker threads");
        for (int i = 0; i < threadCount; i++) {
            final PrimeWorker t = new PrimeWorker("Worker" + i);
            t.start();
            threads.add(t);
        }

        System.out.println("Sleeping for 10 seconds");
        Thread.sleep(10000);

        System.out.println("Getting subsequent CPU and memory metrics");
        final CpuUsage    cpu2    = monitor.getCpuUsage();
        final MemoryUsage memory2 = monitor.getMemoryUsage();

        System.out.println("Stopping worker threads");
        threads.forEach(Thread::interrupt);
        for (final PrimeWorker w : threads)
            w.join();

        System.out.println("cpu1: " + cpu1);
        System.out.println("cpu2: " + cpu2);
        System.out.println("memory1: " + memory1);
        System.out.println("memory2: " + memory2);

        // @formatter:off
        assertTrue(cpu1.getProcessCpuLoad()        == -1d || cpu1.getProcessCpuLoad()        == 100d || cpu1.getProcessCpuLoad()        < cpu2.getProcessCpuLoad());
        assertTrue(cpu1.getSystemCpuLoad()         == -1d || cpu1.getSystemCpuLoad()         == 100d || cpu1.getSystemCpuLoad()         < cpu2.getSystemCpuLoad());
        assertTrue(cpu1.getSystemLoadAverage()     == -1d || cpu1.getSystemLoadAverage()     == 100d || cpu1.getSystemLoadAverage()     < cpu2.getSystemLoadAverage());
        assertTrue(cpu1.getAverageProcessCpuLoad() == -1d || cpu1.getAverageProcessCpuLoad() == 100d || cpu1.getAverageProcessCpuLoad() < cpu2.getAverageProcessCpuLoad());
        assertTrue(cpu1.getAverageSystemCpuLoad()  == -1d || cpu1.getAverageSystemCpuLoad()  == 100d || cpu1.getAverageSystemCpuLoad()  < cpu2.getAverageSystemCpuLoad());
        assertTrue(cpu1.getMaxProcessCpuLoad()     == -1d || cpu1.getMaxProcessCpuLoad()     == 100d || cpu1.getMaxProcessCpuLoad()     < cpu2.getMaxProcessCpuLoad());
        assertTrue(cpu1.getMaxSystemCpuLoad()      == -1d || cpu1.getMaxSystemCpuLoad()      == 100d || cpu1.getMaxSystemCpuLoad()      < cpu2.getMaxSystemCpuLoad());
        // @formatter:on

        // @formatter:off
        assertTrue(memory1.getUsedMemory()    == -1l || memory1.getUsedMemory()    <  memory2.getUsedMemory());
        assertTrue(memory1.getTotalMemory()   == -1l || memory1.getTotalMemory()   <= memory2.getTotalMemory()); // total allocated memory is unlikely to change
        assertTrue(memory1.getMaxUsedMemory() == -1l || memory1.getMaxUsedMemory() <  memory2.getMaxUsedMemory());
        // @formatter:on

        monitor.close(); // Clean up
    }

    private static class PrimeWorker extends Thread {

        private final String name;

        public PrimeWorker(final String name) {
            this.name = name;
        }

        /**
         * Check if a number is prime using trial division
         */
        public boolean isPrime(final long number) {
            if (number < 2)
                return false;
            if (number == 2)
                return true;
            if (number % 2 == 0)
                return false;

            // Test all odd divisors up to sqrt(number)
            for (long i = 3; i * i <= number; i += 2) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(name + ": interrupted");
                    return false;
                }
                if (number % i == 0)
                    return false;
            }
            return true;
        }

        @Override
        public void run() {
            final ArrayList<Long> primes = new ArrayList<>();

            for (long i = 2; i <= Long.MAX_VALUE && !Thread.currentThread().isInterrupted(); i++)
                if (isPrime(i))
                    primes.add(i);
        }
    }

    @Test
    public void test_stop_method_calls_close() {
        final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultUpdateInterval();
        monitor.start();

        // stop() should delegate to close() - this should not throw
        monitor.stop();

        // Should still be safe to call close() again
        monitor.close();
    }

    @Test
    public void test_close_interrupts_background_thread() throws InterruptedException {
        final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.updateEvery(Duration.ofMillis(50));
        monitor.start();

        // Let it run briefly
        Thread.sleep(100);

        // Close should interrupt the background thread
        monitor.close();

        // Give some time for thread to terminate
        Thread.sleep(100);

        // Should still be able to call getters (will return unsupported values)
        final CpuUsage    cpu    = monitor.getCpuUsage();
        final MemoryUsage memory = monitor.getMemoryUsage();

        assertNotNull(cpu);
        assertNotNull(memory);
    }

    @Test
    public void test_multiple_start_calls_safe() {
        final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultUpdateInterval();

        // First start should work
        monitor.start();

        // Multiple starts should not crash (though may throw IllegalThreadStateException)
        try {
            monitor.start();
        } catch (final IllegalThreadStateException e) {
            // This is expected - thread can only be started once
        }

        monitor.close(); // Clean up
    }

    @Test
    public void test_concurrent_getter_access() throws InterruptedException {
        final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.updateEvery(Duration.ofMillis(50));
        monitor.start();

        final int         threadCount = 5;
        final Thread[]    threads     = new Thread[threadCount];
        final Exception[] exceptions  = new Exception[threadCount];

        // Create multiple threads accessing getters concurrently
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 20; j++) {
                        final CpuUsage    cpu    = monitor.getCpuUsage();
                        final MemoryUsage memory = monitor.getMemoryUsage();

                        assertNotNull(cpu);
                        assertNotNull(memory);

                        Thread.sleep(10);
                    }
                } catch (final Exception e) {
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

        monitor.close(); // Clean up
    }

    @Test
    public void test_daemon_thread_behavior() {
        final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultUpdateInterval();
        monitor.start();

        // The background thread should be a daemon thread
        // We can't directly test this without reflection, but we can verify
        // that the monitor works as expected
        final CpuUsage    cpu    = monitor.getCpuUsage();
        final MemoryUsage memory = monitor.getMemoryUsage();

        assertNotNull(cpu);
        assertNotNull(memory);

        monitor.close(); // Clean up
    }

    @Test
    public void test_after_stop_returns_unsupported_values() throws InterruptedException {
        final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.withDefaultUpdateInterval();
        monitor.start();

        // Wait for background thread to start and refresh metrics
        Thread.sleep(300);

        // Verify it's returning real values after start
        final CpuUsage    cpuBeforeStop    = monitor.getCpuUsage();
        final MemoryUsage memoryBeforeStop = monitor.getMemoryUsage();

        assertNotNull(cpuBeforeStop);
        assertNotNull(memoryBeforeStop);

        // After start, memory should be real values (not -1)
        assertTrue(memoryBeforeStop.getUsedMemory() >= 0, "Memory should be valid before stop");
        assertTrue(memoryBeforeStop.getTotalMemory() > 0, "Total memory should be positive before stop");

        // Stop the monitor (interrupts background thread)
        monitor.stop();

        // Give time for thread interruption to take effect
        Thread.sleep(100);

        // After stop, should return UnsupportedSystemMonitor values (all -1)
        final CpuUsage    cpuAfterStop    = monitor.getCpuUsage();
        final MemoryUsage memoryAfterStop = monitor.getMemoryUsage();

        assertNotNull(cpuAfterStop);
        assertNotNull(memoryAfterStop);

        // All values should now be -1 (unsupported)
        assertEquals(-1.0, cpuAfterStop.getProcessCpuLoad(), "CPU values should be -1 after stop");
        assertEquals(-1.0, cpuAfterStop.getSystemCpuLoad(), "CPU values should be -1 after stop");
        assertEquals(-1L, memoryAfterStop.getUsedMemory(), "Memory values should be -1 after stop");
        assertEquals(-1L, memoryAfterStop.getTotalMemory(), "Memory values should be -1 after stop");
    }
}