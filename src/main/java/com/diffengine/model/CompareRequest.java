package com.diffengine.model;

import com.fasterxml.jackson.databind.JsonNode;

public record CompareRequest(
    JsonNode original,
    JsonNode updated
) {}