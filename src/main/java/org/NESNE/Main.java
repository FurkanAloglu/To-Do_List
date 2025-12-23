package org.NESNE;

import javafx.application.Application;
import javafx.stage.Stage;
import org.NESNE.gui.ToDoView;
import org.NESNE.service.TaskService;
import org.NESNE.service.TaskServiceImpl;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        TaskService service = new TaskServiceImpl();
        new ToDoView(service).start(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
