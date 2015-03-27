/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import java.io.File;
import java.util.Objects;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

/**
 * Represents one tile for mosaic, that is taken from video.
 *
 * @author Marek Zuzi
 */
public class FrameTile extends Tile {

    private final int frameNum;

    /**
     * Used to create during loading tiles from video. Creates FrameTile from
     * given parameters.
     *
     * @param f file of source video
     * @param frame image extracted from video at frame i
     * @param i frame number in video
     */
    public FrameTile(File f, Mat frame, int i) {
        imgFile = f;
        meanRGB = ImageUtils.getMeanRGB(frame);
        frameNum = i;
        icon = ImageUtils.createIcon(frame, new Size(Tile.PREVIEW_WIDTH, Tile.PREVIEW_HEIGHT));
    }

    public int getFrameNum() {
        return frameNum;
    }

    @Override
    public Mat loadImage(Size s) {
        VideoCapture c = new VideoCapture(imgFile.getPath());

        if (!c.isOpened()) {
            return null;
        }

        c.set(1, frameNum);

        Mat img = new Mat();
        c.retrieve(img);

        if (s != null) {
            Mat out = new Mat();
            Imgproc.resize(img, out, s);
            img.release();
            return out;
        } else {
            return img;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + this.frameNum;
        hash = 73 * hash + Objects.hashCode(this.imgFile);
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
        final FrameTile other = (FrameTile) obj;
        if (this.frameNum != other.frameNum) {
            return false;
        }
        if (!Objects.equals(this.imgFile, other.imgFile)) {
            return false;
        }
        return true;
    }

}
