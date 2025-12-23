package ru.demo.demo2.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import ru.demo.demo2.util.ValidationUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Параметризованные тесты для валидации различных сценариев
 */
class ParameterizedValidationTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "user@example.com",
        "test.user@example.com",
        "user+tag@example.com",
        "user123@test.co.uk",
        "admin@localhost.local"
    })
    void testValidEmails(String email) {
        assertTrue(ValidationUtil.isValidEmail(email), 
            "Email должен быть валидным: " + email);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalid.email",
        "@example.com",
        "user@",
        "user @example.com",
        "user@domain",
        ""
    })
    @NullAndEmptySource
    void testInvalidEmails(String email) {
        assertFalse(ValidationUtil.isValidEmail(email), 
            "Email должен быть невалидным: " + email);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "+79991234567",
        "89991234567",
        "+7 999 123 45 67",
        "8(999)123-45-67",
        "+1234567890",
        "123456789012345"
    })
    void testValidPhones(String phone) {
        assertTrue(ValidationUtil.isValidPhone(phone), 
            "Телефон должен быть валидным: " + phone);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123",
        "abc",
        "12345678901234567890",
        "phone-number"
    })
    void testInvalidPhones(String phone) {
        assertFalse(ValidationUtil.isValidPhone(phone), 
            "Телефон должен быть невалидным: " + phone);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1",
        "10",
        "100.5",
        "0.01",
        "999999.99",
        "  100  "
    })
    void testValidPositiveDecimals(String value) {
        assertTrue(ValidationUtil.isValidPositiveDecimal(value), 
            "Значение должно быть валидным положительным числом: " + value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0",
        "-1",
        "-10.5",
        "abc",
        "10.5.5",
        ""
    })
    @NullAndEmptySource
    void testInvalidPositiveDecimals(String value) {
        assertFalse(ValidationUtil.isValidPositiveDecimal(value), 
            "Значение должно быть невалидным: " + value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0",
        "1",
        "100.5",
        "0.00",
        "  50  "
    })
    void testValidNonNegativeDecimals(String value) {
        assertTrue(ValidationUtil.isValidNonNegativeDecimal(value), 
            "Значение должно быть валидным неотрицательным числом: " + value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "-1",
        "-0.01",
        "abc"
    })
    void testInvalidNonNegativeDecimals(String value) {
        assertFalse(ValidationUtil.isValidNonNegativeDecimal(value), 
            "Значение должно быть невалидным: " + value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1",
        "100",
        "999999",
        "  50  "
    })
    void testValidPositiveIntegers(String value) {
        assertTrue(ValidationUtil.isValidPositiveInteger(value), 
            "Значение должно быть валидным положительным целым числом: " + value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "0",
        "-5",
        "10.5",
        "abc",
        ""
    })
    @NullAndEmptySource
    void testInvalidPositiveIntegers(String value) {
        assertFalse(ValidationUtil.isValidPositiveInteger(value), 
            "Значение должно быть невалидным: " + value);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "test",
        "  test  ",
        "a",
        "123"
    })
    void testNotEmptyStrings(String value) {
        assertTrue(ValidationUtil.isNotEmpty(value), 
            "Строка не должна быть пустой: '" + value + "'");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "   "
    })
    @NullAndEmptySource
    void testEmptyStrings(String value) {
        assertFalse(ValidationUtil.isNotEmpty(value), 
            "Строка должна считаться пустой: '" + value + "'");
    }

    @Test
    void testTrimVariousStrings() {
        assertEquals("test", ValidationUtil.trim("  test  "));
        assertEquals("hello world", ValidationUtil.trim("hello world"));
        assertEquals("", ValidationUtil.trim("   "));
        assertEquals("", ValidationUtil.trim(null));
        assertEquals("a", ValidationUtil.trim("  a  "));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Администратор",
        "Оператор123",
        "User Name",
        "test@example.com",
        "ООО \"Рога и Копыта\""
    })
    void testNonEmptyRussianAndSpecialCharacters(String value) {
        assertTrue(ValidationUtil.isNotEmpty(value), 
            "Строка с русскими/спец. символами не должна быть пустой");
        assertEquals(value.trim(), ValidationUtil.trim(value));
    }

    @Test
    void testEmailEdgeCases() {
        // Очень длинный email
        String longEmail = "verylongusernamewithmanycharacters@verylongdomainnamewithmanycharacters.com";
        assertTrue(ValidationUtil.isValidEmail(longEmail));

        // Email с точками
        assertTrue(ValidationUtil.isValidEmail("first.last@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name.test@mail.ru"));

        // Email с plus addressing
        assertTrue(ValidationUtil.isValidEmail("user+spam@gmail.com"));
    }

    @Test
    void testPhoneNumberFormats() {
        // Разные форматы российских номеров
        String[] russianPhones = {
            "+79991234567",
            "89991234567",
            "+7 999 123 45 67",
            "+7(999)123-45-67",
            "8 999 123 45 67",
            "8-999-123-45-67"
        };

        for (String phone : russianPhones) {
            assertTrue(ValidationUtil.isValidPhone(phone), 
                "Российский номер должен быть валидным: " + phone);
        }
    }

    @Test
    void testDecimalPrecision() {
        assertTrue(ValidationUtil.isValidPositiveDecimal("0.0001"));
        assertTrue(ValidationUtil.isValidPositiveDecimal("0.000001"));
        assertTrue(ValidationUtil.isValidPositiveDecimal("999999999.999999"));
        
        assertFalse(ValidationUtil.isValidPositiveDecimal("0.0000"));
        assertFalse(ValidationUtil.isValidPositiveDecimal("0.000000000000"));
    }

    @Test
    void testBoundaryValues() {
        // Граничные значения для чисел
        assertTrue(ValidationUtil.isValidPositiveInteger("1"));
        assertTrue(ValidationUtil.isValidPositiveInteger("2147483647")); // MAX_INT
        
        assertTrue(ValidationUtil.isValidPositiveDecimal("0.01"));
        assertTrue(ValidationUtil.isValidNonNegativeDecimal("0"));
        assertTrue(ValidationUtil.isValidNonNegativeDecimal("0.0"));
        
        assertFalse(ValidationUtil.isValidPositiveDecimal("0"));
        assertFalse(ValidationUtil.isValidPositiveInteger("0"));
    }
}
