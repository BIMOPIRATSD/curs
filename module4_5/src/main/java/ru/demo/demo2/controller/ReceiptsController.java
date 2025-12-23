package ru.demo.demo2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;
import ru.demo.demo2.model.*;
import ru.demo.demo2.repository.*;
import ru.demo.demo2.service.ReceiptService;
import ru.demo.demo2.util.HibernateSession;
import ru.demo.demo2.util.ReferenceCheckUtil;
import ru.demo.demo2.util.ValidationUtil;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class ReceiptsController {
    @FXML private TableView<Receipt> tableView;
    @FXML private TableColumn<Receipt, Integer> colId;
    @FXML private TableColumn<Receipt, String> colSupplier, colWasteType, colDatetime, colOperator;
    @FXML private TableColumn<Receipt, BigDecimal> colWeight;
    @FXML private ComboBox<Supplier> cbSupplier;
    @FXML private ComboBox<WasteType> cbWasteType;
    @FXML private ComboBox<User> cbOperator;
    @FXML private TextField tfWeight, tfSearch;

    private final ReceiptDao dao = new ReceiptDao();
    private final ReceiptService service = new ReceiptService();
    private ObservableList<Receipt> list = FXCollections.observableArrayList();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSupplier.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSupplierName()));
        colWasteType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getWasteTypeName()));
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weightKg"));
        colDatetime.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDatetime().format(dtf)));
        colOperator.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getOperatorLogin()));
        
        cbSupplier.setItems(FXCollections.observableArrayList(new SupplierDao().findAll()));
        cbSupplier.setConverter(new javafx.util.StringConverter<Supplier>() {
            public String toString(Supplier s) { return s == null ? "" : s.getName(); }
            public Supplier fromString(String s) { return null; }
        });
        
        cbWasteType.setItems(FXCollections.observableArrayList(new WasteTypeDao().findAll()));
        cbWasteType.setConverter(new javafx.util.StringConverter<WasteType>() {
            public String toString(WasteType w) { return w == null ? "" : w.getName(); }
            public WasteType fromString(String s) { return null; }
        });
        
        cbOperator.setItems(FXCollections.observableArrayList(new UserDao().findAll()));
        cbOperator.setConverter(new javafx.util.StringConverter<User>() {
            public String toString(User u) { return u == null ? "" : u.getLogin(); }
            public User fromString(String s) { return null; }
        });
        
        loadData();
        
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) { cbSupplier.setValue(n.getSupplier()); cbWasteType.setValue(n.getWasteType()); tfWeight.setText(n.getWeightKg().toString()); cbOperator.setValue(n.getOperator()); }
        });
        tfSearch.textProperty().addListener((obs, o, n) -> filter(n));
    }

    private void loadData() { list.setAll(dao.findAll()); tableView.setItems(list); }

    private void filter(String s) {
        if (s == null || s.isEmpty()) { tableView.setItems(list); return; }
        String searchLower = s.toLowerCase();
        tableView.setItems(list.filtered(r -> 
            r.getSupplierName().toLowerCase().contains(searchLower) || 
            r.getWasteTypeName().toLowerCase().contains(searchLower) ||
            r.getOperatorLogin().toLowerCase().contains(searchLower) ||
            r.getId().toString().contains(searchLower) ||
            r.getWeightKg().toString().contains(searchLower)
        ));
    }

    @FXML private void onAdd() {
        // Валидация поставщика
        if (cbSupplier.getValue() == null) { 
            alert("Выберите поставщика"); 
            cbSupplier.requestFocus();
            return; 
        }
        
        // Валидация типа отходов
        if (cbWasteType.getValue() == null) { 
            alert("Выберите тип отходов"); 
            cbWasteType.requestFocus();
            return; 
        }
        
        // Валидация веса
        if (!ValidationUtil.isValidPositiveDecimal(tfWeight.getText())) {
            alert("Вес должен быть положительным числом");
            tfWeight.requestFocus();
            return;
        }
        
        BigDecimal w = new BigDecimal(ValidationUtil.trim(tfWeight.getText()));
        
        // Дополнительная проверка веса
        if (w.compareTo(new BigDecimal("10000")) > 0) {
            alert("Вес превышает максимально допустимое значение (10000 кг)");
            tfWeight.requestFocus();
            return;
        }
        
        // Валидация оператора
        if (cbOperator.getValue() == null) { 
            alert("Выберите оператора"); 
            cbOperator.requestFocus();
            return; 
        }
        
        service.createReceipt(cbSupplier.getValue(), cbWasteType.getValue(), w, cbOperator.getValue());
        loadData(); 
        clear();
        showInfo("Приёмка успешно добавлена");
    }

    @FXML private void onUpdate() {
        Receipt sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { 
            alert("Выберите запись для редактирования"); 
            return; 
        }
        
        // Валидация
        if (cbSupplier.getValue() == null) { 
            alert("Выберите поставщика"); 
            return; 
        }
        if (cbWasteType.getValue() == null) { 
            alert("Выберите тип отходов"); 
            return; 
        }
        if (!ValidationUtil.isValidPositiveDecimal(tfWeight.getText())) {
            alert("Вес должен быть положительным числом");
            tfWeight.requestFocus();
            return;
        }
        if (cbOperator.getValue() == null) { 
            alert("Выберите оператора"); 
            return; 
        }
        
        sel.setSupplier(cbSupplier.getValue()); 
        sel.setWasteType(cbWasteType.getValue()); 
        sel.setWeightKg(new BigDecimal(ValidationUtil.trim(tfWeight.getText()))); 
        sel.setOperator(cbOperator.getValue());
        service.updateReceipt(sel); 
        loadData(); 
        clear();
        showInfo("Приёмка успешно обновлена");
    }

    @FXML private void onDelete() {
        Receipt sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { 
            alert("Выберите запись для удаления"); 
            return; 
        }
        
        // Проверка связей перед удалением
        Session session = HibernateSession.getSessionFactory().openSession();
        try {
            if (ReferenceCheckUtil.isReceiptUsed(session, sel)) {
                alertError(ReferenceCheckUtil.getReceiptUsageMessage(session, sel));
                return;
            }
        } finally {
            session.close();
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
            "Вы уверены, что хотите удалить приёмку #" + sel.getId() + "?", 
            ButtonType.YES, ButtonType.NO);
        confirmAlert.setTitle("Подтверждение удаления");
        confirmAlert.setHeaderText("Удаление приёмки");
        
        confirmAlert.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> { 
            service.deleteReceipt(sel); 
            loadData(); 
            clear(); 
            showInfo("Приёмка успешно удалена");
        });
    }

    @FXML private void onClear() { clear(); tableView.getSelectionModel().clearSelection(); }
    private void clear() { cbSupplier.setValue(null); cbWasteType.setValue(null); tfWeight.clear(); cbOperator.setValue(null); }
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

