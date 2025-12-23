package ru.demo.demo2.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
public class Receipt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;
    
    @ManyToOne @JoinColumn(name = "waste_type_id", nullable = false)
    private WasteType wasteType;
    
    @ManyToOne @JoinColumn(name = "cell_id")
    private StorageCell storageCell;
    
    @Column(name = "weight_kg", nullable = false)
    private BigDecimal weightKg;
    
    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime = LocalDateTime.now();
    
    @ManyToOne @JoinColumn(name = "operator_id", nullable = false)
    private User operator;

    public Receipt() {}
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public String getSupplierName() { 
        if (supplier == null) {
            throw new NullPointerException("Supplier is null");
        }
        return supplier.getName(); 
    }
    public WasteType getWasteType() { return wasteType; }
    public void setWasteType(WasteType wasteType) { this.wasteType = wasteType; }
    public String getWasteTypeName() { return wasteType != null ? wasteType.getName() : ""; }
    public StorageCell getStorageCell() { return storageCell; }
    public void setStorageCell(StorageCell storageCell) { this.storageCell = storageCell; }
    public String getStorageCellCode() { return storageCell != null ? storageCell.getCode() : "-"; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public LocalDateTime getDatetime() { return datetime; }
    public void setDatetime(LocalDateTime datetime) { this.datetime = datetime; }
    public User getOperator() { return operator; }
    public void setOperator(User operator) { this.operator = operator; }
    public String getOperatorLogin() { return operator != null ? operator.getLogin() : ""; }
}

