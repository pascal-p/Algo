import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {
    private final Point[] points;
    private int numSeg;

    public FastCollinearPoints(Point[] points) { // finds all line segments containing 4 points
        if (points == null) throw new IllegalArgumentException("Null points array given");

        this.points = new Point[points.length]; // defensive progr. make a copy
        for (int ix = 0; ix < points.length; ix++) {
            if (points[ix] == null) throw new IllegalArgumentException("Null point detected");
            if (ix > 0 && find(points[ix], ix)) throw new IllegalArgumentException("Null point detected");

            this.points[ix] = points[ix];
            Arrays.sort(this.points, 0, ix + 1);
        }
        // Arrays.sort(this.points, 0, points.length);
        this.numSeg = 0;
    }

    public int numberOfSegments() {
        // the number of line segments
        return this.numSeg;
    }

    public LineSegment[] segments() {
        // the line segments
        if (this.points.length < 4) return new LineSegment[]{};

        // printAry(this.points);
        // int lim = 5 * this.points.length + 1;
        // LineSegment[] lseg = new LineSegment[lim];
        ArrayList<LineSegment> lseg = new ArrayList<LineSegment>();

        int sz = this.points.length;
        // outerloop:
        for (Point p0 : points) {
            sz--;
            // System.out.println("Considering " + Integer.toString(sz) + " points - excluding <= p0: " + p0.toString());
            Point[] pary = creatSortedCpyAry(p0, sz);
            // printAry(pary);

            int len = 1; // len. monotony
            for (int ix = 1; ix < sz; ix++) {
                //
                // System.out.println("Slope: p0/p1: " + p0.slopeTo(pary[ix - 1]) + " / p0/p2: " + p0.slopeTo(pary[ix]) + " / len: " + len + " / ix: " + ix);
                if (Double.compare(p0.slopeTo(pary[ix - 1]), p0.slopeTo(pary[ix])) == 0) { // compare in term of slope
                    len++;
                    continue;
                }

                if (len >= 3) {
                    // found colinear segment
                    // System.out.println("\tFound colinear segment");
                    LineSegment segment = new LineSegment(p0, pary[ix - 1]); // Line
                    // Segment segment = new LineSegment(pary[ix - len], pary[ix - 1]);
                    // lseg[this.numSeg++] = segment;
                    lseg.add(segment);
                }
                len = 1; // reset
            }

            // last one possibly
            if (len >= 3) {
                // System.out.println("\tFound last colinear segment");
                // found colinear segment
                LineSegment segment = new LineSegment(p0, pary[sz - 1]); // LineSegment(pary[sz - len], pary[sz - 1]);
                // lseg[this.numSeg++] = segment;
                lseg.add(segment);
            }

            if (sz < 4) break; // no point checking further, not enough points
        }
        // System.out.println("Done... ");
        // resize to actual number of segments
        LineSegment[] ary = new LineSegment[this.numSeg];
        ary = lseg.toArray(ary); // Arrays.copyOf(lseg, this.numSeg);
        return ary;
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
        // printAry(ary);
        Arrays.sort(ary, 0, sz, p.slopeOrder()); // sort by slope
        return ary;
    }

    private Point[] aryCpy(Point p, int sz) {
        Point[] ary = new Point[sz];

        for (int ix = 0, jx = 0; ix < this.points.length; ix++) {
            // System.out.print("\tix: " + Integer.toString(ix) + ", jx: " + Integer.toString(jx));

            if (this.points[ix].compareTo(p) <= 0) {
                //  System.out.println("\tSkipping ix: " + Integer.toString(ix));
                continue;
            }
            // System.out.println(" => Copy: at ix: " + Integer.toString(ix) + ", jx: " + Integer.toString(jx));
            ary[jx++] = this.points[ix];
        }
        return ary;
    }

    private void printAry(Point[] ary) {
        System.out.print("=> ");
        for (Point p0 : ary) {
            System.out.print(p0.toString() + "; ");
        }
        System.out.println("");
    }
}
