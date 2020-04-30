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
Our `mvn test -Dtest=ConcurrentTest` tests perform 30,000 insertions and deletions divided over 1 to 6 threads.
These values are modifiable in `ConcurrentTest.java`

```json
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running pq.ConcurrentTest
Lock Free Skip PQ
        Thread Count: [1], Work per thread: [30000] insert/removals.
        Time: [489.798] ms
        Thread Count: [2], Work per thread: [15000] insert/removals.
        Time: [220.215] ms
        Thread Count: [3], Work per thread: [10000] insert/removals.
        Time: [132.187] ms
        Thread Count: [4], Work per thread: [7500] insert/removals.
        Time: [175.080] ms
        Thread Count: [5], Work per thread: [6000] insert/removals.
        Time: [76.647] ms
        Thread Count: [6], Work per thread: [5000] insert/removals.
        Time: [119.253] ms
Lock Free Linked PQ
        Thread Count: [1], Work per thread: [30000] insert/removals.
        Time: [3645.382] ms
        Thread Count: [2], Work per thread: [15000] insert/removals.
        Time: [1314.595] ms
        Thread Count: [3], Work per thread: [10000] insert/removals.
        Time: [780.029] ms
        Thread Count: [4], Work per thread: [7500] insert/removals.
        Time: [504.773] ms
        Thread Count: [5], Work per thread: [6000] insert/removals.
        Time: [385.265] ms
        Thread Count: [6], Work per thread: [5000] insert/removals.
        Time: [310.151] ms
Course Grained Lock PQ
        Thread Count: [1], Work per thread: [30000] insert/removals.
        Time: [17.644] ms
        Thread Count: [2], Work per thread: [15000] insert/removals.
        Time: [10.171] ms
        Thread Count: [3], Work per thread: [10000] insert/removals.
        Time: [12.556] ms
        Thread Count: [4], Work per thread: [7500] insert/removals.
        Time: [7.512] ms
        Thread Count: [5], Work per thread: [6000] insert/removals.
        Time: [8.713] ms
        Thread Count: [6], Work per thread: [5000] insert/removals.
        Time: [7.515] ms
Fine Grained Lock PQ
        Thread Count: [1], Work per thread: [30000] insert/removals.
        Time: [58.806] ms
        Thread Count: [2], Work per thread: [15000] insert/removals.
        Time: [68.093] ms
        Thread Count: [3], Work per thread: [10000] insert/removals.
        Time: [123.348] ms
        Thread Count: [4], Work per thread: [7500] insert/removals.
        Time: [81.265] ms
        Thread Count: [5], Work per thread: [6000] insert/removals.
        Time: [89.369] ms
        Thread Count: [6], Work per thread: [5000] insert/removals.
        Time: [82.583] ms
```
