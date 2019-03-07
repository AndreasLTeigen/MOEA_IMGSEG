package imgseg_solver;

import imgseg_representation.*;
import solver.PopulationInitializer;

import java.util.*;

public class HeuristicPopulationInitializer implements PopulationInitializer {

    public static Chromosome HeuristicInitializerOld1(Problem p, float distanceThreshold){
        /*

        Initialization assigns segmentation to a pixel equal to the the most similar of the neighbour pixels
        if the neighbour pixels has an assigned segmentation. If no neighbour pixel surpasses the threshold
        a brand new segmentation label is assigned to the pixel

         */
        Image img = p.img;
        Chromosome chromosome = new Chromosome(p.img);

        int segmentationCount;
        double pixelDistance, closestPixelDistance;
        List<SegLabel> neighbours;
        SegLabel neighbour, mostSimilarNeighbour;

        //chromosome.segmentation.setLabelValue(0,0,0);
        segmentationCount = 0;

        for (int y = 0; y < chromosome.segmentation.getHeight(); y++){
            for (int x = 0; x < chromosome.segmentation.getWidth(); x++){
                mostSimilarNeighbour = null;
                closestPixelDistance = Math.sqrt(3);
                neighbours = chromosome.segmentation.getNeighbours(x, y);
                for(int neighbourNr = 0; neighbourNr < neighbours.size(); neighbourNr++){
                    neighbour = neighbours.get(neighbourNr);
                    if (neighbour != null && neighbour.label != -1){
                        pixelDistance = img.getPixel(x,y).getPixelDistance(img.getPixel(neighbour));
                        if (pixelDistance < closestPixelDistance && pixelDistance < distanceThreshold){
                            closestPixelDistance = pixelDistance;
                            mostSimilarNeighbour = neighbour;
                        }
                    }
                }
                if (mostSimilarNeighbour != null){
                    chromosome.segmentation.setLabelValue(x, y, mostSimilarNeighbour.label);
                }
                else {
                    chromosome.segmentation.setLabelValue(x, y, segmentationCount);
                    segmentationCount += 1;
                }
            }
        }
        return chromosome;
    }

    public static Chromosome HeuristicInitializerOld2(Chromosome chromosome, Image img, double distanceThreshold){
        /*

        Initialization assigns segmentation to a pixel equal to the the most similar of the neighbour pixels
        if the neighbour pixels has an assigned segmentation. If no neighbour pixel surpasses the threshold
        a brand new segmentation label is assigned to the pixel

         */

        int segmentationCount;
        double pixelDistance, closestPixelDistance;
        SegLabel neighbour, mostSimilarNeighbour;

        chromosome.segmentation.setLabelValue(0,0,0);
        segmentationCount = 1;

        for (int x = 1; x <chromosome.segmentation.getWidth(); x++){
            pixelDistance = img.getPixel(x, 0).getPixelDistance(img.getPixel(x-1, 0));
            if(pixelDistance > distanceThreshold){
                segmentationCount += 1;
            }
            chromosome.segmentation.setLabelValue(x,0, segmentationCount);
        }

        for (int y = 1; y < chromosome.segmentation.getHeight(); y++){
            for (int x = 0; x < chromosome.segmentation.getWidth(); x++){
                mostSimilarNeighbour = null;
                closestPixelDistance = Math.sqrt(3);
                for(int neighbourNr = 0; neighbourNr < chromosome.segmentation.getWidth(); neighbourNr++){
                    neighbour = chromosome.segmentation.getLabel(neighbourNr,y-1);
                    if (neighbour != null && neighbour.label != -1){
                        pixelDistance = img.getPixel(x,y).getPixelDistance(img.getPixel(neighbour));
                        if (pixelDistance < closestPixelDistance && pixelDistance < distanceThreshold){
                            closestPixelDistance = pixelDistance;
                            mostSimilarNeighbour = neighbour;
                        }
                    }
                }
                if (mostSimilarNeighbour != null){
                    chromosome.segmentation.setLabelValue(x, y, mostSimilarNeighbour.label);
                }
                else {
                    chromosome.segmentation.setLabelValue(x, y, segmentationCount);
                    segmentationCount += 1;
                }
            }
        }
        return chromosome;
    }

