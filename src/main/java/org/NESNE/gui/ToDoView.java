package org.NESNE.gui;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.NESNE.model.Priority;
import org.NESNE.model.Task;
import org.NESNE.service.TaskService;

import java.util.List;

public class ToDoView {

    private final TaskService service;

    // Tarih formatı (Sınıf seviyesinde tanımladık ki her yerde erişelim)
    java.time.format.DateTimeFormatter dateFormatter =
            java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy");

    public ToDoView(TaskService service) {
        this.service = service;
    }

    public void start(Stage stage) {

        // 1. ÖNCE BİLEŞENLERİ TANIMLIYORUZ (Sıra Önemli!)

        // Arama Kutusu (En başa koyuyoruz)
        TextField searchField = new TextField();
        searchField.setPromptText("Görev Ara...");

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

        // 2. LAYOUT (DÜZEN) AYARLARI
        HBox actions = new HBox(10, doneBtn, deleteBtn);
        actions.setPadding(new javafx.geometry.Insets(10));

        // ... (Üstteki tanımlamalar aynı kalsın) ...

        // --- YENİ EKLENEN KISIM BAŞLANGICI ---

        Button clearBtn = new Button("Clear");
        // Biraz farklı görünsün diye stil verebiliriz (Opsiyonel)
        clearBtn.setStyle("-fx-background-color: #555; -fx-text-fill: white;");

        clearBtn.setOnAction(e -> {
            // 1. Yazı alanlarını temizle
            titleField.clear();
            descriptionArea.clear();

            // 2. Seçim kutularını sıfırla
            deadlinePicker.setValue(null);
            priorityBox.setValue(Priority.MEDIUM);

            // 3. Listeden seçimi kaldır (Önemli! Yoksa hala o görevi düzenliyor sanırsın)
            listView.getSelectionModel().clearSelection();
        });

        // Ekle ve Temizle butonlarını yan yana koyalım
        HBox inputButtonBox = new HBox(10, addBtn, clearBtn);

        // --- YENİ EKLENEN KISIM BİTİŞİ ---

        // VBox inputForm kısmını güncelle:
        VBox inputForm = new VBox(8,
                searchField,
                new Label("Title"), titleField,
                new Label("Description"), descriptionArea,
                new Label("Priority"), priorityBox,
                new Label("Deadline"), deadlinePicker,
                inputButtonBox // <-- ARTIK SADECE addBtn DEĞİL, İKİSİ BİRDEN VAR
        );
        inputForm.setPadding(new javafx.geometry.Insets(10));

        // ... (Geri kalan kodlar aynı) ...

        BorderPane root = new BorderPane();
        root.setLeft(inputForm);
        root.setCenter(listView);
        root.setBottom(actions);

        Scene scene = new Scene(root, 900, 600); // Biraz genişlettim

        // CSS Yükleme
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

        // 3. OLAYLAR (ACTIONS)

        // Ekleme Butonu
        addBtn.setOnAction(e -> {
            service.addTask(
                    titleField.getText(),
                    descriptionArea.getText(),
                    priorityBox.getValue(),
                    deadlinePicker.getValue()
            );

            // Temizlik
            titleField.clear();
            descriptionArea.clear();
            deadlinePicker.setValue(null);

            // Listeyi yenile (Arama metnine göre)
            refresh(listView, searchField.getText());
        });

        // Tamamlama Butonu
        doneBtn.setOnAction(e -> {
            Task task = listView.getSelectionModel().getSelectedItem();
            if (task == null) {
                showAlert("Please select a task.");
                return;
            }
            try {
                service.completeTask(task.getId());
                refresh(listView, searchField.getText());
            } catch (Exception ex) {
                showAlert(ex.getMessage());
            }
        });

        // Silme Butonu
        deleteBtn.setOnAction(e -> {
            Task task = listView.getSelectionModel().getSelectedItem();
            if (task == null) {
                showAlert("Please select a task.");
                return;
            }
            try {
                service.deleteTask(task.getId());
                refresh(listView, searchField.getText());
            } catch (Exception ex) {
                showAlert(ex.getMessage());
            }
        });

        // --- LİSTENERLER (DİNLEYİCİLER) ---

        // Arama Yapıldıkça Çalışan Listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            refresh(listView, newVal);
        });

        // Listeden Seçim Yapılınca Çalışan Listener (Detayları Doldurur)
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                titleField.setText(newSelection.getTitle());
                descriptionArea.setText(newSelection.getDescription());
                priorityBox.setValue(newSelection.getPriority());
                deadlinePicker.setValue(newSelection.getDeadline());
            }
        });

        // --- GÖRÜNÜM AYARLARI (CELL FACTORY) ---

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                // Temizlik
                getStyleClass().removeAll("done", "todo", "priority-HIGH", "priority-MEDIUM", "priority-LOW");

                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);
                    setMouseTransparent(true);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setMouseTransparent(false);

                    // Tarih Formatı
                    String formattedDate = (task.getDeadline() != null)
                            ? task.getDeadline().format(dateFormatter)
                            : "Tarih Yok";

                    // GÖRSEL DÜZELTME: Tamamlandıysa başına [BİTTİ] veya ✓ koyuyoruz
                    String statusPrefix = task.isCompleted() ? "✓ " : "";

                    // Tamamlandıysa yazıyı daha farklı gösterelim
                    String displayText = String.format("%s%s  |  %s  |  %s",
                            statusPrefix,
                            task.getTitle(),
                            task.getPriority(),
                            formattedDate
                    );

                    setText(displayText);

                    // CSS Sınıflarını Ekle
                    getStyleClass().add(task.isCompleted() ? "done" : "todo");
                    getStyleClass().add("priority-" + task.getPriority().name());

                    // EKSTRA GARANTİ: CSS çalışmasa bile kodla renk değiştir (Sarı/Gri)
                    if (task.isCompleted()) {
                        setStyle("-fx-text-fill: #555555; -fx-font-style: italic;"); // Sönük ve İtalik
                    } else {
                        setStyle("-fx-text-fill: white;");
                    }
                }
            }
        });

        // Uygulama ilk açıldığında listeyi doldur
        refresh(listView, "");
    }

    // --- YARDIMCI METOTLAR ---

    // Arama destekli yenileme metodu
    private void refresh(ListView<Task> listView, String searchText) {
        List<Task> allTasks = service.getAllTasks();

        if (searchText == null || searchText.isEmpty()) {
            listView.getItems().setAll(allTasks);
        } else {
            // Arama filtresi (Büyük/küçük harf duyarsız)
            var filtered = allTasks.stream()
                    .filter(t -> t.getTitle().toLowerCase().contains(searchText.toLowerCase()))
                    .toList();
            listView.getItems().setAll(filtered);
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.show();
    }


}