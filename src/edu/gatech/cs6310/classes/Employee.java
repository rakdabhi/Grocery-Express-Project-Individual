package edu.gatech.cs6310.classes;

public abstract class Employee extends User {
    protected String taxID;

    public Employee(String accountID, String firstName, String lastName, String phone, String taxID) {
        super(accountID, firstName, lastName, phone);
        this.taxID = taxID;

    }

    public String getTaxID() {
        return taxID;
    }

    public void setTaxID(String taxID) {
        this.taxID = taxID;
    }
}
