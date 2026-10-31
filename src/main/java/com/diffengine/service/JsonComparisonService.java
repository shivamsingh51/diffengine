package com.diffengine.service;

import com.diffengine.model.Change;
import com.diffengine.model.CompareResponse;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JsonComparisonService {

    public CompareResponse compare(
            JsonNode original,
            JsonNode updated) {

        List<Change> changes = new ArrayList<>();

        compareNodes(original, updated, "$", changes);

        int added = count(changes, "added");
        int removed = count(changes, "removed");
        int modified = count(changes, "modified");

        return new CompareResponse(
            changes,
            added,
            removed,
            modified
        );
    }

    private void compareNodes(
            JsonNode oldNode,
            JsonNode newNode,
            String path,
            List<Change> changes) {

        // Compare JSON objects
        if (oldNode.isObject() && newNode.isObject()) {

            Set<String> fields = new LinkedHashSet<>();

            oldNode.fieldNames().forEachRemaining(fields::add);
            newNode.fieldNames().forEachRemaining(fields::add);

            for (String field : fields) {

                String fieldPath = path + "." + field;

                if (!oldNode.has(field)) {

                    changes.add(new Change(
                        "added",
                        fieldPath,
                        null,
                        newNode.get(field)
                    ));

                } else if (!newNode.has(field)) {

                    changes.add(new Change(
                        "removed",
                        fieldPath,
                        oldNode.get(field),
                        null
                    ));

                } else {

                    compareNodes(
                        oldNode.get(field),
                        newNode.get(field),
                        fieldPath,
                        changes
                    );
                }
            }

            return;
        }

        // Compare JSON arrays by index
        if (oldNode.isArray() && newNode.isArray()) {

            int largestSize = Math.max(
                oldNode.size(),
                newNode.size()
            );

            for (int i = 0; i < largestSize; i++) {

                String itemPath = path + "[" + i + "]";

                if (i >= oldNode.size()) {

                    changes.add(new Change(
                        "added",
                        itemPath,
                        null,
                        newNode.get(i)
                    ));

                } else if (i >= newNode.size()) {

                    changes.add(new Change(
                        "removed",
                        itemPath,
                        oldNode.get(i),
                        null
                    ));

                } else {

                    compareNodes(
                        oldNode.get(i),
                        newNode.get(i),
                        itemPath,
                        changes
                    );
                }
            }

            return;
        }

        // Compare primitive values or different JSON types
        if (!Objects.equals(oldNode, newNode)) {

            changes.add(new Change(
                "modified",
                path,
                oldNode,
                newNode
            ));
        }
    }

    private int count(
            List<Change> changes,
            String type) {

        return (int) changes.stream()
            .filter(change -> change.type().equals(type))
            .count();
    }
}