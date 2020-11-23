package de.bp2019.pusl.model.interfaces;

import java.util.Set;

import org.bson.types.ObjectId;

public interface BelongsToInstitutes {
    Set<ObjectId> getInstitutes();
    void setInstitutes(Set<ObjectId> institutes);
}
