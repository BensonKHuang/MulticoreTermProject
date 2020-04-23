package pq;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedPriorityQueue<T> implements pq.IPriorityQueue<T> {

    private static enum Status {
        EMPTY,
        AVAILABLE,
        BUSY
    }

    private class PQNode<S> {
        S item;
        int priority;
        long owner;
        Lock lock;
        Status status;

        public PQNode() {
            this.status = Status.EMPTY;
            this.lock = new ReentrantLock();
        }

        public void set(final S item, final int priority) {
            this.item = item;
            this.priority = priority;
            this.status = Status.BUSY;
            this.owner = Thread.currentThread().getId();
        }

        public void lock() {
            this.lock.lock();
        }
        
        public void unlock() {
            this.lock.unlock();
        }

        public boolean amOwner() {
            return this.owner == Thread.currentThread().getId() &&
                this.status == Status.BUSY;
        }
        
    }

    private final static int ROOT = 1;
    private final static int NO_OWNER = -1;

    private int pos;
    private final PQNode<T>[] pq;
    private Lock pqLock;

    public FineGrainedPriorityQueue(final int capacity) {
        this.pqLock = new ReentrantLock();
        pos = 1;
        pq = (PQNode<T>[]) new PQNode[capacity + 1];
        for (int i = 0; i < capacity + 1; i++) {
            pq[i] = new PQNode<T>();
        }
    }

    private int getLeftChildIndex(final int index) {
        return 2 * index;
    }

    private int getRightChildIndex(final int index) {
        return 2 * index + 1;
    }

    private int getParentIndex(final int index) {
        return index / 2;
    }

    private void swap(final int index1, final int index2) {
        int tempPriority = pq[index1].priority;
        T tempItem = pq[index1].item;
        Status tempStatus = pq[index1].status;
        long tempOwner = pq[index1].owner;
        pq[index1].priority = pq[index2].priority;
        pq[index1].item = pq[index2].item;
        pq[index1].status = pq[index2].status;
        pq[index1].owner = pq[index2].owner;
        pq[index2].priority = tempPriority;
        pq[index2].item = tempItem;
        pq[index2].status = tempStatus;
        pq[index2].owner = tempOwner;
    }
    

    @Override
    public void insert(T item, int priority) {
        
        // Lock whole heap to initialize heap without deadlock
        pqLock.lock(); 
        int child = pos++;
        pq[child].lock();
        pq[child].set(item, priority);
        pqLock.unlock();
        pq[child].unlock();

        while (child > ROOT) {
            int parent = getParentIndex(child);
            pq[parent].lock();
            pq[child].lock();
            int oldChild = child;
            try {
                // Bubble up if possible and priority is higher
                if (pq[parent].status == Status.AVAILABLE && pq[child].amOwner()) {
                    if (pq[child].priority < pq[parent].priority) {
                        swap(child, parent);
                        child = parent;
                    } else {
                        pq[child].status = Status.AVAILABLE;
                        pq[child].owner = NO_OWNER;
                        return;
                    }
                // Some other thread from removeMin() call has already been moved up
                } else if (!pq[child].amOwner()) {
                    child = parent;
                }
            // Always unlock parent/oldChild after each bubble 
            } finally {
                pq[oldChild].unlock();
                pq[parent].unlock();
            }
        }
        // Once child becomes root, set node to available for removal
        if (child == ROOT) {
            pq[ROOT].lock();
            if (pq[ROOT].amOwner()) {
                pq[ROOT].status = Status.AVAILABLE;
                pq[ROOT].owner = NO_OWNER;
            }
            pq[ROOT].unlock();
        }
    }

    @Override
    public T removeMin() {
        pqLock.lock();
        int bottom = --pos;
        pq[ROOT].lock();
        pq[bottom].lock();
        pqLock.unlock();

        // Extract item from PQNode
        T item = pq[ROOT].item;
        pq[ROOT].status = Status.EMPTY;
        pq[ROOT].owner = NO_OWNER;
        // Reset the root node and clear the new bottom
        swap(bottom, ROOT);
        pq[bottom].unlock();

        if (pq[ROOT].status == Status.EMPTY) {
            pq[ROOT].unlock();
            return item;
        }

        pq[ROOT].status = Status.AVAILABLE;
        int child = 0;
        int parent = ROOT;

        while (parent < pq.length / 2) {
            int left = getLeftChildIndex(parent);
            int right = getRightChildIndex(parent);
            pq[left].lock();
            pq[right].lock();
            if (pq[left].status == Status.EMPTY) {
                pq[right].unlock();
                pq[left].unlock();
                break;
            // Left child swap path
            } else if (pq[right].status == Status.EMPTY) {
                pq[right].unlock();
                child = left;
            // Right child swap path
            } else {
                pq[left].unlock();
                child = right;
            }
            if (pq[child].priority < pq[parent].priority && pq[child].status != Status.EMPTY) {
                swap(parent, child);
                pq[parent].unlock();
                parent = child;
            } else {
                pq[child].unlock();
                break;
            }
        }
        pq[parent].unlock();
        return item;
    }
}
