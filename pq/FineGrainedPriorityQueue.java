package pq;

import java.util.Arrays;

public class FineGrainedPriorityQueue implements pq.IPriorityQueue {

    private int size;
    private int capacity;
    private int[] pq;

    public FineGrainedPriorityQueue() {
        capacity = 10;
        pq = new int[capacity];
        size = 0;
    }

    private int getLeftChildIndex(int index) {
        return 2 * index + 1;
    }

    private int getRightChildIndex(int index) {
        return 2 * index + 2;
    }

    private int getParentIndex(int index) {
        return (index - 1) / 2;
    }

    private boolean hasLeftChild(int index) {
        return getLeftChildIndex(index) < size;
    }

    private boolean hasRightChild(int index) {
        return getRightChildIndex(index) < size;
    }

    private boolean hasParent(int index) {
        return getParentIndex(index) >= 0;
    }

    private int getLeftChildValue(int index) {
        return pq[getLeftChildIndex(index)];
    }

    private int getRightChildValue(int index) {
        return pq[getRightChildIndex(index)];
    }

    private int getParentValue(int index) {
        return pq[getParentIndex(index)];
    }

    private void checkCapacity() {
        if (size == capacity) {
            int[] newPq = new int[capacity*2];
            capacity *= 2;
            Arrays.parallelSetAll(newPq, i -> pq[i]);
        }
    }

    private void swap(int index1, int index2) {
        int temp = pq[index1];
        pq[index1] = pq[index2];
        pq[index2] = temp;
    }

    private void heapifyDown() {
        int index = 0;
        while (hasLeftChild(index)) {
            int smallerChild = getLeftChildIndex(index);
            if (hasRightChild(index) && getRightChildValue(index) < getLeftChildValue(index)) {
                smallerChild = getRightChildIndex(index);
            }

            if (pq[index] > pq[smallerChild]) {
                swap(index, smallerChild);
                index = smallerChild;
            } else {
                break;
            }
        }
    }

    private void heapifyUp() {
        int index = size - 1;
        while (hasParent(index) && getParentValue(index) > pq[index]) {
            swap(index, getParentIndex(index));
            index = getParentIndex(index);
        }
    }

    public int peek() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        return pq[0];
    }


    @Override
    public boolean insert(int i) {
        checkCapacity();
        size += 1;
        pq[size - 1] = i;
        heapifyUp();
        return true;
    }

    @Override
    public int removeMin() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        int res = pq[0];

        pq[0] = pq[size - 1];
        size -= 1;
        heapifyDown();

        return res;
    }

}