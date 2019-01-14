import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class Test1
{
	public static void main(String[] args)
	{
		Scanner input = new Scanner(System.in);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("life7.dat", "UTF-8");
		}
		catch (FileNotFoundException e) {
		   System.out.print("file does not exist");
		   System.exit(0);
		}
		catch (UnsupportedEncodingException e) {
		   System.out.print("file does not exist");
		   System.exit(0);
		}

		for (int i = 0; i < 25; i++)
		{
			for (int j = 0; j < 75; j++)
			{
				if (Math.random() > .6)
					writer.print('X');
				else 
					writer.print('.');
			}
			writer.println();
		}
		writer.close();
	}
}