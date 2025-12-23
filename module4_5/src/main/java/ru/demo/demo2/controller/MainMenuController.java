package ru.demo.demo2.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.util.Duration;

public class MainMenuController {
    @FXML private BorderPane mainContainer;
    @FXML private VBox drawer;
    private boolean isMenuOpen = false;

    @FXML
    public void initialize() { loadView("/ru/demo/demo2/suppliers-view.fxml"); }

    @FXML
    public void toggleMenu() {
        if (isMenuOpen) closeMenu(); else openMenu();
    }

    private void openMenu() {
        isMenuOpen = true;
        drawer.setManaged(true);
        drawer.toFront();
        TranslateTransition t = new TranslateTransition(Duration.millis(250), drawer);
        t.setToX(0);
        t.play();
    }

    private void closeMenu() {
        isMenuOpen = false;
        TranslateTransition t = new TranslateTransition(Duration.millis(250), drawer);
        t.setToX(-220);
        t.setOnFinished(e -> drawer.setManaged(false));
        t.play();
    }

    @FXML private void onSuppliersClick() { closeMenu(); loadView("/ru/demo/demo2/suppliers-view.fxml"); }
    @FXML private void onWasteTypesClick() { closeMenu(); loadView("/ru/demo/demo2/waste-types-view.fxml"); }
    @FXML private void onReceiptsClick() { closeMenu(); loadView("/ru/demo/demo2/receipts-view.fxml"); }
    @FXML private void onStorageClick() { closeMenu(); loadView("/ru/demo/demo2/storage-view.fxml"); }
    @FXML private void onProcessingClick() { closeMenu(); loadView("/ru/demo/demo2/processing-view.fxml"); }
    @FXML private void onShipmentsClick() { closeMenu(); loadView("/ru/demo/demo2/shipments-view.fxml"); }
    @FXML private void onStatisticsClick() { closeMenu(); loadView("/ru/demo/demo2/statistics-view.fxml"); }
    @FXML private void onAuditLogClick() { closeMenu(); loadView("/ru/demo/demo2/audit-log-view.fxml"); }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Node view = loader.load();
            mainContainer.setCenter(view);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
