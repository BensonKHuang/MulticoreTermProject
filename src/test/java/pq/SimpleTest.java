package pq;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {

    // Sequential Tests
    @Test
    public void testCoarseGrained_basic_sequential() {
        pq.IPriorityQueue<Integer> pq = new pq.CoarseGrainedPriorityQueue<Integer>();
        test_basic_sequential(pq); 
    }

    @Test
    public void testFineGrained_basic_sequential() {
        pq.IPriorityQueue<Integer> pq = new pq.FineGrainedPriorityQueue<Integer>();
        test_basic_sequential(pq);
    }

    @Test
    public void testLFPriorityQueue_basic_sequential() {
        pq.IPriorityQueue<Integer> pq = new pq.LFPriorityQueue<Integer>();
        test_basic_sequential(pq);
    }

    @Test
    public void testLFPrioritySkipQueue_basic_sequential() {
        pq.IPriorityQueue<Integer> pq = new pq.LFPrioritySkipQueue<Integer>();
        test_basic_sequential(pq);
    }

    // Concurrent Tests

    @Test
    public void testCoarseGrained_basic_concurrent() {
        pq.IPriorityQueue<Integer> pq = new pq.CoarseGrainedPriorityQueue<Integer>();
        test_basic_concurrent(6, 10000, pq);
    }

    @Test
    public void testFineGrained_basic_concurrent() {
        pq.IPriorityQueue<Integer> pq = new pq.FineGrainedPriorityQueue<Integer>();
        test_basic_concurrent(6, 10000, pq);
    }

    @Test
    public void testLFPriorityQueue_basic_concurrent() {
        pq.IPriorityQueue<Integer> pq = new pq.LFPriorityQueue<Integer>();
        test_basic_concurrent(6, 10000, pq);
    }

    @Test
    public void testLFSkipQueue_basic_concurrent() {
        pq.IPriorityQueue<Integer> pq = new pq.LFPrioritySkipQueue<Integer>();
        test_basic_concurrent(6, 10000, pq);
    }


    // Helper Functions
    public void test_basic_sequential(pq.IPriorityQueue<Integer> pq) {
        long startTime = System.nanoTime();

        for (int i = 0; i < 1000; i++) {
            // System.out.println("Insert: " + i);
            pq.insert(i, i);
        }
        int min = Integer.MIN_VALUE;
        int temp;
        for(int i = 0; i < 1000; i++) {
            temp = pq.removeMin();
            // System.out.println("Remove: " + temp);
            Assert.assertTrue(min <= temp);
            min = temp;
        }

        long endTime = System.nanoTime();
        System.out.println("time (ms): " + ((endTime - startTime) / 1000000));
    }

    public void test_basic_concurrent(int threadNums, int threadAmount, pq.IPriorityQueue<Integer> pq) {
        long startTime = System.nanoTime();
        makeThread(threadNums, threadAmount, pq);
        long endTime = System.nanoTime();
        System.out.println("time (ms): " + ((endTime - startTime) / 1000000));
    }

    private void makeThread(int threadNums, int threadAmount, pq.IPriorityQueue<Integer> pq) {
        Thread[] threads = new Thread[threadNums];
        
        for (int i = 0; i < threadNums; i++) {
            threads[i] = new Thread(new MyThread(i, threadAmount, pq));
        }

        for (Thread thread: threads) {
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
            for (int i = 0; i <= count; ++i) {
                int num = id * count + i; 
                // System.out.println(id + " Insert: " + num);
                pq.insert(num, num);
            }

            for (int i = 0; i <= count; i+=2) {
                int num = pq.removeMin();
                // System.out.println(id + "      Remove: " + num);
            }
        }
    }
}