package ru.demo.demo2.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ProcessingStageTest {
    
    @Test
    void shouldCreateProcessingStage() {
        Receipt receipt = new Receipt();
        receipt.setId(1);
        
        ProcessingStage stage = new ProcessingStage();
        stage.setReceipt(receipt);
        stage.setStage(ProcessingStage.Stage.sorting);
        
        assertEquals(receipt, stage.getReceipt());
        assertEquals(ProcessingStage.Stage.sorting, stage.getStage());
        assertEquals(ProcessingStage.Status.in_progress, stage.getStatus());
        assertNotNull(stage.getStartTime());
    }
    
    @Test
    void shouldCompleteStage() {
        ProcessingStage stage = new ProcessingStage();
        stage.setStage(ProcessingStage.Stage.pressing);
        
        stage.complete();
        
        assertEquals(ProcessingStage.Status.completed, stage.getStatus());
        assertNotNull(stage.getEndTime());
    }
    
    @Test
    void shouldGetStageDisplayName() {
        ProcessingStage sorting = new ProcessingStage();
        sorting.setStage(ProcessingStage.Stage.sorting);
        
        ProcessingStage pressing = new ProcessingStage();
        pressing.setStage(ProcessingStage.Stage.pressing);
        
        ProcessingStage shipping = new ProcessingStage();
        shipping.setStage(ProcessingStage.Stage.shipping);
        
        assertEquals("Сортировка", sorting.getStageDisplayName());
        assertEquals("Прессовка", pressing.getStageDisplayName());
        assertEquals("Отгрузка", shipping.getStageDisplayName());
    }
    
    @Test
    void shouldGetStatusDisplayName() {
        ProcessingStage inProgress = new ProcessingStage();
        inProgress.setStatus(ProcessingStage.Status.in_progress);
        
        ProcessingStage completed = new ProcessingStage();
        completed.setStatus(ProcessingStage.Status.completed);
        
        ProcessingStage cancelled = new ProcessingStage();
        cancelled.setStatus(ProcessingStage.Status.cancelled);
        
        assertEquals("В процессе", inProgress.getStatusDisplayName());
        assertEquals("Завершён", completed.getStatusDisplayName());
        assertEquals("Отменён", cancelled.getStatusDisplayName());
    }
    
    @Test
    void shouldGetReceiptId() {
        Receipt receipt = new Receipt();
        receipt.setId(5);
        
        ProcessingStage stage = new ProcessingStage();
        stage.setReceipt(receipt);
        
        assertEquals(5, stage.getReceiptId());
    }
}
