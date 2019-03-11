package imgseg_solver;

import imgseg_representation.Chromosome;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NsgaParentSelectorTest {

    public void crowdinfSortTest() {
        List<Chromosome> chroms = IntStream.range(0, 3)
                .mapToObj(i -> {
                    float obj1Val = i;
                    float obj2Val = i;
                    return new Chromosome(obj1Val, obj2Val);
                })
                .collect(Collectors.toList());
    }
}
