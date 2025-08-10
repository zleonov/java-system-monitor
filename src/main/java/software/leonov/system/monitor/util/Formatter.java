package software.leonov.system.monitor.util;

/**
 * Basic utility methods for formatting CPU and memory usage metrics.
 * <p>
 * This is a convenience class intended for casual use only (primarily for quick testing and debugging).
 * 
 * @author Zhenya Leonov
 */
public final class Formatter {

    private static final String PRE          = "KMGTPE";
    private static final int    DECIMAL_UNIT = 1000;
    private static final int    BINARY_UNIT  = 1024;

    private Formatter() {
    }

    /**
     * Formats the given percent value into a human-readable string.
     * 
     * @param pct the percent value to format
     * @return a formatted string (e.g., 12.75%) or "-1" if the value is negative
     */
    public static String formatPercent(final double pct) {
        return pct < 0 ? "-1" : String.format("%.2f%%", pct);
    }

    /**
     * Formats the given number of bytes into a human-readable string in the binary (1024) base unit.
     *
     * @param bytes the number of bytes to format
     * @return a formatted string (e.g., "1.23 MiB") or "-1" if the value is negative
     */
    public static String formatBinaryBytes(final long bytes) {
        return formatBytes(bytes, BINARY_UNIT);
    }

    /**
     * Formats the given number of bytes into a human-readable string in the decimal (1000) base unit.
     *
     * @param bytes the number of bytes to format
     * @return a formatted string (e.g., "1.23 MB") or "-1" if the value is negative
     */
    public static String formatDecimalBytes(final long bytes) {
        return formatBytes(bytes, DECIMAL_UNIT);
    }

    private static String formatBytes(final long bytes, final int base) {
        if (bytes < 0)
            return "-1";

        if (bytes < base)
            return bytes + " bytes";

        final int    exp     = (int) (Math.log(bytes) / Math.log(base));
        final char   pre     = PRE.charAt(exp - 1);
        final String pattern = base == BINARY_UNIT ? "%.2f%siB" : "%.2f%sB";

        return String.format(pattern, bytes / Math.pow(base, exp), pre);
    }

}
