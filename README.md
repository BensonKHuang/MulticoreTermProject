# Multicore Term Project
Concurrent Priority Queue implementation (2020 - EE 361C Multicore)

### Authors
Benson Huang, Wesley Klock, Steven (Zijian) Wang, Kyle Zhou

# Priority Queue Implementations
- Coarse-Grained Lock Priority Queue (Array based)
- Fine-Grained Lock Priority Queue (Array based)
- Lock-Free Priority Skip Queue (Skip List based)
- Lock-Free Priority Linked Queue (Ordered Linked List based... *Quite Slow*)

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
Our `mvn test -Dtest=ConcurrentTest` tests perform 300,000 insertions and deletions divided over 1 to 6 threads.
These values are modifiable in `ConcurrentTest.java`

```json
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running pq.ConcurrentTest
Lock Free Skip PQ
        Thread Count: [1], Work per thread: [300000] insert/removals.
        Time in microseconds: [1125]
        Thread Count: [2], Work per thread: [150000] insert/removals.
        Time in microseconds: [284]
        Thread Count: [3], Work per thread: [100000] insert/removals.
        Time in microseconds: [1288]
        Thread Count: [4], Work per thread: [75000] insert/removals.
        Time in microseconds: [13158]
        Thread Count: [5], Work per thread: [60000] insert/removals.
        Time in microseconds: [12762]
        Thread Count: [6], Work per thread: [50000] insert/removals.
        Time in microseconds: [32625]
Course Grained Lock PQ
        Thread Count: [1], Work per thread: [300000] insert/removals.
        Time in microseconds: [351]
        Thread Count: [2], Work per thread: [150000] insert/removals.
        Time in microseconds: [1485]
        Thread Count: [3], Work per thread: [100000] insert/removals.
        Time in microseconds: [47129]
        Thread Count: [4], Work per thread: [75000] insert/removals.
        Time in microseconds: [483]
        Thread Count: [5], Work per thread: [60000] insert/removals.
        Time in microseconds: [527]
        Thread Count: [6], Work per thread: [50000] insert/removals.
        Time in microseconds: [1093]
Fine Grained Lock PQ
        Thread Count: [1], Work per thread: [300000] insert/removals.
        Time in microseconds: [464]
        Thread Count: [2], Work per thread: [150000] insert/removals.
        Time in microseconds: [1852]
        Thread Count: [3], Work per thread: [100000] insert/removals.
        Time in microseconds: [8903]
        Thread Count: [4], Work per thread: [75000] insert/removals.
        Time in microseconds: [1813]
        Thread Count: [5], Work per thread: [60000] insert/removals.
        Time in microseconds: [549709]
        Thread Count: [6], Work per thread: [50000] insert/removals.
        Time in microseconds: [35860]
```
