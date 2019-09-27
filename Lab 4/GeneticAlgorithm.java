package geneticalgorithm;

import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;

/**
 * @author Richard
 */
public class GeneticAlgorithm {

    static int globalStopCount = 5000;
    
    public static void main(String[] args) {
        int size, penalty, oldCost, newCost, numGens; //size, penalty, cost and number of generations
        long startTime, endTime, elapsedTime; // time variables
        double ratio, seconds; // percent improvement and another time variable
        String time; // one more time variable
        int[] firstRandomSolution, selected; // first generation (for oldCost calculation)
        int[][] adjMat;
        boolean check;
        
        size = readSize();
        adjMat = new int[size][size];
        penalty = (int)(size/1.5);
        
        check = readInput(adjMat);   
        if (check){
            startTime = System.nanoTime();
            
            // Calculate Initial random guess
            firstRandomSolution = createGeneration(size);
            oldCost = calculateCost(adjMat, firstRandomSolution, penalty);
            System.out.println("\nOld Cost = " + oldCost);
            printSolution(firstRandomSolution);
                        
            //Optimize Cost via Genetic Algorithm
            numGens = 100; // number of generations per population - MUST BE DIVISIBLE BY 4 
            selected = optimizeCost(adjMat, numGens, penalty);
            newCost = calculateCost(adjMat, selected, penalty);
            System.out.println("\nNew Cost = " + newCost);
            printSolution(selected);
            
            // Print percent improvement
            ratio = ((double)oldCost)/((double)newCost); // percent improvment
            System.out.println("\nRatio (ie improvement): " + ratio);
            
            // Print time statistics
            endTime = System.nanoTime();
            elapsedTime = endTime - startTime; // elasped time in nanoseconds
            seconds = elapsedTime/Math.pow(10,9); // time in seconds
            System.out.println("\nRun-Time:   " + elapsedTime + " nanoseconds");
            time = String.format("\t or %1$.3f seconds", seconds);
            System.out.println(time);
      
        }//if
        System.out.println("Program Ends");
    }//main
    
    private static int readSize(){
        System.out.println("What is the size of the matrix?");
        Scanner input = new Scanner(System.in);
        while(!input.hasNextInt()){
            System.out.println("Not a correct size.");
            System.out.println("What is the size of the matrix?");
            input = new Scanner(System.in);
        }//while
          return input.nextInt();
    }//readSize
    
    private static boolean readInput(int[][] adjMatrix){
        boolean check = true; // checks if input was received
        int row = 0; // used to track rows and columns of the matrix
        int col = 0;
        Scanner lineReader = null; // used to read lines from the file
        Scanner keyboard = new Scanner(System.in); // read from keyboard

        System.out.println("Please enter file name including extension (.txt only) and ensure " +
        "the txt file is in the same folder as this program.");
        String input = keyboard.next();

        try{
          lineReader = new Scanner(new File (input));
          lineReader.useDelimiter(" ");

          while(lineReader.hasNextLine()){
            col = 0;
            while(lineReader.hasNextInt()){
              adjMatrix[row][col] = lineReader.nextInt();
              col++;
            }//while
            lineReader.nextLine();
            row++;
          }//while
          if (row < adjMatrix.length || col < adjMatrix.length) // check if input is too small for defined matrix
              throw new Exception();
        }//try
        catch(Exception e){
          System.out.println("\nERROR - NO MATRIX WAS INPUT - FILE OR SIZE WAS INCORRECT");
          check = false; // no matrix was input
        }//catch   
        return check;   
    }//readInput
    
    private static int[] createGeneration(int size){
        int[] array = new int[size];
        for(int i = 0; i < array.length; i++)
            array[i] = (int)(Math.random()*2);
        return array;
    }//createGeneration
    
    private static int calculateCost(int[][] adjMatrix, int[] array, int penalty){
        int cost = 0; // track the cost
        int bin0count = 0; // track bin cardinality
        int bin1count = 0;

        for(int i = 0; i < adjMatrix.length-1; i++){
          if(array[i] == 0)
            bin0count++;
          else
            bin1count++;
          for (int j = i+1; j < adjMatrix.length; j++){
            if(array[i] != array[j])
              cost += adjMatrix[i][j];
          }//for
        }//for

        if(array[adjMatrix.length-1] == 0)
          bin0count++;
        else
          bin1count++;

        cost = cost + (Math.abs(bin0count - bin1count) * penalty); // add penalty

        return cost;
    }//calculateCost
    
    private static int[] optimizeCost(int[][] adjMat, int numGens, int penalty){
        int[][] population = new int[numGens][adjMat.length];
        int[][] selected = new int[numGens/2][adjMat.length];
        int[] fittest = new int[adjMat.length];
        int cost = Integer.MAX_VALUE;
        int sample1 = 0;
        int sample2 = 0; 
        int stopCount = 0;
        int count = 0;
        int newCost = 0;
        
        while(stopCount < globalStopCount){
            // create random parent generation
            for(int j = 0; j < numGens; j++){
                population[j] = createGeneration(adjMat.length);
            }//for
            
            selected = selectionByTourney(population, adjMat, penalty);

            crossover(population, selected);
            
            mutation(population);

            //Pick the least cost
            for(int i = 0; i < population.length; i++){
                newCost = calculateCost(adjMat, population[i], penalty);
                if(newCost < cost){
                    cost = newCost;
                    fittest = population[i];
                }//if
            }//for
            
            //Used to track how cost is changing - influences stopCount
            if(count % 2 == 0)
                sample1 = cost;
            else
                sample2 = cost;
            
            if(Math.abs(sample1 - sample2) == 0)
                stopCount++;
            else
                stopCount = 0;                        
            
            count++;
        
        }//while
     
        if(adjMat.length * penalty < cost){ //Extreme Case Check
            System.out.println("\n***Penalty may be too low***");
            Arrays.fill(fittest, 0); 
        }//if
        
        return fittest;
    }//optimizeCost
    
