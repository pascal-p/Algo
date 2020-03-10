import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {
    private final SET<Point2D> set;
    private int sz;

    public PointSET() {  // construct an empty set of points
        this.set = new SET<>();
        this.sz = 0;
    }

    public boolean isEmpty() { // is the set empty?
        return this.sz == 0;
    }

    public int size() { // number of points in the set
        return this.sz;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Null Point2D instance given");

        if (this.set.contains(p)) return;
        this.set.add(p);
        this.sz++;
    }

    public boolean contains(Point2D p) { // does the set contain point p?
        if (p == null) throw new IllegalArgumentException("Null Point2D instance given");
        return this.set.contains(p);
    }

    public void draw() { // draw all points to standard draw
        //
        // NOTE: How should I set the size and color of the points and rectangles when drawing?
        //       Use StdDraw.setPenColor(StdDraw.BLACK) and StdDraw.setPenRadius(0.01) before
        //       drawing the points;
        //       use StdDraw.setPenColor(StdDraw.RED) or StdDraw.setPenColor(StdDraw.BLUE) and
        //       StdDraw.setPenRadius() before drawing the splitting lines.
        //
        for (Point2D p : this.set) p.draw();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Null Rectangle instance given");

        SET<Point2D> iset = new SET<>();
        for (Point2D p : this.set) {
            if (rect.contains(p)) { // also true if on the boundary...
                iset.add(p);
            }
        }
        // NOTE: What should range() return if there are no points in the range?
        //       It should return an Iterable<Point2D> object with zero points.
        return iset;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Null Point2D instance given");

        if (this.set == null || this.sz == 0)
            return null;

        double minDist = Double.POSITIVE_INFINITY;
        Point2D pt = null;

        for (Point2D op : this.set) {
            double dop = p.distanceSquaredTo(op);
            int cmp = Double.compare(dop, minDist);
            if (cmp < 0) {
                pt = op;
                minDist = dop;
            }
        }
        if (pt == null) return null;
        return new Point2D(pt.x(), pt.y());
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        System.out.println("Reading cmd line args ...");
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();

        System.out.println("Reading reading the points ...");
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }
        System.out.println("Read " + brute.sz + " points.");

        // process nearest neighbor queries
        StdDraw.enableDoubleBuffering();

        while (true) {
            // the location (x, y) of the mouse
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            Point2D query = new Point2D(x, y);

            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.GREEN);
            StdDraw.setPenRadius(0.02);
            query.draw();

            // draw all of the points
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            brute.draw();

            // draw in red the nearest neighbor (using brute-force algorithm)
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.RED);
            brute.nearest(query).draw();
            StdDraw.setPenRadius(0.02);

            StdDraw.show();
            // break;
        }

    }
}
