import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* *****************************************************************************
 *  Name: Pascal
 *  Date:  May 2020
 *  Description: Shortest Common Ancestor in Digraph
 **************************************************************************** */
public class SAP {
    private class Pair {
        private final int ancestor;
        private final int distance;

        public Pair(int ancestor, int distance) {
            this.ancestor = ancestor;
            this.distance = distance;
        }

        public int getAncestor() {
            return this.ancestor;
        }

        public int getDistance() {
            return this.distance;
        }
    }

    private final Digraph graph;
    private final Map<String, Pair> cache;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        graph = new Digraph(G);
        cache = new HashMap<String, Pair>();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        checkValidVertex(v);
        checkValidVertex(w);
        Pair pair = calcAndCache(v, w);
        return pair.getDistance();  // -1?
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        checkValidVertex(v);
        checkValidVertex(w);
        Pair pair = calcAndCache(v, w);
        return pair.getAncestor();
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        checkValidVertex(v);
        checkValidVertex(w);
        Pair pair = calcAndCache(v, w);
        return pair.getDistance();  // -1?
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkValidVertex(v);
        checkValidVertex(w);
        Pair pair = calcAndCache(v, w);
        return pair.getAncestor();
    }

    private Pair calcAndCache(int v, int w) {
        String key = toKey(v, w);
        if (cache.containsKey(key)) return cache.get(key);

        String okey = toKey(w, v);
        if (cache.containsKey(okey)) return cache.get(okey);

        BreadthFirstDirectedPaths bfdpV = new BreadthFirstDirectedPaths(this.graph, v);
        BreadthFirstDirectedPaths bfdpW = new BreadthFirstDirectedPaths(this.graph, w);

        Pair pair = calcLCADist(bfdpV, bfdpW);
        cache.put(key, pair);
        cache.put(okey, pair);
        return pair;
    }

    private Pair calcAndCache(Iterable<Integer> v, Iterable<Integer> w) {
        String key = toKey(v, w);
        if (cache.containsKey(key)) return cache.get(key);

        String okey = toKey(w, v);
        if (cache.containsKey(okey)) return cache.get(okey);

        BreadthFirstDirectedPaths bfdpV = new BreadthFirstDirectedPaths(this.graph, v);
        BreadthFirstDirectedPaths bfdpW = new BreadthFirstDirectedPaths(this.graph, w);

        Pair pair = calcLCADist(bfdpV, bfdpW);
        cache.put(key, pair);
        cache.put(okey, pair);
        return pair;
    }

    private Pair calcLCADist(BreadthFirstDirectedPaths bfdpV, BreadthFirstDirectedPaths bfdpW) {
        // find common ancestors
        List<Integer> ancestors = new ArrayList<Integer>();
        for (int x = 0; x < graph.V(); x++)
            if (bfdpV.hasPathTo(x) && bfdpW.hasPathTo(x)) ancestors.add(x);

        // determine least common ancestors
        int lca = -1;
        int minDist = Integer.MAX_VALUE;

        if (!ancestors.isEmpty()) {
            lca = ancestors.get(0);
            minDist = bfdpV.distTo(lca) + bfdpW.distTo(lca);

            for (int ix = 1; ix < ancestors.size(); ix++) {
                int ca = ancestors.get(ix);
                int dist = bfdpV.distTo(ca) + bfdpW.distTo(ca);
                if (dist < minDist) {
                    minDist = dist;
                    lca = ca;
                }
            }
        }

        minDist = minDist == Integer.MAX_VALUE ? -1 : minDist;
        return new Pair(lca, minDist);
    }

    private void checkValidVertex(int v) {
        // assuming vertex start at 0
        if (v >= 0 && v < graph.V()) return;
        throw new IllegalArgumentException("Not a valid vertex");
    }

    private void checkValidVertex(Iterable<Integer> v) {
        if (v == null)
            throw new IllegalArgumentException("Not a valid vertex");

        // assuming vertex start at 0
        for (Integer u : v)
            if (u != null && u >= 0 && u < graph.V())
                continue;
            else
                throw new IllegalArgumentException("Not a valid vertex");
    }

    private String toKey(int v, int w) {
        return Integer.toString(v) + "-" + Integer.toString(w);
    }

    private String toKey(Iterable<Integer> v, Iterable<Integer> w) {
        String s = "";
        for (int u : v) s = s + "-" + Integer.toString(u);
        s = s + "/";
        for (int u : w) s = s + "-" + Integer.toString(u);
        return s;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // TODO
    }
}
