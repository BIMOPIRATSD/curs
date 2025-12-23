package ru.demo.demo2.edge;

import org.junit.jupiter.api.Test;
import ru.demo.demo2.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EdgeCaseTest {

    @Test
    void testStorageCellWithZeroCapacity() {
        StorageCell cell = new StorageCell();
        cell.setMaxCapacityKg(BigDecimal.ZERO);
        cell.setCurrentLoadKg(BigDecimal.ZERO);

        assertEquals(0.0, cell.getLoadPercentage());
    }

    @Test
    void testStorageCellOverload() {
        StorageCell cell = new StorageCell();
        cell.setMaxCapacityKg(new BigDecimal("1000"));
        cell.setCurrentLoadKg(new BigDecimal("1500"));

        assertTrue(cell.getLoadPercentage() > 100.0);
        assertEquals(150.0, cell.getLoadPercentage(), 0.01);
    }

    @Test
    void testReceiptWithVeryLargeWeight() {
        Receipt receipt = new Receipt();
        receipt.setWeightKg(new BigDecimal("999999999.99"));

        assertEquals(new BigDecimal("999999999.99"), receipt.getWeightKg());
    }

    @Test
    void testReceiptWithVerySmallWeight() {
        Receipt receipt = new Receipt();
        receipt.setWeightKg(new BigDecimal("0.01"));

        assertEquals(new BigDecimal("0.01"), receipt.getWeightKg());
    }

    @Test
    void testWasteTypeWithNullDensity() {
        WasteType wasteType = new WasteType();
        wasteType.setName("Тест");
        wasteType.setDensityKgPerM3(null);

        assertNull(wasteType.getDensityKgPerM3());
    }

    @Test
    void testSupplierWithVeryLongName() {
        String longName = "A".repeat(255); // Максимально длинное имя
        Supplier supplier = new Supplier();
        supplier.setName(longName);

        assertEquals(255, supplier.getName().length());
    }

    @Test
    void testSupplierWithMinimalData() {
        Supplier supplier = new Supplier();
        supplier.setName("A");
        supplier.setType("F");

        assertNotNull(supplier);
        assertNull(supplier.getPhone());
        assertNull(supplier.getEmail());
    }

    @Test
    void testProcessingStageImmediateCompletion() {
        ProcessingStage stage = new ProcessingStage();
        stage.setStage(ProcessingStage.Stage.sorting);
        
        LocalDateTime startTime = stage.getStartTime();
        stage.complete();
        LocalDateTime endTime = stage.getEndTime();

        assertNotNull(startTime);
        assertNotNull(endTime);
        // Время завершения должно быть >= времени начала
        assertTrue(!endTime.isBefore(startTime));
    }

    @Test
    void testShipmentWithZeroWeight() {
        Shipment shipment = new Shipment();
        shipment.setWeightKg(BigDecimal.ZERO);
        shipment.setRecipient("Покупатель");

        assertEquals(BigDecimal.ZERO, shipment.getWeightKg());
    }

    @Test
    void testShipmentWithNullTransportDoc() {
        Shipment shipment = new Shipment();
        shipment.setRecipient("Покупатель");
        shipment.setWeightKg(new BigDecimal("100"));
        shipment.setTransportDoc(null);

        assertNull(shipment.getTransportDoc());
        assertNotNull(shipment.getRecipient());
    }

    @Test
    void testAuditLogWithNullOldValue() {
        User user = new User();
        user.setLogin("admin");
        
        AuditLog log = new AuditLog(user, "CREATE", "receipt", 1, "новое значение");
        log.setOldValue(null);

        assertNull(log.getOldValue());
        assertNotNull(log.getNewValue());
    }

    @Test
    void testAuditLogWithVeryLongValues() {
        User user = new User();
        user.setLogin("admin");
        
        String longValue = "X".repeat(500);
        AuditLog log = new AuditLog(user, "UPDATE", "receipt", 1, longValue);

        assertEquals(500, log.getNewValue().length());
    }

    @Test
    void testReceiptNullStorageCell() {
        Receipt receipt = new Receipt();
        receipt.setStorageCell(null);

        assertEquals("-", receipt.getStorageCellCode());
    }

    @Test
    void testReceiptNullSupplier() {
        Receipt receipt = new Receipt();
        receipt.setSupplier(null);

        assertThrows(NullPointerException.class, () -> {
            receipt.getSupplierName();
        });
    }

    @Test
    void testUserPasswordHash() {
        User user = new User();
        user.setLogin("testuser");
        user.setPasswordHash("hashedpassword123");

        assertEquals("hashedpassword123", user.getPasswordHash());
    }

    @Test
    void testAllUserRoles() {
        User.UserRole[] roles = User.UserRole.values();
        
        assertEquals(3, roles.length);
        assertTrue(roles.length >= 3);
        
        User adminUser = new User();
        adminUser.setRole(User.UserRole.admin);
        assertEquals("Администратор", adminUser.getRoleDisplayName());
        
        User operatorUser = new User();
        operatorUser.setRole(User.UserRole.operator);
        assertEquals("Оператор", operatorUser.getRoleDisplayName());
        
        User viewerUser = new User();
        viewerUser.setRole(User.UserRole.viewer);
        assertEquals("Наблюдатель", viewerUser.getRoleDisplayName());
    }

    @Test
    void testAllProcessingStages() {
        ProcessingStage.Stage[] stages = ProcessingStage.Stage.values();
        
        assertEquals(3, stages.length);
        
        ProcessingStage sorting = new ProcessingStage();
        sorting.setStage(ProcessingStage.Stage.sorting);
        assertEquals("Сортировка", sorting.getStageDisplayName());
        
        ProcessingStage pressing = new ProcessingStage();
        pressing.setStage(ProcessingStage.Stage.pressing);
        assertEquals("Прессовка", pressing.getStageDisplayName());
        
        ProcessingStage shipping = new ProcessingStage();
        shipping.setStage(ProcessingStage.Stage.shipping);
        assertEquals("Отгрузка", shipping.getStageDisplayName());
    }

    @Test
    void testAllProcessingStatuses() {
        ProcessingStage.Status[] statuses = ProcessingStage.Status.values();
        
        assertEquals(3, statuses.length);
        
        ProcessingStage stage = new ProcessingStage();
        
        // in_progress
        stage.setStatus(ProcessingStage.Status.in_progress);
        assertEquals("В процессе", stage.getStatusDisplayName());
        
        // completed
        stage.setStatus(ProcessingStage.Status.completed);
        assertEquals("Завершён", stage.getStatusDisplayName());
        
        // cancelled
        stage.setStatus(ProcessingStage.Status.cancelled);
        assertEquals("Отменён", stage.getStatusDisplayName());
    }

    @Test
    void testAllAuditLogActions() {
        // Test all possible action strings
        String[] actions = {"CREATE", "UPDATE", "DELETE"};
        
        assertTrue(actions.length >= 3);
        
        User user = new User();
        user.setLogin("admin");
        
        AuditLog createLog = new AuditLog(user, "CREATE", "test", 1, "value");
        assertEquals("Создание", createLog.getActionDisplayName());
        
        AuditLog updateLog = new AuditLog(user, "UPDATE", "test", 1, "value");
        assertEquals("Изменение", updateLog.getActionDisplayName());
        
        AuditLog deleteLog = new AuditLog(user, "DELETE", "test", 1, "value");
        assertEquals("Удаление", deleteLog.getActionDisplayName());
    }

    @Test
    void testStorageCellCodeWithSpecialCharacters() {
        StorageCell cell = new StorageCell();
        
        String[] specialCodes = {
            "A-01",
            "ZONE_1_CELL_5",
            "X1Y2Z3",
            "Ячейка-А1",
            "CELL#123"
        };
        
        for (String code : specialCodes) {
            cell.setCode(code);
            assertEquals(code, cell.getCode());
        }
    }

    @Test
    void testDecimalPrecisionInCalculations() {
        StorageCell cell = new StorageCell();
        cell.setMaxCapacityKg(new BigDecimal("3.00"));
        cell.setCurrentLoadKg(new BigDecimal("1.00"));

        double percentage = cell.getLoadPercentage();
        // Method uses 2 decimal places rounding, so result is 33.33
        assertEquals(33.33, percentage, 0.01);
    }

    @Test
    void testMultipleProcessingStagesForSameReceipt() {
        Receipt receipt = new Receipt();
        receipt.setId(1);
        
        ProcessingStage stage1 = new ProcessingStage();
        stage1.setReceipt(receipt);
        stage1.setStage(ProcessingStage.Stage.sorting);
        
        ProcessingStage stage2 = new ProcessingStage();
        stage2.setReceipt(receipt);
        stage2.setStage(ProcessingStage.Stage.pressing);
        
        ProcessingStage stage3 = new ProcessingStage();
        stage3.setReceipt(receipt);
        stage3.setStage(ProcessingStage.Stage.shipping);
        
        assertEquals(receipt, stage1.getReceipt());
        assertEquals(receipt, stage2.getReceipt());
        assertEquals(receipt, stage3.getReceipt());
        
        assertNotEquals(stage1.getStage(), stage2.getStage());
        assertNotEquals(stage2.getStage(), stage3.getStage());
    }

    @Test
    void testNegativeWeightHandling() {
        Receipt receipt = new Receipt();
        
        // Система должна принять отрицательное значение (валидация на уровне контроллера)
        BigDecimal negativeWeight = new BigDecimal("-100");
        receipt.setWeightKg(negativeWeight);
        
        assertEquals(negativeWeight, receipt.getWeightKg());
        assertTrue(receipt.getWeightKg().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    void testVeryLongEmailAddress() {
        Supplier supplier = new Supplier();
        String longEmail = "verylongemailaddresswithmanychars@verylongdomainnamewithmanychars.example.com";
        supplier.setEmail(longEmail);
        
        assertEquals(longEmail, supplier.getEmail());
    }

    @Test
    void testPhoneNumberWithMaxLength() {
        Supplier supplier = new Supplier();
        String longPhone = "+123456789012345"; // 15 digits + +
        supplier.setPhone(longPhone);
        
        assertEquals(longPhone, supplier.getPhone());
    }
}
