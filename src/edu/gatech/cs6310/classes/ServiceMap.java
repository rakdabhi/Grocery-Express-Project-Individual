package edu.gatech.cs6310.classes;

import java.util.Map;
import java.util.TreeMap;

public class ServiceMap {
    private static ServiceMap map = null;
    private Map<Location, Customer> customerMap;
    private Map<Location, Store> storeMap;
    private Map<Location, Drone> droneMap;

    /**
     * Private constructor to construct a Singleton ServiceMap instance
     */
    private ServiceMap() {
        this.customerMap = new TreeMap<Location, Customer>();
        this.storeMap = new TreeMap<Location, Store>();
        this.droneMap = new TreeMap<Location, Drone>();
    }

    /**
     * Static method to create and return a Singleton ServiceMap instance
     * @return a Singleton ServiceMap instance
     */
    public static synchronized ServiceMap getInstance() {
        if (map == null) {
            map = new ServiceMap();
        }
        return map;
    }

    /**
     * Function to add a location-customer pairing to customerMap
     * @param location - location of customer to add to customerMap
     * @param customer - customer with given location
     * @return true if customer is added to map, else false
     */
    public boolean addLocation(Location location, Customer customer) {
        if (this.locationExists(location)) {
            System.out.println("ERROR:location_already_taken");
            return false;
        }
        this.customerMap.put(location, customer);
        return true;
    }

    /**
     * Function to add a location-store pairing to storeMap
     * @param location - location of store to add to storeMap
     * @param store - store with given location
     * @return true if store is added to map, else false
     */
    public boolean addLocation(Location location, Store store) {
        if (this.locationExists(location)) {
            System.out.println("ERROR:location_already_taken");
            return false;
        }
        this.storeMap.put(location, store);
        return true;
    }

    /**
     * Function to add a location-drone pairing to droneMap
     * @param location - location of drone to add to droneMap
     * @param drone - drone with given location
     * @return true if drone is added to map, else false
     */
    public boolean addLocation(Location location, Drone drone) {
        if (this.locationExists(location)) {
            System.out.println("ERROR:location_already_taken");
            return false;
        }
        this.droneMap.put(location, drone);
        return true;
    }

    /**
     * Determines whether a customer or store already exists at the given location
     * @param location - location to check whether a customer or store is occupying that place
     * @return true if a customer or store exists at the given location, else false
     */
    public boolean locationExists(Location location) {
        return this.customerMap.containsKey(location) || this.storeMap.containsKey(location);
    }
}
