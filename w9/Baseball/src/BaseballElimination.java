/* *****************************************************************************
 *  Name: Pascal
 *  Date: June 2020
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BaseballElimination {
    private static final double EPS = 0.0001;

    private final HashMap<String, Integer> teamNames;
    private final HashMap<Integer, String> teamIds;

    private final int[] win;     // wins[i]
    private final int[] loss;    // loss[i]
    private final int[] rem;     // remaining[i]
    private final int[][] gl;    // game left[i][j]
    private final HashMap<Integer, Set<Integer>> subset; // team(s) beating team[i]

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        Pair<In, Integer> pair = getTeamNum(filename);
        int n = pair.s;
        //
        this.teamNames = new HashMap<>(n);
        this.teamIds = new HashMap<>(n);
        this.win = new int[n];
        this.loss = new int[n];
        this.rem = new int[n];
        this.gl = new int[n][n];
        //
        processInput(filename, pair);
        this.subset = new HashMap<Integer, Set<Integer>>();
    }

    // number of teams
    public int numberOfTeams() {
        return this.teamNames.size();
    }

    // all teams
    public Iterable<String> teams() {
        return this.teamNames.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        checkTeamExists(team);
        int ix = this.teamNames.get(team);
        return this.win[ix];
    }

    // number of losses for given team
    public int losses(String team) {
        checkTeamExists(team);
        int ix = this.teamNames.get(team);
        return this.loss[ix];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        checkTeamExists(team);
        int ix = this.teamNames.get(team);
        return this.rem[ix];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        checkTeamExists(team1);
        checkTeamExists(team2);
        int ix = this.teamNames.get(team1);
        int jx = this.teamNames.get(team2);
        return this.gl[ix][jx];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        checkTeamExists(team);
        this.subset.clear();

        int ix = this.teamNames.get(team);
        this.subset.put(ix, new HashSet<Integer>());

        // (i) trivial elimination?
        Pair<Boolean, Integer> yes = checkTrivialElimination(ix);
        if (yes.f) {
            Set<Integer> hs = this.subset.get(ix);
            hs.add(yes.s);            // add team id that beats team ix
            this.subset.put(ix, hs);
            return true;
        }

        // (ii) non-trivial elimination => FlowNetwork
        int n = this.numberOfTeams();
        int nv = (n - 1) * (n - 2) / 2 + n + 1;         // num. of vertices
        Triple<FlowNetwork, HashMap<Integer, Integer>, Double> t = genFlowNetwork(ix, n, nv);

        FordFulkerson ff = new FordFulkerson(t.f, 0, nv - 1);
        /*
         * If all edges in the maxflow that are pointing from s are full, then this corresponds to
         * assigning winners to all of the remaining games in such a way that no team wins more
         * games than x.
         * If some edges pointing from s are not full, then there is NO scenario in which team x
         * can win the division (so it is mathematically eliminated)
         */
        boolean res = !(Math.abs(ff.value() - t.last) < EPS);
        if (res) {
            for (int v : t.s.keySet()) {
                if (ff.inCut(v)) {
                    int jx = t.s.get(v);
                    Set<Integer> hs = this.subset.get(ix);
                    hs.add(jx);            // add team id that beats team ix
                    this.subset.put(ix, hs);
                }
            }
        }
        return res;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        checkTeamExists(team);
        int ix = this.teamNames.get(team);

        if (!this.subset.containsKey(ix)) isEliminated(team);
        assert this.subset.containsKey(ix) : "Expected key ix " + ix + " to be defined";

        if (this.subset.get(ix).isEmpty()) return null;

        String[] ary = new String[this.subset.get(ix).size()];
        int kx = 0;
        for (int jx : this.subset.get(ix)) ary[kx++] = this.teamIds.get(jx);
        Iterable<String> iter = Arrays.asList(ary);
        return iter;
    }

    //
    // Private Helpers
    //
    private Triple<FlowNetwork, HashMap<Integer, Integer>, Double> genFlowNetwork(int ix, int n,
                                                                                  int v) {
        int pwinix = this.win[ix] + this.rem[ix];      // potential num of wins for team ix
        int s = 0, t = v - 1;                          // source and target for fln
        double totCap = 0.0;
        FlowNetwork fln = new FlowNetwork(v);

        // mapping (node number) nn / id_team (id) => nn: 1, 2, 3 ---> id_t: 0, 2, 3 / Here ix == 1
        HashMap<Integer, Integer> mapnnid = new HashMap<>(n - 1);
        HashMap<Integer, Integer> mapidnn = new HashMap<>(n - 1);
        int teamId = -1;
        int jx = 1;
        while (jx < n) {
            teamId++;
            if (teamId == ix) continue;
            mapnnid.putIfAbsent(jx, teamId);
            mapidnn.putIfAbsent(teamId, jx);
            jx++;
        }

        int kn = n;          // init node/vertex number for vertices: idT1 - idT2
        int count1 = n - 1;  // n - 1 teams to consider
        int idT1 = 0;

        while (count1 > 0) {
            if (idT1 == ix) idT1 = inc(idT1, n);
            int idT2 = inc(idT1, n);
            int count2 = n - 2;  // n - 2 teams to consider

            while (count2 > 0) {
                if (idT2 == ix) idT2 = inc(idT2, n);
                if (idT2 <= idT1) break;
                assert (idT2 != idT1) && (idT2 != ix) :
                        "Expected idT2 " + idT2 + " to be diff of idT1 " + idT1 + " and ix " + ix;
                //
                fln.addEdge(new FlowEdge(s, kn, (double) this.gl[idT1][idT2]));
                fln.addEdge(new FlowEdge(kn, mapidnn.get(idT1), Double.POSITIVE_INFINITY));
                fln.addEdge(new FlowEdge(kn, mapidnn.get(idT2), Double.POSITIVE_INFINITY));
                totCap += (double) this.gl[idT1][idT2];
                //
                kn++;
                idT2 = inc(idT2, n);
                count2--;
            }
            idT1 = (idT1 + 1) % n;
            count1--;
        }
        // now add edge to (sink) t
        for (jx = 1; jx < n; jx++) {
            int capa = pwinix - this.win[mapnnid.get(jx)];
            assert capa >= 0 : "Expected capacity " + capa + " to be > 0 - Got: " + capa;
            fln.addEdge(new FlowEdge(jx, t, capa));
        }
        return new Triple<>(fln, mapnnid, totCap);
    }

    private int inc(int x, int n) {
        return (x + 1) % n;
    }

    private Pair<Boolean, Integer> checkTrivialElimination(int ix) {
        int ixScore = this.win[ix] + this.rem[ix];

        for (int jx = 0; jx < this.numberOfTeams(); jx++) {
            if (jx == ix) continue;
            else if (ixScore < this.win[jx])
                return new Pair<Boolean, Integer>(Boolean.TRUE, jx);
        }
        return new Pair<Boolean, Integer>(Boolean.FALSE, -1);
    }

    private Pair<In, Integer> getTeamNum(String filename) {
        try {
            In in = new In(filename);
            int nbTeams = 0;

            if (!in.isEmpty()) {
                String str = in.readLine();
                nbTeams = Integer.parseInt(str);
            }

            return new Pair<In, Integer>(in, nbTeams);
        }
        catch (IllegalArgumentException err) {
            System.err.println("Error while processing file " + filename + " / got: " + err);
            throw err;
        }
    }

    private void processInput(String filename, Pair<In, Integer> p) {
        try {
            In in = p.f;
            int n = p.s, ix = 0, shift = 4; // team will numbered from 0
            String spaces = "\\s+";

            while (!in.isEmpty()) {
                String str = in.readLine();
                str = str.replaceAll("\\A\\s+", ""); // remove starting spaces

                // Boston      69 66 27   8 2 0 0 3
                // <team>      w  l  r    <n g[i][0..n-1]>
                String[] aryStr = str.split(spaces);
                assert aryStr.length == shift + n :
                        "Expecting " + (shift + n) + " elements, got: " + aryStr.length + " ==> ["
                                + aryStr[0] + "]";

                // team name / id
                this.teamNames.putIfAbsent(aryStr[0], ix);
                this.teamIds.putIfAbsent(ix, aryStr[0]);
                // w. l, r
                this.win[ix] = Integer.parseInt(aryStr[1]);
                this.loss[ix] = Integer.parseInt(aryStr[2]);
                this.rem[ix] = Integer.parseInt(aryStr[3]);
                for (int jx = 0; jx < n; jx++)
                    this.gl[ix][jx] = Integer.parseInt(aryStr[jx + shift]);
                ix++;
            }
            in.close();
        }
        catch (IllegalArgumentException err) {
            System.err.println("Error while processing file " + filename + " / got: " + err);
            throw err;
        }
    }

    private void checkTeamExists(String name) {
        if (name == null)
            throw new IllegalArgumentException("Team name cannot be null");

        if (!this.teamNames.containsKey(name))
            throw new IllegalArgumentException(
                    "Team name is not defined in current recorded teams");

        // All good
    }

    private class Pair<F, S> {
        final F f;
        final S s;

        public Pair(F f, S s) {
            this.f = f;
            this.s = s;
        }
    }

    private class Triple<F, S, L> {
        final F f;
        final S s;
        final L last;

        public Triple(F f, S s, L last) {
            this.f = f;
            this.s = s;
            this.last = last;
        }
    }
}
