package pq;

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
            System.out.println("args[0]:\n\t-c\tCoarse Grained PQ\n\t-f\tFine Grained PQ\n\t-lf\tLock Free PQ");
            System.out.println("args[1]:\n\t#\tNumber of Threads");
            System.out.println("args[2]:\n\t#\tAmount of work per threads\n");
            System.out.println("Examples:");
            System.out.println("\tmvn exec:java -Dexec.mainClass=pq.App -Dexec.args=\"-c 3 100\"");
            System.out.println("\tmvn exec:java -Dexec.mainClass=pq.App -Dexec.args=\"-lf 4 200\"");
            return;
        }

        IPriorityQueue<Integer> pq;
        switch(args[0]) {
            case "-c":
                pq = new CoarseGrainedPriorityQueue<>();
                break;
            case "-f":
                pq = new FineGrainedPriorityQueue<>();
                break;
            case "-lf":
                pq = new LFPrioritySkipQueue<>();
                break;
            default:
                System.out.println("Wrong flag used. Only one of \'-c\', \'-f\', \'-lf\' should be used");
                return;
        }

        int threadNums = Integer.parseInt(args[1]);
        int threadAmount = Integer.parseInt(args[2]);
        long startTime = System.nanoTime();
        makeThread(threadNums, threadAmount, pq);
        long endTime = System.nanoTime();
        System.out.println("Thread Count: [" + threadNums + "], Work per thread: [" + threadAmount + "] insert/removals.");
        System.out.println("time: [" + ((endTime - startTime) / 1000000) + "] ms");
    }

    private static void makeThread(int threadNums, int threadAmount, pq.IPriorityQueue<Integer> pq) {
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
