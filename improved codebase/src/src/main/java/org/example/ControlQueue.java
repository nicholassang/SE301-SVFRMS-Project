package org.example;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

// bounded hand-off between the chamber threads (producers) and the control thread (consumer)
// replaces the ArrayList + wait/notifyAll that used to live in SVFRMS
public class ControlQueue {
    private static final Logger logger = AppLogger.getLogger();
    private static final int CAPACITY = 5;

    // "poison pill": tells the control thread to stop, compared by reference (==) so no real request can match it
    private static final ControlRequest STOP = new ControlRequest("STOP", "STOP");

    // ArrayBlockingQueue does the locking and waiting for us
    private final BlockingQueue<ControlRequest> queue = new ArrayBlockingQueue<>(CAPACITY);

    // called by chamber threads, waits while the queue is full
    public void submit(String chamber, String device) {
        try {
            queue.put(new ControlRequest(chamber, device));
        } catch (InterruptedException e) {
            // keep the interrupt so the calling thread can still see it was asked to stop
            Thread.currentThread().interrupt();
            logger.warning("Interrupted while queueing " + device + " for " + chamber);
        }
    }

    // called by the control thread, waits while the queue is empty
    // returns null once stop() was called and every earlier request has been handed out
    public ControlRequest next() throws InterruptedException {
        ControlRequest request = queue.take();
        if (request == STOP) {
            return null;
        }
        return request;
    }

    // called by main after all chambers finish, requests already queued are still processed first
    public void stop() throws InterruptedException {
        queue.put(STOP);
    }
}
