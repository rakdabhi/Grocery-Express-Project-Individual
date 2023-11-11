package edu.gatech.cs6310.classes;

import java.util.Map;
import java.util.TreeMap;

public class Drone {
    private String droneID;
    private int weightCapacity;
    private int remainingWeight;
    private DronePilot pilot;
    private Map<String, Order> orders;
    private int overloads;

    /*
     * NOTE: Charge has units 'c', Time has minute units 'min', and Distance has units 'd'
     */
    private int fuelCapacity; // maximum fuel capacity of drone in units of charge (c)
    private int remainingFuel; // the remaining charge that this drone has in units of charge (c)
    private int lastChargeUpdate; // timestamp of the last time the drone's charge was updated in units of charge (c)
    private int speed; // speed of drone in units of distance per 10 minutes (d/10min)
    private int refuelRate; // refuel rate of solar-powered drone in units of charge per minute (c/min)
    private int fuelConsumptionRate; // rate of fuel consumption in units of charge per unit distance (c/d)
    private Location location; // current location of drone as a Location class instance


    public Drone(String droneID, int weightCapacity, int fuelCapacity,
                 int refuelRate, int fuelConsumptionRate, Location location) {
        this.droneID = droneID;
        this.weightCapacity = weightCapacity;
        this.remainingWeight = weightCapacity;
        this.fuelCapacity = fuelCapacity;
        this.remainingFuel = fuelCapacity;
        this.lastChargeUpdate = Clock.getInstance().getTime(); // current time is the last time remaining fuel has been updated
        this.speed = 1; // default speed is set to 1 unit distance covered every 10 minutes
        this.refuelRate = refuelRate;
        this.fuelConsumptionRate = fuelConsumptionRate;
        this.pilot = null;
        this.orders = new TreeMap<String, Order>();
        this.location = location;
        this.overloads = 0;
    }

    public String getDroneID() {
        return this.droneID;
    }

    public int getWeightCapacity() {
        return this.weightCapacity;
    }

    public int getFuelCapacity() {
        return this.fuelCapacity;
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
        if (this.fuelCapacity <= 0) {
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
        if (this.fuelCapacity <= 0) {
            System.out.println("ERROR:drone_needs_fuel");
            return false;
        }
        this.fuelCapacity -= 1;
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
     * Function to update the remaining charge level of this drone since the last time it was updated
     */
    private void updateCharge() {
        int currTime = Clock.getInstance().getTime();
        int sunlightAmount = Clock.getLightOverTime(this.lastChargeUpdate, currTime);
        this.remainingFuel = Math.min(this.fuelCapacity,
                             Math.min(sunlightAmount, this.refuelRate * (currTime - this.lastChargeUpdate)));
        this.lastChargeUpdate = Clock.getInstance().getTime();
    }

    /**
     * Information about this drone
     * @return String representing information about this drone
     */
    @Override
    public String toString() {
        updateCharge();
        String retString;
        retString = "droneID:" + this.droneID
                 + ",total_cap:" + this.weightCapacity
                 + ",num_orders:" + this.orders.size()
                 + ",remaining_cap:" + this.remainingWeight;
        if (this.pilot != null) {
            String pilotField = ",flown_by:" + pilot.getFullName();
            retString += pilotField;
        }
        retString = retString + ",fuel_cap:" + this.fuelCapacity + "c";
        retString = retString + ",remaining_fuel:" + this.remainingFuel + "c";
        retString = retString + ",refuel_rate:" + this.refuelRate + "c/min";
        retString = retString + ",fuel_consumption_rate:" + this.fuelCapacity + "c/d";
        retString = retString + ",speed:" + this.speed + "d/10min";
        retString = retString + ",location:" + this.location.toString();

        return retString;
    }
}
