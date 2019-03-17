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

    public static float overallDeviation(Segmentation seg, Image img) {
        List<List<SegLabel>> segments = seg.getSegmentations();

        float totDist = 0;
        for (List<SegLabel> segment : segments) {

            //find the centroid
            //only by coordinates
            //TODO: centroids should be by color!
//            float cx = (float)segment.stream().mapToDouble(l -> img.getPixel(l).x).sum() / segment.size();
//            float cy = (float)segment.stream().mapToDouble(l -> img.getPixel(l).y).sum() / segment.size();
//            Pixel centroid = img.getPixel((int)cx, (int)cy);
            float cr = (float)segment.stream().mapToDouble(l -> img.getPixel(l).r).sum() / segment.size();
            float cg = (float)segment.stream().mapToDouble(l -> img.getPixel(l).g).sum() / segment.size();
            float cb = (float)segment.stream().mapToDouble(l -> img.getPixel(l).b).sum() / segment.size();
            Pixel centroid = new Pixel(cr, cg, cb, -1, -1);

            float totSegDist = (float)segment.stream()
                    .mapToDouble(l -> colorDist(img.getPixel(l), centroid)).sum();
            //this is apparently not a part of the calculationfloat segDist = totSegDist / seg.size();
            totDist += totSegDist;
        }
        return totDist;
    }



    public static float connectivity(Segmentation seg) {
        int neibourhoodDepth = 1;

        float connectivity = (float)seg.stream().mapToDouble(lab -> {

            //store all neighbours of the label of depth given
            //use a set, so duplicate neighbours are not concidered
            List<Set<SegLabel>> nLabels = new ArrayList<>();

            //add the current label
            nLabels.add(new HashSet<>());
            nLabels.get(0).add(lab);

            //add all labels of neighbours, recursively until depth is exceeded
            IntStream.range(0, neibourhoodDepth).forEach( nn -> {
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

            int nbourCount = nLabels.size();

//            System.err.println("connectivity neighbour count: " + nbourCount);

            //get the connectivity of all neighbourhood labels.
            //start at 1 to not get the currLabel
            double segConnectivity = IntStream.range(1, nLabels.size())
                    .mapToDouble(i ->
                        nLabels.get(i).stream()
                                .mapToDouble(l -> labelConnectivity(lab, l, nbourCount)) //get the connectivity of each neighbour
                                .sum()
                    )
                    .sum();

            return segConnectivity;

        }).sum(); //sum op connectivitise of all segements

        return connectivity;
    }

    private static float labelConnectivity(SegLabel currLabel, SegLabel nLabel, int neighbourCount) {
        return (currLabel.label != nLabel.label)? 1.0f / neighbourCount : 0;
    }

}
