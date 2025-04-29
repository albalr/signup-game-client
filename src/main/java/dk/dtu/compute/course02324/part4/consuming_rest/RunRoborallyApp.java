package dk.dtu.compute.course02324.part4.consuming_rest;

import dk.dtu.compute.course02324.part4.consuming_rest.view.MainView;
import javafx.application.Application;
import javafx.stage.Stage;

public class RunRoborallyApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        mainView.show(primaryStage);
    }
}