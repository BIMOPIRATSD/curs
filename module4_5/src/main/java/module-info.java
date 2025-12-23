module ru.demo.demo2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires org.hibernate.validator;
    requires java.naming;
    requires java.sql;
    requires java.desktop;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires org.jfree.jfreechart;

    opens ru.demo.demo2.controller to javafx.fxml;
    opens ru.demo.demo2 to javafx.fxml;
    opens ru.demo.demo2.model to org.hibernate.orm.core, javafx.base;
    opens ru.demo.demo2.service to javafx.base;
    exports ru.demo.demo2;
}
