package imgseg_representation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Population implements Iterable<Chromosome>{

    private List<Chromosome> chromosones = new ArrayList<>();

    public Population(List<Chromosome> chromosomes) {

        this.chromosones = chromosomes;
    }

    public List<Chromosome> getPopulace() {
        return chromosones;
    }

    public void setPopulace(List<Chromosome> populace) {
        this.chromosones = populace;
    }

    public Stream<Chromosome> stream() {
        return chromosones.stream();
    }
    public Iterator<Chromosome> iterator() {
        return chromosones.iterator();
    }
}
