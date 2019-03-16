package imgseg_representation;

import utils.MutableInt;
import utils.MutableObject;
import utils.Utils;

import javax.rmi.CORBA.Util;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SegUtils {

    private static float MAX_COL_DIST = (float)Math.sqrt(3.0);
    private static float MIN_COL_DIST = 0;
    private static GraphSegNode NULL_NODE = new GraphSegNode(-1, -1);


    static class GsegSegment {
        LinkedList<GraphSegNode> nodes = new LinkedList<>();
        GraphSegNode nextSegFromNode;

        GsegSegment prevSeg;
        GsegSegment nextSeg;
        float prevEdgeValue;
        float nextEdgeValue;


        public GsegSegment(GraphSegNode startNode, GsegSegment prevSeg, float prevEdgeValue) {
            this.prevSeg = prevSeg;
            this.nodes.add(startNode);// this.prevEndNode = prevEndNode; this.nextStartNode = nextStartNode;
            this.prevEdgeValue = prevEdgeValue;
        }

        public void setNextSeg(GsegSegment nextSeg, GraphSegNode nextSegFromNode, float nextEdgeValue) {
            this.nextSeg = nextSeg;
            this.nextEdgeValue = nextEdgeValue;
            this.nextSegFromNode = nextSegFromNode;
        }

    }

    static class NodeEdge implements Comparable<NodeEdge> {
        GraphSegNode fromNode;
        GraphSegNode toNode;
        float colDist;

        public NodeEdge(GraphSegNode fromNode, GraphSegNode toNode, Image img) {
            this.fromNode = fromNode;
            this.toNode = toNode;
            if (fromNode == null || toNode == null)
                colDist = Float.MAX_VALUE;
            else
                this.colDist = img.getPixel(fromNode).getPixelDistance(img.getPixel(toNode));
        }

        @Override
        public int compareTo(NodeEdge o) {
            return (int)Math.signum(colDist - o.colDist);
        }
    }

    public static void reduceSegmentsBySize(GraphSeg gseg, Image img, List<GsegSegment> gsegSegments) {
        Comparator<GsegSegment> segSizeComparator = (s1, s2) -> s1.nodes.size() - s2.nodes.size();
        PriorityQueue<GsegSegment> segsSizes = new PriorityQueue<>(gsegSegments.size(), segSizeComparator);

        segsSizes.addAll(gsegSegments);

        //map nodes to segments
        Map<GraphSegNode, GsegSegment> nodeSegment = new HashMap<>();
        gsegSegments.forEach(segment -> segment.nodes.forEach(n -> nodeSegment.put(n, segment)));

        int removeBySizeCount = 20;
        int i = 0;

        while (segsSizes.size() > removeBySizeCount) {
//            System.out.println("Reducing segments by size, current segment count: " + segsSizes.size());
//            System.out.println("Reducing segments by size, current segment count by the overall collection: " + gsegSegments.size());

            if (segsSizes.size() < 5) {
                System.out.println("segments left");
                segsSizes.forEach(s -> System.out.println(s.nodes.size()));

                segsSizes.forEach(seg -> {
                    MutableInt selfpointers = new MutableInt(0);
                    MutableInt pointersOutside = new MutableInt(0);
                    seg.nodes.forEach(n -> {
                        if (n.next == null)
                            selfpointers.value++;
                        else if (!seg.nodes.contains(n))
                            pointersOutside.value++;
                    });
                    System.out.println("For segment with size: " + seg.nodes.size());
                    System.out.println("Self pointers: " + selfpointers.value);
                    System.out.println("Outside seg pointers: " + pointersOutside.value);
                });
            }

            GsegSegment seg = segsSizes.poll();
            //map nodes to neighbours not in the given segment
            NodeEdge bestEdge = seg.nodes.stream()
                    .filter(n -> n.next == null)//chose only nodes that end the segment
                    .flatMap(n -> gseg.getNonDiagonalNeighbours(n).stream()
                            .filter(Objects::nonNull)
                            .filter(nbour -> nodeSegment.get(n) != nodeSegment.get(nbour)) //make sure neighbours are not in the same segment
//                            .filter(nbour -> !seg.nodes.contains(nbour)) //make sure neighbours are not in the same segment
                            .map(nbour -> new NodeEdge(n, nbour, img))
                    )
                    .reduce((e1, e2) -> e1.colDist < e2.colDist? e1 : e2)
                    .orElse(null);

            //if there are no valid nodes that may be connected, move on. A node is valid if it will not break a segmnent
            if (bestEdge == null) {
                System.err.println("Found a segment with no null edge, iteration: " + i);
//                //put back the removed segment
//                segsSizes.add(seg);
                continue;
            }
            i++;

            //remove the previous of the old node that the fromnode is connected to
            if (bestEdge.fromNode.next != null) bestEdge.fromNode.next.previous.remove(bestEdge.fromNode);
            //connect to the new node
            bestEdge.fromNode.next = bestEdge.toNode;
            bestEdge.toNode.previous.add(bestEdge.fromNode);

            //merge segments
            GsegSegment mergeWith = nodeSegment.get(bestEdge.toNode);
            if (mergeWith == seg) {
                System.err.println("best edge lead to same segment");
            }

            mergeWith.nodes.addAll(seg.nodes);

            //reinsert the changed segment
            segsSizes.remove(mergeWith);
            segsSizes.add(mergeWith);

            //update the segment og the merged nodes
            seg.nodes.forEach(n -> nodeSegment.put(n, mergeWith));

            //remove the segment. its now a part of the other segment
            gsegSegments.remove(seg);
        }

        //check if segments are connected
        segsSizes.forEach(segment -> {
            Set<GraphSegNode> segmentNodes = new HashSet<>(segment.nodes);
            if (segment.nodes.size() != segmentNodes.size())
                System.err.println("There are duplicate segment nodes");

            Set<GraphSegNode> conciderNodes = new HashSet<>();
            GraphSegNode firstNode = segmentNodes.iterator().next();
            conciderNodes.add(firstNode);

            while(!conciderNodes.isEmpty()) {
                GraphSegNode currNode = conciderNodes.iterator().next();
                conciderNodes.remove(currNode);
                segmentNodes.remove(currNode);

                Set<GraphSegNode> preceders = new HashSet<>();
                preceders.addAll(currNode.previous);
                if (currNode.next != null) preceders.add(currNode.next);

                preceders.stream()
                        .filter(segmentNodes::contains) //make sure they are left to be removed
                        .forEach(conciderNodes::add);
            }
            System.out.println("Removed nodes path, left: " + segmentNodes.size());
        });

        //draw segments based on the segmentation even thugh the graph seems incorrect
        Segmentation seg = new Segmentation(gseg);
        MutableInt label = new MutableInt(0);
        gsegSegments.forEach(segment -> {
            segment.nodes.forEach(n -> seg.setLabelValue(n.x, n.y, label.value));
            ++label.value;
        });
        IsegImageIO.drawSegmentation(seg);

//        //check if next is neighbour
//        System.out.println("Reduced size segments: " + gsegSegments.size());
//        gsegSegments
//                .forEach(s -> {
//                    s.nodes.forEach(n -> {
//                        List<GraphSegNode> nbours = gseg.getNonDiagonalNeighbours(n).stream()
//                                .filter(Objects::nonNull)
//                                .collect(Collectors.toList());
//                        if (n.next != null) {
//                            if (!nbours.contains(n.next)) {
//                                System.err.println("reduction Next not a nbour!");
//                            }
//                        }
//                    });
//                });
    }

    /**
     * TODO: Does not update the given segments
     */
    public static void reduceSegmentsByColor(List<GsegSegment> gsegSegments) {

//        IsegImageIO.drawSegmentation(getSegRepresentation(gseg));
        class SegEdge implements Comparable<SegEdge>{
            GsegSegment firstSeg, lastSeg;
            float edgeValue;

            public SegEdge(GsegSegment firstSeg, GsegSegment lastSeg, float edgeValue) {
                this.firstSeg = firstSeg; this.lastSeg = lastSeg; this.edgeValue = edgeValue;
            }
            @Override
            public int compareTo(SegEdge o) {
                return (int)Math.signum(edgeValue - o.edgeValue);
            }
        }

        PriorityQueue<SegEdge> segEdges = new PriorityQueue<>();
        IntStream.range(1, gsegSegments.size())
                .forEach(i -> {
                    GsegSegment firstSeg = gsegSegments.get(i-1);
                    GsegSegment lastSeg = gsegSegments.get(i);
                    segEdges.add( new SegEdge(firstSeg, lastSeg, lastSeg.prevEdgeValue) );
                });

        while (segEdges.size() > 1000) {
            SegEdge edge = segEdges.poll();
            //link the first node in the last segment to the last node in the first segment
            edge.lastSeg.nodes.getFirst().next = edge.firstSeg.nodes.getLast();
        }

    }

    public static GraphSeg createMinimalSpanningTreeInSegments(Segmentation seg, Image img) {

        GraphSeg gseg = new GraphSeg(img);
        gseg.streamAll().forEach(n -> n.next = NULL_NODE);

        //map segments to graphseg segments
        List<List<GraphSegNode>> gsegments = seg.getSegmentations().stream()
                .map(lseg -> lseg.stream()
                        .map(l -> gseg.getNode(l))
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());
//        gsegments = new ArrayList<>();
//        gsegments.add(new ArrayList<>(gseg.getAllNodes()));

//        gsegments.forEach(gsegment -> System.out.println(gsegment.size()));
//        seg.getSegmentations().forEach(segment -> System.out.println(segment.size()));
//        System.out.println(gsegments.get(0) == gsegments.get(1));

        int i = 0;
        for (List<GraphSegNode> segment : gsegments) {
            System.out.println("Starting segment " + ++i);
            GraphSegNode startNode = segment.get(0);
//            GraphSegNode startNode = segment.get(Utils.randRange(0, segment.size()));

            PriorityQueue<NodeEdge> concideredNodes = new PriorityQueue<>();
            int nodesAssigend = 0;

            concideredNodes.add(new NodeEdge(null, startNode, img));


            while(nodesAssigend < segment.size()) {
//                System.out.println("concidered nodes left: " + concideredNodes.size());
//                System.out.println("segment size: " + segment.size());
//                System.out.println("nodes assigned: " + nodesAssigend);

                NodeEdge currNodeEdge = concideredNodes.poll();

                //skip if this node is already in the tree
                if (currNodeEdge.toNode.next != NULL_NODE) {
                    if (concideredNodes.size() == 0) {
                        System.err.println("KAAKAKA");
//                        int[] coords = {-10, -9, -8, -7, -6, -5, -4, 4, 5, 6, 7, 8, 9, 10};
//                        for (int x = 0; x < coords.length; x++) {
//                            SegLabel lab = seg.getLabel(currNodeEdge.toNode.x + coords[x], currNodeEdge.toNode.y + coords[x]);
//                            lab.label = 0;
//
//                        }
                        Image im = new Image(img);
                        segment.stream()
                                .forEach(n -> {
                                    SegLabel lab = seg.getLabel(n.x, n.y);
                                    if (n.next != NULL_NODE) {
                                        im.getPixel(lab).b = 1;
                                    }
                                    else {
                                        im.getPixel(lab).g = 1;
                                    }
                                });
                        im.getPixel(currNodeEdge.toNode).r = 1;

                        System.err.println("Did not complete :(");
                        IsegImageIO.drawImage(im);
                        gseg.streamAll()
                                .filter(n -> n.next == NULL_NODE)
                                .forEach(n -> n.next = null);
                        return gseg;
                    }
                    continue;
                }

                if (currNodeEdge.fromNode != null) {
                    //point the end to the start
                    currNodeEdge.toNode.next = currNodeEdge.fromNode;
                    currNodeEdge.fromNode.previous.add(currNodeEdge.toNode);
                } else {
                    currNodeEdge.toNode.next = null;
                }
                ++nodesAssigend;

                //concider neighbours
                gseg.getNonDiagonalNeighbours(currNodeEdge.toNode).stream()
                        .filter(Objects::nonNull)
                        .filter(nbour -> segment.contains(nbour)) //make sure the neighbour belongs to the same segment
                        .filter(nbour -> nbour.next == NULL_NODE) //filter out assigned nodes
                        .forEach(nbour -> concideredNodes.add(new NodeEdge(currNodeEdge.toNode, nbour, img)));


            }
        }

        return gseg;
    }

    public static GraphSeg createMinimalSpanningTree(Image img, List<GsegSegment> getSegments) {
        //a class to represent color distance between two nodes
        class DistToNode implements Comparable<DistToNode> {
            public GraphSegNode visitedNode;
            public GraphSegNode concideredNode;
            public float dist;

            public DistToNode(GraphSegNode visitedNode, GraphSegNode concideredNode) {
                this.dist = img.getPixel(visitedNode).getPixelDistance( img.getPixel(concideredNode) );
                this.visitedNode = visitedNode;
                this.concideredNode = concideredNode;
            }

            public int compareTo(DistToNode o) {
                //if the distance of this is greater than other, it should come last
                return (int)Math.signum(dist - o.dist);
            }
        }


        /*
         * A next reference to NULL_NODE means its not set
         * A next reference to null means its pointing to itself
         */

        final float splitThreshold = 0.05f;
        //final int[] startAt = {Utils.randRange(0, img.getWidth()), Utils.randRange(0, img.getHeight())};
        final int[] startAt = {0, 0};

        GraphSeg gseg = new GraphSeg(img);
        //set all next to null node
        gseg.streamAll().forEach(n -> n.next = NULL_NODE);

        final int nodeCount = gseg.getHeight() * gseg.getWidth();
        final MutableInt visitedNodesCount = new MutableInt(0);

        PriorityQueue<DistToNode> concideredNodes = new PriorityQueue<>(); //nodes to concider dists to nodes not concidered
        final MutableObject<GsegSegment> currSplitSeg = new MutableObject<>();

        //add node function
        BiConsumer<GraphSegNode, GraphSegNode> visitNode = (node, fromNode) -> {
            if (fromNode == null) {
                node.next = null;
                currSplitSeg.obj = new GsegSegment(node, null, Float.MAX_VALUE);
            }
            else {
                float colDist = img.getPixel(node).getPixelDistance(img.getPixel(fromNode));
                if ( colDist < splitThreshold ){
                    //add to current tree
                    node.next = fromNode;
                    fromNode.previous.add(node);

                    currSplitSeg.obj.nodes.add(node);
                }
                else {
                    //TODO: this is not called when the very last node is encountered

                        //new seg start node to point to itself
                    node.next = null;

                    //create the new segment
                    GsegSegment nextSeg = new GsegSegment(node, currSplitSeg.obj, colDist);

                    currSplitSeg.obj.setNextSeg(nextSeg,fromNode, colDist);
                    getSegments.add(currSplitSeg.obj);

                    //set new segent
                    currSplitSeg.obj = nextSeg;
                }
            }

            ++visitedNodesCount.value;


            //put node neighbours as concidered
            gseg.getNonDiagonalNeighbours(node).stream()
                    //filter edge neighbours that are null
                    .filter(Objects::nonNull)
                    //filter out neighbours that have been visited
                    .filter(nbour -> nbour.next == NULL_NODE)
                    .map(nbour -> new DistToNode(node, nbour))
                    .forEach(nodeMapping -> concideredNodes.add(nodeMapping));
        };

        //get a first node as visited, and pint it to itself
        GraphSegNode firstNode = gseg.getNode(startAt[0], startAt[1]);
        //make it point to itself instead of null, so it is concidered visited
        //should later be set to null
        visitNode.accept(firstNode, null);

        while (visitedNodesCount.value < nodeCount) {
            DistToNode visitNodeMapping = concideredNodes.poll();
            GraphSegNode newNode = visitNodeMapping.concideredNode;
            GraphSegNode visitedNode = visitNodeMapping.visitedNode;

            //if the node to add is aready visited, dont concider this edge
            if (newNode.next != NULL_NODE)
                continue;

            visitNode.accept(newNode, visitedNode);
        }

        //add the last segment
        currSplitSeg.obj.setNextSeg(null, null, Float.MAX_VALUE);
        getSegments.add(currSplitSeg.obj);

        //check if every next reference is a neighbour
        gseg.streamAll()
                .forEach(n -> {
                    List<GraphSegNode> nbours = gseg.getNonDiagonalNeighbours(n).stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    if (n.next != null) {
                        if (!nbours.contains(n.next)) {
                            System.err.println("Next not a nbour!");
                        }
                    }
                });

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

        System.out.println("Converted to sgementation representation with segment count: " + nextSegmentLabel);

        return segmentation;
    }

//    public static GraphSeg createMinimalSpanningTree(Image img) {
//        //a class to represent color distance between two nodes
//        class DistToNode implements Comparable<DistToNode> {
//            public GraphSegNode visitedNode;
//            public GraphSegNode concideredNode;
//            public float dist;
//
//            public DistToNode(GraphSegNode visitedNode, GraphSegNode concideredNode) {
//                //set distance to the colordistance of the nodes corresponding pixels
//                //System.out.println(visitedNode + "<visited, concidered>" + concideredNode);
//                this.dist = img.getPixel(visitedNode).getPixelDistance( img.getPixel(concideredNode) );
//                this.visitedNode = visitedNode;
//                this.concideredNode = concideredNode;
//            }
//
//            @Override
//            public int compareTo(DistToNode o) {
//                //if the distance of this is greater than other, it should come last
//                return (int)Math.signum(dist - o.dist);
//            }
//        }
//
//        GraphSeg gseg = new GraphSeg(img);
//
//        final int nodeCount = gseg.getHeight() * gseg.getWidth();
//        final MutableInt visitedNodesCount = new MutableInt(0);
//
//        //Set<GraphSegNode> unvisitedNodes = gseg.getAllNodes();
//        Set<GraphSegNode> visitedNodes = new HashSet<>();
//        PriorityQueue<DistToNode> concideredNodes = new PriorityQueue<>(); //nodes to concider dists to nodes not concidered
//
//        BiConsumer<GraphSegNode, GraphSegNode> visitNode = (node, fromNode) -> {
//            //GraphSegNode node = unvisitedNodes.iterator().next();
//            //unvisitedNodes.remove(node);
//            //visitedNodes.add(node);
//
////            if (img.getPixel(node).getPixelDistance(img.getPixel(fromNode)) < 0.2) {
////                node.next = fromNode;
////                fromNode.previous.add(node);
////            } else {
////                node.next = node;
////            }
//            node.next = fromNode;
//            fromNode.previous.add(node);
//            ++visitedNodesCount.value;
//
//
//            //put node neighbours as concidered
//            gseg.getNonDiagonalNeighbours(node).stream()
//                    //filter edge neighbours that are null
//                    .filter(Objects::nonNull)
//                    //filter neighbours that have been visited
//                    .filter(nbour -> nbour.next == null)
//                    .map(nbour -> new DistToNode(node, nbour))
//                    .forEach(nodeMapping -> concideredNodes.add(nodeMapping));
//        };
//
//        //get a first node as visited, and pint it to itself
//        GraphSegNode firstNode = gseg.getNode(0, 0);
//        //make it point to itself instead of null, so it is concidered visited
//        //should later be set to null
//        visitNode.accept(firstNode, firstNode);
//
//        while (visitedNodesCount.value < nodeCount) {
//            DistToNode visitNodeMapping = concideredNodes.poll();
//            GraphSegNode newNode = visitNodeMapping.concideredNode;
//            GraphSegNode visitedNode = visitNodeMapping.visitedNode;
//
//            //if the node to add is aready visited, dont concider this edge
//            if (newNode.next != null)
//                continue;
//
//            visitNode.accept(newNode, visitedNode);
//        }
//
//        //a node pointing to itself should be null
//        firstNode.next = null;
//
////        //all nodes set to point to themselves should be set to point to null
////        gseg.streamAll().forEach(n -> {
////            if (n.next == n)
////                n.next = null;
////        });
//
//        return gseg;
//    }


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
