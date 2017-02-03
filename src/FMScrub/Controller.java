package FMScrub;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.control.TextField;
import java.io.File;

public class Controller {

    String mInputFile;
    String mOutputFile;
    String mOutputPath;
    final FileChooser fileChooser = new FileChooser();

    @FXML
    TextField inputFileField;

    @FXML TextField outputLocationField;

    @FXML TextField outputNameField;


    public void doScrub() {

        if (inputFileField.getText() == "" || outputLocationField.getText() == "" || outputNameField.getText() == "") {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("All fields must be filled-out.");
            alert.showAndWait();
            return;
        }

        if (outputNameField.getText() != "") {
            mOutputFile = outputNameField.getText();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("You want help?");
            alert.setContentText("Ask Carmine.");
            alert.showAndWait();
            return;
        }

        if (mOutputPath != "" && mOutputFile != "" && mInputFile != ""){
            String outputFile = mOutputPath + "/" + mOutputFile;

            if (outputFile.length() > 4) {
                if (outputFile.substring(outputFile.length() - 4) != ".csv" || outputFile.substring(outputFile.length() - 4) != ".CSV") {
                    outputFile = outputFile + ".csv";
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setContentText("Filename must be at least 5 characters.");
                alert.showAndWait();
                return;

            }

            CSVFile fileToScrub = new CSVFile(mInputFile, outputFile);
        }

    }

    public void chooseInputFile() {

        Stage fileStage = new Stage();
        File initialDirectory = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(initialDirectory);
        File file = fileChooser.showOpenDialog(fileStage);
        if (file.toString() != "") {
            mInputFile = file.toString();
            inputFileField.setText(mInputFile);
        }
    }

    public void chooseOutputLocation() {

        Stage directoryStage = new Stage();
        File initialDirectory = new File(System.getProperty("user.home"));
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(initialDirectory);
        File selectedDirectory = directoryChooser.showDialog(directoryStage);
        if (selectedDirectory != null) {
            mOutputPath = selectedDirectory.getAbsolutePath();
            System.out.printf("Directory: %s", mOutputPath);
            outputLocationField.setText(mOutputPath);

        }
    }

    public void menuFileExit() {
        System.exit(0);
    }

    public void menuHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("You want help?");
        alert.setContentText("Ask Carmine.");
        alert.showAndWait();

    }
}
