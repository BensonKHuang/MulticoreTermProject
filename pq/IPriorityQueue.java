package pq;

public interface IPriorityQueue<T> {
    // Precodition: There is always space for another PQ
    public void insert(T item, int priority);    
    // Precondition: There at least one item in the PQ
    public T removeMin();
}