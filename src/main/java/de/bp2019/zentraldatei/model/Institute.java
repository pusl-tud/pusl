package de.bp2019.zentraldatei.model;

import org.springframework.data.annotation.Id;

public class Institute {
    @Id
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Institute(String name) {
        this.name = name;
    }
}