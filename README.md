# Multicore Term Project
Concurrent Priority Queue implementation (2020 EE 361C Multicore)

### Authors
Benson Huang, Wesley Klock, Steven (Zijian) Wang, Kyle Zhou

# Priority Queue Implementations
- Coarse-Grained Lock Priority Queue 
- Fine-Grained Lock Priority Queue
- Lock-Free Priority Skip Queue

We implemented various Lock Based and Lock Free Priority Queue implementations, with a test suite that automatically compares performance benchmarks.
We tested using different thread counts and heavy Insert/RemoveMin function calls.One notable feature is the generic implementation of our Priority Queue allows any data structure to be used, as long as it is given a priority. 

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

## running tests:
`mvn test`

### Running individual tests:
`mvn test -Dtest=SequentialTest`
`mvn test -Dtest=ConcurrentTest`


### Concurrent Test Interpretation/Example run
Our `mvn test -Dtest=ConcurrentTest` tests perform 60,000 insertions and deletions divided over 1 to 6 threads.

```json
[INFO] Running pq.ConcurrentTest
Lock Free Skip PQ
        Thread Count: [1], Work per thread: [60000] insert/removals.
        time: [1173] ms
        Thread Count: [2], Work per thread: [30000] insert/removals.
        time: [642] ms
        Thread Count: [3], Work per thread: [20000] insert/removals.
        time: [566] ms
        Thread Count: [4], Work per thread: [15000] insert/removals.
        time: [273] ms
        Thread Count: [5], Work per thread: [12000] insert/removals.
        time: [181] ms
        Thread Count: [6], Work per thread: [10000] insert/removals.
        time: [183] ms
Course Grained Lock PQ
        Thread Count: [1], Work per thread: [60000] insert/removals.
        time: [1313] ms
        Thread Count: [2], Work per thread: [30000] insert/removals.
        time: [880] ms
        Thread Count: [3], Work per thread: [20000] insert/removals.
        time: [333] ms
        Thread Count: [4], Work per thread: [15000] insert/removals.
        time: [272] ms
        Thread Count: [5], Work per thread: [12000] insert/removals.
        time: [225] ms
        Thread Count: [6], Work per thread: [10000] insert/removals.
        time: [225] ms
Fine Grained Lock PQ
        Thread Count: [1], Work per thread: [60000] insert/removals.
        time: [2322] ms
        Thread Count: [2], Work per thread: [30000] insert/removals.
        time: [859] ms
        Thread Count: [3], Work per thread: [20000] insert/removals.
        time: [419] ms
        Thread Count: [4], Work per thread: [15000] insert/removals.
        time: [227] ms
        Thread Count: [5], Work per thread: [12000] insert/removals.
        time: [195] ms
        Thread Count: [6], Work per thread: [10000] insert/removals.
        time: [149] ms
```