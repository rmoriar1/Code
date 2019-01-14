import java.util.Scanner;

public class TestSmallInt {
	public static void main(String[] args)
	{
		Scanner input = new Scanner(System.in);
		System.out.print("Enter a number between 0 and " + 
			SmallInt.MAXVALUE +": ");
		SmallInt test = new SmallInt(input.nextInt());
		System.out.println(test.getDec());
		System.out.println(test.getBin());
		System.out.println(test.getHex());
	}
}