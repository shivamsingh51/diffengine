async function compareJson() {

    const originalText =
        document.getElementById("originalJson").value;

    const updatedText =
        document.getElementById("updatedJson").value;

    const errorElement =
        document.getElementById("error");

    const button =
        document.getElementById("compareBtn");

    errorElement.textContent = "";

    try {

        // Convert text into JavaScript JSON objects
        const original = JSON.parse(originalText);
        const updated = JSON.parse(updatedText);

        button.disabled = true;
        button.textContent = "Comparing...";

        // Send data to Spring Boot backend
        const response = await fetch("/api/compare", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                original: original,
                updated: updated
            })

        });

        if (!response.ok) {
            throw new Error(
                `Server error: ${response.status}`
            );
        }

        // Receive backend response
        const result = await response.json();

        // Display comparison results
        displayResults(result);

    } catch (error) {

        errorElement.textContent = error.message;

    } finally {

        button.disabled = false;
        button.textContent = "Compare JSON";

    }
}


function displayResults(result) {

    document.getElementById("resultSection").hidden = false;

    document.getElementById("addedCount").textContent =
        result.addedCount;

    document.getElementById("removedCount").textContent =
        result.removedCount;

    document.getElementById("modifiedCount").textContent =
        result.modifiedCount;

    const changesContainer =
        document.getElementById("changes");

    changesContainer.replaceChildren();

    if (result.changes.length === 0) {

        const message = document.createElement("p");

        message.textContent = "No differences found.";

        changesContainer.appendChild(message);

        return;
    }

    result.changes.forEach(change => {

        const item = document.createElement("div");

        item.className =
            `change-item ${change.type}`;

        const heading = document.createElement("h3");

        heading.textContent =
            `${change.type.toUpperCase()} - ${change.path}`;

        const oldValue = document.createElement("p");

        oldValue.textContent =
            `Old value: ${JSON.stringify(change.oldValue)}`;

        const newValue = document.createElement("p");

        newValue.textContent =
            `New value: ${JSON.stringify(change.newValue)}`;

        item.appendChild(heading);
        item.appendChild(oldValue);
        item.appendChild(newValue);

        changesContainer.appendChild(item);

    });
}