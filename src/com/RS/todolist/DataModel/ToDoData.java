package com.RS.todolist.DataModel;

        import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

public class ToDoData {
    public static ToDoData instance = new ToDoData();
    public static String filename = "TodoItems.txt";
    public static ObservableList<ToDoItem> toDoItems;
    public static DateTimeFormatter dateFormat;

    public static ToDoData getInstance(){
        return instance;
    }

    private ToDoData(){
        dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public static ObservableList<ToDoItem> getToDoItems() {
        return toDoItems;
    }

    public static void addItem(ToDoItem item){
        toDoItems.add(item);
    }

    public void loadToDoItems() throws IOException{
        toDoItems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);

        String input;

        try{
            while((input= br.readLine()) != null){
                String[] split = input.split(",");
                String title = split[0];
                String details = split[1];
                String dateString = split[2];

                LocalDate date = LocalDate.parse(dateString,dateFormat);
                ToDoItem item = new ToDoItem(title,details,date);
                toDoItems.add(item);
            }
        } finally {
            if(br != null){
                br.close();
            }
        }
    }

    public void storeToDoItems() throws IOException{
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);

        try{
            Iterator<ToDoItem> iter = toDoItems.iterator();
            while (iter.hasNext()){
                ToDoItem item = iter.next();
                bw.write(String.format("%s,%s,%s",item.getTitle(),item.getDetail(),item.getDate().format(dateFormat)));
                bw.write("\n");
            }
        } finally {
            if(bw != null){
                bw.close();
            }
        }
    }
    public void deleteItem(ToDoItem item){
        toDoItems.remove(item);
    }
}
