package ru.demo.demo2.util;

import javafx.scene.control.*;
import javafx.collections.transformation.FilteredList;


public class FilterComboBox {
    public static <T> void makeSearchable(ComboBox<T> comboBox) {
        comboBox.setEditable(true);
        
        comboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = comboBox.getEditor();
            final T selected = comboBox.getSelectionModel().getSelectedItem();

            if (selected != null && newValue.equals(comboBox.getConverter().toString(selected))) {
                return;
            }

            FilteredList<T> filteredList = new FilteredList<>(comboBox.getItems(), p -> true);
            
            if (newValue == null || newValue.isEmpty()) {
                filteredList.setPredicate(p -> true);
            } else {
                String lowerCaseFilter = newValue.toLowerCase();
                filteredList.setPredicate(item -> {
                    String itemString = comboBox.getConverter().toString(item);
                    return itemString != null && itemString.toLowerCase().contains(lowerCaseFilter);
                });
            }
            
            comboBox.getItems().setAll(filteredList);
            comboBox.show();
        });
    }
}
