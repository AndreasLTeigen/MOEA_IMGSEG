package imgseg_representation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Population implements Iterable<Chromosome>{

    public List<Chromosome> chromosones = new ArrayList<>();

    public Population(){
        this.chromosones = new ArrayList<>();
    }

    public Population(List<Chromosome> chromosomes) {

        this.chromosones = chromosomes;
    }

    public Stream<Chromosome> stream() {
        return chromosones.stream();
    }
    public Iterator<Chromosome> iterator() {
        return chromosones.iterator();
    }
}
