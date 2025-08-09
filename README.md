Java System Monitor
===================
A lightweight library for monitoring Java Virtual Machine (JVM) and system-level resource usage such as CPU and memory in Java.

Overview
--------
Java System Monitor provides a simple and efficient way to monitor system resources in Java applications. It offers several monitoring strategies to suit different use cases, from background monitoring to on-demand resource checks.

The library is designed to be lightweight, easy to use, and performant, making it suitable for both development and production environments.

Key features:
- CPU usage monitoring (process and system-wide)
- Memory usage monitoring with historical tracking
- Multiple monitoring strategies (lazy, background)
- Minimal overhead
- Thread-safe implementations
- 100% pure Java, no external dependencies

Usage Example
-------------

```java

import static software.leonov.system.monitor.util.Formatter.*;

...

System.out.println("Available processors: " + SystemMonitor.getAvailableProcessors());

final String availableMemory = formatDecimalBytes(SystemMonitor.getAvailableMemory());
System.out.println("Available memory: " + availableMemory);

try (final SystemMonitor monitor = BackgroundSystemMonitor
                                             .refreshEvery(Duration.ofSeconds(1))
                                             .start()) { // Don't forget to start() the monitor

    // do work

    CpuUsage    cpu    = monitor.getCpuUsage();
    MemoryUsage memory = monitor.getMemoryUsage();

    System.out.println("Current CPU load: " + formatPercent(cpu.getSystemCpuLoad()));
    System.out.println("Currently using memory: " + formatDecimalBytes(memory.getUsedMemory()) +
                                                                 " out of " + availableMemory);

    // do work

    cpu    = monitor.getCpuUsage();
    memory = monitor.getMemoryUsage();

    System.out.println("Average CPU load: " + formatPercent(cpu.getAverageSystemCpuLoad()));
    System.out.println("Maximum used memory: " + formatDecimalBytes(memory.getMaxUsedMemory()));

} // Automatically stop() the monitor
```

Documentation
-------------
The latest API documentation can be accessed [here](https://zleonov.github.io/java-system-monitor/api/latest).

Requirements
------------
- Java 8 or higher

Similar Libraries
-----------------
- [OSHI](https://github.com/oshi/oshi) - A free JNA-based (native) Operating System and Hardware Information library for Java.
- [Sigar](https://github.com/hyperic/sigar) - System Information Gatherer And Reporter.