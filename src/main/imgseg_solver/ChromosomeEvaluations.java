package  imgseg_solver;


import imgseg_representation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        int neighbourhoodSize = 1;

        Segmentation seg = chrom.segmentation;

        float connectivity = (float)seg.stream().mapToDouble(lab -> {

            //store all neighbours of the label of depth given
            //use a set, so duplicate neighbours are not concidered
            List<Set<SegLabel>> nLabels = new ArrayList<>();

            //add the current label
            nLabels.add(new HashSet<>());
            nLabels.get(0).add(lab);

            //add all labels of neighbours, recursively until depth is exceeded
            IntStream.range(0, neighbourhoodSize).forEach( nn -> {
                //get neighbours of the outer neighbourhood of depth
                Set<SegLabel> currNeighbourhood = nLabels.get(nLabels.size() -1);
                Set<SegLabel> newNeighbourhood = currNeighbourhood.stream()
                        .flatMap(l -> seg.getNeighbours(l).stream())
                        .filter(Objects::nonNull) //remove labels outside scope
                        .filter(l -> !currNeighbourhood.contains(l)) //remove neighbours in currNeighbourhood
                        .collect(Collectors.toSet());

                //System.out.println(newNeighbourhood);
                //add new neighbourhood
                nLabels.add(newNeighbourhood);
            });

            //get the connectivity of all neighbourhood labels.
            //start at 1 to not get the currLabel
            double segConnectivity = IntStream.range(1, nLabels.size())
                    .mapToDouble(i ->
                        nLabels.get(i).stream()
                                .mapToDouble(l -> labelConnectivity(lab, l, i)) //get the connectivity of each neighbour
                                .sum()
                    )
                    .sum();

            return segConnectivity;

        }).sum(); //sum op connectivitise of all segements

        return connectivity;
    }

    private static float labelConnectivity(SegLabel currLabel, SegLabel nLabel, int neighbourDepth) {
        return (currLabel.label != nLabel.label) ? 0 :
                1.0f / neighbourDepth;
    }

}
