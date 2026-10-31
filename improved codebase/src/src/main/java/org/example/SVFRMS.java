package org.example;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import java.util.logging.Logger;

public class SVFRMS {
    private static final Logger logger = AppLogger.getLogger();

    public static ArrayList<String> reports = new ArrayList<>();
    public static ArrayList<String> controlQueue = new ArrayList<>();
    public static int controlQueueSize = 5;
    public static volatile boolean controlThreadRunning = true;



    public static Object reportLock = new Object();
    // delete waterPumpLock and nutrientLock --> unused

    static Random random = new Random();


    public static ChamberRepository chambers = new ChamberRepository();
    public static EventRecorder events = new EventRecorder(chambers);
    public static ResourceUsageTracker usage = new ResourceUsageTracker();
    public static CropRegister crops = new CropRegister();
    public static SensorMonitor monitor = new SensorMonitor(chambers, crops, usage, events);
    public static ControlService controlService = new ControlService(chambers, events);


    

    public static void addControlRequest(String chamber, String control){
        synchronized(controlQueue){
            while(controlQueue.size() == controlQueueSize){
                try{controlQueue.wait();}catch(Exception e){}
            }
            controlQueue.add(chamber + "|" + control);
            controlQueue.notifyAll();
        }
    }



    static class ControlThread extends Thread{
        public void run(){
            while(true){
                String request;

                synchronized(controlQueue){
                    while(controlQueue.isEmpty() && controlThreadRunning){
                        try{controlQueue.wait();}catch(Exception e){}
                    }

                    if(controlQueue.isEmpty() && !controlThreadRunning){
                        break;
                    }

                    request = controlQueue.remove(0);
                    controlQueue.notifyAll();
                }

                String[] parts = request.split("\\|");
                controlService.activateControl(parts[0], parts[1]);
            }
        }
    }

    static class ChamberThread extends Thread{
        String chamber;
        String crop;

        ChamberThread(String chamber,String crop){
            this.chamber=chamber;
            this.crop=crop;
        }

        public void run(){
            for(int i=0;i<20;i++){
                monitor.processSensor(chamber,crop,SensorType.TEMPERATURE,10+random.nextInt(30));
                monitor.processSensor(chamber,crop,SensorType.HUMIDITY,40+random.nextInt(60));
                monitor.processSensor(chamber,crop,SensorType.SOIL,20+random.nextInt(60));
                monitor.processSensor(chamber,crop,SensorType.LIGHT,300+random.nextInt(900));
                monitor.processSensor(chamber,crop,SensorType.NUTRIENT,20+random.nextInt(80));
                usage.addWater(2);
                usage.addEnergy(1);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Thread activateControlThread = new ControlThread();
        Thread t1 = new ChamberThread("CH1","Tomato");
        Thread t2 = new ChamberThread("CH2","Strawberry");
        Thread t3 = new ChamberThread("CH3","Lettuce");
        Thread t4 = new ChamberThread("CH4","Chili");

        activateControlThread.start();
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        synchronized(controlQueue){
            controlThreadRunning = false;
            controlQueue.notifyAll();
        }
        activateControlThread.join();

        AlertManager.archiveAlerts();
        ReportManager.generateDailyReport();

        logger.info("System finished sensor processing.");
        ReportManager.saveArchiveReport();
    }
}
