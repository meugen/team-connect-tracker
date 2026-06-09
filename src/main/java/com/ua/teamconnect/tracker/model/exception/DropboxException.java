package com.ua.teamconnect.tracker.model.exception;

public class DropboxException extends RuntimeException {
    
    public DropboxException(String path) {
        super("File not found in Dropbox: " + path);
    }
    
}
