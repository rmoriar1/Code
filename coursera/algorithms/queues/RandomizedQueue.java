import java.util.Iterator;
import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> 
{
	private Item[] a;
	private int size;

    public RandomizedQueue() 
    {
    	a = (Item[]) new Object[2];
    	size = 0;
    }              
      // construct an empty randomized queue
    public boolean isEmpty() 
    {
    	return size == 0;
    }
                    // is the randomized queue empty?
    public int size()     
    {
    	return size;
    }
                      // return the number of items on the randomized queue
    public void enqueue(Item item)
    {
    	if (item == null)
    		throw new java.lang.IllegalArgumentException();
    	if (size == a.length)
    	{
    		Item[] temp = (Item[]) new Object[size*2];
    		for (int i = 0; i < size; i++)
    			temp[i] = a[i];
    		a = temp;
    	}
    	a[size] = item;
    	size++;
    }
              // add the item
    public Item dequeue()    
    {
    	if (size == 0)
    		throw new java.util.NoSuchElementException();
    	
    	int x = StdRandom.uniform(size);
    	Item item = a[x];
    	size--;
    	a[x] = a[size];
    	if (size <= a.length/4)
    	{
    		Item[] temp = (Item[]) new Object[a.length/2];
    		for (int i = 0; i < size; i++)
    			temp[i] = a[i];
    		a = temp;
    	}
    	return item;
    }                
   // remove and return a random item
    public Item sample() 
    {
    	if (size == 0)
    		throw new java.util.NoSuchElementException();
    	return a[StdRandom.uniform(size)];
    }                 
      // return a random item (but do not remove it)
    public Iterator<Item> iterator()  
    {
		return new RQueueIterator();
    } 

    private class RQueueIterator implements Iterator<Item> 
    {
        public boolean hasNext()  { return size != 0; }
        public void remove()      { throw new UnsupportedOperationException(); }

        public Item next() 
        {
            if (!hasNext()) throw new java.util.NoSuchElementException();
            return dequeue();
        }
    }   
    // return an independent iterator over items in random order
    public static void main(String[] args) 
    {
    	RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();
        System.out.println(rq.size());
        System.out.println(rq.isEmpty());
        rq.enqueue(270);
        System.out.println(rq.size()); 
        System.out.println(rq.size());  
        rq.enqueue(27);
        System.out.println(rq.size()); 
        System.out.println(rq.isEmpty()); 
        rq.enqueue(476);
        System.out.println(rq.size()); 
        System.out.println(rq.dequeue());
        System.out.println(rq.dequeue());
        System.out.println(rq.dequeue());
        System.out.println(rq.isEmpty());
    }  // unit testing (optional)
}