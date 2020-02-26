import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] q;
    private int head, tail;
    private int cap, sz;

    /*
     * Each iterator must return the items in uniformly random order.
     * The order of two or more iterators to the same randomized queue must be mutually independent;
     * each iterator must maintain i ts own random order.
     */
    private class QueueIterator implements Iterator<Item> {
        private int current; // = head;
        private int[] ixes;  // to keep track of indexes already accessed

        public QueueIterator() {
            current = 0;
            ixes = new int[sz];
            int jx = head;
            for (int ix = 0; ix < sz; ix++) {
                while (q[jx] == null) {  // avoid null value
                    jx = (jx + 1) % cap;
                    // continue;
                }
                ixes[ix] = jx;
                jx = (jx + 1) % cap;
            }
            StdRandom.shuffle(ixes); // Random shuffle the indexes in ixes
        }

        public boolean hasNext() {
            return current < sz;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not implemented");
        }

        public Item next() {
            if (current == sz + 1)
                throw new java.util.NoSuchElementException("No more item");

            // System.out.println(" it./next current: " + Integer.toString(current) + " / ix is: " + Integer.toString(ixes[current]));
            Item item = q[ixes[current]];
            current++;
            return item;
        }
    }

    // construct an empty randomized queue
    public RandomizedQueue() {
        this.cap = 2;
        this.q = (Item[]) new Object[this.cap];
        this.sz = 0;
        this.head = 0;
        this.tail = -1;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return this.sz == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return this.sz;
    }

    // add the item
    public void enqueue(Item item) {
        if (this.sz == this.cap) resize(2 * this.cap);

        this.tail = (this.tail + 1) % this.cap;
        this.q[this.tail] = item;
        this.sz++;
        // System.out.println("Enqueue item " + Integer.toString((int) item) + " / size is now: " +
        //         Integer.toString(this.sz) + " / head is: " + Integer.toString(head) + " / tail: " + Integer.toString(tail));
    }

    // remove and return a random item
    public Item dequeue() {
        if (this.sz == 0)
            throw new java.util.NoSuchElementException("Cannot remove item from empty YaQueue");

        int ix;
        Item item;
        while (true) {
            ix = (this.head + StdRandom.uniform(this.sz)) % cap; // select index - Careful it can be head actually!
            item = this.q[ix];
            if (item != null) break;
        }

        this.q[ix] = null;
        this.sz--;

        if (this.sz > 0 && this.sz == this.cap / 4)
            resize(this.cap / 2);

        assert item != null : "dequeue - Item cannot be null - but it is :(";
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (this.sz == 0)
            throw new java.util.NoSuchElementException("Cannot sample from empty YaQueue");

        int ix = StdRandom.uniform(this.sz);
        Item item = this.q[(this.head + ix) % cap];
        if (item == null) item = this.q[this.head]; // head cannot be null
        return item;
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new QueueIterator();
    }

    // unit testing (required)
    public static void main(String[] args) {
        System.out.println("let's start");
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();

        System.out.println("--- first assert ---");
        assert rq != null : "rq should not be null";
        assert rq.size() == 0 : "yq should be empty, therefore size should be 0";

        //
        assertEnqueueDequeue();
        assertPartialEnqueueDequeue();
    }

    private void setNewHead() {
        while (true) { // need to set next head, which can be null...
            this.head = (this.head + 1) % cap;
            if (q[this.head] != null) break;
        }
    }

    // extend or shrink size of underlying array
    private void resize(int newCap) {
        // System.out.println("==== resize from " + Integer.toString(this.cap) + " to " + Integer.toString(newCap) + "  ====");

        Item[] cq = (Item[]) new Object[newCap];
        int ix = 0, nullOffset = 0;
        while (ix < this.sz) {
            int jx = (this.head + ix + nullOffset) % this.cap;
            if (this.q[jx] == null) { // dot not copy null element
                nullOffset++;
                continue;
            }
            cq[ix++] = this.q[jx];
        }
        this.cap = newCap;
        this.head = 0;
        this.tail = ix - 1;
        this.q = cq;
    }

    private static void assertEnqueueDequeue() {
        System.out.println("--- Assert enqueue and iterator ---");
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();

        final int N = 10;
        for (int ix = 1; ix <= N; ix++) {
            rq.enqueue(ix);
        }

        assert rq.size() == N : "rq should be empty, therefore size should be " + Integer.toString(N);
        assert rq.cap == 16 : "rq should be empty, therefore size should be 16";

        System.out.println("Iterator1: ");
        for (int i : rq)
            for (int j : rq)
                System.out.println(Integer.toString(i) + " -- " + Integer.toString(j));

        System.out.println("--- Assert dequeue ---");
        while (rq.sz > 0) {
            int item = rq.dequeue();
            System.out.println("Dequeue-ing: " + Integer.toString(item));
        }

        assert rq.size() == 0 : "rq should be empty, therefore size should be 0";
        assert rq.cap == 2 : "rq should be of size 2";

    }

    private static void assertPartialEnqueueDequeue() {
        System.out.println("--- Assert Partial enqueue and iterator ---");
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();

        final int N = 20;
        for (int ix = 1; ix <= N; ix++) {
            rq.enqueue(ix);
        }
        assert rq.size() == N : "rq should be of size should be " + Integer.toString(N);
        assert rq.cap == 32 : "rq should be of size 32"; // n=10, 16, n=100, 128

//        System.out.println("Iterator1: ");
//        for (int i : rq)
//            System.out.println(i);

        for (int ix = 1; ix <= N / 2; ix++) {
            int item = rq.dequeue();
            System.out.println(" ==> Deq. item: " + Integer.toString(item) + " / head is: " + Integer.toString(rq.head) + " / tail is: " + Integer.toString(rq.tail));
        }
        assert rq.size() == N / 2 : "rq should be of size should be " + Integer.toString(N / 2);


        System.out.println("Iterator2: ");
        for (int i : rq)
            System.out.println(i);

        // n = 10;
        for (int ix = 1; ix <= N; ix++) {
            rq.enqueue(ix + N);
        }

        assert rq.size() == N + N / 2 : "rq should be of size should be " + Integer.toString(N + N / 2);
        // assert rq.cap == 256 : "rq should be of size 256"; // n=10, 16, n=100, 256

        System.out.println("Iterator3: ");
        for (int i : rq)
            System.out.println(i);

        // assert rq.head == 0 : "rq head should be 0, got: " + Integer.toString(rq.head); // resize!
        // assert rq.head == n / 2 : "rq head should be " + Integer.toString(n / 2) + ", got: " + Integer.toString(rq.head);
        // assert rq.tail == 3 : "rq tail should be 3, got: " + Integer.toString(rq.tail);

        System.out.println("--- Completed ---");
    }

}
