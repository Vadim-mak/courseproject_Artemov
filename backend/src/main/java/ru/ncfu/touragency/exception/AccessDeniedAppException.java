package ru.ncfu.touragency.exception;

public class AccessDeniedAppException extends RuntimeException {
    public AccessDeniedAppException(String message) {
        super(message);
    }
}
