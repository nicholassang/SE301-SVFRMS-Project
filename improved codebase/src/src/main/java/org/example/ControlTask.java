package org.example;

import java.util.logging.Logger;

public class ControlTask implements Runnable {

    private static final Logger logger = AppLogger.getLogger();

    private final ControlQueue controlQueue;
    private final ControlExecutor controlExecutor;

    public ControlTask(
            ControlQueue controlQueue,
            ControlExecutor controlExecutor) {

        this.controlQueue = controlQueue;
        this.controlExecutor = controlExecutor;
    }

    @Override
    public void run() {
        try {
            ControlRequest request;

            while ((request = controlQueue.next()) != null) {
                try {
                    controlExecutor.activateControl(
                            request.chamber(),
                            request.device()
                    );
                } catch (RuntimeException e) {
                    logger.warning(
                            "Control request failed for " + request + ": " + e
                    );
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}