import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] q;
    private int cap, sz;

    /*
     * Each iterator must return the items in uniformly random order.
     * The order of two or more iterators to the same randomized queue must be mutually independent;
     * each iterator must maintain i ts own random order.
     */
    private class QueueIterator implements Iterator<Item> {
        private int current; // = head;
        private final int[] ixes;  // to keep track of indexes already accessed

        public QueueIterator() {
            current = 0;
            ixes = new int[sz];
            for (int ix = 0; ix < sz; ix++)
                ixes[ix] = ix;
            StdRandom.shuffle(ixes); // Random shuffle the indexes in ixes
        }

        public boolean hasNext() {
            return current < sz;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not implemented");
        }

        // Throw a java.util.NoSuchElementException if the client calls the next() method in the iterator when there are no more items to return.
        public Item next() {
            if (current == sz)
                throw new java.util.NoSuchElementException("No more item");

            Item item = q[ixes[current]];
            current++;
            return item;
        }
    }

    // construct an empty randomized queue
    public RandomizedQueue() {
        this.cap = 4;
        this.q = (Item[]) new Object[this.cap];
        this.sz = 0;
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
        checkItem(item);
        assert this.sz >= 0;
        if (this.sz == this.cap) resize(2 * this.cap);
        this.q[this.sz++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (this.sz == 0)
            throw new java.util.NoSuchElementException("Cannot remove item from empty YaQueue");

        int ix = StdRandom.uniform(this.sz);
        Item item = this.q[ix];
        swap(ix); // also decr this.sz

        if (this.sz == this.cap / 4) resize(this.cap / 2);
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (this.sz == 0)
            throw new java.util.NoSuchElementException("Cannot sample from empty YaQueue");

        return q[StdRandom.uniform(this.sz)];
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
        assertDequeSingletonQueue();
        System.out.println("Completed!");
    }

    private void swap(int ix) {
        // swap with last element
        assert ix >= 0 && ix < this.sz && this.sz > 0;
        this.q[ix] = this.q[this.sz - 1]; // NO-OP if ix == sz - 1
        this.q[sz - 1] = null;
        this.sz--;
    }

    // extend or shrink size of underlying array
    private void resize(int newCap) {
        if (newCap == 0) return;

        Item[] cq = (Item[]) new Object[newCap];
        this.cap = newCap;
        for (int ix = 0; ix < this.sz; ix++)
            cq[ix] = this.q[ix];
        this.q = cq;
    }

    private void checkItem(Item item) {
        if (item == null)
            throw new IllegalArgumentException("item cannot be null");
    }

    private static void assertEnqueueDequeue() {
        System.out.println("--- Assert enqueue/dequeue and iterator ---");
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();

        final int N = 20;
        for (int ix = 1; ix <= N; ix++) {
            rq.enqueue(ix);
        }

        assert rq.size() == N : "rq should be empty, therefore size should be " + Integer.toString(N);
        assert rq.cap == 32 : "rq should be empty, therefore size should be 32";

        System.out.println("Iterator1: ");
        for (int i : rq)
            // for (int j : rq)
            System.out.println(Integer.toString(i)); //  + " -- " + Integer.toString(j));

        System.out.println("--- Assert dequeue ---");
        while (rq.sz > 0) {
            int item = rq.dequeue();
            System.out.println("Dequeue-ing: " + Integer.toString(item) + " ? size: " + Integer.toString(rq.sz));
        }

        assert rq.size() == 0 : "rq should be empty, therefore size should be 0";
        assert rq.cap == 1 : "rq should be of size 1, got: " + Integer.toString(rq.cap);

        System.out.println("--- Assert enqueue/dequeue Completed ---");
    }

    private static void assertPartialEnqueueDequeue() {
        System.out.println("--- Assert Partial enqueue and iterator ---");
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();

        final int N = 120;
        for (int ix = 1; ix <= N; ix++) {
            rq.enqueue(ix);
        }
        assert rq.size() == N : "rq should be of size should be " + Integer.toString(N);
        assert rq.cap == 128 : "rq should be of size 128"; // n=10, 16, n=100, 128

//        System.out.println("Iterator1: ");
//        for (int i : rq)
//            System.out.println(i);

        for (int ix = 1; ix <= N / 2; ix++) {
            int item = rq.dequeue();
            System.out.println(" ==> Deq. item: " + Integer.toString(item));
        }
        assert rq.size() == N / 2 : "rq should be of size should be " + Integer.toString(N / 2);

        System.out.println("Iterator2: ");
        for (int i : rq)
            System.out.println(i);

        for (int ix = 1; ix <= N; ix++) {
            rq.enqueue(ix + N);
        }
        assert rq.size() == N + N / 2 : "rq should be of size should be " + Integer.toString(N + N / 2);
        assert rq.cap == 256 : "rq should be of size 256"; // n=10, 16, n=100, 256

        System.out.println("Iterator3: ");
        for (int i : rq)
            System.out.println(i);
        // assert rq.head == 0 : "rq head should be 0, got: " + Integer.toString(rq.head); // resize!
        // assert rq.head == n / 2 : "rq head should be " + Integer.toString(n / 2) + ", got: " + Integer.toString(rq.head);
        // assert rq.tail == 3 : "rq tail should be 3, got: " + Integer.toString(rq.tail);

        System.out.println("--- Completed ---");
    }

    private static void assertDequeSingletonQueue() {
        System.out.println("--- Assert  dequeue singleton list ---");
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();

        rq.enqueue(666);
        System.out.println("IteratorS: ");
        for (int i : rq) System.out.println(i);

        int item = rq.dequeue();
        System.out.println(" ==> Deq. item: " + Integer.toString(item));

        System.out.println("IteratorS: ");
        for (int i : rq) System.out.println(i);

        rq.enqueue(667);
        rq.enqueue(670);
        for (int i : rq) System.out.println(i);

        try {
            while (true) {
                item = rq.dequeue();
                System.out.println(" ==> Deq. item: " + Integer.toString(item));
            }
        } catch (java.util.NoSuchElementException ex) {
            System.out.println("Intercepted expected exception: " + ex.getMessage());
        }
        assert rq.sz == 0;

        System.out.println("enque/dequeue - size: " + Integer.toString(rq.sz) + " / cap: " + Integer.toString(rq.cap));
        rq.enqueue(666);
        System.out.println("enque - size: " + Integer.toString(rq.sz) + " / cap: " + Integer.toString(rq.cap));
        item = rq.dequeue();
        System.out.println("deque - size: " + Integer.toString(rq.sz) + " / cap: " + Integer.toString(rq.cap));

        System.out.println("enque again - size: " + Integer.toString(rq.sz) + " / cap: " + Integer.toString(rq.cap));
        rq.enqueue(666);
        System.out.println("enque again");
        item = rq.dequeue();

        assert rq.sz == 0;
        System.out.println("IteratorS: ");
        for (int i : rq) System.out.println(i);
    }
}
