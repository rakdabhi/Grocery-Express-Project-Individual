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
     * NOTE: Charge has units 'c', Time has minute units 'min', and Distance has units 'd' (you can think of 1d as 1 mile or 1 kilometer)
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
        if (!ServiceMap.getInstance().locationExists(order.getDestination())) {
            System.out.println("ERROR:destination_location_does_not_exist");
            return false;
        }

        updateCharge();
        int deliveryStartTime = Clock.getInstance().getTime();

        double distance = ServiceMap.getInstance().computeDistance(this.location, order.getDestination());

        // sets a reasonable delivery time to 2 * delivery time (time to cover distance at default speed without factoring sitting idle to recharge)
        order.setReasonableDeliveryTime((int) (2 * ((distance / this.speed) * 10)));

        System.out.println("Curr Location: " + this.location.toString() + ", Dest Location: " + order.getDestination().toString());
        travelDistance(distance); // drone travels the distance to delivery order

        int deliveryTime = Clock.getInstance().getTime() - deliveryStartTime; // tracks delivery time of order
        order.setActualDeliveryTime(deliveryTime); // sets delivery time of order
        System.out.println("Reasonable Delivery Time: " + order.getReasonableDeliveryTime() +
                ", Actual Delivery Time: " + order.getActualDeliveryTime());
        this.lastChargeUpdate = Clock.getInstance().getTime();
        this.location = order.getDestination();
        this.pilot.incrementExperience();
        this.increaseRemainingWeightOrder(order.getOrderWeight());
        orders.remove(order.getOrderID());
        this.calculateOverloads();

        return !orders.containsKey(order.getOrderID());
    }

    /**
     * Method to move this drone over a certain distance and update time along the way
     * @param distance - distance that drone has to travel
     */
    private void travelDistance(double distance) {
        int requiredFuel = (int) Math.round(distance * this.fuelConsumptionRate);
        System.out.println("Distance: " + distance + ", Curr Fuel: " + this.remainingFuel
                + ", Required Fuel: " + requiredFuel + ", Time: " + Clock.getInstance().getTime());

        // When the drone has enough remaining fuel to cover the distance
        if (this.remainingFuel >= requiredFuel) {
            this.remainingFuel -= requiredFuel;
            System.out.println("CASE 1: time increment - " + ((int) (distance / this.speed) * 10));
            Clock.getInstance().incrementTime((int) (distance / this.speed) * 10); // Time it takes to cover distance required

        // When the drone can wait at its current place until it's battery has filled enough to cover the distance
        } else if (this.fuelCapacity >= requiredFuel) {
            // Drone waits in-place to charge battery
            int minLightNeeded = requiredFuel - this.remainingFuel;
            int idleTime = -1;

            // if more light is emitted during the span of the refuel rate time than the minimum amount of light needed to cover distance
            System.out.println("2. Delta: " + (minLightNeeded/this.refuelRate) + ", Light over Delta: " + Clock.getInstance().getLightOverDelta(minLightNeeded / this.refuelRate));
            if (minLightNeeded <= Clock.getInstance().getLightOverDelta(minLightNeeded / this.refuelRate)) {
                idleTime = minLightNeeded / this.refuelRate;
                System.out.println("CASE 2.1: time increment - " + idleTime);

            // if less light is emitted than the refuel rate time
            } else {
                int startTime = Clock.getInstance().getTime();
                int endTime = Clock.getEndTime(minLightNeeded, startTime, minLightNeeded / this.refuelRate);
                idleTime = endTime - startTime;
                System.out.println("CASE 2.2: time increment - " + idleTime);
            }
            Clock.getInstance().incrementTime(idleTime); // time to charge battery just enough to cover distance
            this.remainingFuel = requiredFuel; // battery is full enough to cover distance
            travelDistance(distance);

        // When the drone can't cover the distance even on a full charge (i.e. if (this.fuelCapacity - this.remainingFuel < requiredFuel))
        } else {
            int fuelForFullCharge = this.fuelCapacity - this.remainingFuel;
            int idleTime = -1;

            // if more light is emitted during the span of the refuel rate time than the amount of light needed for a full charge
            System.out.println("3. Delta: " + (fuelForFullCharge/this.refuelRate) + ", Light over Delta: " + Clock.getInstance().getLightOverDelta(fuelForFullCharge / this.refuelRate));
            if (fuelForFullCharge <= Clock.getInstance().getLightOverDelta(fuelForFullCharge / this.refuelRate)) {
                idleTime = fuelForFullCharge / this.refuelRate;
                System.out.println("CASE 3.1: time - " + idleTime);

            // if less light is emitted than the refuel rate time
            } else {
                int startTime = Clock.getInstance().getTime();
                int endTime = Clock.getEndTime(fuelForFullCharge, startTime, fuelForFullCharge / this.refuelRate);
                idleTime = endTime - startTime;
                System.out.println("CASE 3.2: time - " + idleTime);
            }
            Clock.getInstance().incrementTime(idleTime); // Time to fully charge battery
            this.remainingFuel = this.fuelCapacity; // battery is now fully charged

            double fullChargeRange = (double)this.remainingFuel / this.fuelConsumptionRate; // max distance drone can cover on full charge
            Clock.getInstance().incrementTime((int) (fullChargeRange / this.speed) * 10); // time it takes to cover max distance
            this.remainingFuel = 0; // battery is depleted
            travelDistance(distance - fullChargeRange);
        }
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
        this.remainingFuel = Math.min(this.fuelCapacity, this.remainingFuel +
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
        retString = retString + ",fuel_consumption_rate:" + this.fuelConsumptionRate + "c/d";
        retString = retString + ",speed:" + this.speed + "d/10min";
        retString = retString + ",location:" + this.location.toString();

        return retString;
    }
}
