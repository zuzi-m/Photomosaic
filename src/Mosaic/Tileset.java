/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;
import org.opencv.core.Mat;

/**
 * Class representing set of tiles that can form a mosaic. Can serve as table model.
 * 
 * @author Marek Zuzi
 */
public class Tileset extends AbstractTableModel {

    private ArrayList<Tile> tiles;
    private Random r;

    public Tileset() {
        tiles = new ArrayList<>();
        r = new Random();
    }

    public Tile loadTile(File src) {
        Tile t = null;
        try {
            t = new Tile(src);
        } catch (IllegalArgumentException e) {
            return null;
        }

        tiles.add(t);

        this.fireTableDataChanged();
        return t;
    }

    public void loadTiles(File dir) {
        for (File f : dir.listFiles()) {
            loadTile(f);
        }
    }

    public void addTile(Tile t) {
        tiles.add(t);
        this.fireTableDataChanged();
    }

    public void removeTile(int row, int col) {
        int idx = row * this.getColumnCount() + col;

        if (idx < 0 || idx >= tiles.size()) {
            return;
        }

        tiles.remove(idx);
        this.fireTableDataChanged();
    }

    public TileMatch chooseTile(Mat origTile, Mosaic.SelectAlgorithm algorithm, boolean avoidRep) {
        Color mean = ImageUtils.getMeanRGB(origTile);
        
        if(algorithm == Mosaic.SelectAlgorithm.RANDOM) {
            Tile t = tiles.get(r.nextInt(tiles.size()));
            return new TileMatch(t, mean, ImageUtils.getColorDifference(mean, t.getMeanRGB()), 0);
        }

        TreeSet<TileMatch> matches = new TreeSet<>();
        for (Tile t : tiles) {
            double diff = ImageUtils.getColorDifference(mean, t.getMeanRGB());
            TileMatch m = new TileMatch(t, mean, diff, 0);
            matches.add(m);
        }
        
        if(algorithm == Mosaic.SelectAlgorithm.COLOR && !avoidRep) return matches.last();
        
        // select only limited range of considered tiles
        int limitCount = (int)Math.ceil(tiles.size()*0.2);
        if(algorithm == Mosaic.SelectAlgorithm.MATCH && limitCount > 50) {
            limitCount = 50;
        }
        TileMatch temp = matches.last();
        ArrayList<TileMatch> candidates = new ArrayList<>(limitCount);
        for(int i=0; i<limitCount; i++) {
            if(algorithm == Mosaic.SelectAlgorithm.MATCH) {
                temp.setPatternMatch(ImageUtils.getMatch(temp.getTile(), origTile));
            }
            candidates.add(temp);
            temp = matches.lower(temp);
        }
        if(algorithm == Mosaic.SelectAlgorithm.MATCH) {
            candidates.sort(new TileMatchMatchComparator());
        }else {
            candidates.sort(new TileMatchPriorityComparator());
        }

        candidates.get(0).getTile().incPriority();
        return candidates.get(0);
    }
    
    public void resetPriority() {
        for(Tile t : tiles) {
            t.resetPriority(0);
        }
    }

    public void setTiles(ArrayList<Tile> t) {
        tiles = t;

        this.fireTableDataChanged();
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public Tile getTile(int index) {
        if (index < 0 || index >= tiles.size()) {
            throw new IllegalArgumentException("index");
        }

        return tiles.get(index);
    }

    @Override
    public int getRowCount() {
        int count = tiles.size() / this.getColumnCount();
        if (tiles.size() % this.getColumnCount() != 0) {
            count++;
        }
        return count;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int idx = rowIndex * 3 + columnIndex;
        if ((idx >= 0) && (idx < tiles.size())) {
            return tiles.get(idx).getIcon();
        } else {
            return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int index) {
        return ImageIcon.class;
    }

    @Override
    public String getColumnName(int index) {
        return "";
    }
}
