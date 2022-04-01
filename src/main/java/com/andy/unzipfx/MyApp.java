package com.andy.unzipfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MyApp extends Application {
//public class MyApp {

//    //主窗口
    private Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mainStage = primaryStage;
        //加載登錄頁面
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/unzip.fxml"));
        Parent root = loader.load();
        UnzipController controller = loader.getController();
        controller.setApp(this);
        Scene scene = new Scene(root);
        //設置樣式
        //scene.getStylesheets().add(getClass().getResource("/css/jfoenix-components.css").toExternalForm());
        //綁到頂層容器上
        primaryStage.setScene(scene);
        //設置標題
        primaryStage.setTitle("IISI解壓縮小工具");
        //展示窗口
        primaryStage.show();
    }

    /**
     * 隱藏當前窗口
     */
    public void hideWindow(){
        mainStage.hide();
    }
}
