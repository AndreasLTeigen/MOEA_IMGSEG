package  solver;

import imgseg_representation.Chromosome;

public interface Mutation {
    Chromosome mutate(Chromosome c);
}
