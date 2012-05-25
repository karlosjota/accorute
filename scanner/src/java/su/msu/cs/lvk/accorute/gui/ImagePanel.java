package su.msu.cs.lvk.accorute.gui;
/*
 * ImagePanel.java
 *
 * Copyright (C) 2007  Scott Carpenter (scottc at movingtofreedom dot org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Created on November 9, 2007, 4:07 PM
 *
 */

import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.*;

public class ImagePanel extends JPanel {

    private Image image;
    private Image scaledImage;
    private int imageWidth = 0;
    private int imageHeight = 0;
    //private long paintCount = 0;

    //constructor
    public ImagePanel() {
        super();
    }

    public void loadImage(String file) throws IOException {
        image = ImageIO.read(new File(file));
        //might be a situation where image isn't fully loaded, and
        //  should check for that before setting...
        imageWidth = image.getWidth(this);
        imageHeight = image.getHeight(this);
        //setScaledImage();
    }

    //e.g., containing frame might call this from formComponentResized
    public void scaleImage() {
        setScaledImage();
    }

    //override paintComponent
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if ( scaledImage != null ) {
            //System.out.println("ImagePanel paintComponent " + ++paintCount);
            g.drawImage(scaledImage, 0, 0, this);
        }
    }

    private void setScaledImage() {
        if ( image != null ) {

            //use floats so division below won't round
            float iw = imageWidth;
            float ih = imageHeight;
            float pw = this.getWidth();   //panel width
            float ph = this.getHeight();  //panel height

            if ( pw < iw || ph < ih ) {

                /* compare some ratios and then decide which side of image to anchor to panel
                   and scale the other side
                   (this is all based on empirical observations and not at all grounded in theory)*/

                //System.out.println("pw/ph=" + pw/ph + ", iw/ih=" + iw/ih);

                if ( (pw / ph) > (iw / ih) ) {
                    iw = -1;
                    ih = ph;
                } else {
                    iw = pw;
                    ih = -1;
                }

                //prevent errors if panel is 0 wide or high
                if (iw == 0) {
                    iw = -1;
                }
                if (ih == 0) {
                    ih = -1;
                }

                scaledImage = image.getScaledInstance(
                            new Float(iw).intValue(), new Float(ih).intValue(), Image.SCALE_DEFAULT);

            } else {
                scaledImage = image;
            }

            //System.out.println("iw = " + iw + ", ih = " + ih + ", pw = " + pw + ", ph = " + ph);
        }
    }

}