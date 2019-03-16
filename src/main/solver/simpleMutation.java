package solver;

import imgseg_representation.Chromosome;
import imgseg_representation.GraphSegNode;

import java.util.List;
import java.util.Random;

public class simpleMutation {

    public static Chromosome mutate(Chromosome chromosome, int x, int y){
        int randomDirection;
        Random randDir = new Random();
        GraphSegNode node;
        List<GraphSegNode> neighbours;

        node = chromosome.graphSeg.getNode(x,y);
        neighbours = chromosome.graphSeg.getNeighbours(x,y);

        randomDirection = randDir.nextInt(5);

        if (node.next != null){
            node.next.previous.remove(node);
        }

        if (randomDirection == 4){
            node.next = null;
        }
        else{
            node.next = neighbours.get(randomDirection);
            node.next.previous.add(node);
        }

        return chromosome;
    }

    public static Chromosome doSimpleMutation(int maxNumberOfMutations, Chromosome chromosome){
        int numMutations, x, y;
        int width = chromosome.img.getWidth(), height = chromosome.img.getHeight();
        GraphSegNode mutationNode;
        Random randomInt = new Random();

        numMutations = randomInt.nextInt(maxNumberOfMutations);

        for(int mutationNr = 0; mutationNr < numMutations; mutationNr++){
            Random randX = new Random();
            Random randY = new Random();
            x = randX.nextInt(width);
            y = randY.nextInt(height);
            chromosome = mutate(chromosome, x, y);
        }

        return chromosome;
    }
}
