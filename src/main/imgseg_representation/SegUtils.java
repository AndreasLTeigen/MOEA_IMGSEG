package imgseg_representation;

import utils.MutableInt;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SegUtils {

    public static GraphSeg createMinimalSpanningTree(List<Pixel> ps) {
        return null;
    }
    public static GraphSeg createMinimalSpanningTree(Image img) {
        //a class to represent color distance between two nodes
        class DistToNode implements Comparable<DistToNode> {
            public GraphSegNode visitedNode;
            public GraphSegNode concideredNode;
            public float dist;

            public DistToNode(GraphSegNode visitedNode, GraphSegNode concideredNode) {
                //set distance to the colordistance of the nodes corresponding pixels
                //System.out.println(visitedNode + "<visited, concidered>" + concideredNode);
                this.dist = img.getPixel(visitedNode).getPixelDistance( img.getPixel(concideredNode) );
                this.visitedNode = visitedNode;
                this.concideredNode = concideredNode;
            }

            @Override
            public int compareTo(DistToNode o) {
                //if the distance of this is greater than other, it should come last
                return (int)Math.signum(dist - o.dist);
            }
        }

        GraphSeg gseg = new GraphSeg(img);

        final int nodeCount = gseg.getHeight() * gseg.getWidth();
        final MutableInt visitedNodesCount = new MutableInt(0);

        //Set<GraphSegNode> unvisitedNodes = gseg.getAllNodes();
        Set<GraphSegNode> visitedNodes = new HashSet<>();
        PriorityQueue<DistToNode> concideredNodes = new PriorityQueue<>(); //nodes to concider dists to nodes not concidered

        BiConsumer<GraphSegNode, GraphSegNode> visitNode = (node, fromNode) -> {
            //GraphSegNode node = unvisitedNodes.iterator().next();
            //unvisitedNodes.remove(node);
            //visitedNodes.add(node);

//            if (img.getPixel(node).getPixelDistance(img.getPixel(fromNode)) < 0.2) {
//                node.next = fromNode;
//                fromNode.previous.add(node);
//            } else {
//                node.next = node;
//            }
            node.next = fromNode;
            fromNode.previous.add(node);
            ++visitedNodesCount.value;


            //put node neighbours as concidered
            gseg.getNonDiagonalNeighbours(node).stream()
                    //filter edge neighbours that are null
                    .filter(Objects::nonNull)
                    //filter neighbours that have been visited
                    .filter(nbour -> nbour.next == null)
                    .map(nbour -> new DistToNode(node, nbour))
                    .forEach(nodeMapping -> concideredNodes.add(nodeMapping));
        };

        //get a first node as visited, and pint it to itself
        GraphSegNode firstNode = gseg.getNode(0, 0);
        //make it point to itself instead of null, so it is concidered visited
        //should later be set to null
        visitNode.accept(firstNode, firstNode);

        while (visitedNodesCount.value < nodeCount) {
            DistToNode visitNodeMapping = concideredNodes.poll();
            GraphSegNode newNode = visitNodeMapping.concideredNode;
            GraphSegNode visitedNode = visitNodeMapping.visitedNode;

            //if the node to add is aready visited, dont concider this edge
            if (newNode.next != null)
                continue;

            visitNode.accept(newNode, visitedNode);
        }

        //a node pointing to itself should be null
        firstNode.next = null;

//        //all nodes set to point to themselves should be set to point to null
//        gseg.streamAll().forEach(n -> {
//            if (n.next == n)
//                n.next = null;
//        });

        return gseg;
    }

    public static List<List<Pixel>> getSubimagesBySegmentation(Segmentation seg, Image img) {
        seg.getSegmentations().stream()
                .map(segment ->
                        segment.stream()
                                .map(sl -> img.getPixel(sl))
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());
        return null;
    }

    public static GraphSeg createMinimalSpanningTreeWithinSegments(Segmentation seg, Image img) {
        return null;
    }

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

    public static Segmentation getSegRepresentation(GraphSeg seg) {
        //create a segLabel for each graph node
        List<List<SegLabel>> segmentationLabels = seg.nodes.stream()
                .map(nrow -> nrow.stream()
                        .map(n -> new SegLabel(n.x, n.y))
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());

        //segmentation without valid labels assigned
        Segmentation segmentation = new Segmentation(segmentationLabels);

        Set<GraphSegNode> allNodes = new HashSet<>(seg.getAllNodes());
        Set<GraphSegNode> currSeg = new HashSet<>();
        Set<GraphSegNode> currSegConcider = new HashSet<>();
        int nextSegmentLabel = 0;

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

            //set segemntation labels accoring to the retrieved nodes
            int label = nextSegmentLabel;
            currSeg.forEach(n -> {
                segmentation.getLabel(n.x, n.y).label = label;
            });

            //remove the found nodes
            allNodes.removeAll(currSeg);
            currSeg.clear();
            ++nextSegmentLabel;
        }

        return segmentation;
    }

//    public static Segmentation getSegRepresentation(GraphSeg seg) {
//        List<List<SegLabel>> segmentation = new ArrayList<>();
//
//        Set<GraphSegNode> allNodes = new HashSet<>(seg.getAllNodes());
//        Set<GraphSegNode> currSeg = new HashSet<>();
//        Set<GraphSegNode> currSegConcider = new HashSet<>();
//
//        while(!allNodes.isEmpty()) {
////            System.out.println("All nodes " + allNodes.size());
//            //get a node from the set
//            GraphSegNode node = allNodes.iterator().next();
//
//            //add this node to concider
//            currSegConcider.add(node);
//
//            while (!currSegConcider.isEmpty()) {
////                System.out.println("Concider " + currSegConcider.size());
//                //get a node to concider and remove it
//                GraphSegNode segNode = currSegConcider.iterator().next();
//                currSegConcider.remove(segNode);
//
//                currSeg.add(segNode);
//
//                //add connections to concider
//                //next might be null, it is pointing to itself
//                Set<GraphSegNode> toConcider = new HashSet<>(segNode.previous);
//                if (segNode.next != null) toConcider.add(segNode.next);
//
//                //add if the nodes are not already in the current segment
//                toConcider.forEach(n -> {
//                    if (!currSeg.contains(n)) {
//                        currSegConcider.add(n);
//                    }
//                });
//            }
//
//            //map the currSegs to SegLabels and add them
//            //get the label, given the position in segmentation
//            int label = segmentation.size();
//            List<SegLabel> segLabels = currSeg.stream()
//                    .map(segNode -> new SegLabel(segNode.x, segNode.y, label))
//                    .collect(Collectors.toList());
//            segmentation.add(segLabels);
//
//            //remove the found nodes
//            allNodes.removeAll(currSeg);
//            currSeg.clear();
//        }
//
//        return new Segmentation(segmentation);
//    }
}
