package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import controller.ToDoController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import model.ToDoList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.ToDoList;
import model.ToDoModel;
import model.ToDoTask;

import javax.swing.*;
import javax.swing.SingleSelectionModel;

public class ToDoView extends Application implements Observer {
	private ToDoView view;
    private ToDoController control;
    private BorderPane window; 
    private VBox taskSection;
    private Label listName;
    private ChoiceBox<String> changeColor;
    private ComboBox<String> sort;
    private CheckBox ck;
    private int id;
    private boolean startup;
    private boolean handle;
    
    public void start(Stage stage) {
    	// Sets view to this view. This is what is passed to addObserver
    	view = this;
    	// Sets startup to true this indicates that a new list is being loaded in.
    	startup = true;
    	// Sets handle to false, so later on when first choice is set up by
    	// computer it doesn't actually do what the button is supposed to.
    	// Important to prevent errors.
    	handle = false;
		stage.setTitle("ToDo");
		window = new BorderPane();
		// If anyone else wants a different window size mention it.
		Scene scene = new Scene(window, 800, 600); // 800 px wide, 600 px tall
	
		// VBox will be used to showoff all the tasks to the user
		taskSection = new VBox(10); // 10 px spacing between rows
		taskSection.setPadding(new Insets(10)); // 10px padding around VBox
		window.setCenter(taskSection);
	
		// Creates the model to be used by the controller.
		ToDoModel modelToBeSent = null;
		try {
			// Case where there is/are existing ToDoLists saved.
			FileInputStream file = new FileInputStream("save.dat");
			ObjectInputStream ois = new ObjectInputStream(file);
			modelToBeSent = (ToDoModel) ois.readObject();
			ois.close();
			file.close();
		} catch (FileNotFoundException e) {
			// Case where there is no existing ToDoLists saved.
			modelToBeSent = new ToDoModel();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("I/O error while reading ObjectInputStream");
		} catch (ClassNotFoundException e) {
			System.out.println("Serialization class could not be found!");
		}
		control = new ToDoController(modelToBeSent);
		control.addObserver(view);
		
		// topPanel holds all the buttons on top.
		HBox topPanel = new HBox(5);
		topPanel.setPadding(new Insets(5));
		window.setTop(topPanel);
	
		// Buttons to be used to add tasks
		Button addTask = new Button("Add Task");
		// Event handler when button is clicked.
		EventHandler<ActionEvent> taskHandler = new NewTaskHandler();
		addTask.setOnAction(taskHandler);

		// ComboBox to choose sort criteria
		Label sortTip = new Label("Sort by: ");
		sort = new ComboBox<>();
		sort.getItems().addAll("Name","Deadline","Importance","Create time","Custom");
		sort.setEditable(false);
		sort.setVisibleRowCount(5);
		sort.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, 
					String oldValue, String newValue) {
				if (handle)
					control.sort(newValue);
			}
		});

		//completed task
		ck = new CheckBox("Hide Completed Task");
		ck.setSelected(false);
		ck.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (handle) {
					if (ck.isSelected()){
						control.hideCompletedTask();
					} else {
						control.showCompletedTask();
					}
				}
			}
		});

		topPanel.getChildren().addAll(sortTip, sort, ck, addTask);

		
		// Sets up the bottom of the window which controls the current list
		// and allows user to create new lists or delete the current list.
		GridPane listSection = new GridPane();
		listSection.setPadding(new Insets(10));
		for (int i = 0; i < 3; i++) {
			RowConstraints row = new RowConstraints();
			row.setPercentHeight(50);
			ColumnConstraints col = new ColumnConstraints();
			col.setPercentWidth(33);
			listSection.getRowConstraints().add(row);
			listSection.getColumnConstraints().add(col);
		}
		// Adds the next and previous list buttons.
		Button nextButton = new Button("Next List");
		nextButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				startup = true;
				control.nextList();
			}
		});
		Button prevButton = new Button("Prev List");
		prevButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				startup = true;
				control.prevList();
			}
		});
		listSection.add(prevButton, 0, 0);
		listSection.add(nextButton, 2, 0);
		// Adds listName label which is where the name of the list will be.
		listName = new Label("List 1");
		listSection.add(listName, 1, 0);
		// Adds the Add List Button
		Button addList = new Button("Add List");
		EventHandler<ActionEvent> listAddHandler = new NewListHandler();
		addList.setOnAction(listAddHandler);
		listSection.add(addList, 0, 1);
		// Adds the Rename List Button
		Button renameList = new Button("Rename List");
		EventHandler<ActionEvent> listRenameHandler = new RenameListHandler();
		renameList.setOnAction(listRenameHandler);
		listSection.add(renameList, 1, 1);
		// Adds the Delete List Button
		Button deleteList = new Button("Delete Current List");
		EventHandler<ActionEvent> listDeleteHandler = new DeleteListHandler();
		deleteList.setOnAction(listDeleteHandler);
		listSection.add(deleteList, 2, 1);
		// Adds the change color button
		changeColor = new ChoiceBox<String>();
		changeColor.getItems().add("List Color: Beige");
		changeColor.getItems().add("List Color: Blue");
		changeColor.getItems().add("List Color: Gray");
		changeColor.getItems().add("List Color: Orange");
		changeColor.getItems().add("List Color: Pink");
		changeColor.getItems().add("List Color: Red");
		changeColor.getItems().add("List Color: Tan");
		changeColor.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (handle) {
					String[] color = ((String) changeColor.getValue()).split(" ");
					control.changeColor(color[2].toLowerCase());
				}
			}
		});
		listSection.add(changeColor, 1, 2);
		// Makes it so the list switch/add/remove/rename stuff is at bottom of
		// window.
		window.setBottom(listSection);
		
		
		// Code to save all the lists when the window is closed.
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				try {
					control.saveLists();
				} catch (IOException e) {
					System.out.println("Error: Could not save Lists!");
				}
			}
		});
	
		stage.setScene(scene);
		stage.show();
		
		control.loadView();
		startup = false;
    }
    
    /**
     * Class deals with adding new lists when the Add List button is clicked.
     * 
     * @author Henry Do
     *
     */
    private class NewListHandler implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent arg0) {
			// Opens a new window for the user to implement info about
		    // a new task for.
		    GridPane window2 = new GridPane();
		    // Sets up 2nd window, so that there is are for new list name
		    // input.
		    for (int i = 0; i < 2; i++) {
		    	RowConstraints row = new RowConstraints();
		    	row.setPercentHeight(50);
		    	window2.getRowConstraints().add(row);
		    	ColumnConstraints col = new ColumnConstraints();
		    	col.setPercentWidth(50);
		    	window2.getColumnConstraints().add(col);
		    }
	
		    // Sets up area for user to input name of New List
		    Label name = new Label("Name: ");
		    TextField nameInput = new TextField();
		    window2.add(name, 0, 0);
		    window2.add(nameInput, 1, 0);
	
		    // Sets up button to close the window and create the new task
		    Button enter = new Button("Create New List");
		    window2.add(enter, 1, 1);
	
		    Scene scene2 = new Scene(window2, 250, 100);
	
		    Stage stage2 = new Stage();
		    stage2.setTitle("New List");
		    stage2.setScene(scene2);
		    
		    // Implementation of New List button
		    enter.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					String curName = nameInput.getText();
					control.addList(curName);
					stage2.close();
				}
		    });
		    stage2.showAndWait();
		}
    	
    }
    
    /**
     * Class deals with renaming lists when the Rename List button is clicked.
     * 
     * @author Henry Do
     *
     */
    private class RenameListHandler implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent arg0) {
			// Opens a new window for the user to implement info about
		    // a new task for.
		    GridPane window2 = new GridPane();
		    // Sets up 2nd window, so that there is are for new list name
		    // input.
		    for (int i = 0; i < 2; i++) {
		    	RowConstraints row = new RowConstraints();
		    	row.setPercentHeight(50);
		    	window2.getRowConstraints().add(row);
		    	ColumnConstraints col = new ColumnConstraints();
		    	col.setPercentWidth(50);
		    	window2.getColumnConstraints().add(col);
		    }
	
		    // Sets up area for user to input name of New List
		    Label name = new Label("Name: ");
		    TextField nameInput = new TextField();
		    window2.add(name, 0, 0);
		    window2.add(nameInput, 1, 0);
	
		    // Sets up button to close the window and create the new task
		    Button enter = new Button("Rename the List");
		    window2.add(enter, 1, 1);
	
		    Scene scene2 = new Scene(window2, 250, 100);
	
		    Stage stage2 = new Stage();
		    stage2.setTitle("Rename List");
		    stage2.setScene(scene2);
		    
		    // Implementation of New List button
		    enter.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					String curName = nameInput.getText();
					control.renameList(curName);
					stage2.close();
				}
		    });
		    stage2.showAndWait();
		}
    	
    }
    
    /**
     * Class deals with deleting lists when the Delete Current List 
     * button is clicked.
     * 
     * @author Henry Do
     *
     */
    private class DeleteListHandler implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent arg0) {
			boolean valid = control.deleteList();
			if (!valid) {
				Alert error = new Alert(Alert.AlertType.INFORMATION);
            	error.setContentText("Can't Delete Current List Because it is"
            			+ " the Only List!");
            	error.showAndWait();
			}
		}
    	
    }

    /**
     * Class deals with adding new tasks to the list when the Add Task button is
     * clicked.
     * 
     * @author Henry Do
     *
     */
    private class NewTaskHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
		    // Opens a new window for the user to implement info about
		    // a new task for.
		    GridPane window2 = new GridPane();
		    // Sets up 2nd window, so that there is enough space for labels
		    // and textfield
		    for (int i = 0; i <= 5; i++) {
				RowConstraints row = new RowConstraints();
				row.setPercentHeight(25);
				window2.getRowConstraints().add(row);
		    }
		    ColumnConstraints col1 = new ColumnConstraints();
		    col1.setPercentWidth(50);
		    window2.getColumnConstraints().add(col1);
		    ColumnConstraints col2 = new ColumnConstraints();
		    col2.setPercentWidth(50);
		    window2.getColumnConstraints().add(col2);
	
		    // Sets up area for user to input name of New Task
		    Label name = new Label("Name: ");
		    TextField nameInput = new TextField();
		    window2.add(name, 0, 0);
		    window2.add(nameInput, 1, 0);
		    
		    // Sets up area for user to input description of New Task
		    Label description = new Label("Description: ");
		    TextField descriptionInput = new TextField();
		    window2.add(description, 0, 1);
		    window2.add(descriptionInput, 1, 1);
		    
		    // Sets up area for user to input deadline date of New Task
		    Label deadline = new Label("Deadline: ");
		    TextField deadlineInput = new TextField("mm/dd/year");
		    window2.add(deadline, 0, 2);
		    window2.add(deadlineInput, 1, 2);
		    
		    // Sets up area for user to input importance
		    // Maybe make this a button or just don't have the user able
		    // to set importance here. Have them set it in main window?
		    Label importance = new Label("Important: ");
		    TextField importanceInput = new TextField("yes/no");
		    CheckBox importanceBox=new CheckBox("Important");
		    window2.add(importanceBox, 0, 3);
		    importanceBox.setSelected(false);
		    
		    //window2.add(importanceInput, 1, 3);      LEFTOVER FROM STRING IMPLEMENTATION
		    
		    //sets up area for user to input the location for the new task
		    Label location = new Label("Location: ");
		    TextField locationInput = new TextField("Name/Address");
		    window2.add(location, 0, 4);
		    window2.add(locationInput, 1, 4);
	
		    // Sets up button to close the window and create the new task
		    Button enter = new Button("Create New Task");
		    window2.add(enter, 1, 5);
	
		    Scene scene2 = new Scene(window2, 500, 300);
	
		    Stage stage2 = new Stage();
		    stage2.setTitle("New Task");
		    stage2.setScene(scene2);
		    
		    // Implementation of New Task button
		    enter.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					String curName = nameInput.getText();
					String curDescription = descriptionInput.getText();
					String curDeadline = deadlineInput.getText();
					String curImportant;
					if(importanceBox.isSelected()) {
					    curImportant="Important!!!";
					}
					else {
					    curImportant="";
					}					
					String curLocation = locationInput.getText();
					control.addTask(curName, curDescription, curDeadline, curImportant,curLocation);
					
					stage2.close();
				}
		    	
		    });
	
		    stage2.showAndWait();
		}

    }
    private class importanceHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			String currID = ((CheckBox) arg0.getSource()).getId();
			int curr = Integer.parseInt(currID);
			if (((CheckBox) arg0.getSource()).isSelected()) {
				control.changeImportance("Important!!!", curr);
			}
			else {
				control.changeImportance("", curr);
			}
		}
    }
    private class completionHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			String currID = ((CheckBox) arg0.getSource()).getId();
			int curr = Integer.parseInt(currID);
			if (((CheckBox) arg0.getSource()).isSelected()) {
				control.changeCompletion(true, curr);
			}
			else {
				control.changeCompletion(false, curr);
			}
		}
    }
    
    private void checkboxHelper(String important, boolean complete, CheckBox c1, CheckBox c2) {
    	if (complete) {
    		c1.setSelected(true);
    	}
    	if (important != "") {
    		c2.setSelected(true);
    	}
  
    	return;
    }
    
    /**
     * Updates the view with the passed Object.
     * 
     */
    @Override
    public void update(Observable o, Object newList) {
    	ObservableList<HBox> rows = FXCollections.observableArrayList();
    	taskSection.getChildren().clear();
    	id = 0;
    	
    	// Ensures that on startUp the button being selected doesn't do anything.
    	handle = false;
    	// Sets the color of the list.
    	switch (((ToDoList) newList).getColor()) {
    		case "blue":
    			taskSection.setStyle("-fx-background-color: lightblue;");
    			if (startup)
    				changeColor.getSelectionModel().select(1);
    			break;
    		case "gray":
    			taskSection.setStyle("-fx-background-color: slategrey;");
    			if (startup)
    				changeColor.getSelectionModel().select(2);
    			break;
    		case "orange":
    			taskSection.setStyle("-fx-background-color: orange;");
    			if (startup)
    				changeColor.getSelectionModel().select(3);
    			break;
    		case "pink":
    			taskSection.setStyle("-fx-background-color: pink;");
    			if (startup)
    				changeColor.getSelectionModel().select(4);
    			break;
    		case "red":
    			taskSection.setStyle("-fx-background-color: crimson;");
    			if (startup)
    				changeColor.getSelectionModel().select(5);
    			break;
    		case "tan":
    			taskSection.setStyle("-fx-background-color: tan;");
    			if (startup)
    				changeColor.getSelectionModel().select(6);
    			break;
    		default:
    			// By default list is colored beige.
    			taskSection.setStyle("-fx-background-color: beige;");
    			if (startup)
    				changeColor.getSelectionModel().select(0);
    			break;
    	}
    	// Sets the sorting method choice.
    	switch (((ToDoList) newList).getCurrentSorting()) {
			case "Name":
				if (startup)
					sort.getSelectionModel().select(0);
				break;
			case "Deadline":
				if (startup)
					sort.getSelectionModel().select(1);
				break;
			case "Importance":
				if (startup)
					sort.getSelectionModel().select(2);
				break;
			case "Create time":
				if (startup)
					sort.getSelectionModel().select(3);
				break;
			default:
				// Default sort is Custom
				sort.getSelectionModel().select(4);
				break;
    	}
    	// Determines what the hide completed checkbox when a new list is
    	// loaded into the view.
    	if (((ToDoList) newList).getHideComplete()) {
    		if (startup)
    			ck.setSelected(true);
    	} else {
    		if (startup)
    			ck.setSelected(false);
    	}
    	// Ensures that program knows that not a startUp and that buttons
    	// being clicked should handle things again.
    	startup = false;
    	handle = true;
    	
    	// For loop sets up all the tasks within the model.
    	for (int i = 0; i < ((ToDoList) newList).amountTasks(); i++) {
    		ToDoTask newTask = ((ToDoList) newList).getTask(i);
    		
    		HBox h = new HBox(5);
    		Label label = new Label(((ToDoTask) newTask).getName());
    		Pane pane = new Pane();
			Button upButton = new Button("Move up");
			Button topButton = new Button("Move Top");
    		Button button = new Button("Remove");
    		CheckBox c1 = new CheckBox("Complete");
    		CheckBox c2 = new CheckBox("Important");
    		c1.setId("" + id);
    		c2.setId("" + id);
    		checkboxHelper(newTask.getImportance(), newTask.getCompletion(), c1, c2);
    		button.setId("" + id);
			upButton.setId("" + id);
			topButton.setId("" + id);
    		id++;
    		
    		Label description=new Label(((ToDoTask) newTask).getDescription());
    		Label deadline=new Label(((ToDoTask)newTask).getDeadline());
    		Label location=new Label(((ToDoTask)newTask).getLocation());
    		h.getChildren().addAll(label, pane,description,deadline,location, c1, c2, upButton, topButton, button);
    		
    		EventHandler<ActionEvent> completionHandler = new completionHandler();
    		EventHandler<ActionEvent> importanceHandler = new importanceHandler();
    		c1.setOnAction(completionHandler);
    		c2.setOnAction(importanceHandler);
    		
    		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
    			@Override
    			public void handle(MouseEvent arg0) {
    				// tell controller to update the view
    				String index =((Node) arg0.getSource()).getId();
    				int ind	= Integer.parseInt(index);
    				for (int i = 0; i < id; i++) {
    					if (i > ind) {
    						String currID = rows.get(i).getChildren().get(7).getId();
    						int curr = Integer.parseInt(currID) - 1;
    						rows.get(i).getChildren().get(5).setId("" + curr);
    						rows.get(i).getChildren().get(6).setId("" + curr);
    						rows.get(i).getChildren().get(7).setId("" + curr);
    					}
    				}
    				rows.remove(ind);
    				id--;
    				control.removeTask(ind);
    			}
    		});

    		upButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					String index =((Node) arg0.getSource()).getId();
					int ind	= Integer.parseInt(index);
					control.moveUp(ind);
				}
			});

    		topButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					String index =((Node) arg0.getSource()).getId();
					int ind	= Integer.parseInt(index);
					control.moveTop(ind);
				}
			});
    			
    		h.setStyle("-fx-background-color: white;");
    		label.setStyle("-fx-padding: 4 0 5 5;");
    		HBox.setHgrow(pane, Priority.ALWAYS);
    		rows.add(h);
    		ListView<HBox> list = new ListView<HBox>();
    		list.setItems(rows);
    		
    		taskSection.getChildren().clear();
    		taskSection.getChildren().addAll(rows);
    	}
    	// Sets up the name of the list.
    	listName.setText(((ToDoList) newList).getNameList());
    }
    	
}

