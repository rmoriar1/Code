import edu.princeton.cs.algs4.StdIn;

public class Permutation {
   public static void main(String[] args)
   {
   		int k = Integer.parseInt(args[0]);
   		RandomizedQueue<String> rq = new RandomizedQueue();
   		int l = 0;
   		while (!StdIn.isEmpty())
   		{
   			rq.enqueue(StdIn.readString());
   			l++;
   		}
   		for (int i = 0; i < k; i++)
   		{
   			System.out.println(rq.dequeue());
   		}
   }
}
