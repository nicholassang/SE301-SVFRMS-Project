package org.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;


// fix the ifelse in handleEvent
public class EventRecorder {
    private static final Logger logger = AppLogger.getLogger();
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    private ChamberRepository chambers;
    private ArrayList<String> sensorLogs = new ArrayList<>();

    public EventRecorder(ChamberRepository chambers){
        this.chambers = chambers;
    }

    public synchronized void logSensor(String message){   // was LOG
        sensorLogs.add(new Date() + " : " + message);
    }

    public void recordAlert(String chamber, String crop, String message){  // was TRACK_ALERT + CREATE_ALERT
        chambers.getChamberSummary(chamber).recordAlert(message);
        AlertManager.addAlert(message);
        logger.info("[" + chamber + "][" + crop + "][ALERT] " + message);
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
