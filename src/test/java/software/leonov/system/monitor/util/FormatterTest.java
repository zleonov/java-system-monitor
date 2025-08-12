package software.leonov.system.monitor.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FormatterTest {

    @Test
    public void test_formatPercent_negative_values() {
        assertEquals("N/A", Formatter.formatPercent(-1.0));
        assertEquals("N/A", Formatter.formatPercent(-0.5));
        assertEquals("N/A", Formatter.formatPercent(-100.0));
    }

    @Test
    public void test_formatPercent_zero() {
        assertEquals("0.00%", Formatter.formatPercent(0.0));
    }

    @Test
    public void test_formatPercent_positive_values() {
        assertEquals("12.75%", Formatter.formatPercent(12.75));
        assertEquals("100.00%", Formatter.formatPercent(100.0));
        assertEquals("0.01%", Formatter.formatPercent(0.01));
        assertEquals("99.99%", Formatter.formatPercent(99.99));
    }

    @Test
    public void test_formatPercent_rounding() {
        assertEquals("12.35%", Formatter.formatPercent(12.346));
        assertEquals("12.34%", Formatter.formatPercent(12.344));
        assertEquals("0.00%", Formatter.formatPercent(0.004));
        assertEquals("0.01%", Formatter.formatPercent(0.005));
    }

    @Test
    public void test_formatBinaryBytes_negative_values() {
        assertEquals("N/A", Formatter.formatBinaryBytes(-1L));
        assertEquals("N/A", Formatter.formatBinaryBytes(-1024L));
    }

    @Test
    public void test_formatBinaryBytes_small_values() {
        assertEquals("0 bytes", Formatter.formatBinaryBytes(0L));
        assertEquals("1 bytes", Formatter.formatBinaryBytes(1L));
        assertEquals("512 bytes", Formatter.formatBinaryBytes(512L));
        assertEquals("1023 bytes", Formatter.formatBinaryBytes(1023L));
    }

    @Test
    public void test_formatBinaryBytes_kibibytes() {
        assertEquals("1.00KiB", Formatter.formatBinaryBytes(1024L));
        assertEquals("1.50KiB", Formatter.formatBinaryBytes(1536L));
        assertEquals("2.00KiB", Formatter.formatBinaryBytes(2048L));
    }

    @Test
    public void test_formatBinaryBytes_mebibytes() {
        assertEquals("1.00MiB", Formatter.formatBinaryBytes(1024L * 1024L));
        assertEquals("1.50MiB", Formatter.formatBinaryBytes(1024L * 1024L + 512L * 1024L));
        assertEquals("2.00MiB", Formatter.formatBinaryBytes(2L * 1024L * 1024L));
    }

    @Test
    public void test_formatBinaryBytes_gibibytes() {
        assertEquals("1.00GiB", Formatter.formatBinaryBytes(1024L * 1024L * 1024L));
        assertEquals("4.00GiB", Formatter.formatBinaryBytes(4L * 1024L * 1024L * 1024L));
    }

    @Test
    public void test_formatBinaryBytes_tebibytes() {
        assertEquals("1.00TiB", Formatter.formatBinaryBytes(1024L * 1024L * 1024L * 1024L));
        assertEquals("2.50TiB", Formatter.formatBinaryBytes(2L * 1024L * 1024L * 1024L * 1024L + 512L * 1024L * 1024L * 1024L));
    }

    @Test
    public void test_formatDecimalBytes_negative_values() {
        assertEquals("N/A", Formatter.formatDecimalBytes(-1L));
        assertEquals("N/A", Formatter.formatDecimalBytes(-1000L));
    }

    @Test
    public void test_formatDecimalBytes_small_values() {
        assertEquals("0 bytes", Formatter.formatDecimalBytes(0L));
        assertEquals("1 bytes", Formatter.formatDecimalBytes(1L));
        assertEquals("500 bytes", Formatter.formatDecimalBytes(500L));
        assertEquals("999 bytes", Formatter.formatDecimalBytes(999L));
    }

    @Test
    public void test_formatDecimalBytes_kilobytes() {
        assertEquals("1.00KB", Formatter.formatDecimalBytes(1000L));
        assertEquals("1.50KB", Formatter.formatDecimalBytes(1500L));
        assertEquals("2.00KB", Formatter.formatDecimalBytes(2000L));
    }

    @Test
    public void test_formatDecimalBytes_megabytes() {
        assertEquals("1.00MB", Formatter.formatDecimalBytes(1000L * 1000L));
        assertEquals("1.50MB", Formatter.formatDecimalBytes(1000L * 1000L + 500L * 1000L));
        assertEquals("2.00MB", Formatter.formatDecimalBytes(2L * 1000L * 1000L));
    }

    @Test
    public void test_formatDecimalBytes_gigabytes() {
        assertEquals("1.00GB", Formatter.formatDecimalBytes(1000L * 1000L * 1000L));
        assertEquals("4.00GB", Formatter.formatDecimalBytes(4L * 1000L * 1000L * 1000L));
    }

    @Test
    public void test_formatDecimalBytes_terabytes() {
        assertEquals("1.00TB", Formatter.formatDecimalBytes(1000L * 1000L * 1000L * 1000L));
        assertEquals("2.50TB", Formatter.formatDecimalBytes(2L * 1000L * 1000L * 1000L * 1000L + 500L * 1000L * 1000L * 1000L));
    }

    @Test
    public void test_formatDecimalBytes_petabytes() {
        assertEquals("1.00PB", Formatter.formatDecimalBytes(1000L * 1000L * 1000L * 1000L * 1000L));
    }

    @Test
    public void test_formatBinaryBytes_pebibytes() {
        assertEquals("1.00PiB", Formatter.formatBinaryBytes(1024L * 1024L * 1024L * 1024L * 1024L));
    }

    @Test
    public void test_formatBinaryBytes_exbibytes() {
        assertEquals("1.00EiB", Formatter.formatBinaryBytes(1024L * 1024L * 1024L * 1024L * 1024L * 1024L));
    }

    @Test
    public void test_formatDecimalBytes_exabytes() {
        assertEquals("1.00EB", Formatter.formatDecimalBytes(1000L * 1000L * 1000L * 1000L * 1000L * 1000L));
    }
}