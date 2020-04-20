package pq;

import static org.junit.Assert.fail;

import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

public class SimpleTest {

    @Test
    public void testCoarseGrained_basic_sequential() {
        IPriorityQueue<Integer> pq = new CoarseGrainedPriorityQueue<Integer>(2000);
        try {
            test_basic_sequential(pq);
        } catch (HeapFullException | HeapEmptyException e) {
            fail();
        }
    }

    @Test
    (expected = HeapFullException.class)
    public void testCoarseGrained_capacity() throws HeapFullException {
        int count = 2000;
        IPriorityQueue<Integer> pq = new CoarseGrainedPriorityQueue<Integer>(count);
        test_capacity(pq, count + 1);
    }

    public void test_basic_sequential(IPriorityQueue<Integer> pq) throws HeapFullException, HeapEmptyException {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            int num = r.nextInt(1000);
            pq.insert(num, num);
        }
        int min = Integer.MIN_VALUE;
        int temp;
        for(int i = 0; i < 1000; i++) {
            temp = pq.removeMin();
            Assert.assertTrue(min <= temp);
            min = temp;
        }
    } 

    public void test_capacity(IPriorityQueue<Integer> pq, int count) throws HeapFullException {
        Random r = new Random();
        for (int i = 0; i < count; i++) {
            int num = r.nextInt(1000);
            pq.insert(num, num);
        }
    }
}