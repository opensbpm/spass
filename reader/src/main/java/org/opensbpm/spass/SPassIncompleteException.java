package org.opensbpm.spass;

public class SPassIncompleteException extends  Exception {
    public SPassIncompleteException(String message) {
        super(message);
    }

    public SPassIncompleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
