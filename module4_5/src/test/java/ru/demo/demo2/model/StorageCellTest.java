package ru.demo.demo2.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StorageCellTest {

    @Test
    void shouldCreateStorageCell() {
        StorageCell cell = new StorageCell();
        cell.setCode("A-01");
        cell.setMaxCapacityKg(new BigDecimal("5000.00"));
        cell.setCurrentLoadKg(new BigDecimal("2500.00"));

        assertEquals("A-01", cell.getCode());
        assertEquals(new BigDecimal("5000.00"), cell.getMaxCapacityKg());
        assertEquals(new BigDecimal("2500.00"), cell.getCurrentLoadKg());
    }

    @Test
    void shouldCalculateLoadPercentage() {
        StorageCell cell = new StorageCell();
        cell.setMaxCapacityKg(new BigDecimal("1000.00"));
        cell.setCurrentLoadKg(new BigDecimal("250.00"));

        double percentage = cell.getLoadPercentage();
        assertEquals(25.0, percentage, 0.01);
    }

    @Test
    void shouldCalculateLoadPercentageWhenFull() {
        StorageCell cell = new StorageCell();
        cell.setMaxCapacityKg(new BigDecimal("1000.00"));
        cell.setCurrentLoadKg(new BigDecimal("1000.00"));

        double percentage = cell.getLoadPercentage();
        assertEquals(100.0, percentage, 0.01);
    }

    @Test
    void shouldCalculateLoadPercentageWhenEmpty() {
        StorageCell cell = new StorageCell();
        cell.setMaxCapacityKg(new BigDecimal("1000.00"));
        cell.setCurrentLoadKg(BigDecimal.ZERO);

        double percentage = cell.getLoadPercentage();
        assertEquals(0.0, percentage, 0.01);
    }

    @Test
    void shouldReturnZeroPercentageWhenMaxCapacityIsZero() {
        StorageCell cell = new StorageCell();
        cell.setMaxCapacityKg(BigDecimal.ZERO);
        cell.setCurrentLoadKg(BigDecimal.ZERO);

        double percentage = cell.getLoadPercentage();
        assertEquals(0.0, percentage, 0.01);
    }

    @Test
    void shouldHandleNullCurrentLoad() {
        StorageCell cell = new StorageCell();
        cell.setMaxCapacityKg(new BigDecimal("1000.00"));
        cell.setCurrentLoadKg(null);

        assertThrows(NullPointerException.class, () -> cell.getLoadPercentage());
    }

    @Test
    void shouldHandleDecimalLoadPercentage() {
        StorageCell cell = new StorageCell();
        cell.setMaxCapacityKg(new BigDecimal("1000.00"));
        cell.setCurrentLoadKg(new BigDecimal("333.33"));

        double percentage = cell.getLoadPercentage();
        assertTrue(percentage > 33.3 && percentage < 33.4);
    }

    @Test
    void shouldHandleVerySmallLoad() {
        StorageCell cell = new StorageCell();
        cell.setMaxCapacityKg(new BigDecimal("10000.00"));
        cell.setCurrentLoadKg(new BigDecimal("0.01"));

        double percentage = cell.getLoadPercentage();
        assertTrue(percentage > 0.0 && percentage < 0.01);
    }

    @Test
    void shouldHandleOverload() {
        StorageCell cell = new StorageCell();
        cell.setMaxCapacityKg(new BigDecimal("1000.00"));
        cell.setCurrentLoadKg(new BigDecimal("1500.00"));

        double percentage = cell.getLoadPercentage();
        assertEquals(150.0, percentage, 0.01);
    }

    @Test
    void shouldSetAndGetId() {
        StorageCell cell = new StorageCell();
        cell.setId(5);

        assertEquals(5, cell.getId());
    }

    @Test
    void shouldHandleDifferentCodeFormats() {
        String[] codes = {"A-01", "B-12", "ZONE-1-CELL-5", "X1Y2Z3"};
        
        for (String code : codes) {
            StorageCell cell = new StorageCell();
            cell.setCode(code);
            assertEquals(code, cell.getCode());
        }
    }

    @Test
    void shouldInitializeWithDefaultValues() {
        StorageCell cell = new StorageCell();
        
        assertNull(cell.getId());
        assertNull(cell.getCode());
        assertNull(cell.getMaxCapacityKg());
        assertNull(cell.getCurrentLoadKg());
    }

    @Test
    void shouldHandleLargeCapacity() {
        StorageCell cell = new StorageCell();
        BigDecimal largeCapacity = new BigDecimal("999999999.99");
        cell.setMaxCapacityKg(largeCapacity);
        cell.setCurrentLoadKg(new BigDecimal("500000000.00"));

        assertEquals(largeCapacity, cell.getMaxCapacityKg());
        assertTrue(cell.getLoadPercentage() > 0);
    }
}
