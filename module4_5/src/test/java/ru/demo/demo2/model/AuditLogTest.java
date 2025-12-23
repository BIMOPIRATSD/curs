package ru.demo.demo2.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuditLogTest {
    
    @Test
    void shouldCreateAuditLog() {
        User user = new User();
        user.setLogin("admin");
        
        AuditLog log = new AuditLog(user, "CREATE", "receipt", 1, "150.50 кг");
        
        assertEquals(user, log.getUser());
        assertEquals("CREATE", log.getAction());
        assertEquals("receipt", log.getEntityType());
        assertEquals(1, log.getEntityId());
        assertEquals("150.50 кг", log.getNewValue());
        assertNotNull(log.getTimestamp());
    }
    
    @Test
    void shouldGetUserLogin() {
        User user = new User();
        user.setLogin("operator1");
        
        AuditLog log = new AuditLog();
        log.setUser(user);
        
        assertEquals("operator1", log.getUserLogin());
    }
    
    @Test
    void shouldReturnSystemWhenNoUser() {
        AuditLog log = new AuditLog();
        
        assertEquals("Система", log.getUserLogin());
    }
    
    @Test
    void shouldSetOldValue() {
        AuditLog log = new AuditLog();
        log.setOldValue("100 кг");
        log.setNewValue("150 кг");
        
        assertEquals("100 кг", log.getOldValue());
        assertEquals("150 кг", log.getNewValue());
    }
}
