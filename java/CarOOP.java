import java.util.Scanner;

public class CarOOP
{
	public static void main(String[] args)
	{
		//Initialize array of car objects
		Car[] cars = new Car[10];
		//Instantiate each car object by iterating over array
		for (int i = 0; i < cars.length; i++)
		{
			cars[i] = new Car();
		} 
		Scanner input = new Scanner(System.in);
		//Ask user which car
		System.out.println("\nWhich car would you like to use? " + 
						   "(choose from 1-10)");
		System.out.print("\nInput: ");
		//Decrement by one because arrays start from 0, use this as index
		int currentCar = input.nextInt() - 1;
		//Print intial car information
		System.out.printf("\nCar #%d Information\n",  currentCar + 1);
		System.out.println(cars[currentCar]);
		//Provide options menu
		System.out.println("\nWhat would you like to do?\n");
		System.out.println("1: Turn the igntion on/off");
		System.out.println("2: Change the position of the car");
		System.out.println("3: Change cars");
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
				cars[currentCar].ignition = cars[currentCar].ignitionSwitch();
				System.out.printf("\nCar #%d Information\n",  currentCar + 1);
				System.out.println(cars[currentCar]);;
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
					cars[currentCar].moveHorizontally(d);
					System.out.printf("\nCar #%d Information\n",  currentCar + 1);
					System.out.println(cars[currentCar]);
				}
				else if (direction.equals("V"))
				{
					/*Ask for distance and run moveVertically method with 
					result as parameter*/
					System.out.print("\nEnter a movement distance: ");
					int d = input.nextInt();
					cars[currentCar].moveVertically(d);
					System.out.printf("\nCar #%d Information\n",  currentCar + 1);
					System.out.println(cars[currentCar]);
				}
				else
				{
					//Neither H or V was entered
					System.out.println("\nInvalid input");
				}
			}
			else if(userInput.equals("3"))
			{
				System.out.println("\nWhich car would you like to use? " + 
						   "(choose from 1-10)");
				System.out.print("\nInput: ");
				currentCar = input.nextInt() - 1;
				System.out.printf("\nCar #%d Information\n",  currentCar + 1);
				System.out.println(cars[currentCar]);
			}
			//Neither 1 2 3 or Q was entered
			else
				System.out.println("\nInvalid input");
			//Repeat until user quits
			System.out.println("\nWhat would you like to do?\n");
			System.out.println("1: Turn the igntion on/off");
			System.out.println("2: Change the position of the car");
			System.out.println("3: Change cars");            
			System.out.println("Q: Quit this program\n");
			System.out.print("Input: ");
			userInput = input.next();
		}
	}
}