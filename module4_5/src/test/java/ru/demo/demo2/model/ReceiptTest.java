package ru.demo.demo2.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ReceiptTest {
    
    @Test
    void shouldCreateReceipt() {
        Supplier supplier = new Supplier();
        supplier.setName("Иванов И.И.");
        
        WasteType wasteType = new WasteType();
        wasteType.setName("Пластик ПЭТ");
        
        StorageCell cell = new StorageCell();
        cell.setCode("A-001");
        
        User operator = new User();
        operator.setLogin("operator1");
        
        Receipt receipt = new Receipt();
        receipt.setSupplier(supplier);
        receipt.setWasteType(wasteType);
        receipt.setStorageCell(cell);
        receipt.setWeightKg(new BigDecimal("150.50"));
        receipt.setOperator(operator);
        
        assertEquals(supplier, receipt.getSupplier());
        assertEquals(wasteType, receipt.getWasteType());
        assertEquals(cell, receipt.getStorageCell());
        assertEquals(new BigDecimal("150.50"), receipt.getWeightKg());
        assertEquals(operator, receipt.getOperator());
        assertNotNull(receipt.getDatetime());
    }
    
    @Test
    void shouldGetSupplierName() {
        Supplier supplier = new Supplier();
        supplier.setName("Иванов И.И.");
        
        Receipt receipt = new Receipt();
        receipt.setSupplier(supplier);
        
        assertEquals("Иванов И.И.", receipt.getSupplierName());
    }
    
    @Test
    void shouldGetWasteTypeName() {
        WasteType wasteType = new WasteType();
        wasteType.setName("Макулатура");
        
        Receipt receipt = new Receipt();
        receipt.setWasteType(wasteType);
        
        assertEquals("Макулатура", receipt.getWasteTypeName());
    }
    
    @Test
    void shouldGetStorageCellCode() {
        StorageCell cell = new StorageCell();
        cell.setCode("B-002");
        
        Receipt receipt = new Receipt();
        receipt.setStorageCell(cell);
        
        assertEquals("B-002", receipt.getStorageCellCode());
    }
    
    @Test
    void shouldReturnDashWhenNoStorageCell() {
        Receipt receipt = new Receipt();
        
        assertEquals("-", receipt.getStorageCellCode());
    }
    
    @Test
    void shouldGetOperatorLogin() {
        User operator = new User();
        operator.setLogin("operator2");
        
        Receipt receipt = new Receipt();
        receipt.setOperator(operator);
        
        assertEquals("operator2", receipt.getOperatorLogin());
    }
}
