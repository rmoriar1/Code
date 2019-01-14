import java.util.Scanner;

public class TimeDifference
{
	public static void main(String[] args)
	{
		final int SECINMINUTE = 60;
		final int SECINHOUR = 3600;

		// Create Scanner object
		Scanner input = new Scanner(System.in);

		// Prompt user for first time
		System.out.print("Enter first time: ");
		int firstTime = input.nextInt();

		// Prompt user for second time
		System.out.print("Enter second time: ");
		int secondTime = input.nextInt();

		// Converts the times into seconds
		int secondsInFirstTime = firstTime % 100 + firstTime / 100 % 100 * 
		SECINMINUTE + firstTime / 10000 * SECINHOUR;
		int secondsInSecondTime = secondTime % 100 + secondTime / 100 % 100 *  
		SECINMINUTE + secondTime / 10000 * SECINHOUR;

		// Calculate difference between the times in seconds
		int differenceInSeconds = secondsInFirstTime - secondsInSecondTime;

		// Convert back to HHMMSS
		int differenceInHHMMSS = differenceInSeconds / SECINHOUR * 10000 +
		differenceInSeconds % 3600 / SECINMINUTE * 100 + 
		differenceInSeconds % SECINMINUTE;

		// Print result
		System.out.println("Time difference: " + differenceInHHMMSS);
	}
}