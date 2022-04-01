package com.andy.unzipfx;

import com.andy.unzipfx.util.ZipFileUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class UnzipController implements Initializable {

    @FXML
    Button chooseFolderBtn;
    @FXML
    ProgressBar progress;

    @FXML
    Button unzipBtn;

    @FXML
    TextField folderPath;

    @FXML
    PasswordField password;

    private boolean onProcess = false;


    private MyApp myApp;

    public void setApp(MyApp myApp) {
        this.myApp = myApp;
    }

    /**
     * 初始化方法
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chooseFolderBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Choose Folder");
                File directory = directoryChooser.showDialog(myApp.getMainStage());
                if (directory != null) {
                    setChooseFolder(directory.getAbsolutePath());
                }
            }
        });
    }

    void setChooseFolder(String folder) {
        this.folderPath.setText(folder);
    }

    @FXML
    public void unzip(ActionEvent actionEvent) throws InterruptedException {
        if (onProcess) {
            return;
        }
        onProcess = true;
        String folderPath = this.folderPath.getText();
        String password = this.password.getText();
        File folder = new File(folderPath);
        String errorMsg = "";
        if ("".equals(folderPath.trim())) {
            errorMsg = "資料夾路徑不能爲空！";
        } else if (!folder.exists() || !folder.isDirectory()) {
            errorMsg = "請確認資料夾路徑是否正確";
        } else if ("".equals(password.trim())) {
            errorMsg = "解壓縮密碼不能爲空！";
        }
        if (!"".equals(errorMsg)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(errorMsg);
            alert.show();
            resetFn();
            return;
        }
        progress.setVisible(true);

        Task task = createTask(folderPath, password);
        progress.progressProperty().unbind();
        progress.progressProperty().bind(task.progressProperty());

        task.messageProperty().addListener((observable, oldValue, newValue) -> {
            String[] info = newValue.split("___");
            //取得結果
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if ("ERROR".equals(info[0])) {
                alert = new Alert(Alert.AlertType.ERROR);
            }
            alert.setContentText(info[1]);
            alert.show();
            resetFn();
        });
        task.progressProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                if (Double.compare((Double) t1, 1.0) == 0) {
                    resetFn();
                }
            }
        });

        new Thread(task).start();
    }

    void resetFn() {
        this.progress.setVisible(false);
        this.onProcess = false;
    }

    public Task createTask(String folderPath, String password) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                // check and clean previous combine.zip
                File folder = new File(folderPath);
                File combineFolder = new File(folder, "combine");
                if (combineFolder.exists()) {
                    FileUtils.forceDelete(combineFolder);
                }
                updateProgress(2, 10);

                // collection all zip
                List<File> files = Arrays.asList(
                        Objects.requireNonNull(
                                folder.listFiles((file) -> file.getName().endsWith(".vip.zip"))
                        ));
                if (files.size() == 0) {
                    updateMessage("ERROR___找不到要解壓縮對象，請確認資料夾是否正確!");
                    return false;
                }
                updateProgress(4, 10);
                // combine
                try {
                    files = ZipFileUtil.unzipAllFiles(files, password);
                    updateProgress(5, 10);
                    files = ZipFileUtil.changeFileType(files, null);
                    updateProgress(7, 10);
                    File combineFile = ZipFileUtil.splitToFile(files, "zip");
                    updateProgress(8, 10);
                    // move to combine folder
                    new ZipFile(combineFile).extractAll(new File(folder, "combine").getAbsolutePath());
                    updateProgress(10, 10);
                } catch (Exception e) {
                    updateMessage("ERROR___" + e.getMessage());
                    return false;
                }
                updateMessage("INFO___解壓縮完成");
                return true;
            }
        };
    }
}
