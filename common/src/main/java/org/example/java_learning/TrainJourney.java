package org.example.java_learning;

/**
 * Represent train journeys from A to B as a mutable TrainJourney class - a simple implementation of a singly linked list.
 */
public class TrainJourney {
    // The price of the current leg of the journey.
    public int price;
    // Journeys that require changing trains have several linked TrainJourney objects via the onward field; a direct train or the final leg of a journey has onward being null
    public TrainJourney onward;

    public TrainJourney(int p, TrainJourney t) {
        price = p;
        onward = t;
    }
    @Override
    public String toString() { return String.format("TrainJourney[%d] -> %s", price, onward); }
}
