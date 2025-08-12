Java System Monitor
===================
A lightweight library for monitoring Java Virtual Machine (JVM) and system-level resource usage such as CPU and memory in Java.

Overview
--------
Java System Monitor provides a simple and efficient way to monitor system resources in Java applications. It offers several monitoring strategies to suit different use cases, from background monitoring to on-demand resource checks.

The library is designed to be lightweight, easy to use, and performant, making it suitable for both development and production environments.

Usage example
-------------

```java
import static software.leonov.system.monitor.util.Formatter.*;
...
final String availableMemory = formatDecimalBytes(SystemMonitor.getAvailableMemory());
...
try (SystemMonitor monitor = BackgroundSystemMonitor
                               .updateEvery(Duration.ofSeconds(1))
                               .onUpdate((cpu, memory) -> {
                                   final String cpuLoad = formatPercent(cpu.getSystemCpuLoad());
                                   final String usedMemory = formatDecimalBytes(memory.getUsedMemory());
                                   logger.info("Current CPU load: %s", cpuLoad);
                                   logger.info("Currently using memory: %s out of %s", usedMemory, availableMemory);
                               })
                               .start()) { // Don't forget to start the monitor

    // Perform CPU and memory intensive tasks

    final CpuUsage cpu = monitor.getCpuUsage();
    final MemoryUsage memory = monitor.getMemoryUsage();

    logger.info("Average CPU load: %s", formatPercent(cpu.getAverageSystemCpuLoad()));
    logger.info("Maximum used memory: %s", formatDecimalBytes(memory.getMaxUsedMemory()));
    
} // Automatically close/stop the monitor
```

Documentation
-------------
Please refer to the [Wiki](https://github.com/zleonov/java-system-monitor/wiki) for details, specifications, API examples, and FAQ.

The latest API documentation can be accessed [here](https://zleonov.github.io/java-system-monitor/api/latest).

Requirements
------------
- Java 8 or higher

Similar libraries
-----------------
- [OSHI](https://github.com/oshi/oshi) - A free JNA-based (native) Operating System and Hardware Information library for Java.
- [Sigar](https://github.com/hyperic/sigar) - System Information Gatherer And Reporter (no longer updated).