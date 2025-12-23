package ru.demo.demo2.service;

import org.hibernate.Session;
import ru.demo.demo2.util.HibernateSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class StatisticsService {
    
    public static class WasteTypeStats {
        private String wasteTypeName;
        private BigDecimal totalReceived, totalShipped, currentStock;
        private Long receiptsCount;

        public WasteTypeStats(String name, BigDecimal received, BigDecimal shipped, Long count) {
            this.wasteTypeName = name;
            this.totalReceived = received != null ? received : BigDecimal.ZERO;
            this.totalShipped = shipped != null ? shipped : BigDecimal.ZERO;
            this.currentStock = this.totalReceived.subtract(this.totalShipped);
            this.receiptsCount = count != null ? count : 0L;
        }
        public String getWasteTypeName() { return wasteTypeName; }
        public BigDecimal getTotalReceived() { return totalReceived; }
        public BigDecimal getTotalShipped() { return totalShipped; }
        public BigDecimal getCurrentStock() { return currentStock; }
        public Long getReceiptsCount() { return receiptsCount; }
    }

    public static class SupplierStats {
        private String supplierName, supplierType;
        private BigDecimal totalWeight;
        private Long receiptsCount;
        public SupplierStats(String name, String type, BigDecimal weight, Long count) {
            this.supplierName = name; this.supplierType = type;
            this.totalWeight = weight != null ? weight : BigDecimal.ZERO;
            this.receiptsCount = count != null ? count : 0L;
        }
        public String getSupplierName() { return supplierName; }
        public String getSupplierTypeDisplayName() { return "F".equals(supplierType) ? "Физ. лицо" : "Юр. лицо"; }
        public BigDecimal getTotalWeight() { return totalWeight; }
        public Long getReceiptsCount() { return receiptsCount; }
    }

    public static class MonthlyStats {
        private Integer month;
        private BigDecimal totalReceived, totalShipped;
        public MonthlyStats(int month, BigDecimal received, BigDecimal shipped) {
            this.month = month;
            this.totalReceived = received != null ? received : BigDecimal.ZERO;
            this.totalShipped = shipped != null ? shipped : BigDecimal.ZERO;
        }
        public BigDecimal getTotalReceived() { return totalReceived; }
        public BigDecimal getTotalShipped() { return totalShipped; }
        public String getMonthName() {
            String[] m = {"Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"};
            return m[month - 1];
        }
    }

    public static class StorageStats {
        private BigDecimal totalLoad, totalCapacity;
        public StorageStats(BigDecimal load, BigDecimal capacity) { this.totalLoad = load; this.totalCapacity = capacity; }
        public BigDecimal getTotalLoad() { return totalLoad; }
        public BigDecimal getTotalCapacity() { return totalCapacity; }
        public double getLoadPercentage() {
            if (totalCapacity.compareTo(BigDecimal.ZERO) == 0) return 0;
            return totalLoad.multiply(BigDecimal.valueOf(100)).divide(totalCapacity, 1, java.math.RoundingMode.HALF_UP).doubleValue();
        }
    }

    public List<WasteTypeStats> getWasteTypeStatistics(LocalDate from, LocalDate to) {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            List<Object[]> rows = s.createQuery("SELECT wt.name, COALESCE(SUM(r.weightKg),0), COUNT(r.id) FROM WasteType wt LEFT JOIN Receipt r ON r.wasteType.id = wt.id AND r.datetime BETWEEN :f AND :t GROUP BY wt.name", Object[].class)
                .setParameter("f", from.atStartOfDay()).setParameter("t", to.plusDays(1).atStartOfDay()).list();
            List<WasteTypeStats> stats = new ArrayList<>();
            for (Object[] r : rows) stats.add(new WasteTypeStats((String)r[0], (BigDecimal)r[1], BigDecimal.ZERO, ((Number)r[2]).longValue()));
            return stats;
        }
    }

    public List<SupplierStats> getSupplierStatistics() {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            List<Object[]> rows = s.createQuery("SELECT sp.name, sp.type, COALESCE(SUM(r.weightKg),0), COUNT(r.id) FROM Supplier sp LEFT JOIN Receipt r ON r.supplier.id = sp.id GROUP BY sp.name, sp.type ORDER BY SUM(r.weightKg) DESC NULLS LAST", Object[].class).list();
            List<SupplierStats> stats = new ArrayList<>();
            for (Object[] r : rows) stats.add(new SupplierStats((String)r[0], (String)r[1], (BigDecimal)r[2], ((Number)r[3]).longValue()));
            return stats;
        }
    }

    public List<MonthlyStats> getMonthlyStats(int year) {
        List<MonthlyStats> stats = new ArrayList<>();
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            for (int m = 1; m <= 12; m++) {
                YearMonth ym = YearMonth.of(year, m);
                LocalDate start = ym.atDay(1), end = ym.atEndOfMonth();
                BigDecimal rec = s.createQuery("SELECT COALESCE(SUM(r.weightKg),0) FROM Receipt r WHERE r.datetime BETWEEN :f AND :t", BigDecimal.class)
                    .setParameter("f", start.atStartOfDay()).setParameter("t", end.plusDays(1).atStartOfDay()).uniqueResult();
                BigDecimal ship = s.createQuery("SELECT COALESCE(SUM(sh.weightKg),0) FROM Shipment sh JOIN sh.processedBatch ps WHERE ps.endTime BETWEEN :f AND :t", BigDecimal.class)
                    .setParameter("f", start.atStartOfDay()).setParameter("t", end.plusDays(1).atStartOfDay()).uniqueResult();
                stats.add(new MonthlyStats(m, rec, ship));
            }
        }
        return stats;
    }

    public StorageStats getStorageStats() {
        try (Session s = HibernateSession.getSessionFactory().openSession()) {
            Object[] r = s.createQuery("SELECT COALESCE(SUM(c.currentLoadKg),0), COALESCE(SUM(c.maxCapacityKg),0) FROM StorageCell c", Object[].class).uniqueResult();
            return new StorageStats((BigDecimal)r[0], (BigDecimal)r[1]);
        }
    }
}

