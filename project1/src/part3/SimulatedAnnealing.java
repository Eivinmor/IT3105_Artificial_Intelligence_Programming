package part3;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Eirikpc on 21-Sep-16.
 */
public class SimulatedAnnealing {

    private int[] startBoard;
    private int n, solutionsFound;
    private Random random;
    private double temp, cooling;
    private int[] generatedsolution, newGeneratedsolution;
    private ArrayList<String> solutionSet;


    public SimulatedAnnealing(int n){

        Scanner scanner = new Scanner(System.in);
        this.startBoard = new int[n];
        random = new Random();
        this.n = n;
        temp = 2;
        cooling = 0.00000005;
        solutionsFound = 0;
        solutionSet = new ArrayList<>();
/*
        System.out.print("n = ");
        this.n = scanner.nextInt();
        scanner.nextLine();
        System.out.print("input = ");
        this.startBoard = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
*/


        sortRows();

        generateStartBoard();
        printBoard(startBoard);
    }

    public void sortRows(){
        boolean[] taken = new boolean[this.n];
        ArrayList<Integer> collisionColumns = new ArrayList<>();

        for (int col = 0; col < startBoard.length; col++) {
            if (!taken[startBoard[col]]) taken[startBoard[col]] = true;
            else collisionColumns.add(col);
        }
        for (int row = 0; row < taken.length; row++) {
            if(taken[row] == false){
                startBoard[collisionColumns.remove(random.nextInt(collisionColumns.size()))] = row;
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

    public void generateStartBoard(){
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < this.n; i++) {
            numbers.add(i);
        }
        for (int i = 0; i < this.n; i++) {
            this.startBoard[i] = numbers.remove(random.nextInt(numbers.size()));
        }
    }

    public String solutionString(int[] array){
        String str = "";
        for (int i :
                array) {
            str+=i;
        }
        return str;
    }

    public void printArray(int[] array){
        System.out.print("Array:" );
        for (int i = 0; i < array.length; i++) {
            System.out.print((array[i]+1)+" ");
        }
        System.out.println('\n');
    }

    //generate random possible changes
    //can testing required to find a good solution.

    //moves a random queen to a random position in the same column.

    public void swapColumns(){
        int[] array;
        array = generatedsolution.clone();
        int first = random.nextInt(this.n);
        int second = random.nextInt(this.n);
        while(first == second){
            second = random.nextInt(this.n);
        }
        int temp = array[first];
        array[first] = array[second];
        array[second] = temp;
        //printBoard(array);
        this.newGeneratedsolution = array;
    }

    //finds the fitness based on how many queens are currently attacking eachother.
    //downside: the way it is not 3 queens all attacking each other is not that much worse than 2
    public int heuristic(int[] array){
        int cost = 0;
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
       System.out.println("cost: "+cost);
        return cost;

/*
        System.out.println("downRight");
        for (int i :
                downRight) {
            System.out.println(i);
        }
        System.out.println("upright");
        for (int i :
                upRight) {
            System.out.println(i);
        }
        System.out.println("horizontal");
        for (int i :
                horizontal) {
            System.out.println(i);
        }
        */
    }

    public void simulatedAnnealing(){
        if(heuristic(startBoard)== 0){
            String str = solutionString(startBoard);
            System.out.println("initial board is a solution!");
            printArray(startBoard);
            printBoard(startBoard);
            if(!solutionSet.contains(str))solutionSet.add(str);
            solutionsFound++;

        }
        generatedsolution = startBoard.clone();
        while(temp > 1){
            swapColumns();
            int cost = heuristic(generatedsolution);
            int newCost = heuristic(newGeneratedsolution);
            if(newCost == 0 ){
                String str = solutionString(newGeneratedsolution);
                //System.out.println("solution found!");

                if(!solutionSet.contains(str)) {

                    solutionSet.add(str);
                    //printArray(newGeneratedsolution);
                    //printBoard(newGeneratedsolution);
                }
                solutionsFound++;
                generatedsolution = startBoard.clone();

                continue;

            }
            if(cost > newCost){
                this.generatedsolution = newGeneratedsolution;
            }
            else{
                if ((Math.exp(-((newCost- cost)/temp)) > Math.random())){
                    this.generatedsolution = newGeneratedsolution;
                }
            }
            this.temp *= 1-this.cooling;
        }


        System.out.println("Solutions found: "+ solutionsFound);
        System.out.println("Unique solutions found: "+ solutionSet.size());
    }

    public static void main(String[] args) {
        SimulatedAnnealing p = new SimulatedAnnealing(16);
        p.printArray(p.startBoard);
        p.printBoard(p.startBoard);
        p.simulatedAnnealing();
    }
}