import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.InterruptedException;

public class GameOfLife 
{
	final static int M = 25;
	final static int N = 75;

	public static void main(String[] args)
	{
		char[][] current = new char[M][N];
		char[][] next = new char[M][N];
		Scanner input = new Scanner(System.in);
		System.out.print("Enter a filename: ");
		String filename = input.next();
		File file = new File(filename);
		Scanner fileReader = null;

		try {
			fileReader = new Scanner(file);
		}
		catch(FileNotFoundException e)
		{
			System.out.println("\nFile " + file + " does not exist");
			System.exit(0);
		}

		for(int i = 0; i < M; i++)
		{
			String line = fileReader.nextLine();
			for(int j = 0; j < N; j++)
			{
				current[i][j] = line.charAt(j);
			}
		}
		
		int generation = 1;
		//As long as world is not empty prompt user
		while (!isEmpty(current))
		{
			
			System.out.println("\nGeneration " + generation + "\n");
			printArray(current);

			System.out.println("\nWhat would you like to do?\n");
			System.out.println("'G': Generate new generation");
			System.out.println("'P': Generate until terminal state");
			System.out.println("'T': Terminate the program\n");
			System.out.print("Input: ");
			String userInput = input.next();

			if (userInput.equals("G"))
			{
				for (int i = 0; i < M; i++)
				{
					for (int j = 0; j < N; j++)
					{
						int neighbors = numOfNeighbors(current, i, j);
						if (neighbors == 3)
							next[i][j] = 'X';
						else if (neighbors == 2)
							next[i][j] = current[i][j];
						else 
							next[i][j] = '.';
					}
				}
				if (noChanges(current, next))
				{
					System.out.println("\nStatic world state\nExiting...");
					System.exit(0);
				}
				generation++;
				//Copy cells from next into current
				for (int i = 0; i < M; i++)
				{
					for (int j = 0; j < N; j++)
					{
						current[i][j] = next[i][j];
					}
				}
			}

			if (userInput.equals("P"))
			{
				while (true)
				{
					try        
					{
    					Thread.sleep(100);
					} 
					catch(InterruptedException ex) 
					{
    					Thread.currentThread().interrupt();
					}
					for (int i = 0; i < M; i++)
					{
						for (int j = 0; j < N; j++)
						{
							int neighbors = numOfNeighbors(current, i, j);
							if (neighbors == 3)
								next[i][j] = 'X';
							else if (neighbors == 2)
								next[i][j] = current[i][j];
							else 
								next[i][j] = '.';
						}
					}
					if (noChanges(current, next) || isEmpty(current))
					{
						System.out.println("\nStatic world state\nExiting...");
						System.exit(0);
					}
					generation++;
					//Copy cells from next into current
					for (int i = 0; i < M; i++)
					{
						for (int j = 0; j < N; j++)
						{
							current[i][j] = next[i][j];
						}
					}
					System.out.println("\nGeneration " + generation + "\n");
					printArray(current);
				}
			}
			
			else if (userInput.equals("T"))
			{
				System.out.println("\nExiting...");
				System.exit(0);
			}
			else
				System.out.println("\nInvalid input");
		}
		System.out.println("\nWorld is empty\nExiting...");
		System.exit(0);
	}

	public static int numOfNeighbors(char[][] array, int row, int col)
	{
		int count = 0;
		//Nested for loop will increment count if ceneter tile has organism
		if (array[row][col] == 'X')
			count--;
		// Iterate over 9 cells counting neighbors
		for (int i = row - 1; i <= row + 1; i++)
		{
			for (int j = col - 1; j <= col + 1; j++)
			{
				if (i < 0 || i >= M || j < 0 || j >= N)
					continue;
				if (array[i][j] == 'X')
					count++;
			}
		}
		return count;
	}

	public static boolean isEmpty(char[][] array)
	{
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				if (array[i][j] == 'X')
				{
					return false;
				}
			}
		}
		return true;
	}

	public static boolean noChanges(char[][] array1, char[][] array2)
	{
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				if (array1[i][j] != array2[i][j])
					return false;
			}
		}
		return true;
	}

	public static void printArray(char[][] array)
	{
		for (int i = 0; i < M; i++)
		{
			for (int j = 0; j < N; j++)
			{
				System.out.print(array[i][j]);
			}
			System.out.println();
		}
	}
}