package de.bp2019.zentraldatei.config;

import java.util.ArrayList;
import java.util.Date;

class ExerciseScheme{
	
	private String name;
	private boolean isNumeric;
	private ArrayList<String> tokens;
	private Date startDate;
	private Date finishDate;
	private ArrayList<User> hasAccess;
	
	public ExerciseScheme(String name, boolean isNumeric, ArrayList<String> tokens, Date startDate, Date finishDate, ArrayList<User> hasAccess) {
		this.name = name;
		this.isNumeric = isNumeric;
		this.tokens = tokens;
		this.startDate = startDate;
		this.finishDate = finishDate;
		this.hasAccess = hasAccess;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean getIsNumeric() {
		return isNumeric;
	}
	
	public ArrayList<String> getTokens(){
		return tokens;
	}
	
	public Date getStartDate(){
		return startDate;
	}
	
	public Date getFinishDate(){
		return finishDate;
	}
	
	public ArrayList<User> getHasAccess(){
		return hasAccess;
	}
	
}