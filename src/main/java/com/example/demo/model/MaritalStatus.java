package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum MaritalStatus {
    NOT_MARRIED("Не женат"),
    DATING("Встречаюсь"),
    ENGAGED("Помолвлен"),
    MARRIED("Женат"),
    COMPLICATED("Все сложно"),
    ACTIVE_SEARCH("В активном поиске");

    private final String label;

    MaritalStatus(String label) {
        this.label = label;
    }

    @JsonCreator
    public static MaritalStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return MaritalStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown marital status value: " + value);
        }
    }
}