    private static void crossover(int[][] population, int[][] selected){
        int[] parent1 = new int[population[0].length];
        int[] parent2 = new int[population[0].length];
        int[] child1 = new int[population[0].length];
        int[] child2 = new int[population[0].length];
        int popCount = 0;
        int randomSeparator;
        
        for (int i = 0; i < selected.length; i += 2){ //used to pair selected individuals
            randomSeparator = randomInt(selected[0].length);
            parent1 = selected[i];
            parent2 = selected[i + 1];
            for(int j = 0; j < selected[0].length; j++){ // perform crossover
                if(j < randomSeparator){
                    child1[j] = parent1[j];
                    child2[j] = parent2[j];
                }//if
                else{
                    child1[j] = parent2[j];
                    child2[j] = parent1[j];
                }//else;
            }//for
                population[popCount] = parent1;
                population[popCount + 1] = parent2;
                population[popCount + 2] = child1;
                population[popCount + 3] = child2;
                
                popCount += 4;
        }//for
    }//crossover
    
    private static void mutation(int[][] population){
        for(int i = 0; i < population.length; i++){
            for(int j = 0; j < population[0].length; j++){
              if(Math.random() < .2){
                  if(population[i][j] == 1)
                      population[i][j] = 0;
                  else
                      population[i][j] = 1;
              }//if
            }//for
        }//for
    }//mutation
    
        private static int[][] selectionByRoulette(int[][] population, int[][] adjMat, int penalty){
        int sigFigs = 3;
        double[] populationFitness = new double[population.length];
        double[] probability = new double[population.length]; 
        int[][] selected = new int[population.length/2][population[0].length];
        int[] roulette = new int[10^sigFigs]; // used for roulette wheel
        int[] randomIndices = new int[selected.length];
        int sumFitness = 0;
        int intervalStart = 0; 
        int intervalEnd = 0;
        int numEntries;
        
        //Calculate fitness
        for(int i = 0; i < population.length; i++){
            populationFitness[i] = (double)1/calculateCost(adjMat, population[i], penalty);
            sumFitness += populationFitness[i];
        }//for
        
        //Calculate probability
        for(int i = 0; i < population.length; i++)
            probability[i] = populationFitness[i]/sumFitness;
        
        //Create Roulette Intervals
        for(int i = 0; i < probability.length; i++){
 
            numEntries = (int) probability[i] * 10^sigFigs;
            intervalEnd += numEntries;
            intervalStart = intervalEnd - numEntries;
            
            for(int j = intervalStart; j < intervalEnd; i++){
                roulette[j] = i;
            }//for
            
        }//for
        
        //Generate Random Indices 
        for(int i = 0; i < randomIndices.length; i++){
            randomIndices[i] = randomInt(10^sigFigs);
        }//for

        // Select population based on random selections from the roulette
        for(int i = 0; i < selected.length; i++){
            selected[i] = population[roulette[randomIndices[i]]];
        }//for
        
        
        return selected;
    }//selectionByRoulette
        
    private static int[][] selectionByTourney(int[][] population, int[][] adjMat, int penalty){
        
        double[] populationFitness = new double[population.length]; 
        int[][] selected = new int[population.length/2][population[0].length];
        int[] randomIndices = new int[population.length];
        
        //Calculate fitness
        for(int i = 0; i < population.length; i++){
            populationFitness[i] = (double)1/calculateCost(adjMat, population[i], penalty);
        }//for
        
        //Note: Greater Fitness means more likely to get picked
        
        //Generate Random Indices
        for(int i = 0; i < randomIndices.length; i++){
            randomIndices[i] = i;
        }//for
        
        shuffleArray(randomIndices);
       
        //Choose 2 random solutions and take the better fitness (ie tournament)
        int count = 0;
        for(int i = 0; i < population.length; i += 2){
            
            if(populationFitness[randomIndices[i]] > populationFitness[randomIndices[i + 1]])
                selected[count] = population[randomIndices[i]];
            else
                selected[count] = population[randomIndices[i + 1]];
            
            count++;
        }//for
        return selected;
    }//SelectionByTourney
    
  // Implementing Fisher–Yates shuffle
  static void shuffleArray(int[] array)
  {
    Random rnd = new Random();
    for (int i = array.length - 1; i > 0; i--)
    {
      int index = rnd.nextInt(i + 1);
      // Simple swap
      int a = array[index];
      array[index] = array[i];
      array[i] = a;
    }//for
  }//shuffleArray
    
    private static void printSolution(int[] array){
        System.out.print("Solution: \n");
        for(int i = 0; i < array.length; i++)
          System.out.print(array[i]);   
        System.out.println();
    }//printSolution
      
    private static void printMatrix(int[][] matrix){
        System.out.println();
        for(int i = 0; i < matrix.length; i++){
          for(int j = 0; j < matrix[0].length; j++)
            System.out.print(matrix[i][j]);
          System.out.println();
        }//for
    }//printMatrix
    
    private static int randomInt(int range){
        return (int)(Math.random() * range);
    }//randomInt
    
      
  // Returns a random number between 0 and range-1 (inclusive) 
    
}//geneticAlgorithm
