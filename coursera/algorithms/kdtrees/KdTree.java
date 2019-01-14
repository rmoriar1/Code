import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;
import java.util.ArrayList;


public class KdTree {

   private boolean v = true;
   private Node root;
   private int size;
   private Point2D closest;
   private double minDist;
   private static class Node {
      private final Point2D p;      // the point
      private RectHV rect;    // the axis-aligned rectangle corresponding to this node
      private Node lb;        // the left/bottom subtree
      private Node rt;        // the right/top subtree

      public Node(Point2D p)
      {
         this.p = p;
      }
   }

   public KdTree() { // construct an empty set of points 
      root = null;
      size = 0;
   }             
                   
   public boolean isEmpty() { // is the set empty? 
      return size == 0;
   }

   public int size() { // number of points in the set 
      return size;
   }

   public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
      if (p == null)
         throw new java.lang.IllegalArgumentException();
      if (this.contains(p))
         return;
      Node newest = new Node(p);
      if (size == 0)
      {
         newest.rect = new RectHV(0, 0, 1, 1);
         root = newest;
      }
      else
      {
         Node walk = root;
         while (true)
         {
            if (v)
            {
               if (p.x() < walk.p.x())
               {
                  if (walk.lb == null)
                  {
                     walk.lb = newest;
                     newest.rect = new RectHV(walk.rect.xmin(), walk.rect.ymin(), walk.p.x(), walk.rect.ymax());
                     break;
                  }
                  walk = walk.lb;
               }
               else
               {
                  if (walk.rt == null)
                  {
                     walk.rt = newest;
                     newest.rect = new RectHV(walk.p.x(), walk.rect.ymin(), walk.rect.xmax(), walk.rect.ymax());
                     break;
                  }
                  walk = walk.rt;
               }
            }
            else
            {
               if (p.y() < walk.p.y())
               {
                  if (walk.lb == null)
                  {
                     walk.lb = newest;
                     newest.rect = new RectHV(walk.rect.xmin(), walk.rect.ymin(), walk.rect.xmax(), walk.p.y());
                     break;
                  }
                  walk = walk.lb;
               }
               else
               {
                  if (walk.rt == null)
                  {
                     walk.rt = newest;
                     newest.rect = new RectHV(walk.rect.xmin(), walk.p.y(), walk.rect.xmax(), walk.rect.ymax());
                     break;
                  }
                  walk = walk.rt;
               }
            }
            v = !v;
         }
      }
      v = true;
      size++;
   }

   public boolean contains(Point2D p) { // does the set contain point p? 
      if (p == null)
         throw new java.lang.IllegalArgumentException();
      Node walk = root;
      while (walk != null)
      {
         if (p.compareTo(walk.p) == 0)
         {
            v = true;
            return true;
         }
         if (v)
         {
            if (p.x() < walk.p.x())
               walk = walk.lb;
            else
               walk = walk.rt;
         }
         else
         {
            if (p.y() < walk.p.y())
               walk = walk.lb;
            else
               walk = walk.rt;
         }
         v = !v;
      }
      v = true;
      return false;
   }

   public void draw() { // draw all points to standard draw 
      drawRec(root);    
   }

   private void drawRec(Node n)
   {
         StdDraw.setPenColor(StdDraw.BLACK);
         if (n != null)
         {
            n.p.draw();
            drawRec(n.lb);
            drawRec(n.rt);
         }
   }

   public Iterable<Point2D> range(RectHV rect) { // all points that are inside the rectangle (or on the boundary)
      if (rect == null)
         throw new java.lang.IllegalArgumentException();
      ArrayList<Point2D> range = new ArrayList<>();
      rangeRec(root, rect, range, v);
      return range;
   } 

   private void rangeRec(Node n, RectHV rect, ArrayList<Point2D> range, boolean b)
   {
      if (n == null)
         return;
      if (rect.contains(n.p))
      {
         range.add(n.p);
         rangeRec(n.lb, rect, range, !b);
         rangeRec(n.rt, rect, range, !b);
      }
      else
      {
         if (b)
         {
            if (rect.xmax() < n.p.x())
               rangeRec(n.lb, rect, range, !b);
            else if (rect.xmin() > n.p.x())
               rangeRec(n.rt, rect, range, !b);
            else
            {
               rangeRec(n.lb, rect, range, !b);
               rangeRec(n.rt, rect, range, !b);
            }
         }
         else
         {
            if (rect.ymax() < n.p.y())
               rangeRec(n.lb, rect, range, !b);
            else if (rect.ymin() > n.p.y())
               rangeRec(n.rt, rect, range, !b);
            else
            {
               rangeRec(n.lb, rect, range, !b);
               rangeRec(n.rt, rect, range, !b);
            }
         }
      }
   }

   public Point2D nearest(Point2D p) { // a nearest neighbor in the set to point p; null if the set is empty 
      if (p == null)
         throw new java.lang.IllegalArgumentException();
      if (size == 0)
         return null;
      minDist = Double.POSITIVE_INFINITY;
      nearestRec(root, p, v);
      return closest;
   }

   private void nearestRec(Node n, Point2D p, boolean b)
   {
      if (n == null)
         return;
      if  (p.distanceSquaredTo(n.p) < minDist)
      {
         closest = n.p;
         minDist = n.p.distanceSquaredTo(p);
      }
      if (b)
      {
         if (n.p.x() > p.x())
         {
            nearestRec(n.lb, p, !b);
            if (n.rt != null && n.rt.rect.distanceSquaredTo(p) < minDist)
               nearestRec(n.rt, p, !b);
         }
         else
         {
            nearestRec(n.rt, p, !b);
            if (n.lb != null && n.lb.rect.distanceSquaredTo(p) < minDist)
               nearestRec(n.lb, p, !b);
         }
      }
      else
      {
         if (n.p.y() > p.y())
         {
            nearestRec(n.lb, p, !b);
            if (n.rt != null && n.rt.rect.distanceSquaredTo(p) < minDist)
               nearestRec(n.rt, p, !b);
         }
         else
         {
            nearestRec(n.rt, p, !b);
            if (n.lb != null && n.lb.rect.distanceSquaredTo(p) < minDist)
               nearestRec(n.lb, p, !b);
         }
      }
   }

   public static void main(String[] args) { // unit testing of the methods (optional) 
       
   }
}