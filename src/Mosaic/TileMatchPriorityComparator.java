/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import java.util.Comparator;

/**
 * Compares TileMatch objects according to priority.
 * 
 * @author Marek Zuzi
 */
public class TileMatchPriorityComparator implements Comparator<TileMatch> {
    @Override
    public int compare(TileMatch o1, TileMatch o2) {
        int val = o1.getTile().getPriority() - o2.getTile().getPriority();
        
        return val;
    }
    
}
