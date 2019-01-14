import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private Node end;
    private int moves;

    private class Node implements Comparable<Node>
    {
      private final Board current;
      private final int moves;
      private final Node prev;
      private int mtn;
      public Node(Board current, int moves, Node prev)
      {
        this.current = current;
        this.moves = moves;
        this.prev = prev;
        mtn = this.current.manhattan();
      }
      public int compareTo(Node that)
      {
        return this.moves + mtn - that.moves - that.mtn;
      }
    }
    public Solver(Board initial) 
    {
        if (initial == null)
          throw new java.lang.IllegalArgumentException();
        MinPQ<Node> pq = new MinPQ<>();
        MinPQ<Node> pqTwin = new MinPQ<>();
        Node start = new Node(initial, 0, null);
        Node startTwin = new Node(initial.twin(), 0, null);
        boolean foundSol = false;
        pq.insert(start);
        pqTwin.insert(startTwin);
        while (!foundSol)
        {
          if (pq.size() > 0)
          {
            Node a = pq.delMin();
            if (a.current.isGoal())
            {
              end = a;
              moves = a.moves;
              foundSol = true;
            }
            for (Board b : a.current.neighbors())
            {
              if (a.prev == null || !b.equals(a.prev.current))
              {
                pq.insert(new Node(b, a.moves+1, a));
              }
             }
          }
          if (pqTwin.size() > 0)
          {
            Node b = pqTwin.delMin();
            if (b.current.isGoal())
            {
              end = null;
              moves = -1;
              foundSol = true;
            }
            for (Board c : b.current.neighbors())
            {
              if (b.prev == null || !c.equals(b.prev.current))
              {
                pqTwin.insert(new Node(c, b.moves+1, b));
              }
            }
          }
        }
    }   
           // find a solution to the initial board (using the A* algorithm)
    public boolean isSolvable()  
    {
      return moves != -1;
    }   
           // is the initial board solvable?
    public int moves()     
    {
      return moves;
    }   
                 // min number of moves to solve initial board; -1 if unsolvable
    public Iterable<Board> solution() 
    {
      if (end == null)
        return null;
      Stack<Board> solution = new Stack<Board>();
      Node iter = new Node(end.current, end.moves, end.prev);
      while (iter.prev != null)
      {
        solution.push(iter.current);
        iter = iter.prev;
      }
      solution.push(iter.current);
      return solution;
    }   
      // sequence of boards in a shortest solution; null if unsolvable
    public static void main(String[] args)
    {
       In in = new In(args[0]);
      int n = in.readInt();
      int[][] blocks = new int[n][n];
      for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
          blocks[i][j] = in.readInt();
      Board initial = new Board(blocks);

      // solve the puzzle
      Solver solver = new Solver(initial);

      // print solution to standard output
      if (!solver.isSolvable())
        StdOut.println("No solution possible");
      else 
      {
        StdOut.println("Minimum number of moves = " + solver.moves() + "\n");
        for (Board board : solver.solution())
            StdOut.println(board);
      }
    } // solve a slider puzzle (given below)
}