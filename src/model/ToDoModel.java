package model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observer;

public class ToDoModel implements Serializable {
	private static final long serialVersionUID = 1L;
	// Can change way to hold lists.
	private ArrayList<ToDoList> lists;
	private int curList;
	private transient Observer observer;
	
	/**
	 * Creates a List of ToDoLists that has only one ToDoList with no Tasks.
	 */
	public ToDoModel() {
	    this.lists = new ArrayList<ToDoList>();
	    this.lists.add(new ToDoList());
	    curList = 0;
	    observer = null;
	}
	
	/**
	 * Changes the currentList color to the specified color.
	 * 
	 * @param color The color the list is changing to.
	 */
	public void changeColor(String color) {
		lists.get(curList).setColor(color);
		loadView();
	}
	
	/**
	 * Adds a new empty list to the list of lists.
	 * 
	 * The view will be automatically added to this new list provided
	 * that the addObserver method was used at least once before the calling
	 * of this method.
	 * 
	 * @param name The name of the new ToDoList.
	 */
	public void addList(String name) {
	    lists.add(new ToDoList(name));
	    curList = lists.size()-1;
	    lists.get(curList).addObserver(observer);
	    loadView();
	}
	
	/**
	 * Deletes the current list.
	 * 
	 * This method does not check to make sure that there is more than 1 list
	 * to be deleted. That is the responsibility of whatever called this to
	 * check to ensure that it is fine to delete.
	 */
	public void deleteList() {
		int toBeRemoved = curList;
		// Switches to the next list because the current one is about to be
		// removed.
		nextList();
		lists.remove(toBeRemoved);
	}
	
	/**
	 * Iterates to the next list. Will loop back to the start if there is no
	 * next list.
	 */
	public void nextList() {
		if (curList + 1 >= lists.size()) {
			// Case where list should loop back to beginning
			curList = 0;
		} else {
			curList++;
		}
		loadView();
	}
	
	/**
	 * Iterates to the previous list. Will loop to the end of the lists if this
	 * function is called when the curList is the first list.
	 */
	public void prevList() {
		if (curList - 1 < 0) {
			// Case where list should loop to the end
			curList = lists.size()-1;
		} else {
			curList--;
		}
		loadView();
	}
	
	/**
	 * @return True if the model holds more than one list currently.
	 */
	public boolean moreThanOneList() {
		return lists.size() > 1;
	}
	
	/**
	 * Renames the current list to the given name and then notifies
	 * the view that the list has been renamed.
	 * 
	 * @param name The new name of the list.
	 */
	public void renameList(String name) {
		lists.get(curList).renameList(name);
		loadView();
	}
	
	/**
	 * Adds a new task with the given parameters.
	 * 
	 * @param name The name of the task.
	 * @param description The description/notes about the task.
	 * @param deadline The deadline for the task.
	 * @param importance Indication of whether the task is important or not.
	 * @param location The location of the task.
	 */
	public void addTask(String name, String description, String deadline, 
			String importance,String location) { 
	    if(lists.size() > 0) {
	    	lists.get(curList).addTask(name, description, deadline, importance,location);
	    	// Sorts the newly added list.
	    	sort(lists.get(curList).getCurrentSorting());
	    } else {
	    	System.out.println("THERE IS NO LIST TO ADD TO");
	    }
	}
	
	/**
	 * Removes the task at the given index.
	 * 
	 * @param index The task's index.
	 */
	public void removeTask(int index) {
	    this.lists.get(curList).removeTask(index);
	    loadView();
	}
	
	
	
	/**
	 * Adds an Observer to all of the lists held in the model.
	 * 
	 * Will also store the Observer so that this model can add this
	 * Observer to any newly created lists.
	 * 
	 * @param view The Observer.
	 */
	@SuppressWarnings("deprecation")
	public void addObserver(Observer view) {
		observer = view;
		
		for (int i = 0; i < lists.size(); i++) {
			lists.get(i).addObserver(observer);
		}
	}
	
	
	/**
	 * Saves this controller which houses all the lists for the ToDo lists
	 * in the file save.dat
	 * 
	 * @throws IOException Means that the file was not able to be written to.
	 */
	public void saveLists() throws IOException {
		FileOutputStream file = new FileOutputStream("save.dat");
		ObjectOutputStream oos = new ObjectOutputStream(file);
		oos.writeObject(this);
		oos.close();
	}
	
	/**
	 * Loads the current List.
	 */
	public void loadView() {
		lists.get(curList).loadView();
	}
	
	/**
	 * Changes the task at the position curr's completion status.
	 *  
	 * @param importance The new task importance status.
	 * @param curr The index of the task to have their importance status
	 *             changed.
	 */
	public void changeImportance(String importance, int curr) {
		lists.get(curList).getTask(curr).setImportance(importance);
		if (lists.get(curList).getCurrentSorting().equals("Importance")) {
			// Must sort the new Important task.
			sort("Importance");
		} else {
			loadView();
		}
	}
	
	/**
	 * Changes the task at the position curr's completion status.
	 *  
	 * @param complete The new task completion status.
	 * @param curr The index of the task to have their completion status
	 *             changed.
	 */
	public void changeCompletion(boolean complete, int curr) {
		lists.get(curList).getTask(curr).setCompletion(complete);
		if (lists.get(curList).getHideComplete()) {
			// Means this task must be now hidden.
			hideCompletedTask();
		} else {
			loadView();
		}
	}
	
	/**
	 * Sorts the current list by the given sorting.
	 * 
	 * Possible ways that the list can be sorted by:
	 * Name, Deadline, Importance, Create Time, Custom
	 * 
	 * @param sortBy The given sorting as String.
	 */
	public void sort(String sortBy){
		switch (sortBy) {
			case "Name":
				lists.get(curList).sortByName();
				break;
			case "Deadline":
				lists.get(curList).sortByDeadline();
				break;
			case "Importance":
				lists.get(curList).sortByImportance();
				break;
			case "Create time":
				lists.get(curList).sortByCreateTime();
				break;
			case "Custom":
				// Does not sort as it indicates it is sorting by
				// Move up and move down.
				break;
		}
		loadView();
	}

	public void hideCompletedTask(){
		lists.get(curList).hideCompleted();
		loadView();
	}

	public void showCompletedTask(){
		lists.get(curList).showCompleted();
		// Must resort the current list with the newly shown tasks.
		sort(lists.get(curList).getCurrentSorting());
	}

	public void moveUp(int pos){
		lists.get(curList).moveUp(pos);
		loadView();
	}

	public void moveTop(int pos){
		lists.get(curList).moveTop(pos);
		loadView();
	}
}
