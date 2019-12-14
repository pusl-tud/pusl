package de.bp2019.zentraldatei.model;

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
	private String id;
	private String name;

	public Institute(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}