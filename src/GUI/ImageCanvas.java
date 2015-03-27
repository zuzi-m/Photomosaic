/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Mosaic.ImageUtils;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JPanel;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Marek Zuzi
 */
public class ImageCanvas extends JPanel {
    private Mat image;
    private Rect currView;
    
    int oldX;
    int oldY;
    
    public ImageCanvas() {
        image = null;
        currView = null;
        
        CanvasMouseListener lis = new CanvasMouseListener();
        this.addMouseListener(lis);
        this.addMouseWheelListener(lis);
        this.addMouseMotionListener(lis);
        
        /*this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                ratio = getWidth()/getHeight();
                repaint();
            }
        });*/
    }

    @Override
    public void paint(Graphics g) {
        if(image == null) return;
        constrainRect();
        
        Mat area = image.submat(currView);
        Mat resized = new Mat();
        Size s = new Size(this.getWidth(),this.getHeight());
        Imgproc.resize(area, resized, s);
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        g.drawImage(ImageUtils.mat2image(resized), 0, 0, null);
        resized.release();
    }
    
    
    
    public void moveRect(int x, int y) {
        int newX = currView.x + x;
        int newY = currView.y + y;
        
        currView.x = newX;
        currView.y = newY;
        
        repaint();
    }
    
    public void setImage(Mat i) {
        if(image != null) image.release();
        image = i.clone();
        
        
        
        if(currView == null) currView = new Rect(0, 0, this.getWidth(), this.getHeight());
        
        repaint();
    }
    
    public Mat getImage() {
        return image;
    }
    
    private void constrainRect() {
        currView.width = Math.max(currView.width, 50);
        currView.width = Math.min(currView.width, image.width());
        
        double ratio = this.getWidth()/(double)this.getHeight();
        int hei = (int)(currView.width/ratio);
        currView.height = hei;
        
        currView.height = Math.max(currView.height, 1);
        currView.height = Math.min(currView.height, image.height());
        
        currView.x = Math.max(currView.x, 0);
        currView.x = Math.min(currView.x, image.width()-currView.width);
        
        currView.y = Math.max(currView.y, 0);
        currView.y = Math.min(currView.y, image.height()-currView.height);
    }
    
    private class CanvasMouseListener extends MouseAdapter
    {
        @Override
        public void mouseReleased(MouseEvent e) {
            moveRect(oldX - e.getX(), oldY - e.getY());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            oldX = e.getX();
            oldY = e.getY();
        }
        
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                int count = e.getWheelRotation();
                currView.width = currView.width+(count*currView.width/10);
                //currView.height = currView.height+(count*currView.height/10);
            }
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            moveRect(oldX - e.getX(), oldY - e.getY());
            oldX = e.getX();
            oldY = e.getY();
        }
        
        
    }
}
