package ru.demo.demo2.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.demo.demo2.model.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReferenceCheckUtilTest {

    @Mock
    private Session session;
    
    @Mock
    private Query<Long> query;
    
    @Mock
    private SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testIsSupplierUsed_WhenUsed() {
        Supplier supplier = new Supplier();
        supplier.setId(1);
        supplier.setName("Тестовый поставщик");

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(5L);

        boolean result = ReferenceCheckUtil.isSupplierUsed(session, supplier);

        assertTrue(result);
        verify(session).createQuery(anyString(), eq(Long.class));
        verify(query).setParameter("supplier", supplier);
    }

    @Test
    void testIsSupplierUsed_WhenNotUsed() {
        Supplier supplier = new Supplier();
        supplier.setId(1);

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(0L);

        boolean result = ReferenceCheckUtil.isSupplierUsed(session, supplier);

        assertFalse(result);
    }

    @Test
    void testIsWasteTypeUsed_WhenUsed() {
        WasteType wasteType = new WasteType();
        wasteType.setId(1);
        wasteType.setName("Пластик");

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(10L);

        boolean result = ReferenceCheckUtil.isWasteTypeUsed(session, wasteType);

        assertTrue(result);
    }

    @Test
    void testIsStorageCellUsed_WhenNotUsed() {
        StorageCell cell = new StorageCell();
        cell.setId(1);

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(0L);

        boolean result = ReferenceCheckUtil.isStorageCellUsed(session, cell);

        assertFalse(result);
    }

    @Test
    void testIsReceiptUsed_WhenUsed() {
        Receipt receipt = new Receipt();
        receipt.setId(1);

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(3L);

        boolean result = ReferenceCheckUtil.isReceiptUsed(session, receipt);

        assertTrue(result);
    }

    @Test
    void testIsProcessingStageUsed_WhenNotUsed() {
        ProcessingStage stage = new ProcessingStage();
        stage.setId(1);

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(0L);

        boolean result = ReferenceCheckUtil.isProcessingStageUsed(session, stage);

        assertFalse(result);
    }

    @Test
    void testIsUserUsed_WhenUsedInMultipleTables() {
        User user = new User();
        user.setId(1);
        user.setLogin("operator1");

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        // Имитируем использование в разных таблицах
        when(query.getSingleResult()).thenReturn(5L, 3L, 2L);

        boolean result = ReferenceCheckUtil.isUserUsed(session, user);

        assertTrue(result);
        verify(session, times(3)).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testIsUserUsed_WhenNotUsed() {
        User user = new User();
        user.setId(1);

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(0L, 0L, 0L);

        boolean result = ReferenceCheckUtil.isUserUsed(session, user);

        assertFalse(result);
    }

    @Test
    void testGetSupplierUsageMessage() {
        Supplier supplier = new Supplier();
        supplier.setId(1);
        supplier.setName("ООО Рога и Копыта");

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(15L);

        String message = ReferenceCheckUtil.getSupplierUsageMessage(session, supplier);

        assertNotNull(message);
        assertTrue(message.contains("ООО Рога и Копыта"));
        assertTrue(message.contains("15"));
        assertTrue(message.contains("Невозможно удалить"));
    }

    @Test
    void testGetWasteTypeUsageMessage() {
        WasteType wasteType = new WasteType();
        wasteType.setId(1);
        wasteType.setName("Металл");

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(8L);

        String message = ReferenceCheckUtil.getWasteTypeUsageMessage(session, wasteType);

        assertTrue(message.contains("Металл"));
        assertTrue(message.contains("8"));
    }

    @Test
    void testGetStorageCellUsageMessage() {
        StorageCell cell = new StorageCell();
        cell.setId(1);
        cell.setCode("A-01");

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(12L);

        String message = ReferenceCheckUtil.getStorageCellUsageMessage(session, cell);

        assertTrue(message.contains("A-01"));
        assertTrue(message.contains("12"));
    }

    @Test
    void testGetReceiptUsageMessage() {
        Receipt receipt = new Receipt();
        receipt.setId(42);

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(3L);

        String message = ReferenceCheckUtil.getReceiptUsageMessage(session, receipt);

        assertTrue(message.contains("42"));
        assertTrue(message.contains("3"));
    }

    @Test
    void testGetProcessingStageUsageMessage() {
        ProcessingStage stage = new ProcessingStage();
        stage.setId(7);

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(2L);

        String message = ReferenceCheckUtil.getProcessingStageUsageMessage(session, stage);

        assertTrue(message.contains("7"));
        assertTrue(message.contains("2"));
    }

    @Test
    void testGetUserUsageMessage_WithMultipleUsages() {
        User user = new User();
        user.setId(1);
        user.setLogin("admin");

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(10L, 5L, 3L);

        String message = ReferenceCheckUtil.getUserUsageMessage(session, user);

        assertTrue(message.contains("admin"));
        assertTrue(message.contains("10"));
        assertTrue(message.contains("5"));
        assertTrue(message.contains("3"));
        assertTrue(message.contains("Приёмок"));
        assertTrue(message.contains("Этапов обработки"));
        assertTrue(message.contains("Отгрузок"));
    }

    @Test
    void testGetUserUsageMessage_WithPartialUsages() {
        User user = new User();
        user.setId(1);
        user.setLogin("operator");

        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(7L, 0L, 0L);

        String message = ReferenceCheckUtil.getUserUsageMessage(session, user);

        assertTrue(message.contains("operator"));
        assertTrue(message.contains("7"));
        assertTrue(message.contains("Приёмок"));
        assertFalse(message.contains("Этапов обработки: 0"));
        assertFalse(message.contains("Отгрузок: 0"));
    }
}
