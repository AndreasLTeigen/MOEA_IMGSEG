package imgseg_representation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class IsegImageIO {

    public static final float rgbMaxVal = 255;
    private static Color[] ColorArray = {Color.black, Color.blue, Color.cyan, Color.darkGray, Color.gray, Color.green, Color.lightGray, Color.magenta, Color.orange, Color.pink, Color.red, Color.white, Color.yellow};


    public static Image loadImage(String filename) {
        BufferedImage buffImg = null;

        try {
            buffImg = ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.err.println("could not load image: "+filename);
            return null;
        }

        Image img = convertToImage(buffImg);
        return img;
    }

    public static void saveSegmentation(Chromosome chrom) {
        Segmentation seg = SegUtils.getSegRepresentation(chrom.graphSeg);
        Image img = createImageOfSegmentationType2(seg);
        Image img2 = createSegmentedImage(chrom.img, seg);

        String filename = "overalldev_" + chrom.objectiveValues.get(Chromosome.overallDeviationIndex)
                +"_connect_" + chrom.objectiveValues.get(Chromosome.connectivityIndex)
                +".png";
        saveImage(img2, "type1_" + filename);
        saveImage(img, "type2_" + filename);
    }

    public static void drawCharomosome(Chromosome c) {
        drawSegmentedImage(c.img, SegUtils.getSegRepresentation(c.graphSeg));
    }

    public static void drawSegmentedImage(Image img, Segmentation seg) {
        Image segImg = createSegmentedImage(img, seg);
        drawImage(segImg);
    }

    public static void drawGraphSeg(GraphSeg gseg) {
        drawSegmentation(SegUtils.getSegRepresentation(gseg));
    }
    public static void drawSegmentation(Segmentation seg) {
        Image img = createImageOfSegmentation(seg);
        drawImage(img);
    }

    public static void drawImage(Image img) {
        BufferedImage buffImg = convertToBufferedImage(img);
        drawBufferedImage(buffImg);
    }


    private void drawBuffimgOnAnother(BufferedImage base, BufferedImage another) {
        Graphics2D g = (Graphics2D) base.getGraphics();
        g.drawImage(another, 0, 0, null);
    }

    private static void saveImage(Image img, String filename) {
        BufferedImage buffImg = convertToBufferedImage(img);
        try {
            ImageIO.write(buffImg, "png", new File("./outImages/" + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Merge the segments and image
     */
    private static Image createSegmentedImage(Image img, Segmentation seg) {
        float[] color = {0, 1, 0};
        Image segimg = img.clone();
        drawBorder(segimg, color);

        //look at four and four pixels, and make them black if there are different segments within
        for (int y = 0; y < img.getHeight() -1; y++) {
            for (int x = 0; x < img.getWidth() -1; x++) {
                SegLabel currLabel = seg.getLabel(x, y);

                //check if there are different labels to the right or down
                Set<Integer> labelsAround = new HashSet<>();
                labelsAround.add(currLabel.label);
                labelsAround.add(seg.getLabelValue(x+1, y));
                labelsAround.add(seg.getLabelValue(x, y+1));

                if (labelsAround.size() > 1) {
                    Pixel p = segimg.getPixel(x, y);
                    p.setColor(color);
                }
            }
        }
        return segimg;
    }

    private static void drawBorder(Image img, float[] color) {
        img.getPixels().get(0).forEach(p -> p.setColor(color));
        img.getPixels().get(img.getPixels().size()-1).forEach(p -> p.setColor(color));

        img.getPixels().forEach(prow -> {
            prow.get(0).setColor(color);
            prow.get(prow.size()-1).setColor(color);
        });
    }

    private static Image convertToImage(BufferedImage buffImg) {

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
                        x, y);
                row.add(p);
            }
            pixels.add(row);
        }

        return new Image(pixels);
    }

    private static Image createImageOfSegmentationType2(Segmentation seg) {
        float[] color = {0, 0, 0};
        Image img = new Image(seg);
        //set all pixels white
        img.streamAll().forEach(p -> p.r = p.b = p.g = 1);
        drawBorder(img, color);

        IntStream.range(0, img.getWidth() -1)
                .forEach(i ->
                        IntStream.range(0, img.getHeight()-1)
                        .forEach(j -> {
                            SegLabel currLabel = seg.getLabel(i, j);

                            //check if there are different labels to the right or down
                            Set<Integer> labelsAround = new HashSet<>();
                            labelsAround.add(currLabel.label);
                            labelsAround.add(seg.getLabelValue(i+1, j));
                            labelsAround.add(seg.getLabelValue(i, j+1));

                            if (labelsAround.size() != 1) {
                                Pixel p = img.getPixel(currLabel);
                                p.setColor(color);
                            }
                        })
                );
        return img;
    }

    private static Image createImageOfSegmentation(Segmentation seg) {
        List<List<Pixel>> pixels = new ArrayList<>(seg.getHeight());
        for (int y = 0; y < seg.getHeight(); y++) {
            List<Pixel> row = new ArrayList<>(seg.getWidth());

            for (int x = 0; x < seg.getWidth(); x++) {
                SegLabel sl = seg.getLabel(x, y);
                int l = sl.label;

                int colorInd = l % ColorArray.length;
                Color c = ColorArray[colorInd];
                //ColorArray[colorInd] = ColorArray[colorInd].brighter();

                float r = c.getRed() / rgbMaxVal;
                float g = c.getGreen() / rgbMaxVal;
                float b = c.getBlue() / rgbMaxVal;
                Pixel p = new Pixel(r, g, b, y, x);
                row.add(p);
            }
            pixels.add(row);
        }
        return new Image(pixels);
    }

    private static BufferedImage convertToBufferedImage(Image img) {
        BufferedImage oimg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                setPixel(oimg, x, y, img.getPixel(x, y));
            }
        }
        return oimg;
    }

    private static void drawBufferedImage(BufferedImage buffImg) {
        JFrame frame = new JFrame("image");
        frame.setVisible(true);

        frame.add(new JLabel(new ImageIcon(buffImg)));

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
