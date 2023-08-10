package com.github.jonataslaet.laetcatalog.services.exceptions;

public class CustomFieldError {

    private String name;
    private String message;

    public CustomFieldError() {
    }

    public CustomFieldError(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
