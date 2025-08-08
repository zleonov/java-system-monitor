package software.leonov.system.monitor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SystemMonitorTest {

    @Test
    public void test_getAvailableMemory_returns_valid_value() {
        final long availableMemory = SystemMonitor.getAvailableMemory();

        // Available memory should be positive
        assertTrue(availableMemory > 0L, "Available memory should be positive, got: " + availableMemory);
    }

    @Test
    public void test_getAvailableProcessors_returns_positive_value() {
        final int processors = SystemMonitor.getAvailableProcessors();

        // Number of processors should always be positive
        assertTrue(processors > 0, "Available processors should be positive, got: " + processors);
    }

    @Test
    public void test_getJVMName_returns_non_null_string() {
        final String jvmName = SystemMonitor.getJVMName();

        assertNotNull(jvmName, "JVM name should not be null");
        assertTrue(jvmName.length() > 0, "JVM name should not be empty");
    }

    @Test
    public void test_getJVMVendor_returns_non_null_string() {
        final String jvmVendor = SystemMonitor.getJVMVendor();

        assertNotNull(jvmVendor, "JVM vendor should not be null");
        assertTrue(jvmVendor.length() > 0, "JVM vendor should not be empty");
    }

    @Test
    public void test_getSupportedJavaVersion_returns_non_null_string() {
        final String version = SystemMonitor.getSupportedJavaVersion();

        assertNotNull(version, "Java version should not be null");
        assertTrue(version.length() > 0, "Java version should not be empty");
    }
}