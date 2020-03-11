package com.RS.todolist.DataModel;

import java.time.LocalDate;

public class ToDoItem {
    private String title;
    private String detail;
    private LocalDate date;

    public ToDoItem(String title, String desc, LocalDate date) {
        this.title = title;
        detail = desc;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public LocalDate getDate() {
        return date;
    }

}