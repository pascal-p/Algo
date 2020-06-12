/* *****************************************************************************
 *  Name: Pascal
 *  Date: June 2020
 *  Description: BoggleSolver API
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {
    private static final byte[][] ADJ4X4 = {
            { 1, 4, 5 },                    //  0 - 3
            { 0, 2, 4, 5, 6 },              //  1 - 5
            { 1, 3, 5, 6, 7 },              //  2 - 5
            { 2, 6, 7 },                    //  3 - 3
            { 0, 1, 5, 8, 9 },              //  4 - 5
            { 0, 1, 2, 4, 6, 8, 9, 10 },    //  5 - 8
            { 1, 2, 3, 5, 7, 9, 10, 11 },   //  6 - 8
            { 2, 3, 6, 10, 11 },            //  7 - 5
            { 4, 5, 9, 12, 13 },            //  8 - 5
            { 4, 5, 6, 8, 10, 12, 13, 14 }, //  9 - 8
            { 5, 6, 7, 9, 11, 13, 14, 15 }, // 10 - 8
            { 6, 7, 10, 14, 15 },           // 11 - 5
            { 8, 9, 13 },                   // 12 - 3
            { 8, 9, 10, 12, 14 },           // 13 - 5
            { 9, 10, 11, 13, 15 },          // 14 - 5
            { 10, 11, 14 }                  // 15 - 3
    };

    private static final byte[][] ADJ3X3 = {
            { 1, 3, 4 },                // 0
            { 0, 2, 3, 4, 5 },          // 1
            { 1, 4, 5 },                // 2
            { 0, 1, 4, 6, 7 },          // 3
            { 0, 1, 2, 3, 5, 6, 7, 8 }, //4
            { 1, 2, 4, 7, 8 },          // 5
            { 3, 4, 7 },                // 6
            { 3, 4, 5, 6, 8 },          // 7
            { 4, 5, 7 }                 // 8
    };

    private final MyTrieSET trieSet;
    private DNode[] graph;
    private byte n;

    private class DNode {
        private final char ch;
        private final byte[] adj;           // at most 8 neighbors
        private final byte numAdj;          // actual number of neighbors
        private boolean marked;
        // what else?

        public DNode(char ch, byte[] adj, byte numAdj) {
            this.ch = ch;
            this.adj = adj.clone();
            this.numAdj = numAdj;
            this.marked = false;
        }
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.trieSet = new MyTrieSET();

        for (String key : dictionary) {
            this.trieSet.add(key);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        initGraph(board);
        Set<String> wordSet = dfs();
        return wordSet;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (trieSet.contains(word)) {
            int wlen = word.length();

            if (wlen < 3) return 0;
            else if (wlen <= 4) return 1;
            else if (wlen <= 5) return 2;
            else if (wlen <= 6) return 3;
            else if (wlen <= 7) return 5;
            else return 11; // wlen >= 8
        }
        return 0;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        StdOut.println("Read input dictionary - It contains " + dictionary.length + " entries.");
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);

        StdOut.println("BoggleSolver TrieSet has " + solver.trieSet.size() + " elements");
        /*
        String w = "ACCURACY";
        StdOut.println("BoggleSolver contains " + w + "? " + solver.trieSet.contains(w));
        w = "FLUMMOXED";
        StdOut.println("BoggleSolver contains word " + w + "? " + solver.trieSet.contains(w));

        Queue<String> q = solver.trieSet.keysWithPrefix("VAL");
        Iterable<String> it = q;
        StdOut.println("BoggleSolver num. of word(s) with VAL as prefix? " + q.size());

        StdOut.println("BoggleSolver dictionary word(s) with VAL as prefix?");
        for (String word : it) StdOut.println("\t" + word);
        StdOut.println();

        for (byte ix = 0; ix < solver.n; ix++) {
            StdOut.println(
                    "The " + solver.graph[ix].numAdj + " Neighbors of node " + ix + ": ");
            for (byte jx = 0; jx < solver.graph[ix].numAdj; jx++)
                StdOut.print(solver.graph[ix].adj[jx] + " ");
            StdOut.println();
        }
        */

        int score = 0, numWords = 0;
        for (String word : solver.getAllValidWords(board)) {
            int wordScore = solver.scoreOf(word);
            StdOut.println(word + " / " + wordScore);
            score += wordScore;
            numWords++;
        }
        StdOut.println("Score = " + score + " / Num. of words: " + numWords);
    }

    private void initGraph(BoggleBoard board) {
        this.n = (byte) ((board.rows() * board.cols()) % 127);
        this.graph = new DNode[this.n];

        if (n == 16) speInitAdj(board, ADJ4X4);
        else if (n == 9) speInitAdj(board, ADJ3X3);
        else {
            byte kx = 0;

            for (short ix = 0; ix < board.rows(); ix++)
                for (short jx = 0; jx < board.cols(); jx++) {
                    byte[] adj = new byte[8];
                    byte numOfAdj = findAdj(adj, board, ix, jx);
                    this.graph[kx] = new DNode(board.getLetter(ix, jx), adj, numOfAdj);
                    kx++;
                }

        }
    }

    private void speInitAdj(BoggleBoard board, byte[][] adj) {
        byte kx = 0;

        for (short ix = 0; ix < board.rows(); ix++)
            for (short jx = 0; jx < board.cols(); jx++) {

                byte numOfAdj = (byte) adj[kx].length;
                this.graph[kx] = new DNode(board.getLetter(ix, jx), adj[kx], numOfAdj);
                kx++;
            }
    }

    // Find neighbors of node x, given the board and current position (ix, jx)
    private byte findAdj(byte[] x, BoggleBoard board, short ix, short jx) {
        byte numAdj = 0;

        for (int nix = ix - 1; nix <= ix + 1; nix++)
            for (int njx = jx - 1; njx <= jx + 1; njx++) {
                if (nix >= 0 && nix < board.rows() && njx >= 0 && njx < board.cols()) {
                    if (nix == ix && njx == jx) continue;
                    x[numAdj++] = (byte) ((nix * board.cols() + njx) % 127);
                }
            }
        return numAdj;
    }

    // Explore all nodes (dices) using dfs
    private Set<String> dfs() {
        Set<String> wordSet = new HashSet<>();
        // Set<String> prefixExplored = new HashSet<>();
        StringBuilder prefix = new StringBuilder();

        int expLen = 1;
        for (byte ix = 0; ix < this.n; ix++) {
            char ch = this.graph[ix].ch;
            prefix.append(ch);

            if (ch == 'Q') {
                prefix.append('U');
                expLen = 2;
            }

            dfs(prefix, ix, wordSet); // dfs(prefix, ix, wordSet, prefixExplored);

            assert !this.graph[ix].marked : "Expected this.graph[ix].marked to be false";
            assert prefix.length() == expLen :
                    "Expected prefix length to be 1, got: " + prefix.length();
            prefix.setLength(0);
            expLen = 1;
        }

        return wordSet;
    }

    private void dfs(StringBuilder prefix, byte pos, Set<String> wordSet) {
        // Set<String> prefixExplored) {

        this.graph[pos].marked = true; // mark this node (die) as explored
        // if (prefixExplored.contains(prefix.toString())) return;

        if (prefix.length() >= 3) { // Only interested in valid prefixes
            String pre = prefix.toString();

            if (trieSet.keysWithPrefix(pre).size() > 0) {
                // Any matching words given the current prefix?
                if (trieSet.contains(pre)) wordSet.add(pre); // Yes, if current prefix is a valid
                //                                          word add it to our set of words (found)
            }
            else {
                // prefixExplored.add(pre);        // Dead end, add prefix to set of explored prefixes
                // this.graph[pos].marked = false; // Also un-mark it
                return;
            }
        }

        // if prefix ends with Q add U before exploring neighbors
        if (prefix.charAt(prefix.length() - 1) == 'Q') prefix.append('U');

        DNode node = this.graph[pos];
        for (byte ix = 0; ix < node.numAdj; ix++) { // For each neighbors of node (at pos)
            byte npos = node.adj[ix];               // Explore neighbor if not yet marked

            if (!this.graph[npos].marked) {
                prefix.append(this.graph[npos].ch);
                if (this.graph[npos].ch == 'Q') prefix.append('U');

                // Launch new search from npos
                dfs(prefix, npos, wordSet); // dfs(prefix, npos, wordSet, prefixExplored);

                prefix.deleteCharAt(prefix.length() - 1);      // Remove last added Char

                if (prefix.charAt(prefix.length() - 1) == 'Q') // is it a Q? we removed U...
                    prefix.deleteCharAt(prefix.length() - 1);  // ...remove it then

                this.graph[npos].marked = false;
            }
        }

        assert this.graph[pos].marked : "[Adj] Expected this.graph[ix].marked to be true";
        this.graph[pos].marked = false;
    }

    private static class MyTrieSET {
        private static final int R = 26;  // only letters from 'A'..'Z'
        private Node root;                // root of trie
        private int n;                    // number of keys in trie

        // R-way trie node
        private static class Node {
            private Node[] next = new Node[R];
            private boolean isString;
        }

        // constructor
        public MyTrieSET() {
        }

        //
        // Public interface
        //
        public boolean contains(String key) {
            if (key == null)
                throw new IllegalArgumentException("argument to contains() is null");
            Node x = get(root, key, 0);

            if (x == null) return false;
            return x.isString;
        }

        public void add(String key) {
            if (key == null) throw new IllegalArgumentException("argument to add() is null");
            root = add(root, key, 0);
        }

        public int size() {
            return n;
        }

        public Queue<String> keysWithPrefix(String prefix) {
            Queue<String> results = new Queue<String>();
            Node x = get(root, prefix, 0);
            collect(x, new StringBuilder(prefix), results);
            return results;
        }

        //
        // Private Helpers
        //
        private char idx(char c) {
            assert c >= 'A' && c <= 'Z' : "Expecting letters in range 'A'..'Z' only";
            return (char) ((int) c - (int) 'A');
        }

        private char chr(char c) {
            return (char) (c + 'A');
        }

        private Node get(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) return x;

            char c = idx(key.charAt(d));
            return get(x.next[c], key, d + 1);
        }

        private Node add(Node x, String key, int d) {
            if (x == null) x = new Node();
            if (d == key.length()) {
                if (!x.isString) n++;
                x.isString = true;
            }
            else {
                char c = idx(key.charAt(d));
                x.next[c] = add(x.next[c], key, d + 1);
            }
            return x;
        }

        private void collect(Node x, StringBuilder prefix, Queue<String> results) {
            if (x == null) return;
            if (x.isString) {
                // HACK: found 1 prefix this is enough!
                results.enqueue(prefix.toString());
                return;
            }

            for (char c = 0; c < R; c++) {
                prefix.append(chr(c));
                collect(x.next[c], prefix, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }
}
