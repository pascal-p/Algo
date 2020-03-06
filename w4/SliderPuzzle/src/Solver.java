import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private final int totMoves;
    private SearchNode searchNode;
    private boolean solvable;

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

            // if tie, use Hamming dist:
            if (prioThis - prioThat == 0) {
                // this.dist = this.board.hamming();
                prioThis = this.board.hamming() + this.numMoves;

                // that.dist = that.board.hamming();
                // prioThat = that.dist + that.numMoves;
            }

            return prioThis - prioThat;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException("initial should not be null");

        MinPQ<SearchNode> pq = new MinPQ<SearchNode>(); // init pq for initial board
        SearchNode cnode = new SearchNode(initial);
        this.searchNode = cnode; // will be mutated later
        assert this.searchNode != null : "(1) searchNode must be defined, got null";
        pq.insert(cnode);

        if (!solvable()) {
            this.totMoves = -1;
            this.searchNode = null;
            return;
        }

        // then process node in order until goal reached
        int cMove = -1;
        long startTime = System.currentTimeMillis();

        while (true) {
            cnode = pq.delMin();
            Board b = cnode.board;
            if (b.isGoal()) break;

            if (cMove < cnode.numMoves) {
                cMove = cnode.numMoves;
                System.out.println("Move: " + cnode.numMoves + " / elapsed time (ms): "
                                           + (System.currentTimeMillis() - startTime));
            }
            // otherwise add each neighbors (if not already added)
            for (Board nb : b.neighbors()) {
                if (cnode.prev != null && nb.equals(cnode.prev.board))
                    continue; // avoid re-adding (already processed) board
                pq.insert(new SearchNode(nb, cnode.numMoves + 1, cnode));
            }

        }
        long timeElapsed = System.currentTimeMillis() - startTime;
        System.out.println("Execution time (roughly): " + timeElapsed + "ms");

        // we have a solution
        this.totMoves = cnode.numMoves;
        this.searchNode = cnode;
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return this.solvable;
    }

    // min number of moves to solve initial board
    public int moves() {
        return this.totMoves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        SearchNode cnode = this.searchNode;
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
        //
        Board initial = new Board(tiles);
        System.out.println("Initial board: " + initial);

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

    private boolean solvable() {
        assert this.searchNode != null : "(2) searchNode must be defined, got null";

        Board b = this.searchNode.board;
        assert b != null;

        // (1) an odd size board is solvable iff the number of inversions (hamming distance) is even
        // -5 % 2 == -1 which is odd (but is not equal to 1) - do (x & 1) = 1 or x %2 != 0
        // even  <-> x & 1 == 0  , odd <=> x & 1 == 1
        if ((b.dimension() & 1) == 1 && (b.numInversions() & 1) == 0) {
            // System.out.println("Board has odd dimension: " + b.dimension()
            //                            + " with even number of inversions: "
            //                            + b.numInversions());
            this.solvable = true;
            return true;
        }

        // (2) and even size board is solvable iff the number of inversions + row of blank tile
        // (counting from 0) is odd
        // NOTE: Change public API
        if ((b.dimension() & 1) == 0 &&
                ((b.numInversions() + b.locateBlankTile()[0]) & 1) == 1) {
            // System.out.println("Board has even dimension: " + b.dimension()
            //                            + "\nwith number of inversions "
            //                            + "\n+ row of blank tile is odd - hamming: "
            //                            + b.numInversions() + " / row: " + b.locateBlankTile()[0]);
            this.solvable = true;
            return true;
        }

        System.out.println("KO - board is not solvable...");
        this.solvable = false;
        return false;
    }
}
