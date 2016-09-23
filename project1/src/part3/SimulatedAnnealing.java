package part3;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Eirikpc on 21-Sep-16.
 */
public class SimulatedAnnealing {

    private int[] queenArray;
    private int n;
    Random random;
    private double temp;
    private double cooling;
    private int[] generatedsolution;
    private int solutionsFound;
    private ArrayList<String> solutionSet;

    public SimulatedAnnealing(int n){
        random = new Random();
        this.n = n;
        temp = 1000;
        cooling = 0.05;
        solutionsFound = 0;
        solutionSet = new ArrayList<>();
        this.queenArray = new int[n];
        for (int i = 0; i < n; i++) {
                int randint = random.nextInt(n);
                queenArray[i] = randint;
        }
    }
    public void printBoard(int[] array) {

        String str;
        for (int i = 0; i < this.n; i++) {
            str = "";
            for (int j = 0; j < this.n; j++) {
                if(array[j] == i) str+='1';
                else str+='0';
            }
            System.out.println(str);
        }
        System.out.println();
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
        System.out.println("Array:");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]+" ");
        }
        System.out.println('\n');
    }

    //generate random possible changes
    //can testing required to find a good solution.

    //moevs a random queen to a random position in the same column.

    public void generateBoardRandomPosition(){
        int[] array;
        array = queenArray.clone();
        array[random.nextInt(this.n)] = random.nextInt(this.n);
        this.generatedsolution = array;
    }

    //moves a random queen one step in the same column.
    public void generateBoardsOneStep(){

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
        if(heuristic(queenArray)== 0){
            String str = solutionString(queenArray);
            System.out.println("initial board is a solution!");
            printArray(queenArray);
            printBoard(queenArray);
            if(!solutionSet.contains(str))solutionSet.add(str);
            solutionsFound++;

        }
        while(temp > 0){
            generateBoardRandomPosition();
            int cost = heuristic(queenArray);
            int newCost = heuristic(generatedsolution);
            if(newCost == 0 ){
                String str = solutionString(generatedsolution);
                System.out.println("solution found!");
                printArray(generatedsolution);
                printBoard(generatedsolution);
                if(!solutionSet.contains(str))solutionSet.add(str);
                solutionsFound++;

            }
            if(cost > newCost){
                this.queenArray = generatedsolution;
            }
            else{
                if ((Math.exp(-((cost - newCost)/temp)) > Math.random())){
                    this.queenArray = generatedsolution;
                }
            }
            this.temp -= this.cooling;
        }


        System.out.println("solutions found: "+ solutionsFound);
        System.out.println("Unique solutions found: "+ solutionSet.size());


    }

    public static void main(String[] args) {
        SimulatedAnnealing p = new SimulatedAnnealing(5);
        p.printArray(p.queenArray);
        p.printBoard(p.queenArray);
        p.generateBoardRandomPosition();
        p.simulatedAnnealing();
    }

}
