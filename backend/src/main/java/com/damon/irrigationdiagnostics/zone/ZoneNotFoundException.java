package com.damon.irrigationdiagnostics.zone;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ZoneNotFoundException extends RuntimeException {

    public ZoneNotFoundException(Long id) {
        super("Zone not found with id: " + id);
    }
}