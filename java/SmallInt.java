public class SmallInt {
	private int value;
	public static final int MAXVALUE = 1000;

	public SmallInt()
	{
	}

	public SmallInt(int i)
	{
		isValid(i);
	}

	public String getDec()
	{
		return value + "";
	}

	public void setDec(int i)
	{
		isValid(i);
	}

	public String getBin()
	{
		String binString = "";
		//Set i = to value as to not alter value
		int i = value;
		if (i == 0)
			return "0";
		/* 1. If value > 0: Take value and % buy 2 (remainder), prepend this to 
		      String
		   2. Set value = value / 2 (quotient)
		   3. Repeat
		   Ex. 115: 115 % 2 = 1 String = "1", 115 / 2 = 57, 
		            57 % 2 = 1, String = "11", 57 / 2 = 28, 
		            28 % 2 = 0, String = "011", 28 / 2 = 14,
		            14 % 2 = 0, String = "0011", 14 / 2 = 7,
		            7 % 2 = 1, String = "10011", 7 / 2 = 3,
		            3 % 2 = 1, String = "110011", 3 / 2 = 1,
		            1 % 2 = 1, String = "1110011", 1 / 2 = 0,
		            value !> 0 */
		while(i> 0)
		{
			binString = (i % 2) + binString;
			i /= 2;
		}
		return binString;
	}

	public String getHex()
	{
		String hexString = "";
		int i = value;
		if (i == 0)
			return "0";
		/* 1. If value > 0: Take value and % buy 16 (remainder), if > 10 
			  type cast to Char, prepend this to StringBuilder
		   2. Set value = value / 16 (quotient)
		   3. Repeat
		   Ex. 115: 115 % 16 = 3 String = "3", 115 / 16 = 7, 
		            7 % 16 = 7, String = "73", 7 / 16 = 0,
		            value !> 0 */
		while (i > 0)
		{
			int remainder = i % 16;
			if (remainder > 9)
				hexString = (char) ('A' + remainder - 10) + hexString;
			else	
				hexString = remainder + hexString;
			i /= 16;
		}
		return hexString;
	}

	public static String binAsHex(String a)
	{
		String binToHex = "";
		int sumOfByte = 0;
		//Iterate over string pulling out the hex value of each byte
		for (int i = 0; i < a.length(); i++)
		{
			int currentPosition = a.length() - i;
			/*If char = '1', find out position of bit using currentPosition 
			- 1 % 4, raise 2 to this power and add to sumOfByte*/
			if (a.charAt(i) == '1')
				sumOfByte += Math.pow(2, (currentPosition - 1) % 4);
			/*If currentPosition % 4 = 1 you've reached the end of the byte,
			convert sumOfByte to Hex value and add to end of string,
			reset sumOfByte to 0*/
			if (currentPosition % 4 == 1)
			{
				if (sumOfByte > 9)
					binToHex = binToHex + (char) ('A' + sumOfByte - 10);
				else	
					binToHex = binToHex + sumOfByte;
				sumOfByte = 0;
			}
		}
		return binToHex;
	}

	public static boolean sameValue(String bin, String hex)
	{
		return binAsHex(bin).equals(hex);
	}

	public void isValid(int i)
	{
		//Check if input is between 1 and 100
		if (i < 0 || i > MAXVALUE)
		{
			System.out.println("SmallInt must be between 0 and 1000");
			value = 0;
		}
		else
			value = i;
	}
}