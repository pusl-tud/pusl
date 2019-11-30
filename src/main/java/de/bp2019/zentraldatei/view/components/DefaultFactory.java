package de.bp2019.zentraldatei.view.components;

/**
 * Default Factory Interface
 * 
 * @param <T> Type for which a default factory should be created
 * @author Leon Chemnitz
 */
public interface DefaultFactory<T> {
    public T createDefaultInstance();
}