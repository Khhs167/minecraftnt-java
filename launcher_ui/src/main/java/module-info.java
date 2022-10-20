module net.minecraftnt.launcher_ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.apache.commons.io;
    requires com.google.gson;

    opens net.minecraftnt.launcher.ui to javafx.fxml;
    exports net.minecraftnt.launcher.ui;
}