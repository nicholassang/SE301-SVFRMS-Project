package org.example.chamber;

public class ChamberSummary {
    private int alerts = 0;
    private int mitigationActions = 0;
    private String lastAction = "No action";
    private String lastAlert = "None";

    public synchronized void recordAlert(String message){
        alerts++;
        lastAlert = message;
    }

    public synchronized void recordAction(String message){
        mitigationActions++;
        lastAction = message;
    }

    public synchronized String toReportLine(String chamber) {
        return chamber + " -> Alerts=" + alerts + ", Actions=" + mitigationActions
                + ", Last Action=" + lastAction + ", Last Alert=" + lastAlert;
    }
}
