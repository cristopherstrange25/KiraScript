module com.kirascript.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.kirascript.demo to javafx.fxml;
    exports com.kirascript.demo;
}