/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

/**
 * Class for loading tiles using SwingWorker
 *
 * @author Marek Zuzi
 */
public class TileLoader extends SwingWorker<Void, Void> {

    private Tileset tileset;
    private JLabel updateLabel;
    private JLabel countLabel;
    private File file;
    private JButton button;
    private String resultStr;
    private final ImageFileFilter filter = new ImageFileFilter();

    private Tile loadTile(File src) {
        if (src == null) {
            return null;
        }
        if (!src.isFile()) {
            return null;
        }

        Tile t = null;
        try {
            t = new Tile(src);
        } catch (IllegalArgumentException e) {
            Logger.getLogger(TileLoader.class.getName()).log(Level.WARNING, "Tile not loaded - " + src.getAbsolutePath());
            return null;
        }

        tileset.addTile(t);
        Logger.getLogger(TileLoader.class.getName()).log(Level.INFO, "Tile loaded - {0}", src.getAbsolutePath());
        return t;
    }

    private int loadTiles(File srcDir) {
        if (srcDir == null || !srcDir.isDirectory()) {
            return 0;
        }

        int count = 0;
        File[] files = srcDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isFile()) {
                continue;
            }
            if (!filter.accept(files[i])) {
                continue;
            }

            Tile t = loadTile(files[i]);
            if (t != null) {
                count++;
            }
            double prog = (i + 1) / (files.length * 1d);
            super.setProgress((int) Math.floor(prog * 100));
        }

        return count;
    }

    @Override
    protected Void doInBackground() throws Exception {
        checkState();
        super.setProgress(0);

        if (file.isFile()) {
            Tile t = null;
            t = loadTile(file);
            if (t != null) {
                resultStr = ResourceBundle.getBundle("Resources/Strings").getString("fileLoaded") + file.getName();
            } else {
                resultStr = ResourceBundle.getBundle("Resources/Strings").getString("cantLoad") + file.getName();
            }
        } else if (file.isDirectory()) {
            int count = loadTiles(file);
            resultStr = ResourceBundle.getBundle("Resources/Strings").getString("fileLoaded") + count;
        } else {
            resultStr = ResourceBundle.getBundle("Resources/Strings").getString("cantLoad");
        }

        super.setProgress(100);
        return null;
    }

    @Override
    public void done() {
        if (button != null) {
            button.setEnabled(true);
        }
        if (updateLabel != null) {
            updateLabel.setText(resultStr);
        }
        if(countLabel != null) {
            countLabel.setText(ResourceBundle.getBundle("Resources/Strings").getString("tileCount") + tileset.getTiles().size());
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File f) {
        file = f;
    }

    public Tileset getTileset() {
        return tileset;
    }

    public void setTileset(Tileset tileset) {
        this.tileset = tileset;
    }

    public JLabel getUpdateLabel() {
        return updateLabel;
    }

    public void setUpdateLabel(JLabel updateLabel) {
        this.updateLabel = updateLabel;
    }

    public JButton getButton() {
        return button;
    }

    public void setButton(JButton button) {
        this.button = button;
    }

    public JLabel getCountLabel() {
        return countLabel;
    }

    public void setCountLabel(JLabel countLabel) {
        this.countLabel = countLabel;
    }

    private void checkState() {
        if (tileset == null) {
            throw new IllegalArgumentException("tileset is null");
        }
        if (file == null) {
            throw new IllegalArgumentException("source file is null");
        }
    }
}
