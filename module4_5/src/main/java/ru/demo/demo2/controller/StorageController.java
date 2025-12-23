package ru.demo.demo2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;
import ru.demo.demo2.model.StorageCell;
import ru.demo.demo2.repository.StorageCellDao;
import ru.demo.demo2.util.HibernateSession;
import ru.demo.demo2.util.ReferenceCheckUtil;
import ru.demo.demo2.util.ValidationUtil;
import java.math.BigDecimal;

public class StorageController {
    @FXML private TableView<StorageCell> tableView;
    @FXML private TableColumn<StorageCell, Integer> colId;
    @FXML private TableColumn<StorageCell, String> colCode, colPercentage;
    @FXML private TableColumn<StorageCell, BigDecimal> colMaxCapacity, colCurrentLoad;
    @FXML private TextField tfCode, tfMaxCapacity, tfCurrentLoad, tfSearch;
    @FXML private ProgressBar progressBar;
    @FXML private Label lblStatus;

    private final StorageCellDao dao = new StorageCellDao();
    private ObservableList<StorageCell> list = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colMaxCapacity.setCellValueFactory(new PropertyValueFactory<>("maxCapacityKg"));
        colCurrentLoad.setCellValueFactory(new PropertyValueFactory<>("currentLoadKg"));
        colPercentage.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.format("%.1f%%", c.getValue().getLoadPercentage())));
        loadData();
        
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) { tfCode.setText(n.getCode()); tfMaxCapacity.setText(n.getMaxCapacityKg().toString()); tfCurrentLoad.setText(n.getCurrentLoadKg().toString()); if (progressBar != null) progressBar.setProgress(n.getLoadPercentage() / 100.0); }
        });
        tfSearch.textProperty().addListener((obs, o, n) -> filter(n));
    }

    private void loadData() { 
        list.setAll(dao.findAll()); tableView.setItems(list);
        if (lblStatus != null) {
            BigDecimal total = BigDecimal.ZERO, cap = BigDecimal.ZERO;
            for (StorageCell c : list) { total = total.add(c.getCurrentLoadKg()); cap = cap.add(c.getMaxCapacityKg()); }
            double percentage = cap.compareTo(BigDecimal.ZERO) > 0 ? total.divide(cap, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue() : 0;
            lblStatus.setText(String.format("Всего: %.0f / %.0f кг (%.1f%%)", total, cap, percentage));
        }
    }
    
    private void filter(String s) { 
        if (s == null || s.isEmpty()) { 
            tableView.setItems(list); 
            return; 
        }
        String searchLower = s.toLowerCase();
        tableView.setItems(list.filtered(c -> 
            c.getCode().toLowerCase().contains(searchLower) ||
            c.getMaxCapacityKg().toString().contains(searchLower) ||
            c.getCurrentLoadKg().toString().contains(searchLower)
        )); 
    }

    @FXML private void onAdd() {
        // Валидация кода
        if (!ValidationUtil.isNotEmpty(tfCode.getText())) { 
            alert("Код ячейки не может быть пустым"); 
            tfCode.requestFocus();
            return; 
        }
        
        String code = ValidationUtil.trim(tfCode.getText());
        if (code.length() < 1) {
            alert("Код ячейки должен содержать минимум 1 символ");
            tfCode.requestFocus();
            return;
        }
        
        // Проверка уникальности кода
        if (list.stream().anyMatch(c -> c.getCode().equalsIgnoreCase(code))) {
            alert("Ячейка с таким кодом уже существует");
            tfCode.requestFocus();
            return;
        }
        
        // Валидация вместимости
        if (!ValidationUtil.isValidPositiveDecimal(tfMaxCapacity.getText())) {
            alert("Вместимость должна быть положительным числом");
            tfMaxCapacity.requestFocus();
            return;
        }
        
        BigDecimal maxCap = new BigDecimal(ValidationUtil.trim(tfMaxCapacity.getText()));
        
        // Валидация текущей загрузки
        BigDecimal curLoad = BigDecimal.ZERO;
        if (ValidationUtil.isNotEmpty(tfCurrentLoad.getText())) {
            if (!ValidationUtil.isValidNonNegativeDecimal(tfCurrentLoad.getText())) {
                alert("Текущая загрузка должна быть неотрицательным числом");
                tfCurrentLoad.requestFocus();
                return;
            }
            curLoad = new BigDecimal(ValidationUtil.trim(tfCurrentLoad.getText()));
        }
        
        if (curLoad.compareTo(maxCap) > 0) {
            alert("Текущая загрузка не может превышать вместимость");
            tfCurrentLoad.requestFocus();
            return;
        }
        
        StorageCell e = new StorageCell(); 
        e.setCode(code); 
        e.setMaxCapacityKg(maxCap);
        e.setCurrentLoadKg(curLoad);
        dao.save(e); 
        loadData(); 
        clear();
        showInfo("Ячейка хранения успешно добавлена");
    }

    @FXML private void onUpdate() {
        StorageCell sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { 
            alert("Выберите ячейку для редактирования"); 
            return; 
        }
        
        // Валидация кода
        if (!ValidationUtil.isNotEmpty(tfCode.getText())) { 
            alert("Код ячейки не может быть пустым"); 
            tfCode.requestFocus();
            return; 
        }
        
        String code = ValidationUtil.trim(tfCode.getText());
        
        // Проверка уникальности кода (кроме текущей записи)
        if (list.stream().anyMatch(c -> !c.getId().equals(sel.getId()) && c.getCode().equalsIgnoreCase(code))) {
            alert("Ячейка с таким кодом уже существует");
            tfCode.requestFocus();
            return;
        }
        
        // Валидация вместимости
        if (!ValidationUtil.isValidPositiveDecimal(tfMaxCapacity.getText())) {
            alert("Вместимость должна быть положительным числом");
            tfMaxCapacity.requestFocus();
            return;
        }
        
        BigDecimal maxCap = new BigDecimal(ValidationUtil.trim(tfMaxCapacity.getText()));
        
        // Валидация текущей загрузки
        if (!ValidationUtil.isValidNonNegativeDecimal(tfCurrentLoad.getText())) {
            alert("Текущая загрузка должна быть неотрицательным числом");
            tfCurrentLoad.requestFocus();
            return;
        }
        
        BigDecimal curLoad = new BigDecimal(ValidationUtil.trim(tfCurrentLoad.getText()));
        
        if (curLoad.compareTo(maxCap) > 0) {
            alert("Текущая загрузка не может превышать вместимость");
            tfCurrentLoad.requestFocus();
            return;
        }
        
        sel.setCode(code); 
        sel.setMaxCapacityKg(maxCap); 
        sel.setCurrentLoadKg(curLoad);
        dao.update(sel); 
        loadData(); 
        clear();
        showInfo("Ячейка хранения успешно обновлена");
    }

    @FXML private void onDelete() {
        StorageCell sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { 
            alert("Выберите ячейку для удаления"); 
            return; 
        }
        
        // Проверка связей перед удалением
        Session session = HibernateSession.getSessionFactory().openSession();
        try {
            if (ReferenceCheckUtil.isStorageCellUsed(session, sel)) {
                alertError(ReferenceCheckUtil.getStorageCellUsageMessage(session, sel));
                return;
            }
        } finally {
            session.close();
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
            "Вы уверены, что хотите удалить ячейку '" + sel.getCode() + "'?", 
            ButtonType.YES, ButtonType.NO);
        confirmAlert.setTitle("Подтверждение удаления");
        confirmAlert.setHeaderText("Удаление ячейки хранения");
        
        confirmAlert.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> { 
            dao.delete(sel); 
            loadData(); 
            clear(); 
            showInfo("Ячейка хранения успешно удалена");
        });
    }

    @FXML private void onClear() { clear(); tableView.getSelectionModel().clearSelection(); }
    private void clear() { tfCode.clear(); tfMaxCapacity.clear(); tfCurrentLoad.clear(); if (progressBar != null) progressBar.setProgress(0); }
    private void alert(String msg) { 
        Alert a = new Alert(Alert.AlertType.WARNING, msg);
        a.setTitle("Предупреждение");
        a.setHeaderText(null);
        a.showAndWait(); 
    }
    private void alertError(String msg) { 
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setTitle("Ошибка");
        a.setHeaderText("Невозможно выполнить операцию");
        a.showAndWait(); 
    }
    private void showInfo(String msg) { 
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setTitle("Успешно");
        a.setHeaderText(null);
        a.showAndWait(); 
    }
}

