import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private final int totMoves;
    private final SearchNode lastSeartchNode;

    private class SearchNode implements Comparable<SearchNode> {
        final Board board;
        int numMoves = 0;
        final SearchNode prev;
        private final int dist;

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
            // ordered by priority == manHanttanDist (or hammingDist) + numMoves
            int prioThis = this.dist + this.numMoves;
            int prioThat = that.dist + that.numMoves;
            return prioThis - prioThat;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException("initial should not be null");

        MinPQ<SearchNode> pq = new MinPQ<SearchNode>(); // init pq
        SearchNode snode = new SearchNode(initial);

        if (!isSolvable()) { // check initial board is solvable
            this.totMoves = 0;
            this.lastSeartchNode = snode;
            return;
        }

        pq.insert(snode);

        // then process node in order until goal reached, unless unsolvable...
        SearchNode cnode;
        while (true) {
            cnode = pq.delMin();
            Board b = cnode.board;
            if (b.isGoal()) break;

            // otherwise add each neighbors (if not already added)
            for (Board nb : b.neighbors()) {
                if (nb.equals(b)) continue; // avoid re-adding (already processed) board
                pq.insert(new SearchNode(nb, cnode.numMoves + 1, cnode));
            }
        }
        // produce solution
        this.totMoves = cnode.numMoves;
        this.lastSeartchNode = cnode;
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        // TODO
        return true;
    }

    // min number of moves to solve initial board
    public int moves() {
        return this.totMoves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        Stack<Board> sb = new Stack<>();
        SearchNode cnode = this.lastSeartchNode;
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
