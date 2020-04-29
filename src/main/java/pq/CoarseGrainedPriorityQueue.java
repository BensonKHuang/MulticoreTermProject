package pq;

public class CoarseGrainedPriorityQueue<T> implements pq.IPriorityQueue<T> {

    public class PQNode<S> {
        S item;
        int priority;

        public void set(final S item, final int priority) {
            this.item = item;
            this.priority = priority;
        }
    }

    private int size;
    private final PQNode<T>[] pq;

    public CoarseGrainedPriorityQueue(final int capacity) {
        size = 0;
        pq = (PQNode<T>[]) new PQNode[capacity];
        for (int i = 0; i < capacity; i++) {
            pq[i] = new PQNode<T>();
        }
    }

    private int getLeftChildIndex(final int index) {
        return 2 * index + 1;
    }

    private int getRightChildIndex(final int index) {
        return 2 * index + 2;
    }

    private int getParentIndex(final int index) {
        return (index - 1) / 2;
    }

    private boolean hasLeftChild(final int index) {
        return getLeftChildIndex(index) < size;
    }

    private boolean hasRightChild(final int index) {
        return getRightChildIndex(index) < size;
    }

    private boolean hasParent(final int index) {
        return getParentIndex(index) >= 0;
    }

    private int getLeftChildPriority(final int index) {
        return pq[getLeftChildIndex(index)].priority;
    }

    private int getRightChildPriority(final int index) {
        return pq[getRightChildIndex(index)].priority;
    }

    private int getParentPriority(final int index) {
        return pq[getParentIndex(index)].priority;
    }

    private int getPriority(final int index) {
        return pq[index].priority;
    }

    private void swap(final int index1, final int index2) {
        int tmpP = pq[index1].priority;
        T tempI = pq[index1].item;

        pq[index1].priority = pq[index2].priority;
        pq[index1].item = pq[index2].item;

        pq[index2].priority = tmpP;
        pq[index2].item = tempI;
    }

    private void heapifyDown() {
        int index = 0;
        while (hasLeftChild(index)) {
            int smallerChild = getLeftChildIndex(index);
            if (hasRightChild(index) && getRightChildPriority(index) < getLeftChildPriority(index)) {
                smallerChild = getRightChildIndex(index);
            }

            if (getPriority(index) > getPriority(smallerChild)) {
                swap(index, smallerChild);
                index = smallerChild;
            } else {
                break;
            }
        }
    }

    private void heapifyUp() {
        int index = size - 1;
        while (hasParent(index) && getParentPriority(index) > getPriority(index)) {
            swap(index, getParentIndex(index));
            index = getParentIndex(index);
        }
    }

    @Override
    public synchronized void insert(final T item, final int priority) {
        pq[size++].set(item, priority);;
        heapifyUp();
    }

    @Override
    public synchronized T removeMin() {
        final T res = pq[0].item;
        pq[0] = pq[--size];
        heapifyDown();

        return res;
    }
}