    /**
     * Place every pixel in a bin based on their intensity
     */
    public static Chromosome HeuristicInitializer(Problem p, int numBins, int smallesSegmentSize) {

        Image img = p.img;
        Chromosome chromosome = new Chromosome(p.img);

        int binNr;
        double pixelIntensity;
        Pixel pixel;
        for (int y = 0; y < chromosome.segmentation.getHeight(); y++) {
            for (int x = 0; x < chromosome.segmentation.getWidth(); x++) {
                pixel = img.getPixel(x, y);
                pixelIntensity = pixel.getPixelIntensity();
                if (pixelIntensity == Math.sqrt(3)) {
                    binNr = numBins - 1;
                } else {
                    binNr = (int) (pixelIntensity / (Math.sqrt(3) / numBins));
                }
                chromosome.segmentation.setLabelValue(x, y, binNr);
            }
        }

        /*

        Place every pixel in the bin that is used by most of their neighbours

         */

        //SegLabel neighbour;
        int largestBin;
        List<Integer> neighbourBinCount = new ArrayList<>();
        List<SegLabel> neighbours;

        //Fill array to right size
        for (int i = 0; i < numBins; i++) {
            neighbourBinCount.add(0);
        }

        Chromosome tempChromosome = new Chromosome(img);
        for (int y = 0; y < chromosome.segmentation.getHeight(); y++) {
            for (int x = 0; x < chromosome.segmentation.getWidth(); x++) {

                //Empty neighbourBinCount list
                for (int i = 0; i < numBins; i++) {
                    neighbourBinCount.set(i, 0);
                }

                neighbours = chromosome.segmentation.getNeighbours(x, y);
                largestBin = 0;
                for (SegLabel neighbour : neighbours) {
                    if (neighbour != null) {
                        neighbourBinCount.set(neighbour.label, neighbourBinCount.get(neighbour.label) + 1);
                    }
                }
                for (int i = 0; i < numBins; i++) {
                    if (neighbourBinCount.get(i) > neighbourBinCount.get(largestBin)) {
                        largestBin = i;
                    }
                }
                tempChromosome.segmentation.setLabelValue(x, y, largestBin);
            }
        }

        //System.out.println(tempChromosome.segmentation.toString());

        //ImageUtils.drawSegmentation(tempChromosome.segmentation);

        /*

        Seperate all color patches into its own segment

         */
        chromosome = new Chromosome(img);
        int currentLabel;
        SegLabel currentSegLabel;
        List<Integer> segmentCounter = new ArrayList<>();
        for (int y = 0; y < chromosome.segmentation.getHeight(); y++){
            for (int x = 0; x < chromosome.segmentation.getWidth(); x++){
                if (chromosome.segmentation.getLabelValue(x,y) == -1) {
                    segmentCounter.add(1);
                    currentLabel = segmentCounter.size() - 1;
                    chromosome.segmentation.setLabelValue(x,y, currentLabel);
                    currentSegLabel = tempChromosome.segmentation.getLabel(x, y);
                    segmentCounter = markNeighboursInSegmentIterative(chromosome.segmentation, tempChromosome.segmentation, currentSegLabel, currentLabel, segmentCounter);
                }
            }
        }
        //chromosome.segmentation = combineSegmentsLargerThanReccursonDepth(tempChromosome.segmentation, chromosome.segmentation);
        chromosome.segmentation = deleteSegmentsSmallerThan(smallesSegmentSize, chromosome.segmentation);
        chromosome.segmentation = reIndexSegments(chromosome.segmentation);
        //System.out.println(chromosome.segmentation.toString());
        //deleteSegmentsSmallerThan(smallesSegmentSize, chromosome.segmentation, segmentCounter);
        //deleteSegmentsSmallerThan(smallesSegmentSize, chromosome.segmentation, segmentCounter);

        //System.out.println(segmentCounter.toString());
        //System.out.println(chromosome.segmentation.toString());
        return chromosome;
    }

    public static List<Integer> markNeighboursInSegmentIterative(Segmentation segmentation, Segmentation binSegmentation, SegLabel segLabel, int currentLabel, List<Integer> segmentCounter){
        SegLabel currentSegLabel;
        List<SegLabel> neighbours;
        List<SegLabel> segment = new ArrayList<>();

        segment.add(segLabel);

        for (int i = 0; i < segment.size(); i++){
            currentSegLabel = segment.get(i);
            neighbours = binSegmentation.getNeighbours(currentSegLabel.x, currentSegLabel.y);
            for(SegLabel neighbour: neighbours){
                if(neighbour != null && segmentation.getLabelValue(neighbour.x, neighbour.y) == -1 && neighbour.label == segLabel.label){
                    segment.add(neighbour);
                    segmentation.setLabelValue(neighbour.x, neighbour.y, currentLabel);
                    segmentCounter.set(currentLabel, segmentCounter.get( currentLabel ) + 1);
                }
            }
        }

        return segmentCounter;
    }

