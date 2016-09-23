package part3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by xcr on 22/09/16.
 */
@SuppressWarnings("Duplicates")
public class GeneticAlgorithm {

    private boolean input = true;

    private Random random;
    private int n, popSize,numOfMutations, numOfParentCombinations;
    private int[] startBoard;
    private Scanner reader;
    private int[][] arrayOfBoards;

    public GeneticAlgorithm(){
        reader = new Scanner(System.in);
        this.n = 5;
        this.popSize = 5;
        this.numOfMutations = 1;
        this.numOfParentCombinations = 1;


        this. startBoard = new int[n];
        this.random = new Random();
        this.arrayOfBoards = new int[popSize][n];

        if (input){
            System.out.print("n = ");
            this.n = reader.nextInt();
            reader.nextLine();
            System.out.print("input = ");
            String initialBoardString = reader.nextLine();
            if (initialBoardString.length()>0){
                this.startBoard = Arrays.stream(initialBoardString.split(" ")).mapToInt(Integer::parseInt).toArray();
            }

        }
        System.out.println("Initial board:");
        printBoard(startBoard);
        sortRows(startBoard);
        System.out.println("\nSorted initial board:");
        printBoard(startBoard);
        System.out.println("Initial populaton:");
        generatePopulation();
        selectParents();

    }

    public void generatePopulation(){
        
        for (int i = 0; i < popSize; i++) {
            arrayOfBoards[i] = swapColumns(startBoard);
            printBoard(arrayOfBoards[i]);
        }
    }

    public int[] swapColumns(int[] array){
        for (int i = 0; i < this.numOfMutations; i++) {
            int first = random.nextInt(this.n);
            int second = random.nextInt(this.n);
            while(first == second){
                second = random.nextInt(this.n);
            }
            int temp = array[first];
            array[first] = array[second];
            array[second] = temp;
            
            
        }
        return array;
    }

    public void sortRows(int[] array){
        boolean[] taken = new boolean[this.n];
        ArrayList<Integer> collisionColumns = new ArrayList<>();

        for (int col = 0; col < array.length; col++) {
            if (!taken[array[col]]) taken[array[col]] = true;
            else collisionColumns.add(col);
        }
        for (int row = 0; row < taken.length; row++) {
            if(taken[row] == false){
                array[collisionColumns.remove(random.nextInt(collisionColumns.size()))] = row;
            }
        }
    }

    public void printBoard(int[] array) {
        for (int i = this.n-1; i > -1; i--) {
            for (int j = 0; j < this.n; j++) {
                if(array[j] == i) System.out.print("X ");
                else System.out.print("- ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void selectParents(){
        int[] parentFitnessArray = new int[popSize];
        int totalFitness = 0;
        double[] parentProbArray = new double[popSize];
        int parentFitness;
        for (int i = 0; i < arrayOfBoards.length; i++) {
            parentFitness = heuristic(arrayOfBoards[i]);
            System.out.println(parentFitness);
            parentFitnessArray[i] = parentFitness;
            totalFitness += parentFitness;
        }
        parentProbArray[0] = heuristic(arrayOfBoards[0]) / totalFitness;
        for (int i = 1; i < arrayOfBoards.length; i++) {
            parentProbArray[i] = heuristic(arrayOfBoards[i]) / totalFitness + parentProbArray[i - 1];
        }
        for (int i = 0; i < parentProbArray.length; i++) {
            System.out.println(parentProbArray[i]);
        }

    }
    public void breed(int[] first, int[] second){

    }

    public int heuristic(int[] array){
        int cost= 0;
        int downRight[] = new int[2*this.n-1];
        int upRight[] = new int[2*this.n-1];
        int horizontal[] = new int[array.length];
        //finds queen and increment its row and diagonal values.
        for (int x = 0; x < array.length; x++) {
            downRight[array[x]+x]++;
            upRight[x-array[x]+this.n-1]++;
            horizontal[array[x]]++;
        }

        //adds 1 to cost if queens are attacking each other
        for (int i = 0; i < downRight.length; i++) {
            if (downRight[i] > 0) cost += downRight[i] - 1;
            if (upRight[i] > 0) cost += upRight[i] - 1;
        }

        for (int i = 0; i < horizontal.length; i++) {
            if (horizontal[i] > 0 ) cost += horizontal[i] - 1;
        }
        // System.out.println("cost: "+cost);
        System.out.println("Cost: " + cost);
        return cost;

    }

    public void printArray(int[] array){
        System.out.print("Array:" );
        for (int i = 0; i < array.length; i++) {
            System.out.print((array[i]+1)+" ");
        }
        System.out.println('\n');
    }


    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm();

    }

}
