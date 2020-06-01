/* *****************************************************************************
 *  Name: Pascal
 *  Date: May 2020
 *  Description:SeamCarver API
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

public class SeamCarver {
    private static final double BORDER_E = 1000.0;

    // mutable - state
    private int width, height;
    private double[][] energy2D; // order rows, cols
    private int[][] color2D;     // ditto
    private boolean transpose;


    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("Argument picture cannot be null");

        // this.picture = new Picture(picture);
        this.height = picture.height();
        this.width = picture.width();
        genColor(picture);
        genEnergy(picture);
        this.transpose = false;
    }

    // current picture
    public Picture picture() {
        Picture pict;

        if (transpose) transpose();
        pict = new Picture(this.width, this.height);

        for (int r = 0; r < this.height; r++) {
            for (int c = 0; c < this.width; c++) {
                // Sets the color of pixel (col, row) to given color.
                pict.setRGB(c, r, this.color2D[r][c]);
            }
        }
        return pict;
    }

    // width of current picture
    public int width() {
        return this.width;
    }

    // height of current picture
    public int height() {
        return this.height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        checkX(x);
        checkY(y);
        return this.energy2D[y][x];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!transpose) transpose();
        SeamFinder hsf = new SeamFinder(this.height, this.width);
        if (transpose) transpose(); // transpose back
        return hsf.getSP();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (transpose) transpose();
        SeamFinder vsf = new SeamFinder(this.height, this.width);
        // transpose back ?
        return vsf.getSP();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (!transpose) transpose();
        removeSeamAndReCalcEnergy(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (transpose) transpose();
        removeSeamAndReCalcEnergy(seam);
    }

    //
    // Private helpers
    //

    private void removeSeamAndReCalcEnergy(int[] seam) {
        removeSeam(seam);
        this.width--;
        reCalcEnergy(seam);
    }

    private void removeSeam(int[] seam) {
        checkSeam(seam);
        int r = 0; // row index
        for (int c : seam) {
            // remove cell at (c, jx) by overwriting it with cell value at (c+1, jx), if it exists
            if (c + 1 < this.width) {
                int len = this.width - c - 1;
                System.arraycopy(this.color2D[r], c + 1, color2D[r], c, len);
                System.arraycopy(this.energy2D[r], c + 1, energy2D[r], c, len);
            }
            r++;
        }
    }

    private void genColor(Picture picture) {
        this.color2D = new int[picture.height()][picture.width()];
        for (int r = 0; r < this.height(); r++)
            for (int c = 0; c < this.width(); c++)
                this.color2D[r][c] = picture.getRGB(c, r);
    }

    private void genEnergy(Picture picture) {
        this.energy2D = new double[picture.height()][picture.width()];
        for (int r = 0; r < this.height(); r++)
            for (int c = 0; c < this.width(); c++)
                this.energy2D[r][c] = calcEnergy(c, r);
    }

    private double calcEnergy(int c, int r) {
        if (c == 0 || c >= this.width - 1) return BORDER_E;
        if (r == 0 || r >= this.height - 1) return BORDER_E;

        double deltaX2 = calcDelta(this.color2D[r][c - 1], this.color2D[r][c + 1]);
        double deltaY2 = calcDelta(this.color2D[r - 1][c], this.color2D[r + 1][c]);
        return Math.sqrt(deltaX2 + deltaY2);
    }

    private void reCalcEnergy(int[] seam) {
        int r = 0;
        for (int c : seam) {
            if (c > 0)
                this.energy2D[r][c - 1] = calcEnergy(c - 1, r);
            this.energy2D[r][c] = calcEnergy(c, r);
            r++;
        }
    }

    private void transpose() {
        this.energy2D = transposeEnergy();
        this.color2D = transposeColor();
        // toggle transpose marker and swap dims
        this.transpose = !this.transpose;
        int tmp = this.width;
        this.width = this.height;
        this.height = tmp;
    }

    private double[][] transposeEnergy() {
        double[][] transEnergy2D;
        if (!transpose) { // not yet transposed
            transEnergy2D = new double[this.width][this.height];

            for (int r = 0; r < this.height; r++)
                for (int c = 0; c < this.width; c++)
                    transEnergy2D[c][r] = this.energy2D[r][c];
        }
        else { // transpose back
            int w = this.height, h = this.width;
            transEnergy2D = new double[h][w];
            for (int r = 0; r < h; r++)
                for (int c = 0; c < w; c++) {
                    transEnergy2D[r][c] = this.energy2D[c][r];
                }
        }
        return transEnergy2D;
    }

    private int[][] transposeColor() {
        int[][] transColor2D;
        if (!transpose) { // not yet transposed
            transColor2D = new int[this.width][this.height];

            for (int r = 0; r < this.height; r++)
                for (int c = 0; c < this.width; c++)
                    transColor2D[c][r] = this.color2D[r][c];
        }
        else { // transpose back
            int w = this.height, h = this.width;
            transColor2D = new int[h][w];

            for (int r = 0; r < h; r++)
                for (int c = 0; c < w; c++)
                    transColor2D[r][c] = this.color2D[c][r];
        }
        return transColor2D;
    }

    private void checkSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException("Seam cannot be null");

        if (this.width <= 1)
            throw new IllegalArgumentException("Picture width is less or equal to 1!");

        if (seam.length != this.height)
            throw new IllegalArgumentException("Unexpected seam length");

        for (int ix = 1; ix < seam.length; ix++) {
            int py = seam[ix - 1];
            int cy = seam[ix];
            checkX(py);
            if (Math.abs(cy - py) > 1)
                throw new IllegalArgumentException(
                        "[width] Two adjacent entries differ by more than 1 - cy: " + cy
                                + " / py: " + py);
        }
        checkX(seam[seam.length - 1]);
        return; // All goood
    }

    private void checkX(int x) {
        if (x < 0 || x >= this.width)
            throw new IllegalArgumentException("0 <= x < " + this.width() + " got x: " + x);
        // else return;
    }

    private void checkY(int y) {
        if (y < 0 || y >= this.height)
            throw new IllegalArgumentException("0 <= y < " + this.height() + " / got y: " + y);
        // else return;
    }

    private double calcDelta(int rgbP, int rgbN) {
        int dR = ((rgbP >> 16) & 0xFF) - ((rgbN >> 16) & 0xFF); // square diff red comp
        int dG = ((rgbP >> 8) & 0xFF) - ((rgbN >> 8) & 0xFF);   // square diff green comp
        int dB = ((rgbP >> 0) & 0xFF) - ((rgbN >> 0) & 0xFF);   // square diff blue comp

        return (double) dR * dR + dG * dG + dB * dB;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        // TODO
    }

    private class Pair {
        public final int x;
        public final int y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private class SeamFinder {
        private final int nrows, ncols;
        private final int[] sp;

        public SeamFinder(int rows, int cols) {
            this.nrows = rows;
            this.ncols = cols;
            int n = this.nrows * this.ncols;
            int[] edgeTo = new int[n];
            double[] distTo = new double[n];
            this.sp = new int[this.nrows];
            acyclicSP(n, edgeTo, distTo);
            findSP(n, edgeTo, distTo);
        }

        private void acyclicSP(int n, int[] edgeTo, double[] distTo) {
            for (int v = this.ncols; v < n; v++)
                distTo[v] = Double.POSITIVE_INFINITY;

            for (int v = 0; v < this.ncols; v++) // init first row
                distTo[v] = 0.0;

            for (int u : topologicalOrder(n)) {
                Pair pair = cell2Coord(u);
                int col = pair.x, row = pair.y;
                int[] adjacency = adj(col, row);

                for (int v : adjacency) {
                    // relax(v)
                    Pair p = cell2Coord(v);
                    int c = p.x, r = p.y;
                    double w = energy2D[r][c];
                    if (distTo[v] > distTo[u] + w) {
                        distTo[v] = distTo[u] + w;
                        edgeTo[v] = u;
                    }
                }
            }
        }

        private void findSP(int n, int[] edgeTo, double[] distTo) {
            Stack<Integer> stack = new Stack<Integer>();
            // find minimal value from last row
            int iMin = -1;
            double minVal = Double.POSITIVE_INFINITY;
            for (int ix = (this.nrows - 1) * this.ncols; ix < n; ix++) {
                if (distTo[ix] < minVal) {
                    minVal = distTo[ix];
                    iMin = ix;
                }
            }
            stack.push(iMin);
            int jx = iMin;
            while (distTo[jx] != 0 || jx >= this.ncols) {
                jx = edgeTo[jx];
                stack.push(jx);
            }
            jx = 0; // row index
            for (int ix : stack) {
                Pair p = cell2Coord(ix);
                sp[jx++] = p.x;
            }
            // Assert
        }

        public int[] getSP() {
            int[] csp = new int[this.nrows];

            // Copy elements of sp[] to csp[]
            System.arraycopy(sp, 0, csp, 0, this.nrows);
            return csp;
        }

        private Iterable<Integer> topologicalOrder(int n) {
            int[] state = new int[n];
            for (int ix = 0; ix < n; ix++) state[ix] = 0;
            Stack<Integer> stack = new Stack<Integer>();
            for (int ix = 0; ix < n; ix++)
                dfs(ix, stack, state);
            return stack;
        }

        private void dfs(int ix, Stack<Integer> stack, int[] state) {
            if (state[ix] == 1)
                throw new IllegalArgumentException("Cycle detected");
            else if (state[ix] == 2)
                return;
            else {
                state[ix] = 1;
                Pair pair = cell2Coord(ix);
                int col = pair.x, row = pair.y;
                int[] adjacency = adj(col, row);
                for (int u : adjacency) dfs(u, stack, state);
                state[ix] = 2;
                stack.push(ix);
            }
        }

        private int[] adj(int col, int row) {
            int nc = 0;
            for (int c = col - 1; c <= col + 1; c++)
                if (c >= 0 && c < this.ncols) nc++;

            if (row < nrows - 1) { // starting at 0
                int[] adjacency = new int[nc];
                int ix = 0;
                for (int c = col - 1; c <= col + 1; c++)
                    if (c >= 0 && c < this.ncols)
                        adjacency[ix++] = coord2Cell(c, row + 1);
                return adjacency;
            }
            else {
                return new int[0]; // empty, because no more rows...
            }
        }

        private Pair cell2Coord(int ix) {
            int row = ix / this.ncols;
            assert 0 <= row && row < this.nrows;

            int col = ix % this.ncols;
            assert 0 <= col && col < this.ncols;
            //               x    y
            return new Pair(col, row);
        }

        private int coord2Cell(int col, int row) {
            assert col >= 0 && col < this.ncols : "0 <= col < ncols / " + col;
            assert row >= 0 && row < this.nrows : "0 <= row < nrows / " + row;

            int ix = this.ncols * row + col;
            assert 0 <= ix && ix < this.nrows * this.ncols :
                    "0 <= ix && ix < this.nrows * this.ncols / " + ix;
            return ix;
        }

    }

}
