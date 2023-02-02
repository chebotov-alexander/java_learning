package org.example.java_learning;

public class Orange extends Fruit {

    private int weight = 0;
    private Color color;

    public Orange() {
        this.weight = 0;
        this.color = Color.GREEN;
    }

    public Orange(int weight) {
        this.weight = weight;
        this.color = Color.GREEN;
    }
    public Orange(int weight, Color color) {
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
        return String.format("Orange{color=%s, weight=%d}", color, weight);
    }

}