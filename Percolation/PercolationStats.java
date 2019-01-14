import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class PercolationStats {
	private final int trials;
	private final double [] openedSites;
   public PercolationStats(int n, int trials)
   {
   		if (n<=0 || trials <=0)
   			throw new java.lang.IllegalArgumentException();
   		this.trials = trials;
   		openedSites = new double[trials];
   		for (int i = 1; i <= trials; i++)
   		{
   			Percolation trial = new Percolation(n);
   			while(!trial.percolates())
   			{
   				int randomX = StdRandom.uniform(n) + 1;
   				int randomY = StdRandom.uniform(n) + 1;
   				trial.open(randomX, randomY);
   			}
   			openedSites[i-1] = (double) trial.numberOfOpenSites()/ (n*n);
   		}
   }    // perform trials independent experiments on an n-by-n grid
   public double mean()
   {
   		return StdStats.mean(openedSites);
   }                          // sample mean of percolation threshold
   public double stddev()
   {
   		return StdStats.stddev(openedSites);
   }                        // sample standard deviation of percolation threshold
   public double confidenceLo()
   {
   		return mean() - ((stddev() * 1.96) / Math.sqrt(trials));
   }                  // low  endpoint of 95% confidence interval
   public double confidenceHi()
   {
   		return mean() + ((stddev() * 1.96) / Math.sqrt(trials));
   }                  // high endpoint of 95% confidence interval

   public static void main(String[] args){

   		PercolationStats x = new PercolationStats(Integer.parseInt(args[0]),
   			Integer.parseInt(args[1]));
   		System.out.println("mean: " + x.mean());
   		System.out.println("stddev: " + x.stddev());
   		System.out.println("95% confidence interval : [" + 
   			x.confidenceLo() + ", " + x.confidenceHi() + "]");
   }        // test client (described below)
}