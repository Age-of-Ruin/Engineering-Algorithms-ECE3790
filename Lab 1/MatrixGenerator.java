import java.util.Scanner;

public class MatrixGenerator {
  
  
  public static void main(String[] args) { 
    
    int size, sparseness; // user selected parameters of size and sparseness
    int[][] adjacencyMatrix; // define the 2d matrix
    boolean twoParts; // user selected parameter that determines whether there are 2 distinct, connected parts
    
    try{
      
      Scanner keyboard = new Scanner(System.in); // read input from the keyboard
      
      System.out.println("Please enter the size of the adjacency matrix (n x n) as an integer.");
      size = keyboard.nextInt(); // save size parameter
           
      System.out.println("Please enter the sparseness of the adjacency matrix as an integer between " +
                         "0 and 100 (0 being full - 100 being totally sparse).");
      sparseness = keyboard.nextInt(); // determines sparseness of the matrix (0 means full - 100 means totally sparse)
      
      System.out.println("Would you like 2 distinct & connected components? (type 'yes' or anything else for 'no')");
      String response = keyboard.next(); // determine whether 2 connected components
      
      if(response.equals("yes")) // determine user response
        twoParts = true;
      else
        twoParts = false;    
      
      adjacencyMatrix = new int[size][size]; // declare matrix of dimensions size x size
      
      generateMatrix(size, twoParts, sparseness, adjacencyMatrix);
      
      System.out.println("\nProgram Ends");
      
    }//try
    
    catch (Exception InputMismatchException){
      System.out.println("Woops, wrong input.");
    }//catch
    
  }//main
  
  private static void generateMatrix(int size, boolean twoParts, int sparse, int[][] adjMatrix){
    
    int randomNum;
      
// fill top-right of matrix w/ weights between 0-9
      for(int i = 0; i < size; i++){
        for(int j = i+1; j < size; j++){
          randomNum = 1 + (int)(Math.random() * 100); // random number between 1 and 100
          if(sparse < randomNum)
            adjMatrix[i%j][j] = 1 + (int)(Math.random() * 9); // fill top right with weights between 0-9
        }//for
      }//for
      
// ************************* Two Connected Components ***********************************  
      if(twoParts){
        for(int i = 0; i < size; i++){
          for(int j = 0; j < size; j++){
            if(i <= size/2 && j > size/2)
              adjMatrix[i][j] = 0; // set appropriate elements to 0
          }//for
        }//for
      }//if

// copy top-right into bottom-left 
      for(int i = 0; i < size; i++){
        for(int j = i+1; j < size; j++){
          adjMatrix[j][i] = adjMatrix[i][j] ;
        }//for
      }//for
    
    printMatrix(size, adjMatrix);
    
  }//generateMatrix
  
  private static void printMatrix(int size, int[][] adjMatrix){
    
    System.out.println("\nAdjacency Matrix");
    
    // print matrix
    for(int i = 0; i < size; i++){
      for(int j = 0; j < size; j++){
        System.out.print(adjMatrix[i][j] + " ");
      }//for
      System.out.println();
    }//for
    
  }//print
  
}//MatrixGenerator