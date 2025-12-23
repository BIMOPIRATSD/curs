package ru.demo.demo2.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "waste_types")
public class WasteType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(name = "density_kg_per_m3")
    private BigDecimal densityKgPerM3;
    
    @Column(nullable = false)
    private Boolean recyclable = true;

    public WasteType() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getDensityKgPerM3() { return densityKgPerM3; }
    public void setDensityKgPerM3(BigDecimal d) { this.densityKgPerM3 = d; }
    public Boolean getRecyclable() { return recyclable; }
    public void setRecyclable(Boolean recyclable) { this.recyclable = recyclable; }
    public String getRecyclableDisplayName() { return recyclable ? "Да" : "Нет"; }
}

