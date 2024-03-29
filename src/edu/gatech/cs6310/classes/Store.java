package edu.gatech.cs6310.classes;

import java.util.Map;
import java.util.TreeMap;

public class Store {
    private String name;
    private int revenue;
    private Map<String, Item> items;
    private Map<String, Drone> drones;
    private Map<String, Order> orders;
    private Location location;
    private int purchases;
    private int overloads;
    private int transfers;
    private int penalties;
    private int penaltiesCost;

    public Store(String name, int revenue, Location location) {
        this.name = name;
        this.revenue = revenue;
        this.items = new TreeMap<String, Item>();
        this.drones = new TreeMap<String, Drone>();
        this.orders = new TreeMap<String, Order>();
        this.location = location;
        this.purchases = 0;
        this.overloads = 0;
        this.transfers = 0;
        this.penalties = 0;
        this.penaltiesCost = 0;
    }

    public Store(String name, String revenue, Location location) {
        this(name, Integer.parseInt(revenue), location);
    }

    public String getStoreID() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public Order getOrder(String orderID) {
        return orders.get(orderID);
    }

    public String getCustomerID(String orderID) {
        return orders.get(orderID).getCustomerID();
    }

    public Drone getDrone(String droneID) {
        return drones.get(droneID);
    }

    public Item getItem(String itemID) {
        return items.get(itemID);
    }

    public Location getLocation() {
        return this.location;
    }

    /**
     * Adds an item to the store's catalog of items available to be requested and purchased
     * @param itemName - name of item
     * @param itemWeight - weight of item as an int
     * @return true if item is added, else false
     */
    public boolean addItem(String itemName, int itemWeight) {
        if (items.containsKey(itemName)) {
            System.out.println("ERROR:item_identifier_already_exists");
            return false;
        }
        Item item = new Item(itemName, itemWeight);
        items.put(item.getName(), item);
        System.out.println("OK:change_completed");
        return true;
    }

    /**
     * Adds an item to the store's catalog of items available to be requested and purchased
     * @param itemName - name of item
     * @param itemWeight - weight of item as a String
     * @return true if item is added, else false
     */
    public boolean addItem(String itemName, String itemWeight) {
        return addItem(itemName, Integer.parseInt(itemWeight));
    }

    /**
     * Displays all items that store has available for request and purchase
     */
    public void displayItems() {
        for (Map.Entry<String, Item> itemEntry : items.entrySet()) {
            System.out.println(itemEntry.getValue().toString());
        }
        Clock.getInstance().incrementTime(this.items.size());
        System.out.println("OK:display_completed");
    }

    /**
     * Adds a drone for the store to use to deliver orders to their respective customers
     * @param droneID - unique ID of the drone
     * @param weightCapacity - maximum weight drone can lift
     * @param fuelCapacity - maximum fuel capacity of drone in units of charge (c)
     * @param refuelRate - refuel rate of solar-powered drone in units of charge per minute (c/min)
     * @param fuelConsumptionRate - rate of fuel consumption in units of charge per unit distance (c/d)
     * @return true if drone is added, else false
     */
    public boolean addDrone(String droneID, int weightCapacity,
                            int fuelCapacity, int refuelRate, int fuelConsumptionRate) {
        if (drones.containsKey(droneID)) {
            System.out.println("ERROR:drone_identifier_already_exists");
            return false;
        }
        Drone drone = new Drone(droneID, weightCapacity, fuelCapacity, refuelRate, fuelConsumptionRate, this.location);
        drones.put(drone.getDroneID(), drone);
        return true;
    }

    /**
     * Adds a drone for the store to use to deliver orders to their respective customers
     * @param droneID - unique ID of the drone as a string
     * @param weightCapacity - maximum weight drone can lift as a string
     * @param fuelCapacity - maximum fuel capacity of drone in units of charge (c)
     * @param refuelRate - refuel rate of solar-powered drone in units of charge per minute (c/min)
     * @param fuelConsumptionRate - rate of fuel consumption in units of charge per unit distance (c/d)
     * @return true if drone is added, else false
     */
    public boolean addDrone(String droneID, String weightCapacity,
                            String fuelCapacity, String refuelRate, String fuelConsumptionRate) {
        return addDrone(droneID, Integer.parseInt(weightCapacity),
                        Integer.parseInt(fuelCapacity), Integer.parseInt(refuelRate), Integer.parseInt(fuelConsumptionRate));
    }

    /**
     * Displays all drones that store has to help with delivering orders
     */
    public void displayDrones() {
        for (Map.Entry<String, Drone> droneEntry : drones.entrySet()) {
            System.out.println(droneEntry.getValue().toString());
        }
        Clock.getInstance().incrementTime(this.drones.size());
        System.out.println("OK:display_completed");
    }

    /**
     * Checks whether a drone exists
     * @param droneID - unique ID of drone
     * @return true if drone exists, else false
     */
    public boolean containsDrone(String droneID) {
        return drones.containsKey(droneID);
    }

    /**
     * Checks whether an order exists
     * @param orderID - unique ID of order
     * @return true if order exists, else false
     */
    public boolean containsOrder(String orderID) {
        return orders.containsKey(orderID);
    }

    /**
     * Checks whether an item exists
     * @param itemID - unique ID of item
     * @return true if item exists, else false
     */
    public boolean containsItem(String itemID) {
        return items.containsKey(itemID);
    }

