package ru.demo.demo2.util;

import org.hibernate.Session;
import ru.demo.demo2.model.*;

public class ReferenceCheckUtil {
    
    public static boolean isSupplierUsed(Session session, Supplier supplier) {
        Long count = session.createQuery(
            "SELECT COUNT(r) FROM Receipt r WHERE r.supplier = :supplier", Long.class)
            .setParameter("supplier", supplier)
            .getSingleResult();
        return count > 0;
    }

    public static boolean isWasteTypeUsed(Session session, WasteType wasteType) {
        Long count = session.createQuery(
            "SELECT COUNT(r) FROM Receipt r WHERE r.wasteType = :wasteType", Long.class)
            .setParameter("wasteType", wasteType)
            .getSingleResult();
        return count > 0;
    }

    public static boolean isStorageCellUsed(Session session, StorageCell cell) {
        Long count = session.createQuery(
            "SELECT COUNT(r) FROM Receipt r WHERE r.storageCell = :cell", Long.class)
            .setParameter("cell", cell)
            .getSingleResult();
        return count > 0;
    }

    public static boolean isReceiptUsed(Session session, Receipt receipt) {
        Long count = session.createQuery(
            "SELECT COUNT(p) FROM ProcessingStage p WHERE p.receipt = :receipt", Long.class)
            .setParameter("receipt", receipt)
            .getSingleResult();
        return count > 0;
    }
    
    public static boolean isProcessingStageUsed(Session session, ProcessingStage stage) {
        Long count = session.createQuery(
            "SELECT COUNT(s) FROM Shipment s WHERE s.processedBatch = :stage", Long.class)
            .setParameter("stage", stage)
            .getSingleResult();
        return count > 0;
    }

    public static boolean isUserUsed(Session session, User user) {
        Long receiptCount = session.createQuery(
            "SELECT COUNT(r) FROM Receipt r WHERE r.operator = :user", Long.class)
            .setParameter("user", user)
            .getSingleResult();
            
        Long processingCount = session.createQuery(
            "SELECT COUNT(p) FROM ProcessingStage p WHERE p.operator = :user", Long.class)
            .setParameter("user", user)
            .getSingleResult();
            
        Long shipmentCount = session.createQuery(
            "SELECT COUNT(s) FROM Shipment s WHERE s.operator = :user", Long.class)
            .setParameter("user", user)
            .getSingleResult();
            
        return receiptCount > 0 || processingCount > 0 || shipmentCount > 0;
    }
    
    public static String getSupplierUsageMessage(Session session, Supplier supplier) {
        Long count = session.createQuery(
            "SELECT COUNT(r) FROM Receipt r WHERE r.supplier = :supplier", Long.class)
            .setParameter("supplier", supplier)
            .getSingleResult();
        return String.format("Невозможно удалить поставщика '%s'.\nОн используется в %d приёмках отходов.", 
            supplier.getName(), count);
    }

    public static String getWasteTypeUsageMessage(Session session, WasteType wasteType) {
        Long count = session.createQuery(
            "SELECT COUNT(r) FROM Receipt r WHERE r.wasteType = :wasteType", Long.class)
            .setParameter("wasteType", wasteType)
            .getSingleResult();
        return String.format("Невозможно удалить тип отходов '%s'.\nОн используется в %d приёмках.", 
            wasteType.getName(), count);
    }

    public static String getStorageCellUsageMessage(Session session, StorageCell cell) {
        Long count = session.createQuery(
            "SELECT COUNT(r) FROM Receipt r WHERE r.storageCell = :cell", Long.class)
            .setParameter("cell", cell)
            .getSingleResult();
        return String.format("Невозможно удалить ячейку '%s'.\nОна используется в %d приёмках.", 
            cell.getCode(), count);
    }

    public static String getReceiptUsageMessage(Session session, Receipt receipt) {
        Long count = session.createQuery(
            "SELECT COUNT(p) FROM ProcessingStage p WHERE p.receipt = :receipt", Long.class)
            .setParameter("receipt", receipt)
            .getSingleResult();
        return String.format("Невозможно удалить приёмку #%d.\nОна используется в %d этапах обработки.", 
            receipt.getId(), count);
    }

    public static String getProcessingStageUsageMessage(Session session, ProcessingStage stage) {
        Long count = session.createQuery(
            "SELECT COUNT(s) FROM Shipment s WHERE s.processedBatch = :stage", Long.class)
            .setParameter("stage", stage)
            .getSingleResult();
        return String.format("Невозможно удалить этап обработки #%d.\nОн используется в %d отгрузках.", 
            stage.getId(), count);
    }

    public static String getUserUsageMessage(Session session, User user) {
        Long receiptCount = session.createQuery(
            "SELECT COUNT(r) FROM Receipt r WHERE r.operator = :user", Long.class)
            .setParameter("user", user)
            .getSingleResult();
            
        Long processingCount = session.createQuery(
            "SELECT COUNT(p) FROM ProcessingStage p WHERE p.operator = :user", Long.class)
            .setParameter("user", user)
            .getSingleResult();
            
        Long shipmentCount = session.createQuery(
            "SELECT COUNT(s) FROM Shipment s WHERE s.operator = :user", Long.class)
            .setParameter("user", user)
            .getSingleResult();
            
        StringBuilder msg = new StringBuilder(String.format("Невозможно удалить пользователя '%s'.\n", user.getLogin()));
        if (receiptCount > 0) msg.append(String.format("Приёмок: %d\n", receiptCount));
        if (processingCount > 0) msg.append(String.format("Этапов обработки: %d\n", processingCount));
        if (shipmentCount > 0) msg.append(String.format("Отгрузок: %d", shipmentCount));
        
        return msg.toString();
    }
}
