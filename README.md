# Project Loom Comparison (Spring Boot Edition)

This project has the goal to replicate the great results that
[@ebarlas](https://github.com/ebarlas) achieved in his
[Project Loom Comparison](https://github.com/ebarlas/project-loom-comparison)
with [Microhttp](https://github.com/ebarlas/microhttp) but with the more
widespread stack of Spring Boot, Project Reactor, Netty, Jetty, and Tomcat.

It compares different methods for achieving scalable concurrency with minimal
Spring Boot apps:

- Platform OS threads used in a Servlet app written with Spring Web MVC.
- Virtual threads used in a Servlet app written with Spring Web MVC.
- Asynchronous programming used in a reactive app written with Spring Webflux.

A very similar model as in
[Project Loom Comparison](https://github.com/ebarlas/project-loom-comparison)
is used.

A user sends requests against a frontend server, which sends three HTTP
requests in succession to a backend server. Each of the latter two backend
calls requires context the previous call. Each backend call introduces 300 ms
second of latency. Thus, the target latency from user to frontend service is
900 ms. The server latency is overwhelmingly due to wait time and threading
overhead.

# Tomcat, Jetty and Loom

As [Cay Horstmann](https://horstmann.com) very nicely shows in a recent
[blog post](https://horstmann.com/unblog/2022-04-15/index.html), virtual
threads cannot effectively be used with Tomcat at the moment. Broadly speaking,
the problem is that with Tomcat there is blocking I/O in `synchronized` blocks,
which pins the virtual threads to their carrier threads. In the sample
application in this repository, this limits the number of concurrent requests
to the size of the fork join pool.

Virtual Threads can however be used with Jetty if a suitable instance of
[ThreadPool](https://www.eclipse.org/jetty/javadoc/jetty-10/org/eclipse/jetty/util/thread/ThreadPool.html) is provided as 
[Mark Reinhold showed at Devoxx UK 2019](https://www.youtube.com/watch?v=kpio9jFhpD8).
The same `ThreadPool` is used in the sample application in this repository and
seems to work reasonably well with early access builds of JDK 19.

# Experiment

Experiments were conducted on EC2 instances:

- 1 instance of type c5.4xlarge (16 vCPUs / 32 GB RAM)
- 2 instances of type c5.2xlarge (8 vCPUs / 16 GB RAM)
- Amazon Linux 2 with Linux Kernel 5.10, AMI ami-09439f09c55136ecf
- Eclipse Temurin JDK 17.0.3
- Open JDK 19 Early Access Build 22 from https://jdk.java.net/19/

The applications are written with

- Spring 2.6.8 (including its dependency management except for Jetty)
- Jetty 10.0.9 (instead of Jetty 9 which Spring Boot 2.x suggests only due to its Java 8 baseline)
- Gradle 7.4.1

With JDK 17 as command line default, the fat jars of the applications can be
built with `./gradlew bootJar` and an optional `--no-daemon` for remote hosts.
The Gradle wrapper will download the appropriate Gradle version and auto-detect
JDK 19 for its toolchain if the JDK has been downloaded and placed in one of
[the usual paths](https://docs.gradle.org/current/userguide/toolchains.html#sec:auto_detection).

## Apache Bench

ApacheBench is an HTTP benchmarking tool that is packaged with the Apache HTTP 
server. It sends the indicated number of requests continuously using a 
specified number of persistent connections.

The following concurrency levels and workloads were tested from one of the 
c5.2xlarge instances:

- `-c 1000 -n 120000`
- `-c 5000 -n 600000`
- `-c 10000 -n 1200000`
- `-c 15000 -n 1800000`
- `-c 20000 -n 2400000`

with commands such as

```
ab -kl -c 20000 -n 2400000 http://172.31.4.14:8080/capabilities
```

To allow Apache Bench to send a high number of concurrent requests, the ephemeral port
range of the host was extended by setting

```
net.ipv4.ip_local_port_range = 9000 65535
```

in `/etc/sysctl.conf`.

## Frontend

The frontend web server receives connections and requests from ApacheBench. For
each request received, it makes three calls in succession to the backend web 
server. Each backend call has a configured latency of 300 ms, so the 
target latency at the frontend web server is 900 ms.

It's implemented in three different flavors has been run on the larger c5.4xlarge instance.

- Servlet app with Spring Web MVC and platform threads
- Servlet app with Spring Web MVC and Loom's virtual threads
- Reactive with Spring Webflux and Netty

### Servlet with Platform Threads

The servlet frontend was compiled and run with the early access JDK 19 build,
and given an ample amount of threads when running with platform threads.

```
java -Xmx12g -jar servlet-frontend/build/libs/servlet-frontend.jar --server.jetty.threads.max=25000 --backend.url=http://<backend ip>:8080 
```

### Servlet with Virtual Threads

The servlet frontend was compiled and run with the early access JDK 19 build,
and given an ample amount of threads:

```
java --enable-preview -Xmx12g -jar servlet-frontend/build/libs/servlet-frontend.jar --spring.profiles.active=loom --backend.url=http://<backend ip>:8080 
```

### Reactive

The reactive frontend was compiled and run with JDK 17.0.3.

```
java -Xmx12g -jar reactive-frontend/build/libs/reactive-frontend.jar --backend.url=http://<backend ip>:8080
```


## Backend

The backend web server receives connections and requests from the frontend web
server. It responds to each request after a configured delay of 300 ms.

It's implemented as a Spring Boot application with Webflux and Netty. It was
compiled and run with JDK 17.0.3 on one of the two c5.2xlarge instances.

```
java -Xmx12g -jar reactive-backend/build/libs/reactive-backend.jar
```

# Results

## Response times

The following response metrics are taken from the Apache Bench output of
representative runs. Times are in milliseconds if not explicitly given.

### Servlet with Platform Threads

|Concurreny Level| 20,000    | 15,000    | 10,000    | 5,000   | 1,000   |
|---|-----------|-----------|-----------|---------|---------|
|Requests| 2,400,000 | 1,800,000 | 1,200,000 | 600,000 | 120,000 |
|Time taken for tests [s]|157.658|115.331|111.523|109.883|109.429|
|Requests per second|15222.83|15607.22|10760.11|5460.33|1096.6|
|Time per request|1313.817|961.094|929.358|915.695|911.907|
|Min. total connection time|902|902|902|902|902|
|Mean total connection time|1277|949|917|905|904|
|Standard deviation|1347|64.8|36.3|12.8|5.6|
|50% percentile|1133|925|905|903|903|
|66% percentile|1207|944|909|903|903|
|75% percentile|1258|961|915|903|904|
|80% percentile|1292|974|920|904|904|
|90% percentile|1387|1017|940|906|904|
|95% percentile|1475|1064|970|914|905|
|98% percentile|1603|1142|1039|938|917|
|99% percentile|1792|1253|1119|976|944|
|100% percentile|17918|1750|1433|1089|1133|

### Servlet with Virtual Threads

|Concurreny Level| 20,000    | 15,000    | 10,000    | 5,000   | 1,000   |
|---|-----------|-----------|-----------|---------|---------|
|Requests| 2,400,000 | 1,800,000 | 1,200,000 | 600,000 | 120,000 |
|Time taken for tests [s]| 118.725   | 113.172   | 110.606   | 109.662 | 109.355 |
|Requests per second| 20214.73  | 15904.95  | 10849.33  | 5471.34 | 1097.34 |
|Time per request| 989.378   | 943.102   | 921.716   | 913.854 | 911.293 |
|Min. total connection time| 902       | 902       | 902       | 902     | 902     |
|Mean total connection time| 977       | 931       | 910       | 905     | 904     |
|Standard deviation| 56        | 37.3      | 20.7      | 9.1     | 2.6     |
|50% percentile| 967       | 919       | 903       | 902     | 903     |
|66% percentile| 987       | 929       | 904       | 903     | 903     |
|75% percentile| 1001      | 940       | 906       | 903     | 904     |
|80% percentile| 1011      | 947       | 909       | 904     | 904     |
|90% percentile| 1039      | 969       | 923       | 906     | 905     |
|95% percentile| 1068      | 989       | 937       | 914     | 906     |
|98% percentile| 1117      | 1018      | 958       | 925     | 914     |
|99% percentile| 1204      | 1056      | 999       | 942     | 918     |
|100% percentile| 1552      | 1415      | 1195      | 1047    | 929     |

### Reactive

|Concurreny Level| 20,000    | 15,000    | 10,000    | 5,000   | 1,000   |
|---|-----------|-----------|-----------|---------|---------|
|Requests| 2,400,000 | 1,800,000 | 1,200,000 | 600,000 | 120,000 |
|Time taken for tests [s]|136.547|115.811|110.788|109.706|109.612|
|Requests per second|17576.43|15542.62|10831.49|5469.16|1094.77|
|Time per request|1137.888|965.088|923.234|914.217|913.437|
|Min. total connection time|902|902|902|902|902|
|Mean total connection time|1125|953|912|905|904|
|Standard deviation|128.8|63.2|28.6|12.6|3|
|50% percentile|1106|934|904|903|903|
|66% percentile|1151|953|906|903|903|
|75% percentile|1183|968|909|903|903|
|80% percentile|1205|978|912|903|904|
|90% percentile|1274|1012|927|907|904|
|95% percentile|1347|1054|941|914|906|
|98% percentile|1461|1148|964|926|912|
|99% percentile|1565|1256|1043|963|916|
|100% percentile|2675|1551|1342|1111|1163|

## Resource Use

TBA
