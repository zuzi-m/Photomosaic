/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * File filter for video file formats.
 *
 * @author Marek Zuzi
 */
public class VideoFileFilter extends FileFilter {

    @Override
    public boolean accept(File f) {
        if (f == null) {
            return false;
        }
        if (f.isDirectory()) {
            return true;
        }

        String name = f.getName().toLowerCase();
        return name.endsWith(".avi") || name.endsWith(".mov") || name.endsWith(".wmv") || name.endsWith(".mp4") || name.endsWith(".mpg");
    }

    @Override
    public String getDescription() {
        return "Video files";
    }

}
