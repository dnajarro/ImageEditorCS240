package com.example;

/**
 * Image class that represents the image with an array of Pixel objects.
 */

public class Image {
    private int width;
    private int height;
    private Pixel[][] pixels;

    public Image(int height, int width) {
        this.height = height;
        this.width = width;
        pixels = new Pixel[height][width];
    }

    public Pixel getPixel(int row, int col) {
        Pixel pixel = pixels[row][col];
        return pixel;
    }

    public void setPixel(int red, int green, int blue, int row, int col) {
        Pixel pixel = new Pixel(red, green, blue);
        pixels[row][col] = pixel;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
