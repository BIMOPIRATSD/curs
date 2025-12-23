package ru.demo.demo2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;
import ru.demo.demo2.model.Supplier;
import ru.demo.demo2.repository.SupplierDao;
import ru.demo.demo2.util.HibernateSession;
import ru.demo.demo2.util.ReferenceCheckUtil;
import ru.demo.demo2.util.ValidationUtil;

public class SuppliersController {
    @FXML private TableView<Supplier> tableView;
    @FXML private TableColumn<Supplier, Integer> colId;
    @FXML private TableColumn<Supplier, String> colName, colType, colPhone, colEmail;
    @FXML private TextField tfName, tfPhone, tfEmail, tfSearch;
    @FXML private ComboBox<String> cbType;

    private final SupplierDao dao = new SupplierDao();
    private ObservableList<Supplier> list = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTypeDisplayName()));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        cbType.setItems(FXCollections.observableArrayList("Физ. лицо", "Юр. лицо"));
        cbType.getSelectionModel().selectFirst();
        loadData();
        
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) { tfName.setText(n.getName()); cbType.setValue(n.getTypeDisplayName()); tfPhone.setText(n.getPhone()); tfEmail.setText(n.getEmail()); }
        });
        tfSearch.textProperty().addListener((obs, o, n) -> filter(n));
    }

    private void loadData() { list.setAll(dao.findAll()); tableView.setItems(list); }

    private void filter(String s) {
        if (s == null || s.isEmpty()) { tableView.setItems(list); return; }
        String searchLower = s.toLowerCase();
        tableView.setItems(list.filtered(x -> 
            x.getName().toLowerCase().contains(searchLower) ||
            x.getTypeDisplayName().toLowerCase().contains(searchLower) ||
            (x.getPhone() != null && x.getPhone().contains(s)) ||
            (x.getEmail() != null && x.getEmail().toLowerCase().contains(searchLower))
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

        String email = ValidationUtil.trim(tfEmail.getText());
        if (ValidationUtil.isNotEmpty(email) && !ValidationUtil.isValidEmail(email)) {
            alert("Некорректный формат email");
            tfEmail.requestFocus();
            return;
        }

        String phone = ValidationUtil.trim(tfPhone.getText());
        if (!ValidationUtil.isValidPhone(phone)) {
            alert("Некорректный формат телефона\n(10-15 цифр, допускается +, пробелы, дефисы, скобки)");
            tfPhone.requestFocus();
            return;
        }
        
        Supplier e = new Supplier(); 
        e.setName(name); 
        e.setType(cbType.getValue().equals("Физ. лицо") ? "F" : "L"); 
        e.setPhone(phone.isEmpty() ? null : phone); 
        e.setEmail(email.isEmpty() ? null : email);
        dao.save(e); 
        loadData(); 
        clear();
        showInfo("Поставщик успешно добавлен");
    }

    @FXML private void onUpdate() {
        Supplier sel = tableView.getSelectionModel().getSelectedItem();
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

        String email = ValidationUtil.trim(tfEmail.getText());
        if (ValidationUtil.isNotEmpty(email) && !ValidationUtil.isValidEmail(email)) {
            alert("Некорректный формат email");
            tfEmail.requestFocus();
            return;
        }

        String phone = ValidationUtil.trim(tfPhone.getText());
        if (!ValidationUtil.isValidPhone(phone)) {
            alert("Некорректный формат телефона\n(10-15 цифр, допускается +, пробелы, дефисы, скобки)");
            tfPhone.requestFocus();
            return;
        }
        
        sel.setName(name); 
        sel.setType(cbType.getValue().equals("Физ. лицо") ? "F" : "L"); 
        sel.setPhone(phone.isEmpty() ? null : phone); 
        sel.setEmail(email.isEmpty() ? null : email);
        dao.update(sel); 
        loadData(); 
        clear();
        showInfo("Поставщик успешно обновлен");
    }

    @FXML private void onDelete() {
        Supplier sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null) { 
            alert("Выберите запись для удаления"); 
            return; 
        }

        Session session = HibernateSession.getSessionFactory().openSession();
        try {
            if (ReferenceCheckUtil.isSupplierUsed(session, sel)) {
                alertError(ReferenceCheckUtil.getSupplierUsageMessage(session, sel));
                return;
            }
        } finally {
            session.close();
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
            "Вы уверены, что хотите удалить поставщика '" + sel.getName() + "'?", 
            ButtonType.YES, ButtonType.NO);
        confirmAlert.setTitle("Подтверждение удаления");
        confirmAlert.setHeaderText("Удаление поставщика");
        
        confirmAlert.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> { 
            dao.delete(sel); 
            loadData(); 
            clear(); 
            showInfo("Поставщик успешно удален");
        });
    }

    @FXML private void onClear() { clear(); tableView.getSelectionModel().clearSelection(); }
    private void clear() { tfName.clear(); cbType.getSelectionModel().selectFirst(); tfPhone.clear(); tfEmail.clear(); }
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

