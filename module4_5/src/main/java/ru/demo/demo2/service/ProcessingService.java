package ru.demo.demo2.service;

import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import java.math.BigDecimal;

public class ProcessingService {
    private final ProcessingStageDao stageDao = new ProcessingStageDao();
    private final ShipmentDao shipmentDao = new ShipmentDao();
    private final AuditLogDao auditLogDao = new AuditLogDao();
    
    public ProcessingStage startSorting(Receipt receipt, User operator) {
        return createStage(receipt, ProcessingStage.Stage.sorting, operator);
    }
    
    public ProcessingStage startPressing(Receipt receipt, User operator) {
        return createStage(receipt, ProcessingStage.Stage.pressing, operator);
    }
    
    public ProcessingStage startShipping(Receipt receipt, User operator) {
        return createStage(receipt, ProcessingStage.Stage.shipping, operator);
    }
    
    private ProcessingStage createStage(Receipt receipt, ProcessingStage.Stage stage, User operator) {
        ProcessingStage ps = new ProcessingStage();
        ps.setReceipt(receipt);
        ps.setStage(stage);
        stageDao.save(ps);
        auditLogDao.save(new AuditLog(operator, "CREATE", "processing_stages", ps.getId(), stage.name()));
        return ps;
    }
    
    public void completeStage(ProcessingStage stage, User operator) {
        stage.complete();
        stageDao.update(stage);
        auditLogDao.save(new AuditLog(operator, "UPDATE", "processing_stages", stage.getId(), "completed"));
    }
    
    public void cancelStage(ProcessingStage stage) {
        stage.setStatus(ProcessingStage.Status.cancelled);
        stageDao.update(stage);
    }
    
    public Shipment createShipment(ProcessingStage batch, String recipient, BigDecimal weight, String doc, User operator) {
        Shipment s = new Shipment();
        s.setProcessedBatch(batch);
        s.setRecipient(recipient);
        s.setWeightKg(weight);
        s.setTransportDoc(doc);
        shipmentDao.save(s);
        auditLogDao.save(new AuditLog(operator, "CREATE", "shipment", s.getId(), weight + " кг -> " + recipient));
        return s;
    }
    
    public void deleteStage(ProcessingStage stage) { stageDao.delete(stage); }
    public void updateShipment(Shipment s) { shipmentDao.update(s); }
    public void deleteShipment(Shipment s) { shipmentDao.delete(s); }
}

