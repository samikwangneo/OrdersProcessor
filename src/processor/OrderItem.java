package processor;

public class OrderItem implements Comparable {
	private String itemName;
	private int quantity;
	private double itemPrice, totalCost;
	
	public OrderItem(String itemName, int quantity, double itemPrice, double totalCost) {
		this.itemName = itemName;
		this.quantity = quantity;
		this.itemPrice = itemPrice;
		this.totalCost = totalCost;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public double getItemPrice() {
		return itemPrice;
	}
	
	public double getTotalCost() {
		return totalCost;
	}

	@Override
	public int compareTo(Object o) {
		OrderItem o1 = (OrderItem) o;
		return itemName.compareTo(o1.getItemName());
	}
	
	
}
