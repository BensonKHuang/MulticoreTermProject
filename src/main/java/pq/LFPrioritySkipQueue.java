package pq;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LFPrioritySkipQueue<T> implements IPriorityQueue<T> {
    // Precodition: There is always space for another PQ
    final static int MAX_LEVEL = 40;
    Random random = new Random(50);

    @Override
    public void insert(T item, int priority) {
        Node<T> node = new Node<T>(item, priority, random.nextInt(MAX_LEVEL));
        skiplist.add(node);
    }

    // Precondition: There at least one item in the PQ
    @Override
    public T removeMin() {
        Node<T> node = skiplist.findAndMarkMin();
        if (node != null) {
            skiplist.remove(node);
            return node.value;
        } else {
            return null;
        }
    }

    PrioritySkipList<T> skiplist;

    public LFPrioritySkipQueue() {
        skiplist = new PrioritySkipList<T>();
    }

    public static final class Node<T> {
        final T value;
        final int key;
        AtomicBoolean marked;
        final AtomicMarkableReference<Node<T>>[] next;
        private int topLevel;

        // constructor for sentinel nodes
        public Node(int key) {
            this.value = null;
            this.key = key;
            this.marked = new AtomicBoolean(false);
            next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[MAX_LEVEL + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node<T>>(null, false);
            }
            topLevel = MAX_LEVEL;
        }

        // constructor for ordinary nodes
        public Node(T x, int priority, int height) {
            this.value = x;
            this.key = priority;
            this.marked = new AtomicBoolean(false);
            next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[height + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node<T>>(null, false);
            }
            topLevel = height;
        }
    }

    public static final class PrioritySkipList<T> {
        final Node<T> head = new Node<T>(Integer.MIN_VALUE);
        final Node<T> tail = new Node<T>(Integer.MAX_VALUE);

        public PrioritySkipList() {
            for (int i = 0; i < head.next.length; i++) {
                head.next[i] = new AtomicMarkableReference<Node<T>>(tail, false);
            }
        }

        boolean add(Node<T> node) {
            int key = node.key;
            int bottomLevel = 0;
            int topLevel = node.topLevel;
            Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
            Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
            while (true) {
                boolean found = find((T) node.value, key, preds, succs);
                
                // Prevent Duplicates
                if (found) {
                    return false;
                }

                Node<T> newNode = node;
                for (int level = bottomLevel; level <= topLevel; level++) {
                    Node<T> succ = succs[level];
                    newNode.next[level].set(succ, false);
                }
                Node<T> pred = preds[bottomLevel];
                Node<T> succ = succs[bottomLevel];
                newNode.next[bottomLevel].set(succ, false);
                if (!pred.next[bottomLevel].compareAndSet(succ, newNode, false, false)) {
                    continue;
                }
                for (int level = bottomLevel + 1; level <= topLevel; level++) {
                    while (true) {
                        pred = preds[level];
                        succ = succs[level];
                        if (pred.next[level].compareAndSet(succ, newNode, false, false))
                            break;
                        find((T) node.value, key, preds, succs);
                    }
                }
                return true;
            }
        }

        boolean find(T x, int key, Node<T>[] preds, Node<T>[] succs) {
            int bottomLevel = 0;
            boolean[] marked = { false };
            boolean snip;
            Node<T> pred = null, curr = null, succ = null;
            retry: while (true) {
                pred = head;
                for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
                    curr = pred.next[level].getReference();
                    while (true) {
                        succ = curr.next[level].get(marked);
                        while (marked[0]) {
                            snip = pred.next[level].compareAndSet(curr, succ, false, false);
                            if (!snip)
                                continue retry;
                            curr = pred.next[level].getReference();
                            succ = curr.next[level].get(marked);
                        }
                        if (curr.key < key) {
                            pred = curr;
                            curr = succ;
                        } else {
                            break;
                        }
                    }
                    preds[level] = pred;
                    succs[level] = curr;
                }
                return (curr.key == key);
            }
        }

        boolean remove(Node<T> node) {
            int key = node.key;
            int bottomLevel = 0;
            Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
            Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
            Node<T> succ;
            while (true) {
                boolean found = find((T) node.value, key, preds, succs);
                if (!found) {
                    return false;
                }
                Node<T> nodeToRemove = succs[bottomLevel];
                for (int level = nodeToRemove.topLevel; level >= bottomLevel + 1; level--) {
                    boolean[] marked = { false };
                    succ = nodeToRemove.next[level].get(marked);
                    while (!marked[0]) {
                        nodeToRemove.next[level].attemptMark(succ, true);
                        succ = nodeToRemove.next[level].get(marked);
                    }
                }
                boolean[] marked = { false };
                succ = nodeToRemove.next[bottomLevel].get(marked);
                while (true) {
                    boolean iMarkedIt = nodeToRemove.next[bottomLevel].compareAndSet(succ, succ, false, true);
                    succ = succs[bottomLevel].next[bottomLevel].get(marked);
                    if (iMarkedIt) {
                        find((T) node.value, key, preds, succs);
                        return true;
                    } else if (marked[0])
                        return false;
                }
            }
        }

        public Node<T> findAndMarkMin() {
            Node<T> curr = null;
            curr = head.next[0].getReference();
            while (curr != tail) {
                if (!curr.marked.get()) {
                    if (curr.marked.compareAndSet(false, true)) {
                        return curr;
                    } else {
                        curr = curr.next[0].getReference();
                    }
                }
            }
            return null; // no unmarked nodes
        }
    }
}
