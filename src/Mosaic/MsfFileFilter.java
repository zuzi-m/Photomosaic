/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * File filter for mosaic settings files.
 * 
 * @author Marek Zuzi
 */
public class MsfFileFilter extends FileFilter {

    @Override
    public boolean accept(File pathname) {
        String name = pathname.getName().toLowerCase();
        return pathname.isDirectory() | name.endsWith(".msf");
    }
    
    @Override
    public String getDescription() {
        return "*.msf - mosaic settings file";
    }
    
}
