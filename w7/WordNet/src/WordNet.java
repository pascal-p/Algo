/* *****************************************************************************
 *  Name: Pascal P
 *  Date:  May 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WordNet {
    /*
     * Build the WordNet digraph: each vertex v is an integer that represents a synset,
     * and each directed edge v → w represents that w is a hypernym of v.
     * The WordNet digraph is a rooted DAG: it is acyclic and has one vertex - the root - that
     * is an ancestor of every other vertex.
     * However, it is not necessarily a tree because a synset can have more than one hypernym.
     */

    /*
     * Corner cases.  Throw an IllegalArgumentException in the following situations:
     *    Any argument to the constructor or an instance method is null
     *    The input to the constructor does not correspond to a rooted DAG.
     *    Any of the noun arguments in distance() or sap() is not a WordNet noun.
     */
    private final Digraph graph;
    private final HashMap<Integer, String> wnHshMap;        // from id to noun
    private final HashMap<String, Set<Integer>> revHshMap;  // from noun to set of ids
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("sysnsets or hypernyms must be defined");
        /*
         * Read in and parse the files described in the assignment, synsets.txt and hypernyms.txt.
         * Don't worry about storing the data in any data structures yet. Test that you are parsing the input correctly before proceeding.

         * Create a data type WordNet. Divide the constructor into two (or more) subtasks (private methods).
         * - Read in the synsets.txt file and build appropriate data structures.
         *   Record the number of synsets for later use.
         *
         * - Read in the hypernyms.txt file and build a Digraph.
         *   For this input, your digraph should have 82,192 vertices and 84,505 edges (but do not hardwire either of these numbers into your program because it must work for any valid input files).
         *
         */
        this.wnHshMap = new HashMap<Integer, String>();
        this.revHshMap = new HashMap<String, Set<Integer>>();
        procSynSet(synsets);
        assert this.wnHshMap.size() > 0 : "number of ids/Synsets is non-negative";
        assert this.revHshMap.size() > 0 : "number of Synsets/ids is non-negative";

        this.graph = new Digraph(this.wnHshMap.size());
        procHyperbyns(hypernyms);
        // validity checks
        if (hasCycle() || !hasUniqRoot())
            throw new IllegalArgumentException("this graph is not a DAG");

        this.sap = new SAP(this.graph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return this.revHshMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null || word.equals(""))
            throw new IllegalArgumentException("not a noun");
        return revHshMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (isNoun(nounA) && isNoun(nounB)) {
            Set<Integer> nounAIds = revHshMap.get(nounA);
            Set<Integer> nounBIds = revHshMap.get(nounB);
            return sap.length(nounAIds, nounBIds);
        }
        throw new IllegalArgumentException("not a noun!");
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (isNoun(nounA) && isNoun(nounB)) {
            Set<Integer> nounAIds = revHshMap.get(nounA);
            Set<Integer> nounBIds = revHshMap.get(nounB);
            int ca = sap.ancestor(nounAIds, nounBIds);
            return wnHshMap.get(ca);
        }
        throw new IllegalArgumentException("not a noun!");
    }

    private void procSynSet(String synset) {
        // IN class: https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/In.java.html
        try {
            In in = new In(synset);
            while (!in.isEmpty()) {
                String str = in.readLine();
                String[] aryStr = str.split(",");
                assert aryStr.length == 3 : "Expecting 3 items only";

                // only interested in 2 first fields
                int id = Integer.parseInt(aryStr[0]);
                this.wnHshMap.put(id, aryStr[1]);

                // and now for reverse map
                String[] kvals = aryStr[1].split(" "); // Split further
                for (String kval : kvals) {
                    if (!this.revHshMap.containsKey(kval)) // Create set if not existing yet
                        this.revHshMap.put(kval, new HashSet<Integer>());

                    // add new id to existing set
                    Set<Integer> hs = this.revHshMap.get(kval);
                    hs.add(id);
                    this.revHshMap.put(kval, hs);
                }
            }
            in.close();
        }
        catch (IllegalArgumentException err) {
            System.err.println(err);
        }
        // finally in.close - no file may not exist at all
    }

    private void procHyperbyns(String hypernyms) {
        try {
            In in = new In(hypernyms);
            while (!in.isEmpty()) {
                String[] aryStr = in.readLine().split(",");
                int id = Integer.parseInt(aryStr[0]);

                for (int ix = 1; ix < aryStr.length; ix++)
                    this.graph.addEdge(id, Integer.parseInt(aryStr[ix]));
            }
            in.close();
        }
        catch (IllegalArgumentException err) {
            System.err.println(err);
        }
    }

    private boolean hasUniqRoot() {
        // returns true if there exist one root exactly
        int roots = 0;
        for (int x = 0; x < this.graph.V(); x++) {
            Iterator<Integer> adjIter = this.graph.adj(x).iterator();
            if (!adjIter.hasNext()) roots++;
            if (roots > 1) return false;
        }
        return roots == 1;
    }

    private boolean hasCycle() {
        // returns true if a directed cycle is detected
        DirectedCycle cg = new DirectedCycle(this.graph);
        return cg.hasCycle();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        String synset = new String(args[0]);
        String hypernyms = new String(args[1]);

        WordNet wn = new WordNet(synset, hypernyms);
        // StdOut.printf("num of synsset = %d, hyperyms = %d\n", wn.numSynset, wn.numSynset);
        StdOut.printf("num of synsset = %d, hyperyms = %d\n", wn.wnHshMap.size(),
                      wn.revHshMap.size());

        for (String noun : wn.nouns()) {
            StdOut.printf("\tFound noun: %s\n", noun);
        }

        StdOut.printf("is e a noun? %s\n", wn.isNoun("e"));
        StdOut.printf("is yy a noun? %s\n", wn.isNoun("yy"));

        System.out.println("Has unique Root? " + wn.hasUniqRoot());
        System.out.println("Has Cycle? " + wn.hasCycle());

        StdOut.println("Done for now");
    }
}
