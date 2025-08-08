Java System Monitor
===================
A lightweight library for monitoring Java Virtual Machine (JVM) and system-level resource usage such as CPU and memory in Java.

Overview
--------
Java System Monitor provides a simple and efficient way to monitor system resources in pure Java applications. It offers several monitoring strategies to suit different use cases, from background monitoring to on-demand resource checks.

The library is designed to be lightweight, easy to use, and performant, making it suitable for both development and production environments.

Key features:
- CPU usage monitoring
- Memory usage monitoring  
- Multiple monitoring strategies (lazy, background)
- Minimal overhead
- Thread-safe implementations

Usage Example
-------------

```java
try (final BackgroundSystemMonitor monitor = BackgroundSystemMonitor.refreshEvery(Duration.ofSeconds(1))) {
    // do work

    System.out.println("Current CPU load: " + formatPercent(monitor.getCpuUsage().getSystemCpuLoad()));
    System.out.println("Current used memory: " + formatDecimalBytes(monitor.getMemoryUsage().getUsedMemory()));

    // do work

    System.out.println("Average CPU load: " + formatPercent(monitor.getCpuUsage().getAverageSystemCpuLoad()));
    System.out.println("Maximum used memory: " + formatDecimalBytes(monitor.getMemoryUsage().getMaxUsedMemory()));

}
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