# Multicore Term Project
Concurrent Priority Queue implementation (2020 - EE 361C Multicore)

### Authors
Benson Huang, Wesley Klock, Steven (Zijian) Wang, Kyle Zhou

# Priority Queue Implementations
- Coarse-Grained Lock Priority Queue (Array based)
- Fine-Grained Lock Priority Queue (Array based)
- Lock-Free Priority Skip Queue (Skip List based)
- Lock-Free Priority Linked Queue (Ordered Linked List based... *Quite Slow*)

We found that for large inputs 

We implemented various Lock Based and Lock Free Priority Queue implementations, with a test suite that automatically compares performance benchmarks.
We tested using different thread counts and heavy Insert/RemoveMin function calls. One notable feature is the generic implementation of our Priority Queue allows any data structure to be used, as long as a priority is provided. 

The interface we are using for all our queues are shared: 
```java
public interface IPriorityQueue<T> {
    public void insert(T item, int priority);    
    public T removeMin();
}
```

### Resources
“The Art of Multiprocessor Programming” by Maurice Herlihy & Nir Shavit.

# How to run program with mvn commands

## system Requirements
- Maven (https://maven.apache.org/install.html)
- Java 8+ (https://www.java.com/en/download/)

## Running command line:

```java
Concurrent Priority Queue Program takes 3 arguments

args[0]:
        -c      Coarse Grained PQ
        -f      Fine Grained PQ
        -s      Lock Free Priority SkipQueue
        -l      Lock Free Priority LinkedQueue

args[1]:
        #       Number of Threads

args[2]:
        #       Amount of work per threads

Examples:
        mvn exec:java -Dexec.mainClass=pq.App -Dexec.args="-c 3 100"
        mvn exec:java -Dexec.mainClass=pq.App -Dexec.args="-s 4 200"
```

### Example Command Line Tests:

`mvn exec:java -Dexec.mainClass=pq.App -Dexec.args="-c 6 20"`

`mvn exec:java -Dexec.mainClass=pq.App -Dexec.args="-f 6 20"`

`mvn exec:java -Dexec.mainClass=pq.App -Dexec.args="-s 6 20"`

`mvn exec:java -Dexec.mainClass=pq.App -Dexec.args="-l 6 20"`

## running tests:
`mvn test`

### Running individual tests:
`mvn test -Dtest=SequentialTest`

`mvn test -Dtest=ConcurrentTest`


### Concurrent Test Interpretation/Example run
Our `mvn test -Dtest=ConcurrentTest` tests perform 30,000,000 insertions and deletions divided over 1 to 6 threads.
These values are modifiable in `ConcurrentTest.java`

```json
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running pq.ConcurrentTest
Lock Free Skip PQ
        Thread Count: [1], Work per thread: [30000000] insert/removals.
        Time: [1.129] ms
        Thread Count: [2], Work per thread: [15000000] insert/removals.
        Time: [0.231] ms
        Thread Count: [3], Work per thread: [10000000] insert/removals.
        Time: [0.274] ms
        Thread Count: [4], Work per thread: [7500000] insert/removals.
        Time: [4.052] ms
        Thread Count: [5], Work per thread: [6000000] insert/removals.
        Time: [12.401] ms
        Thread Count: [6], Work per thread: [5000000] insert/removals.
        Time: [18.403] ms
Course Grained Lock PQ
        Thread Count: [1], Work per thread: [30000000] insert/removals.
        Time: [4.540] ms
        Thread Count: [2], Work per thread: [15000000] insert/removals.
        Time: [3.973] ms
        Thread Count: [3], Work per thread: [10000000] insert/removals.
        Time: [10.595] ms
        Thread Count: [4], Work per thread: [7500000] insert/removals.
        Time: [5.962] ms
        Thread Count: [5], Work per thread: [6000000] insert/removals.
        Time: [8.723] ms
        Thread Count: [6], Work per thread: [5000000] insert/removals.
        Time: [14.259] ms
Fine Grained Lock PQ
        Thread Count: [1], Work per thread: [30000000] insert/removals.
        Time: [4.450] ms
        Thread Count: [2], Work per thread: [15000000] insert/removals.
        Time: [12.404] ms
        Thread Count: [3], Work per thread: [10000000] insert/removals.
        Time: [23.186] ms
        Thread Count: [4], Work per thread: [7500000] insert/removals.
        Time: [28.726] ms
        Thread Count: [5], Work per thread: [6000000] insert/removals.
        Time: [10.367] ms
        Thread Count: [6], Work per thread: [5000000] insert/removals.
        Time: [18.528] ms
```
