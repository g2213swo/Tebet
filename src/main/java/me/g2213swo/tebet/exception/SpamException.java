package me.g2213swo.tebet.exception;

public class SpamException extends RuntimeException{
    public SpamException(){}

    public SpamException(String message) {
        super(message);
    }
    public SpamException(String message, Throwable cause) {
        super(message, cause);
    }

}
