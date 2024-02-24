package edu.gatech.cs6310.classes;

import java.util.Map;
import java.util.TreeMap;

public class Customer extends User {
    private Map<String, Order> orders;
    private Location location;
    private int rating;
    private int credit;
    private int pendingCost;

    public Customer(String accountID, String firstName, String lastName, String phone,
                    int rating, int credit, Location location) {
        super(accountID, firstName, lastName, phone);
        this.orders = new TreeMap<String, Order>();
        this.location = location;
        this.rating = rating;
        this.credit = credit;
        this.pendingCost = 0;
    }

    public Customer(String accountID, String firstName, String lastName, String phone,
                    String rating, String credit, Location location) {
        this(accountID, firstName, lastName, phone,
                Integer.parseInt(rating), Integer.parseInt(credit), location);
    }

    public boolean containsOrder(String orderID) {
        return orders.containsKey(orderID);
    }

    public Location getLocation() {
        return this.location;
    }

    public int getPendingCost() {
        return this.pendingCost;
    }

    public Order getOrder(String orderID) {
        return orders.get(orderID);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    /**
     * Creates an initial stub for an order if it doesn't exist
     * @param orderID - unique ID of order
     * @param store - store where order will be placed
     * @param droneID - unique ID of drone that will carry order
     * @return true if order is added to store's list of pending orders, else false
     */
    public boolean startOrder(String orderID, Store store, String droneID) {
        if (store == null) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        if (orders.containsKey(orderID)) {
            System.out.println("ERROR:order_identifier_already_exists");
            return false;
        }
        if (!store.containsDrone(droneID)) {
            System.out.println("ERROR:drone_identifier_does_not_exist");
            return false;
        }
        Order order = new Order(orderID, this, store, store.getDrone(droneID));
        boolean orderAdded = store.addOrder(order);
        if (orderAdded) {
            orders.put(order.getOrderID(), order);
        }
        return orderAdded && orders.containsKey(orderID);
    }

    /**
     * Customer requests an item (of a specified quantity and price) to be added to their order
     * @param orderID - unique ID of order to add item to
     * @param itemID - unique ID of item to be added
     * @param quantity - quantity of item to be added
     * @param price - unit price of item
     * @return true if item is added to order, else false
     */
    public boolean requestItem(String orderID, String itemID, int quantity, int price) {
        if (!this.containsOrder(orderID)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        Order order = orders.get(orderID);
        if (!order.getStore().containsItem(itemID)) {
            System.out.println("ERROR:item_identifier_does_not_exist");
            return false;
        }
        if (order.containsItem(itemID)) {
            System.out.println("ERROR:item_already_ordered");
            return false;
        }
        if (quantity * price + this.pendingCost > this.credit) {
            System.out.println("ERROR:customer_cant_afford_new_item");
            return false;
        }
        Store store = order.getStore();
        Item item = store.getItem(itemID);
        return order.addItem(item, quantity, price);
    }

    /**
     * requestItem() overload method to convert certain String values into required int values
     * @param orderID - unique ID of order to add item to
     * @param itemID - unique ID of item to be added
     * @param quantity - quantity of item to be added
     * @param price - unit price of item
     * @return true if item is added to order, else false
     */
    public boolean requestItem(String orderID, String itemID, String quantity, String price) {
        return this.requestItem(orderID, itemID, Integer.parseInt(quantity), Integer.parseInt(price));
    }

    /**
     * Adds a dollar amount to the pendingCost variable to keep track of the total cost of pending orders
     * @param amount - dollar amount to be added
     * @return true if amount can be added to the pending cost, else false
     */
    public boolean addPendingCost(int amount) {
        if (amount + this.pendingCost > this.credit) {
            System.out.println("ERROR:customer_cant_afford_new_item");
            return false;
        }
        this.pendingCost += amount;
        return true;
    }

    /**
     * If order is cancelled, the order amount will be removed from pendingCost variable
     * @param canceledAmount - total cost of order being cancelled
     */
    public void revertPendingCost(int canceledAmount) {
        this.pendingCost -= canceledAmount;
    }

    /**
     * Customer purchases an order
     * @param orderID - unique ID of order to be purchased
     * @return true if order was purchased successfully, else false
     */
    public boolean purchaseOrder(String orderID) {
        if (!orders.containsKey(orderID)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        Order order = orders.get(orderID);
        Store store = order.getStore();
        boolean orderDelivered = store.shipOrder(orderID);
        if (orderDelivered) {
            this.useCredit(order.getOrderCost());
            store.addPurchaseRevenue(order.getOrderCost());
            orders.remove(orderID);
        }
        return orderDelivered && !orders.containsKey(orderID);
    }

    /**
     * Customer uses their credit if they purchased an order successfully
     * @param orderCost - total cost of order that was successfully purchased
     */
    public void useCredit(int orderCost) {
        this.credit -= orderCost;
        this.pendingCost -= orderCost;
    }

    /**
     * Gives customer some credit if their order was not delivered in a reasonable time
     * @param credit - amount of credit given by store
     */
    public void giveCredit(int credit) {
        this.credit += credit;
    }

    /**
     * Customer cancels an order
     * @param storeID - unique ID of store to cancel order from
     * @param orderID - unique ID of order to cancel
     * @return true if order is cancelled, else false
     */
    public boolean cancelOrder(String storeID, String orderID) {
        if (orders.containsKey(orderID) && !orders.get(orderID).getStoreID().equals(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        if (!orders.containsKey(orderID)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        Order order = orders.get(orderID);
        boolean orderCanceled = order.getStore().cancelOrder(storeID, orderID);
        if (orderCanceled) {
            this.revertPendingCost(order.getOrderCost());
            orders.remove(order.getOrderID());
        }
        return orderCanceled && !orders.containsKey(orderID);
    }

    /**
     * String representing information about this customer
     * @return - a String representing information about this customer
     */
    @Override
    public String toString() {
        return "name:" + this.getFullName() +
              ",phone:" + this.phone +
              ",rating:" + this.rating +
              ",credit:" + this.credit +
              ",location:" + this.location.toString();
    }
}
