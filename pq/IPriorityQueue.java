package pq;

public interface IPriorityQueue {
    public boolean offer(Integer i);
    public boolean contains(Integer i);
    public Integer peek();
    public Integer poll();
}