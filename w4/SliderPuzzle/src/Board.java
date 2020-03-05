import edu.princeton.cs.algs4.Queue;

public class Board {
    private final char[][] tiles;
    private final int n;
    private final int hdist;
    private final int mdist;
    private final boolean goal;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)

    public Board(int[][] tiles) {
        this.n = tiles.length;
        this.tiles = convBoard(tiles);
        this.hdist = hammingDist();
        this.mdist = manhattanDist();
        this.goal = this.goal();
    }

    private Board(char[][] tiles) {
        this.n = tiles.length;
        this.tiles = cpyBoard(tiles);
        this.hdist = hammingDist();
        this.mdist = manhattanDist();
        this.goal = this.goal();
    }


    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int ix = 0; ix < n; ix++) {
            for (int jx = 0; jx < n; jx++) {
                s.append(String.format("%2d ", (int) tiles[ix][jx]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return this.n;
    }

    // number of tiles out of place - immutable
    public int hamming() {
        return this.hdist;
    }

    // sum of Manhattan distances between tiles and goal - immutable
    public int manhattan() {
        return this.mdist;
    }

    // is this board the goal board? - immutable
    public boolean isGoal() {
        return this.goal;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        // cf. FAQ => If a and b are of type char[][], then use Arrays.deepEquals(Object[] a1, Object[] a2)
        if (y == null) return false;
        if (y == this) return true;
        if (y.getClass() != this.getClass()) return false;

        Board that = (Board) y;
        if (this.n != that.n) return false;
        for (int ix = 0; ix < n; ix++) {
            for (int jx = 0; jx < n; jx++) {
                if (this.tiles[ix][jx] != that.tiles[ix][jx]) // char cmp
                    return false;
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> qb = new Queue<>();

        // find where is blank tile (aka 0)
        int[] coord = locateBlank();
        assert coord[0] != -1 && coord[1] != -1 : "no blank tile in the board? Check!";
        int x = coord[0], y = coord[1];

        if (x - 1 >= 0) {
            char[][] ntiles = cpyBoard(this.tiles);
            qb.enqueue(new Board(swapTile(ntiles, x, y, x - 1, y)));
        }

        if (x + 1 < n) {
            char[][] ntiles = cpyBoard(this.tiles);
            qb.enqueue(new Board(swapTile(ntiles, x, y, x + 1, y)));
        }

        if (y - 1 >= 0) {
            char[][] ntiles = cpyBoard(this.tiles);
            qb.enqueue(new Board(swapTile(ntiles, x, y, x, y - 1)));
        }

        if (y + 1 < n) {
            char[][] ntiles = cpyBoard(this.tiles);
            qb.enqueue(new Board(swapTile(ntiles, x, y, x, y + 1)));
        }
        return qb;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        char[][] ntiles = cpyBoard(this.tiles);

        // exchange first and last (but avoid blank tile)
        int px1 = 1, px2 = n * n;
        int[] coordO = pos2Coord(px1);
        int[] coordD = pos2Coord(px2);

        if ((int) this.tiles[coordO[0]][coordO[1]] == 0) {
            px1++;
            coordO = pos2Coord(px1);
        }
        else if ((int) this.tiles[coordD[0]][coordD[1]] == 0) {
            px2--;
            coordD = pos2Coord(px2);
        }
        assert px1 != px2 : "px1 must be different from px2, but got equality px1: " + px1;
        return new Board(swapTile(ntiles, coordO[0], coordO[1], coordD[0],
                                  coordD[1])); // board should be immutable
    }

    /*
     * Private Implementation
     */

    private char[][] convBoard(int[][] itiles) {
        char[][] ctiles = new char[this.n][this.n];
        for (int ix = 0; ix < n; ix++) {
            for (int jx = 0; jx < n; jx++) {
                ctiles[ix][jx] = (char) itiles[ix][jx];
            }
        }
        return ctiles;
    }

    private char[][] cpyBoard(char[][] itiles) {
        char[][] ctiles = new char[this.n][this.n];
        for (int ix = 0; ix < n; ix++) {
            for (int jx = 0; jx < n; jx++) {
                ctiles[ix][jx] = itiles[ix][jx];
            }
        }
        return ctiles;
    }

    private int hammingDist() {
        int d = 0;
        for (int ix = 0; ix < n; ix++) {
            for (int jx = 0; jx < n; jx++) {
                int k = (int) this.tiles[ix][jx];
                if (k == 0) continue;          // ignore blank tile
                d += (k == coord2Pos(ix, jx)) ? 0 : 1;
            }
        }
        return d;
    }

    private int manhattanDist() {
        int d = 0;
        for (int ix = 0; ix < n; ix++) {
            for (int jx = 0; jx < n; jx++) {
                int k = (int) tiles[ix][jx];
                if (k == 0) continue;          // ignore blank tile
                int[] coord = pos2Coord(k);
                d += Math.abs(coord[0] - ix) + Math.abs(coord[1] - jx);
            }
        }
        return d;
    }

    private boolean goal() {
        for (int ix = 0; ix < n - 1; ix++) {
            for (int jx = 0; jx < n; jx++) {
                if ((int) this.tiles[ix][jx] != coord2Pos(ix, jx))
                    return false;
            }
        }
        // last row
        for (int jx = 0; jx < n - 1; jx++) {
            if ((int) this.tiles[n - 1][jx] != coord2Pos(n - 1, jx))
                return false;
        }
        return true;
    }

    /*
     * ix in [0, n), jx in [0, n)
     */
    private int coord2Pos(int ix, int jx) {
        int px = n * ix + jx + 1;
        assert px >= 1 && px <= n * n : "pos should be between 1 and " + n + " / got: " + px;
        return px;
    }

    /*
     * 1 <= px <= n*n
     * ix in [0, n), jx in [0, n)
     */
    private int[] pos2Coord(int px) {
        int ix = (px - 1) / n;
        int jx = (px - 1) % n;
        assert ix >= 0 && ix < n : "ix should be between 0 and " + (n - 1) + " / got: " + ix;
        assert jx >= 0 && jx < n : "jx should be between 0 and " + (n - 1) + " / got: " + jx;
        int[] coord = { ix, jx };
        return coord;
    }

    private int[] locateBlank() {
        for (int ix = 0; ix < n; ix++) {
            for (int jx = 0; jx < n; jx++) {
                if ((int) this.tiles[ix][jx] == 0) return new int[] { ix, jx };
            }
        }
        return new int[] { -1, -1 };
    }

    private char[][] swapTile(char[][] ctiles, int x1, int y1, int x2, int y2) {
        char t = ctiles[x1][y1];
        ctiles[x1][y1] = ctiles[x2][y2];
        ctiles[x2][y2] = t;
        return ctiles;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        testEquals();
        testHamming();
        testManhattan();
        testGoal();
        testNeighbors();
        testMyTwin();
    }

    private static void testEquals() {
        System.out.print("testEquals: ");
        char[][] tiles1 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 } };
        char[][] tiles2 = { { 1, 2 }, { 4, 0 } };
        char[][] tiles3 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 } };
        char[][] tiles4 = { { 1, 2, 3 }, { 4, 5, 0 }, { 6, 7, 8 } };

        Board b1 = new Board(tiles1);
        Board b2 = new Board(tiles2);
        assert !b1.equals(b2) : "board 1 and 2 must be different";

        Board b3 = new Board(tiles3);
        assert b1.equals(b3) : "board 1 and 3 must be eqwals";

        Board b4 = new Board(tiles4);
        assert !b1.equals(b4) : "board 1 and 4 must be different";

        // b4 = null;
        // assert !b1.equals(b4) : "board 1 and 5 must be different";
        System.out.println("ok");
    }

    private static void testHamming() {
        System.out.print("testHamming: ");
        char[][] gtiles = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 } };
        Board gboard = new Board(gtiles);
        assert gboard.dimension() == 3 : "goal board has dimension 3x3";
        int hdG = gboard.hamming();
        assert gboard.hamming() == 0 : "goal board should have hamming dist of 0, got: " + hdG;

        char[][] ctiles1 = { { 8, 1, 3 }, { 4, 0, 2 }, { 7, 6, 5 } };
        Board cboard = new Board(ctiles1);
        assert cboard.dimension() == 3 : "cand board has dimention 3x3";
        int hdExp = 5, hdAct = cboard.hamming();
        assert hdAct == hdExp :
                "cand board should have hamming dist of " + hdExp + ", got: " + hdAct;

        char[][] ctiles2 = { { 1, 8, 3 }, { 2, 5, 0 }, { 7, 4, 6 } };
        cboard = new Board(ctiles2);
        assert cboard.dimension() == 3 : "cand board has dimention 3x3";
        hdExp = 4;
        hdAct = cboard.hamming();
        assert hdAct == hdExp :
                "cand board should have hamming dist of " + hdExp + ", got: " + hdAct;

        char[][] ctiles3 = {
                { 1, 2, 8, 4, 5 }, { 6, 7, 9, 3, 10 }, { 11, 16, 13, 14, 15 },
                { 12, 17, 19, 18, 20 }, { 21, 23, 22, 0, 24 }
        };
        cboard = new Board(ctiles3);
        assert cboard.dimension() == 5 : "cand board has dimention 5x5";
        hdExp = 10;
        hdAct = cboard.hamming();
        assert hdAct == hdExp :
                "cand board should have hamming dist of " + hdExp + ", got: " + hdAct;
        System.out.println("ok");
    }

    private static void testManhattan() {
        System.out.print("testManhattan: ");
        char[][] gtiles = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 } };
        Board gboard = new Board(gtiles);
        assert gboard.dimension() == 3 : "goal board has dimension 3x3";
        int hmG = gboard.manhattan();
        assert hmG == 0 : "goal board should have manhattan dist of 0, got: " + hmG;

        char[][] ctiles1 = { { 8, 1, 3 }, { 4, 0, 2 }, { 7, 6, 5 } };
        Board cboard = new Board(ctiles1);
        assert cboard.dimension() == 3 : "cand board has dimention 3x3";
        int hmExp = 10, hmAct = cboard.manhattan();
        assert hmAct == hmExp :
                "cand board should have manhattan dist of " + hmExp + ", got: " + hmAct;

        char[][] ctiles2 = { { 1, 8, 3 }, { 2, 5, 0 }, { 7, 4, 6 } };
        cboard = new Board(ctiles2);
        assert cboard.dimension() == 3 : "cand board has dimention 3x3";
        hmExp = 7;
        hmAct = cboard.manhattan();
        assert hmAct == hmExp :
                "cand board should have manhattan dist of " + hmExp + ", got: " + hmAct;
        //
        char[][] ctiles3 = {
                { 1, 2, 8, 4, 5 }, { 6, 7, 9, 3, 10 }, { 11, 16, 13, 14, 15 },
                { 12, 17, 19, 18, 20 }, { 21, 23, 22, 0, 24 }
        };
        cboard = new Board(ctiles3);
        assert cboard.dimension() == 5 : "cand board has dimention 5x5";
        hmExp = 13;
        hmAct = cboard.manhattan();
        assert hmAct == hmExp :
                "cand board should have hamming dist of " + hmExp + ", got: " + hmAct;
        System.out.println("ok");
    }

    private static void testGoal() {
        System.out.print("testGoal: ");
        char[][] tiles1 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 } };
        char[][] tiles2 = { { 1, 2 }, { 4, 0 } };
        char[][] tiles3 = { { 1, 2, 3 }, { 4, 5, 0 }, { 6, 7, 8 } };

        Board b1 = new Board(tiles1);
        assert b1.isGoal() : "board b1 should be the goal!";

        Board b2 = new Board(tiles2);
        assert !b2.isGoal() : "board 2 is not the goal (and cannot)";

        Board b3 = new Board(tiles3);
        assert !b3.isGoal() : "board b3 is not the goal";
        System.out.println("ok");
    }

    private static void testNeighbors() {
        System.out.print("testNeighbors: ");
        char[][] tiles1 = { { 1, 0, 3 }, { 4, 2, 5 }, { 7, 8, 6 } };
        Board b1 = new Board(tiles1);

        System.out.println("\ninitial board: ");
        System.out.println(b1);

        System.out.println("neighbors board(s): ");
        int n = 0;
        for (Board b : b1.neighbors()) {
            System.out.println(b);
            n++;
        }
        assert n == 3 : "This board should have " + n + "neighbors. Got: " + n;
        //
        char[][] tiles2 = { { 0, 1, 3 }, { 4, 2, 5 }, { 7, 8, 6 } };
        b1 = new Board(tiles2);
        System.out.println("\ninitial board: ");
        System.out.println(b1);

        System.out.println("neighbors board(s): ");
        n = 0;
        for (Board b : b1.neighbors()) {
            System.out.println(b);
            n++;
        }
        assert n == 2 : "This board should have " + n + "neighbors. Got: " + n;

        System.out.println("ok");
    }

    private static void testMyTwin() {
        System.out.println("testTwin: original");
        char[][] tiles1 = {
                { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 10, 11, 12 }, { 13, 14, 15, 0 }
        };
        Board b11 = new Board(tiles1);
        System.out.println(b11);

        int hdExp = 0;
        int hdAct = b11.hamming();
        assert hdAct == hdExp :
                "cand board should have hamming dist of " + hdExp + ", got: " + hdAct;

        Board b21 = b11.twin();
        // FIXME
        System.out.println("twin board: \n" + b21);
        hdExp = 2; // because we swapped 2 elements
        hdAct = b21.hamming();
        assert hdAct == hdExp :
                "cand board should have hamming dist of " + hdExp + ", got: " + hdAct;
        System.out.println("testTwin: ok");
    }
}

