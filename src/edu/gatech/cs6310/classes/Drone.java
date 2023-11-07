package edu.gatech.cs6310.classes;

import java.util.Map;
import java.util.TreeMap;

public class Drone {
    private String droneID;
    private int weightCapacity;
    private int remainingWeight;
    private int remainingTrips;
    private DronePilot pilot;
    private Map<String, Order> orders;
    private Location location;
    private int overloads;

    public Drone(String droneID, int weightCapacity, int remainingTrips, Location location) {
        this.droneID = droneID;
        this.weightCapacity = weightCapacity;
        this.remainingWeight = weightCapacity;
        this.remainingTrips = remainingTrips;
        this.pilot = null;
        this.orders = new TreeMap<String, Order>();
        this.location = location;
        this.overloads = 0;
    }

    public Drone(String droneID, String weightCapacity, String remainingTrips, Location location) {
        this(droneID, Integer.parseInt(weightCapacity), Integer.parseInt(remainingTrips), location);
    }

    public String getDroneID() {
        return this.droneID;
    }

    public int getWeightCapacity() {
        return this.weightCapacity;
    }

    public int getRemainingTrips() {
        return this.remainingTrips;
    }

    public DronePilot getPilot() {
        return pilot;
    }

    public void setPilot(DronePilot pilot) {
        this.pilot = pilot;
    }

    public boolean containsOrder(String orderID) {
        return orders.containsKey(orderID);
    }

    public int getRemainingWeight() {
        return this.remainingWeight;
    }

    public Order getOrder(String orderID) {
        return orders.get(orderID);
    }

    public Location getLocation() {
        return this.location;
    }

    public int getOverloads() {
        return this.overloads;
    }

    /**
     * Calculates the number of overloads (extra unpurchased orders) that this drone carried during a delivery
     */
    public void calculateOverloads() {
        this.overloads = orders.size();
    }

    /**
     * Adds an order to a drone's list of orders
     * @param order - order to add
     * @return true if order is added, else false
     */
    public boolean addOrder(Order order) {
        if (orders.containsKey(order.getOrderID())) {
            System.out.println("ERROR:order_identifier_already_exists");
            return false;
        }
        if (order.getOrderWeight() > this.remainingWeight) {
            System.out.println("ERROR:new_drone_does_not_have_enough_capacity");
            return false;
        }
        order.setDrone(this);
        orders.put(order.getOrderID(), order);
        this.decreaseRemainingWeightOrder(order.getOrderWeight());
        return orders.containsKey(order.getOrderID());
    }

    /**
     * Updates an order that the drone is carrying when a customer adds a new item
     * @param order - order to update
     * @param lineWeight - total weight of line item (weight of item multiplied by its quantity)
     * @return true if order is updated, else false
     */
    public boolean updateOrder(Order order, int lineWeight) {
        if (!orders.containsKey(order.getOrderID())) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        if (lineWeight > this.remainingWeight) {
            System.out.println("ERROR:drone_cant_carry_new_item");
            return false;
        }
        orders.put(order.getOrderID(), order);
        this.decreaseRemainingWeightLine(lineWeight);
        return true;
    }

    /**
     * Decreases the remaining weight capacity of the drone when an item is added
     * @param lineWeight - total weight of item and its quantity to be added
     */
    private void decreaseRemainingWeightLine(int lineWeight) {
        if (lineWeight > this.remainingWeight) {
            System.out.println("ERROR:drone_cant_carry_new_item");
            return;
        }
        this.remainingWeight -= lineWeight;
    }

    /**
     * Decreases the remaining weight capacity of the drone when an order is transferred to this drone
     * @param orderWeight - total weight of order
     */
    private void decreaseRemainingWeightOrder(int orderWeight) {
        if (orderWeight > this.remainingWeight) {
            System.out.println("ERROR:new_drone_does_not_have_enough_capacity");
            return;
        }
        this.remainingWeight -= orderWeight;
    }

    /**
     * Increases the remaining weight capacity of the drone when an order is purchased or cancelled, or transferred to another drone
     * @param orderWeight - total weight of order
     */
    private void increaseRemainingWeightOrder(int orderWeight) {
        this.remainingWeight += orderWeight;
    }

    /**
     * Drone delivers the specified order to the customer
     * @param order - order to be delivered
     * @return true if order is delivered, else false
     */
    public boolean deliverOrder(Order order) {
        if (!orders.containsKey(order.getOrderID())) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        if (this.pilot == null) {
            System.out.println("ERROR:drone_needs_pilot");
            return false;
        }
        if (this.remainingTrips <= 0) {
            System.out.println("ERROR:drone_needs_fuel");
            return false;
        }
        boolean isDelivered = this.decrementFuel();
        if (isDelivered) {
            this.pilot.incrementExperience();
            this.increaseRemainingWeightOrder(order.getOrderWeight());
            orders.remove(order.getOrderID());
            this.calculateOverloads();
        }
        return isDelivered && !orders.containsKey(order.getOrderID());
    }

    /**
     * Decrements the remaining number of trips that this drone can take after delivering an order
     * @return true if remaining trips is decremented, else false
     */
    private boolean decrementFuel() {
        if (this.remainingTrips <= 0) {
            System.out.println("ERROR:drone_needs_fuel");
            return false;
        }
        this.remainingTrips -= 1;
        return true;
    }

    /**
     * Removes an order from drone's list if order is cancelled or transferred to another drone
     * @param orderID - unique ID of order to remove
     * @return true if order is removed, else false
     */
    public boolean removeOrder(String orderID) {
        if (!orders.containsKey(orderID)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        this.increaseRemainingWeightOrder(orders.get(orderID).getOrderWeight());
        orders.remove(orderID);
        return !orders.containsKey(orderID);
    }

    /**
     * Information about this drone
     * @return String representing information about this drone
     */
    @Override
    public String toString() {
        String retString = "";
        retString = "droneID:" + this.droneID
                 + ",total_cap:" + this.weightCapacity
                 + ",num_orders:" + this.orders.size()
                 + ",remaining_cap:" + this.remainingWeight
                 + ",trips_left:" + this.remainingTrips;

        if (this.pilot != null) {
            String pilotField = ",flown_by:" + pilot.getFullName();
            retString += pilotField;
        }

        retString = retString + ",location:" + this.location.toString();

        return retString;
    }
}
