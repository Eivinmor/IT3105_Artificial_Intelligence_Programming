package part3;
import java.util.*;


@SuppressWarnings({"Duplicates", "ConstantConditions"})
public class SimulatedAnnealing {

    private int[] startBoard;
    private int n, solutionsFound;
    private Random random;
    private double temperature, cooling;
    private HashSet<String> solutionSet;

    // usually finds all solutions for up to n=10 with temp = 20000000, cooling = 0.0000005
    private SimulatedAnnealing(){

        // ---- SETTINGS ---------------------------------
        boolean input = true;
        n = 10;
        temperature = 20000000;
        cooling = 0.0000005;
        // -----------------------------------------------

        solutionsFound = 0;
        random = new Random();
        solutionSet = new HashSet<>();
        Scanner reader = new Scanner(System.in);
        this.startBoard = new int[n];
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
        }

        sortRowCollisions(startBoard);
    }

    private void runAlgorithm(){
        System.out.println();
        printBoard(startBoard);
        System.out.println("Running simulated annealing...");

        int[] currentBoard = startBoard.clone();
        int[] swappedBoard = swapColumns(currentBoard);

        while(temperature > 1){
            int cost = calculateFitness(currentBoard);
            int newCost = calculateFitness(swappedBoard);
            if (newCost == 0 ){
                String str = arrayToString(swappedBoard);
                if (!solutionSet.contains(str)) solutionSet.add(str);
                solutionsFound++;
            }
            // Acceptance function
            if (cost > newCost) currentBoard = swappedBoard;
            else {
                if ((Math.exp(-((newCost- cost)/temperature)) > Math.random())){
                    currentBoard = swappedBoard;
                }
            }
            this.temperature *= 1-this.cooling;
            swappedBoard = swapColumns(currentBoard);
        }
    }

    //finds the fitness based on how many queens are currently attacking each other.
    //downside: the way it is not 3 queens all attacking each other is not that much worse than 2
    private int calculateFitness(int[] array){
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
        return cost;
    }

    private int[] swapColumns(int[] oldArray){
        int[] newArray = oldArray.clone();
        int first = random.nextInt(this.n);
        int second = random.nextInt(this.n);
        while (first == second){
            second = random.nextInt(this.n);
        }
        int temp = newArray[first];
        newArray[first] = newArray[second];
        newArray[second] = temp;
        return newArray;
    }

    private void sortRowCollisions(int[] array){
        boolean[] taken = new boolean[this.n];
        ArrayList<Integer> collisionColumns = new ArrayList<>();
        for (int col = 0; col < array.length; col++) {
            if (!taken[array[col]]) taken[array[col]] = true;
            else collisionColumns.add(col);
        }
        for (int row = 0; row < taken.length; row++) {
            if(!taken[row]){
                array[collisionColumns.remove(random.nextInt(collisionColumns.size()))] = row;
            }
        }
    }

    private int[] processInput(String input) {
        int[] array = Arrays.stream(input.split(" ")).mapToInt(Integer::parseInt).toArray();
        for (int i = 0; i < array.length; i++) {
            array[i]--;
        }
        return array;
    }

    private void generateStartBoard(){
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < this.n; i++) numbers.add(i);
        for (int i = 0; i < this.n; i++) {
            this.startBoard[i] = numbers.remove(random.nextInt(numbers.size()));
        }
    }

    private String arrayToString(int[] array){
        String str = "";
        for (int i : array) str += i;
        return str;
    }

    private void printArray(int[] array){
        for (int i :  array) {
            System.out.print((i+1) + " ");
        }
        System.out.println();
    }

    private void printBoard(int[] queenArray) {
        printArray(queenArray);
        for (int i = n-1; i > -1; i--) {
            for (int j = 0; j < n; j++) {
                if (queenArray[j] == i) System.out.print("X ");
                else System.out.print("- ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        SimulatedAnnealing simAn = new SimulatedAnnealing();
        long startTime = System.currentTimeMillis();
        simAn.runAlgorithm();
        long endTime = System.currentTimeMillis();
        double executionTime = endTime - startTime;
        System.out.println("\nSolutions found: "+ simAn.solutionsFound);
        System.out.println("Unique solutions found: "+ simAn.solutionSet.size());
        System.out.println("Execution time: " + executionTime/1000 + " seconds");
    }
}