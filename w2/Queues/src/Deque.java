import java.util.Iterator;

public class Deque<Item> implements Iterable<Item> {
    private Node first, last;
    private int sz;

    private class Node {
        Item item;
        Node next;
        Node prev;
    }

    private class ListIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not implemented");
        }

        public Item next() {
            if (current == null)
                throw new java.util.NoSuchElementException("No more item");
            Item item = current.item;
            this.current = current.next;
            return item;
        }
    }

    // construct an empty deque
    public Deque() {
        this.first = null;
        this.last = null;
        this.sz = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return first == null;
    }

    // return the number of items on the deque
    public int size() {
        return this.sz;
    }

    // add the item to the front
    public void addFirst(Item item) {
        checkItem(item);

        Node exfirst = first; // copy
        first = addHelper(item, exfirst, "first"); // new node
        if (last == null) last = first;
        else exfirst.prev = first;
    }

    // add the item to the back
    public void addLast(Item item) {
        checkItem(item);

        Node exlast = last; // copy
        last = addHelper(item, exlast, "last"); // new node
        if (first == null) first = last;
        else exlast.next = last;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (this.isEmpty())
            throw new java.util.NoSuchElementException("Cannot remove item from empty Deque");

        Item item = removeHelper(first, "first");
        if (this.sz == 0) last = null; // case we remove the only element for Deque
        else first.prev = null;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (this.isEmpty())
            throw new java.util.NoSuchElementException("Cannot remove item from empty Deque");

        Item item = removeHelper(last, "last");
        if (this.sz == 0) first = null; // case we remove the only element for Deque
        else last.next = null;
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    // unit testing (required)
    public static void main(String[] args) {
        System.out.println("let's start");
        Deque<String> dq = new Deque<String>();

        System.out.println("--- first assert ---");
        assert dq != null : "dq should not be null";
        assert dq.isEmpty() : "dq should be empty";
        assert dq.size() == 0 : "dq should be empty, therefore size should be 0";

        // test insert first - empty deque
        System.out.println("--- insert first / empty deque ---");
        String strtest = addInsertFirstEmptyDeque(dq);

        // test remove first - 0nly 1 item
        System.out.println("--- remove first ---");
        assertRmFirstFromSingletonDeque(dq, strtest);

        System.out.println("--- remove first / empty deque => exception ---");
        assertRmFirstFromEmptyDeque(dq);

        // test insert last - empty deque
        System.out.println("--- insert last / empty deque ---");
        strtest = assertInsertLastEmptyDeque(dq);
        assertRmFirstFromSingletonDeque(dq, strtest);

        // test multi-insert first
        System.out.println("--- insert multi-first / empty deque multi-remove first ---");
        assertAddMultiInsertFirstEmptyDeque(dq);

        System.out.println("--- remove last / empty deque => exception ---");
        assertRmLastFromEmptyDeque(dq);

        System.out.println("--- remove last / singleton deque => empty deque ---");
        assertRmLastFromSingletonDeque(dq);

        System.out.println("--- multi remove last / deque => (finally) empty deque ---");
        assertRmLastFromDeque(dq);

        //
        System.out.println("--- test 1..n ---");
        assertOneToN();

        System.out.println("--- Completed ---");
    }

    private void checkItem(Item item) {
        if (item == null)
            throw new IllegalArgumentException("item cannot be null");
    }

    private Node addHelper(Item item, Node current, String key) {
        // new node:
        Node node = new Node();
        node.item = item;
        if (key.equals("first")) {
            node.next = current;
            node.prev = null;
        } else { // last
            node.next = null;
            node.prev = current;
        }
        this.sz++;
        return node;
    }

    private Item removeHelper(Node current, String key) {
        Item item = current.item;
        current.item = null; // for gc

        if (key.equals("first")) {
            first = first.next;
            current.next = null;
        } else {
            last = last.prev;
            current.prev = null;
        }
        this.sz--;
        return item;
    }

    private static String addInsertFirstEmptyDeque(Deque<String> dq) {
        String strtest = "un";
        dq.addFirst(strtest);
        assert dq.size() == 1 : "dq should be size 1";
        assert dq.first == dq.last : "only 1 element in deque hence first == last";
        assert dq.first != null : "dq.first should NOT be null";
        return strtest;
    }

    private static void assertRmFirstFromSingletonDeque(Deque<String> dq, String strtest) {
        String item = dq.removeFirst();
        assert item.equals(strtest) : "expect item to be: " + strtest + " / got: " + item;
        assert dq.size() == 0 : "dq should be empty, therefore size should be 0";
        assert dq.isEmpty() : "dq should be empty";
    }

    private static String assertInsertLastEmptyDeque(Deque<String> dq) {
        String strtest = "enfin";
        dq.addLast(strtest);
        assert dq.size() == 1 : "dq should be size 1";
        assert dq.first == dq.last : "only 1 element in deque hence first == last";
        assert dq.last != null : "dq.first should NOT be null";
        return strtest;
    }

    private static void assertAddMultiInsertFirstEmptyDeque(Deque<String> dq) {
        // also remove all items
        String strtest1 = "un";
        dq.addFirst(strtest1);
        assert !dq.isEmpty() : "dq should NOT be empty";

        String strtest2 = "deux";
        dq.addFirst(strtest2);
        assert !dq.isEmpty() : "dq should NOT be empty";

        String strtest3 = "trois";
        dq.addFirst(strtest3);
        assert !dq.isEmpty() : "dq should NOT be empty";

        assert dq.size() == 3 : "dq should be size 3";
        assert !dq.isEmpty() : "dq should NOT be empty";
        assert dq.first != dq.last : "many elements (>1) in deque";
        assert dq.last != null : "dq.last should NOT be null";
        assert dq.first != null : "dq.first should NOT be null";

        // remove - first
        String item = dq.removeFirst();
        assert item.equals(strtest3);
        assert dq.size() == 2 : "dq should be size 2";
        assert dq.first != dq.last : "many elements (>1) in deque";

        item = dq.removeFirst();
        assert item.equals(strtest2);
        assert dq.size() == 1 : "dq should be size 1";
        assert dq.first == dq.last : "only 1 element left in deque";

        item = dq.removeFirst();
        assert item.equals(strtest1);
        assert dq.size() == 0 : "dq should be size 0";
        assert dq.first == dq.last : "empty";
        assert dq.first == null : "dq.first should be null";
        assert dq.last == null : "dq.last should be null";
    }

    private static void assertRmFirstFromEmptyDeque(Deque<String> dq) {
        try {
            dq.removeFirst();

        } catch (java.util.NoSuchElementException ex) {
            System.out.println("Intercepted expected exception: " + ex.getMessage());
            assert ex.getMessage().equals("Cannot remove item from empty Deque");
        }
    }

    private static void assertRmLastFromSingletonDeque(Deque<String> dq) {
        String strtest = "un";
        dq.addFirst(strtest);

        String item = dq.removeLast();
        assert item.equals(strtest) : "expect item to be: " + strtest + " / got: " + item;
        assert dq.size() == 0 : "dq should be empty, therefore size should be 0";
        assert dq.isEmpty() : "dq should be empty";
        assert dq.first == null : "dq.first should be null";
        assert dq.last == null : "dq.last should be null";
    }

    private static void assertRmLastFromDeque(Deque<String> dq) {
        // add 3 items first
        String str1 = "un";
        dq.addFirst(str1);

        String str2 = "deux";
        dq.addFirst(str2);

        String str3 = "trois";
        dq.addFirst(str3);
        assert dq.size() == 3 : "dq should be of size 3";

        // Iterator<String> dq_it = dq.iterator();
        // for (String s : dq)
        //    System.out.println(s);
        //
        String item = dq.removeLast();
        assert item.equals(str1) : "expect item to be: " + str1 + " / got: " + item;
        assert dq.size() == 2 : "dq should be of size 2";
        assert !dq.isEmpty() : "dq should NOT be empty";
        assert dq.first != dq.last : "non empty";
        assert dq.first != null : "dq.first should NOT be null";
        assert dq.last != null : "dq.last should NOT be null";

        //
        for (String s : dq)
            System.out.println(s);
        //
        item = dq.removeLast();
        assert item.equals(str2) : "expect item to be: " + str2 + " / got: " + item;
        assert dq.size() == 1 : "dq should be of size 1";
        assert !dq.isEmpty() : "dq should NOT be empty";

        //
        item = dq.removeLast();
        assert item.equals(str3) : "expect item to be: " + str3 + " / got: " + item;
        assert dq.size() == 0 : "dq should be empty, therefore size should be 0";
        assert dq.isEmpty() : "dq should be empty";
        assert dq.first == null : "dq.first should be null";
        assert dq.last == null : "dq.last should be null";
    }

    private static void assertRmLastFromEmptyDeque(Deque<String> dq) {
        try {
            dq.removeLast();

        } catch (java.util.NoSuchElementException ex) {
            System.out.println("Intercepted expected exception: " + ex.getMessage());
            assert ex.getMessage().equals("Cannot remove item from empty Deque");
        }
    }

    private static void assertOneToN() {
        Deque<Integer> dqi = new Deque<Integer>();
        final int N = 1000;
        for (int ix = 1; ix <= N; ix++) {
            dqi.addFirst(ix);
        }
        assert dqi.sz == N : "Size of dq should be " + Integer.toString(N);
        for (int ix = 1; ix <= N; ix++) {
            int jx = dqi.removeLast();
            assert jx == ix;
        }
        assert dqi.sz == 0 : "Size of dq should be 0";
        assert dqi.first == null : "dqi.first should be null";
        assert dqi.first == dqi.last : "dqi.last should be null";

        final int M = 100;
        for (int ix = 1; ix <= M; ix++) {
            dqi.addLast(ix);
        }
        assert dqi.sz == M : "Size of dq should be " + Integer.toString(M);
        for (int ix = M; ix >= 1; ix--) {
            int jx = dqi.removeLast();
            assert jx == ix;
        }
        assert dqi.sz == 0 : "Size of dq should be 0";
        assert dqi.first == null : "dqi.first should be null";
        assert dqi.first == dqi.last : "dqi.last should be null";

        // n = 1000;
        for (int ix = 1; ix <= N; ix++) {
            dqi.addLast(ix);
        }
        assert dqi.sz == N : "Size of dq should be " + Integer.toString(N);
        for (int ix = N; ix >= 1; ix--) {
            int jx = dqi.removeLast();
            assert jx == ix;
        }
        assert dqi.sz == 0 : "Size of dq should be 0";
        assert dqi.first == null : "dqi.first should be null";
        assert dqi.first == dqi.last : "dqi.last should be null";

        final int P = 1;
        dqi.addFirst(P);
        int jx = dqi.removeLast();
        assert jx == P;
        assert dqi.sz == 0 : "Size of dq should be 0";
        assert dqi.first == null : "dqi.first should be null";
        assert dqi.first == dqi.last : "dqi.last should be null";
    }
}

