package ru.demo.demo2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.demo.demo2.model.AuditLog;
import ru.demo.demo2.repository.AuditLogDao;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AuditLogController {
    @FXML private TableView<AuditLog> tableView;
    @FXML private TableColumn<AuditLog, Integer> colId, colEntityId;
    @FXML private TableColumn<AuditLog, String> colTimestamp, colUser, colAction, colEntityType, colOldValue, colNewValue;
    @FXML private DatePicker dpFrom, dpTo;
    @FXML private TextField tfSearch;

    private final AuditLogDao dao = new AuditLogDao();
    private ObservableList<AuditLog> list = FXCollections.observableArrayList();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTimestamp.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTimestamp().format(dtf)));
        colUser.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUserLogin()));
        colAction.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getActionDisplayName()));
        colEntityType.setCellValueFactory(new PropertyValueFactory<>("entityType"));
        colEntityId.setCellValueFactory(new PropertyValueFactory<>("entityId"));
        colOldValue.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
        colNewValue.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        
        dpFrom.setValue(LocalDate.now().minusMonths(1));
        dpTo.setValue(LocalDate.now());
        loadData();
        tfSearch.textProperty().addListener((obs, o, n) -> filter(n));
    }

    private void loadData() {
        if (dpFrom.getValue() != null && dpTo.getValue() != null)
            list.setAll(dao.findByDateRange(dpFrom.getValue().atStartOfDay(), dpTo.getValue().plusDays(1).atStartOfDay()));
        else list.setAll(dao.findAll());
        tableView.setItems(list);
    }

    private void filter(String s) { 
        if (s == null || s.isEmpty()) { 
            tableView.setItems(list); 
            return; 
        }
        String searchLower = s.toLowerCase();
        tableView.setItems(list.filtered(l -> 
            l.getUserLogin().toLowerCase().contains(searchLower) ||
            l.getActionDisplayName().toLowerCase().contains(searchLower) ||
            l.getEntityType().toLowerCase().contains(searchLower) ||
            l.getId().toString().contains(searchLower) ||
            (l.getOldValue() != null && l.getOldValue().toLowerCase().contains(searchLower)) ||
            (l.getNewValue() != null && l.getNewValue().toLowerCase().contains(searchLower))
        )); 
    }

    @FXML private void onRefresh() { loadData(); }
    @FXML private void onClearFilter() { dpFrom.setValue(LocalDate.now().minusMonths(1)); dpTo.setValue(LocalDate.now()); tfSearch.clear(); loadData(); }
}