    public static List<Integer> markNeighboursInSegment(Segmentation segmentation, Segmentation binSegmentation, SegLabel segLabel, int currentLabel, List<Integer> segmentCounter){
        SegLabel newSegLabel;
        List<SegLabel> neighbours;

        neighbours = binSegmentation.getNeighbours(segLabel.x, segLabel.y);

        for (SegLabel neighbour: neighbours){
            if (neighbour != null && neighbour.label == segLabel.label && segmentation.getLabelValue(neighbour.x, neighbour.y) == -1 && segmentCounter.get(currentLabel) < 5200){
                segmentation.setLabelValue(neighbour.x, neighbour.y, currentLabel);
                segmentCounter.set( currentLabel, segmentCounter.get( currentLabel ) + 1 );

                //System.out.println(segmentCounter.get(currentLabel));

                newSegLabel = binSegmentation.getLabel(neighbour.x,neighbour.y);
                segmentCounter = markNeighboursInSegment(segmentation, binSegmentation, newSegLabel, currentLabel, segmentCounter);
            }
        }

        return segmentCounter;
    }

    public static Segmentation reIndexSegments(Segmentation segmentation){
        boolean segmentInList;
        List<Integer> segmentList = new ArrayList<>();

        /*
        Make a list of segmentationNumbers
        */

        segmentList.add(segmentation.getLabelValue(0,0));
        for (int y = 0; y < segmentation.getHeight(); y++){
            for (int x = 0; x < segmentation.getWidth(); x++){
                segmentInList = false;
                for (Integer segmentNr: segmentList){
                    if (segmentation.getLabelValue(x,y) == segmentNr){
                        segmentInList = true;
                    }
                }
                if (!segmentInList){
                    segmentList.add(segmentation.getLabelValue(x,y));
                }
            }
        }

        for (int y = 0; y < segmentation.getHeight(); y++) {
            for (int x = 0; x < segmentation.getWidth(); x++) {
                for (int newSegmentLabel = 0; newSegmentLabel < segmentList.size(); newSegmentLabel++){
                    if (segmentation.getLabelValue(x,y) == segmentList.get(newSegmentLabel)){
                        segmentation.setLabelValue(x,y,newSegmentLabel);
                    }
                }
            }
        }

        return segmentation;
    }

    public static Segmentation deleteSegmentsSmallerThanOld(int size, Segmentation segmentation, List<Integer> segmentCounter){
        //Problems with the first pixel, changing it to the first segment that is large enough
        for (int i = 0; i < segmentCounter.size(); i++){
            if (segmentCounter.get(i) > size){
                segmentation.setLabelValue(0,0, i);
                break;
            }
        }

        int newlabel;
        List<SegLabel> neighbours;
        List<Integer> neighbourLabels;
        for (int y = 0; y < segmentation.getHeight(); y++){
            for (int x = 0; x < segmentation.getWidth(); x++){
                if(segmentCounter.get(segmentation.getLabelValue(x,y)) < size){
                    neighbours = segmentation.getNeighbours(x,y);
                    neighbourLabels = new ArrayList<>();
                    for (SegLabel neighbour: neighbours){
                        if (neighbour != null) {
                            neighbourLabels.add(neighbour.label);
                        }
                    }
                    newlabel = getMostFrequentValidLabelIn(neighbourLabels,segmentCounter, size);
                    segmentation.setLabelValue(x,y,newlabel);
                }
            }
        }
        return segmentation;
    }

