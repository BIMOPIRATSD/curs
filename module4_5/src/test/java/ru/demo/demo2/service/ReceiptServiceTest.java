package ru.demo.demo2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReceiptServiceTest {

    private ReceiptService receiptService;

    @Mock
    private ReceiptDao receiptDao;

    @Mock
    private AuditLogDao auditLogDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        receiptService = new ReceiptService();
    }

    @Test
    void testCreateReceipt_Success() {
        // Подготовка данных
        Supplier supplier = new Supplier();
        supplier.setId(1);
        supplier.setName("Тестовый поставщик");

        WasteType wasteType = new WasteType();
        wasteType.setId(1);
        wasteType.setName("Пластик");

        User operator = new User();
        operator.setId(1);
        operator.setLogin("operator1");

        BigDecimal weight = new BigDecimal("100.50");

        // Создание приёмки
        Receipt receipt = receiptService.createReceipt(supplier, wasteType, weight, operator);

        // Проверки
        assertNotNull(receipt);
        assertEquals(supplier, receipt.getSupplier());
        assertEquals(wasteType, receipt.getWasteType());
        assertEquals(weight, receipt.getWeightKg());
        assertEquals(operator, receipt.getOperator());
        assertNotNull(receipt.getDatetime());
    }

    @Test
    void testCreateReceipt_WithZeroWeight() {
        Supplier supplier = new Supplier();
        WasteType wasteType = new WasteType();
        User operator = new User();
        BigDecimal weight = BigDecimal.ZERO;

        Receipt receipt = receiptService.createReceipt(supplier, wasteType, weight, operator);

        assertNotNull(receipt);
        assertEquals(BigDecimal.ZERO, receipt.getWeightKg());
    }

    @Test
    void testCreateReceipt_WithLargeWeight() {
        Supplier supplier = new Supplier();
        WasteType wasteType = new WasteType();
        User operator = new User();
        BigDecimal weight = new BigDecimal("99999.99");

        Receipt receipt = receiptService.createReceipt(supplier, wasteType, weight, operator);

        assertNotNull(receipt);
        assertEquals(new BigDecimal("99999.99"), receipt.getWeightKg());
    }

    @Test
    void testCreateReceipt_NullSupplier_ShouldThrowException() {
        WasteType wasteType = new WasteType();
        User operator = new User();
        BigDecimal weight = new BigDecimal("100");

        assertThrows(NullPointerException.class, () -> {
            Receipt receipt = receiptService.createReceipt(null, wasteType, weight, operator);
            receipt.getSupplierName(); // NPE при попытке получить имя поставщика
        });
    }

    @Test
    void testUpdateReceipt() {
        Receipt receipt = new Receipt();
        receipt.setId(1);
        Supplier supplier = new Supplier();
        supplier.setId(1);
        receipt.setSupplier(supplier);

        assertDoesNotThrow(() -> receiptService.updateReceipt(receipt));
    }

    @Test
    void testDeleteReceipt() {
        Receipt receipt = new Receipt();
        receipt.setId(1);

        assertDoesNotThrow(() -> receiptService.deleteReceipt(receipt));
    }
}
