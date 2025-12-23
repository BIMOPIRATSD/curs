package ru.demo.demo2.repository;

import org.hibernate.Session;
import ru.demo.demo2.model.AuditLog;
import java.time.LocalDateTime;
import java.util.List;

public class AuditLogDao extends BaseDao<AuditLog> {
    public AuditLogDao() { super(AuditLog.class); }
    
    public List<AuditLog> findByDateRange(LocalDateTime from, LocalDateTime to) {
        try (Session s = getCurrentSession()) {
            return s.createQuery("FROM AuditLog a WHERE a.timestamp BETWEEN :f AND :t ORDER BY a.timestamp DESC", AuditLog.class)
                .setParameter("f", from).setParameter("t", to).list();
        }
    }
    
    @Override
    public List<AuditLog> findAll() {
        try (Session s = getCurrentSession()) {
            return s.createQuery("FROM AuditLog a ORDER BY a.timestamp DESC", AuditLog.class).list();
        }
    }
}

