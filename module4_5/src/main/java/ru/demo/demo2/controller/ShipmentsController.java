package ru.demo.demo2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import ru.demo.demo2.service.ProcessingService;
import ru.demo.demo2.util.ValidationUtil;
import java.math.BigDecimal;

public class ShipmentsController {
    @FXML private TableView<Shipment> tableView;
    @FXML private TableColumn<Shipment, Integer> colId;
    @FXML private TableColumn<Shipment, String> colBatch, colRecipient, colTransportDoc;
    @FXML private TableColumn<Shipment, BigDecimal> colWeight;
    @FXML private ComboBox<ProcessingStage> cbBatch;
    @FXML private ComboBox<User> cbOperator;
    @FXML private TextField tfRecipient, tfWeight, tfTransportDoc, tfSearch;

    private final ShipmentDao dao = new ShipmentDao();
    private final ProcessingStageDao stageDao = new ProcessingStageDao();
    private final ProcessingService service = new ProcessingService();
    private ObservableList<Shipment> list = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBatch.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("Этап #" + c.getValue().getProcessedBatchId()));
        colRecipient.setCellValueFactory(new PropertyValueFactory<>("recipient"));
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weightKg"));
        colTransportDoc.setCellValueFactory(new PropertyValueFactory<>("transportDoc"));
        
        cbBatch.setItems(FXCollections.observableArrayList(stageDao.findCompletedShippingStages()));
        cbBatch.setConverter(new javafx.util.StringConverter<ProcessingStage>() {
            public String toString(ProcessingStage p) { return p == null ? "" : "Этап #" + p.getId() + " (" + p.getStageDisplayName() + ")"; }
            public ProcessingStage fromString(String s) { return null; }
        });
        
        cbOperator.setItems(FXCollections.observableArrayList(new UserDao().findAll()));
        cbOperator.setConverter(new javafx.util.StringConverter<User>() {
            public String toString(User u) { return u == null ? "" : u.getLogin(); }
            public User fromString(String s) { return null; }
        });
        
        loadData();
        
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) { cbBatch.setValue(n.getProcessedBatch()); tfRecipient.setText(n.getRecipient()); tfWeight.setText(n.getWeightKg().toString()); tfTransportDoc.setText(n.getTransportDoc()); }
        });
        tfSearch.textProperty().addListener((obs, o, n) -> filter(n));
    }

    private void loadData() { list.setAll(dao.findAll()); tableView.setItems(list); cbBatch.setItems(FXCollections.observableArrayList(stageDao.findCompletedShippingStages())); }
    
    private void filter(String s) { 
        if (s == null || s.isEmpty()) { 
            tableView.setItems(list); 
            return; 
        }
        String searchLower = s.toLowerCase();
        tableView.setItems(list.filtered(x -> 
            x.getRecipient().toLowerCase().contains(searchLower) ||
            x.getTransportDoc().toLowerCase().contains(searchLower) ||
            x.getId().toString().contains(searchLower) ||
            x.getWeightKg().toString().contains(searchLower)
        )); 
    }

    @FXML private void onAdd() {
        // Валидация партии
        if (cbBatch.getValue() == null) { 
            alert("Выберите партию (этап обработки)"); 
            cbBatch.requestFocus();
            return; 
        }
        
        // Валидация получателя
        if (!ValidationUtil.isNotEmpty(tfRecipient.getText())) { 
            alert("Введите название получателя"); 
            tfRecipient.requestFocus();
            return; 
        }
        
        String recipient = ValidationUtil.trim(tfRecipient.getText());
        if (recipient.length() < 2) {
            alert("Название получателя должно содержать минимум 2 символа");
            tfRecipient.requestFocus();
            return;
        }
        
        // Валидация веса
        if (!ValidationUtil.isValidPositiveDecimal(tfWeight.getText())) {
            alert("Вес должен быть положительным числом");
            tfWeight.requestFocus();
            return;
        }
        
        BigDecimal w = new BigDecimal(ValidationUtil.trim(tfWeight.getText()));
        
        // Валидация оператора
        if (cbOperator.getValue() == null) { 
            alert("Выберите оператора"); 
            cbOperator.requestFocus();
            return; 
        }
        
        String transportDoc = ValidationUtil.trim(tfTransportDoc.getText());
        service.createShipment(cbBatch.getValue(), recipient, w, transportDoc, cbOperator.getValue());
        loadData(); 
        clear();
        showInfo("Отгрузка успешно добавлена");
    }

    @FXML private void onUpdate() {
        Shipment sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { 
            alert("Выберите отгрузку для редактирования"); 
            return; 
        }
        
        // Валидация
        if (cbBatch.getValue() == null) { 
            alert("Выберите партию"); 
            return; 
        }
        if (!ValidationUtil.isNotEmpty(tfRecipient.getText())) { 
            alert("Введите получателя"); 
            tfRecipient.requestFocus();
            return; 
        }
        if (!ValidationUtil.isValidPositiveDecimal(tfWeight.getText())) {
            alert("Вес должен быть положительным числом");
            tfWeight.requestFocus();
            return;
        }
        
        sel.setProcessedBatch(cbBatch.getValue()); 
        sel.setRecipient(ValidationUtil.trim(tfRecipient.getText())); 
        sel.setWeightKg(new BigDecimal(ValidationUtil.trim(tfWeight.getText()))); 
        sel.setTransportDoc(ValidationUtil.trim(tfTransportDoc.getText()));
        service.updateShipment(sel); 
        loadData(); 
        clear();
        showInfo("Отгрузка успешно обновлена");
    }

    @FXML private void onDelete() {
        Shipment sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { 
            alert("Выберите отгрузку для удаления"); 
            return; 
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
            "Вы уверены, что хотите удалить отгрузку #" + sel.getId() + "?", 
            ButtonType.YES, ButtonType.NO);
        confirmAlert.setTitle("Подтверждение удаления");
        confirmAlert.setHeaderText("Удаление отгрузки");
        
        confirmAlert.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> { 
            service.deleteShipment(sel); 
            loadData(); 
            clear(); 
            showInfo("Отгрузка успешно удалена");
        });
    }

    @FXML private void onClear() { clear(); tableView.getSelectionModel().clearSelection(); }
    private void clear() { cbBatch.setValue(null); tfRecipient.clear(); tfWeight.clear(); tfTransportDoc.clear(); }
    private void alert(String msg) { 
        Alert a = new Alert(Alert.AlertType.WARNING, msg);
        a.setTitle("Предупреждение");
        a.setHeaderText(null);
        a.showAndWait(); 
    }
    private void showInfo(String msg) { 
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setTitle("Успешно");
        a.setHeaderText(null);
        a.showAndWait(); 
    }
}

