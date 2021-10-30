package controller;

import java.io.IOException;
import java.util.Observer;

import javafx.scene.control.Alert;
import model.ToDoModel;


public class ToDoController {
	private ToDoModel model;
	
	/**
	 * Creates a ToDoController from the given ToDoModel.
	 * 
	 * Note: View must use the method addObserver after this method in order
	 * for it to become an Observer of the first list.
	 * 
	 * @param model The ToDoModel which will store the data for the ToDoList
	 *              and its tasks.
	 */
	public ToDoController(ToDoModel model) {
		this.model = model;
	}
	
	/**
	 * Loads the current List into view.
	 * 
	 * Must be used after the controller was created in order to load
	 * the data into the view correctly. 
	 */
	public void loadView() {
		model.loadView();
	}
	
	/**
	 * Changes the list color to the given one.
	 * 
	 * @param color The new list color.
	 */
	public void changeColor(String color) {
		model.changeColor(color);
	}
	
	/**
	 * Creates a new list and switches to it.
	 * 
	 * Note: addObserver method must be used after this method in order for the
	 * view to become an Observer of the newly created list.
	 * 
	 * @param name The name of the new list.
	 */
	public void addList(String name) {
	    model.addList(name);
	}
	
	/**
	 * Renames the current list to the given name.
	 * 
	 * @param name The given name.
	 */
	public void renameList(String name) {
		model.renameList(name);
	}
	
	/**
	 * Deletes the current list.
	 * 
	 * In this program it is not possible to delete the current list
	 * if the current list is the only list. Boolean is used so view
	 * can recognize a failed delete due to there only being one list.
	 * 
	 * @return True if the list was successfully deleted. False if the
	 *         list was not able to be deleted because the current list
	 *         is the only list.
	 */
	public boolean deleteList() {
		if (!model.moreThanOneList()) {
			return false; // Case where current list should not be deleted.
		}
		model.deleteList();
		
		return true;
	}
	
	/**
	 * Iterates to the next list.
	 */
	public void nextList() {
		model.nextList();
	}
	
	/**
	 * Iterates to the previous list.
	 */
	public void prevList() {
		model.prevList();
	}
	
	/**
	 * Adds a new task to the current viewable list.
	 * 
	 * @param name The name of the task.
	 * @param description The notes/description about the task.
	 * @param deadline The deadline for the task. (Should be (m/d/yr))
	 * @param importance Indicates whether the task is important or not.
	 */
	public void addTask(String name, String description, String deadline, 
			String importance,String location) { 
		// TODO: Must have a way to check that deadline is a valid date
		// and that Importance is valid. Currently both are not really
		// implemented.
	    if(name.equals("")) {
	    	name = "unnamed task";
	    }
	    if(deadline.equals("mm/dd/year")) {
	    	deadline = "";
	    }
	    else if(!deadline.equals("") && deadline.split("/").length!=3) {
			Alert error = new Alert(Alert.AlertType.INFORMATION);
			error.setTitle("ERROR");
			error.setHeaderText("Invalid Date Entered");
			error.setContentText("Use Format 'mm/dd/year'.");
			error.showAndWait();
			return;
	    }
	    if(location.equals("Name/Address")) {
	    	location = "";
	    }
	    model.addTask(name, description, deadline, importance,location);
	}
	
	/**
	 * Removes the given task from the current viewable list.
	 * 
	 * @param index The ToDoTask to be removed.
	 */
	public void removeTask(int index) {
	    model.removeTask(index);
	}
	
	public void changeImportance(String important, int curr) {
		model.changeImportance(important, curr);
	}
	
	public void changeCompletion(boolean complete, int curr) {
		model.changeCompletion(complete, curr);
	}
	
	/**
	 * Adds the view as an observer to the current list.
	 * 
	 * Must be called whenever a new list is being added in order for
	 * the GUI to be able to correctly display the new list.
	 * 
	 * @param view The GUI view that will be the observer.
	 */
	public void addObserver(Observer view) {
	    model.addObserver(view);
	}
	
	/**
	 * Saves the ToDoModel which houses all of the data about the ToDoLists.
	 * 
	 * @throws IOException Means that the file was not able to be written to.
	 */
	public void saveLists() throws IOException {
		model.saveLists();
	}

	public void sort(String sortBy){
		model.sort(sortBy);
	}

	public void hideCompletedTask(){
		model.hideCompletedTask();
	}

	public void showCompletedTask(){
		model.showCompletedTask();
	}

	public void moveUp(int pos){
		model.moveUp(pos);
	}

	public void moveTop(int pos){
		model.moveTop(pos);
	}
}
