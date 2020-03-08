import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private static final char VERT = 'V';
    private static final char HOR = 'H';
    private int sz;
    private Node root;

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
            this.lb = null;
            this.rt = null;
        }

        public Node(Point2D p, RectHV rect, Node lb, Node rt) {
            this.p = p;
            this.rect = rect;
            this.lb = lb;
            this.rt = rt;
        }
    }

    // construct an empty set of points
    public KdTree() {
        this.root = null;
        this.sz = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return (this.sz == 0); // or (this.root == null)
    }

    // number of points in the set
    public int size() { // number of points in the set
        return this.sz;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Null Point2D instance given");

        this.root = insert(this.root, null, p, VERT, 0);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Null Point2D instance given");

        if (this.isEmpty()) return false;
        return contains(this.root, p, VERT);
    }

    // draw all points to standard draw
    public void draw() {
        // ref. https://www.coursera.org/learn/algorithms-part1/discussions/weeks/5/threads/hOO_NKV6EeaJsxK88fW92A
        draw(this.root, VERT);
    }

    // public void inOrder() {
    //     inOrder(this.root);
    // }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Null RectHV instance given");

        // To find all points contained in a given query rectangle, start at the root and recursively
        // search for points in both subtrees using the following pruning rule:
        // - if the query rectangle does not intersect the rectangle corresponding to a node,
        // there is no need to explore that node (or its subtrees).
        // A subtree is searched only if it might contain a point contained in the query rectangle.

        if (this.isEmpty()) return null;
        SET<Point2D> iset = new SET<>();
        return inOrder(iset, this.root, rect);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Null Point2D instance given");
        // TODO
        return null;
    }

    /*
     * Implementation
     */
    private Node insert(Node x, Node px, Point2D p, char orient, int pcmp) {
        // px is the parent of Node x - exp.
        // check for duplicate
        if (pcmp == 0 && px != null) { // parent defined - parent orientation?
            char porient = (orient == VERT) ? HOR : VERT;

            if (porient == VERT && Double.compare(p.y(), px.p.y()) == 0) return x;
            else if (porient == HOR && Double.compare(p.x(), px.p.x()) == 0) return x;
        }
        if (x == null) {
            this.sz++;
            return new Node(p,
                            setRectHV(px, p, orient == HOR ? VERT : HOR, pcmp));
        }
        int cmp = (orient == VERT) ?
                  Double.compare(p.x(), x.p.x()) : Double.compare(p.y(), x.p.y());
        char nextOrient = (orient == VERT) ? HOR : VERT;

        if (cmp < 0)
            x.lb = insert(x.lb, x, p, nextOrient, -1);
        else if (cmp > 0)
            x.rt = insert(x.rt, x, p, nextOrient, 1);
        else // cmp == 0
            x.rt = insert(x.rt, x, p, nextOrient, 0);
        return x;
    }

    private RectHV setRectHV(Node px, Point2D p, char porient, int pcmp) {
        //
        // As nodes are added to the tree, each node's rectangle is either:
        // - The left portion of its parent's rectangle, if going to left subtree   (orient: HOR)
        // - The right portion of its parent's rectangle, if going to right subtree (orient: HOR)
        // - The top portion of its parent's rectangle, if
        // - The bottom portion of its parent's rectangle.
        //
        // A new node's rectangle depends on the orientation of the parent (left/right or up/down)
        // and the value of the child node's point?.
        //
        double ox = 0., oy = 0., rx = 1., ry = 1.;
        if (px != null) { // parent
            assert px.rect != null : "parent node must de defined, but got null";
            ox = px.rect.xmin();
            oy = px.rect.ymin();
            rx = px.rect.xmax();
            ry = px.rect.ymax();

            if (porient == VERT) { // parent orientation
                if (pcmp == -1) rx = px.p.x();
                else ox = px.p.x(); // pcmp == 1, cannot be 0
            }
            else { // porient was HOR
                if (pcmp == -1) ry = px.p.y();
                else oy = px.p.y(); // pcmp == 1, cannot be 0
            }
        }
        return new RectHV(ox, oy, rx, ry);
    }

    private boolean contains(Node x, Point2D p, char orient) {
        if (x == null) return false;

        int cmp = (orient == VERT) ?
                  Double.compare(p.x(), x.p.x()) : Double.compare(p.y(), x.p.y());
        // define complement
        int cmpC = (orient == VERT) ?
                   Double.compare(p.y(), x.p.y()) : Double.compare(p.x(), x.p.x());
        char nextOrient = (orient == VERT) ? HOR : VERT;

        if (cmp == 0 && cmpC == 0)
            return true;
        else if (cmp < 0)
            return contains(x.lb, p, nextOrient);
        else // if (cmp > 0)
            return contains(x.rt, p, nextOrient);
    }

    private SET<Point2D> inOrder(SET<Point2D> s, Node x, RectHV rect) {
        // To find all points contained in a given query rectangle, start at the root and recursively
        // search for points in both subtrees using the following pruning rule:
        // - if the query rectangle does not intersect the rectangle corresponding to a node,
        // there is no need to explore that node (or its subtrees).
        // A subtree is searched only if it might contain a point contained in the query rectangle.

        if (x == null) return s;
        else if (x.rect.intersects(rect)) {
            if (rect.contains(x.p)) s.add(x.p);
            SET<Point2D> ls = inOrder(s, x.lb, rect);
            SET<Point2D> rs = inOrder(s, x.rt, rect);
            return ls.union(rs);
        }
        else // nothing to do
            assert true;
        return s;
    }

    // private void inOrder(Node x) {
    //     if (x != null) {
    //         if (x.lb != null) inOrder(x.lb);
    //         if (x.rt != null) inOrder(x.rt);
    //     }
    // }

    private void draw(Node n, char orient) { // recursive pre-order Tree Traversal
        // Traverse the left node
        // Draw the current node
        // Draw the current partition line (checking whether it should be vertical or horizontal)
        // Traverse the right node

        // To draw a Node:
        // Draw the vertical line or horizontal line that this Node uses to divide the tree.
        // Draw the point that this node represents.
        // Recursively draw the left child and the right child.
        Point2D orig, dest;
        if (orient == VERT) {
            StdDraw.setPenColor(StdDraw.RED);
            orig = new Point2D(n.p.x(), n.rect.ymin());
            dest = new Point2D(n.p.x(), n.rect.ymax());
        }
        else {
            // HORIZ
            StdDraw.setPenColor(StdDraw.BLUE);

            orig = new Point2D(n.rect.xmin(), n.p.y());
            dest = new Point2D(n.rect.xmax(), n.p.y());
        }
        StdDraw.setPenRadius();
        orig.drawTo(dest);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        n.p.draw(); // draw point

        char newOrient = orient == VERT ? HOR : VERT;
        if (n.lb != null) draw(n.lb, newOrient);
        if (n.rt != null) draw(n.rt, newOrient);
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // System.out.println("Reading cmd line args ...");
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        // char o = '|';
        // System.out.println("Reading reading the points ...");
        // Point2D prev = null, p = null;
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            // if (p != null) {
            //     prev = p;
            //     assert kdtree.contains(prev) : "Point2D " + prev + " should be in the tree!";
            //     double nx = x / 2;
            //     double ny = y / 3;
            //     Point2D xp = new Point2D(nx, ny);
            //     assert !kdtree.contains(xp) : "Point2D " + xp + " should NOT be in the tree!";
            // }
            // p = new Point2D(x, y);
            // System.out.println("==> Creating new Point: " + p + " / orientation: " + o);
            // o = (o == '|') ? '-' : '|';
            kdtree.insert(p);
        }
        // System.out.println("Read " + kdtree.sz + " points.");
        // kdtree.inOrder();

        // process nearest neighbor queries
        StdDraw.enableDoubleBuffering();

        while (true) {
            // the location (x, y) of the mouse
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            Point2D query = new Point2D(x, y);

            // draw all of the points
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            kdtree.draw();

            // draw in blue the nearest neighbor (using kd-tree algorithm)
            // StdDraw.setPenColor(StdDraw.BLUE);
            // kdtree.nearest(query).draw();
            StdDraw.show();
            StdDraw.pause(40);
        }
    }
}
