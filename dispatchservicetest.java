package com.dispatch.service;

import com.dispatch.exception.InvalidRequestException;
import com.dispatch.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DispatchServiceTest {
    private DispatchService service;

    @BeforeEach
    void init() {
        service = new DispatchService();
    }

    @Test
    void testCriticalPriorityDispatchesFirst() {
        Ambulance icuUnit = new Ambulance("AMB-ICU", AmbulanceType.ICU, "Dr. Driver", 0, 0);
        service.registerAmbulance(icuUnit);

        EmergencyRequest lowPriority = new EmergencyRequest("REQ-01", "P-101", "Minor Injury", 
            EmergencyPriority.NORMAL, 10, 10, "City General");
        EmergencyRequest criticalPriority = new EmergencyRequest("REQ-02", "P-102", "Cardiac Arrest", 
            EmergencyPriority.CRITICAL, 2, 2, "Heart Clinic");

        // Submit low priority first when no matching basic vehicles are free -> should queue
        service.submitEmergencyRequest(lowPriority);
        service.submitEmergencyRequest(criticalPriority);

        assertEquals(AmbulanceState.DISPATCHED, icuUnit.getState());
        assertEquals("REQ-02", service.getHistoryLog().get(0).getRequestId());
    }

    @Test
    void testInvalidRequestExceptionThrown() {
        EmergencyRequest damagedRecord = new EmergencyRequest(null, null, null, null, 0, 0, null);
        assertThrows(InvalidRequestException.class, () -> service.submitEmergencyRequest(damagedRecord));
    }
}
