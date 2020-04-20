package pq;

public interface IPriorityQueue<T> {
    public void insert(T item, int priority) throws HeapFullException;    
    public T removeMin() throws HeapEmptyException;
}