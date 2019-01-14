public class Converter 
{
	public String infix;
	public ArrayStack<Character> stack = new ArrayStack<Character>();
	public String postfix = "";

	public Converter(String infix)
	{
		this.infix = infix;
	}

	public String toPostFix()
	{
		// Create char array from infix string
		char[] tokens = infix.toCharArray();
		for (int i = 0; i < tokens.length; i++)
		{
			char c = tokens[i];
			// If char is Digit check to see if its a multi digit number
			if (Character.isDigit(c))
				// If so do not add a space after the digit in postfix string
				if (i + 1 < tokens.length && Character.isDigit(tokens[i+1]))
					postfix += c + "";
				else
					postfix += c + " ";
			// If char is left bracket push onto stack
			else if (c == '(')
				stack.push(c);
			// If char is right bracket pop until left bracket
			else if (c == ')')
			{
				while (stack.top() != '(')
					postfix += stack.pop() + " ";
				// Pop again to remove left bracket from stack
				stack.pop();
			}
			else if (c == '*' || c == '/' || c == '+' || c == '^' || 
	                c == '-' || c == '(' || c == ')')
			{
				/* If char is operator pop until top of stack is of lower 
				precendece, is a left bracket or is empty, then add char*/
				while (!stack.isEmpty() && stack.top() != '(' && 
					operatorPrecedence(c, stack.top()) <= 0)
					postfix += stack.pop() + " ";
				stack.push(c);
			}
		}
		// After reading all the tokens pop from stack until its empty 
		while (!stack.isEmpty())
		{
			postfix += stack.pop() + " ";
		}
		return postfix;
	}

	// Method for testing opPrecedence: ^ > * = / > + = -
	public int operatorPrecedence(char a, char b)
	{
		int aVal = 0;
		int bVal = 0;
		if (a == '^')
			aVal = 2;
		else if (a == '*' || a == '/')
			aVal = 1;
		if (b == '^')
			bVal = 2;
		else if (b == '*' || b == '/')
			bVal = 1;
		if (aVal > bVal)
			return 1;
		else if (aVal < bVal)
			return -1;
		else 
			return 0;
	}
}