package main;

import imgseg_representation.*;
import imgseg_solver.IsegSolver;
import imgseg_solver.RandomPopulationInitializer;
import solver.GeneticSolver;

public class Main{
    public static void main(String[] args){

        Image img = IsegImageIO.loadImage("images/86016/Test image.jpg");
        Problem p = new Problem(img);

        GeneticSolver solver = new IsegSolver();
        solver.popSize = 2;
        //solver.solve(p);

        //test drawImageAndSegmentation
        Chromosome chrom = RandomPopulationInitializer.createRandomChromosome(p);
        IsegImageIO.drawSegmentedImage(chrom.img, chrom.segmentation);

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