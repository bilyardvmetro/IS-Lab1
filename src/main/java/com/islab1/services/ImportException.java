package com.islab1.services;

import java.util.Collections;
import java.util.List;

public class ImportException extends RuntimeException {

    private final List<String> errors;

    public ImportException(List<String> errors) {
        super("Ошибки при импорте объектов");
        this.errors = errors == null ? List.of() : errors;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
