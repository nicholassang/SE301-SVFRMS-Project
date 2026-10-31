package org.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;


// fix the ifelse in handleEvent
public class EventRecorder {
    private final AlertManager alertManager;

    private static final Logger logger = AppLogger.getLogger();
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    private final ChamberRepository chambers;
    private final ArrayList<String> sensorLogs = new ArrayList<>();   // guarded by synchronized methods

    public EventRecorder(
            ChamberRepository chambers,
            AlertManager alertManager) {

        this.chambers = chambers;
        this.alertManager = alertManager;
    }

    public synchronized void logSensor(String message){   // was LOG
        sensorLogs.add(new Date() + " : " + message);
    }

    public void recordAlert(Alert alert) {
        String message = alert.getMessage();

        chambers.getChamberSummary(alert.getChamber())
                .recordAlert(message);

        alertManager.addAlert(alert);

        logger.info(
            "[" + alert.getChamber() + "]"
            + "[" + alert.getCrop() + "]"
            + "[" + alert.getSeverity() + "] "
            + message
        );
    }

    public void recordAction(String chamber, String action, String result){ // was TRACK_ACTION + CONTROL_LOG
        chambers.getChamberSummary(chamber).recordAction(action);
        logger.info("\t" + GREEN + "[" + chamber + "][CONTROL] " + action + RESET);
        logger.info("\t" + GREEN + "[" + chamber + "][CONTROL] " + result + RESET);
    }

    public synchronized ArrayList<String> getSensorLogs(){
        return new ArrayList<>(sensorLogs);
    }
}
