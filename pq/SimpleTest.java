package pq;

import static org.junit.Assert.fail;

import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {

    @Test
    public void testCoarseGrained_basic_sequential() {
        IPriorityQueue<Integer> pq = new CoarseGrainedPriorityQueue<Integer>(2000);
        test_basic_sequential(pq); 
    }

    @Test
    public void testFineGrained_basic_sequential() {
        IPriorityQueue<Integer> pq = new FineGrainedPriorityQueue<Integer>(2000);
        test_basic_sequential(pq);
    }

    @Test
    public void testCoarseGrained_basic_concurrent() {
        IPriorityQueue<Integer> pq = new CoarseGrainedPriorityQueue<Integer>(5000);
        test_basic_concurrent(2, 100, pq);
    }

    @Test
    public void testFineGrained_basic_concurrent() {
        IPriorityQueue<Integer> pq = new FineGrainedPriorityQueue<Integer>(5000);
        test_basic_concurrent(2, 2000, pq);
    }

    public void test_basic_sequential(IPriorityQueue<Integer> pq) {
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

    public void test_basic_concurrent(int threadNums, int threadAmount, IPriorityQueue<Integer> pq) {
        makeThread(threadNums, threadAmount, pq);
    }

    private void makeThread(int threadNums, int threadAmount, IPriorityQueue<Integer> pq) { 
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
        IPriorityQueue<Integer> pq;

        MyThread(int id, int count, IPriorityQueue<Integer> pq) {
            this.id = id;
            this.rand = new Random();    
            this.count = count;
            this.pq = pq;
        }

        @Override
        public void run() {
            for (int i = 0; i <= count; ++i) {
                int num = rand.nextInt(count);
                System.out.println(id + " Insert: " + num);
                pq.insert(num, num);
            }

            for (int i = 0; i <= count; i+=2) {
                int num = pq.removeMin();
                System.out.println(id + "      Remove: " + num);
            }
        }
    }
}