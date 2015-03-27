/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mosaic;

import Mosaic.Mosaic.Blend;
import Mosaic.Mosaic.SelectAlgorithm;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class for saving and loading mosaic settings to file in simple key=value
 * format.
 *
 * @author Marek Zuzi
 */
public class MosaicSettingIO {

    public static Mosaic loadMosaic(File srcFile) {
        Mosaic m = null;
        try (BufferedReader rd = new BufferedReader(new FileReader(srcFile))) {
            m = new Mosaic();
            while (rd.ready()) {
                String line = rd.readLine();
                String[] parts = line.split("=");
                if (parts.length != 2) {
                    return null;
                }
                switch (parts[0]) {
                    case "xTiles":
                        m.setxTiles(Integer.parseInt(parts[1]));
                        break;
                    case "yTiles":
                        m.setyTiles(Integer.parseInt(parts[1]));
                        break;
                    case "tileWidth":
                        m.setTileWidth(Integer.parseInt(parts[1]));
                        break;
                    case "tileHeight":
                        m.setTileHeight(Integer.parseInt(parts[1]));
                        break;
                    case "algorithm":
                        m.setAlgorithm(parseAlgorithm(parts[1]));
                        break;
                    case "avoidRep":
                        m.setAvoidRep(Boolean.parseBoolean(parts[1]));
                        break;
                    case "blend":
                        m.setBlend(parseBlend(parts[1]));
                        break;
                    case "blendAmount":
                        m.setBlendAmount(Integer.parseInt(parts[1]));
                        break;
                }
            }
        } catch (IOException e) {
            return null;
        }

        return m;
    }

    public static boolean saveMosaic(File dstFile, Mosaic m) {
        String path = dstFile.getPath();
        if (!path.endsWith(".msf")) {
            path = path + ".msf";
        }
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(path))) {
            wr.write("xTiles=" + m.getxTiles());
            wr.newLine();
            wr.write("yTiles=" + m.getyTiles());
            wr.newLine();
            wr.write("tileWidth=" + m.getTileWidth());
            wr.newLine();
            wr.write("tileHeight=" + m.getTileHeight());
            wr.newLine();
            wr.write("algorithm=" + m.getAlgorithm());
            wr.newLine();
            wr.write("avoidRep=" + m.isAvoidRep());
            wr.newLine();
            wr.write("blend=" + m.getBlend());
            wr.newLine();
            wr.write("blendAmount=" + m.getBlendAmount());
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private static SelectAlgorithm parseAlgorithm(String str) {
        switch (str) {
            case "RANDOM":
                return SelectAlgorithm.RANDOM;
            case "COLOR":
                return SelectAlgorithm.COLOR;
            case "MATCH":
                return SelectAlgorithm.MATCH;
            default:
                return SelectAlgorithm.RANDOM;
        }
    }

    private static Blend parseBlend(String str) {
        switch (str) {
            case "NOBLEND":
                return Blend.NOBLEND;
            case "COLOR":
                return Blend.COLOR;
            case "ORIGINAL":
                return Blend.ORIGINAL;
            default:
                return Blend.NOBLEND;
        }
    }
}
