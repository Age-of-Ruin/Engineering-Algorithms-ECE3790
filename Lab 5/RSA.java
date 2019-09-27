/*******************************************************************************
*
* RSA.java
*
* This program implements the RSA algorithm to encrypt and then decrypt a
* simple string represented in text format.
*
* RSA is a public key encryption system which makes use of two keys, a public
* one and a private one. Each key is made up of two parts an exponent value and
* a modulus value.
*
* The modulus value is determined by two (pseudo) random numbers of a given
* (large) size, and is the same for both keys. The public exponent may then be
* selected, and a private exponent calculated.
*
* The BigIntegers class provides native support for implementing encryption
* algorithms in Java.
*
*******************************************************************************/

import java.math.BigInteger;
import java.io.*;
import java.nio.file.*;
import java.util.*;


class RSA {
	
static public void main(String args[]){        
    // Time Variables
    long startTime, stopTime, elapsedTime;
    double seconds;
    
    // Start Time
    startTime = System.nanoTime();
    
    // Select Key
    int KEY_SIZE = 2048;
    
    // Create a BigInteger constant for the integer ’1’.
	BigInteger one = new BigInteger("1");
	
	// Determine a modulus of sufficient size (based on the size of P and Q).
	BigInteger p = new BigInteger(KEY_SIZE,20, new Random());
	BigInteger q = new BigInteger(KEY_SIZE,20, new Random());
	BigInteger n = p.multiply(q);
	
	// Calculate PHI(n) for use in determining the public and private exponents.
	BigInteger phi = p.subtract(one).multiply(q.subtract(one));
	
	// Select a public exponent (repeat until GCD condition is met).
	BigInteger e = new BigInteger(32,4, new Random());
	BigInteger gcd = phi.gcd(e);
	while (!gcd.equals(one)) {
            e = new BigInteger(32,4, new Random());
            gcd = phi.gcd(e);
	}//while
	
	// Calculate the private exponent.
	BigInteger d = e.modInverse(phi);	
                	
    // Input file
    int blockSize = KEY_SIZE/8;
    byte[] byteArray = new byte[0];
    byte[] byteArrayCopy = new byte[0];
    byte[] block = new byte[blockSize];
    byte[] blockBack = new byte[blockSize];
    String strFilePath = "./rsa_msg.txt";
    String messageString = "";
    String messageStringBack = "";
    Path file = Paths.get(strFilePath);
    
    // Other Variables
    int count;
    int start;
    int end;
    
    try{
        // Read all bytes of the file
        byteArray = Files.readAllBytes(file);
        messageString = new String(byteArray);
        byteArrayCopy = new byte[byteArray.length];
        
        // Loop through entire file (while breaking file into blocks)
        count = 0;
        start = 0;
        end = block.length;
        while(count < (byteArray.length / blockSize) + 1){
            
            // If end exceeds length of array
            if (end > byteArray.length)
                end = byteArray.length;

            //Create Blocks
            for(int i = start; i < end; i++){
                block[i%block.length] = byteArray[i];
            }

            // Turn byteArray into (BigInteger) message
            BigInteger messageAsBigInt = new BigInteger(block); 
            String blockString = new String(block);

            // Encrypt and decrypt the message text (as blocks).
            BigInteger cypherAsBigInt = messageAsBigInt.modPow(e,n);
            BigInteger  decypherAsBigInt = cypherAsBigInt.modPow(d,n);

            // Return decyphered message to bytes & text
            blockBack = decypherAsBigInt.toByteArray();	
            String blockStringBack = new String(blockBack);
            
            //Store Decrypted Block into new array
            for(int i = start; i < end; i++){
                byteArrayCopy[i] = blockBack[i%block.length];
            }

            // Print Intermediate Results 
            // System.out.println("Key Length = " + KEY_SIZE);
            // System.out.println("Key Length = " + blocksize);
            // System.out.println("P = " + p);
            // System.out.println("Q = " + q);
            // System.out.println("Modulus n = " + n);
            // System.out.println("PHI = " + phi);
            // System.out.println("Public Exponent e = " + e);
            // System.out.println("Private Exponent d = " + d);
            // System.out.println("d e mod phi = " + d.multiply(e).mod(phi));
            // System.out.println("The block as blockAsBigInt:\n" + messageAsBigInt + "\n");
            // System.out.println("The cyphertext cypherAsBigInt is:\n" + cypherAsBigInt + "\n");
            // System.out.println("The decrypted cipher decypherAsBigInt is:\n" + decypherAsBigInt + "\n");
            // System.out.println("The blockString is:\n" + blockString + "\n");
            // System.out.println("The blockStringBack is:\n" + blockString + "\n");
            
            // Iterate count and block indices
            count++;
            start += block.length;
            end += block.length;
            
        }//while
    }//try
    catch(Exception except){
        System.out.println(except.getMessage());
    }//catch
    
    // Turn the decyphered byte array into string
    messageStringBack = new String(byteArrayCopy);
                        
    // Calulate ElapsedTime
    stopTime = System.nanoTime();
    elapsedTime = stopTime - startTime;
    seconds = (double)elapsedTime/1000000000;

    System.out.println("The messageString is:\n" + messageString + "\n");
    System.out.println("*******************************************************************************\n\n" +
            "Decyphering Message as Blocks and Returning Decyphered Message\n\n" +
            "*******************************************************************************\n");
    System.out.println("The messageStringBack is:\n" + messageStringBack + "\n");
    System.out.println("\nElapsed Time: " + seconds + " seconds");
	
    } // main()	
} // RSAtest
