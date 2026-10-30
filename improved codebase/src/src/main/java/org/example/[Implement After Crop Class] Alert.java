package org.example;

import java.util.Date;

public class Alert {

    private final String chamber;
    private final String crop;
    private final String type;
    private final String message;
    private final String severity;

    private final Date createdAt;

    private Date resolvedAt;
    private Date archivedAt;

    private AlertStatus status;

    public Alert(String chamber, String crop,
                 String type, String message,
                 String severity) {

        this.chamber = chamber;
        this.crop = crop;
        this.type = type;
        this.message = message;
        this.severity = severity;

        this.createdAt = new Date();
        this.status = AlertStatus.ACTIVE;
    }

    public void resolve() {
        if (status == AlertStatus.ACTIVE) {
            status = AlertStatus.RESOLVED;
            resolvedAt = new Date();
        }
    }

    public void archive() {
        if (status == AlertStatus.RESOLVED) {
            status = AlertStatus.ARCHIVED;
            archivedAt = new Date();
        }
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

    public String getSeverity() {
        return severity;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getResolvedAt() {
        return resolvedAt;
    }

    public Date getArchivedAt() {
        return archivedAt;
    }

    public AlertStatus getStatus() {
        return status;
    }
}