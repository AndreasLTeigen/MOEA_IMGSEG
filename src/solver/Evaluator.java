package solver;

import main.Population;

import java.util.List;
import java.util.Map;

public interface Evaluator {
    List<Evaluation> evaluate(Population pop);
}
