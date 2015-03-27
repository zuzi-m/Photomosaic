/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * File filter for image file formats.
 *
 * @author Marek Zuzi
 */
public class ImageFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f == null) {
            return false;
        }
        String name = f.getName().toLowerCase();
        return f.isDirectory() || name.endsWith(".jpg") || name.endsWith(".gif") || name.endsWith(".png") || name.endsWith(".bmp");
    }

    @Override
    public String getDescription() {
        return "Image files";
    }

}
