package org.example.sensor;

// the values a simulated sensor can produce: min (included) up to max (not included)
// e.g. new ReadingRange(10, 40) gives temperatures 10..39
// a record is immutable, so every chamber thread can share it without locking
public record ReadingRange(int min, int max) {

    public ReadingRange {
        if (max <= min) {
            throw new IllegalArgumentException("max must be greater than min");
        }
    }
}
