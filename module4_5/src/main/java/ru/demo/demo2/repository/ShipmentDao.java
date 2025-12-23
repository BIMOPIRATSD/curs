package ru.demo.demo2.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.demo.demo2.model.Shipment;
import ru.demo.demo2.util.HibernateSession;

import java.util.List;

public class ShipmentDao extends BaseDao<Shipment> {
    public ShipmentDao() { super(Shipment.class); }
    
    public List<Shipment> findByRecipient(String recipient) {
        Session session = getCurrentSession();
        Transaction tx = session.beginTransaction();
        List<Shipment> items = session.createQuery(
            "FROM Shipment s WHERE s.recipient LIKE :recipient ORDER BY s.id DESC", Shipment.class)
            .setParameter("recipient", "%" + recipient + "%")
            .list();
        tx.commit();
        session.close();
        return items;
    }
}
