package net.minecraftnt.launcher.ui;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.Effect;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ProfileEditor implements Initializable {
    public TextField nameBox;
    public ComboBox<String> versionsBox;
    public TextField usernameBox;
    public ListView<Profile> profilesList;
    public VBox editorBox;
    public Profile current;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        profilesList.getItems().clear();
        profilesList.getItems().addAll(LauncherInfo.getProfiles());

        versionsBox.getItems().clear();
        versionsBox.getItems().addAll(LauncherInfo.getVersions());

        editorBox.setDisable(true);

        profilesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, profile) -> {

            current = profile;

            if(profile == null){
                editorBox.setDisable(true);
                return;
            }

            editorBox.setDisable(false);

            boolean disabled = Objects.equals(current.getName(), "Latest");

            versionsBox.setDisable(disabled);
            nameBox.setDisable(disabled);

            versionsBox.setValue(current.getVersion());
            nameBox.setText(current.getName());
            usernameBox.setText(current.getUsername());


        });

    }

    public void saveProfile(ActionEvent event) {
        LauncherInfo.saveProfiles();

        current.setName(nameBox.getText());
        current.setVersion(versionsBox.getValue());
        current.setUsername(usernameBox.getText());

        profilesList.getItems().clear();
        profilesList.getItems().addAll(LauncherInfo.getProfiles());

        profilesList.getSelectionModel().select(current);
    }


    public void addProfile(ActionEvent event) {

        Profile profile = new Profile();

        profile.setName("New profile");
        profile.setVersion(LauncherInfo.getVersions()[0]);
        profile.setUsername("player");

        profilesList.getItems().add(profile);
        LauncherInfo.addProfile(profile);
        LauncherInfo.saveProfiles();

    }

    public void removeProfile(ActionEvent event) {

        if(current == null)
            return;

        if(Objects.equals(current.getName(), "Latest")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot delete Latest profile!");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete profile " + current.getName() + "?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();
        if(alert.getResult() != ButtonType.YES)
            return;

        LauncherInfo.removeProfile(current);
        LauncherInfo.saveProfiles();

        profilesList.getItems().remove(current);
        profilesList.getSelectionModel().select(null);


        alert = new Alert(Alert.AlertType.INFORMATION, "Profile deleted", ButtonType.OK);
        alert.showAndWait();

    }
}
