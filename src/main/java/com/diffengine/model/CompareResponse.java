package com.diffengine.model;

import java.util.List;

public record CompareResponse(
    List<Change> changes,
    int addedCount,
    int removedCount,
    int modifiedCount
) {}