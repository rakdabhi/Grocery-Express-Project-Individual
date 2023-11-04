package edu.gatech.cs6310.classes;

public class DronePilot extends Employee {
    private String licenseID;
    private int experience;
    private Drone drone;

    public DronePilot(String accountID, String firstName, String lastName, String phone,
                      String taxID, String licenseID, int experience) {
        super(accountID, firstName, lastName, phone, taxID);
        this.licenseID = licenseID;
        this.experience = experience;
    }

    public DronePilot(String accountID, String firstName, String lastName, String phone,
                      String taxID, String licenseID, String experience) {
        this(accountID, firstName, lastName, phone, taxID, licenseID, Integer.parseInt(experience));
    }

    public String getLicenseID() {
        return licenseID;
    }

    public void setLicenseID(String licenseID) {
        this.licenseID = licenseID;
    }

    public int getExperience() {
        return experience;
    }

    /**
     * Increments the experience of this drone pilot after a successful delivery
     */
    public void incrementExperience() {
        this.experience += 1;
    }

    public Drone getDrone() {
        return drone;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }

    /**
     * Information about this drone pilot
     * @return String representing information about this drone pilot
     */
    @Override
    public String toString() {
        return "name:" + this.getFullName() +
              ",phone:" + this.phone +
              ",taxID:" + this.taxID +
              ",licenseID:" + this.licenseID +
              ",experience:" + this.experience;
    }
}
