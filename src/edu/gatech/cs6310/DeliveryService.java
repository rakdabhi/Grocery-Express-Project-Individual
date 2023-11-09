package edu.gatech.cs6310;
import edu.gatech.cs6310.classes.*;

import java.util.*;


public class DeliveryService {
    private Map<String, Store> stores = new TreeMap<String, Store>();
    private Map<String, Employee> employees = new TreeMap<String, Employee>();
    private Set<String> employeeUniques = new HashSet<String>();
    private Map<String, Customer> customers = new TreeMap<String, Customer>();
    private Clock clock = Clock.getInstance();
    private ServiceMap map = ServiceMap.getInstance();

    // a map of how much time it takes to execute each function based on the number of error checks it does
    private Map<String, Integer> timeMap = new HashMap<String, Integer>() {{
        put("make_store", 2);
        put("sell_item", 3);
        put("make_pilot", 3);
        put("make_drone", 3);
        put("fly_drone", 4);
        put("make_customer", 2);
        put("start_order", 5);
        put("request_item", 7);
        put("purchase_order", 5);
        put("cancel_order", 3);
        put("transfer_order", 6);
    }};

    public void commandLoop() {
        Scanner commandLineInput = new Scanner(System.in);
        String wholeInputLine;
        String[] tokens;
        final String DELIMITER = ",";
        boolean isSuccessful = false;

        label:
        while (true) {
            try {
                // Determine the next command and echo it to the monitor for testing purposes
                wholeInputLine = commandLineInput.nextLine();
                tokens = wholeInputLine.split(DELIMITER);
                System.out.println("> " + wholeInputLine);

                switch (tokens[0]) {
                    case "make_store":
                        // System.out.println("store: " + tokens[1] + ", revenue: " + tokens[2]);
                        // xCoordinate = tokens[3], yCoordinate = tokens[4]
                        isSuccessful = make_store(tokens[1], tokens[2], tokens[3], tokens[4]);
                        break;

                    case "display_stores":
                        // System.out.println("no parameters needed");
                        display_stores();
                        break;

                    case "sell_item":
                        // System.out.println("store: " + tokens[1] + ", item: " + tokens[2] + ", weight: " + tokens[3]);
                        isSuccessful = sell_item(tokens[1], tokens[2], tokens[3]);
                        break;

                    case "display_items":
                        display_items(tokens[1]);
                        break;

                    case "make_pilot":
                        // System.out.print("account: " + tokens[1] + ", first_name: " + tokens[2] + ", last_name: " + tokens[3]);
                        // System.out.println(", phone: " + tokens[4] + ", tax: " + tokens[5] + ", license: " + tokens[6] + ", experience: " + tokens[7]);
                        isSuccessful = make_pilot(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], tokens[7]);
                        break;

                    case "display_pilots":
                        // System.out.println("no parameters needed");
                        display_pilots();
                        break;

                    case "make_drone":
                        // System.out.println("store: " + tokens[1] + ", drone: " + tokens[2] + ", capacity: " + tokens[3] + ", fuel: " + tokens[4]);
                        isSuccessful = make_drone(tokens[1], tokens[2], tokens[3], tokens[4]);
                        break;

                    case "display_drones":
                        // System.out.println("store: " + tokens[1]);
                        display_drones(tokens[1]);
                        break;

                    case "fly_drone":
                        // System.out.println("store: " + tokens[1] + ", drone: " + tokens[2] + ", pilot: " + tokens[3]);
                        isSuccessful = fly_drone(tokens[1], tokens[2], tokens[3]);
                        break;

                    case "make_customer":
                        // System.out.print("account: " + tokens[1] + ", first_name: " + tokens[2] + ", last_name: " + tokens[3]);
                        // System.out.println(", phone: " + tokens[4] + ", rating: " + tokens[5] + ", credit: " + tokens[6]);
                        // xCoordinate = tokens[7], yCoordinate = tokens[8]
                        isSuccessful = make_customer(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], tokens[7], tokens[8]);
                        break;

                    case "display_customers":
                        // System.out.println("no parameters needed");
                        display_customers();
                        break;

                    case "start_order":
                        // System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", drone: " + tokens[3] + ", customer: " + tokens[4]);
                        isSuccessful = start_order(tokens[1], tokens[2], tokens[3], tokens[4]);
                        break;

                    case "display_orders":
                        // System.out.println("store: " + tokens[1]);
                        display_orders(tokens[1]);
                        break;

                    case "request_item":
                        // System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", item: " + tokens[3] + ", quantity: " + tokens[4] + ", unit_price: " + tokens[5]);
                        isSuccessful = request_item(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]);
                        break;

                    case "purchase_order":
                        // System.out.println("store: " + tokens[1] + ", order: " + tokens[2]);
                        isSuccessful = purchase_order(tokens[1], tokens[2]);
                        break;

                    case "cancel_order":
                        // System.out.println("store: " + tokens[1] + ", order: " + tokens[2]);
                        isSuccessful = cancel_order(tokens[1], tokens[2]);
                        break;

                    case "transfer_order":
                        // System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", new_drone: " + tokens[3]);
                        isSuccessful = transfer_order(tokens[1], tokens[2], tokens[3]);
                        break;

                    case "display_efficiency":
                        // System.out.println("no parameters needed");
                        display_efficiency();
                        break;

                    case "display_time":
                        display_time();
                        break;

                    case "stop":
                        System.out.println("stop acknowledged");
                        break label;

                    default:
                        if (!tokens[0].startsWith("//")) {
                            System.out.println("command " + tokens[0] + " NOT acknowledged");
                        }
                        break;
                }

