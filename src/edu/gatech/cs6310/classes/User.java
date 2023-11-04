package edu.gatech.cs6310.classes;

public abstract class User {
    protected String accountID;
    protected String firstName;
    protected String lastName;
    protected String phone;

    public User(String accountID, String firstName, String lastName, String phone) {
        this.accountID = accountID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getAccountID() {
        return this.accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return this.firstName + "_" + this.lastName;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
