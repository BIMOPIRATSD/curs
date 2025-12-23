package ru.demo.demo2.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SupplierTest {
    
    @Test
    void shouldCreateSupplier() {
        Supplier supplier = new Supplier();
        supplier.setName("Иванов И.И.");
        supplier.setType("F");
        supplier.setPhone("+7-900-111-1111");
        supplier.setEmail("ivanov@mail.ru");
        
        assertEquals("Иванов И.И.", supplier.getName());
        assertEquals("F", supplier.getType());
        assertEquals("+7-900-111-1111", supplier.getPhone());
        assertEquals("ivanov@mail.ru", supplier.getEmail());
    }
    
    @Test
    void shouldGetTypeDisplayNameForIndividual() {
        Supplier supplier = new Supplier();
        supplier.setType("F");
        
        assertEquals("Физ. лицо", supplier.getTypeDisplayName());
    }
    
    @Test
    void shouldGetTypeDisplayNameForLegal() {
        Supplier supplier = new Supplier();
        supplier.setType("L");
        
        assertEquals("Юр. лицо", supplier.getTypeDisplayName());
    }
    
    @Test
    void shouldSetAndGetId() {
        Supplier supplier = new Supplier();
        supplier.setId(1);
        
        assertEquals(1, supplier.getId());
    }
    
    @Test
    void shouldHandleNullPhone() {
        Supplier supplier = new Supplier();
        supplier.setPhone(null);
        assertNull(supplier.getPhone());
    }

    @Test
    void shouldHandleNullEmail() {
        Supplier supplier = new Supplier();
        supplier.setEmail(null);
        assertNull(supplier.getEmail());
    }

    @Test
    void shouldCreateSupplierWithMinimalData() {
        Supplier supplier = new Supplier();
        supplier.setName("Минимальный");
        supplier.setType("F");

        assertNotNull(supplier);
        assertEquals("Минимальный", supplier.getName());
        assertNull(supplier.getPhone());
        assertNull(supplier.getEmail());
    }

    @Test
    void shouldHandleLongNames() {
        String longName = "Очень длинное название организации с большим количеством слов и деталей";
        Supplier supplier = new Supplier();
        supplier.setName(longName);
        assertEquals(longName, supplier.getName());
    }

    @Test
    void shouldHandleSpecialCharactersInEmail() {
        Supplier supplier = new Supplier();
        supplier.setEmail("test+tag@example.co.uk");
        assertEquals("test+tag@example.co.uk", supplier.getEmail());
    }
}
