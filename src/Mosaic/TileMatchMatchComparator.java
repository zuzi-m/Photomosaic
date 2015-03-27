/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import java.util.Comparator;

/**
 * Compares TileMatch objects according to pattern match.
 * 
 * @author Marek Zuzi
 */
public class TileMatchMatchComparator implements Comparator<TileMatch> {
    @Override
    public int compare(TileMatch o1, TileMatch o2) {
        double diff = o2.getPatternMatch() - o1.getPatternMatch();
        
        return (int)Math.signum(diff);
    }
    
}
