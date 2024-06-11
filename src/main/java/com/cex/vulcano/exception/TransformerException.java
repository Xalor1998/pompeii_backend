package com.cex.vulcano.exception;

public class TransformerException extends Exception {

    private static final long serialVersionUID = -1563054737233443945L;

    public TransformerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformerException(String message) {
        super(message);
    }
}
