package software.leonov.system.monitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class UnsupportedSystemMonitorTest {

    @Test
    public void test_getInstance_returns_singleton() {
        final UnsupportedSystemMonitor monitor1 = UnsupportedSystemMonitor.getInstance();
        final UnsupportedSystemMonitor monitor2 = UnsupportedSystemMonitor.getInstance();

        assertNotNull(monitor1);
        assertNotNull(monitor2);
        assertSame(monitor1, monitor2, "Should return same singleton instance");
    }

    @Test
    public void test_getCpuUsage_returns_negative_values() {
        final UnsupportedSystemMonitor monitor = UnsupportedSystemMonitor.getInstance();
        final CpuUsage                 cpu     = monitor.getCpuUsage();

        assertNotNull(cpu);

        // All CPU values should be -1 (unsupported)
        assertEquals(-1.0, cpu.getProcessCpuLoad(), "Process CPU usage should be -1.0");
        assertEquals(-1.0, cpu.getSystemCpuLoad(), "System CPU usage should be -1.0");
        assertEquals(-1.0, cpu.getSystemLoadAverage(), "System load average should be -1.0");
        assertEquals(-1.0, cpu.getAverageProcessCpuLoad(), "Average process CPU should be -1.0");
        assertEquals(-1.0, cpu.getAverageSystemCpuLoad(), "Average system CPU should be -1.0");
        assertEquals(-1.0, cpu.getMaxProcessCpuLoad(), "Max process CPU should be -1.0");
        assertEquals(-1.0, cpu.getMaxSystemCpuLoad(), "Max system CPU should be -1.0");
    }

    @Test
    public void test_getMemoryUsage_returns_negative_values() {
        final UnsupportedSystemMonitor monitor = UnsupportedSystemMonitor.getInstance();
        final MemoryUsage              memory  = monitor.getMemoryUsage();

        assertNotNull(memory);

        // All memory values should be -1 (unsupported)
        assertEquals(-1L, memory.getUsedMemory(), "Used memory should be -1");
        assertEquals(-1L, memory.getTotalMemory(), "Total memory should be -1");
        assertEquals(-1L, memory.getMaxUsedMemory(), "Max used memory should be -1");
    }

    @Test
    public void test_multiple_calls_return_same_instances() {
        final UnsupportedSystemMonitor monitor = UnsupportedSystemMonitor.getInstance();

        final CpuUsage    cpu1    = monitor.getCpuUsage();
        final CpuUsage    cpu2    = monitor.getCpuUsage();
        final MemoryUsage memory1 = monitor.getMemoryUsage();
        final MemoryUsage memory2 = monitor.getMemoryUsage();

        // Should return same instances (cached)
        assertSame(cpu1, cpu2, "Should return same CPU usage instance");
        assertSame(memory1, memory2, "Should return same memory usage instance");
    }
    
    @Test
    public void test_usage_after_close_still_works() {
        final UnsupportedSystemMonitor monitor = UnsupportedSystemMonitor.getInstance();

        monitor.close();

        // Should still work after close
        final CpuUsage    cpu    = monitor.getCpuUsage();
        final MemoryUsage memory = monitor.getMemoryUsage();

        assertNotNull(cpu);
        assertNotNull(memory);
        assertEquals(-1.0, cpu.getProcessCpuLoad());
        assertEquals(-1L, memory.getUsedMemory());
    }
}