                // Increments time on the clock based on the command and whether it was successful
                if (timeMap.containsKey(tokens[0])) {
                    if (!isSuccessful) {
                        clock.incrementTime(1);
                    } else {
                        clock.incrementTime(timeMap.get(tokens[0]));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println();
            }
        }

        System.out.println("simulation terminated");
        commandLineInput.close();
    }

    /**
     * Method to create a store if it doesn't exist
     * @param storeID - unique ID of store to create
     * @param revenue - revenue of store as an integer
     * @param x - x coordinate of store
     * @param y - y coordinate of store
     * @return true if store is created, else false
     */
    private boolean make_store(String storeID, String revenue, String x, String y) {
        if (stores.containsKey(storeID)) {
            System.out.println("ERROR:store_identifier_already_exists");
            return false;
        }
        Location location = new Location(x, y);
        if (map.locationExists(location)) {
            System.out.println("ERROR:location_already_taken");
            return false;
        }
        Store store = new Store(storeID, revenue, location);
        map.addLocation(location, store);
        stores.put(store.getStoreID(), store);
        System.out.println("OK:change_completed");
        return true;
    }

    /**
     * Displays all stores, ordered on the stores' names
     */
    private void display_stores() {
        for (Map.Entry<String, Store> storeEntry : stores.entrySet()) {
            System.out.println(storeEntry.getValue().toString());
            clock.incrementTime(1);
        }
        System.out.println("OK:display_completed");
    }

    /**
     * Adds an item to the catalog of items available to be requested and purchased from a particular store
     * @param storeID - unique ID of store to add item to
     * @param itemName - name of item
     * @param itemWeight - weight of item
     * @return true if item is added to store's catalog, else false
     */
    private boolean sell_item(String storeID, String itemName, String itemWeight) {
        if (!stores.containsKey(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        return stores.get(storeID).addItem(itemName, itemWeight);
    }

    /**
     * Displays all items that are available for request/purchase at a specific store, ordered on the items' names
     * @param storeID - unique ID of store
     */
    private void display_items(String storeID) {
        if (!stores.containsKey(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return;
        }
        stores.get(storeID).displayItems();
    }

    /**
     * Creates a drone pilot who could fly a drone later to support grocery deliveries
     * @param pilotID - unique ID to represent pilot
     * @param firstName - first name of pilot
     * @param lastName - last name of pilot
     * @param phone - phone number of pilot
     * @param taxID - tax identifier of pilot
     * @param licenceID - unique licence ID of pilot
     * @param experience - number of deliveries completed successfully
     * @return true if drone pilot is created, else false
     */
    private boolean make_pilot(String pilotID, String firstName, String lastName,
                               String phone, String taxID, String licenceID, String experience) {
        if (employeeUniques.contains(pilotID)) {
            System.out.println("ERROR:pilot_identifier_already_exists");
            return false;
        }
        if (employeeUniques.contains(licenceID)) {
            System.out.println("ERROR:pilot_license_already_exists");
            return false;
        }
        DronePilot pilot = new DronePilot(pilotID, firstName, lastName, phone, taxID, licenceID, experience);
        employees.put(pilot.getAccountID(), pilot);
        employeeUniques.add(pilot.getAccountID());
        employeeUniques.add(pilot.getLicenseID());
        System.out.println("OK:change_completed");
        return true;
    }

    /**
     * Displays all employees, ordered on the employees' account IDs
     */
    private void display_pilots() {
        for (Map.Entry<String, Employee> employeeEntry : employees.entrySet()) {
            System.out.println(employeeEntry.getValue().toString());
            clock.incrementTime(1);
        }
        System.out.println("OK:display_completed");
    }

    /**
     * Creates a drone that will carry and deliver groceries for a particular store
     * @param storeID - unique ID of store that drone works for
     * @param droneID - unique ID of the drone
     * @param weightCapacity - maximum weight drone can lift
     * @param tripsCapacity - number of remaining trips that drone can take for delivery before needing to be refueled
     * @return true if drone is created, else false
     */
    private boolean make_drone(String storeID, String droneID, String weightCapacity, String tripsCapacity) {
        if (!stores.containsKey(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        Store store = stores.get(storeID);
        boolean droneCreated = store.addDrone(droneID, weightCapacity,tripsCapacity);
        if (droneCreated) {
            map.addLocation(store.getDrone(droneID).getLocation(), store.getDrone(droneID));
            System.out.println("OK:change_completed");
        }
        return droneCreated;
    }

    /**
     * Displays all drones that a particular store owns, ordered on the drones' unique IDs
     * @param storeID - unique ID of store that owns the drones
     */
    private void display_drones(String storeID) {
        if (!stores.containsKey(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return;
        }
        stores.get(storeID).displayDrones();
    }

    /**
     * Assigns a given pilot to take control of a given drone
     * @param storeID - unique ID of store where drone works
     * @param droneID - unique ID of the drone
     * @param pilotID - unique identifier for pilot
     * @return true if pilot is successfully assigned to control a given drone
     */
    private boolean fly_drone(String storeID, String droneID, String pilotID) {
        if (!stores.containsKey(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        if (!stores.get(storeID).containsDrone(droneID)) {
            System.out.println("ERROR:drone_identifier_does_not_exist");
            return false;
        }
        if (!employees.containsKey(pilotID)) {
            System.out.println("ERROR:pilot_identifier_does_not_exist");
            return false;
        }

        DronePilot pilot = (DronePilot) employees.get(pilotID);
        if (pilot != null && pilot.getDrone() != null) {
            pilot.getDrone().setPilot(null);
        }

        Drone drone = stores.get(storeID).getDrone(droneID);
        if (drone != null && drone.getPilot() != null) {
            drone.getPilot().setDrone(null);
        }

        if (pilot != null && drone != null) {
            pilot.setDrone(drone);
            drone.setPilot(pilot);
            System.out.println("OK:change_completed");
            return true;
        }
        return false;
    }

    /**
     * Creates a customer if they don't exist
     * @param accountID - unique ID of customer
     * @param firstName - first name of customer
     * @param lastName - last name of customer
     * @param phone - phone number of customer
     * @param rating - rating of customer
     * @param credit - credit/money that customer has available to spend on groceries
     * @param x - x coordinate of customer
     * @param y - y coordinate of customer
     * @return true if customer is created, else false
     */
    private boolean make_customer(String accountID, String firstName, String lastName,
                                  String phone, String rating, String credit,
                                  String x, String y) {
        if (customers.containsKey(accountID)) {
            System.out.println("ERROR:customer_identifier_already_exists");
            return false;
        }
        Location location = new Location(x, y);
        if (map.locationExists(location)) {
            System.out.println("ERROR:location_already_taken");
            return false;
        }
        Customer customer = new Customer(accountID, firstName, lastName, phone, rating, credit, location);
        map.addLocation(location, customer);
        customers.put(customer.getAccountID(), customer);
        System.out.println("OK:change_completed");
        return true;
    }

    /**
     * Displays all customers
     */
    private void display_customers() {
        for (Map.Entry<String, Customer> customerEntry : customers.entrySet()) {
            System.out.println(customerEntry.getValue().toString());
            clock.incrementTime(1);
        }
        System.out.println("OK:display_completed");
    }

    /**
     * Creates an initial stub for an order if it doesn't exist
     * @param storeID - unique ID of store where order will be placed
     * @param orderID - unique ID of order
     * @param droneID - unique ID of drone that will carry order
     * @param customerID - unique ID of customer who is initiating the order
     * @return true is order is created, else false
     */
    private boolean start_order(String storeID, String orderID, String droneID, String customerID) {
        if (!stores.containsKey(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        if (stores.get(storeID).containsOrder(orderID)) {
            System.out.println("ERROR:order_identifier_already_exists");
            return false;
        }
        if (!stores.get(storeID).containsDrone(droneID)) {
            System.out.println("ERROR:drone_identifier_does_not_exist");
            return false;
        }
        if (!customers.containsKey(customerID)) {
            System.out.println("ERROR:customer_identifier_does_not_exist");
            return false;
        }
        boolean orderCreated = customers.get(customerID).startOrder(orderID, stores.get(storeID), droneID);
        if (orderCreated) System.out.println("OK:change_completed");
        return orderCreated;
    }

    /**
     * Displays all orders that a store has
     * @param storeID - unique ID of store that has orders
     */
    private void display_orders(String storeID) {
        if (!stores.containsKey(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return;
        }
        stores.get(storeID).displayOrders();
    }

    /**
     * Add an item to the designated order
     * @param storeID - unique ID of store that holds item
     * @param orderID - unique ID of order that will add the item
     * @param itemID - unique ID of item
     * @param quantity - quantity of item
     * @param price - unit price of item
     * @return true if item is added to order, else false
     */
    private boolean request_item(String storeID, String orderID, String itemID, String quantity, String price) {
        if (!stores.containsKey(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        if (!stores.get(storeID).containsOrder(orderID)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        if (!stores.get(storeID).containsItem(itemID)) {
            System.out.println("ERROR:item_identifier_does_not_exist");
            return false;
        }
        if (stores.get(storeID).getOrder(orderID).containsItem(itemID)) {
            System.out.println("ERROR:item_already_ordered");
            return false;
        }
        Customer customer = customers.get(stores.get(storeID).getCustomerID(orderID));
        boolean itemRequested = customer.requestItem(orderID, itemID, quantity, price);
        if (itemRequested) System.out.println("OK:change_completed");
        return itemRequested;
    }

    /**
     * Customer purchases an order they placed at a store and system information is updated accordingly
     * @param storeID - unique ID of store where order is to be purchased from
     * @param orderID - unique ID of order to be purchased
     * @return true if order is purchased, else false
     */
    private boolean purchase_order(String storeID, String orderID) {
        if (!stores.containsKey(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        if (!stores.get(storeID).containsOrder(orderID)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        Customer customer = customers.get(stores.get(storeID).getCustomerID(orderID));
        boolean orderPurchased = customer.purchaseOrder(orderID);
        if (orderPurchased) System.out.println("OK:change_completed");
        return orderPurchased;
    }

    /**
     * Customer cancels an order they placed at a store
     * @param storeID - unique ID of store where order is to be cancelled from
     * @param orderID - unique ID of order to be cancelled
     * @return true if order is cancelled, else false
     */
    private boolean cancel_order(String storeID, String orderID) {
        if (!stores.containsKey(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        if (!stores.get(storeID).containsOrder(orderID)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        Store store = stores.get(storeID);
        Customer customer = customers.get(store.getCustomerID(orderID));
        boolean orderCanceled = customer.cancelOrder(storeID, orderID);
        if (orderCanceled) System.out.println("OK:change_completed");
        return orderCanceled;
    }

    /**
     * Transfers an order from one drone to another drone within the particular store that the order is made from
     * @param storeID - unique ID of store
     * @param orderID - unique ID of order
     * @param droneID - unique ID of new drone that order will be transferred to
     * @return true if order is transferred, else false
     */
    private boolean transfer_order(String storeID, String orderID, String droneID) {
        if (!stores.containsKey(storeID)) {
            System.out.println("ERROR:store_identifier_does_not_exist");
            return false;
        }
        if (!stores.get(storeID).containsOrder(orderID)) {
            System.out.println("ERROR:order_identifier_does_not_exist");
            return false;
        }
        Store store = stores.get(storeID);
        boolean isTransferred = store.transferOrder(orderID, droneID);
        if (isTransferred) System.out.println("OK:change_completed");
        return isTransferred;
    }

    /**
     * Displays information about three metrics for each store (purchases, overloads, and transfers)
     */
    private void display_efficiency() {
        for (Map.Entry<String, Store> storeEntry : stores.entrySet()) {
            System.out.println(storeEntry.getValue().getEfficiency());
            clock.incrementTime(1);
        }
        System.out.println("OK:display_completed");
    }

    /**
     * Displays the current time of the system
     */
    private void display_time() {
        clock.incrementTime(1);
        System.out.println(clock.toString());
    }
}