package main.solver;

import main.Population;

import java.util.List;

public interface Evaluator {
    List<Evaluation> evaluate(Population pop);
}
