public class TestAnimal {
	public static void main(String[] args) {

		Animal[] animals = new Animal[3];
		animals[0] = new Dog();
		animals[1] = new Cat();
		animals[2] = new Bird();

		for(int i = 0; i < animals.length; i++) {
			
			if(animals[i] instanceof Bird) {
				Bird bird = (Bird)animals[i];
				bird.flyAway();
			}
			else
				animals[i].makeNoise();
			System.out.println(animals[i].getName());
		}
	}
}

abstract class Animal {
	private int age;
	public String name;
	private String color;

	public Animal(int age, String name, String color) {
		this.age = age;
		this.name = name;
		this.color = color;
	}

	public Animal() {
		this(0, "Bob", "White");
	}

	public int getAge() {
		return age;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public void setAge(int age) {
		this.age = (age > 0) ? age : 0;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setColor(String color) {
		this.color = color;
	}


	public abstract void makeNoise();

}

class Dog extends Animal {

	private boolean hasFleas;

	public Dog(int age, String name, String color, boolean hasFleas) {
		super(age, name, color);
		this.hasFleas = hasFleas;
	}

	public Dog(){
		//implicitly calls super();
	}

	public String getName() {
		return super.name;
	}

	public void makeNoise() {
		System.out.println("Woof");
	}

}

class Cat extends Animal {

	private boolean isAJerk = true;

	public Cat(int age, String name, String color, boolean isAJerk) {
		super(age, name, color);
		this.isAJerk = isAJerk;
	}

	public Cat(){
		//implicitly calls super();
	}

	public void makeNoise() {
		System.out.println("Meow");
	}
}

class Bird extends Animal {

	private boolean canFly;

	public Bird() {
		//implicitly calls super();
	}

	public Bird(int age, String name, String color, boolean canFly) {
		super(age, name, color);
		this.canFly = canFly;
	}

	public void flyAway() {
		System.out.println("flap flap");
	}

	public void makeNoise() {
		System.out.println("Squak");
	}
}