import java.util.Scanner;

public class Calculator
{
	public static void main(String[] args)
	{
		//Get first operand
		System.out.print("1st input: ");
		Scanner input = new Scanner(System.in);
		double i = input.nextDouble();
		double j;
		int count = 0;
		//Get operator
		System.out.print("op: ");
		String operator = input.next();
		//Continue until user exits
		while (!operator.equals("x"))
		{
			//Check if user cleared the buffer
			if (operator.equals("c"))
			{
				count++;
				i = 0;
				System.out.println("ans: " + i);
			}
			//Else continue
			else
			{
				count++;
				//If this is first calculation ask for 2nd input 
				if (count == 1)
					System.out.print("2nd input: ");
				else
					System.out.print("More input: ");
				j = input.nextDouble();
				//Perform calculation based on operator
				switch (operator)
				{
					case "+":
						i = i + j;
						System.out.println("ans: " + i);
						break;
					case "-":
						i = i - j;
						System.out.println("ans: " + i);
						break;
					case "*":
						i = i * j;
						System.out.println("ans: " + i);
						break;
					case "/":
						//If divisor is zero throw error
						if (j == 0)
							System.out.println("Error: division by zero");
						else
						{
							i = i / j;
							System.out.println("ans: " + i);
						}
						break;
					default: 
						System.out.println("Unknown operator " + operator);
						break;
				}
			}
			System.out.print("op: ");
			operator = input.next();
		}
		System.out.println("Exiting");
		System.exit(1);
	}
}