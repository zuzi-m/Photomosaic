/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.Objects;
import javax.swing.ImageIcon;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * Represents one tile used to create photo mosaic.
 *
 * @author Marek Zuzi
 */
public class Tile implements Comparable<Tile> {

    public static final int PREVIEW_WIDTH = 100;
    public static final int PREVIEW_HEIGHT = 100;

    protected File imgFile;
    protected Color meanRGB;
    protected ImageIcon icon;
    private int priority = 0;

    public Tile(File f) {
        imgFile = f;
        Mat i = ImageUtils.loadImage(f);
        if (i.empty()) {
            throw new IllegalArgumentException("File could not be read.");
        }

        icon = ImageUtils.createIcon(i, new Size(PREVIEW_WIDTH, PREVIEW_HEIGHT));
        meanRGB = ImageUtils.getMeanRGB(i);

        i.release();
    }
    
    protected Tile() {
        
    }
    
    public Mat loadImage(Size s) {
        Mat out = new Mat();
        Mat img = ImageUtils.loadImage(imgFile);
        
        if(img.empty()) return null;
        
        if(s != null) {
            Imgproc.resize(img, out, s);
            img.release();
            return out;
        }else {
            out.release();
            return img;
        }
    }

    public File getImgFile() {
        return imgFile;
    }

    public Color getMeanRGB() {
        return meanRGB;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public int getPriority() {
        return priority;
    }

    public void resetPriority(int priority) {
        this.priority = 0;
    }
    
    public void incPriority() {
        this.priority++;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.imgFile);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tile other = (Tile) obj;
        if (!Objects.equals(this.imgFile, other.imgFile)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Tile o) {
        return 0;
    }

}
