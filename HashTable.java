

package hw8;

import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.StringCharacterIterator;

@SuppressWarnings("unchecked")
public class HashTable implements IHashTable {
	private int nelems; // Number of element stored in the hash table
	private int expand; // Number of times that the table has been expanded
	private int collision; // Number of collisions since last expansion

	// FilePath for the file to write statistics upon every rehash
	private String statsFileName;

	// Boolean to decide whether to write statistics to file or not after
	// rehashing
	private boolean printStats = false;

	// Array of linkedlists to store contents of the hash table
	private LinkedList<String>[] contents;

	/**
	 * Constructor for hash table
	 * 
	 * @param size
	 *            size of the hash table
	 */
	public HashTable(int size) {
		contents = new LinkedList[size];
		nelems = 0;
		expand = 0;
		collision = 0;
		printStats = false;
	}

	/**
	 * Constructor for hash table
	 * 
	 * @param size
	 *            size of the hash table
	 * @param fileName
	 *            path to write statistics
	 */
	public HashTable(int size, String fileName) {
		contents = new LinkedList[size];
		nelems = 0;
		expand = 0;
		collision = 0;
		printStats = true;
		statsFileName = fileName;
	}

	/**
	 * Insert the string value into the hash table
	 * 
	 * @param value
	 *            value to insert
	 * @throws NullPointerException
	 *             if value is null
	 * @return true if the value was inserted, false if the value was already
	 *         present
	 */
	@Override
	public boolean insert(String value) {
		if (value == null)
			throw new NullPointerException();
		if (lookup(value)) // checks if value is present
			return false;

		int h = hash(value);
		if (contents[h] == null)
			contents[h] = new LinkedList<>();
		contents[h].add(value); // adds element at hash index
		if (contents[h].size() > 1)
			collision++;

		nelems++;
		rehash(h); // checks load factor and rehashes accordingly
		return true;
	}

	/**
	 * Delete the given value from the hash table
	 * 
	 * @param value
	 *            value to delete
	 * @throws NullPointerException
	 *             if value is null
	 * @return true if the value was deleted, false if the value was not found
	 */
	@Override
	public boolean delete(String value) {
		if (value == null)
			throw new NullPointerException();
		if (!lookup(value))
			return false;
		nelems--;
		return contents[hash(value)].remove(value);
	}

	/**
	 * Check if the given value is present in the hash table
	 * 
	 * @param value
	 *            value to look up
	 * @throws NullPointerException
	 *             if value is null
	 * @return true if the value was found, false if the value was not found
	 */
	@Override
	public boolean lookup(String value) {
		if (value == null)
			throw new NullPointerException();
		int hash = hash(value);
		if (contents[hash] == null) // check if list at hash even exists
			return false;
		return contents[hash(value)].contains(value);
	}

	/**
	 * Print the contents of the hash table. Print nothing if table is empty
	 */
	@Override
	public void printTable() {
		int i = 0;
		for (LinkedList<String> l : contents) {
			System.out.print(i + ": ");
			for (String s : l) // access each element in each list
				System.out.print(s + ", ");
			System.out.print("\n");
			i++;
		}
	}

	/**
	 * Return the number of elements currently stored in the hashtable
	 * 
	 * @return nelems
	 */
	@Override
	public int getSize() {
		return nelems;
	}

	/**
	 * uses the crc method to hash a string into an integer and modulate it to
	 * the length of the current containing array
	 * 
	 * @param s
	 *            the object that needs to be hashed
	 * @return the index in the array that this element s belongs in
	 */
	private int hash(String s) {
		int h = 0;
		StringCharacterIterator strItr = new StringCharacterIterator(s);
		while (true) { // loops through the characters in the string
			char n = strItr.next();
			if (n == '\uFFFF')
				break; // ends loop when DONE character is reached

			int highorder = h & 0xf8000000; // extracts 5 bits from the hash
											// value
			h = h << 5; // left shift 5
			h = h ^ (highorder >> 27); // XOR into h, the highorder 5 bits lower
			h = h ^ n; // XOR the hash value and the character value
		}
		return Math.abs(h % contents.length);
	}

	/**
	 * should the load factor of this hashtable exceed 2/3, this funciton will
	 * create a new container wiht double the length of the original one and add
	 * all of the elements from the current table to the new one
	 * 
	 * @param h
	 *            the index of the list more recently contributed to
	 */
	private void rehash(int h) {
		int s = contents[h].size();
		double lf = (double) s / contents.length; // calculates the load factor
		double max = 2.0 / 3.0;
		if (lf <= max) // checks if the load factor is > 2/3
			return;

		expand++; // rehashing will occur, container will be "expanded"
		printStatistics(lf, s); // notes statistics in file
		collision = 0; // resets collision count

		// new [r]e[h]ashed [t]able
		HashTable rht = new HashTable(contents.length * 2);

		for (LinkedList<String> l : contents) {
			if (l != null)
				for (String str : l) // copies all the elements from original
					rht.insert(str); // to the new
		}

		this.contents = rht.contents; // copies contents of temporary rht
	}

	/**
	 * prints the statistics each time the table is rehashed to file
	 * statsFileName if it is meant to
	 * 
	 * @param lf
	 *            the load factor
	 * @param s
	 *            the size of the longest chain
	 */
	private void printStatistics(double lf, int s) {
		if (!printStats)
			return; // no file
		lf = Math.floor(lf * 100) / 100; // stores load factor as decimal

		// composes data for printing,
		String data = "";
		data += (expand + " resizes, ");
		data += ("load factor " + lf + ", ");
		data += (collision + " collisions" + ", ");
		data += (s + " longest chain");

		// prints data to file
		printToFile(statsFileName, data + "\n");
	}

	/**
	 * prints information to file
	 * 
	 * @param path
	 *            where the file to be printed to is
	 * @param data
	 *            the data to print to the file
	 */
	private void printToFile(String path, String data) {
		FileWriter fileWritter;
		try {
			fileWritter = new FileWriter(path, true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(data);
			bufferWritter.close();
		} catch (IOException e) {
			System.err.println("Please use valid file path");
		}
	}
}