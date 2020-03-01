import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class FastCollinearPoints {
    private final Point[] points;
    private LineSegment[] lSeg;
    private int numSeg;

    /* ********************************************************************************************************** */
    // Cannot modify Point nor LineSegment, thus create a triple <Point_orig, Point_dest, slope>
    private static class Triple {
        private final Point orig, dest;
        private final double slope;

        public Triple() {
            this.orig = null;
            this.dest = null;
            this.slope = 0.0;
        }

        public Triple(Point orig, Point dest, double slope) {
            this.orig = orig;
            this.dest = dest;
            this.slope = slope;
        }

        public boolean cmp(Triple that) {
            return this.orig.compareTo(that.orig) < 0 &&
                    this.dest.compareTo(that.dest) == 0 &&
                    Double.compare(this.slope, that.slope) == 0;
        }

        public String toString() {
            return "<" + this.orig + ", " + this.dest + ", " + this.slope + ">";
        }

        public Comparator<Triple> destSlopeOrder() {
            return new DestSlopeOrder();
        }

        static class DestSlopeOrder implements Comparator<Triple> {
            public int compare(Triple t1, Triple t2) {
                // int cmpO = t1.orig.compareTo(t2.orig);
                int cmpD = t1.dest.compareTo(t2.dest);
                int cmpS = Double.compare(t1.slope, t2.slope);

                if (cmpD < 0) return -1;
                if (cmpD > 0) return 1;

                return cmpS;
            }
        }
    }
    /* ********************************************************************************************************** */

    public FastCollinearPoints(Point[] points) { // finds all line segments containing 4 points
        if (points == null) throw new IllegalArgumentException("Null points array given");

        this.points = new Point[points.length]; // defensive progr. make a copy
        for (int ix = 0; ix < points.length; ix++) {
            if (points[ix] == null) throw new IllegalArgumentException("Null point detected");
            if (ix > 0 && find(points[ix], ix)) throw new IllegalArgumentException("Null point detected");

            this.points[ix] = points[ix];
            Arrays.sort(this.points, 0, ix + 1);
        }
        this.numSeg = 0;
        calcSegments();
    }

    public int numberOfSegments() {
        return this.numSeg;
    }

    public LineSegment[] segments() {
        return Arrays.copyOf(this.lSeg, this.numSeg);
    }

    // called from constructor and not from segment() method
    private void calcSegments() {
        if (this.points.length < 4) this.lSeg = new LineSegment[]{};

        final ArrayList<Triple> lpt = new ArrayList<Triple>();
        int sz = this.points.length;
        for (Point p0 : points) {
            sz--;
            Point[] pary = creatSortedCpyAry(p0, sz); // sort per order of slope

            int len = 1; // len. in term of points
            for (int ix = 1; ix < sz; ix++) {
                if (Double.compare(p0.slopeTo(pary[ix - 1]), p0.slopeTo(pary[ix])) == 0) {
                    len++;
                    if (ix < sz) continue;
                }
                if (len >= 3) { // found colinear segment
                    lpt.add(new Triple(p0, pary[ix - 1], p0.slopeTo(pary[ix - 1])));
                    this.numSeg++;
                }
                len = 1; // reset
            }
            if (len >= 3) { // found last colinear segment
                lpt.add(new Triple(p0, pary[sz - 1], p0.slopeTo(pary[sz - 1])));
                this.numSeg++;
            }

            if (sz < 4) break; // no point checking further, not enough points
        }
        Collections.sort(lpt, new Triple().destSlopeOrder());
        ArrayList<Integer> ixes = new ArrayList<Integer>();
        int lim = this.numSeg, dup = 0;

        for (int ix = 1; ix < lim; ix++) {
            if (lpt.get(ix - 1).cmp(lpt.get(ix))) {
                dup++;
                if (ix < lim) continue;
            }
            ixes.add(ix - 1 - dup);
            dup = 0;
        }
        if (lim - dup > 0) ixes.add(lim - dup - 1);
        this.numSeg = ixes.size();
        this.lSeg = new LineSegment[this.numSeg];
        int jx = 0;
        for (int ix : ixes) {
            Triple t = lpt.get(ix);
            this.lSeg[jx++] = new LineSegment(t.orig, t.dest);
        }
    }

    private boolean find(Point px, int ix) {
        for (int jx = 0; jx < ix; jx++) {
            if (this.points[jx].compareTo(px) == 0) return true;
        }
        return false;
    }

    /*
     * Create a sorted array containing all points > given Point p
     * Sort this array according to slope with this point: p
     */
    private Point[] creatSortedCpyAry(Point p, int sz) {
        Point[] ary = aryCpy(p, sz);
        Arrays.sort(ary, 0, sz, p.slopeOrder()); // sort by slope
        return ary;
    }

    private Point[] aryCpy(Point p, int sz) {
        Point[] ary = new Point[sz];

        int jx = 0;
        for (int ix = 0; ix < this.points.length; ix++) {
            if (this.points[ix].compareTo(p) <= 0) continue;
            ary[jx++] = this.points[ix];
        }
        return ary;
    }

}

