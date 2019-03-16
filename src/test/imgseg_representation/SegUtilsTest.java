package imgseg_representation;

import imgseg_solver.HeuristicPopulationInitializer;
import imgseg_solver.RandomPopulationInitializer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SegUtilsTest {

    private Chromosome createChomosome() {
        Image img = IsegImageIO.loadImage("images/147091/Test image.jpg");
        Problem p = new Problem(img);
        return RandomPopulationInitializer.createRandomChromosome(p);
//        return HeuristicPopulationInitializer.HeuristicInitializer(p, 3, 1000);
    }


    public void testCreateMinimalSpanningTree() {
        Chromosome c = createChomosome();
        IsegImageIO.drawCharomosome(c);
        IsegImageIO.drawSegmentation(c.segmentation);

        List<SegUtils.GsegSegment> segs = new ArrayList<>();
        GraphSeg gseg = SegUtils.createMinimalSpanningTree(c.img, segs);
        System.out.println("converted to graph segmentation");

        IsegImageIO.drawSegmentation(SegUtils.getSegRepresentation(gseg));

        SegUtils.reduceSegmentsBySize(gseg, c.img, segs);

        IsegImageIO.drawSegmentation(SegUtils.getSegRepresentation(gseg));


        new Scanner(System.in).nextLine();
    }

    @Test
    public void testCreateMinimalSpanningTreeInSegments() {
        Chromosome c = createChomosome();
        IsegImageIO.drawCharomosome(c);
        IsegImageIO.drawSegmentation(c.segmentation);

        GraphSeg gseg = SegUtils.createMinimalSpanningTreeInSegments(c.segmentation, c.img);
        System.out.println("Converted segments to graph");

//        System.out.println(gseg);

        Segmentation newSeg = SegUtils.getSegRepresentation(gseg);
        System.out.println("Converted back");
        IsegImageIO.drawSegmentation(newSeg);


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