    public static Segmentation deleteSegmentsSmallerThan(int size, Segmentation segmentation){
        /*

        Find out how large each segment is

         */

        int segmentNr;
        int numSegments = getLargestSegmentValue(segmentation);

        List<Integer> segmentCounter = new ArrayList<Integer>(Collections.nCopies(numSegments+1, 0));
        for (int y = 0; y < segmentation.getHeight(); y++) {
            for (int x = 0; x < segmentation.getWidth(); x++) {
                segmentNr = segmentation.getLabelValue(x,y);
                segmentCounter.set(segmentNr, segmentCounter.get(segmentNr)+1);
            }
        }

        /*

        Deleting segments smaller than the threshold value

         */

        //Setting segmentation(0,0).label = to belonging to the segment with the lowest index that is larger than size
        /*for (int i = 0; i < segmentCounter.size(); i++){
            if (segmentCounter.get(i) > size){
                segmentation.setLabelValue(0,0,i);
                break;
            }
        }*/

        int x_startingValue = 0, y_startingValue = 0;
        for (int y = 0; y < segmentation.getHeight(); y++) {
            for (int x = 0; x < segmentation.getWidth(); x++){
                segmentNr = segmentation.getLabelValue(x,y);
                if (segmentCounter.get(segmentNr) > size){
                    x_startingValue = x;
                    y_startingValue = y;
                }
            }
        }

        int newlabel;
        List<Integer> neighbourLabels;
        List<SegLabel> neighbours;
        int x = x_startingValue, y = y_startingValue;
        for (; y < segmentation.getHeight(); y++) {
            for (;x < segmentation.getWidth(); x++) {
                segmentNr = segmentation.getLabelValue(x,y);
                if (segmentCounter.get(segmentNr) < size){
                    neighbours = segmentation.getNeighbours(x,y);
                    neighbourLabels = new ArrayList<>();
                    //System.out.print("Labels: ");
                    for (SegLabel neighbour: neighbours){
                        if (neighbour != null){
                            //System.out.print(neighbour.label + "  ");
                            neighbourLabels.add(neighbour.label);
                        }
                    }
                    newlabel = getMostFrequentValidLabelIn(neighbourLabels,segmentCounter, size);
                    //System.out.println("Newlabel: " + newlabel);
                    segmentation.setLabelValue(x,y,newlabel);
                }
            }
            x=0;
        }

        x = x_startingValue;
        y = y_startingValue;
        for (; y >= 0; y--) {
            for (;x >= 0; x--) {
                segmentNr = segmentation.getLabelValue(x,y);
                if (segmentCounter.get(segmentNr) < size){
                    neighbours = segmentation.getNeighbours(x,y);
                    neighbourLabels = new ArrayList<>();
                    //System.out.print("Labels: ");
                    for (SegLabel neighbour: neighbours){
                        if (neighbour != null){
                            //System.out.print(neighbour.label + "  ");
                            neighbourLabels.add(neighbour.label);
                        }
                    }
                    newlabel = getMostFrequentValidLabelIn(neighbourLabels,segmentCounter, size);
                    //System.out.println("Newlabel: " + newlabel);
                    segmentation.setLabelValue(x,y,newlabel);
                }
            }
            x=segmentation.getWidth()-1;
        }


        return segmentation;
    }

    public static int getLargestSegmentValue(Segmentation segmentation){
        int numSegments = 0;
        for (int y = 0; y < segmentation.getHeight(); y++) {
            for (int x = 0; x < segmentation.getWidth(); x++) {
                if (segmentation.getLabelValue(x,y) > numSegments){
                    numSegments = segmentation.getLabelValue(x,y);
                }
            }
        }
        return numSegments;
    }

    public static int getMostFrequentValidLabelIn(List<Integer> list, List<Integer> segmentCounter, int size){
        int mostFreqLabel, highestFreq, frequenzy;
        mostFreqLabel = -1;
        highestFreq = 0;
        for (Integer label: list) {
            if (segmentCounter.get(label) > size){
                frequenzy = Collections.frequency(list, label);
                //System.out.println("Label: " + label + "  Frequncy: " + frequenzy);
                if (frequenzy > highestFreq){
                    mostFreqLabel = label;
                    highestFreq = frequenzy;
                }
            }
        }
        return mostFreqLabel;
    }

    public static Segmentation combineSegmentsLargerThanReccursonDepth(Segmentation binSegmentation, Segmentation segmentation){
        for(int y = 0; y < segmentation.getHeight()-1; y++){
            for (int x = 0; x < segmentation.getWidth()-1; x++){
                if(binSegmentation.getLabelValue(x,y) == binSegmentation.getLabelValue(x+1,y) && segmentation.getLabelValue(x,y) != segmentation.getLabelValue(x+1,y)){
                    segmentation = updateSegmentValues(segmentation, segmentation.getLabelValue(x+1,y), segmentation.getLabelValue(x,y));
                }
                else if (binSegmentation.getLabelValue(x,y) == binSegmentation.getLabelValue(x,y+1) && segmentation.getLabelValue(x,y) != segmentation.getLabelValue(x,y+1)){
                    segmentation = updateSegmentValues(segmentation, segmentation.getLabelValue(x,y+1), segmentation.getLabelValue(x,y));
                }
                else if (binSegmentation.getLabelValue(x,y) == binSegmentation.getLabelValue(x+1,y+1) && segmentation.getLabelValue(x,y) != segmentation.getLabelValue(x+1,y+1)){
                    segmentation = updateSegmentValues(segmentation, segmentation.getLabelValue(x+1,y+1), segmentation.getLabelValue(x,y));
                }
            }
        }

        return segmentation;
    }

    public static Segmentation updateSegmentValues(Segmentation segmentation, int oldSegmentValue, int newSegmentValue){
        //ImageUtils.drawSegmentation(segmentation);
        for(int y = 0; y < segmentation.getHeight(); y++) {
            for (int x = 0; x < segmentation.getWidth(); x++) {
                if (segmentation.getLabelValue(x,y) == oldSegmentValue){
                    segmentation.setLabelValue(x,y, newSegmentValue);
                }
            }
        }
        return segmentation;
    }

    @Override
    public Population initPopulation(Problem p, int populationSize) {
        return null;
    }
}
