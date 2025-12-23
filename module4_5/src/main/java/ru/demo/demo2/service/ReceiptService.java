package ru.demo.demo2.service;

import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import java.math.BigDecimal;

public class ReceiptService {
    private final ReceiptDao receiptDao = new ReceiptDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();
    
    public Receipt createReceipt(Supplier supplier, WasteType wasteType, BigDecimal weightKg, User operator) {
        Receipt receipt = new Receipt();
        receipt.setSupplier(supplier);
        receipt.setWasteType(wasteType);
        receipt.setWeightKg(weightKg);
        receipt.setOperator(operator);
        receiptDao.save(receipt);
        auditLogDao.save(new AuditLog(operator, "CREATE", "receipt", receipt.getId(), weightKg + " кг"));
        return receipt;
    }
    
    public void updateReceipt(Receipt receipt) { receiptDao.update(receipt); }
    public void deleteReceipt(Receipt receipt) { receiptDao.delete(receipt); }
}

