public class Car 
{
	//Define data fields
	boolean ignition;
	private char color;
	private int xCoordinate;
	private int yCoordinate;
	//Create car constructor
	Car() 
	{	
		xCoordinate = randomizePosition();
		yCoordinate = randomizePosition();
		color = assignColor();
	}
	//Pick random num from 1-20
	public int randomizePosition()
	{
		return (int) (Math.random() * 20) + 1;
	}
	//Pick random num from 0-4 and choose color based on result
	public char assignColor()
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

	public boolean ignitionSwitch()
	{
			return !ignition;
	}

	public void moveHorizontally(int d)
	{
		//Check if car is on
		if (!ignition)
		{
			System.out.println("\nThe ignition is off!");
			return;
		}
		setX(xCoordinate + d);
	}

	public void moveVertically(int d)
	{
		//Check if car is on
		if (!ignition)
		{
			System.out.println("\nThe ignition is off!");
			return;
		}
		setY(yCoordinate + d);
	}

	public String getColor()
	{
		switch (color)
		{
			case 'R': 
				return "Red";
			case 'G':
				return "Green";
			case 'B':
				return "Blue";
			case 'W':
				return "White";
			case 'S':
				return "Silver";
		}
		return "Invalid Color";
	}

	public boolean getIgnition()
	{
		return ignition;
	}

	public int getX()
	{
		return xCoordinate;
	}

	public int getY()
	{
		return yCoordinate;
	}

	public void setX(int x)
	{
		//Check if result will be in bounds
		if (x < 1 || x > 20)
		{
			System.out.println("\nYou must stay in bounds!");
			return;
		}
		xCoordinate = x;
	}

	public void setY(int y)
	{
		//Check if result will be in bounds
		if (y < 1 || y > 20)
		{
			System.out.println("\nYou must stay in bounds!");
			return;
		}
		yCoordinate = y;
	}

	//Overide Object class's toString method
	@Override
	public String toString()
	{
		//Get ignition status as string
		String ignitionString;
		if (ignition)
			ignitionString = "On";
		else
			ignitionString = "Off";
        //Build grid as string
		String grid = "\n";
		for (int i = 1; i <= 20; i++)
		{
			for (int j = 1; j <= 20; j++)
			{
				if (j == xCoordinate && i == yCoordinate)
				{
					grid += color;
				}
				else
					grid +="-";
			}
			grid += "\n";
		}
		return String.format("Color: %s\nIgnition: %s\nLocation: (%d, %d)\n" +
			grid, getColor(), ignitionString, xCoordinate, yCoordinate);
	}
}