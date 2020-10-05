package com.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.lang.String;
import java.lang.Integer;
import java.io.BufferedWriter;

public class ImageEditor {
    public void write(Image image, int w, int h, File output) {
        // each image starts with P3
        // each element must be followed by a whitespace
        StringBuilder sb = new StringBuilder();
        sb.append("P3\n");
        sb.append(Integer.toString(w));
        sb.append("\n");
        sb.append(Integer.toString(h));
        sb.append("\n");
        sb.append("255\n");
        int row = 0;
        int col = 0;
        for (int i = 0; i < w * h; i++) {
            if (i != 0) {
                // reset col value every time that i is incremented
                col = i % w;
                if (col == 0) {
                    // reset row value each time i reaches the leftmost column
                    row++;
                }
            }
            sb.append(image.getPixel(row, col).getRed());
            sb.append("\n");
            sb.append(image.getPixel(row, col).getGreen());
            sb.append("\n");
            sb.append(image.getPixel(row, col).getBlue());
            sb.append("\n");
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));
            bw.write(sb.toString());
            bw.close();
        } catch (IOException exception) {   System.err.println(exception); }
    }

    public static void main(String[] args) {
        File input = new File(args[0]);
        File output = new File(args[1]);
        String effect;
        effect = args[2];
        try {
            // ignore all the comments
            Scanner in = new Scanner(input).useDelimiter("((#[^\\n]*\\n)|(\\s+))+");
            if (in.hasNext()) {
                in.next();
            }
            int w = 0;
            int h = 0;
            int maxVal = 0;
            // save the values for width, height, and the maximum RGB value
            while ((w == 0 || h == 0 || maxVal == 0)) {
                if (in.hasNext()) {
                    String copy = in.next();
                    if (w == 0) {
                        w = Integer.parseInt(copy);
                    }
                    else if (h == 0) {
                        h = Integer.parseInt(copy);
                    }
                    else if (maxVal == 0) {
                        maxVal = Integer.parseInt(copy);
                    }
                }
            }
            Image image = new Image(h, w);
            int pos = 0;
            boolean setRed = true;
            boolean setGreen = false;
            boolean setBlue = false;
            int row = 0;
            int col = 0;
            while (in.hasNext()) {
                // parse through the file and set each of the RGB values for each of the Pixels
                String copy = in.next();
                int value = Integer.parseInt(copy);
                    if (pos != 0) {
                        // increment the col each time that another value is read in
                        col = pos % w;
                        // increment row each time that we are at the leftmost column's Red value
                        if (col == 0 && setRed) {
                            row++;
                        }
                    }
                    if (setRed) {
                        image.setPixel(value, 0, 0, row, col);
                        setRed = false;
                        setGreen = true;
                    }
                    else if (setGreen) {
                        image.getPixel(row, col).setGreen(value);
                        setGreen = false;
                        setBlue = true;
                    }
                    else if (setBlue) {
                        image.getPixel(row, col).setBlue(value);
                        setBlue = false;
                        setRed = true;
                        pos++;
                    }
            }
            // close the input file reader
            in.close();
            Image newImage = new Image(h, w);
            if ("invert".equals(effect)) { // invert, subtract RGB values from maxValue
                row = 0;
                col = 0;
                for (int i = 0; i < w * h; i++) {
                    if (i != 0) {
                        col = i % w;
                        if (col == 0) {
                            row++;
                        }
                    }
                    int r = image.getPixel(row, col).getRed();
                    int g = image.getPixel(row, col).getGreen();
                    int b = image.getPixel(row, col).getBlue();
                    int invR = maxVal - r;
                    int invG = maxVal - g;
                    int invB = maxVal - b;
                    newImage.setPixel(invR, invG, invB, row, col);
                }
            } else if ("grayscale".equals(effect)) { // grayscale, average the RGB values
                row = 0;
                col = 0;
                for (int i = 0; i < w * h; i++) {
                    if (i != 0) {
                        col = i % w;
                        if (col == 0) {
                            row++;
                        }
                    }
                    int r = image.getPixel(row, col).getRed();
                    int g = image.getPixel(row, col).getGreen();
                    int b = image.getPixel(row, col).getBlue();
                    int avg = (r + b + g) / 3;
                    newImage.setPixel(avg, avg, avg, row, col);
                }
            } else if ("emboss".equals(effect)) {
                /* emboss, Find the greatest difference between the RGB values of the current pixel
                and its top-left neighbor. Then use that value to replace the RGB values of the
                current pixel. */
                row = 0;
                col = 0;
                for (int i = 0; i < w * h; i++) {
                    if (i != 0) {
                        col = i % w;
                        if (col == 0) {
                            row++;
                        }
                    }
                    int r = image.getPixel(row, col).getRed();
                    int g = image.getPixel(row, col).getGreen();
                    int b = image.getPixel(row, col).getBlue();
                    int v;
                    if (row == 0 || col == 0) {
                        v = 128;
                    } else {
                        int redDiff = r - image.getPixel((row - 1), (col - 1)).getRed();
                        int greenDiff = g - image.getPixel((row - 1), (col - 1)).getGreen();
                        int blueDiff = b - image.getPixel((row - 1), (col - 1)).getBlue();
                        int absRedDiff = Math.abs(redDiff);
                        int absGreenDiff = Math.abs(greenDiff);
                        int absBlueDiff = Math.abs(blueDiff);
                        int maxDiff = 0;
                        if (absRedDiff == absGreenDiff) {
                            if (absRedDiff >= absBlueDiff) {
                                maxDiff = redDiff;
                            } else {
                                maxDiff = blueDiff;
                            }
                        } else if (absRedDiff == absBlueDiff) {
                            if (absRedDiff >= absGreenDiff) {
                                maxDiff = redDiff;
                            } else {
                                maxDiff = greenDiff;
                            }
                        } else if (absGreenDiff == absBlueDiff) {
                            if (absGreenDiff > absRedDiff) {
                                maxDiff = greenDiff;
                            } else if (absGreenDiff <= absRedDiff) {
                                maxDiff = redDiff;
                            }
                        } else {
                            int absMaxDiff = Math.max(Math.max(absRedDiff, absGreenDiff), absBlueDiff);
                            if (absMaxDiff == Math.abs(redDiff)) {
                                maxDiff = redDiff;
                            } else if (absMaxDiff == Math.abs(greenDiff)) {
                                maxDiff = greenDiff;
                            } else {
                                maxDiff = blueDiff;
                            }
                        }
                        v = 128 + maxDiff;
                        if (v < 0) {
                            v = 0;
                        } else if (v > maxVal) {
                            v = maxVal;
                        }
                    }
                    newImage.setPixel(v, v, v, row, col);
                }
            } else if ("motionblur".equals(effect)) {
                /* motionblur, using the given value for blurLevel,
                average the RGB values of all the pixels that are within x + blurLevel columns to
                the right of the current pixel, where x is the value of the current pixel's column */
                int blurLevel = Integer.parseInt(args[3]);
                if (blurLevel > 0) {
                    row = 0;
                    col = 0;
                    for (int i = 0; i < w * h; i++) {
                        if (i != 0) {
                            col = i % w;
                            if (col == 0) {
                                row++;
                            }
                        }
                        int redAvg = 0;
                        int greenAvg = 0;
                        int blueAvg = 0;
                        int totalPixels = 0;
                        if (blurLevel > 1) {
                            for (int j = col; j < (col + blurLevel); j++) {
                                if (j < w) {
                                    totalPixels++;
                                    redAvg = redAvg + image.getPixel(row, j).getRed();
                                    greenAvg = greenAvg + image.getPixel(row, j).getGreen();
                                    blueAvg = blueAvg + image.getPixel(row, j).getBlue();
                                }
                            }
                        }
                        else {
                            totalPixels = 1;
                            redAvg = image.getPixel(row, col).getRed();
                            greenAvg = image.getPixel(row, col).getGreen();
                            blueAvg = image.getPixel(row, col).getBlue();
                        }
                        redAvg = redAvg / totalPixels;
                        greenAvg = greenAvg / totalPixels;
                        blueAvg = blueAvg / totalPixels;
                        newImage.setPixel(redAvg, greenAvg, blueAvg, row, col);
                    }
                }
                else {
                    row = 0;
                    col = 0;
                    for (int i = 0; i < w * h; i++) {
                        if (i != 0) {
                            col = i % w;
                            if (col == 0) {
                                row++;
                            }
                        }
                        int r = image.getPixel(row, col).getRed();
                        int g = image.getPixel(row, col).getGreen();
                        int b = image.getPixel(row, col).getBlue();
                        newImage.setPixel(r, g, b, row, col);
                    }
                }
            }
            else { // if no valid commands are given, output the original image
                row = 0;
                col = 0;
                for (int i = 0; i < w * h; i++) {
                    if (i != 0) {
                        col = i % w;
                        if (col == 0) {
                            row++;
                        }
                    }
                    int r = image.getPixel(row, col).getRed();
                    int g = image.getPixel(row, col).getGreen();
                    int b = image.getPixel(row, col).getBlue();
                    newImage.setPixel(r, g, b, row, col);
                }
            }
            ImageEditor ie = new ImageEditor();
            ie.write(newImage, w, h, output);
        }   catch (IOException exception) {   System.err.println(exception); }
    }
}
