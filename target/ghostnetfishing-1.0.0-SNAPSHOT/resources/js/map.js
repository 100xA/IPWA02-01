document.addEventListener("DOMContentLoaded", function () {
    var mapContainer = document.getElementById("open-map");
    if (!mapContainer || typeof L === "undefined") {
        return;
    }

    var hidden = document.querySelector("input[id$='openNetsJson']");
    var labelsNode = document.getElementById("map-i18n");
    var labels = labelsNode ? labelsNode.dataset : {};
    var nets = [];

    if (hidden && hidden.value) {
        try {
            nets = JSON.parse(hidden.value);
        } catch (err) {
            console.error(labels.invalid || "Ungueltige Kartendaten:", err);
        }
    }

    var map = L.map("open-map").setView([20, 0], 2);
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution: "&copy; OpenStreetMap contributors"
    }).addTo(map);

    if (!Array.isArray(nets) || nets.length === 0) {
        return;
    }

    nets.forEach(function (net) {
        if (typeof net.latitude !== "number" || typeof net.longitude !== "number") {
            return;
        }

        var rescuerText = net.rescuerName
            ? net.rescuerName
            : (labels.unassigned || "noch unzugeordnet");
        var popupText = [
            "<strong>" + (labels.net || "Netz") + " #" + net.id + "</strong>",
            (labels.status || "Status") + ": " + (net.statusLabel || net.status),
            (labels.size || "Groesse") + ": " + net.estimatedSizeM2 + " m2",
            (labels.recovery || "Bergung") + ": " + rescuerText
        ].join("<br/>");

        L.marker([net.latitude, net.longitude]).addTo(map).bindPopup(popupText);
    });
});
