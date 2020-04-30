package pq;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

// Concurrent Tests
public class ConcurrentTest {
    final static int THREADMAX = 6;
    final static int THREADTOTAL = 30000;

    @Test
    public void testCoarseGrained_basic_concurrent() {
        System.out.println("Course Grained Lock PQ");
        pq.IPriorityQueue<Integer> pq;
        for (int i = 1; i <= THREADMAX; i++) {
            pq = new pq.CoarseGrainedPriorityQueue<Integer>();
            test_basic_concurrent(i, THREADTOTAL / i, pq);
        }
    }

    @Test
    public void testFineGrained_basic_concurrent() {
        System.out.println("Fine Grained Lock PQ");
        pq.IPriorityQueue<Integer> pq;
        for (int i = 1; i <= THREADMAX; i++) {
            pq = new pq.FineGrainedPriorityQueue<Integer>();
            test_basic_concurrent(i, THREADTOTAL / i, pq);
        }
    }

    @Test
    public void testLFSkipQueue_basic_concurrent() {
        System.out.println("Lock Free Skip PQ");
        pq.IPriorityQueue<Integer> pq;
        for (int i = 1; i <= THREADMAX; i++) {
            pq = new pq.LFPrioritySkipQueue<Integer>();
            test_basic_concurrent(i, THREADTOTAL / i, pq);
        }
    }

    @Test
    public void testLFLinkedQueue_basic_concurrent() {
        System.out.println("Lock Free Linked PQ");
        pq.IPriorityQueue<Integer> pq;
        for (int i = 1; i <= THREADMAX; i++) {
            pq = new pq.LFPriorityLinkedQueue<Integer>();
            test_basic_concurrent(i, THREADTOTAL / i, pq);
        }
    }

    // Helper
    public void test_basic_concurrent(int threadNums, int threadAmount, pq.IPriorityQueue<Integer> pq) {
        long startTime = System.nanoTime();
        
        makeThread(threadNums, threadAmount, pq);

        long endTime = System.nanoTime();
        float duration = (endTime - startTime) / 1000000f;
        String durationString = String.format("%.3f", duration);
        System.out.println("\tThread Count: [" + threadNums + "], Work per thread: [" + threadAmount + "] insert/removals.");
        System.out.println("\tTime: [" + durationString + "] ms");
    }

    private void makeThread(int threadNums, int threadAmount, pq.IPriorityQueue<Integer> pq) {
        ExecutorService executor = Executors.newFixedThreadPool(threadNums);
        for (int i = 0; i < threadNums; i++) {
            executor.execute(new MyThread(i, threadAmount, pq));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("hello world!");
        }
    }

    private class MyThread implements Runnable {
        int count;
        int id;
        pq.IPriorityQueue<Integer> pq;

        MyThread(int id, int count, pq.IPriorityQueue<Integer> pq) {
            this.id = id;
            this.count = count;
            this.pq = pq;
        }

        @Override
        public void run() {
            for (int i = 0; i < count; ++i) {
                int num = (id * count) + i;
                // System.out.println(id + " Insert: " + num);
                pq.insert(num, num);
            }
            for (int i = 0; i < count; ++i) {
                int num = pq.removeMin();
                // System.out.println(id + " Remove: " + num);
            }
        }
    }
}