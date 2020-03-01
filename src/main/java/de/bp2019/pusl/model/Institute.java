package de.bp2019.pusl.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model of an Institute
 *
 * @author Leon Chemnitz
 */
@Document
public class Institute {

	@Id
	private ObjectId id;

	private String name;

	@Override
    public boolean equals(Object o) {   
        if (o == this) { 
            return true; 
        } 
        if (!(o instanceof Institute)) { 
            return false; 
        }           
        Institute i = (Institute) o;           
        return i.getId().equals(this.getId());
    } 


	public Institute() {
	}

	public Institute(String name) {
		this.name = name;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}