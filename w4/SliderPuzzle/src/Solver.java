import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private final int totMoves;
    private final SearchNode lastSearchNode;
    private char solvable = 0;

    private class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private int numMoves = 0;
        private final SearchNode prev;
        private final int dist; // cache distance

        public SearchNode(Board board) {
            this.board = board;
            this.prev = null;
            this.dist = board.manhattan();
        }

        public SearchNode(Board board, int numMoves, SearchNode prev) {
            this.board = board;
            this.numMoves = numMoves;
            this.prev = prev;
            this.dist = board.manhattan();
        }

        public int compareTo(SearchNode that) {
            // ordered by priority == ManHanttan dist (or Hamming dist) + numMoves
            int prioThis = this.dist + this.numMoves;
            int prioThat = that.dist + that.numMoves;
            return prioThis - prioThat;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException("initial should not be null");

        MinPQ<SearchNode> pqi = new MinPQ<SearchNode>(); // init pqi for initial board
        MinPQ<SearchNode> pqt = new MinPQ<SearchNode>(); // init pqt for twin board
        SearchNode snode = new SearchNode(initial);
        SearchNode tnode = new SearchNode(initial.twin());
        pqi.insert(snode);
        pqt.insert(tnode);

        // then process node (alternating between initial and twin) in order until goal reached
        SearchNode cnode;
        char ind = '0';
        MinPQ<SearchNode> pq = pqi;

        while (true) {
            cnode = pq.delMin();
            Board b = cnode.board;
            if (b.isGoal()) break;

            // otherwise add each neighbors (if not already added)
            for (Board nb : b.neighbors()) {
                if (cnode.prev != null && nb.equals(cnode.prev.board))
                    continue; // avoid re-adding (already processed) board
                pq.insert(new SearchNode(nb, cnode.numMoves + 1, cnode));
            }

            // now switch
            pq = (pq == pqi) ? pqt : pqi;
            ind = (ind == '0') ? '1' : '0';
        }
        if (ind == '0') { // we have a solution
            this.totMoves = cnode.numMoves;
            this.lastSearchNode = cnode;
            this.solvable = 1;
        }
        else { // unsolvable
            this.totMoves = -1;
            this.lastSearchNode = null;
            this.solvable = 2;
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return (this.solvable == 1);
    }

    // min number of moves to solve initial board
    public int moves() {
        return this.totMoves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        SearchNode cnode = this.lastSearchNode;
        if (cnode == null) return null;

        Stack<Board> sb = new Stack<>();
        while (cnode != null) {
            sb.push(cnode.board);
            cnode = cnode.prev;
        }
        return sb;
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int ix = 0; ix < n; ix++)
            for (int jx = 0; jx < n; jx++)
                tiles[ix][jx] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
