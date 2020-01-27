package de.bp2019.zentraldatei.model;

import com.mongodb.lang.NonNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Encapsulates an instute as a class for ease of handling
 *
 * @author Alex Späth
 */
@Document
public class Institute {

	@Id
	private ObjectId id;

	@NonNull
	private String name;

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