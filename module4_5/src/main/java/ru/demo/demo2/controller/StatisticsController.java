package ru.demo.demo2.controller;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import ru.demo.demo2.service.StatisticsService;
import ru.demo.demo2.service.StatisticsService.*;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

public class StatisticsController {
    @FXML private TableView<WasteTypeStats> wasteTypeTable;
    @FXML private TableColumn<WasteTypeStats, String> colWasteType;
    @FXML private TableColumn<WasteTypeStats, BigDecimal> colReceived, colShipped, colStock;
    @FXML private TableColumn<WasteTypeStats, Long> colCount;
    @FXML private TableView<SupplierStats> supplierTable;
    @FXML private TableColumn<SupplierStats, String> colSupplierName, colSupplierType;
    @FXML private TableColumn<SupplierStats, BigDecimal> colTotalWeight;
    @FXML private TableColumn<SupplierStats, Long> colReceiptsCount;
    @FXML private DatePicker dpFrom, dpTo;
    @FXML private ComboBox<Integer> cbYear;
    @FXML private VBox chartContainer;
    @FXML private Label lblStorageStatus;
    
    private final StatisticsService service = new StatisticsService();

    @FXML
    public void initialize() {
        colWasteType.setCellValueFactory(new PropertyValueFactory<>("wasteTypeName"));
        colReceived.setCellValueFactory(new PropertyValueFactory<>("totalReceived"));
        colShipped.setCellValueFactory(new PropertyValueFactory<>("totalShipped"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        colCount.setCellValueFactory(new PropertyValueFactory<>("receiptsCount"));
        
        colSupplierName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colSupplierType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSupplierTypeDisplayName()));
        colTotalWeight.setCellValueFactory(new PropertyValueFactory<>("totalWeight"));
        colReceiptsCount.setCellValueFactory(new PropertyValueFactory<>("receiptsCount"));
        
        dpFrom.setValue(LocalDate.now().minusMonths(1));
        dpTo.setValue(LocalDate.now());
        int year = Year.now().getValue();
        cbYear.setItems(FXCollections.observableArrayList(year - 2, year - 1, year));
        cbYear.setValue(year);
        loadData();
    }

    @FXML private void onRefresh() { loadData(); }

    private void loadData() {
        if (dpFrom.getValue() != null && dpTo.getValue() != null)
            wasteTypeTable.setItems(FXCollections.observableArrayList(service.getWasteTypeStatistics(dpFrom.getValue(), dpTo.getValue())));
        supplierTable.setItems(FXCollections.observableArrayList(service.getSupplierStatistics()));
        
        StorageStats s = service.getStorageStats();
        lblStorageStatus.setText(String.format("Склад: %.0f / %.0f кг (%.1f%%)", s.getTotalLoad(), s.getTotalCapacity(), s.getLoadPercentage()));
        
        if (cbYear.getValue() != null) updateChart(cbYear.getValue());
    }

    private void updateChart(int year) {
        var stats = service.getMonthlyStats(year);
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (var ms : stats) { ds.addValue(ms.getTotalReceived().doubleValue(), "Принято", ms.getMonthName()); ds.addValue(ms.getTotalShipped().doubleValue(), "Отгружено", ms.getMonthName()); }
        
        JFreeChart chart = ChartFactory.createBarChart("Динамика за " + year, "Месяц", "Вес (кг)", ds, PlotOrientation.VERTICAL, true, true, false);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(800, 400));
        SwingNode node = new SwingNode();
        SwingUtilities.invokeLater(() -> node.setContent(panel));
        chartContainer.getChildren().setAll(node);
    }
}

