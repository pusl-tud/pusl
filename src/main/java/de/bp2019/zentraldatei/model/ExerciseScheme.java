package de.bp2019.zentraldatei.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Date;

/* A class to model a exercise scheme. Connsists of a name, a flag for numeric grading, a list of possible tokens,
 * a start date, a finish date and a list of users alowed 
 */

@Document
class ExerciseScheme{
	
	private String name;
	private boolean isNumeric;
	private List<String> tokens;
	private Date startDate;
	private Date finishDate;
	private List<User> hasAccess;
	
	public ExerciseScheme(String name, boolean isNumeric, List<String> tokens, Date startDate, Date finishDate, List<User> hasAccess) {
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
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean getIsNumeric() {
		return isNumeric;
	}
	
	public void setIsNumeric(boolean isNumeric) {
		this.isNumeric = isNumeric;
	}
	
	public List<String> getTokens(){
		return tokens;
	}
	
	public void setTokens(List<String> tokens){
		this.tokens = tokens;
	}
	
	public Date getStartDate(){
		return startDate;
	}
	
	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}
	
	public Date getFinishDate(){
		return finishDate;
	}
	
	public void getFinishDate(Date finishDate){
		this.finishDate = finishDate;
	}
	
	public List<User> getHasAccess(){
		return hasAccess;
	}
	
	public void getHasAccess(List<User> hasAccess){
		this.hasAccess = hasAccess;
	}
	
}