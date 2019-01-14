import edu.princeton.cs.algs4.In;
import java.util.ArrayList;

public class Board {

    private final int[][] blocks;
    private final int n;

    public Board(int[][] blocks)
    {
        n = blocks.length;
        this.blocks = new int[n][n];
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                this.blocks[i][j] = blocks[i][j];
            }
        }
    }        // construct a board from an n-by-n array of blocks
            // (where blocks[i][j] = block in row i, column j)
    public int dimension()
    {
        return n;
    }   
                  // board dimension n
    public int hamming()
    {
        int hamming = 0;
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (blocks[i][j] != 0 && blocks[i][j] != i * n + j + 1)
                    hamming++;
            }
        }
        return hamming;
    }   
                    // number of blocks out of place
    public int manhattan() 
    {
        int manhattan = 0;
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (blocks[i][j] != 0 && blocks[i][j] != i * n + j + 1)
                {
                    manhattan += (Math.abs((blocks[i][j] - 1) / n - i) + 
                                  Math.abs((blocks[i][j] - 1) % n - j));
                }
            }
        }
        return manhattan;
    }   
                // sum of Manhattan distances between blocks and goal
    public boolean isGoal()
    {
        return hamming() == 0;
    }                // is this board the goal board?
    public Board twin()  
    {
        int [][] twinArr = new int[n][n];
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                twinArr[i][j] = blocks[i][j];
            }
        }
        Board twin = new Board(twinArr);
        if (twin.blocks[0][0] != 0 && twin.blocks[0][1] != 0)
        {
            twin.blocks[0][0] = blocks[0][1];
            twin.blocks[0][1] = blocks[0][0];
        }
        else
        {
            twin.blocks[1][0] = blocks[1][1];
            twin.blocks[1][1] = blocks[1][0];
        }
        return twin;
    }                 
     // a board that is obtained by exchanging any pair of blocks
    public boolean equals(Object y) 
    {
        if (y == null)
            return false;
        if (y.getClass() != this.getClass())
            return false;
        Board eq = (Board) y;
        if (eq.n != n)
            return false;
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (blocks[i][j] != eq.blocks[i][j])
                    return false;
            }
        }
        return true;
        
    }   
        // does this board equal y?
    public Iterable<Board> neighbors() 
    {
        ArrayList<Board> neighbors = new ArrayList<Board>();
        int i = 0;
        int j = 0;
        for (int a = 0; a < n; a++)
        {
            for (int b = 0; b < n; b++)
            {
                if (blocks[a][b] == 0)
                {
                    i = a;
                    j = b;
                    break;
                }
            }
        }
        if (i > 0)
        {
            int [][] neighArr1 = new int[n][n];
            for (int a = 0; a < n; a++)
            {
                for (int b = 0; b < n; b++)
                {
                neighArr1[a][b] = blocks[a][b];
                }
            }
            neighArr1[i-1][j] = blocks[i][j];
            neighArr1[i][j] = blocks[i-1][j];
            neighbors.add(new Board(neighArr1));
        }
        if (i + 1 < n)
        {
            int [][] neighArr2 = new int[n][n];
            for (int a = 0; a < n; a++)
            {
                for (int b = 0; b < n; b++)
                {
                neighArr2[a][b] = blocks[a][b];
                }
            }
            neighArr2[i+1][j] = blocks[i][j];
            neighArr2[i][j] = blocks[i+1][j];
            neighbors.add(new Board(neighArr2));
        }
        if (j > 0)
        {
            int [][] neighArr3 = new int[n][n];
            for (int a = 0; a < n; a++)
            {
                for (int b = 0; b < n; b++)
                {
                neighArr3[a][b] = blocks[a][b];
                }
            }
            neighArr3[i][j-1] = blocks[i][j];
            neighArr3[i][j] = blocks[i][j-1];
            neighbors.add(new Board(neighArr3));
        }
        if (j + 1 < n)
        {
            int [][] neighArr4 = new int[n][n];
            for (int a = 0; a < n; a++)
            {
                for (int b = 0; b < n; b++)
                {
                neighArr4[a][b] = blocks[a][b];
                }
            }
            neighArr4[i][j+1] = blocks[i][j];
            neighArr4[i][j] = blocks[i][j+1];
            neighbors.add(new Board(neighArr4));
        }
        return neighbors;
    }   
     // all neighboring boards
    public String toString()  
    {
        StringBuilder sb = new StringBuilder();
        sb.append(n + "\n");
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                sb.append(String.format("%2d ", blocks[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }             // string representation of this board (in the output format specified below)

    public static void main(String[] args) 
    {
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        System.out.println(initial.hamming());
        System.out.println(initial.manhattan());
        System.out.println(initial.isGoal());
        System.out.println(initial.dimension());
        System.out.println(initial);
        System.out.println(initial.twin().toString());
        System.out.println(initial.neighbors());
    }
}