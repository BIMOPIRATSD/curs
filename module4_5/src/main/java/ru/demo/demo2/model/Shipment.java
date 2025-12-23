package ru.demo.demo2.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "shipments")
public class Shipment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne @JoinColumn(name = "processed_batch_id", nullable = false)
    private ProcessingStage processedBatch;
    
    @Column(nullable = false)
    private String recipient;
    
    @Column(name = "weight_kg", nullable = false)
    private BigDecimal weightKg;
    
    @Column(name = "transport_doc")
    private String transportDoc;

    public Shipment() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public ProcessingStage getProcessedBatch() { return processedBatch; }
    public void setProcessedBatch(ProcessingStage batch) { this.processedBatch = batch; }
    public Integer getProcessedBatchId() { return processedBatch != null ? processedBatch.getId() : null; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weight) { this.weightKg = weight; }
    public String getTransportDoc() { return transportDoc; }
    public void setTransportDoc(String doc) { this.transportDoc = doc; }
}

