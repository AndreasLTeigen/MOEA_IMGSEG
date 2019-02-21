package solver;

import main.Population;

import java.util.List;

public interface Evaluator {
    List<Float> evaluate(Population pop);
}
