#!/bin/sh 
export PATH_TO_FX=./javafx-sdk-18/lib 
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -jar unzipFx.jar    