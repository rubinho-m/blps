package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Sex {
    MALE,
    FEMALE;

    @JsonCreator
    public static Sex fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Sex.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown sex value: " + value);
        }
    }
}
