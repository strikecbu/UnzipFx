package com.andy.unzipfx;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 登錄
 *
 * @author 程就人生
 * @Date
 */
@Component
public class UnzipController implements Initializable {

    @FXML
    ProgressBar progress;

    @FXML
    Button unzipBtn;

    @FXML
    TextField folderPath;

    @FXML
    PasswordField password;

    double nowProgress = 0;


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
    }

    @FXML
    public void unzip(ActionEvent actionEvent) throws InterruptedException {
        String folderPath = this.folderPath.getText();
        String password = this.password.getText();
        File folder = new File(folderPath);
        String errorMsg = "";
        if (StringUtils.isEmpty(folderPath)) {
            errorMsg = "資料夾路徑不能爲空！";
        } else if (folder.exists() || !folder.isDirectory()) {
//            errorMsg = "請確認資料夾路徑是否正確";
        } else if (StringUtils.isEmpty(password)) {
            errorMsg = "解壓縮密碼不能爲空！";
        }
        if (!"".equals(errorMsg)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(errorMsg);
            alert.show();
            return;
        }
        progress.setVisible(true);

        Task task = createTask();
        progress.progressProperty().unbind();
        progress.progressProperty().bind(task.progressProperty());

        task.messageProperty().addListener((observable, oldValue, newValue) -> {
            //取得結果
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(newValue);
            alert.show();
        });

        new Thread(task).start();
    }

    public Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                updateProgress(3, 10);
                Thread.sleep(3000);
                updateProgress(6, 10);
                Thread.sleep(2000);
                updateProgress(10, 10);
                updateMessage("It's done");

                return false;
            }
        };
    }
}
