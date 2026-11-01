package org.example.control;

import org.example.logging.AppLogger;
import java.util.logging.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class ControlQueue {
    private static final Logger logger = AppLogger.getLogger();
    private static final int CAPACITY = 5;

    private static final ControlRequest STOP =
            new ControlRequest(
                    "STOP",
                    new ControlAction("STOP", 0, 0, 0)
            );

    private final BlockingQueue<ControlRequest> queue =
            new ArrayBlockingQueue<>(CAPACITY);

    public boolean submit(String chamber, ControlAction action) {
        try {
            queue.put(new ControlRequest(chamber, action));
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning(
                    "Interrupted while queueing control action for " + chamber
            );
            return false;
        }
    }

    public ControlRequest next() throws InterruptedException {
        ControlRequest request = queue.take();
        if (request == STOP) {
            return null;
        }
        return request;
    }

    public void stop() throws InterruptedException {
        queue.put(STOP);
    }
}
