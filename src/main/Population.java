package main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Population implements Iterable<Chromosome>{

    private List<Chromosome> chromosones = new ArrayList<>();

    public Population(List<Chromosome> chromosomes) {

        this.chromosones = chromosomes;
    }

    public Iterator<Chromosome> iterator() {
        return chromosones.iterator();
    }
}
