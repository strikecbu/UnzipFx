package com.andy.unzipfx;

import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 主頁面
 * @author 程就人生
 * @Date
 */
@Component
public class MainController implements Initializable {

    private MyApp myApp;

    private Stage stage;

    public void setApp(MyApp myApp) {
        this.myApp = myApp;
    }

    public void setStage(Stage _stage){
        this.stage = _stage;
    }

    /**
     * 初始化方法
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
