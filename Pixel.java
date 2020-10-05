package com.example;

/**
 * Pixel class that holds the RGB values of each pixel.
 */

public class Pixel {
    private int red;
    private int green;
    private int blue;

    public Pixel(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public void setRed(int value) {
        this.red = value;
    }

    public void setGreen(int value) {
        this.green = value;
    }

    public void setBlue(int value) {
        this.blue = value;
    }

    public int getRed() { return red; }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }
}
