package com.dispatch.model;

public class EmergencyRequest {
    private final String requestId;
    private final String patientId;
    private final String description;
    private final EmergencyPriority priority;
    private final double pickupX;
    private final double pickupY;
    private final String destinationHospital;
    private Ambulance assignedAmbulance;
    private double estimatedArrivalTimeMinutes;

    public EmergencyRequest(String requestId, String patientId, String description, 
                            EmergencyPriority priority, double pickupX, double pickupY, String destinationHospital) {
        this.requestId = requestId;
        this.patientId = patientId;
        this.description = description;
        this.priority = priority;
        this.pickupX = pickupX;
        this.pickupY = pickupY;
        this.destinationHospital = destinationHospital;
    }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public String getPatientId() { return patientId; }
    public EmergencyPriority getPriority() { return priority; }
    public double getPickupX() { return pickupX; }
    public double getPickupY() { return pickupY; }
    public String getDestinationHospital() { return destinationHospital; }
    public Ambulance getAssignedAmbulance() { return assignedAmbulance; }
    public void setAssignedAmbulance(Ambulance assignedAmbulance) { this.assignedAmbulance = assignedAmbulance; }
    public double getEstimatedArrivalTimeMinutes() { return estimatedArrivalTimeMinutes; }
    public void setEstimatedArrivalTimeMinutes(double eta) { this.estimatedArrivalTimeMinutes = eta; }
}
