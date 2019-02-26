package main.solver;

import main.Population;

public interface ParentSelector {
    Population selectParents(Population population);
}
