package org.example.chamber;

public class ChamberSummary {
    private int alerts = 0;
    private int mitigationActions = 0;
    private String lastAction = "No action";
    private String lastAlert = "None";

    // equivalent to TRACK_ALERT inside handleEvent 
    public synchronized void recordAlert(String message){
        alerts++;
        lastAlert = message;
    }

    // equivalent to TRACK_ACTION inside handleEvent
    public synchronized void recordAction(String message){
        mitigationActions++;
        lastAction = message;
    }

    public synchronized String toReportLine(String chamber) {
        return chamber + " -> Alerts=" + alerts + ", Actions=" + mitigationActions
                + ", Last Action=" + lastAction + ", Last Alert=" + lastAlert;
    }
}
