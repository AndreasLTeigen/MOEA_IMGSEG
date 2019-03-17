package imgseg_solver;

import imgseg_representation.Chromosome;
import imgseg_representation.Population;
import solver.CrossoverPopulation;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class IsegCrossover implements CrossoverPopulation {
    @Override
    public Population crossoverPopulation(Population pop) {

        List<Chromosome> parents = new ArrayList<>(pop.chromosones);
        List<Chromosome> childs = new ArrayList<>(pop.chromosones.size());

        while(parents.size() > 0) {

            //choose two parents at random
            Chromosome chrom1 = parents.remove(Utils.randRange(0, parents.size()));
            Chromosome chrom2 = parents.remove(Utils.randRange(0, parents.size()));

            childs.addAll(UniformCrossover.doUniformCrossover(chrom1, chrom2));
        }



        return new Population(childs);
    }
}
