package ru.demo.demo2.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class ShipmentTest {
    
    @Test
    void shouldCreateShipment() {
        ProcessingStage stage = new ProcessingStage();
        stage.setId(3);
        
        Shipment shipment = new Shipment();
        shipment.setProcessedBatch(stage);
        shipment.setRecipient("ООО Полимер");
        shipment.setWeightKg(new BigDecimal("145.00"));
        shipment.setTransportDoc("ТН-2025-001");
        
        assertEquals(stage, shipment.getProcessedBatch());
        assertEquals("ООО Полимер", shipment.getRecipient());
        assertEquals(new BigDecimal("145.00"), shipment.getWeightKg());
        assertEquals("ТН-2025-001", shipment.getTransportDoc());
    }
    
    @Test
    void shouldGetProcessedBatchId() {
        ProcessingStage stage = new ProcessingStage();
        stage.setId(7);
        
        Shipment shipment = new Shipment();
        shipment.setProcessedBatch(stage);
        
        assertEquals(7, shipment.getProcessedBatchId());
    }
    
    @Test
    void shouldReturnNullWhenNoProcessedBatch() {
        Shipment shipment = new Shipment();
        
        assertNull(shipment.getProcessedBatchId());
    }
}
