package de.bp2019.zentraldatei.model;

import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.annotation.Id;

/**
 * Encapsulates an token as a class for ease of handling
 *
 * @author Luca Dinies
 */
@Document
public class Token {


    //private String id;
    private String name;

    public Token(String name) {
        this.name = name;
    }

/*   public String getId() {
        return id;
    }
*/
    public String getTokenName() {
        return name;
    }

    public void setTokenName(String name) {
        this.name = name;
    }

}