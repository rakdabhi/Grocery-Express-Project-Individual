package edu.gatech.cs6310.classes;

import java.util.Map;
import java.util.TreeMap;

public class Order {
    private String orderID;
    private Customer customer;
    private Store store;
    private  Drone drone;
    private Map<String, Line> items;
    private int orderCost;
    private int orderWeight;
    private int reasonableDeliveryTime;
    private int actualDeliveryTime;

    public Order(String orderID, Customer customer, Store store, Drone drone) {
        this.orderID = orderID;
        this.customer = customer;
        this.store = store;
        this.drone = drone;
        this.items = new TreeMap<String, Line>();
        this.orderCost = 0;
        this.orderWeight = 0;
        this.reasonableDeliveryTime = -1;
        this.actualDeliveryTime = -1;
    }

    public String getOrderID() {
        return this.orderID;
    }

    public String getCustomerID() {
        return this.customer.getAccountID();
    }

    public Customer getCustomer() {
        return this.customer;
    }
    public Location getDestination() {
        return this.customer.getLocation();
    }

    public String getStoreID() {
        return this.store.getStoreID();
    }

    public Store getStore() {
        return this.store;
    }

    public String getDroneID() {
        return drone.getDroneID();
    }

    public Drone getDrone() {
        return this.drone;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }

    public int getOrderCost() {
        return this.orderCost;
    }

    public int getOrderWeight() {
        return this.orderWeight;
    }
    public int getReasonableDeliveryTime() {
        return this.reasonableDeliveryTime;
    }

    public void setReasonableDeliveryTime(int reasonableDeliveryTime) {
        this.reasonableDeliveryTime = reasonableDeliveryTime;
    }

    public int getActualDeliveryTime() {
        return this.actualDeliveryTime;
    }

    public void setActualDeliveryTime(int actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public boolean containsItem(String itemID) {
        return items.containsKey(itemID);
    }

    /**
     * Adds an item to an order if certain conditions are met
     * @param item - item to be added
     * @param quantity - quantity of item to be added
     * @param price - unit price of item to be added
     * @return true if item is added to order, else false
     */
    public boolean addItem(Item item, int quantity, int price) {
        if (this.store == null) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        if (!this.store.containsOrder(this.orderID)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        if (item == null || !this.store.containsItem(item.getItemID())) {
            System.out.println("ERROR:item_identifier_does_not_exist");
            return false;
        }
        if (items.containsKey(item.getItemID())) {
            System.out.println("ERROR:item_already_ordered");
            return false;
        }
        if (this.customer == null || quantity * price + this.customer.getPendingCost() > this.customer.getCredit()) {
            System.out.println("ERROR:customer_cant_afford_new_item");
            return false;
        }
        if (this.drone == null || quantity * item.getWeight() > this.drone.getRemainingWeight()) {
            System.out.println("ERROR:drone_cant_carry_new_item");
            return false;
        }
        Line line = new Line(item, quantity, price);
        this.addLine(line);
        boolean isOrderUpdated = this.drone.updateOrder(this, line.getLineWeight()) && this.store.updateOrder(this);
        if (!isOrderUpdated) {
            this.removeLine(line);
        }
        return isOrderUpdated && items.containsKey(line.getLineID());
    }

    /**
     * Adds a line item to this order and updates the order's information accordingly
     * @param line - line containing information about item, quantity, and price
     */
    private void addLine(Line line) {
        items.put(line.getLineID(), line);
        this.updateOrderCost(line.getLineCost());
        this.updateOrderWeight(line.getLineWeight());
    }

    /**
     * Removes a line item from this order and updates the order's information accordingly
     * @param line - line to be removed
     */
    private void removeLine(Line line) {
        items.remove(line.getLineID());
        this.updateOrderCost(-1 * line.getLineCost());
        this.updateOrderWeight(-1 * line.getLineWeight());
    }

    /**
     * Updates the total cost of the order if a line item is added or removed from the order
     * @param lineCost - total cost of line item
     */
    private void updateOrderCost(int lineCost) {
        this.orderCost += lineCost;
        this.customer.addPendingCost(lineCost);
    }

    /**
     * Updates the total weight of the order if a line item is added or removed from the order
     * @param lineWeight - total weight of line item
     */
    private void updateOrderWeight(int lineWeight) {
        this.orderWeight += lineWeight;
    }

    /**
     * Information about the items inside this order
     * @return String representing information about items inside this order
     */
    private String itemsToString() {
        StringBuilder retString = new StringBuilder();
        for (Map.Entry<String, Line> lineEntry : items.entrySet()) {
            retString.append("\n");
            retString.append(lineEntry.getValue().toString());
        }
        return retString.toString();
    }

    /**
     * Information about this order
     * @return String representing information this order
     */
    @Override
    public String toString() {
        return "orderID:" + this.orderID + itemsToString();
    }
}
