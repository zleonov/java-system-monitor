package software.leonov.system.monitor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SystemMonitorTest {

    @Test
    public void test_create_returns_non_null_monitor() {
        SystemMonitor monitor = SystemMonitor.create();
        assertNotNull(monitor);
    }

    @Test
    public void test_cpu_usage_returns_non_null() {
        SystemMonitor monitor = SystemMonitor.create();
        CpuUsage cpu = monitor.getCpuUsage();
        assertNotNull(cpu);
    }

    @Test
    public void test_memory_usage_returns_non_null() {
        SystemMonitor monitor = SystemMonitor.create();
        MemoryUsage memory = monitor.getMemoryUsage();
        assertNotNull(memory);
    }

    @Test
    public void test_cpu_usage_values_in_valid_range() {
        SystemMonitor monitor = SystemMonitor.create();
        CpuUsage cpu = monitor.getCpuUsage();

        double processCpu = cpu.getProcessUsage();
        double systemCpu = cpu.getSystemUsage();

        // CPU percentages should be -1 (unsupported) or 0-100%
        assertTrue(processCpu == -1.0 || (processCpu >= 0.0 && processCpu <= 100.0),
                "Process CPU should be -1 or 0-100%, got: " + processCpu);
        assertTrue(systemCpu == -1.0 || (systemCpu >= 0.0 && systemCpu <= 100.0),
                "System CPU should be -1 or 0-100%, got: " + systemCpu);
    }

    @Test
    public void test_memory_usage_values_valid() {
        SystemMonitor monitor = SystemMonitor.create();
        MemoryUsage memory = monitor.getMemoryUsage();

        long used = memory.getUsed();
        long total = memory.getTotal();
        long available = memory.getAvailable();

        // Memory values should be non-negative
        assertTrue(used >= 0, "Used memory should be non-negative, got: " + used);
        assertTrue(total >= 0, "Total memory should be non-negative, got: " + total);
        assertTrue(available >= 0, "Available memory should be non-negative, got: " + available);

        // Used memory should not exceed total
        assertTrue(used <= total, "Used memory (" + used + ") should not exceed total (" + total + ")");
    }

    @Test
    public void test_memory_usage_percentage_valid() {
        SystemMonitor monitor = SystemMonitor.create();
        MemoryUsage memory = monitor.getMemoryUsage();

        double usagePercent = memory.getUsage();

        // Usage percentage should be -1 or 0-100%
        assertTrue(usagePercent == -1.0 || (usagePercent >= 0.0 && usagePercent <= 100.0),
                "Memory usage percentage should be -1 or 0-100%, got: " + usagePercent);
    }

    @Test
    public void test_cpu_averages_valid_range() {
        SystemMonitor monitor = SystemMonitor.create();
        CpuUsage cpu = monitor.getCpuUsage();

        double avgProcess = cpu.getAverageProcessUsage();
        double avgSystem = cpu.getAverageSystemUsage();

        // Average CPU should be -1 or 0-100%
        assertTrue(avgProcess == -1.0 || (avgProcess >= 0.0 && avgProcess <= 100.0),
                "Average process CPU should be -1 or 0-100%, got: " + avgProcess);
        assertTrue(avgSystem == -1.0 || (avgSystem >= 0.0 && avgSystem <= 100.0),
                "Average system CPU should be -1 or 0-100%, got: " + avgSystem);
    }

    @Test
    public void test_cpu_maximums_valid_range() {
        SystemMonitor monitor = SystemMonitor.create();
        CpuUsage cpu = monitor.getCpuUsage();

        double maxProcess = cpu.getMaxProcessUsage();
        double maxSystem = cpu.getMaxSystemUsage();

        // Max CPU should be -1 or 0-100%
        assertTrue(maxProcess == -1.0 || (maxProcess >= 0.0 && maxProcess <= 100.0),
                "Max process CPU should be -1 or 0-100%, got: " + maxProcess);
        assertTrue(maxSystem == -1.0 || (maxSystem >= 0.0 && maxSystem <= 100.0),
                "Max system CPU should be -1 or 0-100%, got: " + maxSystem);
    }

    @Test
    public void test_system_load_average_valid() {
        SystemMonitor monitor = SystemMonitor.create();
        CpuUsage cpu = monitor.getCpuUsage();

        double loadAverage = cpu.getSystemLoadAverage();

        // Load average should be non-negative or -1 (unsupported)
        assertTrue(loadAverage == -1.0 || loadAverage >= 0.0,
                "System load average should be -1 or non-negative, got: " + loadAverage);
    }

    @Test
    public void test_multiple_calls_return_consistent_types() {
        SystemMonitor monitor = SystemMonitor.create();

        CpuUsage cpu1 = monitor.getCpuUsage();
        CpuUsage cpu2 = monitor.getCpuUsage();
        MemoryUsage memory1 = monitor.getMemoryUsage();
        MemoryUsage memory2 = monitor.getMemoryUsage();

        assertNotNull(cpu1);
        assertNotNull(cpu2);
        assertNotNull(memory1);
        assertNotNull(memory2);

        // Should return same type of objects
        assertTrue(cpu1.getClass().equals(cpu2.getClass()));
        assertTrue(memory1.getClass().equals(memory2.getClass()));
    }

    @Test
    public void test_max_memory_not_negative() {
        SystemMonitor monitor = SystemMonitor.create();
        MemoryUsage memory = monitor.getMemoryUsage();

        long maxUsed = memory.getMaxUsed();
        assertTrue(maxUsed >= -1, "Max used memory should be non-negative or -1, got: " + maxUsed);
    }
}