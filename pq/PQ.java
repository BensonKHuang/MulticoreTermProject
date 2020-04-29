package pq;

import java.util.Random;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

public class PQ {
    private class Node {
        int key;
        int level;
        int validLevel;
        AtomicMarkableReference<Object> value;
        AtomicReference<Node> prev;
        AtomicReference<Node>[] next;

        public Node(int key, Object value, int level) {
            this.key = key;
            this.value = new AtomicMarkableReference<Object>(value, false);
            this.level = level;
            this.validLevel = 0;
            this.next = new AtomicReference[PQ.maxLevels];
        }
    }

    AtomicReference<Node> head;
    AtomicReference<Node> tail;
    final Random r = new Random();
    final static int maxLevels = 10;

    private AtomicReference<Node> readNext(AtomicReference<Node> node1, int level) {
        if (node1.get().value.isMarked()) {
            node1.set(helpDelete(node1, level).get());
        }
        AtomicReference<Node> node2 = this.readNode(node1).next[level];
        while (node2 == null) {
            node1 = helpDelete(node1, level);
            node2 = readNode(node1).next[level];
        }
        return node2;
    }

    private AtomicReference<Node> scanKey(AtomicReference<Node> node1, int level, int key) {
        AtomicReference<Node> node2 = readNext(node1, level);
        while (node2.get().key < key) {
            node1 = node2;
            node2 = readNext(node1, level);
        }
        return node2;
    }

    public boolean insert(int key, Object value) {
        int level = this.r.nextInt(PQ.maxLevels);
        AtomicReference<Node> newNode = new AtomicReference<Node>(new Node(key, value, level));
        AtomicReference<Node> copyNode = new AtomicReference<Node>(newNode.get());
        AtomicReference<Node> node1 = new AtomicReference<Node>(this.head.get());
        AtomicReference<Node> node2;
        AtomicReference<Node>[] savedNodes = new AtomicReference[PQ.maxLevels];

        while (true) {
            node2 = scanKey(node1, 0, key);
            boolean[] markHolder = new boolean[1];
            Object val2 = node2.get().value.get(markHolder);
            if (markHolder[0] == false && node2.get().key == key) {
                node2.get().value.compareAndSet(val2, value, false, false);
                for (int i = 1; i < PQ.maxLevels; i++) {
                    savedNodes[i] = null;
                }
            } else {
                continue;
            }
            newNode.get().next[0] = node2;
            if (node1.get().next[0].compareAndSet(node2.get(), newNode.get())) {
                break;
            }
        }
        for (int i = 1; i < PQ.maxLevels; i++) {
            newNode.get().validLevel = i;
            node1 = savedNodes[i];
            while (true) {
                node2 = scanKey(node1, i, key);
                newNode.get().next[i] = node2;
                if (newNode.get().value.isMarked() || node1.get().next[i].compareAndSet(node2.get(), newNode.get())) {
                    break;`
                }
            }
        }
        newNode.get().validLevel = level;
        if (newNode.get().value.isMarked()) {
            newNode = this.helpDelete(newNode, 0);
        }
        return true;
    }

    public AtomicReference<Node> deleteMin() {
        AtomicReference<Node> prev;
        AtomicReference<Node> last;
        AtomicReference<Node> node1;
        AtomicReference<Node> node2;
        prev = new AtomicReference<Node>(this.head.get());
        outter: while (true) {
            node1 = this.readNext(prev, 0);
            if (node1.get() == tail.get()) {
                return null;
            }
            while (true) {
                AtomicMarkableReference<Object> value = node1.get().value;
                if (!value.isMarked()) {
                    if (node1.get().value.compareAndSet(node1.get().value, value.get(null), false, true)) {
                        node1.get().prev = prev;
                        break outter; //breaks the outter while loop
                    } 
                } else if (value.isMarked()) {
                    node1 = helpDelete(node1, 0);
                }
                prev = node1;
            }
        }
        //D20
        for (int i = 0; i < node1.get().level - 1; i++) {

        }

        return null;
    }

    private AtomicReference<Node> helpDelete(AtomicReference<Node> node, int level) {
        return null;
    }

    private Node readNode(AtomicReference<Node> node) {
        if (node.get().value.isMarked()) {
            return null;
        } else {
            return node.get();
        }
    }

    public String toString() {
        return "hello i'm a pq";
    }
}