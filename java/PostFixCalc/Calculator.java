import java.util.Scanner;

public class Calculator
{
	public static void main(String[] args)
	{
		Scanner input = new Scanner(System.in);
		System.out.print("Enter infix expression: ");
		String infix = input.nextLine();
		String postfix = new Converter(infix).toPostFix();
		System.out.println("Postfix expression: " + postfix);
		Calculator c = new Calculator(postfix);
		System.out.println("Result: " + c.evaluate());
		System.exit(0);
	}
	
	public String postfix;
	public ArrayStack<Double> operands = new ArrayStack<>();
	
	public Calculator(String postfix) 
	{
		this.postfix = postfix;
	}

	public double evaluate() throws IllegalArgumentException
	{
		double result = 0;
		// Create char array from postfix string
		char[] postFixTokens = postfix.toCharArray();
		for (int i = 0; i < postFixTokens.length; i++)
		{
			char c = postFixTokens[i];
			/* If char is digit, create a number string and add to it until the
			following char is not a digit. Convert string to double and push */
			if (Character.isDigit(c))
			{
				String number = c + "";
				for (int j = i + 1; j < postFixTokens.length; j++) 
				{
	                if (Character.isDigit(postFixTokens[j])) 
	                {
	                    number += postFixTokens[j];
	                    i = j;
	                } 
	                else 
	                    break;
	            }
				operands.push(Double.parseDouble(number));
				continue;
			}
			/* If char is operator perform the operation on the top two numbers
			on the stack. Make sure first popped number is second in the calc
			on operations where order matters. Push result onto stack */
			switch (c)
			{
				case '*':
						result = operands.pop() * operands.pop();
						operands.push(result);
						break;
				case '/':
						double divisor = operands.pop();
						if (divisor == 0)
							throw new 
							IllegalArgumentException("Divisor cannot be zero");
						result = operands.pop() / divisor;
						operands.push(result);
						break;
				case '+':
						result = operands.pop() + operands.pop();
						operands.push(result);
						break;
				case '-':
						double second = operands.pop();
						result = operands.pop() - second;
						operands.push(result);
						break;
				case '^':
						double exponent = operands.pop();
						result = Math.pow(operands.pop(), exponent);
						operands.push(result);
						break;
			}
		}
		// After iterating, the result should be the only number on stack
		return operands.pop();
	}
}