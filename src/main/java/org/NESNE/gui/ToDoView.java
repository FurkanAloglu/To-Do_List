package org.NESNE.gui;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.NESNE.model.Priority;
import org.NESNE.model.Task;
import org.NESNE.service.TaskService;

public class ToDoView {

    private final TaskService service;

    public ToDoView(TaskService service) {
        this.service = service;
    }

    public void start(Stage stage) {

        TextField titleField = new TextField();

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Task description");
        descriptionArea.setPrefRowCount(3);

        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setPromptText("Deadline");

        ComboBox<Priority> priorityBox =
                new ComboBox<>(FXCollections.observableArrayList(Priority.values()));
        priorityBox.setValue(Priority.MEDIUM);

        Button addBtn = new Button("Add Task");

        ListView<Task> listView = new ListView<>();

        Button doneBtn = new Button("Complete");
        Button deleteBtn = new Button("Delete");

        HBox actions = new HBox(10, doneBtn, deleteBtn);
        actions.setPadding(new javafx.geometry.Insets(10));

        VBox inputForm = new VBox(8,
                new Label("Title"), titleField,
                new Label("Description"), descriptionArea,
                new Label("Priority"), priorityBox,
                new Label("Deadline"), deadlinePicker,
                addBtn
        );
        inputForm.setPadding(new javafx.geometry.Insets(10));

        BorderPane root = new BorderPane();
        root.setLeft(inputForm);
        root.setCenter(listView);
        root.setBottom(actions);

        Scene scene = new Scene(root, 800, 500);
        
        try {
            if (getClass().getResource("/style.css") != null) {
                scene.getStylesheets().add(
                        getClass().getResource("/style.css").toExternalForm()
                );
            }
        } catch (Exception e) {
            System.err.println("Could not load style.css");
        }

        stage.setTitle("To-Do List");
        stage.setScene(scene);
        stage.show();

        addBtn.setOnAction(e -> {
            service.addTask(
                    titleField.getText(),
                    descriptionArea.getText(),
                    priorityBox.getValue(),
                    deadlinePicker.getValue()
            );

            titleField.clear();
            descriptionArea.clear();
            deadlinePicker.setValue(null);
            refresh(listView);
        });


        doneBtn.setOnAction(e -> {
            Task task = listView.getSelectionModel().getSelectedItem();

            if (task == null) {
                showAlert("Please select a task.");
                return;
            }

            try {
                service.completeTask(task.getId());
                refresh(listView);
            } catch (Exception ex) {
                showAlert(ex.getMessage());
            }
        });


        deleteBtn.setOnAction(e -> {
            Task task = listView.getSelectionModel().getSelectedItem();

            if (task == null) {
                showAlert("Please select a task.");
                return;
            }

            try {
                service.deleteTask(task.getId());
                refresh(listView);
            } catch (Exception ex) {
                showAlert(ex.getMessage());
            }
        });


        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                getStyleClass().removeAll(
                        "done", "todo",
                        "priority-HIGH", "priority-MEDIUM", "priority-LOW"
                );

                if (empty || task == null) {
                    setText(null);
                } else {
                    setText(task.toString());

                    getStyleClass().add(task.isCompleted() ? "done" : "todo");
                    getStyleClass().add("priority-" + task.getPriority().name());
                }
            }
        });

        refresh(listView);
    }

    private void refresh(ListView<Task> listView) {
        listView.getItems().setAll(service.getAllTasks());
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.show();
    }
}
