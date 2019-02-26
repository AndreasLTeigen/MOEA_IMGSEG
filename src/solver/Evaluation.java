package solver;

import main.Chromosome;

public interface Evaluation<T> extends Comparable<Evaluation<T>> {

    float getEval();
    T getEvaluatedInstance();


    @Override
    default int compareTo(Evaluation<T> o) {
        return (int)Math.signum(getEval() - o.getEval());
    }
}
