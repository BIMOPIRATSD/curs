package ru.demo.demo2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;
import ru.demo.demo2.model.WasteType;
import ru.demo.demo2.repository.WasteTypeDao;
import ru.demo.demo2.util.HibernateSession;
import ru.demo.demo2.util.ReferenceCheckUtil;
import ru.demo.demo2.util.ValidationUtil;
import java.math.BigDecimal;

public class WasteTypesController {
    @FXML private TableView<WasteType> tableView;
    @FXML private TableColumn<WasteType, Integer> colId;
    @FXML private TableColumn<WasteType, String> colName, colRecyclable;
    @FXML private TableColumn<WasteType, BigDecimal> colDensity;
    @FXML private TextField tfName, tfDensity, tfSearch;
    @FXML private CheckBox cbRecyclable;

    private final WasteTypeDao dao = new WasteTypeDao();
    private ObservableList<WasteType> list = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDensity.setCellValueFactory(new PropertyValueFactory<>("densityKgPerM3"));
        colRecyclable.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRecyclableDisplayName()));
        loadData();
        
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) { tfName.setText(n.getName()); tfDensity.setText(n.getDensityKgPerM3() != null ? n.getDensityKgPerM3().toString() : ""); cbRecyclable.setSelected(n.getRecyclable()); }
        });
        tfSearch.textProperty().addListener((obs, o, n) -> filter(n));
    }

    private void loadData() { list.setAll(dao.findAll()); tableView.setItems(list); }
    
    private void filter(String s) { 
        if (s == null || s.isEmpty()) { 
            tableView.setItems(list); 
            return; 
        }
        String searchLower = s.toLowerCase();
        tableView.setItems(list.filtered(w -> 
            w.getName().toLowerCase().contains(searchLower) ||
            w.getRecyclableDisplayName().toLowerCase().contains(searchLower) ||
            (w.getDensityKgPerM3() != null && w.getDensityKgPerM3().toString().contains(searchLower))
        )); 
    }

    @FXML private void onAdd() {
        if (!ValidationUtil.isNotEmpty(tfName.getText())) { 
            alert("Название не может быть пустым"); 
            tfName.requestFocus();
            return; 
        }
        
        String name = ValidationUtil.trim(tfName.getText());
        if (name.length() < 2) {
            alert("Название должно содержать минимум 2 символа");
            tfName.requestFocus();
            return;
        }

        if (list.stream().anyMatch(w -> w.getName().equalsIgnoreCase(name))) {
            alert("Тип отходов с таким названием уже существует");
            tfName.requestFocus();
            return;
        }

        BigDecimal density = null;
        if (ValidationUtil.isNotEmpty(tfDensity.getText())) {
            if (!ValidationUtil.isValidPositiveDecimal(tfDensity.getText())) {
                alert("Плотность должна быть положительным числом");
                tfDensity.requestFocus();
                return;
            }
            density = new BigDecimal(ValidationUtil.trim(tfDensity.getText()));
        }
        
        WasteType e = new WasteType(); 
        e.setName(name); 
        e.setRecyclable(cbRecyclable.isSelected());
        e.setDensityKgPerM3(density);
        dao.save(e); 
        loadData(); 
        clear();
        showInfo("Тип отходов успешно добавлен");
    }

    @FXML private void onUpdate() {
        WasteType sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { 
            alert("Выберите запись для редактирования"); 
            return; 
        }

        if (!ValidationUtil.isNotEmpty(tfName.getText())) { 
            alert("Название не может быть пустым"); 
            tfName.requestFocus();
            return; 
        }
        
        String name = ValidationUtil.trim(tfName.getText());
        if (name.length() < 2) {
            alert("Название должно содержать минимум 2 символа");
            tfName.requestFocus();
            return;
        }

        if (list.stream().anyMatch(w -> !w.getId().equals(sel.getId()) && w.getName().equalsIgnoreCase(name))) {
            alert("Тип отходов с таким названием уже существует");
            tfName.requestFocus();
            return;
        }

        BigDecimal density = null;
        if (ValidationUtil.isNotEmpty(tfDensity.getText())) {
            if (!ValidationUtil.isValidPositiveDecimal(tfDensity.getText())) {
                alert("Плотность должна быть положительным числом");
                tfDensity.requestFocus();
                return;
            }
            density = new BigDecimal(ValidationUtil.trim(tfDensity.getText()));
        }
        
        sel.setName(name); 
        sel.setRecyclable(cbRecyclable.isSelected());
        sel.setDensityKgPerM3(density);
        dao.update(sel); 
        loadData(); 
        clear();
        showInfo("Тип отходов успешно обновлен");
    }

    @FXML private void onDelete() {
        WasteType sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { 
            alert("Выберите запись для удаления"); 
            return; 
        }

        Session session = HibernateSession.getSessionFactory().openSession();
        try {
            if (ReferenceCheckUtil.isWasteTypeUsed(session, sel)) {
                alertError(ReferenceCheckUtil.getWasteTypeUsageMessage(session, sel));
                return;
            }
        } finally {
            session.close();
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
            "Вы уверены, что хотите удалить тип отходов '" + sel.getName() + "'?", 
            ButtonType.YES, ButtonType.NO);
        confirmAlert.setTitle("Подтверждение удаления");
        confirmAlert.setHeaderText("Удаление типа отходов");
        
        confirmAlert.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> { 
            dao.delete(sel); 
            loadData(); 
            clear(); 
            showInfo("Тип отходов успешно удален");
        });
    }

    @FXML private void onClear() { clear(); tableView.getSelectionModel().clearSelection(); }
    private void clear() { tfName.clear(); tfDensity.clear(); cbRecyclable.setSelected(true); }
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
