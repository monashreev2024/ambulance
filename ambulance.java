package com.dispatch.model;

public class Ambulance {
    private final String id;
    private final AmbulanceType type;
    private AmbulanceState state;
    private final String driverDetails;
    private double currentLocationX;
    private double currentLocationY;

    public Ambulance(String id, AmbulanceType type, String driverDetails, double x, double y) {
        this.id = id;
        this.type = type;
        this.driverDetails = driverDetails;
        this.state = AmbulanceState.AVAILABLE;
        this.currentLocationX = x;
        this.currentLocationY = y;
    }

    // Getters and Setters
    public String getId() { return id; }
    public AmbulanceType getType() { return type; }
    public AmbulanceState getState() { return state; }
    public void setState(AmbulanceState state) { this.state = state; }
    public String getDriverDetails() { return driverDetails; }
    public double getCurrentLocationX() { return currentLocationX; }
    public double getCurrentLocationY() { return currentLocationY; }
    
    public void updateLocation(double x, double y) {
        this.currentLocationX = x;
        this.currentLocationY = y;
    }
}
