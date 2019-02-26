package  solver;

import main.Chromosome;

public interface Mutation {
    Chromosome mutate(Chromosome c);
}
