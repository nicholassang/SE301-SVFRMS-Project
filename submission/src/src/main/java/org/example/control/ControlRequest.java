package org.example.control;

public record ControlRequest(String chamber, ControlAction action) {

    public ControlRequest {
        if (chamber == null || chamber.isBlank()) {
            throw new IllegalArgumentException("chamber must not be empty");
        }

        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
    }
}
