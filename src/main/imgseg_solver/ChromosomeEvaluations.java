package  imgseg_solver;


import imgseg_representation.Chromosome;
import imgseg_representation.Image;
import imgseg_representation.Pixel;
import imgseg_representation.SegLabel;

import java.util.List;

public class ChromosomeEvaluations {

    /**
     * returns the color distance between two pixels
     */
    private static float colorDist(Pixel p1, Pixel p2) {
        float rDiff = p2.r - p1.r;
        float gDiff = p2.g - p1.g;
        float bDiff = p2.b - p1.b;
        return (float)Math.sqrt( rDiff*rDiff + gDiff*gDiff + bDiff*bDiff );
    }

    public static float overallDeviation(Chromosome chrom) {
        Image img = chrom.img;
        List<List<SegLabel>> segments = chrom.segmentation.getSegmentations();

        float totDist = 0;
        for (List<SegLabel> seg : segments) {

            //find the centroid
            //only by coordinates
            float cx = (float)seg.stream().mapToDouble(l -> chrom.img.getPixel(l).x).sum() / seg.size();
            float cy = (float)seg.stream().mapToDouble(l -> chrom.img.getPixel(l).y).sum() / seg.size();
            Pixel centroid = chrom.img.getPixel((int)cx, (int)cy);

            float totSegDist = (float)seg.stream()
                    .mapToDouble(l -> colorDist(img.getPixel(l), centroid)).sum();
            //this is apparently not a part of the calculationfloat segDist = totSegDist / seg.size();
            totDist += totSegDist;
        }
        return totDist;
    }
    public static float connectivity(Chromosome chrom) {
        return 0;
    }

}
