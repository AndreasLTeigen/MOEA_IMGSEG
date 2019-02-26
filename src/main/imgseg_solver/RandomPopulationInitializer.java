package  imgseg_solver;

import imgseg_representation.*;
import solver.PopulationInitializer;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RandomPopulationInitializer implements PopulationInitializer {

    public static Chromosome createRandomChromosome(Problem p) {
        Chromosome c = new Chromosome(p.img);
        Segmentation seg = c.segmentation;

        //scramble segmentation
        int nextLabel = 0;

        List<SegLabel> remaining = seg.getLabelsCollection();
        while (remaining.size() > 0) {
            //pick a random label that is left, and remove it from the remaining
            SegLabel currLabel = remaining.get(Utils.randRange(0, remaining.size()));
            remaining.remove(currLabel);

            //with a probability for each neighbour, take its value

            //grow region with the neighbours of the neighbours
            int depth = 15;

            boolean foundLabel = false;
            List<SegLabel> nWLabel = null;
            Set<SegLabel> region = new HashSet<>(2000);
            Set<SegLabel> oldNeighbours = new HashSet<>();
            Set<SegLabel> newNeighbours = new HashSet<>();

            oldNeighbours.add(currLabel);

            for (int i = 0; i < depth; i++) {

                for (SegLabel l : oldNeighbours) {
                    if (l == null) continue;
                    //get neighbours
                    List<SegLabel> nlabels = seg.getNeighbours(l);
                    //put neighbours in th region
                    newNeighbours.addAll(nlabels);
                }
                region.addAll(oldNeighbours);

                //get those with a set label
                nWLabel = newNeighbours.stream().filter(la -> la != null && la.label != -1).collect(Collectors.toList());
                //check if there are any with labels
                if (nWLabel.size() != 0) {
                    //break out, and set the whole region to a random label of those found

                    region.addAll(newNeighbours);
                    foundLabel = true;
                    break;
                }
                oldNeighbours = newNeighbours;
                newNeighbours = new HashSet<>();
            }

            int newLabel;
            if (!foundLabel) {
                //if no neighbour has a label, set a new label, and apply it to all neighbours
                newLabel = nextLabel++;
                //neighbours.stream().filter(l -> l != null).forEach(l -> l.label = currLabel.label);
            } else {
                //get the label from one of the neighbours with a label
                SegLabel getFrom = nWLabel.get( Utils.randRange(0, nWLabel.size()) );
                newLabel = getFrom.label;
            }
            region.stream().filter(l-> l != null && l.label == -1).forEach(l -> l.label = newLabel);
        }
        return c;
    }

    @Override
    public Population initPopulation(Problem p, int populationSize) {
        List<Chromosome> cs = new ArrayList<>(populationSize);
        for (int i = 0; i < populationSize; i++) {
            cs.add(createRandomChromosome(p));
        }
        return new Population(cs);
    }
}
