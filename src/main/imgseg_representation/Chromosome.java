package imgseg_representation;

import com.sun.javafx.UnmodifiableArrayList;
import imgseg_solver.ChromosomeEvaluations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chromosome {
    public Segmentation segmentation;
    public Image img;

    public int overallDeviationIndex = 0, connectivityIndex = 1;

    public final List<Float> objectiveValues;

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
        this.segmentation = new Segmentation(img);
        this.img = img;

        Float[] objectiveValuesList = {
                ChromosomeEvaluations.overallDeviation(this),
                ChromosomeEvaluations.connectivity(this)
        };

        objectiveValues = new UnmodifiableArrayList<>(objectiveValuesList, objectiveValuesList.length);
    }


}

