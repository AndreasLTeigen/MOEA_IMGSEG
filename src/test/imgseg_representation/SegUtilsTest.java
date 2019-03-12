package imgseg_representation;

import imgseg_solver.HeuristicPopulationInitializer;
import imgseg_solver.RandomPopulationInitializer;
import org.junit.Test;

public class SegUtilsTest {

    @Test
    public void testSegmentationConversion() {

        Image img = IsegImageIO.loadImage("images/86016/Test image.jpg");
        Problem p = new Problem(img);

        IsegImageIO.drawImage(img);

        Chromosome c = RandomPopulationInitializer.createRandomChromosome(p);

        IsegImageIO.drawSegmentation(c.segmentation);

        //convert to graph seg
        GraphSeg graphSeg = SegUtils.getClosestNeighbourGraphRepresentation(c.segmentation, img);

        System.out.println("Converted to graphSeg");

        //convert back
        Segmentation sameSeg = SegUtils.getSegRepresentation(graphSeg);

        IsegImageIO.drawSegmentation(sameSeg);
    }
}
