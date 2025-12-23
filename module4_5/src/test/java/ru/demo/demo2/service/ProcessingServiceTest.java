package ru.demo.demo2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.demo.demo2.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProcessingServiceTest {

    private ProcessingService processingService;

    @BeforeEach
    void setUp() {
        processingService = new ProcessingService();
    }

    @Test
    void testStartSorting_CreatesProcessingStage() {
        Receipt receipt = createTestReceipt();
        User operator = createTestUser();

        ProcessingStage stage = processingService.startSorting(receipt, operator);

        assertNotNull(stage);
        assertEquals(receipt, stage.getReceipt());
        assertEquals(ProcessingStage.Stage.sorting, stage.getStage());
        assertEquals(ProcessingStage.Status.in_progress, stage.getStatus());
        assertNotNull(stage.getStartTime());
        assertNull(stage.getEndTime());
    }

    @Test
    void testStartPressing_CreatesProcessingStage() {
        Receipt receipt = createTestReceipt();
        User operator = createTestUser();

        ProcessingStage stage = processingService.startPressing(receipt, operator);

        assertNotNull(stage);
        assertEquals(ProcessingStage.Stage.pressing, stage.getStage());
        assertEquals(ProcessingStage.Status.in_progress, stage.getStatus());
    }

    @Test
    void testStartShipping_CreatesProcessingStage() {
        Receipt receipt = createTestReceipt();
        User operator = createTestUser();

        ProcessingStage stage = processingService.startShipping(receipt, operator);

        assertNotNull(stage);
        assertEquals(ProcessingStage.Stage.shipping, stage.getStage());
        assertEquals(ProcessingStage.Status.in_progress, stage.getStatus());
    }

    @Test
    void testCompleteStage_ChangesStatusToCompleted() {
        Receipt receipt = createTestReceipt();
        User operator = createTestUser();
        
        ProcessingStage stage = processingService.startSorting(receipt, operator);
        assertEquals(ProcessingStage.Status.in_progress, stage.getStatus());
        assertNull(stage.getEndTime());

        processingService.completeStage(stage, operator);

        assertEquals(ProcessingStage.Status.completed, stage.getStatus());
        assertNotNull(stage.getEndTime());
        assertTrue(stage.getEndTime().isAfter(stage.getStartTime()) || 
                   stage.getEndTime().isEqual(stage.getStartTime()));
    }

    @Test
    void testCancelStage_ChangesStatusToCancelled() {
        Receipt receipt = createTestReceipt();
        User operator = createTestUser();
        
        ProcessingStage stage = processingService.startSorting(receipt, operator);
        
        processingService.cancelStage(stage);

        assertEquals(ProcessingStage.Status.cancelled, stage.getStatus());
    }

    @Test
    void testCreateShipment_Success() {
        ProcessingStage batch = createTestProcessingStage();
        User operator = createTestUser();
        String recipient = "ООО Переработка";
        BigDecimal weight = new BigDecimal("500.00");
        String doc = "ТТН-12345";

        Shipment shipment = processingService.createShipment(batch, recipient, weight, doc, operator);

        assertNotNull(shipment);
        assertEquals(batch, shipment.getProcessedBatch());
        assertEquals(recipient, shipment.getRecipient());
        assertEquals(weight, shipment.getWeightKg());
        assertEquals(doc, shipment.getTransportDoc());
        assertNotNull(shipment.getProcessedBatch().getStartTime());
    }

    @Test
    void testCreateShipment_WithNullTransportDoc() {
        ProcessingStage batch = createTestProcessingStage();
        User operator = createTestUser();

        Shipment shipment = processingService.createShipment(
            batch, "Покупатель", new BigDecimal("100"), null, operator);

        assertNotNull(shipment);
        assertNull(shipment.getTransportDoc());
    }

    @Test
    void testCreateShipment_WithEmptyRecipient_ShouldStillCreate() {
        ProcessingStage batch = createTestProcessingStage();
        User operator = createTestUser();

        Shipment shipment = processingService.createShipment(
            batch, "", new BigDecimal("100"), "DOC", operator);

        assertNotNull(shipment);
        assertEquals("", shipment.getRecipient());
    }

    @Test
    void testUpdateShipment() {
        Shipment shipment = new Shipment();
        shipment.setId(1);
        shipment.setRecipient("Покупатель");
        shipment.setWeightKg(new BigDecimal("200"));

        assertDoesNotThrow(() -> processingService.updateShipment(shipment));
    }

    @Test
    void testDeleteShipment() {
        Shipment shipment = new Shipment();
        shipment.setId(1);

        assertDoesNotThrow(() -> processingService.deleteShipment(shipment));
    }

    @Test
    void testDeleteStage() {
        ProcessingStage stage = new ProcessingStage();
        stage.setId(1);

        assertDoesNotThrow(() -> processingService.deleteStage(stage));
    }

    @Test
    void testMultipleStagesForSameReceipt() {
        Receipt receipt = createTestReceipt();
        User operator = createTestUser();

        ProcessingStage sorting = processingService.startSorting(receipt, operator);
        ProcessingStage pressing = processingService.startPressing(receipt, operator);
        ProcessingStage shipping = processingService.startShipping(receipt, operator);

        assertNotNull(sorting);
        assertNotNull(pressing);
        assertNotNull(shipping);
        
        assertEquals(receipt, sorting.getReceipt());
        assertEquals(receipt, pressing.getReceipt());
        assertEquals(receipt, shipping.getReceipt());
        
        assertNotEquals(sorting.getStage(), pressing.getStage());
        assertNotEquals(pressing.getStage(), shipping.getStage());
    }

    @Test
    void testStageCompletionTime() throws InterruptedException {
        Receipt receipt = createTestReceipt();
        User operator = createTestUser();
        
        ProcessingStage stage = processingService.startSorting(receipt, operator);
        LocalDateTime startTime = stage.getStartTime();
        
        Thread.sleep(10); // Небольшая задержка
        
        processingService.completeStage(stage, operator);
        LocalDateTime endTime = stage.getEndTime();

        assertNotNull(endTime);
        assertTrue(endTime.isAfter(startTime) || endTime.isEqual(startTime));
    }

    // Вспомогательные методы для создания тестовых объектов

    private Receipt createTestReceipt() {
        Receipt receipt = new Receipt();
        receipt.setId(1);
        
        Supplier supplier = new Supplier();
        supplier.setId(1);
        supplier.setName("Тестовый поставщик");
        receipt.setSupplier(supplier);
        
        WasteType wasteType = new WasteType();
        wasteType.setId(1);
        wasteType.setName("Пластик");
        receipt.setWasteType(wasteType);
        
        receipt.setWeightKg(new BigDecimal("1000.00"));
        receipt.setDatetime(LocalDateTime.now());
        
        User operator = createTestUser();
        receipt.setOperator(operator);
        
        return receipt;
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setLogin("operator1");
        user.setRole(User.UserRole.operator);
        return user;
    }

    private ProcessingStage createTestProcessingStage() {
        ProcessingStage stage = new ProcessingStage();
        stage.setId(1);
        stage.setReceipt(createTestReceipt());
        stage.setStage(ProcessingStage.Stage.shipping);
        stage.setStatus(ProcessingStage.Status.completed);
        stage.setStartTime(LocalDateTime.now().minusHours(2));
        stage.setEndTime(LocalDateTime.now());
        return stage;
    }
}
