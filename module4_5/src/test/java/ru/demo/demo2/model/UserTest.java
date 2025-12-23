package ru.demo.demo2.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    
    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setLogin("operator1");
        user.setPasswordHash("e10adc3949ba59abbe56e057f20f883e");
        user.setRole(User.UserRole.operator);
        
        assertEquals("operator1", user.getLogin());
        assertEquals("e10adc3949ba59abbe56e057f20f883e", user.getPasswordHash());
        assertEquals(User.UserRole.operator, user.getRole());
    }
    
    @Test
    void shouldGetRoleDisplayNameForAdmin() {
        User user = new User();
        user.setRole(User.UserRole.admin);
        
        assertEquals("Администратор", user.getRoleDisplayName());
    }
    
    @Test
    void shouldGetRoleDisplayNameForOperator() {
        User user = new User();
        user.setRole(User.UserRole.operator);
        
        assertEquals("Оператор", user.getRoleDisplayName());
    }
    
    @Test
    void shouldGetRoleDisplayNameForViewer() {
        User user = new User();
        user.setRole(User.UserRole.viewer);
        
        assertEquals("Наблюдатель", user.getRoleDisplayName());
    }
}
