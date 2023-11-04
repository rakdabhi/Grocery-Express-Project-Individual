package edu.gatech.cs6310.classes;

public class Line {
    private String lineID;
    private Item item;
    private int quantity;

    private int price;
    private int lineWeight;
    private int lineCost;

    public Line(Item item, int quantity, int price) {
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.lineID = item.getItemID();
        this.lineWeight = item.getWeight() * quantity;
        this.lineCost = price * quantity;
    }

    public Item getItem() {
        return this.item;
    }

    public String getLineID() {
        return this.lineID;
    }

    public int getLineWeight() {
        return this.lineWeight;
    }

    public int getLineCost() {
        return this.lineCost;
    }

    /**
     * Information about this line item
     * @return String representing information about this line item
     */
    @Override
    public String toString() {
        return "item_name:" + this.item.getName() +
              ",total_quantity:" + this.quantity +
              ",total_cost:" + this.lineCost +
              ",total_weight:" + this.lineWeight;
    }
}
