public class ArrayStack<E> implements Stack<E>
{
	public static final int CAPACITY = 1000;
	private E[] data;
	public int size = 0;

	public ArrayStack()
	{
		this(CAPACITY);
	}

	public ArrayStack(int capacity)
	{
		data = (E[]) new Object[capacity];
	}

	public int size()
	{
		return size;
	}

	public boolean isEmpty()
	{
		return size == 0;
	}

	public void push(E e) throws IllegalStateException
	{
		if (size == data.length)
			throw new IllegalStateException("Stack is Full");
		data[size] = e;
		size++;
	}

	public E pop()
	{
		if (isEmpty())
			return null;
		E answer = data[size - 1];
		data[size - 1] = null;
		size--;
		return answer;
	}

	public E top()
	{
		if (isEmpty())
			return null;
		return data[size - 1];
	}
}

