import java.util.Arrays;

public class Set<E>
{
	private static class Node<E>
	{
		private E element;
		private Node<E> next;
		public Node(E e, Node<E> n)
		{
			element = e;
			next = n;
		}
		public E getElement()
		{
			return element;
		}
		public Node<E> getNext()
		{
			return next;
		}
		public void setNext(Node<E> n)
		{
			next = n;
		}
	}

	private Node<E> head = null;
	private Node<E> tail = null;
	private int size = 0;

	public Set() {}

	public Set(E[] elems)
	{
		removeDuplicates(elems);
		for(int i = 0; i < elems.length; i++)
		{
			if (elems[i] != null)
				add(elems[i]);
		}
	}
	//O(1), checks size once and returns
	public boolean isEmpty() 
	{
		return size == 0;
	}
	/*O(N), add calls contains method which is O(N) because it checks each 
	element in the set. Actually adding the node is constant, but O(N) 
	dominates*/
	public void add(E e)
	{
		if (contains(e))
			return;
		Node<E> newest = new Node<>(e,null);
		if (size == 0)
			head = newest;
		else 
			tail.setNext(newest);
		tail = newest;
		size++;
	}
	//O(1), checks size once and returns
	public int size()
	{
		return size;
	}
	//O(N), walks through each element in the set to see if it matches e
	public E remove(E e)
	{
		E answer = null;
		if (size == 0)
			return null;
		if (head.getElement() == e)
		{
			answer = head.getElement();
			head = head.getNext();
		}
		else 
		{
			Node<E> walk = head;
			while(walk.next != null && walk.next.getElement() != e)
			{
				walk = walk.next;
			}
			if (walk == tail)
				return null;
			else if (walk.next == tail)
				tail = walk;
			answer = walk.next.getElement();
			walk.next = walk.next.next;
		}
		size--;
		if (size == 0)
			tail = null;
		return answer;
	}
	//O(N), checks each element in the set to see if it matches e
	public boolean contains(E e)
	{
		if (size == 0)
			return false;
		if (head.getElement() == e)
			return true;
		Node<E> walk = head;
		while(walk.next != null)
		{
			if (walk.next.getElement() == e)
				return true;
			walk = walk.next;
		}
		return false;
	}
	/*O(N^2), Call to sort is N log N. Method then iterates over each element
	in the array and each time iterates over the remainder of the array to 
	check for duplicates, ~N^2/2 worst case*/
	public void removeDuplicates(E[] elems)
	{
		Arrays.sort(elems);
		for (int i = 0; i < elems.length; i++)
		{
			for (int j = i + 1; j < elems.length; j++)
			{
				if (elems[j] == elems[i])
					elems[j] = null;
				else
				{
					i = j - 1;
					break;
				}
			}
		}
	}
	//O(N), iterates over the entire array and adds element to string
	public String toString()
	{
		if (size == 0)
			return new String("Set is empty!");
		String setString = "";
		Node<E> walk = head;
		while (walk != null)
		{
			setString += walk.getElement();
			if (walk != tail)
				setString += ", ";
			walk = walk.next;
		}
		return setString;
	}
}