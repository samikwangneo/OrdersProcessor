package tools;
import java.util.*;
import java.io.*;

/**
 * Generates text files with random articles.  The articles
 * are defined in the articlesDatabase map.  Adjust 
 * NUMBER_OF_ORDERS, MAX_ITEMS_PER_ORDER, BASE_FILE_NAME for
 * different data sets.
 * @author cmsc132
 *
 * Setting for example* files:
 * 	private static int NUMBER_OF_ORDERS = 3;
	private static int MIN_ITEMS_PER_ORDER = 4;
	private static int DELTA_FOR_ITEMS_PER_ORDER = 5;
 * Settings for secret tests files:
 *  BASE_FILE_NAME "DataSetOne", NUMBER_OF_ORDERS 25	
 *  BASE_FILE_NAME "DataSetTwo", NUMBER_OF_ORDERS 10
 *  BASE_FILE_NAME "DataSetThree", NUMBER_OF_ORDERS 100		
 */
public class DataFilesGenerator {
	private TreeMap<String, Double> itemsDatabase;

	/* Listing of settings for different data sets */
	
	/*
	private static String BASE_FILE_NAME = "DataSetOne";
	private static int NUMBER_OF_ORDERS = 25;
	private static int DELTA_FOR_ITEMS_PER_ORDER = 20;
    */
	
	/*
	private static String BASE_FILE_NAME = "DataSetTwo";
	private static int NUMBER_OF_ORDERS = 50;
	private static int DELTA_FOR_ITEMS_PER_ORDER = 40;
	*/
	
	/*
	private static String BASE_FILE_NAME = "DataSetThree";
	private static int NUMBER_OF_ORDERS = 100;
	private static int DELTA_FOR_ITEMS_PER_ORDER = 60;
	*/
	
	/*
	private static String BASE_FILE_NAME = "SecDataSetFour";
	private static int NUMBER_OF_ORDERS = 2;
	private static int DELTA_FOR_ITEMS_PER_ORDER = 100;
	
	private static int MIN_ITEMS_PER_ORDER = 2;
	private static long RANDOM_SEED_BASE = 1L;
	private static String ITEMS_DATABASE_NAME = "secItemsData.txt";
	*/

	/*
	private static String BASE_FILE_NAME = "SecDataSetFive";
	private static int NUMBER_OF_ORDERS = 15;
	private static int DELTA_FOR_ITEMS_PER_ORDER = 100;
	
	private static int MIN_ITEMS_PER_ORDER = 2;
	private static long RANDOM_SEED_BASE = 1L;
	private static String ITEMS_DATABASE_NAME = "secItemsData.txt";
	 */
	
	/* Values to change to create new data sets */
	private static String BASE_FILE_NAME = "Report";
	private static int NUMBER_OF_ORDERS = 15;
	private static int DELTA_FOR_ITEMS_PER_ORDER = 100;
	
	private static int MIN_ITEMS_PER_ORDER = 100000;
	private static long RANDOM_SEED_BASE = 1L;
	private static String ITEMS_DATABASE_NAME = "itemsData.txt";
	/* END Values to change to create new data sets */
	
	private static int MAX_NUMBER_OF_ORDERS = 500;
	public static void main(String[] args) throws IOException {
		if (NUMBER_OF_ORDERS > MAX_NUMBER_OF_ORDERS) {
			System.out.println("Number of orders exceeds MAX_TEXT_FILES");
			System.exit(1);
		}
		DataFilesGenerator generateDataFiles = new DataFilesGenerator();
		generateDataFiles.deleteTextFiles();
		generateDataFiles.loadDatabase();
		generateDataFiles.printDatabase();
		Random random = new Random(RANDOM_SEED_BASE);
		for (int orderId = 1; orderId <= NUMBER_OF_ORDERS; orderId++) {
			int extraItems = random.nextInt(DELTA_FOR_ITEMS_PER_ORDER + 1);
			int numberOfItems = MIN_ITEMS_PER_ORDER + extraItems;
			generateDataFiles.createTextFileRandomData(BASE_FILE_NAME + orderId + ".txt", orderId, 
													   numberOfItems, new Random(RANDOM_SEED_BASE + orderId));
		}
	}

	private void loadDatabase() throws IOException {
		itemsDatabase = new TreeMap<String, Double>();
		Scanner scanner = new Scanner(new File(ITEMS_DATABASE_NAME));
		
		while (scanner.hasNextLine()) {
			String itemName = scanner.next();
			double itemPrice = scanner.nextDouble();
			itemsDatabase.put(itemName, itemPrice);
		}
	}
	
	private void printDatabase() {
		System.out.println("Items Database");
		System.out.println(itemsDatabase);
	}
	
	private void createTextFileRandomData(String filename, int clientId, 
									      int numberOfItems, Random random) throws IOException {
		Object[] keysArray = itemsDatabase.keySet().toArray();
		
		boolean append = false;
		Writer fileWriter = new FileWriter(filename, append); 
		fileWriter.write("ClientId: " + (clientId + 1000));
		for (int i = 0; i < numberOfItems; i++) {
			int randomIndex = random.nextInt(keysArray.length);
			String articleName = (String)keysArray[randomIndex];
			fileWriter.write("\n" + articleName + " " + randomDate(random));
		}
		
		fileWriter.close();
		System.out.println(filename + " created.");
	}
	
	private String randomDate(Random random) {
		int month = random.nextInt(12) + 1;
		int day = random.nextInt(26) + 1; /* Adding one so we never get 0; 26 to avoid Feb problems */
		
		return month + "/" + day;
	}
	
	private void deleteTextFiles() {
		for (int orderId = 1; orderId <= MAX_NUMBER_OF_ORDERS; orderId++) {
			File file = new File(BASE_FILE_NAME + orderId + ".txt");
			file.delete();
		}
	}
}