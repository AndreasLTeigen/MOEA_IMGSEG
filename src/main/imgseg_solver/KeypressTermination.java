package imgseg_solver;

import imgseg_representation.Population;
import solver.TerminateCondition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class KeypressTermination implements TerminateCondition {


    private BufferedReader br;
    private int iterations;

    public KeypressTermination(int iterations) {
        this.iterations = iterations;
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public boolean shouldTerminate(int iteration, Population pop) {
        if (iteration > iterations) {
            return true;
        }
        try {
            if (br.ready()) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
