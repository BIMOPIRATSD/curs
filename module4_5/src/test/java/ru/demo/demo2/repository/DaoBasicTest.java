package ru.demo.demo2.repository;

import org.junit.jupiter.api.Test;
import ru.demo.demo2.model.WasteType;
import ru.demo.demo2.model.Supplier;
import ru.demo.demo2.model.StorageCell;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DaoBasicTest {

    @Test
    void testWasteTypeDaoInstantiation() {
        WasteTypeDao dao = new WasteTypeDao();
        assertNotNull(dao);
    }

    @Test
    void testSupplierDaoInstantiation() {
        SupplierDao dao = new SupplierDao();
        assertNotNull(dao);
    }

    @Test
    void testStorageCellDaoInstantiation() {
        StorageCellDao dao = new StorageCellDao();
        assertNotNull(dao);
    }

    @Test
    void testReceiptDaoInstantiation() {
        ReceiptDao dao = new ReceiptDao();
        assertNotNull(dao);
    }

    @Test
    void testProcessingStageDaoInstantiation() {
        ProcessingStageDao dao = new ProcessingStageDao();
        assertNotNull(dao);
    }

    @Test
    void testShipmentDaoInstantiation() {
        ShipmentDao dao = new ShipmentDao();
        assertNotNull(dao);
    }

    @Test
    void testUserDaoInstantiation() {
        UserDao dao = new UserDao();
        assertNotNull(dao);
    }

    @Test
    void testAuditLogDaoInstantiation() {
        AuditLogDao dao = new AuditLogDao();
        assertNotNull(dao);
    }

    // Тесты для проверки создания объектов перед сохранением
    
    @Test
    void testWasteTypeCreationBeforeSave() {
        WasteType wasteType = new WasteType();
        wasteType.setName("Тестовый тип");
        wasteType.setDensityKgPerM3(new BigDecimal("1000.00"));
        wasteType.setRecyclable(true);

        assertNull(wasteType.getId()); // ID еще не установлен
        assertEquals("Тестовый тип", wasteType.getName());
    }

    @Test
    void testSupplierCreationBeforeSave() {
        Supplier supplier = new Supplier();
        supplier.setName("Тестовый поставщик");
        supplier.setType("L");
        supplier.setPhone("+79991234567");
        supplier.setEmail("test@test.com");

        assertNull(supplier.getId());
        assertEquals("Тестовый поставщик", supplier.getName());
    }

    @Test
    void testStorageCellCreationBeforeSave() {
        StorageCell cell = new StorageCell();
        cell.setCode("TEST-01");
        cell.setMaxCapacityKg(new BigDecimal("1000.00"));
        cell.setCurrentLoadKg(BigDecimal.ZERO);

        assertNull(cell.getId());
        assertEquals("TEST-01", cell.getCode());
    }
}
