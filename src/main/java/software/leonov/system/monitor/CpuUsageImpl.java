package software.leonov.system.monitor;

/**
 * An implementation of the {@link CpuUsage} interface.
 * 
 * @author Zhenya Leonov
 */
final class CpuUsageImpl implements CpuUsage {

    private final double processCpu;
    private final double systemCpu;
    private final double systemLoadAverage;
    private final double avgProcessCpuLoad;
    private final double avgSystemCpuLoad;
    private final double maxProcessCpu;
    private final double maxSystemCpu;

    CpuUsageImpl(final double processCpu, final double systemCpu, final double systemLoadAverage, final double avgProcessCpuLoad, final double avgSystemCpuLoad, final double maxProcessCpu, final double maxSystemCpu) {
        this.processCpu        = processCpu;
        this.systemCpu         = systemCpu;
        this.systemLoadAverage = systemLoadAverage;
        this.avgProcessCpuLoad = avgProcessCpuLoad;
        this.avgSystemCpuLoad  = avgSystemCpuLoad;
        this.maxProcessCpu     = maxProcessCpu;
        this.maxSystemCpu      = maxSystemCpu;
    }

    @Override
    public double getProcessCpuLoad() {
        return processCpu;
    }

    @Override
    public double getSystemCpuLoad() {
        return systemCpu;
    }

    @Override
    public double getSystemLoadAverage() {
        return systemLoadAverage;
    }

    @Override
    public double getAverageProcessCpuLoad() {
        return avgProcessCpuLoad;
    }

    @Override
    public double getAverageSystemCpuLoad() {
        return avgSystemCpuLoad;
    }

    @Override
    public double getMaxProcessCpuLoad() {
        return maxProcessCpu;
    }

    @Override
    public double getMaxSystemCpuLoad() {
        return maxSystemCpu;
    }

    @Override
    public String toString() {
        return String.format("[processCpu=%s, systemCpu=%s, systemLoadAverage=%s, avgProcessCpuLoad=%s, avgSystemCpuLoad=%s, maxProcessCpu=%s, maxSystemCpu=%s]", processCpu, systemCpu, systemLoadAverage, avgProcessCpuLoad, avgSystemCpuLoad,
                maxProcessCpu, maxSystemCpu);
    }

}
