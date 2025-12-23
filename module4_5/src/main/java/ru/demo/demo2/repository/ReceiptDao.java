package ru.demo.demo2.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.demo.demo2.model.Receipt;
import ru.demo.demo2.util.HibernateSession;

import java.time.LocalDateTime;
import java.util.List;

public class ReceiptDao extends BaseDao<Receipt> {
    public ReceiptDao() { super(Receipt.class); }
    
    public List<Receipt> findByDateRange(LocalDateTime from, LocalDateTime to) {
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        List<Receipt> items = session.createQuery(
            "FROM Receipt r WHERE r.datetime BETWEEN :from AND :to ORDER BY r.datetime DESC", Receipt.class)
            .setParameter("from", from)
            .setParameter("to", to)
            .list();
        tx.commit();
        session.close();
        return items;
    }
    
    public List<Receipt> findBySupplierId(Integer supplierId) {
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        List<Receipt> items = session.createQuery(
            "FROM Receipt r WHERE r.supplier.id = :sid ORDER BY r.datetime DESC", Receipt.class)
            .setParameter("sid", supplierId)
            .list();
        tx.commit();
        session.close();
        return items;
    }
    
    public List<Receipt> findByWasteTypeId(Integer wasteTypeId) {
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        List<Receipt> items = session.createQuery(
            "FROM Receipt r WHERE r.wasteType.id = :wid ORDER BY r.datetime DESC", Receipt.class)
            .setParameter("wid", wasteTypeId)
            .list();
        tx.commit();
        session.close();
        return items;
    }
}
