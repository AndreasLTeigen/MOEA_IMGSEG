package imgseg_solver;

import imgseg_representation.Chromosome;
import imgseg_representation.GraphSegNode;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class SimpleMutation {

    private static Chromosome mutate(Chromosome chromosome, int x, int y){
        int randomDirection;
        Random randDir = new Random();
        GraphSegNode node;
        List<GraphSegNode> neighbours;

        node = chromosome.graphSeg.getNode(x,y);

        if (node.next != null) {
            node.next.previous.remove(node);
        }

        randomDirection = randDir.nextInt(5);
        if (randomDirection == 4){
            node.next = null;
        }
        else {
            neighbours = chromosome.graphSeg.getNeighbours(x, y).stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            randomDirection = randDir.nextInt(neighbours.size());

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
