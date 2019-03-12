package imgseg_representation;

import java.util.*;
import java.util.stream.Collectors;

public class SegUtils {

    /**
     * Gets a graph representation of this segmentation, with nodes pointing to their most similar neighbour within the segment
     * @return a GraphSeg representation of this segmentation
     */
    public static GraphSeg getClosestNeighbourGraphRepresentation(Segmentation seg, Image img) {

        List<List<SegLabel>> segments = seg.getSegmentations();

        //init nodes
        List<List<GraphSegNode>> nodes = seg.segmentation.stream()
                .map(segRow -> segRow.stream()
                        .map(seglab -> new GraphSegNode(seglab.x, seglab.y))
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());

        GraphSeg gseg = new GraphSeg(nodes);

        nodes.forEach(row -> {
            row.forEach(n -> {
                SegLabel nodeLabel = seg.getLabel(n.x, n.y);

                SegLabel closestNeighbour = findMostSimilarNeighbourInSameSegment(seg, nodeLabel, img);
                //is null if there was no closest node
                if (closestNeighbour != null) {
                    GraphSegNode closestNode = gseg.getNode(closestNeighbour.x, closestNeighbour.y);

                    //add the closest node reference, and previous nodes in that node
                    n.next = closestNode;
                    closestNode.previous.add(n);
                } else {
                    n.next = null; //point to itself
                }
            });
        });

        return gseg;
    }


    /**
     * Return the most simular neighbour, or null if no such neighbour could be retrieved.
     * That might be due to no neighbour of the same segment
     */
    public static SegLabel findMostSimilarNeighbourInSameSegment(Segmentation seg, SegLabel seglab, Image img) {
        Pixel segPix = img.getPixel(seglab);
        List<SegLabel> neighbours = seg.getNeighbours(seglab);

        SegLabel closestLab = neighbours.stream()
                .filter(Objects::nonNull)
                .filter(nl -> nl.label == seglab.label) //filter out labels not in the given segment
                //reduce to the pixel most similar to segLabe, given its pixels
                .reduce((lastLab, nextLab) -> {
//                    System.out.println(lastLab);
//                    System.out.println(nextLab);
                    Pixel lastP = img.getPixel(lastLab);
                    Pixel nextP = img.getPixel(nextLab);

                    return lastP.getPixelDistance(segPix) <= nextP.getPixelDistance(segPix)
                            ? lastLab : nextLab;
                }).orElse(null);
        return closestLab;
    }

    //TODO: FIX that the segmentation returned is sorted in segments, not xy. But this function is also valuable
    public static Segmentation getSegRepresentation(GraphSeg seg) {
        List<List<SegLabel>> segmentation = new ArrayList<>();

        Set<GraphSegNode> allNodes = new HashSet<>(seg.getAllNodes());
        Set<GraphSegNode> currSeg = new HashSet<>();
        Set<GraphSegNode> currSegConcider = new HashSet<>();

        while(!allNodes.isEmpty()) {
//            System.out.println("All nodes " + allNodes.size());
            //get a node from the set
            GraphSegNode node = allNodes.iterator().next();

            //add this node to concider
            currSegConcider.add(node);

            while (!currSegConcider.isEmpty()) {
//                System.out.println("Concider " + currSegConcider.size());
                //get a node to concider and remove it
                GraphSegNode segNode = currSegConcider.iterator().next();
                currSegConcider.remove(segNode);

                currSeg.add(segNode);

                //add connections to concider
                //next might be null, it is pointing to itself
                Set<GraphSegNode> toConcider = new HashSet<>(segNode.previous);
                if (segNode.next != null) toConcider.add(segNode.next);

                //add if the nodes are not already in the current segment
                toConcider.forEach(n -> {
                    if (!currSeg.contains(n)) {
                        currSegConcider.add(n);
                    }
                });
            }

            //map the currSegs to SegLabels and add them
            //get the label, given the position in segmentation
            int label = segmentation.size();
            List<SegLabel> segLabels = currSeg.stream()
                    .map(segNode -> new SegLabel(segNode.x, segNode.y, label))
                    .collect(Collectors.toList());
            segmentation.add(segLabels);

            //remove the found nodes
            allNodes.removeAll(currSeg);
            currSeg.clear();
        }

        return new Segmentation(segmentation);
    }
}
