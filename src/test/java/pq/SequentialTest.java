package pq;

import org.junit.Assert;
import org.junit.Test;

public class SequentialTest {

    // Sequential Tests
    @Test
    public void testCoarseGrained_basic_sequential() {
        System.out.println("Coarse Grained Lock PQ");
        pq.IPriorityQueue<Integer> pq = new pq.CoarseGrainedPriorityQueue<Integer>();
        test_basic_sequential(pq); 
    }

    @Test
    public void testFineGrained_basic_sequential() {
        System.out.println("Fine Grained Lock PQ");
        pq.IPriorityQueue<Integer> pq = new pq.FineGrainedPriorityQueue<Integer>();
        test_basic_sequential(pq);
    }

    @Test
    public void testLFLinkedQueue_basic_concurrent() {
        System.out.println("Lock Free Linked PQ");
        pq.IPriorityQueue<Integer> pq = new pq.LFPriorityLinkedQueue<Integer>();
        test_basic_sequential(pq);
    }

    @Test
    public void testLFPrioritySkipQueue_basic_sequential() {
        System.out.println("Lock Free Skip PQ");
        pq.IPriorityQueue<Integer> pq = new pq.LFPrioritySkipQueue<Integer>();
        test_basic_sequential(pq);
    }

    // Helper Functions
    public void test_basic_sequential(pq.IPriorityQueue<Integer> pq) {
        long startTime = System.nanoTime();
        int amount = 30000;
        for (int i = 0; i < amount; i++) {
            // System.out.println("Insert: " + i);
            pq.insert(i, i);
        }
        int min = Integer.MIN_VALUE;
        int temp;
        for(int i = 0; i < amount; i++) {
            temp = pq.removeMin();
            // System.out.println("Remove: " + temp);
            Assert.assertTrue(min <= temp);
            min = temp;
        }

        long endTime = System.nanoTime();
        float duration = (endTime - startTime) / 1000000f;
        String durationString = String.format("%.3f", duration);
        System.out.println("\tWork: [" + amount + "] inserts/removal");
        System.out.println("\tTime: [" + durationString + "] ms");
    }
}