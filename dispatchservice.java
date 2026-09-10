package com.dispatch.service;

import com.dispatch.exception.InvalidRequestException;
import com.dispatch.model.*;
import java.util.*;

public class DispatchService {
    private final List<Ambulance> ambulances = new ArrayList<>();
    private final List<EmergencyRequest> historyLog = new ArrayList<>();
    
    // Priority Queue to always favor highest threat levels (Critical -> High -> Moderate -> Normal)
    private final PriorityQueue<EmergencyRequest> waitingQueue = new PriorityQueue<>(
        Comparator.comparingInt(r -> r.getPriority().getRank())
    );

    public void registerAmbulance(Ambulance ambulance) {
        ambulances.add(ambulance);
    }

    public synchronized void submitEmergencyRequest(EmergencyRequest request) {
        validateRequest(request);
        
        Optional<Ambulance> targetAmbulance = findBestAvailableAmbulance(request);
        
        if (targetAmbulance.isPresent()) {
            dispatchAmbulance(request, targetAmbulance.get());
        } else {
            waitingQueue.add(request);
        }
    }

    private void validateRequest(EmergencyRequest r) {
        if (r.getRequestId() == null || r.getPatientId() == null || r.getPriority() == null) {
            throw new InvalidRequestException("Emergency record metrics cannot contain null variants.");
        }
    }

    private Optional<Ambulance> findBestAvailableAmbulance(EmergencyRequest request) {
        return ambulances.stream()
            .filter(a -> a.getState() == AmbulanceState.AVAILABLE)
            .filter(a -> matchTypeRequirements(request.getPriority(), a.getType()))
            .min(Comparator.comparingDouble(a -> calculateDistance(a, request)));
    }

    private boolean matchTypeRequirements(EmergencyPriority priority, AmbulanceType type) {
        if (priority == EmergencyPriority.CRITICAL) return type == AmbulanceType.ICU;
        if (priority == EmergencyPriority.HIGH) return type == AmbulanceType.ADVANCED_LIFE_SUPPORT;
        return true; // Moderate & Normal handleable by basic assets
    }

    private double calculateDistance(Ambulance a, EmergencyRequest r) {
        return Math.sqrt(Math.pow(a.getCurrentLocationX() - r.getPickupX(), 2) + 
                         Math.pow(a.getCurrentLocationY() - r.getPickupY(), 2));
    }

    private void dispatchAmbulance(EmergencyRequest request, Ambulance ambulance) {
        ambulance.setState(AmbulanceState.DISPATCHED);
        request.setAssignedAmbulance(ambulance);
        
        double distance = calculateDistance(ambulance, request);
        double averageSpeedKmH = 50.0; 
        request.setEstimatedArrivalTimeMinutes((distance / averageSpeedKmH) * 60.0);
        
        historyLog.add(request);
    }

    public synchronized void updateAmbulanceState(String ambulanceId, AmbulanceState newState) {
        Ambulance ambulance = ambulances.stream()
            .filter(a -> a.getId().equals(ambulanceId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Asset ID tracking signature not recognized."));

        ambulance.setState(newState);

        // When lifecycle returns to available, look into discharging wait queues
        if (newState == AmbulanceState.AVAILABLE && !waitingQueue.isEmpty()) {
            processWaitingQueue();
        }
    }

    private void processWaitingQueue() {
        List<EmergencyRequest> unresolved = new ArrayList<>();
        while (!waitingQueue.isEmpty()) {
            EmergencyRequest nextInLine = waitingQueue.poll();
            Optional<Ambulance> ambulance = findBestAvailableAmbulance(nextInLine);
            
            if (ambulance.isPresent()) {
                dispatchAmbulance(nextInLine, ambulance.get());
            } else {
                unresolved.add(nextInLine);
            }
        }
        waitingQueue.addAll(unresolved);
    }

    public List<EmergencyRequest> getHistoryLog() { return new ArrayList<>(historyLog); }
    public Queue<EmergencyRequest> getWaitingQueue() { return waitingQueue; }
    public List<Ambulance> getAmbulances() { return ambulances; }
}
