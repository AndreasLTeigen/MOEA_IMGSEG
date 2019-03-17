package imgseg_solver;

import imgseg_representation.Chromosome;
import imgseg_representation.GraphSegNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UniformCrossover {

    public static List<Chromosome> doUniformCrossover(Chromosome parent1, Chromosome parent2){

        GraphSegNode node1, node2;
        List<GraphSegNode> neighbours1, neighbours2;

        int nodeDirection1, nodeDirection2;

        List<Chromosome> children = new ArrayList<>();
        children.add(parent1.clone());
        children.add(parent2.clone());

        for (int y = 0; y < children.get(0).img.getHeight(); y++){

            for (int x = 0; x < children.get(0).img.getWidth(); x++){

                Random randomFloat = new Random();
                if(randomFloat.nextFloat() < 0.5 ){

                    node1 = children.get(0).graphSeg.getNode(x,y);
                    neighbours1 = children.get(0).graphSeg.getNeighbours(x,y);
                    nodeDirection1 = children.get(0).graphSeg.getNodeDirection(node1);
                    node2 = children.get(1).graphSeg.getNode(x,y);
                    neighbours2 = children.get(1).graphSeg.getNeighbours(x,y);
                    nodeDirection2 = children.get(1).graphSeg.getNodeDirection(node2);

                    //remove the earlier connected nodes previous
                    if (node1.next != null && node2.next != null){
                        node2.next.previous.remove(node2);
                    }
                    if (node2.next != null && node1.next != null){
                        node1.next.previous.remove(node1);
                    }

                    if (nodeDirection1 == -1){
                        node2.next = null;
                    }
                    else{
                        node2.next = neighbours2.get(nodeDirection1);
                        node2.next.previous.add(node2);
                    }

                    if (nodeDirection2 == -1){
                        node1.next = null;
                    }
                    else{
                        node1.next = neighbours1.get(nodeDirection2);
                        node1.next.previous.add(node1);
                    }


                }
            }
        }

        children.forEach(c -> c.computeObjectives());
        return children;
    }
}
