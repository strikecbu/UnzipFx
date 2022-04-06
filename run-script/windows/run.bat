@echo off

set PATH_TO_FX=".\javafx-sdk-18\lib"
echo This program is running...

java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -jar ./unzipFx.jar
