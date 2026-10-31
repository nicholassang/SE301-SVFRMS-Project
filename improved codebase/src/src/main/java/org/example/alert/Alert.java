package org.example.alert;

import org.example.logging.AppLogger;
import java.util.logging.Logger;

import java.util.Date;

public class Alert {

    private final String chamber;
    private final String crop;
    private final String type;
    private final String message;
    private final AlertSeverity severity;

    private final Date createdAt;

    private Date resolvedAt;
    private Date archivedAt;

    private AlertStatus status;

    public Alert(
            String chamber,
            String crop,
            String type,
            String message,
            AlertSeverity severity) {

        this.chamber = chamber;
        this.crop = crop;
        this.type = type;
        this.message = message;
        this.severity = severity;

        this.createdAt = new Date();
        this.status = AlertStatus.ACTIVE;
    }

    public synchronized void resolve() {
        if (status == AlertStatus.ACTIVE) {
            status = AlertStatus.RESOLVED;
            resolvedAt = new Date();
        }
    }

    public synchronized boolean archive() {
        if (status == AlertStatus.RESOLVED) {
            status = AlertStatus.ARCHIVED;
            archivedAt = new Date();
            return true;
        }

        return false;
    }

    public String getChamber() {
        return chamber;
    }

    public String getCrop() {
        return crop;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public Date getCreatedAt() {
        return new Date(createdAt.getTime());
    }

    public synchronized Date getResolvedAt() {
        return resolvedAt == null
                ? null
                : new Date(resolvedAt.getTime());
    }

    public synchronized Date getArchivedAt() {
        return archivedAt == null
                ? null
                : new Date(archivedAt.getTime());
    }

    public synchronized AlertStatus getStatus() {
        return status;
    }

    public boolean matches(
            String chamber,
            String crop,
            String type) {

        return this.chamber.equals(chamber)
                && this.crop.equals(crop)
                && this.type.equals(type);
    }
}
