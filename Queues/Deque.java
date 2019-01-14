import java.util.Iterator;

public class Deque<Item> implements Iterable<Item> 
{
	private final Node<Item> firstPlaceholder;
	private final Node<Item> lastPlaceholder;
	private int size;

	private class Node<Item>
	{
		private final Item item;
		private Node<Item> previous;
		private Node<Item> next;
		public Node(Item item, Node<Item> previous, Node<Item> next)
		{
			this.item = item;
			this.previous = previous;
			this.next = next;
		}
	}

   	public Deque() 
    {
   		firstPlaceholder = new Node<Item>(null, null, null);
   		lastPlaceholder = new Node<Item>(null, firstPlaceholder, null);
   		firstPlaceholder.next = lastPlaceholder;
   		size = 0;
    }                          // construct an empty deque

    public boolean isEmpty() 
    {
    	return size == 0;
    }                // is the deque empty?

    public int size() 
    {
    	return size;
    }                    // return the number of items on the deque

    public void addFirst(Item item)
    {
    	if (item == null)
    		throw new java.lang.IllegalArgumentException();
    	Node<Item> newFirst = new Node<Item>(item, firstPlaceholder, firstPlaceholder.next);
    	firstPlaceholder.next.previous = newFirst;
    	firstPlaceholder.next = newFirst;
    	size++;
    }          // add the item to the front

    public void addLast(Item item) 
    {
    	if (item == null)
    		throw new java.lang.IllegalArgumentException();
    	Node<Item> newLast = new Node<Item>(item, lastPlaceholder.previous, lastPlaceholder);
    	newLast.previous.next = newLast;
    	lastPlaceholder.previous = newLast;
    	size++;

    }         // add the item to the end

    public Item removeFirst()      
    {
    	if (size == 0)
    		throw new java.util.NoSuchElementException();
    	Node<Item> first = firstPlaceholder.next;
    	firstPlaceholder.next = firstPlaceholder.next.next;
    	firstPlaceholder.next.previous = firstPlaceholder;
    	size--;
    	return first.item;

    }          // remove and return the item from the front

    public Item removeLast()     
    {
    	if (size == 0)
    		throw new java.util.NoSuchElementException();
    	Node<Item> last = lastPlaceholder.previous;
    	lastPlaceholder.previous = last.previous;
    	last.previous.next = lastPlaceholder;
    	size--;
    	return last.item;
    }            // remove and return the item from the end

    public Iterator<Item> iterator()   
    {
    	return new ListIterator();
    } 

    private class ListIterator implements Iterator<Item> {
        private Node<Item> current = firstPlaceholder.next;
        public boolean hasNext()  { return current.next != null;                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new java.util.NoSuchElementException();
            Item item = current.item;
            current = current.next; 
            return item;
        }
    }
     // return an iterator over items in order from front to end

    public static void main(String[] args)
    {
    	Deque<Integer> d = new Deque<>();
        for (int i = 0; i < 9; i++) {
            d.addFirst(i);
        }
        for (int i = 0; i < 9; i++) {
            System.out.println(d.removeLast());
        }
        System.out.println(d.isEmpty());
    }   // unit testing (optional)
}