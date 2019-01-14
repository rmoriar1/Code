public class Checkout 
{
	DesertItem [] items;
	int numberOfItems;
	int totalCost;
	int totalTax;
	public Checkout()
	{
		items = new DesertItem[100];
	}

	public void clear()
	{
		DesertItem [] clear = new DesertItem[100];
		items = clear;
		numberOfItems = totalCost = totalCost = 0;
	}

	public void enterItem(DesertItem item)
	{
		items[numberOfItems] = item;
		numberOfItems++;
		totalCost += item.getCost()
		totalTax += item.getCost * TAX_RATE / 100;
	}

	public int numberOfItems()
	{}

	@Override
	public String toString()
	{
		StringBuilder reciept;
		reciept = "       STORE_NAME\n        -----------------------\n\n";
		for (int i = 0; i < numberOfItems; i++)
		{
			reciept += items[i].getName();
			reciept += "     " + items[i].getCost();
		}
		reciept += "\n Tax " + totalTax;
		reciept += "\n Total Cost " + totalCost;
		System.out.println(recipt.toString);
	}

	public int totalCost()
	{
		return totalCost;
	}

	public int totalTax()
	{
		return totalTax;
	}

}