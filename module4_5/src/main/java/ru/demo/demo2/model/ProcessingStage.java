package ru.demo.demo2.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "processing_stages")
public class ProcessingStage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stage stage;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime = LocalDateTime.now();
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.in_progress;

    public enum Stage { sorting, pressing, shipping }
    public enum Status { in_progress, completed, cancelled }

    public ProcessingStage() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Receipt getReceipt() { return receipt; }
    public void setReceipt(Receipt receipt) { this.receipt = receipt; }
    public Integer getReceiptId() { return receipt != null ? receipt.getId() : null; }
    public Stage getStage() { return stage; }
    public void setStage(Stage stage) { this.stage = stage; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime t) { this.startTime = t; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime t) { this.endTime = t; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public String getStageDisplayName() {
        return switch (stage) {
            case sorting -> "Сортировка";
            case pressing -> "Прессовка";
            case shipping -> "Отгрузка";
        };
    }
    
    public String getStatusDisplayName() {
        return switch (status) {
            case in_progress -> "В процессе";
            case completed -> "Завершён";
            case cancelled -> "Отменён";
        };
    }
    
    public void complete() {
        this.status = Status.completed;
        this.endTime = LocalDateTime.now();
    }
}

