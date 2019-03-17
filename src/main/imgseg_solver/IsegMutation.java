package imgseg_solver;

import imgseg_representation.Chromosome;
import imgseg_representation.Population;
import solver.MutatePopulation;
import utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class IsegMutation implements MutatePopulation {
    @Override
    public Population mutatePopulation(Population pop) {
        List<Chromosome> mutatedChroms = pop.chromosones.stream().map(c -> c.clone()).collect(Collectors.toList());

        mutatedChroms.forEach(c -> {
            if (Math.random() < 0.5)
                return;

            //int maxMutationCount = Utils.randRange(0, 1000);
            SimpleMutation.doSimpleMutation(50, c);
            c.computeObjectives();
        });

        return new Population(mutatedChroms);
    }
}
