/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import java.awt.Color;
import java.util.Objects;

/**
 * Stands for selection of tile for particular place in mosaic.
 * 
 * @author Marek Zuzi
 */
public class TileMatch implements Comparable<TileMatch> {
    private static final double PRECISION = 0.000001d;
    
    private double colorDiff = 0;
    private double patternMatch = 0;
    private Tile tile;
    private Color origColor;
    
    public TileMatch(Tile t, Color c, double diff, double match) {
        tile = t;
        origColor = c;
        colorDiff = diff;
        patternMatch = match;
    }

    public double getColorDiff() {
        return colorDiff;
    }

    public void setColorDiff(double colorDiff) {
        this.colorDiff = colorDiff;
    }

    public double getPatternMatch() {
        return patternMatch;
    }

    public void setPatternMatch(double patternMatch) {
        this.patternMatch = patternMatch;
    }
    
    public Tile getTile() {
        return tile;
    }
    
    public Color getOrigColor() {
        return origColor;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.colorDiff) ^ (Double.doubleToLongBits(this.colorDiff) >>> 32));
        hash = 53 * hash + Objects.hashCode(this.tile);
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
        final TileMatch other = (TileMatch) obj;
        if (Math.abs(this.colorDiff - other.colorDiff) > PRECISION) {
            return false;
        }
        if (!Objects.equals(this.tile, other.tile)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(TileMatch o) {
        if(this.equals(o)) return 0;
        double val = o.colorDiff - this.colorDiff;
        
        return (int)Math.signum(val);
    }
}
