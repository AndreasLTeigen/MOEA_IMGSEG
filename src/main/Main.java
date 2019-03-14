package main;

import graphics.Plot;
import imgseg_representation.Chromosome;
import imgseg_representation.Image;
import imgseg_representation.IsegImageIO;
import imgseg_representation.Problem;
import imgseg_solver.ChromosomeEvaluations;
import imgseg_solver.HeuristicPopulationInitializer;
import imgseg_solver.IsegSolver;
import imgseg_solver.RandomPopulationInitializer;
import solver.GeneticSolver;

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

        int chromCount = 3;

        List<Chromosome> chroms = Stream.generate(() -> RandomPopulationInitializer.createRandomChromosome(p))//HeuristicPopulationInitializer.HeuristicInitializer(p, 3, 1000))
                .limit(chromCount).collect(Collectors.toList());

        //compute objectives
        chroms.forEach(Chromosome::computeObjectives);

        Plot.PlotObjectiveValues(chroms);


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