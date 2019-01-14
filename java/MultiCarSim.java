import java.util.Scanner;

public class MultiCarSim
{
	public static void main(String[] args)
	{
		//Initialize array for each attribute
		boolean[] ignitions = new boolean[10];
		int[] xCoordinates = new int[10];
		int[] yCoordinates = new int[10];
		char[] colors = new char[10];
		//Create cars by iterating over arrays and initializing attributes
		for (int i = 0; i < ignitions.length; i++)
		{
			xCoordinates[i] = randomizePosition();
			yCoordinates[i] = randomizePosition();
			colors[i] = assignColor();
		}
		Scanner input = new Scanner(System.in);
		//Ask user which car
		System.out.println("\nWhich car would you like to use? " + 
						   "(choose from 1-10)");
		System.out.print("\nInput: ");
		//Decrement by one because arrays start from 0, use this as index
		int currentCar = input.nextInt() - 1;
		//Provide options menus
		System.out.println("\nWhat would you like to do?\n");
		System.out.println("1: Turn the igntion on/off");
		System.out.println("2: Change the position of the car");
		System.out.println("Q: Quit this program\n");
		System.out.print("Input: ");
		String userInput = input.next();
		//Exit if Q is entered
		while (!userInput.equals("Q"))
		{
			if (userInput.equals("1"))
			{
				/*Change ignition and report state passing arguments using
				 currentCar as index of attribute */
				ignitions[currentCar] = 
				ignitionSwitch(ignitions[currentCar]);
				reportState(colors[currentCar], ignitions[currentCar], 
					xCoordinates[currentCar], yCoordinates[currentCar],
					currentCar);

			}
			else if(userInput.equals("2"))
			{
				//Ask which direction
				System.out.println("\nIn which direction do you want to move" +
				" the car?\n");
				System.out.println("H: Horizontal");
				System.out.println("V: Verical\n");
				System.out.print("Direction: ");
				String direction = input.next();
				if (direction.equals("H"))
				{
					/*Ask for distance and run moveHorizontally method with 
					result as parameter*/
					System.out.print("\nEnter a movement distance: ");
					int d = input.nextInt();
					xCoordinates[currentCar] = 
					moveHorizontally(xCoordinates[currentCar], d, 
						ignitions[currentCar]);
					reportState(colors[currentCar], ignitions[currentCar], 
						xCoordinates[currentCar], yCoordinates[currentCar],
						currentCar);
				}
				else if (direction.equals("V"))
				{
					/*Ask for distance and run moveVertically method with 
					result as parameter*/
					System.out.print("\nEnter a movement distance: ");
					int d = input.nextInt();
					yCoordinates[currentCar] = 
					moveVertically(yCoordinates[currentCar], d, 
						ignitions[currentCar]);
					reportState(colors[currentCar], ignitions[currentCar], 
						xCoordinates[currentCar], yCoordinates[currentCar],
						currentCar);
				}
				else
				{
					//Neither H or V was entered
					System.out.println("\nInvalid input");
				}
			}
			//Neither 1 2 or Q was entered
			else
				System.out.println("\nInvalid input");
			//Repeat until user quits
			System.out.println("\nWhich car would you like to use? " + 
							   "(choose from 1-10)");
			System.out.print("\nInput: ");
			currentCar = input.nextInt() - 1;
			System.out.println("\nWhat would you like to do?\n");
			System.out.println("1: Turn the igntion on/off");
			System.out.println("2: Change the position of the car");            
			System.out.println("Q: Quit this program\n");
			System.out.print("Input: ");
			userInput = input.next();
		}
	}
	//Pick random num from 1-20
	public static int randomizePosition()
	{
		return (int) (Math.random() * 20) + 1;
	}
	//Pick random num from 0-4 and choose color based on result
	public static char assignColor()
	{
		int i = (int) (Math.random() * 5);
		if (i == 0)
			return 'R';
		else if (i == 1)
			return 'G';
		else if (i == 2)
			return 'B';
		else if (i == 3)
			return 'W';
		else
			return 'S';			
	}

	public static boolean ignitionSwitch(boolean a)
	{
			return !a;
	}

	public static int moveHorizontally(int x, int d, boolean a)
	{
		//Check if car is on
		if (!a)
		{
			System.out.println("\nThe ignition is off!");
			return x;
		}
		//Check if result will be in bounds
		if (x + d < 1 || x + d > 20)
		{
			System.out.println("\nYou must stay in bounds!");
			return x;
		}
		else
			return x + d;
	}

	public static int moveVertically(int y, int d, boolean a)
	{
		//Check if car is on
		if (!a)
		{
			System.out.println("\nThe ignition is off!");
			return y;
		}
		//Check if result will be in bounds
		if (y + d < 1 || y + d > 20)
		{
			System.out.println("\nYou must stay in bounds!");
			return y;
		}
		else
			return y + d;
	}
	//Add currentCar as argument to access Car #
	public static void reportState(char c, boolean a, int x, int y, 
		int currentCar)
	{
		System.out.println("\nCar Information");
		//Add one to print current car not current index
		System.out.println("Car#: " + (currentCar + 1));

		switch (c)
		{
			case 'R': 
				System.out.println("Color: Red");
				break;
			case 'G':
				System.out.println("Color: Green");
				break;
			case 'B':
				System.out.println("Color: Black");
				break;
			case 'W':
				System.out.println("Color: White");
				break;
			case 'S':
				System.out.println("Color: Silver");
				break;
		}

		if (a)
			System.out.println("Ignition: On");
		else
			System.out.println("Ignition: Off");

		System.out.printf("Location: (%d, %d)\n\n", x, y);
		//Display grid, if j = x and i = y display car
		for (int i = 1; i <= 20; i++)
		{
			for (int j = 1; j <= 20; j++)
			{
				if (j == x && i == y)
				{
					System.out.print(c);
				}
				else
					System.out.print("-");
			}
			System.out.println();
		}
	}
}