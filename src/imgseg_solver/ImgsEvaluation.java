package imgseg_solver;

import main.Chromosome;
import solver.Evaluation;

public class ImgsEvaluation implements Evaluation<Chromosome> {

    private Chromosome chromosome;
    private float eval;


    public ImgsEvaluation(float evaluation, Chromosome chrom) {
        this.eval = evaluation;
        this.chromosome = chrom;
    }

    @Override
    public float getEval() {
        return eval;
    }

    @Override
    public Chromosome getEvaluatedInstance() {
        return chromosome;
    }
}
