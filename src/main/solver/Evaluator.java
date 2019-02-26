package  solver;



import imgseg_representation.Population;

import java.util.List;

public interface Evaluator {
    List<Evaluation> evaluate(Population pop);
}
