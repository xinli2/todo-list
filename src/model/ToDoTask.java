package model;

import java.io.Serializable;
import java.util.Date;


//TODO: IMPLEMENT FIELDS AND FUNCTIONALITY FOR FIELDS: deadline, important

public class ToDoTask implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String deadline;  //leaving as string for now
	private Date createTime;
	private String location;
	private String important;
    private boolean complete;
    
	public ToDoTask() {
	    this.name = "untitled task";
	    this.description = null;
	    this.complete = false;
	}
	
	public ToDoTask(String name) {
	    this.name = name;
	    this.description = null;
	    this.complete = false;
	}
	
	public ToDoTask(String name, String description,String deadline,String importance,String location) {
	    this.name = name;
	    this.description = description;
	    this.deadline = deadline;
	    this.important = importance;
	    this.location = location;
	    this.createTime = new Date();
	    this.complete = false;
	    //System.out.println("Name:"+this.name+"\nDescription:"+this.description);  //TESTING PURPOSES
	}
	
	public void rename(String newName) {  //TODO: implement somehow when functionality is added to GUI
	    if(newName == null) {
	    	System.out.println("Don't rename as null");
	    } else {
	    	this.name=newName;
	    }
	}
	
	public String getName() {
	    return this.name;
	}
	
	public String getDescription() {
	    return this.description;
	}
	public String getDeadline() {
	    return this.deadline;
	}
	public String getImportance() {
	    return this.important;
	}
	public String getLocation(){
	    return this.location;
	}
	
	public boolean getCompletion() {
		return this.complete;
	}
	public void setImportance(String importance) {
		this.important = importance;
	}
	public void setCompletion(boolean complete) {
		this.complete = complete;
	}

	public Date getCreateTime(){
		return this.createTime;
	}
}
