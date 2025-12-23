package ru.demo.demo2.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import ru.demo.demo2.model.*;
import ru.demo.demo2.util.ValidationUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ModelIntegrationTest {

    private Supplier supplier;
    private WasteType wasteType;
    private StorageCell storageCell;
    private User operator;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setId(1);
        supplier.setName("Тестовый поставщик");
        supplier.setType("L");
        supplier.setPhone("+79991234567");
        supplier.setEmail("test@supplier.com");

        wasteType = new WasteType();
        wasteType.setId(1);
        wasteType.setName("Пластик ПЭТ");
        wasteType.setDensityKgPerM3(new BigDecimal("1380.00"));
        wasteType.setRecyclable(true);

        storageCell = new StorageCell();
        storageCell.setId(1);
        storageCell.setCode("A-01");
        storageCell.setMaxCapacityKg(new BigDecimal("5000.00"));
        storageCell.setCurrentLoadKg(new BigDecimal("1000.00"));

        operator = new User();
        operator.setId(1);
        operator.setLogin("operator1");
        operator.setRole(User.UserRole.operator);
    }

    @Test
    void testCompleteReceiptWorkflow() {
        // Создание приёмки
        Receipt receipt = new Receipt();
        receipt.setSupplier(supplier);
        receipt.setWasteType(wasteType);
        receipt.setStorageCell(storageCell);
        receipt.setWeightKg(new BigDecimal("500.00"));
        receipt.setOperator(operator);

        // Проверка связей
        assertNotNull(receipt.getSupplier());
        assertNotNull(receipt.getWasteType());
        assertNotNull(receipt.getStorageCell());
        assertNotNull(receipt.getOperator());

        // Проверка методов получения данных
        assertEquals("Тестовый поставщик", receipt.getSupplierName());
        assertEquals("Пластик ПЭТ", receipt.getWasteTypeName());
        assertEquals("A-01", receipt.getStorageCellCode());
        assertEquals("operator1", receipt.getOperatorLogin());
    }

    @Test
    void testProcessingWorkflow() {
        // Создание приёмки
        Receipt receipt = new Receipt();
        receipt.setId(1);
        receipt.setSupplier(supplier);
        receipt.setWasteType(wasteType);
        receipt.setWeightKg(new BigDecimal("1000.00"));
        receipt.setOperator(operator);
        receipt.setDatetime(LocalDateTime.now());

        // Создание этапа сортировки
        ProcessingStage sorting = new ProcessingStage();
        sorting.setReceipt(receipt);
        sorting.setStage(ProcessingStage.Stage.sorting);

        assertEquals(ProcessingStage.Status.in_progress, sorting.getStatus());
        assertNotNull(sorting.getStartTime());
        assertNull(sorting.getEndTime());

        // Завершение сортировки
        sorting.complete();
        assertEquals(ProcessingStage.Status.completed, sorting.getStatus());
        assertNotNull(sorting.getEndTime());

        // Создание этапа прессовки
        ProcessingStage pressing = new ProcessingStage();
        pressing.setReceipt(receipt);
        pressing.setStage(ProcessingStage.Stage.pressing);

        assertNotEquals(sorting.getStage(), pressing.getStage());
        assertEquals(receipt, pressing.getReceipt());
    }

    @Test
    void testShipmentWorkflow() {
        // Создание завершённого этапа обработки
        Receipt receipt = new Receipt();
        receipt.setId(1);
        receipt.setSupplier(supplier);
        receipt.setWasteType(wasteType);
        receipt.setWeightKg(new BigDecimal("1000.00"));
        receipt.setOperator(operator);

        ProcessingStage stage = new ProcessingStage();
        stage.setId(1);
        stage.setReceipt(receipt);
        stage.setStage(ProcessingStage.Stage.shipping);
        stage.complete();

        // Создание отгрузки
        Shipment shipment = new Shipment();
        shipment.setProcessedBatch(stage);
        shipment.setRecipient("ООО Переработка");
        shipment.setWeightKg(new BigDecimal("800.00"));
        shipment.setTransportDoc("ТТН-12345");

        assertEquals(stage, shipment.getProcessedBatch());
        assertEquals("ООО Переработка", shipment.getRecipient());
        assertTrue(shipment.getWeightKg().compareTo(receipt.getWeightKg()) <= 0);
    }

    @Test
    void testStorageCapacityManagement() {
        // Начальная загрузка
        assertEquals(20.0, storageCell.getLoadPercentage(), 0.01);

        // Добавление новой приёмки
        Receipt receipt1 = new Receipt();
        receipt1.setStorageCell(storageCell);
        receipt1.setWeightKg(new BigDecimal("500.00"));

        // Обновление загрузки ячейки
        BigDecimal newLoad = storageCell.getCurrentLoadKg().add(receipt1.getWeightKg());
        storageCell.setCurrentLoadKg(newLoad);

        assertEquals(new BigDecimal("1500.00"), storageCell.getCurrentLoadKg());
        assertEquals(30.0, storageCell.getLoadPercentage(), 0.01);

        // Проверка, что не превышена вместимость
        assertTrue(storageCell.getCurrentLoadKg().compareTo(storageCell.getMaxCapacityKg()) <= 0);
    }

    @Test
    void testAuditLogCreation() {
        AuditLog log = new AuditLog(operator, "CREATE", "receipt", 1, "100 кг");

        assertEquals(operator, log.getUser());
        assertEquals("operator1", log.getUserLogin());
        assertEquals("CREATE", log.getAction());
        assertEquals("Создание", log.getActionDisplayName());
        assertEquals("receipt", log.getEntityType());
        assertEquals(1, log.getEntityId());
        assertEquals("100 кг", log.getNewValue());
        assertNotNull(log.getTimestamp());
    }

    @Test
    void testDataValidationIntegration() {
        // Валидация данных поставщика
        assertTrue(ValidationUtil.isNotEmpty(supplier.getName()));
        assertTrue(ValidationUtil.isValidEmail(supplier.getEmail()));
        assertTrue(ValidationUtil.isValidPhone(supplier.getPhone()));

        // Валидация данных приёмки
        BigDecimal weight = new BigDecimal("500.00");
        assertTrue(ValidationUtil.isValidPositiveDecimal(weight.toString()));

        // Валидация вместимости ячейки
        assertTrue(ValidationUtil.isValidPositiveDecimal(storageCell.getMaxCapacityKg().toString()));
        assertTrue(ValidationUtil.isValidNonNegativeDecimal(storageCell.getCurrentLoadKg().toString()));
    }

    @Test
    void testMultipleReceiptsForSameSupplier() {
        Receipt receipt1 = new Receipt();
        receipt1.setId(1);
        receipt1.setSupplier(supplier);
        receipt1.setWasteType(wasteType);
        receipt1.setWeightKg(new BigDecimal("100.00"));
        receipt1.setOperator(operator);

        Receipt receipt2 = new Receipt();
        receipt2.setId(2);
        receipt2.setSupplier(supplier);
        receipt2.setWasteType(wasteType);
        receipt2.setWeightKg(new BigDecimal("200.00"));
        receipt2.setOperator(operator);

        // Один поставщик, разные приёмки
        assertEquals(receipt1.getSupplierName(), receipt2.getSupplierName());
        assertNotEquals(receipt1.getId(), receipt2.getId());
        
        // Общий вес от поставщика
        BigDecimal totalWeight = receipt1.getWeightKg().add(receipt2.getWeightKg());
        assertEquals(new BigDecimal("300.00"), totalWeight);
    }

    @Test
    void testProcessingStageStatusTransitions() {
        Receipt receipt = new Receipt();
        receipt.setId(1);
        
        ProcessingStage stage = new ProcessingStage();
        stage.setReceipt(receipt);
        stage.setStage(ProcessingStage.Stage.sorting);

        // В процессе -> Завершён
        assertEquals(ProcessingStage.Status.in_progress, stage.getStatus());
        stage.complete();
        assertEquals(ProcessingStage.Status.completed, stage.getStatus());

        // Попытка повторного завершения (должен остаться завершённым)
        stage.complete();
        assertEquals(ProcessingStage.Status.completed, stage.getStatus());

        // Отмена завершённого этапа
        ProcessingStage stage2 = new ProcessingStage();
        stage2.setReceipt(receipt);
        stage2.setStage(ProcessingStage.Stage.pressing);
        
        stage2.setStatus(ProcessingStage.Status.cancelled);
        assertEquals(ProcessingStage.Status.cancelled, stage2.getStatus());
    }

    @Test
    void testWasteTypeDensityCalculations() {
        // Расчёт объёма по весу и плотности
        BigDecimal weightKg = new BigDecimal("1380.00"); // вес = плотность, значит 1 м³
        BigDecimal densityKgPerM3 = wasteType.getDensityKgPerM3();

        BigDecimal volumeM3 = weightKg.divide(densityKgPerM3, 2, java.math.RoundingMode.HALF_UP);
        assertEquals(new BigDecimal("1.00"), volumeM3);

        // Для 690 кг должно быть 0.5 м³
        BigDecimal weight2 = new BigDecimal("690.00");
        BigDecimal volume2 = weight2.divide(densityKgPerM3, 2, java.math.RoundingMode.HALF_UP);
        assertEquals(new BigDecimal("0.50"), volume2);
    }

    @Test
    void testComplexBusinessScenario() {
        // Сценарий: приём -> сортировка -> прессовка -> отгрузка
        
        // 1. Приёмка отходов
        Receipt receipt = new Receipt();
        receipt.setId(1);
        receipt.setSupplier(supplier);
        receipt.setWasteType(wasteType);
        receipt.setStorageCell(storageCell);
        receipt.setWeightKg(new BigDecimal("1000.00"));
        receipt.setOperator(operator);
        receipt.setDatetime(LocalDateTime.now());

        // 2. Начало сортировки
        ProcessingStage sorting = new ProcessingStage();
        sorting.setId(1);
        sorting.setReceipt(receipt);
        sorting.setStage(ProcessingStage.Stage.sorting);
        assertEquals(ProcessingStage.Status.in_progress, sorting.getStatus());

        // 3. Завершение сортировки, начало прессовки
        sorting.complete();
        ProcessingStage pressing = new ProcessingStage();
        pressing.setId(2);
        pressing.setReceipt(receipt);
        pressing.setStage(ProcessingStage.Stage.pressing);

        // 4. Завершение прессовки, подготовка к отгрузке
        pressing.complete();
        ProcessingStage shipping = new ProcessingStage();
        shipping.setId(3);
        shipping.setReceipt(receipt);
        shipping.setStage(ProcessingStage.Stage.shipping);
        shipping.complete();

        // 5. Создание отгрузки
        Shipment shipment = new Shipment();
        shipment.setProcessedBatch(shipping);
        shipment.setRecipient("ООО Переработка");
        shipment.setWeightKg(new BigDecimal("950.00")); // небольшие потери при обработке
        shipment.setTransportDoc("ТТН-001");

        // Проверки целостности всего процесса
        assertEquals(sorting.getReceipt(), pressing.getReceipt());
        assertEquals(pressing.getReceipt(), shipping.getReceipt());
        assertEquals(ProcessingStage.Status.completed, sorting.getStatus());
        assertEquals(ProcessingStage.Status.completed, pressing.getStatus());
        assertEquals(ProcessingStage.Status.completed, shipping.getStatus());
        assertTrue(shipment.getWeightKg().compareTo(receipt.getWeightKg()) <= 0);
        assertEquals(shipping, shipment.getProcessedBatch());
    }
}
