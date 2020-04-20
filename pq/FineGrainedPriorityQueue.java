package pq;

public class FineGrainedPriorityQueue<T> implements pq.IPriorityQueue<T> {

    public class PQNode<S> {
        S item;
        int priority;

        public void set(final S item, final int priority) {
            this.item = item;
            this.priority = priority;
        }
    }

    @Override
    public void insert(T item, int priority) throws HeapFullException {

    }

    @Override
    public T removeMin() throws HeapEmptyException {
        return null;
    }

}