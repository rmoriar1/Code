import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.SET;


public class PointSET {

   private final SET<Point2D> ps;
   public PointSET() { // construct an empty set of points 
      ps = new SET<Point2D>();
   }             
                   
   public boolean isEmpty() { // is the set empty? 
      return ps.size() == 0;
   }

   public int size() { // number of points in the set 
      return ps.size();
   }

   public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
      if (p == null)
         throw new java.lang.IllegalArgumentException();
      if (ps.contains(p))
         return;
      ps.add(p);
   }

   public boolean contains(Point2D p) { // does the set contain point p? 
      return ps.contains(p);
   }

   public void draw() { // draw all points to standard draw 
      for (Point2D pt : ps)
         pt.draw();
   }

   public Iterable<Point2D> range(RectHV rect) { // all points that are inside the rectangle (or on the boundary)
      if (rect == null)
         throw new java.lang.IllegalArgumentException();
      SET<Point2D> range = new SET<>();
      for (Point2D pt : ps)
      {
         if (rect.contains(pt))
            range.add(pt);
      }
      return range;
   } 

   public Point2D nearest(Point2D p) { // a nearest neighbor in the set to point p; null if the set is empty 
      if (p == null)
         throw new java.lang.IllegalArgumentException();
      double distance = Double.POSITIVE_INFINITY;
      Point2D closest = null;
      for (Point2D pt : ps)
         if (pt.distanceSquaredTo(p) < distance)
         {
            distance = pt.distanceSquaredTo(p);
            closest = pt;
         }
      return closest;
   }

   public static void main(String[] args) { // unit testing of the methods (optional) 
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }
        System.out.println(brute.size());
        double x0 = 0.25, y0 = 0.25;      // initial endpoint of rectangle
        double x1 = 0.75, y1 = 0.75;      // current location of mouse
        RectHV r = new RectHV(x0, y0, x1, y1);    // is the user dragging a rectangle
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        brute.draw();
        StdDraw.show();
        StdDraw.pause(1000);
        StdDraw.setPenColor(StdDraw.RED);
        r.draw();
        Point2D test = new Point2D(0.5, 0.5);
        test.draw();
        r.draw();
        brute.nearest(test).draw();
        for (Point2D pt : brute.range(r))
         pt.draw();
        StdDraw.show();
   }
}