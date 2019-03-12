package imgseg_representation;

import com.sun.javafx.UnmodifiableArrayList;
import imgseg_solver.ChromosomeEvaluations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chromosome {
    public GraphSeg graphSeg;
    public Segmentation segmentation;  //TODO remove this and fix all other code that relies on this
    public Image img;

    public static final int overallDeviationIndex = 0, connectivityIndex = 1;

    public List<Float> objectiveValues;

    /**
     * Fo testing puposes
     */
    public Chromosome(Float... objectiveVals) {
        objectiveValues = new UnmodifiableArrayList<>(objectiveVals, objectiveVals.length);
    }
    /**
     * Create an empty chromosome with the size of the given image
     * @param img
     */
    public Chromosome(Image img){
        //TODO initialize graphSeg
        this.segmentation = new Segmentation(img);
        this.img = img;
    }

    public void computeObjectives() {
        Float[] objectiveValuesList = {
                ChromosomeEvaluations.overallDeviation(this),
                ChromosomeEvaluations.connectivity(this)
        };

        objectiveValues = new UnmodifiableArrayList<>(objectiveValuesList, objectiveValuesList.length);
    }


}

