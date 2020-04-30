package pq;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class LFPriorityLinkedQueue<T> implements pq.IPriorityQueue<T> {

    // Helper Classes 
    private class PQNode<S> {
        private S item;
        private int priority;
        private AtomicMarkableReference<PQNode<S>> next;

        PQNode(S item, int priority, PQNode<S> next) {
            this.item = item;
            this.priority = priority;
            this.next = new AtomicMarkableReference<PQNode<S>>(next, false);
        }

        S getValue() {
            return item;
        }

        int getPriority() {
            return priority;
        }

        PQNode<S> getNext() {
            return next.getReference();
        } 

        PQNode<S> getNext(boolean marks[]) {
            return next.get(marks);
        }

        boolean isMarked() {
            return next.isMarked();
        }

        boolean CASNext(PQNode<S> oldNode, PQNode<S> newNode) {
            return next.compareAndSet(oldNode, newNode, false, false);
        }

        boolean CASNextSetMark(PQNode<S> oldNode, PQNode<S> newNode) {
            return next.compareAndSet(oldNode, newNode, false, true);
        }
    }

    // Positional Helper Class that contain location to insert new node
    private class PositionNodes<S> {
        PQNode<S> left;
        PQNode<S> right;

        PositionNodes(PQNode<S> left, PQNode<S> right) {
            this.left = left;
            this.right = right;
        }

        PQNode<S> getLeft() {
            return left;
        }

        PQNode<S> getRight() {
            return right;
        }
    }

    // Returns location of positional add
    private PositionNodes<T> searchPosition(int priority) {
        
        PQNode<T> right;
        PQNode<T> left = null;
        PQNode<T> leftNext = null;

        do {
            PQNode<T> temp = head;
            // Iterate through ordered linked list until you find left and right values.
            do {
                if (!temp.isMarked()) {
                    left = temp;
                    leftNext = temp.getNext();
                }
                temp = temp.getNext();
                if (temp == tail) {
                    break;
                }
            } while (temp.isMarked() || (priority > temp.getPriority()));

            right = temp;
            if (leftNext == right) {
                if ((right != tail) && right.next.isMarked()) {
                    continue;
                }
                else {
                    return new PositionNodes<>(left, right);
                }
            }
            if (left.CASNext(leftNext, right)) {
                if (right == tail || !right.next.isMarked()) {
                    return new PositionNodes<>(left, right);
                }
            }
        } while (true);
    }

    // Implementation
    private PQNode<T> tail;
    private PQNode<T> head;

    public LFPriorityLinkedQueue() {
        tail = new PQNode<>(null, Integer.MAX_VALUE, null);
        head = new PQNode<>(null, Integer.MIN_VALUE, tail); 
    }

    @Override
    public void insert(T item, int priority) {
        while (true) {
            PositionNodes<T> pos = searchPosition(priority);
            PQNode<T> prev = pos.getLeft();
            PQNode<T> next = pos.getRight();
            PQNode<T> newNode = new PQNode<>(item, priority, next);
            if (prev.CASNext(next, newNode)) {
                return;
            }
        }
    }

    @Override
    public T removeMin() {
        PQNode<T> begin, end;
        PQNode<T> cur = null;
        PQNode<T> next = null;
        boolean[] marks = {false};

        while (true) {
            begin = head;
            end = tail;
            cur = begin.getNext();
            next = cur.getNext(marks);
            if (marks[0]) {
                begin.CASNext(cur, next);
            }
            else if (cur != end) {
                if (cur.CASNextSetMark(next, next)) {
                    begin.CASNext(cur, next);
                    return cur.getValue();
                }
            }
            else {
                return null;
            }
        }
    }
}
