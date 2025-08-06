Java System Monitor
===================
A lightweight library for monitoring Java Virtual Machine (JVM) and system-level resource usage such as CPU and memory in Java.

Overview
--------
Java System Monitor provides a simple and efficient way to monitor system resources in Java applications. It offers multiple monitoring strategies to suit different use cases, from background monitoring to on-demand resource checks.

The library is designed to be lightweight, easy to use, and performant, making it suitable for both development and production environments.

Key features:
- CPU usage monitoring
- Memory usage monitoring  
- Multiple monitoring strategies (lazy, background, unsupported fallback)
- Clean, type-safe API
- Minimal overhead
- Thread-safe implementations

Usage Examples
--------------

### Basic Usage

```java
// Get a system monitor instance
SystemMonitor monitor = SystemMonitor.create();

// Get current CPU usage
CpuUsage cpu = monitor.getCpuUsage();
System.out.println("CPU Usage: " + cpu.getUsage() + "%");

// Get current memory usage
MemoryUsage memory = monitor.getMemoryUsage();
System.out.println("Memory Usage: " + memory.getUsage() + "%");
System.out.println("Available Memory: " + memory.getAvailable() + " bytes");
```

### Background Monitoring

```java
// Create a background monitor that updates every 5 seconds
SystemMonitor monitor = new BackgroundSystemMonitor(Duration.ofSeconds(5));

// Usage data is automatically updated in the background
CpuUsage cpu = monitor.getCpuUsage();
MemoryUsage memory = monitor.getMemoryUsage();

// Don't forget to stop the background monitor when done
((BackgroundSystemMonitor) monitor).stop();
```

### Lazy Monitoring

```java
// Create a lazy monitor that only updates when requested
SystemMonitor monitor = new LazySystemMonitor(Duration.ofSeconds(1));

// Data is refreshed only if older than the specified interval
CpuUsage cpu = monitor.getCpuUsage(); // Fresh data
CpuUsage cpu2 = monitor.getCpuUsage(); // Cached data (if within 1 second)
```

Documentation
-------------
The latest API documentation can be accessed [here](https://zleonov.github.io/java-system-monitor/api/latest).

Requirements
------------
- Java 8 or higher
- No external dependencies

But if you want something else?
-------------------------------
- [OSHI](https://github.com/oshi/oshi) - A free JNA-based (native) Operating System and Hardware Information library for Java.
- [Sigar](https://github.com/hyperic/sigar) - System Information Gatherer And Reporter.
- [JVM Monitor](https://www.eclipse.org/mat/downloads.php) - Eclipse Memory Analyzer Tool for monitoring JVM memory usage.
- [Micrometer](https://micrometer.io/) - Application metrics facade for the most popular monitoring tools.
- [Dropwizard Metrics](https://metrics.dropwizard.io/) - Capturing JVM- and application-level metrics.