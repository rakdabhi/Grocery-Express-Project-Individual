package edu.gatech.cs6310.classes;

public class Item {
    private String name;
    private int weight;


    public Item(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public Item(String name, String weight) {
        this(name, Integer.parseInt(weight));
    }

    public String getItemID() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Information about this item
     * @return String representing information about this item
     */
    @Override
    public String toString() {
        return this.name + "," + this.weight;
    }
}
