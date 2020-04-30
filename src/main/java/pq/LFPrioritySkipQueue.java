package pq;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LFPrioritySkipQueue<T> implements IPriorityQueue<T> {
    // Precondition: There is always space for another PQ
    final static int MAX_LEVEL = 40;

    @Override
    public void insert(T item, int priority) {
        skiplist.add(item, priority);
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
        final int priority;
        AtomicBoolean marked;
        final AtomicMarkableReference<Node<T>>[] next;
        private int topLevel;

        // constructor for sentinel nodes
        public Node(int priority) {
            this.value = null;
            this.priority = priority;
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
            this.priority = priority;
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
        Random random;

        public PrioritySkipList() {
            random = new Random(50);
            for (int i = 0; i < head.next.length; i++) {
                head.next[i] = new AtomicMarkableReference<Node<T>>(tail, false);
            }
        }

        boolean add(T item, int priority) {
            int bottomLevel = 0;
            int topLevel = random.nextInt(MAX_LEVEL);
            Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
            Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
            
            while (true) {
                // Find location for new item with pred and succ arrays
                // If find == true, then key already exists (priority)
                boolean found = find(priority, preds, succs);
                
                // Prevent Duplicates
                if (found) {
                    return false;
                }
                // Item not set, so add it...
                // Create a new node and connect to successors
                Node<T> newNode = new Node<T>(item, priority, topLevel);
                for (int level = bottomLevel; level <= topLevel; level++) {
                    Node<T> succ = succs[level];
                    newNode.next[level].set(succ, false);
                }
                Node<T> pred = preds[bottomLevel];
                Node<T> succ = succs[bottomLevel];

                // Check if pred at level 0 (closest pred) is connected to successor at level 0 (closest succ)
                // Means no new nodes are inserted between them or it not marked (marked indicates deletion)

                // If OK, then connect atomically with CAS: the pred to the new node at level 0
                newNode.next[bottomLevel].set(succ, false);
                if (!pred.next[bottomLevel].compareAndSet(succ, newNode, false, false)) {
                    // CAS fails, so try again... something was changed
                    continue;
                }
                // Now that level 0 is connected, connect preds from other levels to new nodes!
                // If fail, then find again, meaning prepare a new array of preds and succs and
                // continue from where you left off.

                for (int level = bottomLevel + 1; level <= topLevel; level++) {
                    while (true) {
                        if (newNode.next[level].isMarked()) {
                            break; // Node is deleted, so don't proceed
                        }

                        pred = preds[level];
                        succ = succs[level];
                        
                        // Connect new node to next successor
                        if (pred.next[level].compareAndSet(succ, newNode, false, false)) {
                            break;
                        }

                        find(priority, preds, succs);
                    }
                }
                return true;
            }
        }

        boolean find(int priority, Node<T>[] preds, Node<T>[] succs) {
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
                        if (curr.priority < priority) {
                            pred = curr;
                            curr = succ;
                        } else {
                            break;
                        }
                    }
                    preds[level] = pred;
                    succs[level] = curr;
                }
                return (curr.priority == priority);
            }
        }

        boolean remove(Node<T> node) {
            int priority = node.priority;
            int bottomLevel = 0;
            Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
            Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
            Node<T> succ;
            
            while (true) {
                
                // Find location for new item with preds and succs arrays
                // if false, then key does not exist and you early return
                // if true, then key exists
                boolean found = find(priority, preds, succs);
                if (!found) {
                    return false;
                }

                // The node exists in list
                Node<T> nodeToRemove = succs[bottomLevel];
                // Traverse all levels from top to level 1 and checked if marked, ignore victim
                // (level 0).
                // Otherwise, try to mark using CAS = it is being marked (by some thread)
                for (int level = nodeToRemove.topLevel; level >= bottomLevel + 1; level--) {
                    boolean[] marked = { false };
                    succ = nodeToRemove.next[level].get(marked);
                    while (!marked[0]) {
                        nodeToRemove.next[level].attemptMark(succ, true);
                        succ = nodeToRemove.next[level].get(marked);
                    }
                }
                // When all levels except 0 are marked, try to mark level 0, and logically
                // delete it
                boolean[] marked = { false };
                // Get direct successor (level 0)

                succ = nodeToRemove.next[bottomLevel].get(marked);
                while (true) {
                    // Try to only mark the victim: selfMarked == true
                    // Otherwise: another thread marked the successor or marked the victim already

                    boolean selfMarked = nodeToRemove.next[bottomLevel].compareAndSet(succ, succ, false, true);
                    // Check if victim is marked
                    succ = succs[bottomLevel].next[bottomLevel].get(marked);
                    if (selfMarked) {
                        // Since this thread marked value, find will physically remove the node
                        find(priority, preds, succs);
                        return true;
                        // Since this thread did not mark, then either node is not marked at all and CAS
                        // failed because a thread marked the victim's succ.
                        // If marked, return because someone else removed it, otherwise we start over...
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
