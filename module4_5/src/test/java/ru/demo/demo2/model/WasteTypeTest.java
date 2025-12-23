package ru.demo.demo2.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class WasteTypeTest {
    
    @Test
    void shouldCreateWasteType() {
        WasteType wasteType = new WasteType();
        wasteType.setName("Пластик ПЭТ");
        wasteType.setDensityKgPerM3(new BigDecimal("1380.00"));
        wasteType.setRecyclable(true);
        
        assertEquals("Пластик ПЭТ", wasteType.getName());
        assertEquals(new BigDecimal("1380.00"), wasteType.getDensityKgPerM3());
        assertTrue(wasteType.getRecyclable());
    }
    
    @Test
    void shouldHaveDefaultRecyclableTrue() {
        WasteType wasteType = new WasteType();
        
        assertTrue(wasteType.getRecyclable());
    }
    
    @Test
    void shouldGetRecyclableDisplayName() {
        WasteType recyclable = new WasteType();
        recyclable.setRecyclable(true);
        
        WasteType notRecyclable = new WasteType();
        notRecyclable.setRecyclable(false);
        
        assertEquals("Да", recyclable.getRecyclableDisplayName());
        assertEquals("Нет", notRecyclable.getRecyclableDisplayName());
    }
}
