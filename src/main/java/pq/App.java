package pq;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        if (args.length < 3) {
            System.out.println("Concurrent Priority Queue Program takes 3 arguments\n");
            System.out.println("args[0]:");
            System.out.println("\t-c\tCoarse Grained PQ");
            System.out.println("\t-f\tFine Grained PQ");
            System.out.println("\t-s\tLock Free Priority SkipQueue");
            System.out.println("\t-l\tLock Free Priority LinkedQueue");
            System.out.println("args[1]:\n\t#\tNumber of Threads");
            System.out.println("args[2]:\n\t#\tAmount of work per threads\n");
            System.out.println("Examples:");
            System.out.println("\tmvn exec:java -Dexec.mainClass=pq.App -Dexec.args=\"-c 3 100\"");
            System.out.println("\tmvn exec:java -Dexec.mainClass=pq.App -Dexec.args=\"-s 4 200\"");
            return;
        }

        IPriorityQueue<Integer> pq;
        String name = "PriorityQueue";
        switch(args[0]) {
            case "-c":
                name = "Coarse Grained Priority Queue";
                pq = new CoarseGrainedPriorityQueue<>();
                break;
            case "-f":
                name = "Fine Grained Priority Queue";
                pq = new FineGrainedPriorityQueue<>();
                break;
            case "-s":
                name = "Lock Free Priority SkipQueue";
                pq = new LFPrioritySkipQueue<>();
                break;
            case "-l":
                name = "Lock Free Priority LinkedQueue";
                pq = new LFPriorityLinkedQueue<>();
                break;
            default:
                System.out.println("Wrong flag used. Only one of \'-c\', \'-f\', \'-s\', \'-l\' should be used.");
                return;
        }

        int threadNums = Integer.parseInt(args[1]);
        int threadAmount = Integer.parseInt(args[2]);
        long startTime = System.nanoTime();
        makeThread(threadNums, threadAmount, pq);
        long endTime = System.nanoTime();
        System.out.println();
        System.out.println(name);
        System.out.println("Thread Count: [" + threadNums + "], Work per thread: [" + threadAmount + "] insert/removals.");
        System.out.println("Time in microseconds: [" + ((endTime - startTime) / 1000) + "]");
    }

    private static void makeThread(int threadNums, int threadAmount, pq.IPriorityQueue<Integer> pq) {
        ExecutorService executor = Executors.newFixedThreadPool(threadNums);
        for (int i = 0; i < threadNums; i++) {
            executor.execute(new MyThread(i, threadAmount, pq));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {

        }
    }

    private static class MyThread implements Runnable {

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
                System.out.println(id + " Insert: " + num);
                pq.insert(num, num);
            }

            for (int i = 0; i < count; ++i) {
                int num = pq.removeMin();
                System.out.println(id + " Remove: " + num);
            }
        }
    }
}
