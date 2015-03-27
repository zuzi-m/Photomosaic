/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Represents mosaic with all its parameters and is used to create mosaic
 *
 * @author Marek Zuzi
 */
public class Mosaic {

    private Mat img;
    private Mat result;

    private int xTiles = 10;
    private int yTiles = 10;

    private int tileWidth = 20;
    private int tileHeight = 20;

    private SelectAlgorithm algorithm = SelectAlgorithm.COLOR;
    private boolean avoidRep = false;
    private Blend blend = Blend.NOBLEND;
    private int blendAmount = 50;

    private int progress;
    private PropertyChangeSupport supp = new PropertyChangeSupport(this);

    public enum SelectAlgorithm {

        RANDOM, COLOR, MATCH
    }

    public enum Blend {

        NOBLEND, COLOR, ORIGINAL
    }

    public Mosaic() {
        img = null;
    }

    /**
     * Creates mosaic from given tileset. Applies currently set parameters and
     * reports progress to its property change listeners.
     *
     * @param tileset tileset to create mosaic
     * @return image with mosaic
     */
    public Mat createMosaic(Tileset tileset) {
        checkState();
        tileset.resetPriority();
        this.setProgress(0);

        Mat imgMod = new Mat();
        Imgproc.resize(img, imgMod, new Size(xTiles * tileWidth, yTiles * tileHeight));

        TileMatch[][] matches = new TileMatch[yTiles][xTiles];
        ArrayList<Integer> indices = new ArrayList<>(xTiles * yTiles);
        for (int i = 0; i < yTiles; i++) {
            for (int j = 0; j < xTiles; j++) {
                indices.add(i * xTiles + j);
            }
        }

        // assemble mosaic randomly to avoid uniform degradation of homogenous regions
        Random r = new Random();
        int count = 0;
        int size = indices.size();
        while (!indices.isEmpty()) {
            int rand = r.nextInt(indices.size());
            int index = indices.get(rand);
            indices.remove(rand);

            int i = index / xTiles;
            int j = index % xTiles;
            Mat origTile = imgMod.submat(i * tileHeight, i * tileHeight + tileHeight, j * tileWidth, j * tileWidth + tileWidth);
            matches[i][j] = tileset.chooseTile(origTile, algorithm, avoidRep);

            count++;
            double prog = count / ((double) size);
            this.setProgress((int) (prog * 80));
        }

        return drawMosaic(matches, new Size(tileWidth, tileHeight), imgMod);
    }

    /**
     * Draws mosaic by applying tile matches.
     *
     * @param matches array of tiles selected
     * @param tileSize size of one tile in result image
     * @param res template image resized to result mosaic size
     * @return result mosaic image
     */
    private Mat drawMosaic(TileMatch[][] matches, Size tileSize, Mat res) {

        int width = (int) tileSize.width;
        int height = (int) tileSize.height;

        HashMap<Tile, Mat> loadedTiles = new HashMap<>();
        for (int i = 0; i < yTiles; i++) {
            for (int j = 0; j < xTiles; j++) {
                TileMatch match = matches[i][j];

                if (!loadedTiles.containsKey(match.getTile())) {
                    loadedTiles.put(match.getTile(), match.getTile().loadImage(tileSize));
                }

                Mat tile = loadedTiles.get(match.getTile());
                int rowPos = i * height;
                int colPos = j * width;
                Mat origTile = res.rowRange(rowPos, rowPos + height).colRange(colPos, colPos + width);

                if (blend == Blend.COLOR) {
                    Mat adjusted = ImageUtils.adjustImageToColor(tile, match.getOrigColor(), blendAmount);
                    adjusted.copyTo(res.rowRange(rowPos, rowPos + height).colRange(colPos, colPos + width));
                } else if (blend == Blend.ORIGINAL) {
                    Mat adjusted = ImageUtils.adjustImageToColor(tile, origTile, blendAmount);
                    adjusted.copyTo(origTile);
                } else {
                    tile.copyTo(origTile);
                }
            }
        }

        for (Mat m : loadedTiles.values()) {
            m.release();
        }

        this.setProgress(100);
        return res;
    }

    private void checkState() {
        if (xTiles <= 0) {
            throw new IllegalArgumentException("xTiles < 0");
        }
        if (yTiles <= 0) {
            throw new IllegalArgumentException("yTiles < 0");
        }
        if (tileWidth <= 0) {
            throw new IllegalArgumentException("tileWidth < 0");
        }
        if (tileHeight <= 0) {
            throw new IllegalArgumentException("tileHeight < 0");
        }
        if (img == null) {
            throw new IllegalArgumentException("Img is null");
        }
    }

    public Mat getImg() {
        return img;
    }

    public void setImg(Mat img) {
        this.img = img;
    }

    public int getxTiles() {
        return xTiles;
    }

    public void setxTiles(int xTiles) {
        if (xTiles <= 0) {
            throw new IllegalArgumentException("xTiles <= 0");
        }
        this.xTiles = xTiles;
    }

    public int getyTiles() {
        return yTiles;
    }

    public void setyTiles(int yTiles) {
        if (yTiles <= 0) {
            throw new IllegalArgumentException("yTiles <= 0");
        }
        this.yTiles = yTiles;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
        if (tileWidth <= 0) {
            throw new IllegalArgumentException("tileWidth <= 0");
        }
        this.tileWidth = tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        if (tileHeight <= 0) {
            throw new IllegalArgumentException("tileHeight <= 0");
        }
        this.tileHeight = tileHeight;
    }

    public Blend getBlend() {
        return blend;
    }

    public void setBlend(Blend blendColor) {
        this.blend = blendColor;
    }

    public boolean isAvoidRep() {
        return avoidRep;
    }

    public void setAvoidRep(boolean avoidRep) {
        this.avoidRep = avoidRep;
    }

    public Mat getResult() {
        return result;
    }

    public void setResult(Mat result) {
        this.result = result;
    }

    public int getBlendAmount() {
        return blendAmount;
    }

    public void setBlendAmount(int blendAmount) {
        this.blendAmount = blendAmount;
    }

    public void setAlgorithm(SelectAlgorithm a) {
        algorithm = a;
    }

    public SelectAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        supp.firePropertyChange("progress", this.progress, progress);
        this.progress = progress;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        supp.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        supp.removePropertyChangeListener(l);
    }
}
