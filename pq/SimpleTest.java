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

    public void test_basic_sequential(IPriorityQueue<Integer> pq) {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            int num = r.nextInt(1000);
            System.out.println("Insert: " + num);
            pq.insert(num, num);
        }
        int min = Integer.MIN_VALUE;
        int temp;
        for(int i = 0; i < 1000; i++) {
            temp = pq.removeMin();
            System.out.println("Remove: " + temp);
            Assert.assertTrue(min <= temp);
            min = temp;
        }
    }
}