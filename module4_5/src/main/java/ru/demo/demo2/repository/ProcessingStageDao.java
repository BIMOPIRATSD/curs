package ru.demo.demo2.repository;

import org.hibernate.Session;
import ru.demo.demo2.model.ProcessingStage;
import java.util.List;

public class ProcessingStageDao extends BaseDao<ProcessingStage> {
    public ProcessingStageDao() { super(ProcessingStage.class); }
    
    public List<ProcessingStage> findCompletedShippingStages() {
        try (Session s = getCurrentSession()) {
            return s.createQuery("FROM ProcessingStage p WHERE p.stage = :stage AND p.status = :status", ProcessingStage.class)
                .setParameter("stage", ProcessingStage.Stage.shipping)
                .setParameter("status", ProcessingStage.Status.completed).list();
        }
    }
}

