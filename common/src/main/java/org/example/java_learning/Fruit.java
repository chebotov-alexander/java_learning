package org.example.java_learning;

public class Fruit {
    int weight = 0;
    Color color;

    public Fruit() {
        this.weight = 0;
        this.color = Color.GREEN;
    }

    public Fruit(int weight) {
        this.weight = weight;
        this.color = Color.GREEN;
    }
    public Fruit(int weight, Color color) {
        this.weight = weight;
        this.color = color;
    }
    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @SuppressWarnings("boxing")
    @Override
    public String toString() {
        return String.format("Fruit{color=%s, weight=%d}", color, weight);
    }
}
