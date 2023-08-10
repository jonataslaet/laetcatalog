package com.github.jonataslaet.laetcatalog.services.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError {
    private List<CustomFieldError> customFieldErrors = new ArrayList<>();

    void addCustomFieldError(CustomFieldError customFieldError) {
        this.customFieldErrors.add(customFieldError);
    }

    public List<CustomFieldError> getCustomFieldErrors() {
        return customFieldErrors;
    }
}
