

package hw8;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.junit.Before;
import org.junit.Test;

public class HashTableTester {
	boolean runLocalTests = false;
	HashTable hashy;

	// sets up a hash table hashy for other tests to make use of
	@Before
	public void setUp() {
		if (runLocalTests)
			hashy = new HashTable(2, "notes");
		else // uses low, prime initial size to engage rehashing
			hashy = new HashTable(2);
	}

	// tests the insert method in multiple situations
	@Test
	public void testInsert() {
		hashy.insert("yes");
		assertTrue("Checks single", hashy.lookup("yes"));

		hashy.insert("no");
		hashy.insert("ok");
		hashy.insert("maybe");
		assertEquals("Checks multiple", 4, hashy.getSize());
		assertTrue("Checks multiple", hashy.lookup("yes"));
		assertTrue("Checks multiple", hashy.lookup("maybe"));
		assertTrue("Checks multiple", hashy.lookup("no"));

		try {
			hashy.insert(null);
			fail();
		} catch (NullPointerException e) {
			// pass
		}

		hashy.insert("yes");
		assertEquals("Checks duplicate add", 4, hashy.getSize());

		hashy.delete("ok");
		assertTrue("Checks after removal", hashy.lookup("no"));
	}

	// tests the lookup method in multiple situations
	@Test
	public void testLookup() {
		hashy.insert("yes");
		assertTrue("Checks single", hashy.lookup("yes"));
		assertFalse("Checks single", hashy.lookup("no"));

		hashy.insert("no");
		hashy.insert("ok");
		hashy.insert("maybe");
		assertFalse("Checks multiple", hashy.lookup("oops"));
		assertTrue("Checks multiple", hashy.lookup("ok"));

		try {
			hashy.lookup(null);
			fail();
		} catch (NullPointerException e) {
			// pass
		}

		hashy.delete("maybe");
		assertFalse("Checks after removal", hashy.lookup("maybe"));
	}

	// tests the delete method in multiple situations
	@Test
	public void testDelete() {
		hashy.insert("yes");
		hashy.delete("yes");
		assertFalse("Checks single", hashy.lookup("yes"));

		try {
			hashy.delete(null);
			fail();
		} catch (NullPointerException e) {
			// pass
		}

		hashy.insert("no");
		hashy.insert("ok");
		hashy.insert("maybe");
		hashy.delete("maybe");
		hashy.delete("no");
		assertTrue("Checks control", hashy.lookup("ok"));
		assertFalse("Checks multiple", hashy.lookup("no"));
	}

	// tests the size method in multiple situations
	@Test
	public void testSize() {

		hashy.insert("yes");
		assertEquals("Checks single", 1, hashy.getSize());

		hashy.insert("no");
		hashy.insert("ok");
		hashy.insert("maybe");
		assertEquals("Checks multiple", 4, hashy.getSize());

		hashy.delete("maybe");
		assertEquals("Checks after removal", 3, hashy.getSize());
	}

	// loads the dictionary into a hash table so user can check printed
	// statistics and runtime
	@Test
	public void testDictionary() {
		if (!runLocalTests)
			return;
		try (Scanner scanner = new Scanner(new File("dictionary"))) {
			while (scanner.hasNext())
				hashy.insert(scanner.nextLine());
		} catch (IOException e) {
			System.err.println("Please use valid file path");
		}
	}
}
