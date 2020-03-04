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
    private LineSegment[] lSeg;
    private int numSeg;

    public BruteCollinearPoints(final Point[] points) { // finds all line segments containing 4 points
        if (points == null) throw new IllegalArgumentException("Null points array given");

        int len = points.length;
        this.points = new Point[len]; // defensive progr. make a copy

        for (int ix = 0; ix < len; ix++) {
            if (points[ix] == null) throw new IllegalArgumentException("Null point detected");
            if (ix > 0 && find(points[ix], ix)) throw new IllegalArgumentException("Duplicate point detected");
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
        // the line segments
        if (this.points.length < 4)
            this.lSeg = new LineSegment[]{};

        final ArrayList<LineSegment> lseg = new ArrayList<LineSegment>();

        for (Point p0 : this.points) {
            for (Point p1 : this.points) {
                if (p1.compareTo(p0) <= 0) continue;

                for (Point p2 : this.points) {
                    if (p2.compareTo(p1) <= 0) continue;

                    for (Point p3 : this.points) {
                        if (p3.compareTo(p2) <= 0) continue;

                        double slopeP0P1 = p0.slopeTo(p1), slopeP0P2 = p0.slopeTo(p2);
                        int cmp = Double.compare(slopeP0P1, slopeP0P2);
                        if (cmp != 0) continue;

                        double slopeP0P3 = p0.slopeTo(p3);
                        cmp = Double.compare(slopeP0P1, slopeP0P3);
                        if (cmp != 0) continue;

                        // OK all equals - add them to whole list (if not already done)
                        LineSegment segment = new LineSegment(p0, p3);
                        lseg.add(segment);
                        this.numSeg++;
                    }
                }
            }
        }
        final LineSegment[] ary = new LineSegment[this.numSeg];
        this.lSeg = lseg.toArray(ary);
    }

    private boolean find(Point px, int ix) {
        for (int jx = 0; jx < ix; jx++) {
            if (this.points[jx].compareTo(px) == 0) return true;
        }
        return false;
    }

}
