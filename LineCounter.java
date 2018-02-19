

package hw8;

import java.io.*;
import java.util.Scanner;

public class LineCounter {

	/**
	 * print the filename to console
	 * 
	 * @param filename
	 *            the file name to print out
	 */
	public static void printFileName(String filename) {
		System.out.println("\n" + filename + ":");
	}

	/**
	 * print the statistics to console
	 * 
	 * @param compareFileName
	 *            the file being compared to
	 * @param percentage
	 *            the percentage of similarity as integer
	 */
	public static void printStatistics(String compareFileName,
			int percentage) {
		System.out.println(percentage + "% of lines are also in "
				+ compareFileName);
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.err.println("Invalid number of arguments passed");
			return;
		}

		int numArgs = args.length;

		// Create a hash table for every file
		HashTable[] tableList = new HashTable[numArgs];

		// Stores length of each file
		int[] length = new int[numArgs];

		// Preprocessing: Read every file and create a HashTable
		for (int i = 0; i < numArgs; i++) {
			HashTable hashy = new HashTable(5); // keeps hashtable
			try (Scanner scanner = new Scanner(new File(args[i]))) {
				int x = 0; // counts number of lines
				while (scanner.hasNext()) {
					hashy.insert(scanner.nextLine()); // adds line to hashy
					x++;
				}
				length[i] = x;
			} catch (IOException e) {
				System.err.println("Please use valid file path");
			}
			tableList[i] = hashy; // stores hashtable for each file
		}

		// Find similarities across files
		for (int i = 0; i < numArgs; i++) {
			printFileName(args[i]);
			for (int j = 0; j < numArgs; j++) {
				if (args[i].equals(args[j]))
					continue; // same file
				int d = 0; // counts number of duplicate lines

				try (Scanner scanner = new Scanner(new File(args[i]))) {
					while (scanner.hasNext())
						if (tableList[j].lookup(scanner.nextLine()))
							d++;
				} catch (IOException e) {
					System.err.println("Please use valid file path");
				}

				int p = (d * 100) / length[i];
				printStatistics(args[j], (int) p);
			}
			System.out.println();
		}
	}

}
