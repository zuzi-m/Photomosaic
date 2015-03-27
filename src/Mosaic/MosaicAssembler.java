/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import GUI.ImageCanvas;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import org.opencv.core.Mat;

/**
 * Wrapper used to create mosaic using SwingWorker
 *
 * @author Marek Zuzi
 */
public class MosaicAssembler extends SwingWorker<Mat, Void> {

    private Mosaic m;
    private ImageCanvas displayCanvas;
    private JLabel statusLabel;
    private Tileset tileset;

    public MosaicAssembler(Mosaic m, Tileset t) {
        this.m = m;
        tileset = t;
    }

    @Override
    protected Mat doInBackground() throws Exception {
        Logger.getLogger(Mosaic.class.getName()).log(Level.INFO, "Mosaic creation started.");
        super.setProgress(0);
        checkState();

        Mat result = m.createMosaic(tileset);

        super.setProgress(100);
        return result;
    }

    @Override
    public void done() {
        try {
            Mat result = super.get();
            if (displayCanvas != null) {
                displayCanvas.setImage(result);
            }
            if (statusLabel != null) {
                statusLabel.setText(ResourceBundle.getBundle("Resources/Strings").getString("mosaicReady"));
            }
            m.setResult(result);
            Logger.getLogger(Mosaic.class.getName()).log(Level.INFO, "Mosaic finished.");
            return;
        } catch (InterruptedException ex) {
            Logger.getLogger(Mosaic.class.getName()).log(Level.SEVERE, "Mosaic interrupted", ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(Mosaic.class.getName()).log(Level.SEVERE, "Mosaic error", ex);
        }

        if (statusLabel != null) {
            statusLabel.setText(ResourceBundle.getBundle("Resources/Strings").getString("interrupted"));
        }
    }

    public Mosaic getMosaic() {
        return m;
    }

    public void setMosaic(Mosaic m) {
        this.m = m;
    }

    public ImageCanvas getDisplayCanvas() {
        return displayCanvas;
    }

    public void setDisplayCanvas(ImageCanvas displayLabel) {
        this.displayCanvas = displayLabel;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(JLabel statusLabel) {
        this.statusLabel = statusLabel;
    }

    public Tileset getTileset() {
        return tileset;
    }

    public void setTileset(Tileset tileset) {
        this.tileset = tileset;
    }

    private void checkState() {
        if (m == null) {
            throw new IllegalArgumentException("Mosaic is null");
        }
        if (tileset == null) {
            throw new IllegalArgumentException("Tileset is null");
        }
    }

}
