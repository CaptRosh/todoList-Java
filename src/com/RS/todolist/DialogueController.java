package com.RS.todolist;

import com.RS.todolist.DataModel.ToDoData;
import com.RS.todolist.DataModel.ToDoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.time.LocalDate;

public class DialogueController {

    @FXML
    private TextField titleText;

    @FXML
    private TextArea detailsText;

    @FXML
    private DatePicker dateText;

    public ToDoItem process(){
        String title = titleText.getText().trim();
        String details = detailsText.getText().trim();
        LocalDate date = dateText.getValue();

        ToDoItem item = new ToDoItem(title,details,date);
        ToDoData.getInstance().addItem(item);
        return item;
    }
}

