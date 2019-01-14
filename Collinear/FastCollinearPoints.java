import java.util.ArrayList;
import java.util.Arrays;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.In;

public class FastCollinearPoints 
{
  private final LineSegment [] ls;

   public FastCollinearPoints(Point[] points) 
   {
    if (points == null)
      throw new java.lang.IllegalArgumentException();
    for (int i = 0; i < points.length; i++)
      if (points[i] == null)
        throw new java.lang.IllegalArgumentException();
    for (int i = 0; i < points.length - 1; i++)
      for (int j = i+1; j < points.length; j++)
        if (points[i].compareTo(points[j]) == 0)
          throw new java.lang.IllegalArgumentException();
    ArrayList<LineSegment> foundSegment = new ArrayList<>();     
    for (int i = 0; i < points.length; i++)
    {
      Point [] pointsCopy = Arrays.copyOf(points, points.length);
      Point p = points[i];
      Arrays.sort(pointsCopy, p.slopeOrder());
      for (int j = 1; j < pointsCopy.length-2; j++)
      {
        if (p.slopeTo(pointsCopy[j]) == p.slopeTo(pointsCopy[j+1]) &&
            p.slopeTo(pointsCopy[j+1]) == p.slopeTo(pointsCopy[j+2])) 
          if (p.compareTo(pointsCopy[j]) < 0 && p.compareTo(pointsCopy[j+1]) < 0 &&
              p.compareTo(pointsCopy[j+2]) < 0)
            if (pointsCopy[j+2].compareTo(pointsCopy[j+1]) > 0 && 
                pointsCopy[j+2].compareTo(pointsCopy[j]) > 0)
                foundSegment.add(new LineSegment(p, pointsCopy[j+2]));
            else if (pointsCopy[j+1].compareTo(pointsCopy[j+2]) > 0 && 
                pointsCopy[j+1].compareTo(pointsCopy[j]) > 0)
                foundSegment.add(new LineSegment(p, pointsCopy[j+1]));
            else 
                foundSegment.add(new LineSegment(p, pointsCopy[j]));

      }
    }
    ls = new LineSegment[foundSegment.size()];
    foundSegment.toArray(ls);
   }
       // finds all line segments containing 4 or more points
   public int numberOfSegments()
   {
      return ls.length;
   }
           // the number of line segments
   public LineSegment[] segments()
   {
      LineSegment[] lsCopy = Arrays.copyOf(ls, ls.length);
      return lsCopy;
   }                // the line segments
   public static void main(String[] args)
   {
      // read the n points from a file
    In in = new In(args[0]);
    int n = in.readInt();
    Point[] points = new Point[n];
    for (int i = 0; i < n; i++) {
        int x = in.readInt();
        int y = in.readInt();
        points[i] = new Point(x, y);
    }

    // draw the points
    StdDraw.enableDoubleBuffering();
    StdDraw.setXscale(0, 32768);
    StdDraw.setYscale(0, 32768);
    for (Point p : points) {
        p.draw();
    }
    StdDraw.show();

    // print and draw the line segments
    FastCollinearPoints collinear = new FastCollinearPoints(points);
    for (LineSegment segment : collinear.segments()) {
        StdOut.println(segment);
        segment.draw();
    }
    StdDraw.show();
   }
}