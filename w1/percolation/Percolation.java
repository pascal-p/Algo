/* *****************************************************************************
 *  Name: Pascal P.
 *  Date: Feb 2020
 *  Description: Assign 1 - Princeton Algorithms
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[][] site;          // to tell whether site is open or block
    private final int sz;                    // number of site
    private int numOpenSites = 0;

    /*
     * Addressing backwash problem
     */
    private final WeightedQuickUnionUF ufTb;
    private final WeightedQuickUnionUF ufT;


    // creates n-by-n site, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("size must be >= 1");

        this.site = new boolean[n][n];
        this.sz = n;

        for (int ix = 0; ix < n; ix++) {
            for (int jx = 0; jx < n; jx++) {
                this.site[ix][jx] = false;
            }
        }

        int n2 = n * n;
        ufTb = new WeightedQuickUnionUF(n2 + 2); // add top and bottom virtual site
        ufT = new WeightedQuickUnionUF(n2 + 1); // add top virtual site (only)

        // connect top virtual site to top row (index 0), bottom virtual site (index n*n + 1) to bottom row
        n2 += 1;
        for (int ix = 1; ix <= n; ix++) {
            ufTb.union(0, ix);           // connect to virtaul top site
            ufT.union(0, ix);            // ditto
            ufTb.union(n2, n2 - ix);   // connect to bottom virtual site
        }
    }

    /*
     * Each site is either opened or blocked.
     * A full site is an open site that can be connected to an open site in the top row via a chain of neighboring (left, right, up, down) open sites.
     */

    // test client (optional)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);   // by default: 16
        Percolation perco = new Percolation(n);

        assert perco.site.length == n;
        assert !perco.isFull(1, 1);
        assert perco.numberOfOpenSites() == 0;

        int row = 1, col = 1;
        perco.open(row, col);
        assert perco.isOpen(row, col);
        assert perco.numberOfOpenSites() == 1;

        int n2 = n * n + 1;
        for (int ix = 1; ix <= n; ix++) {
            assert perco.ufTb.connected(0, ix);
            assert perco.ufT.connected(0, ix);

            assert perco.ufTb.connected(n2, n2 - ix);
        }

        assert !perco.ufTb.connected(0, n + 1);
        assert !perco.percolates() : "initial system cannot percolate - but here it does!";

    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        checkCoord(row, col, "open");

        if (!this.isOpen(row, col)) {
            this.site[row - 1][col - 1] = true;
            this.numOpenSites++;

            // check neighbors
            int ix = coordPos(row, col);
            if (row < this.sz && this.isOpen(row + 1, col))
                this.updateUf(coordPos(row + 1, col), ix);
            if (row > 1 && this.isOpen(row - 1, col))
                this.updateUf(coordPos(row - 1, col), ix);
            if (col < this.sz && this.isOpen(row, col + 1))
                this.updateUf(coordPos(row, col + 1), ix);
            if (col > 1 && this.isOpen(row, col - 1))
                this.updateUf(coordPos(row, col - 1), ix);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        checkCoord(row, col, "isOpen");
        return this.site[row - 1][col - 1];
    }

    // is the site (row, col) full? which means it is connected to the top
    public boolean isFull(int row, int col) {
        checkCoord(row, col, "isFull");
        return isOpen(row, col) && ufT.connected(coordPos(row, col), 0);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return this.numOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        if (this.sz == 1) return this.isOpen(1, 1);
        return this.ufTb.connected(0, this.sz * this.sz + 1);
    }

    private void checkCoord(int row, int col, String from) {
        if (row < 1 || row > this.sz)
            throw new IllegalArgumentException(
                    " 1 <=  row <= " + Integer.toString(this.sz) + " from: " + from
                            + " got row ix: " + Integer.toString(row));
        if (col < 1 || col > this.sz)
            throw new IllegalArgumentException(
                    " 1 <=  col <= " + Integer.toString(this.sz) + " from: " + from
                            + " got col ix: " + Integer.toString(col));
    }

    private int coordPos(int row, int col) {
        checkCoord(row, col, "coord_pos"); // row >=1
        return (row - 1) * this.sz + col;
    }

    private void updateUf(int src, int dst) {
        this.ufTb.union(src, dst);
        this.ufT.union(src, dst);
    }

}
