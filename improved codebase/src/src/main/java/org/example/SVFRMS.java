package org.example;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.logging.Logger;

public class SVFRMS {
    private static final Logger logger = AppLogger.getLogger();

    // final: shared by every thread, so they must never be swapped out mid-run
    public static final ArrayList<String> reports = new ArrayList<>();   // guarded by reportLock
    public static final Object reportLock = new Object();

    static final Random random = new Random();

    public static final ChamberRepository chambers = new ChamberRepository();
    public static final EventRecorder events = new EventRecorder(chambers);
    public static final ResourceUsageTracker usage = new ResourceUsageTracker();
    public static final CropRegister crops = new CropRegister();
    public static final ControlQueue controlQueue = new ControlQueue();   // must be created before monitor
    public static final SensorMonitor monitor = new SensorMonitor(chambers, crops, usage, events, controlQueue);
    public static final ControlService controlService = new ControlService(chambers, events);



    // Runnable = the job only, main decides which thread runs it
    static class ControlTask implements Runnable{
        // passed in instead of reading the SVFRMS globals, so the task only uses what it is given
        private final ControlQueue controlQueue;
        private final ControlService controlService;

        ControlTask(ControlQueue controlQueue, ControlService controlService){
            this.controlQueue = controlQueue;
            this.controlService = controlService;
        }

        public void run(){
            try{
                ControlRequest request;
                // next() waits for a request, and returns null after the STOP pill
                while((request = controlQueue.next()) != null){
                    try{
                        controlService.activateControl(request.chamber(), request.device());
                    }catch(RuntimeException e){
                        // one bad request must not kill this thread, or the chamber threads hang on a full queue
                        logger.warning("Control request failed for " + request + ": " + e);
                    }
                }
            }catch(InterruptedException e){
                // asked to stop while waiting: keep the interrupt flag and exit
                Thread.currentThread().interrupt();
            }
        }
    }

    // simulates one chamber's sensors, run by the chamber thread pool in main
    static class ChamberTask implements Runnable{
        private final String chamber;
        private final String crop;
        private final SensorMonitor monitor;
        private final ResourceUsageTracker usage;

        ChamberTask(String chamber, String crop, SensorMonitor monitor, ResourceUsageTracker usage){
            this.chamber=chamber;
            this.crop=crop;
            this.monitor=monitor;
            this.usage=usage;
        }

        public void run(){
            for(int i=0;i<20;i++){
                monitor.processSensor(chamber,crop,"Temperature",10+random.nextInt(30));
                monitor.processSensor(chamber,crop,"Humidity",40+random.nextInt(60));
                monitor.processSensor(chamber,crop,"Soil",20+random.nextInt(60));
                monitor.processSensor(chamber,crop,"Light",300+random.nextInt(900));
                monitor.processSensor(chamber,crop,"Nutrient",20+random.nextInt(80));
                usage.addWater(2);
                usage.addEnergy(1);
            }
        }
    }

    public static void showAlerts(){
        for(String a: AlertManager.getActiveAlerts()){
            logger.info(a);
        }
    }

    public static void showLogs(){
        for(String s: events.getSensorLogs()){
            logger.info(s);
        }
    }

    public static void main(String[] args) throws Exception {
        // try-with-resources calls close() at the closing brace,
        // and ExecutorService.close() waits until every task it was given has finished
        try(ExecutorService controlThread = Executors.newSingleThreadExecutor()){
            controlThread.execute(new ControlTask(controlQueue, controlService));

            try(ExecutorService chamberThreads = Executors.newFixedThreadPool(4)){
                chamberThreads.execute(new ChamberTask("CH1","Tomato", monitor, usage));
                chamberThreads.execute(new ChamberTask("CH2","Strawberry", monitor, usage));
                chamberThreads.execute(new ChamberTask("CH3","Lettuce", monitor, usage));
                chamberThreads.execute(new ChamberTask("CH4","Chili", monitor, usage));
            }   // waits here for all 4 chambers, replaces the join() calls

            controlQueue.stop();   // STOP goes behind any requests still waiting
        }   // waits here for the control task to finish the queue and exit

        ArchiveManager.archiveAlerts();
        ReportManager.generateDailyReport();

        logger.info("System finished sensor processing.");
        ReportManager.saveArchiveReport();
    }
}
