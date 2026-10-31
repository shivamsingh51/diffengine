package com.diffengine.model;

import com.fasterxml.jackson.databind.JsonNode;

public record Change(
    String type,
    String path,
    JsonNode oldValue,
    JsonNode newValue
) {}