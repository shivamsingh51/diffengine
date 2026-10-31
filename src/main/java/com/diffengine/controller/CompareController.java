package com.diffengine.controller;

import com.diffengine.model.CompareRequest;
import com.diffengine.model.CompareResponse;
import com.diffengine.service.JsonComparisonService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CompareController {

    private final JsonComparisonService comparisonService;

    public CompareController(
            JsonComparisonService comparisonService) {

        this.comparisonService = comparisonService;
    }

    @PostMapping("/api/compare")
    public CompareResponse compare(
            @RequestBody CompareRequest request) {

        if (request == null ||
            request.original() == null ||
            request.updated() == null) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Both JSON documents are required"
            );
        }

        return comparisonService.compare(
            request.original(),
            request.updated()
        );
    }
}