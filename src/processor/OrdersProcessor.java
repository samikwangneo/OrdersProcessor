package processor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


public class OrdersProcessor {	
	private static Map<String, Integer> totalItems;
	private static NumberFormat currencyFormat;
	private static Map<Integer, Set<OrderItem>> orders;

	public OrdersProcessor() {
		OrdersProcessor.currencyFormat = NumberFormat.getCurrencyInstance();
        totalItems = new TreeMap<>();
        orders = new TreeMap<>();
	}
	
	public static void main(String[] args) {
		OrdersProcessor op = new OrdersProcessor();
		
		Scanner scanner = new Scanner(System.in);
		
	    System.out.println("Enter item's data file name:");
		String itemsDataFile = scanner.nextLine();
		
		System.out.println("Enter 'y' for multiple threads, any other character otherwise:");
		boolean useMultiple = scanner.nextLine().equals("y");
		
		System.out.println("Enter number of orders to process:");
		int numOrders = scanner.nextInt();
		scanner.nextLine();
		
		System.out.println("Enter order's base filename:");
		String orderBaseName = scanner.nextLine();
		
		System.out.println("Enter result filename:");
		String resultName = scanner.nextLine();
		
		long startTime = System.currentTimeMillis();
		
		if (useMultiple) {
			processOrdersMultiple(itemsDataFile, numOrders, orderBaseName, resultName);
		}
		else {
			processOrdersSingle(itemsDataFile, numOrders, orderBaseName, resultName);
		}
		
		long endTime = System.currentTimeMillis();
        System.out.println("Processing time (msec): " + (endTime - startTime));
        System.out.println("Results can be found in the file: " + resultName);
        
        scanner.close();

			
	}
	
	private static void processOrdersSingle(String itemsDataFile, int numOrders, String orderBaseName, String resultName) {
		try {
			Map<String, Double> itemPrices = readItemPrices(itemsDataFile);

			FileWriter fw = new FileWriter(resultName);

			for (int i = 1; i <= numOrders; i++) {
				String orderName = orderBaseName + i + ".txt";
				BufferedReader br = new BufferedReader(new FileReader(orderName));
				processOrder(br, orderName, fw, itemsDataFile, itemPrices);
				br.close();
				
			}
			writeOrderSummary(fw, itemPrices);

			writeTotalSummary(fw, itemPrices);
			fw.close();

		}
		
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	private static void processOrdersMultiple(String itemsDataFile, int numOrders, String orderBaseName, String resultName) {
		try {
			Map<String, Double> itemPrices = readItemPrices(itemsDataFile);
			FileWriter fw = new FileWriter(resultName);
			
			ArrayList<Thread> threads = new ArrayList<>();
	        for(int i = 1; i <= numOrders; i++) {
	        	String orderName = orderBaseName + i + ".txt";
				BufferedReader br = new BufferedReader(new FileReader(orderName));
	        	Thread thread = new Thread(() -> processOrder(br, orderName, fw, itemsDataFile, itemPrices));
	        	threads.add(thread);
	        	thread.start();
	        }
	        
	        for (Thread thread : threads) {
	        	try {
	        		thread.join();
	        	}
	        	catch (InterruptedException e) {
	        		System.err.println(e.getMessage());
	        	}
	        }
			writeOrderSummary(fw, itemPrices);

		    writeTotalSummary(fw, itemPrices);
	
	        fw.close();

		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
    }
	
	private static void processOrder(BufferedReader brOrder, String orderName, FileWriter fw, String itemsDataFile, Map<String, Double> itemPrices) {
		try {
			
			Set<OrderItem> orderItems = new TreeSet<>();
			int clientId = 0;
			ArrayList<String> items = new ArrayList<>();
			
			String line = brOrder.readLine();
			clientId = Integer.parseInt(line.substring(line.indexOf(":") + 2, line.length()));
			
			Map<String, Integer> soldItems = new TreeMap<>();
			
			while ((line = brOrder.readLine()) != null) {
				String itemName = line.substring(0, line.indexOf(" "));
				items.add(itemName);
				
			}
				
			Collections.sort(items);
			Map<String, Integer> itemCount = new HashMap<>();
			
			for (String s : items) {
				itemCount.put(s, itemCount.getOrDefault(s, 0) + 1);
			}	
		
			for (Map.Entry<String, Integer> e : itemCount.entrySet()) {
				String item = e.getKey();
				int quantity = e.getValue();
				double itemPrice = itemPrices.get(item);
				double totalCost = itemPrice*quantity;
				orderItems.add(new OrderItem(item, quantity, itemPrice, totalCost));
			}
				
			brOrder.close();
			
			synchronized(orders) {
				orders.put(clientId, orderItems);
			}

		}
		
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
	
	}
	
	private static Map<String, Double> readItemPrices(String itemsDataFile) {
		Map<String, Double> itemPrices = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(itemsDataFile));
			String line;
			while ((line = br.readLine()) != null) {
				String itemsDataFileName = line.substring(0, line.indexOf(" "));
				String itemPriceString = line.substring(line.indexOf(" ") + 1);
				double itemPrice = Double.parseDouble(itemPriceString);
				itemPrices.put(itemsDataFileName, itemPrice);
			}
		}
		
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		return itemPrices;
	}
	
