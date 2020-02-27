/******************************************************************************
 *
 *  Name: Pascal P.
 *  Date: Feb 2020
 *  Description: Assign 3 - Princeton Algorithms
 *
 ******************************************************************************/

import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {
    private final Point[] points;
    private int numSeg;

    public BruteCollinearPoints(Point[] points) { // finds all line segments containing 4 points
        if (points == null) throw new IllegalArgumentException("Null points array given");

        this.points = new Point[points.length]; // defensive progr. make a copy

        for (int ix = 0; ix < points.length; ix++) {
            if (points[ix] == null) throw new IllegalArgumentException("Null point detected");
            if (ix > 0 && find(points[ix], ix)) throw new IllegalArgumentException("Null point detected");

            this.points[ix] = points[ix];
            Arrays.sort(this.points, 0, ix + 1);
        }
        this.numSeg = 0;
    }

    public int numberOfSegments() {
        // the number of line segments
        return this.numSeg;
    }

    public LineSegment[] segments() {
        // the line segments
        if (this.points.length < 4) return new LineSegment[]{};

        int lim = 5 * this.points.length + 1;
        // LineSegment[] lseg = new LineSegment[lim];
        ArrayList<LineSegment> lseg = new ArrayList<LineSegment>();

        outerloop:
        for (Point p0 : points) {
            for (Point p1 : points) {
                if (p1.compareTo(p0) <= 0) continue;

                for (Point p2 : points) {
                    if (p2.compareTo(p1) <= 0) continue;

                    for (Point p3 : points) {
                        if (p3.compareTo(p2) <= 0) continue;

                        double slopeP0P1 = p0.slopeTo(p1);
                        double slopeP0P2 = p0.slopeTo(p2);
                        int cmp = Double.compare(slopeP0P1, slopeP0P2);
                        if (cmp != 0) continue;

                        double slopeP0P3 = p0.slopeTo(p3);
                        cmp = Double.compare(slopeP0P1, slopeP0P3);
                        if (cmp != 0) continue;

                        // OK all equals - add them to whole list (if not already done)
                        LineSegment segment = new LineSegment(p0, p3);
                        // lseg[this.numSeg++] = segment;
                        lseg.add(segment);

                        // FIXME: temporary.
                        if (this.numSeg >= lim) {
                            // System.out.println("==> Break: limit reached");
                            break outerloop;
                        }
                    }

                }

            }

        }

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

}
