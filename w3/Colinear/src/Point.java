/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 *  Name: Pascal P.
 *  Date: Feb 2020
 *  Description: Assign 3 - Princeton Algorithms
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.StdDraw;

import java.util.Comparator;

public class Point implements Comparable<Point> {
    private static final double ZERO = +0.0;
    private static final double M_INF = Double.NEGATIVE_INFINITY;
    private static final double P_INF = Double.POSITIVE_INFINITY;

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param x the <em>x</em>-coordinate of the point
     * @param y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        /* YOUR CODE HERE */
        if (this.x == that.x && this.y == that.y) return M_INF; // both equals -> Double.NEGATIVE_INFINITY;
        if (this.y == that.y) return ZERO; // horizontal slope +0.0
        if (this.x == that.x) return P_INF; // vertical slope => Double.POSITIVE_INFINITY;

        double den = that.x - this.x;
        double num = that.y - this.y;
        return num / den;
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     * point (x0 = x1 and y0 = y1);
     * a negative integer if this point is less than the argument
     * point; and a positive integer if this point is greater than the
     * argument point
     */
    public int compareTo(Point that) {
        /* YOUR CODE HERE */
        int cmpy = Integer.compare(this.y, that.y);
        if (cmpy < 0) return -1;

        int cmpx = Integer.compare(this.x, that.x);
        if (cmpy == 0 && cmpx < 0) return -1;
        if (cmpy == 0 && cmpx == 0) return 0;

        return 1;
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        /* YOUR CODE HERE */
        return new SlopeOrder();
    }

    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    private class SlopeOrder implements Comparator<Point> {
        public int compare(Point p1, Point p2) {
            double slope1 = p1.slopeTo(Point.this);
            double slope2 = p2.slopeTo(Point.this);
            int cmp = Double.compare(slope1, slope2);

            if (cmp < 0) return -1;
            if (cmp > 0) return 1;
            return 0;
        }
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        /* YOUR CODE HERE */
        int x0 = 2, y0 = 1;
        int x1 = 5, y1 = y0;
        int x2 = x0, y2 = 5;
        int x3 = 4, y3 = 2;
        int x4 = 3, y4 = -1;

        Point p0 = new Point(x0, y0);
        Point p1 = new Point(x1, y1);
        Point p2 = new Point(x2, y2);
        Point p3 = new Point(x3, y3);
        Point p4 = new Point(x4, y4);

        // p0 && p1 relationships
        assert p0.compareTo(p1) < 0 : "p0: " + p0.toString() + " should be < than p1: " + p1.toString();
        assert p1.slopeTo(p0) == Point.ZERO : "slope (p0: " + p0.toString() + ", p1: " + p1.toString() + ") is horizontal";
        assert p0.slopeTo(p0) == Point.M_INF : "slope (p0: " + p0.toString() + ", p0: " + p1.toString() + ") is -inf";


        // p0 && p2 relationships
        assert p0.compareTo(p2) < 0 : "p0: " + p0.toString() + " should be < than p2: " + p2.toString();
        double slopeP2P0 = p2.slopeTo(p0);
        assert slopeP2P0 == Point.P_INF : "slope (p0: " + p0.toString() + ", p2: " + p2.toString() + ") is vertical (+inf)";

        // p0 && p3 relationships
        assert p0.compareTo(p3) < 0 : "p0: " + p0.toString() + " should be < than p3: " + p3.toString();
        double slopeP3P0 = p3.slopeTo(p0);
        assert slopeP3P0 > Point.ZERO && slopeP3P0 < Point.P_INF : "slope (p0: " + p0.toString() + ", p3: " + p3.toString() + ") is definite >0 - Got: "
                + Double.toString(slopeP3P0);

        // p0 && p4 relationships
        assert p0.compareTo(p4) > 0 : "p0: " + p0.toString() + " should be > than p4: " + p4.toString();
        double slopeP4P0 = p4.slopeTo(p0);
        assert slopeP4P0 < Point.ZERO && slopeP4P0 > Point.M_INF : "slope (p0: " + p0.toString() + ", p4: " + p4.toString() + ") is definite <0 - Got: "
                + Double.toString(slopeP4P0);

        // Slope comparison
        assert p0.slopeOrder().compare(p3, p4) > 0 : "slope p3/p0: " + Double.toString(slopeP3P0) + " > slope p4/p0: " + Double.toString(slopeP4P0);
        assert p0.slopeOrder().compare(p2, p4) > 0 : "slope p2/p0: " + Double.toString(slopeP2P0) + " > slope p4/p0: " + Double.toString(slopeP4P0);
        assert p0.slopeOrder().compare(p4, p2) < 0 : "slope p4/p0: " + Double.toString(slopeP4P0) + " < slope p2/p0: " + Double.toString(slopeP2P0);
    }
}
