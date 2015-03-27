/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * Set of static convenience methods to process/prepare images with use of
 * OpenCV
 *
 * @author Marek Zuzi
 */
public class ImageUtils {

    /**
     * Returns mean color of given Mat image.
     *
     * @param m image
     * @return Color representation of mean image color.
     */
    public static Color getMeanRGB(Mat m) {
        Scalar s = Core.mean(m);
        return new Color((int) (s.val[2]), (int) (s.val[1]), (int) (s.val[0]));
    }

    /**
     * Computes distance between two colors in simple metric.
     *
     * @param c1
     * @param c2
     * @return square root of differences between red, green and blue
     * components.
     */
    public static double getColorDifference(Color c1, Color c2) {
        int dR = c1.getRed() - c2.getRed();
        int dG = c1.getGreen() - c2.getGreen();
        int dB = c1.getBlue() - c2.getBlue();
        double distance = Math.sqrt(dR * dR + dG * dG + dB * dB);
        return distance;
    }

    /**
     * Computes pattern matching between tile and template.
     *
     * @param t tile with image to be matched
     * @param pattern pattern for matching
     * @return amount of best match between tile and pattern.
     */
    public static double getMatch(Tile t, Mat pattern) {
        Mat tile = t.loadImage(pattern.size());
        Mat out = new Mat();
        Imgproc.matchTemplate(tile, pattern, out, Imgproc.TM_CCORR);
        Core.MinMaxLocResult res = Core.minMaxLoc(out);
        out.release();
        tile.release();

        return res.maxVal;
    }

    /**
     * Blends given image with target color, in specified amount of percent.
     *
     * @param img image to be adjusted
     * @param targetColor target color
     * @param percent amount of blending
     * @return blended image.
     */
    public static Mat adjustImageToColor(Mat img, Color targetColor, int percent) {
        Scalar color = new Scalar(targetColor.getBlue(), targetColor.getGreen(), targetColor.getRed());
        Mat temp = new Mat(img.rows(), img.cols(), img.type(), color);
        Mat res = new Mat();

        double alpha = percent / 100.0d;

        Core.addWeighted(img, 1 - alpha, temp, alpha, 0, res);
        temp.release();
        return res;
    }

    /**
     * Blends given image with target image, in specified amount of percent.
     *
     * @param img image to be adjusted
     * @param template target image
     * @param percent amount of blending
     * @return blended image.
     */
    public static Mat adjustImageToColor(Mat img, Mat template, int percent) {
        double alpha = percent / 100.0d;
        Mat res = new Mat();

        Core.addWeighted(img, 1 - alpha, template, alpha, 0, res);
        return res;
    }

    /**
     * Creates ImageIcon with given size from given image.
     *
     * @param img image for icon
     * @param iconSize size of icon
     * @return icon with iconSize
     */
    public static ImageIcon createIcon(Mat img, Size iconSize) {
        Mat dst = new Mat();
        Imgproc.resize(img, dst, iconSize, 0, 0, Imgproc.INTER_LINEAR);

        ImageIcon res = new ImageIcon(mat2image(dst));
        dst.release();

        return res;
    }

    /**
     * Creates ImageIcon from given image, uniformly resized by sizeMult
     *
     * @param img image for icon
     * @param sizeMult size multiplier
     * @return icon resized by sizeMult
     */
    public static ImageIcon createIcon(Mat img, double sizeMult) {
        Size size = new Size(img.width() * sizeMult, img.height() * sizeMult);

        return createIcon(img, size);
    }

    /**
     * Loads image from specified file. If OpenCV can not load image, it is
     * tried with BufferedImage.
     *
     * @param f source file of image
     * @return Mat representation of source image or null if file could not be
     * loaded
     */
    public static Mat loadImage(File f) {
        Mat res = Highgui.imread(f.getPath());
        if (!res.empty()) {
            return res;
        }

        BufferedImage bi = null;
        try {
            bi = ImageIO.read(f);
        } catch (IOException e) {
            return res;
        }
        res = ImageUtils.image2mat(bi);

        return res;
    }

    /**
     * Converts Mat image to BufferedImage
     *
     * @param matrix Mat image
     * @return BufferedImage
     */
    public static BufferedImage mat2image(Mat matrix) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int) matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;

        matrix.get(0, 0, data);

        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;

            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;

                // bgr to rgb
                byte b;
                for (int i = 0; i < data.length; i = i + 3) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;

            default:
                return null;
        }

        BufferedImage image = new BufferedImage(cols, rows, type);
        image.getRaster().setDataElements(0, 0, cols, rows, data);

        return image;
    }

    /**
     * Converts BufferedImage to Mat image.
     *
     * @param bi image
     * @return Mat representation of image
     */
    public static Mat image2mat(BufferedImage bi) {
        File f = null;
        try {
            f = File.createTempFile("tempImg", ".jpg");
            ImageIO.write(bi, "jpg", f);
        } catch (IOException e) {
            return new Mat();
        }

        return Highgui.imread(f.getPath());
    }

}
