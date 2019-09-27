package simulatedannealing;

import java.util.* ;
import java.io.*;

public class SimulatedAnnealing {
 
  static int globalStopCount = 100; // program will stop using Metropolous criteria – after 100 equilibriums with no change
  static int localStopCount = 10000; // determines the iterations until 1 equilibrium
  
  public static void main(String[] args) { 
        
    int size, oldCost, newCost, penalty; // initilize size and variables to track costs and penalty (cardinality)
    long startTime, endTime, elapsedTime; // used to track the run-time of the program
    double ratio, seconds; // ratio (ie percent improvement) and another time variable
    String time; // one more time variable
    boolean check; // used to check for appropriate inputs
    int[] bins; // store which bin each component will be assigned
    int[][] matrix; // store adjMatrix
    
    //Establish Size, Penalty and Matrix
    size = readSize();
    penalty = (int)(size/1.5);

    matrix = new int[size][size]; 
    bins = new int[size]; 

    check = readInput(matrix); // read input from file and store in matrix
    
    // Perform Algorithm    
    if(check){
      startTime = System.nanoTime();
      
      assignBins(bins); // assign each component to a random bin (either 0 or 1)
      //printMatrix(matrix); // print matrix
      
      oldCost = calculateCost(matrix, bins, penalty); // calculate initial state
      System.out.println("\nOld Cost = " +oldCost+ "\n");
      printBins(bins);
      
      newCost = optimizeCost(matrix, bins, oldCost, penalty); // optimize the cost via simulated Annealing
      System.out.println("\nNew Cost = " +newCost+ "\n");
      printBins(bins);
      
      endTime = System.nanoTime();
      elapsedTime = endTime - startTime; // elasped time in nanoseconds
      seconds = elapsedTime/Math.pow(10,9); // time in seconds
      System.out.println("\nRun-Time:   " + elapsedTime + " nanoseconds");
      time = String.format("\t or %1$.3f seconds", seconds);
      System.out.println(time);

      ratio = ((double)oldCost)/((double)newCost); // percent improvment
      System.out.println("\nRatio (ie improvement): " + ratio);
    }//if
    System.out.println("\nProgram Ends");
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
      if (row < adjMatrix.length || col < adjMatrix.length) // check if input is too small for defined matrix/bins
          throw new Exception();
    }//try
    catch(Exception e){
      System.out.println("\nERROR - NO MATRIX WAS INPUT - FILE OR SIZE WAS INCORRECT");
      check = false; // no matrix was input
    }//catch   
    return check;   
  }//readInput
  
  private static void printMatrix(int[][] adjMatrix){
    for(int i = 0; i < adjMatrix.length; i++){
      for(int j = 0; j < adjMatrix.length; j++)
        System.out.print(adjMatrix[i][j]);
      System.out.println();
    }//for
  }//printMatrix
  
  private static void assignBins(int[] binArray){
    for(int i = 0; i < binArray.length; i++)
      binArray[i] = (int)(Math.random()*2);
  }//assignBins
  
  private static void printBins(int[] binArray){
    System.out.print("Bins: ");
    for(int i = 0; i < binArray.length; i++)
      System.out.print(binArray[i]);   
    System.out.println();
  }//printBins
  
  private static int calculateCost(int[][] adjMatrix, int[] binArray, int penalty){
    int cost = 0; // track the cost
    int bin0count = 0; // track bin cardinality
    int bin1count = 0;
    
    for(int i = 0; i < adjMatrix.length-1; i++){
      if(binArray[i] == 0)
        bin0count++;
      else
        bin1count++;
      for (int j = i+1; j < adjMatrix.length; j++){
        if(binArray[i] != binArray[j])
          cost += adjMatrix[i][j];
      }//for
    }//for
    
    if(binArray[adjMatrix.length-1] == 0)
      bin0count++;
    else
      bin1count++;
    
    cost = cost + (Math.abs(bin0count - bin1count) * penalty); // add penalty
    
    return cost;
  }//calculateCost
  
  private static int optimizeCost(int[][] adjMatrix, int[] binArray, int oldCost, int penalty){
    int bestCost = oldCost; // stores the initial state as best cost (so far)
    int newCost = 0; // tracks the cost when optimized
    int globalCount = 0; int count = 0; // tracks the number of unsuccessful attempts and total executions
    int sample1 = 0; int sample2 = 0; // used determine when solution is changing rapidly
    int rand1, rand2, costChange, prevCost; // used to store random numbers and the change of cost
    double controlParameter = 3000; // initialize control paramter
    
    while (globalCount < globalStopCount){
        prevCost = bestCost;
        for (int i = 0; i < localStopCount; i++){// 10000 or 50000 attempts to yield equilibrium

          do{
            rand1 = randomInt(adjMatrix.length);
            rand2 = randomInt(adjMatrix.length); 
          }//do
          while(rand1 == rand2 || binArray[rand1] == binArray[rand2]); // ensures 2 unique components in different bins

          swapBin(binArray, rand1, rand2); // generate new solution
          newCost = calculateCost(adjMatrix, binArray, penalty); // calculate new cost      

          costChange = newCost-bestCost; // calulate change in cost of new state

          if(costChange < 0) // if newCost < oldCost
            bestCost = newCost; // keep new cost
          else if(costChange > 0 && 
                  (Math.random() < Math.exp(-costChange/controlParameter))){
              bestCost = newCost; // keep new cost
          }//else if
          else{
            swapBin(binArray, rand1, rand2); // swap back
          }//else
        }//for
                
        if (Math.abs(bestCost-prevCost) == 0) // track the number of times cost does not change
            globalCount++;
        else
            globalCount = 0;
        
        if(count % 2 == 0)
            sample1 = globalCount;
        else
            sample2 = globalCount;
        
        if (Math.abs(sample2-sample1) == 0) // sample more when global count is changing
            localStopCount = 50000;
        else
            localStopCount = 10000;
        
        controlParameter = controlParameter * .9; // change control parameter
        count++;
    }//while
    
    // check case where all components in 1 bin 
    int extremeCase =  adjMatrix.length * penalty;   
    if(extremeCase < bestCost){ 
      bestCost = extremeCase;
      Arrays.fill(binArray, 0);
      System.out.print("\n*Penalty may be too low*");
    }
    return bestCost;
  }//optimizeCost
  
  private static int randomInt(int range){
    return (int)(Math.random() * range);
  }//randomInt
  
  // Returns a random number between 0 and range-1 (inclusive) 
    
  private static void swapBin(int[] binArray, int index1, int index2){
    int temp = binArray[index1];
    binArray[index1] = binArray[index2];
    binArray[index2] = temp;    
  }//swapBin
  
}//simulatedAnnealing