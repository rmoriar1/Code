import java.util.ArrayList;
import java.util.Arrays;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.In;

public class BruteCollinearPoints 
{
  private final LineSegment[] ls;
  public BruteCollinearPoints(Point[] points)
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
   	Point[] pointsCopy = Arrays.copyOf(points, points.length);
   	Arrays.sort(pointsCopy);
   	for (int a = 0; a < pointsCopy.length; a++)
    {
   		for (int b = a + 1; b < pointsCopy.length; b++)
      {
   			for (int c = b + 1; c < pointsCopy.length; c++)
        {
          if (pointsCopy[a].slopeTo(pointsCopy[b]) != pointsCopy[a].slopeTo(pointsCopy[c]))
            continue;   
   				for (int d = c + 1; d < pointsCopy.length; d++)
   				{   
            if (pointsCopy[a].slopeTo(pointsCopy[c]) == pointsCopy[a].slopeTo(pointsCopy[d]))
                foundSegment.add(new LineSegment(pointsCopy[a], pointsCopy[d]));
          }
        }
      }
    }
    ls = new LineSegment[foundSegment.size()];
    foundSegment.toArray(ls);
  }
       // finds all line segments containing 4 points
  public int numberOfSegments()
  {
    return ls.length;
  }
           // the number of line segments
  public LineSegment[] segments()
  {
    LineSegment[] lsCopy = Arrays.copyOf(ls, ls.length);
    return lsCopy;
  }
                  // the line segments
  public static void main(String[] args)
  {
      // read the n points from a file
    In in = new In(args[0]);
    int n = in.readInt();
    Point[] points = new Point[n];
    for (int i = 0; i < n; i++) 
    {
      int x = in.readInt();
      int y = in.readInt();
      points[i] = new Point(x, y);
    }

        // draw the points
    StdDraw.enableDoubleBuffering();
    StdDraw.setXscale(0, 32768);
    StdDraw.setYscale(0, 32768);
    for (Point p : points) 
    {
      p.draw();
    }
    StdDraw.show();

        // print and draw the line segments
    BruteCollinearPoints collinear = new BruteCollinearPoints(points);
    for (LineSegment segment : collinear.segments()) 
    {
      StdOut.println(segment);
      segment.draw();
    }
    StdDraw.show();
   }
}