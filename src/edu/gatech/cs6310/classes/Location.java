package edu.gatech.cs6310.classes;

public class Location implements Comparable<Location> {
    private int x, y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Location thatLocation)) {
            return false;
        }
        return this.x == thatLocation.getX() && this.y == thatLocation.getY();
    }

    @Override
    public int compareTo(Location thatLocation) {
        if (this.x != thatLocation.getX()) {
            return Integer.compare(this.x, thatLocation.getX());
        } else {
            return Integer.compare(this.y, thatLocation.getY());
        }
    }
}
