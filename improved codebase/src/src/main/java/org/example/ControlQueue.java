package org.example;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class ControlQueue {

    private static final Logger logger = AppLogger.getLogger();
    private static final int CAPACITY = 5;

    // Special request used to shut down the consumer.
    private static final ControlRequest STOP =
            new ControlRequest(
                    "STOP",
                    new ControlAction("STOP", 0, 0, 0)
            );

    private final BlockingQueue<ControlRequest> queue =
            new ArrayBlockingQueue<>(CAPACITY);

    // Returns true if the request was successfully queued.
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

    // Returns null when the STOP request is received.
    public ControlRequest next() throws InterruptedException {

        ControlRequest request = queue.take();

        if (request == STOP) {
            return null;
        }

        return request;
    }

    // Called after all producer threads finish.
    public void stop() throws InterruptedException {
        queue.put(STOP);
    }
}