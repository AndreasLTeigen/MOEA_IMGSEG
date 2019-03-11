package imgseg_solver;

import imgseg_representation.Chromosome;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NsgaParentSelectorTest {

    @Test
    public void crowdinfSortTest() {
        System.out.println("Crowding sort test");

        double[] chromObjectives = {
                0,10,
                1,9,
                2,8,
                2,9,

                10,0,
                9,1,
                8,0,

                3,3
        };

        List<Chromosome> chroms = IntStream.range(0, chromObjectives.length/2)
                .mapToObj(i -> {
                    float obj1 = (float)chromObjectives[2*i];
                    float obj2 = (float)chromObjectives[2*i+1];

                    return new Chromosome(obj1, obj2);
                })
                .collect(Collectors.toList());

        System.out.println("Initial objectives:\n" + getObjectives(chroms));

        List<Chromosome> sortedChroms = NsgaParentSelector.crowdingDistanceSort(chroms);

        //check that chromosomes are copied and not modified
        Assert.assertNotEquals(chroms, sortedChroms);

        System.out.println("Sorted objectives:\n" + getObjectives(sortedChroms));
    }

    private List<List<Float>> getObjectives(List<Chromosome> chroms) {
        return chroms.stream()
                .map(c -> c.objectiveValues)
                .collect(Collectors.toList());
    }
}
