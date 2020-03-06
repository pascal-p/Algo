import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class KdTree {
    private int sz;

    // construct an empty set of points
    public KdTree() {
        // TODO
        this.sz = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        // TODO
        return true;
    }

    // number of points in the set
    public int size() { // number of points in the set
        return this.sz;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        // TODO
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        // TODO
        return false;
    }

    // draw all points to standard draw
    public void draw() {
        // TODO
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        // TODO
        return null;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        // TODO
        return null;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // TODO
    }
}
