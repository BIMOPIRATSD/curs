package ru.demo.demo2.util;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    void testIsValidEmail_ValidEmails() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name@domain.co.uk"));
        assertTrue(ValidationUtil.isValidEmail("user+tag@example.org"));
        assertTrue(ValidationUtil.isValidEmail("123@test.ru"));
    }

    @Test
    void testIsValidEmail_InvalidEmails() {
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail("   "));
        assertFalse(ValidationUtil.isValidEmail(null));
        assertFalse(ValidationUtil.isValidEmail("invalid.email"));
        assertFalse(ValidationUtil.isValidEmail("@domain.com"));
        assertFalse(ValidationUtil.isValidEmail("user@"));
        assertFalse(ValidationUtil.isValidEmail("user @domain.com"));
    }

    @Test
    void testIsValidPhone_ValidPhones() {
        assertTrue(ValidationUtil.isValidPhone(""));
        assertTrue(ValidationUtil.isValidPhone(null));
        assertTrue(ValidationUtil.isValidPhone("1234567890"));
        assertTrue(ValidationUtil.isValidPhone("+79991234567"));
        assertTrue(ValidationUtil.isValidPhone("+7 (999) 123-45-67"));
        assertTrue(ValidationUtil.isValidPhone("8 800 555 35 35"));
    }

    @Test
    void testIsValidPhone_InvalidPhones() {
        assertFalse(ValidationUtil.isValidPhone("123")); // слишком короткий
        assertFalse(ValidationUtil.isValidPhone("abc123456789"));
        assertFalse(ValidationUtil.isValidPhone("12345678901234567890")); // слишком длинный
    }

    @Test
    void testIsValidPositiveDecimal_ValidValues() {
        assertTrue(ValidationUtil.isValidPositiveDecimal("10"));
        assertTrue(ValidationUtil.isValidPositiveDecimal("10.5"));
        assertTrue(ValidationUtil.isValidPositiveDecimal("0.01"));
        assertTrue(ValidationUtil.isValidPositiveDecimal("999999.99"));
        assertTrue(ValidationUtil.isValidPositiveDecimal("  100  "));
    }

    @Test
    void testIsValidPositiveDecimal_InvalidValues() {
        assertFalse(ValidationUtil.isValidPositiveDecimal("0"));
        assertFalse(ValidationUtil.isValidPositiveDecimal("-10"));
        assertFalse(ValidationUtil.isValidPositiveDecimal(""));
        assertFalse(ValidationUtil.isValidPositiveDecimal(null));
        assertFalse(ValidationUtil.isValidPositiveDecimal("abc"));
        assertFalse(ValidationUtil.isValidPositiveDecimal("10.5.5"));
    }

    @Test
    void testIsValidNonNegativeDecimal_ValidValues() {
        assertTrue(ValidationUtil.isValidNonNegativeDecimal(""));
        assertTrue(ValidationUtil.isValidNonNegativeDecimal(null));
        assertTrue(ValidationUtil.isValidNonNegativeDecimal("0"));
        assertTrue(ValidationUtil.isValidNonNegativeDecimal("10.5"));
        assertTrue(ValidationUtil.isValidNonNegativeDecimal("0.00"));
    }

    @Test
    void testIsValidNonNegativeDecimal_InvalidValues() {
        assertFalse(ValidationUtil.isValidNonNegativeDecimal("-1"));
        assertFalse(ValidationUtil.isValidNonNegativeDecimal("-0.01"));
        assertFalse(ValidationUtil.isValidNonNegativeDecimal("abc"));
    }

    @Test
    void testIsValidPositiveInteger_ValidValues() {
        assertTrue(ValidationUtil.isValidPositiveInteger("1"));
        assertTrue(ValidationUtil.isValidPositiveInteger("100"));
        assertTrue(ValidationUtil.isValidPositiveInteger("  50  "));
    }

    @Test
    void testIsValidPositiveInteger_InvalidValues() {
        assertFalse(ValidationUtil.isValidPositiveInteger("0"));
        assertFalse(ValidationUtil.isValidPositiveInteger("-5"));
        assertFalse(ValidationUtil.isValidPositiveInteger(""));
        assertFalse(ValidationUtil.isValidPositiveInteger(null));
        assertFalse(ValidationUtil.isValidPositiveInteger("10.5"));
        assertFalse(ValidationUtil.isValidPositiveInteger("abc"));
    }

    @Test
    void testIsNotEmpty_ValidValues() {
        assertTrue(ValidationUtil.isNotEmpty("test"));
        assertTrue(ValidationUtil.isNotEmpty("  test  "));
    }

    @Test
    void testIsNotEmpty_InvalidValues() {
        assertFalse(ValidationUtil.isNotEmpty(""));
        assertFalse(ValidationUtil.isNotEmpty(null));
        assertFalse(ValidationUtil.isNotEmpty("   "));
    }

    @Test
    void testTrim() {
        assertEquals("test", ValidationUtil.trim("  test  "));
        assertEquals("hello", ValidationUtil.trim("hello"));
        assertEquals("", ValidationUtil.trim(""));
        assertEquals("", ValidationUtil.trim(null));
        assertEquals("", ValidationUtil.trim("   "));
    }

    @Test
    void testPhoneValidation_RealWorldExamples() {
        assertTrue(ValidationUtil.isValidPhone("+79991234567"));
        assertTrue(ValidationUtil.isValidPhone("89991234567"));
        assertTrue(ValidationUtil.isValidPhone("+7 999 123 45 67"));
        assertTrue(ValidationUtil.isValidPhone("8(999)123-45-67"));

        assertTrue(ValidationUtil.isValidPhone("+441234567890"));
        assertTrue(ValidationUtil.isValidPhone("+1234567890123"));
    }

    @Test
    void testDecimalValidation_EdgeCases() {
        assertTrue(ValidationUtil.isValidPositiveDecimal("0.0001"));
        assertTrue(ValidationUtil.isValidPositiveDecimal("0.000000001"));

        assertTrue(ValidationUtil.isValidPositiveDecimal("999999999.99"));

        // Scientific notation is actually accepted by BigDecimal
        assertTrue(ValidationUtil.isValidPositiveDecimal("1e5"));
    }

    @Test
    void testEmailValidation_EdgeCases() {
        // Длинные домены
        assertTrue(ValidationUtil.isValidEmail("test@very-long-domain-name.example.com"));
        
        // Поддомены
        assertTrue(ValidationUtil.isValidEmail("user@mail.server.example.org"));
        
        // Специальные символы в локальной части
        assertTrue(ValidationUtil.isValidEmail("user_name@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user+tag@example.com"));
    }
}
