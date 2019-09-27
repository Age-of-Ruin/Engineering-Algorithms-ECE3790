import java.util.* ;
import java.io.*;

public class ComponentMinimization {
  
  static int stopCount = 10000; // declares the number of unsuccessful attempts (in a row) until the program stops
  
  public static void main(String[] args) { 
        
    int size = 200; // MUST DEFINE SIZE of the matrix BEFORE EACH READ (ie 100 for lab files provided)
    int penalty = (int) (size/1.5); // define the penalty for differences in cardinality of the bins
    int oldCost, newCost; // used to track the cost before and after optimization
    long startTime, endTime, elapsedTime; // used to track the run-time of the program
    double ratio; // holds the percent increase of the cost
    
    int[][] matrix = new int[size][size]; // store adjMatrix
    int[] bins = new int[size]; // store which bin each component will be assigned

    boolean check = readInput(matrix); // read input from file and store in matrix
    
    if(check){
      startTime = System.nanoTime();
      
      assignBins(bins); // assign each component to a random bin (either 0 or 1)
      //printMatrix(matrix); // print matrix
      
      oldCost = calculateCost(matrix, bins, penalty); // calculate first initial guess of the cost
      System.out.println("\nOld Cost = " +oldCost+ "\n");
      printBins(bins);
      
      newCost = optimizeCost(matrix, bins, penalty); // optimize the cost via greedy swapping method
      System.out.println("\nNew Cost = " +newCost+ "\n");
      printBins(bins);
      
      endTime = System.nanoTime();
      elapsedTime = endTime - startTime;    
      System.out.println("\nRun-Time: " + elapsedTime + " nanoseconds");
      
      ratio = ((double)oldCost)/((double)newCost);
      System.out.println("\nRatio (ie improvement): " + ratio);
    }//if
    System.out.println("\nProgram Ends");
  }//main
  
  private static boolean readInput(int[][] adjMatrix){
    
    boolean check = true; // checks if input was received
    Scanner lineReader = null; // used to read lines from the file
    Scanner keyboard = new Scanner(System.in); // read from keyboard
    
    System.out.println("Please enter file name including extension (.txt only) and ensure " +
    "the txt file is in the same folder as this program.");
    String input = keyboard.next();
    
    try{
      lineReader = new Scanner(new File (input));
      lineReader.useDelimiter(" ");
      
      int row = 0;
      while(lineReader.hasNextLine()){
        int col = 0;
        while(lineReader.hasNextInt()){
          adjMatrix[row][col] = lineReader.nextInt();
          col++;
        }//while
        lineReader.nextLine();
        row++;
      }//while
    }//try
    catch(IOException e){
      System.out.println("ERROR - NO MATRIX WAS INPUT");
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
  
  private static int optimizeCost(int[][] adjMatrix, int[] binArray, int penalty){
    int bestCost = calculateCost(adjMatrix, binArray, penalty); // stores the current best cost
    int newCost = 0; // tracks the cost when optimized
    int errorCount = 0; // tracks the number of unsuccessful attempts
    int rand1, rand2; // used to store random numbers 
    
    while(errorCount < stopCount){ // 10000 unsuccessful attempts yields stop condition
      
      do{
        rand1 = randomNum(adjMatrix.length);
        rand2 = randomNum(adjMatrix.length); 
      }//do
      while(rand1 == rand2 || binArray[rand1] == binArray[rand2]); // ensures 2 unique components in different bins
      
      swapBin(binArray, rand1, rand2);
      newCost = calculateCost(adjMatrix, binArray, penalty);
      if(newCost < bestCost){
        bestCost = newCost; // keep new cost
        errorCount = 0; // reset error count
      }//if
      else{
        swapBin(binArray, rand1, rand2); // swap back
        errorCount++;
      }//else
    }//while
    
    int extremeCase =  adjMatrix.length * penalty; // check case where all components in 1 bin   
    if(extremeCase < bestCost){ 
      bestCost = extremeCase;
      Arrays.fill(binArray, 0);
      System.out.print("\n*Penalty may be too low*");
    }
    return bestCost;
    
  }//optimizeCost
  
  private static int randomNum(int range){
    
    return (int)(Math.random() * range);
    
  }//randomNum
  
  // Returns a random number between 0 and range-1 (inclusive) 
  
  
  private static void swapBin(int[] binArray, int index1, int index2){
    int temp = binArray[index1];
    binArray[index1] = binArray[index2];
    binArray[index2] = temp;    
  }//swapBin
  
}//componentMinimization
