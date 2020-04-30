package pq;

import org.junit.Test;

// Concurrent Tests
public class ConcurrentTest {
    final static int THREADMAX = 6;
    final static int THREADTOTAL = 60000;
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

    //Helper
    public void test_basic_concurrent(int threadNums, int threadAmount, pq.IPriorityQueue<Integer> pq) {
        long startTime = System.nanoTime();
        makeThread(threadNums, threadAmount, pq);
        long endTime = System.nanoTime();
        System.out.println("\tThread Count: [" + threadNums + "], Work per thread: [" + threadAmount + "] insert/removals.");
        System.out.println("\ttime: [" + ((endTime - startTime) / 1000000) + "] ms");
    }

    private void makeThread(int threadNums, int threadAmount, pq.IPriorityQueue<Integer> pq) {
        Thread[] threads = new Thread[threadNums];

        for (int i = 0; i < threadNums; i++) {
            threads[i] = new Thread(new MyThread(i, threadAmount, pq));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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