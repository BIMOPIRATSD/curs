package ru.demo.demo2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import ru.demo.demo2.service.ProcessingService;
import ru.demo.demo2.util.HibernateSession;
import ru.demo.demo2.util.ReferenceCheckUtil;
import java.time.format.DateTimeFormatter;

public class ProcessingController {
    @FXML private TableView<ProcessingStage> tableView;
    @FXML private TableColumn<ProcessingStage, Integer> colId;
    @FXML private TableColumn<ProcessingStage, String> colReceipt, colStage, colStartTime, colEndTime, colStatus;
    @FXML private ComboBox<Receipt> cbReceipt;
    @FXML private ComboBox<String> cbStage;
    @FXML private ComboBox<User> cbOperator;
    @FXML private TextField tfSearch;

    private final ProcessingStageDao dao = new ProcessingStageDao();
    private final ProcessingService service = new ProcessingService();
    private ObservableList<ProcessingStage> list = FXCollections.observableArrayList();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colReceipt.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("Партия #" + c.getValue().getReceiptId()));
        colStage.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStageDisplayName()));
        colStartTime.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStartTime().format(dtf)));
        colEndTime.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEndTime() != null ? c.getValue().getEndTime().format(dtf) : "-"));
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatusDisplayName()));
        
        cbReceipt.setItems(FXCollections.observableArrayList(new ReceiptDao().findAll()));
        cbReceipt.setConverter(new javafx.util.StringConverter<Receipt>() {
            public String toString(Receipt r) { return r == null ? "" : "Партия #" + r.getId() + " (" + r.getWasteTypeName() + ", " + r.getWeightKg() + " кг)"; }
            public Receipt fromString(String s) { return null; }
        });
        
        cbStage.setItems(FXCollections.observableArrayList("Сортировка", "Прессовка", "Отгрузка"));
        
        cbOperator.setItems(FXCollections.observableArrayList(new UserDao().findAll()));
        cbOperator.setConverter(new javafx.util.StringConverter<User>() {
            public String toString(User u) { return u == null ? "" : u.getLogin(); }
            public User fromString(String s) { return null; }
        });
        
        loadData();
        tfSearch.textProperty().addListener((obs, o, n) -> filter(n));
    }

    private void loadData() { list.setAll(dao.findAll()); tableView.setItems(list); }
    
    private void filter(String s) { 
        if (s == null || s.isEmpty()) { 
            tableView.setItems(list); 
            return; 
        }
        String searchLower = s.toLowerCase();
        tableView.setItems(list.filtered(x -> 
            x.getStageDisplayName().toLowerCase().contains(searchLower) ||
            x.getStatusDisplayName().toLowerCase().contains(searchLower) ||
            x.getId().toString().contains(searchLower) ||
            ("\u043f\u0430\u0440\u0442\u0438\u044f #" + x.getReceiptId()).toLowerCase().contains(searchLower)
        )); 
    }

    @FXML private void onStartStage() {
        // Валидация
        if (cbReceipt.getValue() == null) { 
            alert("Выберите партию отходов"); 
            cbReceipt.requestFocus();
            return; 
        }
        if (cbStage.getValue() == null) { 
            alert("Выберите этап обработки"); 
            cbStage.requestFocus();
            return; 
        }
        if (cbOperator.getValue() == null) { 
            alert("Выберите оператора"); 
            cbOperator.requestFocus();
            return; 
        }
        
        try {
            switch (cbStage.getValue()) {
                case "Сортировка" -> service.startSorting(cbReceipt.getValue(), cbOperator.getValue());
                case "Прессовка" -> service.startPressing(cbReceipt.getValue(), cbOperator.getValue());
                case "Отгрузка" -> service.startShipping(cbReceipt.getValue(), cbOperator.getValue());
            }
            loadData(); 
            clear();
            showInfo("Этап \"" + cbStage.getValue() + "\" успешно запущен");
        } catch (Exception e) {
            alertError("Ошибка при запуске этапа: " + e.getMessage());
        }
    }

    @FXML private void onComplete() {
        ProcessingStage sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { 
            alert("Выберите этап для завершения"); 
            return; 
        }
        
        if (sel.getStatus() != ProcessingStage.Status.in_progress) { 
            alert("Можно завершить только активный этап (\u0412 процессе)"); 
            return; 
        }
        
        if (cbOperator.getValue() == null) { 
            alert("Выберите оператора"); 
            cbOperator.requestFocus();
            return; 
        }
        
        service.completeStage(sel, cbOperator.getValue()); 
        loadData();
        showInfo("Этап успешно завершён");
    }

    @FXML private void onCancel() {
        ProcessingStage sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { 
            alert("Выберите этап для отмены"); 
            return; 
        }
        
        if (sel.getStatus() == ProcessingStage.Status.completed) {
            alert("Нельзя отменить завершённый этап");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
            "Вы уверены, что хотите отменить этап?", 
            ButtonType.YES, ButtonType.NO);
        confirmAlert.setTitle("Подтверждение отмены");
        
        confirmAlert.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> { 
            service.cancelStage(sel); 
            loadData();
            showInfo("Этап отменён");
        });
    }

    @FXML private void onDelete() {
        ProcessingStage sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { 
            alert("Выберите этап для удаления"); 
            return; 
        }
        
        // Проверка связей перед удалением
        Session session = HibernateSession.getSessionFactory().openSession();
        try {
            if (ReferenceCheckUtil.isProcessingStageUsed(session, sel)) {
                alertError(ReferenceCheckUtil.getProcessingStageUsageMessage(session, sel));
                return;
            }
        } finally {
            session.close();
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
            "Вы уверены, что хотите удалить этап #" + sel.getId() + "?", 
            ButtonType.YES, ButtonType.NO);
        confirmAlert.setTitle("Подтверждение удаления");
        confirmAlert.setHeaderText("Удаление этапа обработки");
        
        confirmAlert.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> { 
            service.deleteStage(sel); 
            loadData(); 
            showInfo("Этап успешно удалён");
        });
    }

    @FXML private void onClear() { clear(); tableView.getSelectionModel().clearSelection(); }
    private void clear() { cbReceipt.setValue(null); cbStage.setValue(null); }
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

