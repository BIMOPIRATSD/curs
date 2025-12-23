package ru.demo.demo2.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.demo.demo2.model.StorageCell;
import ru.demo.demo2.util.HibernateSession;

import java.math.BigDecimal;
import java.util.List;

public class StorageCellDao extends BaseDao<StorageCell> {
    public StorageCellDao() { super(StorageCell.class); }
    
    public StorageCell findByCode(String code) {
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        StorageCell cell = session.createQuery(
            "FROM StorageCell s WHERE s.code = :code", StorageCell.class)
            .setParameter("code", code)
            .uniqueResult();
        tx.commit();
        session.close();
        return cell;
    }
    
    public List<StorageCell> findWithAvailableCapacity(BigDecimal minCapacity) {
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        List<StorageCell> items = session.createQuery(
            "FROM StorageCell s WHERE (s.maxCapacityKg - s.currentLoadKg) >= :minCap ORDER BY s.code", StorageCell.class)
            .setParameter("minCap", minCapacity)
            .list();
        tx.commit();
        session.close();
        return items;
    }
}
