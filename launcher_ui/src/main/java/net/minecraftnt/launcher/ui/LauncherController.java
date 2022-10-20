package net.minecraftnt.launcher.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LauncherController implements Initializable {

    public ComboBox<Profile> profiles;
    public Button play;
    public Label changelogs;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        profiles.getItems().clear();
        profiles.getItems().addAll(LauncherInfo.getProfiles());

        profiles.setValue(LauncherInfo.PROFILE_LATEST);


        changelogs.setText(LauncherInfo.getChangelog());

    }

    public int playGame() throws IOException, InterruptedException {

        String location = LauncherInfo.getLauncherFileLocation("launcher.jar");

        File launcherJar = new File(location);
        if (!launcherJar.exists()) {
            System.out.println("Downloading launcher jar");
            URL downloadURL = new URL("https://openability.tech/jimmster/minecraftnt/download/launcher_core.jar");

            if (!launcherJar.exists()) {
                FileUtils.copyURLToFile(downloadURL, launcherJar);
            }
        }


        ProcessBuilder builder = new ProcessBuilder();

        String version = profiles.getValue().getVersion();

        ArrayList<String> command = new ArrayList<>();
        command.add("java");
        command.add("-jar");
        command.add(location);
        command.add("-username");
        command.add(profiles.getValue().getUsername());

        if(version != "latest"){
            command.add("-version");
            command.add(version);
        }

        builder.command(command);


        System.out.println("Launching Minecraftn't launcher with command " + String.join(" ", builder.command()));
        Process process;
        process = builder.start();

        StreamGobbler outGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
        StreamGobbler errGobbler =
                new StreamGobbler(process.getErrorStream(), System.err::println);

        Future<?> outFuture = Executors.newSingleThreadExecutor().submit(outGobbler);
        Future<?> errFuture = Executors.newSingleThreadExecutor().submit(errGobbler);


        return process.waitFor();

    }

    public void updateLauncher() throws IOException {
        System.out.println("Downloading launcher core jar");
        String location = LauncherInfo.getLauncherFileLocation("launcher.jar");
        File versionJar = new File(location);
        URL downloadURL = new URL("https://openability.tech/jimmster/minecraftnt/download/launcher_core.jar");
        FileUtils.copyURLToFile(downloadURL, versionJar);
        System.out.println("Downloaded latest launcher core jar");
    }

    public void openAddProfile(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(LauncherApplication.class.getResource("ProfileEditor.fxml")));
            Stage stage = new Stage();
            stage.setTitle("Profile Editor");
            stage.setScene(new Scene(root, 600, 400));
            stage.setResizable(false);

            Window currentWindow = Stage.getWindows().get(0);

            stage.initOwner(currentWindow);
            stage.initModality(Modality.WINDOW_MODAL);

            stage.showAndWait();

            profiles.getItems().clear();
            profiles.getItems().addAll(LauncherInfo.getProfiles());

            profiles.setValue(LauncherInfo.PROFILE_LATEST);

            // Hide this current window (if this is what you want)
            //((Node)(event.getSource())).getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
