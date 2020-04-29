package pq;

import org.junit.Assert;
import org.junit.Test;

public class SequentialTest {

    // Sequential Tests
    @Test
    public void testCoarseGrained_basic_sequential() {
        pq.IPriorityQueue<Integer> pq = new pq.CoarseGrainedPriorityQueue<Integer>(200000);
        test_basic_sequential(pq); 
    }

    @Test
    public void testFineGrained_basic_sequential() {
        pq.IPriorityQueue<Integer> pq = new pq.FineGrainedPriorityQueue<Integer>(200000);
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
}