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
        final CpuUsage    cpu1    = monitor.getCpuUsage();
        final MemoryUsage memory1 = monitor.getMemoryUsage();

        // Get values again immediately (should be cached)
        final CpuUsage    cpu2    = monitor.getCpuUsage();
        final MemoryUsage memory2 = monitor.getMemoryUsage();

        // Should return same object instances due to caching
        assertSame(cpu1, cpu2);
        assertSame(memory1, memory2);
    }

    @Test
    public void test_refresh_after_threshold_elapsed() throws InterruptedException {
        final LazySystemMonitor monitor = LazySystemMonitor.withRefreshThreshold(Duration.ofMillis(100));

        // Get initial values
        final CpuUsage    cpu1    = monitor.getCpuUsage();
        final MemoryUsage memory1 = monitor.getMemoryUsage();

        // Wait for threshold to elapse
        Thread.sleep(150);

        // Get values again (should be refreshed)
        final CpuUsage    cpu2    = monitor.getCpuUsage();
        final MemoryUsage memory2 = monitor.getMemoryUsage();

        // Should return different object instances after refresh
        assertNotSame(cpu1, cpu2);
        assertNotSame(memory1, memory2);
    }

    // This is a very rudimentary test to get CPU and memory usage to increase
    @Test
    public void test_cpu_and_memory_usage_under_load() throws InterruptedException {
        final LazySystemMonitor monitor     = LazySystemMonitor.withRefreshThreshold(Duration.ofMillis(500));
        final int               threadCount = SystemMonitor.getAvailableProcessors();
        final List<PrimeWorker> threads     = new ArrayList<>(threadCount);

        System.out.println("Sleeping for 10 second");
        Thread.sleep(10000);

        System.out.println("Getting intial CPU and memory metrics");
        final CpuUsage    cpu1    = monitor.getCpuUsage();
        final MemoryUsage memory1 = monitor.getMemoryUsage();

        System.out.println("Starting worker threads");
        for (int i = 0; i < threadCount; i++) {
            final PrimeWorker t = new PrimeWorker("Worker" + i);
            t.start();
            threads.add(t);
        }

        System.out.println("Sleeping for 10 second");
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
    }

    static class PrimeWorker extends Thread {

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
    public void test_usage_after_close_still_works() {
        final LazySystemMonitor monitor = LazySystemMonitor.withDefaultRefreshThreshold();

        monitor.close();

        // Should still work after close since it's a no-op
        final CpuUsage    cpu    = monitor.getCpuUsage();
        final MemoryUsage memory = monitor.getMemoryUsage();

        assertNotNull(cpu);
        assertNotNull(memory);
    }

}