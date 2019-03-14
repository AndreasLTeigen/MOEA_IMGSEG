package imgseg_representation;

import imgseg_solver.HeuristicPopulationInitializer;
import imgseg_solver.RandomPopulationInitializer;
import org.junit.Test;

import java.util.Scanner;

public class SegUtilsTest {

    private Chromosome createChomosome() {
        Image img = IsegImageIO.loadImage("images/86016/Test image.jpg");
        Problem p = new Problem(img);
        return HeuristicPopulationInitializer.HeuristicInitializer(p, 3, 1000);
    }

    @Test
    public void testCreateMinimalSpanningTree() {
        Chromosome c = createChomosome();
        IsegImageIO.drawCharomosome(c);
        IsegImageIO.drawSegmentation(c.segmentation);

        GraphSeg gseg = SegUtils.createMinimalSpanningTree(c.img);
        System.out.println("converted to graph segmentation");

        System.out.println(gseg);

        Segmentation seg = SegUtils.getSegRepresentation(gseg);
        System.out.println("Converted back to segmentation");

        IsegImageIO.drawSegmentation(seg);

        new Scanner(System.in).nextLine();
    }

    public void testSegmentationConversion() {

        Image img = IsegImageIO.loadImage("images/86016/Test image.jpg");
        Problem p = new Problem(img);

        IsegImageIO.drawImage(img);

        Chromosome c = HeuristicPopulationInitializer.HeuristicInitializer(p, 3, 1000);

        IsegImageIO.drawSegmentation(c.segmentation);

        //convert to graph seg
        GraphSeg graphSeg = SegUtils.getClosestNeighbourGraphRepresentation(c.segmentation, img);

        System.out.println(graphSeg);

        System.out.println("Converted to graphSeg");

        //convert back
        Segmentation sameSeg = SegUtils.getSegRepresentation(graphSeg);
        System.out.println("Converted to segmentation");

        IsegImageIO.drawSegmentation(sameSeg);

        //wait fo rinput to close
        new Scanner(System.in).nextLine();
    }
}
