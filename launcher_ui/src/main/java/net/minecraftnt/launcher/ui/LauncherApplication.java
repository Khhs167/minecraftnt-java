package net.minecraftnt.launcher.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LauncherApplication extends Application {

    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Profile.class, new ProfileJSONAdapter()).create();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader splashLoader = new FXMLLoader(LauncherApplication.class.getResource("launcher.fxml"));
        Scene scene = new Scene(splashLoader.load(), 1024, 512);
        scene.getStylesheets().addAll(Objects.requireNonNull(this.getClass().getResource("style.css")).toExternalForm());

        Font.loadFont(
                Objects.requireNonNull(LauncherApplication.class.getResource("minecraftia.ttf")).toExternalForm(),
                10
        );

        stage.setTitle("Minecraftn't launcher");
        stage.setScene(scene);
        stage.show();

        Platform.runLater( () -> scene.getRoot().requestFocus() );
    }

    public static void main(String[] args) {
        LauncherInfo.loadProfiles();
        launch(args);
        LauncherInfo.saveProfiles();
    }
}