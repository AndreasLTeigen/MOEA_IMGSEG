package  solver;

import imgseg_representation.Chromosome;

import java.util.List;

public interface Crossover {
    List<Chromosome> crossover(Chromosome c1, Chromosome c2);
}
