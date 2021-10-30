package model;

public class TaskNameCompare implements java.util.Comparator<ToDoTask> {

    @Override
    public int compare(ToDoTask t1, ToDoTask t2) {

        return t1.getName().compareTo(t2.getName());

    }

}