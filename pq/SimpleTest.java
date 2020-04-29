package pq;

import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {

    @Test
    public void testCoarseGrained_basic_sequential() {
        pq.IPriorityQueue<Integer> pq = new pq.CoarseGrainedPriorityQueue<Integer>(2000);
        test_basic_sequential(pq); 
    }

    @Test
    public void testFineGrained_basic_sequential() {
        pq.IPriorityQueue<Integer> pq = new pq.FineGrainedPriorityQueue<Integer>(2000);
        test_basic_sequential(pq);
    }

    @Test
    public void testLFPriorityQueue_basic_sequential() {
        pq.IPriorityQueue<Integer> pq = new pq.LFPriorityQueue<Integer>();
        test_basic_sequential(pq);
    }

    @Test
    public void testCoarseGrained_basic_concurrent() {
        pq.IPriorityQueue<Integer> pq = new pq.CoarseGrainedPriorityQueue<Integer>(1000000);
        test_basic_concurrent(6, 10000, pq);
    }

    @Test
    public void testFineGrained_basic_concurrent() {
        pq.IPriorityQueue<Integer> pq = new pq.FineGrainedPriorityQueue<Integer>(1000000);
        test_basic_concurrent(6, 10000, pq);
    }

    @Test
    public void testLFPriorityQueue_basic_concurrent() {
        pq.IPriorityQueue<Integer> pq = new pq.LFPriorityQueue<Integer>();
        test_basic_concurrent(6, 10000, pq);
    }

    public void test_basic_sequential(pq.IPriorityQueue<Integer> pq) {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            int num = r.nextInt(1000);
            // System.out.println("Insert: " + num);
            pq.insert(num, num);
        }
        int min = Integer.MIN_VALUE;
        int temp;
        for(int i = 0; i < 1000; i++) {
            temp = pq.removeMin();
            // System.out.println("Remove: " + temp);
            Assert.assertTrue(min <= temp);
            min = temp;
        }
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
        
        Random rand;
        int count;
        int id;
        pq.IPriorityQueue<Integer> pq;

        MyThread(int id, int count, pq.IPriorityQueue<Integer> pq) {
            this.id = id;
            this.rand = new Random();    
            this.count = count;
            this.pq = pq;
        }

        @Override
        public void run() {
            for (int i = 0; i <= count; ++i) {
                int num = rand.nextInt(count);
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