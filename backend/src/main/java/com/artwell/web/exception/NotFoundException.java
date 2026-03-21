package com.artwell.web.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

    private final String resource;
    private final UUID id;

    public NotFoundException(String message) {
        super(message);
        this.resource = null;
        this.id = null;
    }

    public NotFoundException(String resource, UUID id) {
        super(resource + " not found: " + id);
        this.resource = resource;
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public UUID getId() {
        return id;
    }
}
