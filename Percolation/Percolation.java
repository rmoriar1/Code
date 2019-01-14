import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
	private final int n;
	private final WeightedQuickUnionUF uf; 
	private boolean [][] grid;

	private int xyTo1D(int x, int y)
	{
		if (!isValid(x,y))
			throw new java.lang.IllegalArgumentException();
		return (x-1)*(n) + y;
	}

	private boolean isValid(int x, int y)
	{
		if (x>n || x<1 || y>n || y<1)
		{
			return false;
		}
		return true;
	}

  	public Percolation(int n)
  	{
  		if (n<=0)
  			throw new java.lang.IllegalArgumentException();
  		this.n = n;
  		uf = new WeightedQuickUnionUF(n*n + 2);
  		grid = new boolean[n][n];
  	}  

    public void open(int row, int col)
    {
    	int openedSite = xyTo1D(row,col);
    	grid[row-1][col-1] = true;
    	if (row ==1)
    		uf.union(0,openedSite);
    	if (row == n)
    		uf.union(openedSite, n*n+1);
    	if (row-1 >0 && grid[row-2][col-1] == true)
    		uf.union(openedSite, xyTo1D(row-1,col));
    	if (row+1 <=n && grid[row][col-1] == true)
    		uf.union(openedSite, xyTo1D(row+1,col));
    	if (col-1 >0 && grid[row-1][col-2] == true)
    		uf.union(openedSite, xyTo1D(row,col-1));
    	if (col+1 <=n && grid[row-1][col] == true)
    		uf.union(openedSite, xyTo1D(row,col+1));
    }    // open site (row, col) if it is not open already
    public boolean isOpen(int row, int col)
    {
    	int openedSite = xyTo1D(row,col);
    	return grid[row-1][col-1];
    }  // is site (row, col) open?
    public boolean isFull(int row, int col)
    {
    	return uf.connected(xyTo1D(row, col), 0);
    }  // is site (row, col) full?
    public int numberOfOpenSites()
    {
    	int os = 0;
    	for (int i = 0; i < n; i++)
    		for (int j = 0; j<n; j++)
    			if (grid[i][j])
    				os++;
    	return os;
    }       // number of open sites
    public boolean percolates()
    {
    	return uf.connected(0,n*n+1);
    }              // does the system percolate?

    public static void main(String[] args)
    {
    	Percolation x = new Percolation(4);
    	x.open(1,1);
    	x.open(2,1);
    	x.open(3,1);
    	x.open(4,4);
    	x.open(4,1);
    	System.out.println(x.uf.connected(0,1));
    	System.out.println(x.uf.connected(13,17));
    	System.out.println(x.numberOfOpenSites());
    	System.out.println(x.percolates());
    }   // test client (optional)
}