    /**
     * Adds an order to the store's list of pending orders
     * @param order - the order to add
     * @return returns true if order is added, else false
     */
    public boolean addOrder(Order order) {
        if (orders.containsKey(order.getOrderID())) {
            System.out.println("ERROR:order_identifier_already_exists");
            return false;
        }
        if (!drones.containsKey(order.getDroneID())) {
            System.out.println("ERROR:drone_identifier_does_not_exist");
            return false;
        }
        boolean orderAdded = drones.get(order.getDroneID()).addOrder(order);
        if (orderAdded) {
            orders.put(order.getOrderID(), order);
        }
        return orderAdded && orders.containsKey(order.getOrderID());
    }

    /**
     * Updates an order when a new line item is added to order
     * @param order - order to be updated
     * @return true if order is updated, else false
     */
    public boolean updateOrder(Order order) {
        if (!orders.containsKey(order.getOrderID())) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        orders.put(order.getOrderID(), order);
        return true;
    }

    /**
     * Ships an order that is purchased by a customer
     * @param orderID - unique ID of order to be shipped
     * @return true if order is shipped, else false
     */
    public boolean shipOrder(String orderID) {
        if (!orders.containsKey(orderID)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        Order order = orders.get(orderID);
        Drone drone = order.getDrone();
        boolean orderDelivered = drone.deliverOrder(order);
        if (orderDelivered && !drone.containsOrder(orderID)) {
            checkDeliveryTime(order);
            this.overloads += drone.getOverloads();
            orders.remove(orderID);
        }
        return orderDelivered && !orders.containsKey(orderID);
    }

    /**
     * Checks the delivery time of order and gives credit to customer if delivery time is unreasonable
     * @param order - order to check delivery time
     */
    public void checkDeliveryTime(Order order) {
        if (order.getReasonableDeliveryTime() < 0) {
            System.out.println("ERROR:order_has_not_been_purchased");
            return;
        }
        if (order.getActualDeliveryTime() < 0) {
            System.out.println("ERROR:order_not_delivered_yet");
            return;
        }

        // if order delivery took longer than the reasonable time expected,
        // then store gives customer a discount of 20%, or the amount of revenue they have, whichever is min
        if (order.getReasonableDeliveryTime() < order.getActualDeliveryTime()) {
            Customer customer = order.getCustomer();
            int discount = Math.min((int) (order.getOrderCost() * 0.2), this.revenue);
            customer.giveCredit(discount);
            this.revenue -= discount;
            this.penalties += 1;
            this.penaltiesCost += discount;
        }
    }

    /**
     * Adds some dollar amount to the store's revenue after an order has been purchased and successfully delivered,
     * and increments the purchases counter by 1
     * @param amount - amount to be added to revenue
     */
    public void addPurchaseRevenue(int amount) {
        this.revenue += amount;
        this.purchases += 1;
    }

    /**
     * Deletes an order if the customer cancels their order
     * @param orderID - unique ID of order to be cancelled
     * @return true if order is cancelled, else false
     */
    public boolean cancelOrder(String storeID, String orderID) {
        if (!this.name.equals(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        if (!orders.containsKey(orderID)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        Order order = orders.get(orderID);
        boolean orderCanceled = order.getDrone().removeOrder(orderID);
        if (orderCanceled) {
            orders.remove(order.getOrderID());
        }
        return orderCanceled && !orders.containsKey(orderID);
    }

    /**
     * Displays all orders that this store has
     */
    public void displayOrders() {
        for (Map.Entry<String, Order> orderEntry : orders.entrySet()) {
            System.out.println(orderEntry.getValue().toString());
        }
        Clock.getInstance().incrementTime(this.orders.size());
        System.out.println("OK:display_completed");
    }

    /**
     * Transfers an order from one drone to another drone available at a particular store
     * @param orderID - unique ID of order to be transferred
     * @param droneID - unique ID of drone that order will be transferred to
     * @return true if order is transferred, else false
     */
    public boolean transferOrder(String orderID, String droneID) {
        if (!orders.containsKey(orderID)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        if (!drones.containsKey(droneID)) {
            System.out.println("ERROR:drone_identifier_does_not_exist");
            return false;
        }
        if (orders.get(orderID).getOrderWeight() > drones.get(droneID).getRemainingWeight()) {
            System.out.println("ERROR:new_drone_does_not_have_enough_capacity");
            return false;
        }
        if (orders.get(orderID).getDroneID().equals(droneID)) {
            System.out.println("OK:new_drone_is_current_drone_no_change");
            return false;
        }
        Order order = orders.get(orderID);
        Drone oldDrone = order.getDrone();
        Drone newDrone = drones.get(droneID);
        boolean orderTransferred = newDrone.addOrder(order) && oldDrone.removeOrder(orderID);
        if (orderTransferred) {
            newDrone.getOrder(orderID).setDrone(newDrone);
            this.transfers += 1;
        }
        return orderTransferred;
    }

    /**
     * Returns the efficiency of this store as a String
     * @return efficiency of store
     */
    public String getEfficiency() {
        return "name:" + this.name +
              ",purchases:" + this.purchases +
              ",overloads:" + this.overloads +
              ",transfers:" + this.transfers +
              ",penalties:" + this.penalties +
              ",penalties_cost:" + this.penaltiesCost;
    }

    /**
     * Information about this store
     * @return - String with information about this store
     */
    @Override
    public String toString() {
        return "name:" + this.name +
               ",revenue:" + this.revenue +
               ",location:" + this.location.toString();
    }
}
