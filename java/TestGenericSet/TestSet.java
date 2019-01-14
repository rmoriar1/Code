import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

public class TestSet {

	private Set<Integer> ints;
	private Set<String> strings;

	@Before
	public void initSet() {
		ints = new Set<Integer>();
		strings = new Set<String>();
	}

	@Test
	public void isEmptyReturnsTrueIfEmpty() {
		assertTrue("If set is empty, return true", ints.isEmpty());
		assertTrue("If set is empty, return true", strings.isEmpty());
	}

	@Test
	public void setHasOneElement() {
		ints.add(5);
		assertFalse("If set has one or more elements, return false", ints.isEmpty());
		assertTrue("Set should have size == 1", ints.size() == 1);

		strings.add("hello");
		assertFalse("If set has one or more elements, return false", strings.isEmpty());
		assertTrue("Set should have size == 1", strings.size() == 1);
	}

	@Test
	public void setStillHasOneElementAfterAddingDuplicate() {
		ints.add(5); ints.add(5);
		assertFalse("If set has one or more elements, return false", ints.isEmpty());
		assertTrue("Set should have size == 1", ints.size() == 1);

		strings.add("hello"); strings.add("hello");
		assertFalse("If set has one or more elements, return false", strings.isEmpty());
		assertTrue("Set should have size == 1", strings.size() == 1);
	}

	@Test
	public void setHasTwoElements() {
		ints.add(5); ints.add(42);
		assertFalse("If set has one or more elements, return false", ints.isEmpty());
		assertTrue("Set should have size == 2", ints.size() == 2);

		strings.add("hello"); strings.add("world");
		assertFalse("If set has one or more elements, return false", strings.isEmpty());
		assertTrue("Set should have size == 2", strings.size() == 2);
	}

	@Test
	public void setIsEmptyAfterRemoveOneElement() {
		ints.add(5); ints.remove(5);
		assertTrue("If set is empty, return true", ints.isEmpty());

		strings.add("hello"); strings.remove("hello");
		assertTrue("If set is empty, return true", strings.isEmpty());

	}

	@Test
	public void removeReturnsFoundValue() {
		ints.add(5);
		Integer intValue = ints.remove(5);
		assertEquals("Integer removed == 5", new Integer(5), intValue);

		strings.add("hello");
		String stringValue = strings.remove("hello");
		assertEquals("String removed == \"hello\"", "hello", stringValue);
	}

	@Test
	public void removeReturnsNullIfEmpty() {
		assertEquals("Remove returns null if set is empty", null, ints.remove(5));
		assertEquals("Remove returns null if set is empty", null, strings.remove("hello"));
	}

	@Test
	public void removeReturnsNullIfElementNotInSet() {
		ints.add(4); ints.add(6);
		assertEquals("Remove returns null is set is empty", null, ints.remove(5));

		strings.add("hello");
		assertEquals("Remove returns null is set is empty", null, strings.remove("world"));
	}

	@Test
	public void containsReturnsTrueIfElementPresent() {
		strings.add("hello"); strings.add("world"); strings.add("dinosaur");
		assertTrue("Set contains \"world\"", strings.contains("world"));

		ints.add(1); ints.add(2); ints.add(3);
		assertTrue("Set contains 2", ints.contains(2));

	}

	@Test
	public void containsReturnsFalseIfElementNotPresent() {
		strings.add("hello");
		assertFalse("Set does not contain \"world\"", strings.contains("world"));

		ints.add(1);
		assertFalse("Set does not contain 2", ints.contains(2));

	}

	@Test
	public void testSize() {
		ints.add(4); ints.add(5); ints.add(6);
		assertEquals("Size of set should be three (3)", 3, ints.size());

		strings.add("hi"); strings.add("hey"); strings.add("howdy");
		assertEquals("Size of set should be three (3)", 3, ints.size());
	}

	@Test
	public void toStringFormattedProperly() {
		ints.add(4); ints.add(5); ints.add(6);
		assertEquals("Formatted toString(): ", "4, 5, 6", ints.toString());

		strings.add("hello"); strings.add("world"); strings.add("dinosaur");
		assertEquals("Formatted toString(): ", "hello, world, dinosaur", strings.toString());
	}

	@Test
	public void emptySetToStringFormattedProperly() {
		assertEquals("Formatted toString(): ", "Set is empty!", ints.toString());
		assertEquals("Formatted toString(): ", "Set is empty!", strings.toString());
	}

	@Test
	public void testRemoveDuplicateUnsortedIntegersInSetConstructor() {
		Integer[] arr = {1,4,2,5,3,3,2,4,1,2};
		ints = new Set<Integer>(arr);
		//constructor calls private method removeDuplicates(T[] elems)

		assertEquals("Size of set should be five (5)", 5, ints.size());
		assertEquals("Formatted toString(): ", "1, 2, 3, 4, 5", ints.toString());
	}

	@Test
	public void testRemoveDuplicateUnsortedStringsInSetConstructor() {
		String[] arr = {"a", "b", "a", "b", "c", "b", "c", "d", "a"};
		strings = new Set<String>(arr);
		//constructor calls private method removeDuplicates(T[] elems)

		assertEquals("Size of set should be four (4)", 4, strings.size());
		assertEquals("Formatted toString(): ", "a, b, c, d", strings.toString());
	}

	@Test
	public void testRemoveDuplicatesEmptyArrayInSetConstructor() {
		String[] arr = {};
		strings = new Set<String>(arr);
		//constructor calls private method removeDuplicates(T[] elems)

		assertEquals("Formatted toString(): ", "Set is empty!", strings.toString());
	}
}