	private static void writeOrderSummary(FileWriter fw, Map<String, Double> itemPrices) throws IOException {
		for (Map.Entry<Integer, Set<OrderItem>> e : orders.entrySet()) {
			int clientId = e.getKey();
			fw.write("----- Order details for client with Id: " + clientId + " -----\n");
			double orderTotal = 0;
			for (OrderItem oi : e.getValue()) {
				String itemName = oi.getItemName();
				int quantity = oi.getQuantity();
				double itemPrice = oi.getItemPrice();
				double totalCost = oi.getTotalCost();
				orderTotal += totalCost;
				fw.write("Item's name: " + itemName + ", Cost per item: " + 
						currencyFormat.format(itemPrice) + ", Quantity: " + quantity + 
							", Cost: " + currencyFormat.format(totalCost) + "\n");
			}
			fw.write("Order Total: " + currencyFormat.format(orderTotal) + "\n");

		}
		
	}
	
	private static void writeTotalSummary(FileWriter fw, Map<String, Double> itemPrices) throws IOException {
	    fw.write("***** Summary of all orders *****\n");
	   
	    double completeTotal = 0.0;
	    Map<String, Integer> itemQuantities = new TreeMap<>();
	    Map<String, Double> itemTotalPrices = new TreeMap<>();

	    for (Set<OrderItem> orderItems : orders.values()) {
	        for (OrderItem item : orderItems) {
	        	String itemName = item.getItemName();
	        	int quantity = item.getQuantity();
	        	double totalItemCost = item.getTotalCost();
	        	
	        	itemQuantities.put(itemName, itemQuantities.getOrDefault(itemName, 0) 
	        			+ quantity);
	        	itemTotalPrices.put(itemName, itemTotalPrices.getOrDefault(itemName, 0.0) 
	        			+ totalItemCost);
	        	completeTotal += totalItemCost;
	        	
	        }
	   
	    }
	    for (Map.Entry<String, Integer> entry : itemQuantities.entrySet()) {
	        String itemName = entry.getKey();
	        int quantity = entry.getValue();
	        double itemTotal = itemTotalPrices.get(itemName);
	        
	        fw.write("Summary - Item's name: " + itemName + ", Cost per item: " + 
	        		currencyFormat.format(itemTotal / quantity) + ", Number sold: " + 
	        			quantity + ", Item's Total: " +
	        				currencyFormat.format(itemTotal) + "\n");
	    }
	    
	    fw.write("Summary Grand Total: " + currencyFormat.format(completeTotal) + "\n");
	}

}













