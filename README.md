# Reactor Benchmarks

These benchmarks measure the relative performance of various components of the Reactor architecture. There are also benchmarks for standard JDK components in different configurations to try and determine what's the fastest way to do various tasks.

### Running

The benchmarks are [JMH](http://openjdk.java.net/projects/code-tools/jmh/) based. To run them, clone the project, then run the `reactor-benchmark.jar` using `java -jar`. For example, to run the `ReactorBenchmarks` which measure the performance of the event publication system, do something like the following:

		> git clone https://github.com/reactor/reactor-benchmark.git
		> cd reactor-benchmark

		> ./gradlew jmhJar && java -jar build/libs/reactor-benchmark-1.1.0.BUILD-SNAPSHOT-jmh.jar '.*Benchmarks.*'
Or
		> ./gradlew jmh
		
Sensible defaults are placed as annotations on the benchmark classes themselves. In many cases, those can be overridden by passing the appropriate CLI parameter. Refer to the JMH documentation for more information on what options are available and what effect they have on the benchmark runs.
