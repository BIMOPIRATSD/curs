package ru.demo.demo2.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "storage_cells")
public class StorageCell {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(name = "max_capacity_kg", nullable = false)
    private BigDecimal maxCapacityKg;
    
    @Column(name = "current_load_kg", nullable = false)
    private BigDecimal currentLoadKg = BigDecimal.ZERO;

    public StorageCell() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public BigDecimal getMaxCapacityKg() { return maxCapacityKg; }
    public void setMaxCapacityKg(BigDecimal max) { this.maxCapacityKg = max; }
    public BigDecimal getCurrentLoadKg() { return currentLoadKg; }
    public void setCurrentLoadKg(BigDecimal load) { this.currentLoadKg = load; }
    
    public double getLoadPercentage() {
        if (maxCapacityKg.compareTo(BigDecimal.ZERO) == 0) return 0;
        return currentLoadKg.multiply(BigDecimal.valueOf(100))
            .divide(maxCapacityKg, 2, java.math.RoundingMode.HALF_UP).doubleValue();
    }
}

