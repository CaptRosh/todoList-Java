package com.RS.todolist;

import com.RS.todolist.DataModel.ToDoData;
import com.RS.todolist.DataModel.ToDoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    @FXML
    private ListView<ToDoItem> toDoItemListView;

    @FXML
    private TextArea itemDetails;

    @FXML
    private Label dueDate;

    @FXML
    private Label dueText;

    @FXML
    private BorderPane mainWindow;

    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private ToggleButton filterToggle;

    private FilteredList<ToDoItem> filtered;

    public void initialize() {

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                deleteItem(toDoItemListView.getSelectionModel().getSelectedItem());
            }
        });

        listContextMenu.getItems().addAll();
        toDoItemListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observableValue, ToDoItem toDoItem, ToDoItem t1) {
                if (t1 != null) {
                    ToDoItem selectedItem = toDoItemListView.getSelectionModel().getSelectedItem();
                    dueText.setVisible(true);
                    itemDetails.setText(selectedItem.getDetail());
                    DateTimeFormatter df =  DateTimeFormatter.ofPattern("d MMMM yyyy");
                    dueDate.setText(df.format(selectedItem.getDate()));
                }
            }
        });

        filtered = new FilteredList<>(ToDoData.getInstance().getToDoItems(), new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem toDoItem) {
                return true;
            }
        });

        SortedList<ToDoItem> sortedList = new SortedList<>(filtered, new Comparator<ToDoItem>() {
            @Override
            public int compare(ToDoItem o1, ToDoItem o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        toDoItemListView.setItems(sortedList);
        toDoItemListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        toDoItemListView.getSelectionModel().selectFirst();

        toDoItemListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
                ListCell<ToDoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(ToDoItem toDoItem, boolean b) {
                        super.updateItem(toDoItem, b);
                        if (b){
                            setText(null);
                        } else {
                            setText(toDoItem.getTitle());
                            if(toDoItem.getDate().isBefore(LocalDate.now().plusDays(1))){
                                setTextFill(Color.RED);
                            } else if (toDoItem.getDate().isEqual(LocalDate.now().plusDays(1))){
                                setTextFill(Color.GREEN);

                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                    (obs,wasEmpty,nowEmpty) -> {
                        if(nowEmpty){
                            cell.setContextMenu(null);
                        } else {
                            cell.setContextMenu(listContextMenu);
                        }
                    });
                return cell;
            }
        });
    }

    @FXML
    public void showNewItemBox(){
        Dialog<ButtonType> dialogue = new Dialog();
        dialogue.initOwner(mainWindow.getScene().getWindow());
        dialogue.setTitle("Add new Item");
        dialogue.setHeaderText("Create a new reminder");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("Dialogue.fxml"));
        try{
            dialogue.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e){
            System.out.print("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialogue.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialogue.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> pressed = dialogue.showAndWait();
        if(pressed.isPresent() && pressed.get() ==ButtonType.OK){
            DialogueController ok = fxmlLoader.getController();
            ToDoItem newItem = ok.process();
            toDoItemListView.getSelectionModel().select(newItem);
        }
    }
    public void handleKeyPress(KeyEvent key){
        ToDoItem selectedItem = toDoItemListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null){
            if(key.getCode().equals(KeyCode.DELETE)){
                deleteItem((selectedItem));
            }
        }
    }

    public void deleteItem(ToDoItem item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText("Delete " + item.getTitle());
        alert.setContentText("Are you sure you want to delete this reminder?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            ToDoData.getInstance().deleteItem(item);
        }
    }

    @FXML
    public void handleFilter() {
        if(filterToggle.isSelected()){
            filtered.setPredicate(new Predicate<ToDoItem>() {
                @Override
                public boolean test(ToDoItem toDoItem) {
                    return (toDoItem.getDate().equals(LocalDate.now()));
                }
            });
            toDoItemListView.getSelectionModel().selectFirst();
        } else {
            filtered.setPredicate(new Predicate<ToDoItem>() {
                @Override
                public boolean test(ToDoItem toDoItem) {
                    return true;
                }
            });
            toDoItemListView.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void handleExit(){
        Platform.exit();
    }

}