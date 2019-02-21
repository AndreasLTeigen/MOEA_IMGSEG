package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    public static final float rgbMaxVal = 255;

    public static Image loadImage(String filename) {
        BufferedImage buffImg = null;

        try {
            buffImg = ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.err.println("could not load image: "+filename);
            return null;
        }

        Raster raster = buffImg.getRaster(); //the raster object has better pixel obtainment
        int w = raster.getWidth();
        int h = raster.getHeight();

        //y values will be in the outer list, because we want our matrix row-major
        List<List<Pixel>> pixels = new ArrayList<>(h);

        for (int y = 0; y < h; y++) {
            List<Pixel> row = new ArrayList<>(w);

            for (int x = 0; x < w; x++) {
                float[] rgb = raster.getPixel(x,y, (float[])null);

                Pixel p = new Pixel(rgb[0]/rgbMaxVal, rgb[1]/rgbMaxVal, rgb[2]/rgbMaxVal,
                                    y, x);
                row.add(p);
            }
            pixels.add(row);
        }

        return new Image(pixels);
    }

    public static void drawImage(Image img) {
        //create a buffered image
        BufferedImage oimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                setPixel(oimg, x, y, img.getPixel(x, y));
            }
        }

        JFrame frame = new JFrame("image");
        frame.setVisible(true);

        frame.add(new JLabel(new ImageIcon(oimg)));

        frame.pack();
//      frame.setSize(WIDTH, HEIGHT);
        // Better to DISPOSE than EXIT
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * Draw pixels into an image, given rgb in the range [0,1]
     */
    private static void setPixel(BufferedImage img, int x, int y, Pixel p) {
        int alpha = (int)rgbMaxVal;
        int red = (int)(p.r*rgbMaxVal);
        int green = (int)(p.g*rgbMaxVal);
        int blue = (int)(p.b*rgbMaxVal);
        int rgb = (alpha<<24) | (red<<16) | (green<<8) | blue;
        img.setRGB(x, y, rgb);
    }
}
