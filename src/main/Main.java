package main;

import graphics.Plot;
import imgseg_representation.*;
import imgseg_solver.ChromosomeEvaluations;
import imgseg_solver.HeuristicPopulationInitializer;
import imgseg_solver.IsegSolver;
import imgseg_solver.RandomPopulationInitializer;
import solver.GeneticSolver;
import imgseg_solver.NsgaParentSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main{
    public static void main(String[] args){

        Image img = IsegImageIO.loadImage("images/86016/Test image.jpg");
        Problem p = new Problem(img);


//        GeneticSolver solver = new IsegSolver();
//        solver.popSize = 2;
//        solver.solve(p);

        int chromCount = 10;

        Plot plot = new Plot();
        //HeuristicPopulationInitializer.HeuristicInitializer(p, 3, 1000))
        List<Chromosome> chroms = Stream.generate(() -> {
                    Chromosome c = RandomPopulationInitializer.createRandomChromosome(p);
                    c.computeObjectives();
                    plot.addParetoFront(Arrays.asList(c));
                    return c;
                })
                .limit(chromCount).collect(Collectors.toList());

        List<Chromosome> chroms2 = Stream.generate(() -> {
            Chromosome c = RandomPopulationInitializer.createRandomChromosome(p);
            c.computeObjectives();
            plot.addParetoFront(Arrays.asList(c));
            return c;
        })
                .limit(chromCount).collect(Collectors.toList());

        //compute objectives
        chroms.forEach(Chromosome::computeObjectives);

        NsgaParentSelector parentSelector = new NsgaParentSelector(chromCount);
        Population populace1 = new Population();
        populace1.chromosones = chroms;
        Population populace2 = new Population();
        populace2.chromosones = chroms2;
        populace1 =  parentSelector.selectParents(populace1, populace2);
        
        //Plot.PlotObjectiveValues(chroms);
        //chroms.forEach(Chromosome::computeObjectives);


        List<Integer> segemntCounts = chroms.stream().map(c -> c.segmentation.getSegmentations().size())
                .collect(Collectors.toList());

//        System.out.println("seg deviation: " + chroms.stream().map(c -> c.objectiveValues.get(Chromosome.overallDeviationIndex)).collect(Collectors.toList()));
//        System.out.println("seg connectivities: " + chroms.stream().map(c -> c.objectiveValues.get(Chromosome.connectivityIndex)).collect(Collectors.toList()));
//        System.out.println("image segment count: " + segemntCounts);
//
//        chroms.forEach(IsegImageIO::drawCharomosome);


        //test drawImageAndSegmentation
//        IsegImageIO.drawSegmentedImage(chrom.img, chrom.segmentation);

//        long startTime = System.currentTimeMillis();
//        Population pop = new RandomPopulationInitializer().initPopulation(p, 100);
//        System.out.println("time to create a solution: "+ ((double)(System.currentTimeMillis() - startTime))/1000.0/100.0);
//        int i = 0;
//        for (Chromosome c : pop) {
//            if (i++ % 20 == 0)
//                IsegImageIO.drawSegmentation(c.segmentation);
//        }
    }
}