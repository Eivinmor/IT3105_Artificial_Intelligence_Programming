package part3;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Eirikpc on 24-Sep-16.
 */
@SuppressWarnings("Duplicates")
public class TabuSearch {
    private ArrayList<Integer> startBoard;
    private int n, arrayPrintIndexing,runTime;
    private Random random;
    private HashSet<ArrayList<Integer>> solutionSet,tabuSet,neighbourSet;
    private Scanner reader;
    private boolean input, stepByStep;


    private TabuSearch(){

        // ---- SETTINGS ---------------------------------
        this.input = false;
        this.stepByStep = false;
        this.n = 16;
        this.arrayPrintIndexing = 0;
        this.runTime = 120;
        // -----------------------------------------------

        solutionSet = new HashSet<>();
        tabuSet = new HashSet<>();
        neighbourSet = new HashSet<>();

        reader = new Scanner(System.in);
        this.startBoard = new ArrayList<>();
        random = new Random();
        if (input) {
            System.out.print("n = ");
            this.n = reader.nextInt();
            reader.nextLine();
            System.out.print("Initial queen positions:");
            startBoard = processInput(reader.nextLine());
            System.out.println();
            System.out.println("Board after sortRowCollision:");
        }
        else {
            generateStartBoard();
            System.out.println();
            System.out.println("n = " + n);
            System.out.println();
            System.out.print("Initial queen positions:");
            printArray(startBoard);
            System.out.println();
        }
        sortRowCollisions(startBoard);







    }

    private void runAlgorithm(){
        ArrayList<Integer> currentBoard = this.startBoard;
        long startTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();
        while(currentTime - startTime < runTime*1000){

            neighbourSet = generateNeighbours(currentBoard);
            currentBoard = findBestNeighbour(neighbourSet);
            tabuSet.add(currentBoard);

            currentTime = System.currentTimeMillis();
        }

    }

    // OPTIMISE!!
    private HashSet<ArrayList<Integer>> generateNeighbours(ArrayList<Integer> array){
        ArrayList<Integer> newNeighbour;
        HashSet<ArrayList<Integer>> newNeighbourSet = new HashSet<>();
        for (int i = 0; i < array.size(); i++) {
            for (int j = 0; j < array.size(); j++) {
                newNeighbour = new ArrayList<>(array);
                swapColumns(newNeighbour,i,j);
                newNeighbourSet.add(newNeighbour);

            }
        }
        newNeighbourSet.remove(array);
        newNeighbourSet.removeAll(tabuSet);
        return newNeighbourSet;
    }
    private ArrayList<Integer> findBestNeighbour(HashSet<ArrayList<Integer>> set){
        ArrayList<Integer> bestNeighbour = new ArrayList<>();
        int bestFitness = Integer.MAX_VALUE;
        int currentFitness;
        for (ArrayList<Integer> newNeighbour :
                set) {
            currentFitness = calculateFitness2(newNeighbour);
            if(currentFitness == 0){
                //System.out.println("solution found!!");
                solutionSet.add(newNeighbour);
            }
            if(currentFitness < bestFitness){
                bestFitness = currentFitness;
                bestNeighbour = new ArrayList<Integer>(newNeighbour);

            }
        }
        return bestNeighbour;
    }




    private void swapColumns(ArrayList<Integer> array,int first, int second) {
        int temp = array.get(first);
        array.set(first,array.get(second));
        array.set(second,temp);
//        System.out.println("Swapped column "+first+" with " + second);
    }
    private void sortRowCollisions(ArrayList<Integer> array){
        boolean[] taken = new boolean[this.n];
        ArrayList<Integer> collisionColumns = new ArrayList<>();
        for (int col = 0; col < array.size(); col++) {
            if (!taken[array.get(col)]) taken[array.get(col)] = true;
            else collisionColumns.add(col);
        }
        for (int row = 0; row < taken.length; row++) {
            if(!taken[row]){

                array.set(collisionColumns.remove(random.nextInt(collisionColumns.size())),row);
            }
        }
    }

    private void printHashSet(HashSet<ArrayList<Integer>> set){
        for (ArrayList<Integer> arrayList :
                set) {
            printArray(arrayList);

        }
    }


    private int calculateFitness2(ArrayList<Integer> array){
        int cost = 0;
        int downRight[] = new int[2*this.n-1];
        int upRight[] = new int[2*this.n-1];
        //finds queen and increment its row and diagonal values.
        for (int x = 0; x < array.size(); x++) {
            downRight[array.get(x)+x]++;
            upRight[x-array.get(x)+this.n-1]++;
        }
        //adds 1 to cost if queens are attacking each other
        for (int i = 0; i < downRight.length; i++) {
            if (downRight[i] > 0) cost += downRight[i] - 1;
            if (upRight[i] > 0) cost += upRight[i] - 1;
        }
        return cost;
    }

    private ArrayList<Integer> processInput(String input) {
        int[] array = Arrays.stream(input.split(" ")).mapToInt(Integer::parseInt).toArray();
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {

            arrayList.add(array[i]--);
        }
        return arrayList;
    }

    private void generateStartBoard(){
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < this.n; i++) numbers.add(i);
        for (int i = 0; i < this.n; i++) {
            this.startBoard.add(numbers.remove(random.nextInt(numbers.size())));
        }
    }


    private void printBoard(ArrayList<Integer> array) {
        printArray(array);
        for (int i = this.n-1; i > -1; i--) {
            for (int j = 0; j < this.n; j++) {
                if(array.get(j) == i) System.out.print("X ");
                else System.out.print("- ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void printArray(ArrayList<Integer> array){
        for (int i = 0; i < array.size(); i++) {
            System.out.print((array.get(i)+arrayPrintIndexing)+" ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        TabuSearch ts = new TabuSearch();
        long startTime = System.currentTimeMillis();
        ts.runAlgorithm();
        long endTime = System.currentTimeMillis();
        double executionTime = endTime - startTime;
        System.out.println("n = " + ts.n);
        System.out.println("TabuSet Length: " + ts.tabuSet.size());
        System.out.println("Unique solutions found: "+ ts.solutionSet.size());
        System.out.println("Execution time: " + executionTime/1000 + " seconds");


    }

}
