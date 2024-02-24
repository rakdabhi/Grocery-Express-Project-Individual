package edu.gatech.cs6310.classes;

import java.util.Objects;

public class Location implements Comparable<Location> {
    private int x, y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Location(String x, String y) {
        this(Integer.parseInt(x), Integer.parseInt(y));
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    /**
     * Checks if another location is equal to this location
     * @param o - Object/Location to compare this location to
     * @return true if locations are equal, else false
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Location thatLocation)) {
            return false;
        }
        return this.x == thatLocation.getX() && this.y == thatLocation.getY();
    }

    /**
     * Compares this location to another given location to check if
     * this location is greater than, less than, or equal to the other location
     * @param thatLocation the object to be compared.
     * @return 1 if this location is greater than, -1 if this location is less than,
     * else 0 if this location is equal to
     */
    @Override
    public int compareTo(Location thatLocation) {
        if (this.x != thatLocation.getX()) {
            return Integer.compare(this.x, thatLocation.getX());
        } else {
            return Integer.compare(this.y, thatLocation.getY());
        }
    }

    /**
     * Overridden hashcode function to ensure that locations with the same x,y coordinates have the same hash
     * @return hashcode of this location based on the x,y coordinates
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Function returning this location in String format
     * @return Location written in String format as "(x,y)"
     */
    @Override
    public String toString() {
        return "(" + this.getX() + "," + this.getY() + ")";
    }
}
