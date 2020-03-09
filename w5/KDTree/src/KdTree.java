import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private static final char VERT = 'V';
    private static final char HOR = 'H';
    private int sz;
    private Node root;

    private static class Node {
        private final Point2D p;      // the point
        private final RectHV rect;    // the axis-aligned rectangle corresponding to this node
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
        if (this.isEmpty()) return null;

        Queue<Point2D> q = new Queue<>();
        // cf. inOrder for details on the impl.
        return inOrder(q, this.root, rect);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Null Point2D instance given");
        if (this.isEmpty()) return null;

        // System.out.println("Let's get started...");
        Point2D closest = nearest(this.root, p, this.root.p, VERT);
        // System.out.println("\nFinally closest is: " + closest);
        return closest;
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
                            getRectHV(px, orient == HOR ? VERT : HOR, pcmp));
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

    private RectHV getRectHV(Node px, char porient, int pcmp) {
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

    private Queue<Point2D> inOrder(Queue<Point2D> q, Node x, RectHV rect) {
        //
        // To find all points contained in a given query rectangle, start at the root and recursively
        // search for points in both subtrees using the following pruning rule:
        // - if the query rectangle does not intersect the rectangle corresponding to a node,
        // there is no need to explore that node (or its subtrees).
        // A subtree is searched only if it might contain a point contained in the query rectangle.
        //
        if (x == null) return q;
        else if (x.rect.intersects(rect)) {
            if (rect.contains(x.p)) q.enqueue(x.p);
            q = inOrder(q, x.lb, rect);
            return inOrder(q, x.rt, rect);
        }
        else // nothing to do
            assert true;
        return q;
    }

    private Point2D nearest(Node x, Point2D qp, Point2D ccand, char orient) {
        // To find a closest point to a given query point, start at the root and recursively search
        // in both subtrees using the following pruning rule:
        // - if the closest point discovered so far is closer than the distance between the query point
        // and the rectangle corresponding to a node, there is no need to explore that node (or its
        // subtrees).
        // That is, search a node only only if it might contain a point that is closer than the best
        // one found so far. The effectiveness of the pruning rule depends on quickly finding a
        // nearby point.
        // To do this, organize the recursive method so that when there are two possible subtrees to
        // go down, you always choose the subtree that is on the same side of the splitting line as
        // the query point as the first subtree to explore — the closest point
        // found while exploring the first subtree may enable pruning of the second subtree.

        if (x == null) return ccand;
        // System.out.println("\nCurrent node is " + x.p + " / orient. " + orient + " / Qry pt: " + qp
        //                            + " / nearest(so far): " + ccand);
        if (x.lb == null && x.rt == null) {
            // System.out.print("TERMINAL NODE ");
            int cmp = compare(x.p, ccand, qp); // x.p -- qp vs ccand -- qp
            if (cmp < 0) ccand = x.p; // d(x.p, qp) < d(ccand, qp), hence closest/nearest is x.p
            // System.out.println("FOUND closest: " + ccand);
            return ccand;
        }

        // update closest candidate here?
        int cmp = compare(x.p, ccand, qp);
        if (cmp < 0) ccand = x.p;

        if (orient == VERT) {
            Point2D[] seg = vertLineDiv(x);
            cmp = Double.compare(qp.x(), seg[0].x());
            if (cmp < 0) { // start with left subtree
                // System.out.println(" | go to Left subtree... next orient: " + HOR);
                Point2D lcp = nearest(x.lb, qp, ccand, HOR);
                // System.out.println(" | BACK FROM Left subtree of " + x.p);
                cmp = compare(lcp, ccand, qp); // [lcp, qp] vs [ccand, qp] - lcp or ccand?

                if (cmp <= 0) { // new closest point it is lcp => explore right subtree
                    // System.out.println(" => go to Right subtree... next orient: " + HOR);
                    Point2D rcp = nearest(x.rt, qp, lcp, HOR);
                    cmp = compare(rcp, lcp, qp); // [rcp, qp] vs [lcp, qp] - rcp or lcp?
                    return (cmp < 0) ? rcp : lcp;
                }
                else { // ccand was the nearest/closest point return it!
                    // System.out.println(" =L1> ccand(|) explored only LB: " + ccand
                    //                            + " was closest point - return it");
                    return ccand;
                }
            }
            else { // start with rigth subtree
                // System.out.println(" | go to Right subtree... next orient: " + HOR);
                Point2D rcp = nearest(x.rt, qp, ccand, HOR);
                cmp = compare(rcp, ccand, qp); // [rcp, qp] vs [ccand, qp] - rcp or ccand?

                if (cmp <= 0) { // found new closest point it is rcp => explore left subtree
                    // System.out.println(" => go to Left subtree... next orient: " + HOR);
                    Point2D lcp = nearest(x.lb, qp, rcp, HOR);
                    // System.out.println(" | BACK FROM Right subtree of " + x.p);
                    cmp = compare(lcp, rcp, qp); // [lcp, qp] vs [rcp, qp] - lcp or rcp?
                    return (cmp < 0) ? lcp : rcp;
                }
                else { // ccand was the nearest/closest point return it!
                    // System.out.println(" =R1> ccand(|) explored only RT: " + ccand
                    //                            + " was closest point - return it");
                    return ccand;
                }
            }
        }
        else { // orient == HOR
            Point2D[] seg = horLineDiv(x);
            cmp = Double.compare(qp.y(), seg[0].y());
            if (cmp < 0) { // start with left subtree
                // System.out.println(" -- go to Left subtree... next orient: " + VERT);
                Point2D lcp = nearest(x.lb, qp, ccand, VERT);
                // System.out.println(" -- BACK FROM Left subtree of " + x.p);
                cmp = compare(lcp, ccand, qp); // [lcp, qp] vs [ccand, qp] - lcp or ccand?

                if (cmp <= 0) { // new closest point it is lcp => explore right subtree
                    // System.out.println(" => go to Right subtree... next orient: " + VERT);
                    Point2D rcp = nearest(x.rt, qp, lcp, VERT);
                    cmp = compare(rcp, lcp, qp); // [rcp, qp] vs [lcp, qp] - rcp or lcp?

                    return (cmp < 0) ? rcp : lcp;
                }
                else { // ccand was the nearest/closest point return it!
                    // System.out.println(" =L2> ccand(--) explored only LB: " + ccand
                    //                            + " was closest point - return it");
                    return ccand;
                }
            }
            else { // start with rigth subtree
                // System.out.println(" -- go to Right subtree... next orient: " + VERT);
                Point2D rcp = nearest(x.rt, qp, ccand, VERT);
                // System.out.println(" -- BACK FROM Right subtree of " + x.p);
                cmp = compare(rcp, ccand, qp); // [rcp, qp] vs [ccand, qp] - rcp or ccand?

                if (cmp <= 0) { // found new closest point it is rcp => explore left subtree
                    // System.out.println(" => go to Left subtree... next orient: " + VERT);
                    Point2D lcp = nearest(x.lb, qp, rcp, VERT);
                    cmp = compare(lcp, rcp, qp); // [lcp, qp] vs [rcp, qp] - lcp or rcp?

                    return (cmp < 0) ? lcp : rcp;
                }
                else { // ccand was the nearest/closest point return it!
                    // System.out.println(" =R2> ccand(--) explored only RT: " + ccand
                    //                            + " was closest point - return it");
                    return ccand;
                }
            }
        }
    }

    private int compare(Point2D p1, Point2D p2, Point2D rp) {
        // cmp d(p1, rp) vs d(p2, rp), rp == reference point
        double d1 = p1.distanceSquaredTo(rp);
        double d2 = p2.distanceSquaredTo(rp);
        int cmp = Double.compare(d1, d2);
        // System.out.println(
        //         "\tcmp d1(" + p1 + ", " + rp + ")= " + d1 + " to d2(" + p2 + ", " + rp + ")= "
        //                 + d2 + " => " + cmp);
        return cmp;
    }

    // private void inOrder(Node x) {
    //     if (x != null) {
    //         if (x.lb != null) inOrder(x.lb);
    //         if (x.rt != null) inOrder(x.rt);
    //     }
    // }

    private void draw(Node n, char orient) { // recursive pre-order Tree Traversal
        // To draw a Node:
        // Draw the vertical line or horizontal line that this Node uses to divide the tree.
        // Draw the point that this node represents.
        // Recursively draw the left child and the right child.
        Point2D[] seg; // orig, dest;
        if (orient == VERT) {
            StdDraw.setPenColor(StdDraw.RED);
            seg = vertLineDiv(n); // orig = new Point2D(n.p.x(), n.rect.ymin());
            // dest = new Point2D(n.p.x(), n.rect.ymax());
        }
        else { // HORIZ
            StdDraw.setPenColor(StdDraw.BLUE);
            seg = horLineDiv(n); // orig = new Point2D(n.rect.xmin(), n.p.y());
            // dest = new Point2D(n.rect.xmax(), n.p.y());
        }
        StdDraw.setPenRadius();
        seg[0].drawTo(seg[1]); // orig.drawTo(dest); // dividing line
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        n.p.draw(); // draw point

        char newOrient = orient == VERT ? HOR : VERT;
        if (n.lb != null) draw(n.lb, newOrient);
        if (n.rt != null) draw(n.rt, newOrient);
    }

    private Point2D[] vertLineDiv(Node n) {
        Point2D orig = new Point2D(n.p.x(), n.rect.ymin()),
                dest = new Point2D(n.p.x(), n.rect.ymax());
        Point2D[] seg = { orig, dest };
        return seg;
    }

    private Point2D[] horLineDiv(Node n) {
        Point2D orig = new Point2D(n.rect.xmin(), n.p.y()),
                dest = new Point2D(n.rect.xmax(), n.p.y());
        Point2D[] seg = { orig, dest };
        return seg;
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

        int ix = 0;
        while (true) {
            // the location (x, y) of the mouse
            double x = 0.25; // 0.5 // StdDraw.mouseX();
            double y = 0.0; // 0.5 // StdDraw.mouseY();
            Point2D query = new Point2D(x, y);

            // draw all of the points
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            kdtree.draw();

            StdDraw.setPenColor(StdDraw.GREEN);
            StdDraw.setPenRadius(0.02);
            query.draw();

            // draw in blue the nearest neighbor (using kd-tree algorithm)
            //
            System.out.println("Which point is closest to " + query + "?");
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.BLUE);
            kdtree.nearest(query).draw();
            StdDraw.show();
            StdDraw.pause(100);

            if (ix == 0) break;
            ix--;
        }
    }
}
