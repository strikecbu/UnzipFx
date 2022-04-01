package com.andy.unzipfx;


import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UnzipFxApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(UnzipFxApplication.class, View.class, args);
//        SpringApplication.run(MyApp.class, args);
    }

}
