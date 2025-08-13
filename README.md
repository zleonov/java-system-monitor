Java System Monitor
===================
A lightweight Java library for monitoring Java Virtual Machine (JVM) and system-level CPU and memory usage.

Overview
--------
Java System Monitor provides a simple and efficient way to monitor CPU and memory resources in Java applications. It offers several monitoring strategies to suit different use cases, from background monitoring to on-demand resource checks.

Written in pure Java without JNI or JNA, the library is designed to be lightweight, easy to use, and performant, making it suitable for both development and production environments. It is not designed to substitute comprehensive hardware and platform-specific solutions like [OSHI](https://github.com/oshi/oshi).

Usage example
-------------

```java
import static software.leonov.system.monitor.util.Formatter.*;
import static java.lang.System.out;
...
try (final SystemMonitor monitor = BackgroundSystemMonitor
                    .updateEvery(Duration.ofSeconds(1))
                    .onUpdate((cpu, memory) ->
                        out.printf("process-cpu: %s, system-cpu; %s used-memory: %s%n", formatPercent(cpu.getProcessCpuLoad()), formatPercent(cpu.getSystemCpuLoad()), formatDecimalBytes(memory.getUsedMemory())))
                    .onClose((cpu, memory) ->
                        out.printf("avg. process-cpu: %s, avg. system-cpu %s, max. used-memory: %s%n", formatPercent(cpu.getAverageProcessCpuLoad()), formatPercent(cpu.getAverageSystemCpuLoad()), formatDecimalBytes(memory.getMaxUsedMemory())))
                    .start()) {  // Don't forget to start the monitor

    ... // Do work

} // Automatically close/stop monitor
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