package de.bp2019.pusl.util.exceptions;

/**
 * Exception signaling that user is not authorized to access an Entity
 * 
 * @author Leon Chemnitz
 */
public class UnauthorizedException extends Exception {
    private static final long serialVersionUID = -1537831871836016922L;

    public UnauthorizedException() {
        super();
    }
}