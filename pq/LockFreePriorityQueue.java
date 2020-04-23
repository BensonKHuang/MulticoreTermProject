package pq;

public class LockFreePriorityQueue<T> implements pq.IPriorityQueue<T> {

    public class PQNode<S> {
        S item;
        int priority;

        public void set(final S item, final int priority) {
            this.item = item;
            this.priority = priority;
        }
    }

    @Override
    public void insert(T item, int priority){
    }

    @Override
    public T removeMin() {
        return null;
    }

}