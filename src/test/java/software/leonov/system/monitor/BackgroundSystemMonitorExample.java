package software.leonov.system.monitor;

import static java.lang.System.out;

import java.time.Duration;

public class BackgroundSystemMonitorExample {

    public static void main(String... strings) throws InterruptedException {

        try (final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.updateEvery(Duration.ofSeconds(10)).registerUpdateListener((cpu, memory) -> {
            System.out.printf("%s%n%s%n", cpu, memory);
        }).start()) { // Don't forget to start the monitor

            Thread.sleep(50000);

        }
    }

}
