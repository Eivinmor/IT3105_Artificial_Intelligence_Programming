package part3;
import java.util.*;


@SuppressWarnings({"Duplicates", "ConstantConditions"})
public class SimulatedAnnealing {

    private int[] startBoard;
    private int n, solutionsFound, arrayPrintIndexing, maxUniqueSolutions;
    private Random random;
    private double temperature, cooling, minTemperature;
    private HashSet<String> solutionSet;
    private Scanner reader;
    private boolean input;

    // usually finds all solutions for up to n=10 with temp = 20000000, cooling = 0.0000005
    private SimulatedAnnealing(){

        // ---- SETTINGS ---------------------------------
        this.input = true;
        this.n = 10;
        this.temperature = 1; // 1
        this.cooling = 0.0000005; // 0.0000005
        this.arrayPrintIndexing = 1;
        this.minTemperature = 0.00000005; // 0.000000005;
        this.maxUniqueSolutions = Integer.MAX_VALUE;
        // default settings gives 40% chance to jump to 1 less cost
        // -----------------------------------------------

        solutionsFound = 0;
        random = new Random();
        solutionSet = new HashSet<>();
        reader = new Scanner(System.in);
        this.startBoard = new int[n];
        if (input) {
            System.out.print("n = ");
            this.n = reader.nextInt();
            reader.nextLine();
            System.out.print("Initial queen positions:");
            startBoard = processInput(reader.nextLine());
        }
        else generateStartBoard();
        System.out.println();
        System.out.println("Initial board:");
        printBoard(startBoard);
        System.out.println();
        System.out.println("Board after sortRowCollision:");
        sortRowCollisions(startBoard);
        printBoard(startBoard);
    }

    private void runAlgorithm(){
        System.out.println("Running simulated annealing...");

        int[] currentBoard = startBoard.clone();
        if (calculateCost(currentBoard) == 0) addSolution(currentBoard);
        
        int[] swappedBoard = swapColumns(currentBoard);

        while(temperature > minTemperature && solutionSet.size() < maxUniqueSolutions){
            int cost = calculateCost(currentBoard);
            int newCost = calculateCost(swappedBoard);
            if (newCost == 0 ){
                addSolution(swappedBoard);
            }
            if (acceptanceFunction(cost, newCost)){
                currentBoard = swappedBoard;
            }
            this.temperature *= 1-this.cooling;
            swappedBoard = swapColumns(currentBoard);

        }
    }

    private void addSolution(int[]board) {
        String str = arrayToString(board);
        solutionSet.add(str);
        solutionsFound++;
    }

    private boolean acceptanceFunction(int cost, int newCost) {
        if (cost > newCost) return true;
        else {
            double rand = random.nextDouble();
            if ((Math.exp(-((newCost - cost)/temperature)) > rand)){
                return true;
            }
        }
        return false;
    }

    //finds the cost based on how many queens are currently attacking each other.
    //downside: the way it is not 3 queens all attacking each other is not that much worse than 2
    private int calculateCost(int[] array){
        int cost = 0;
        int downRight[] = new int[2*this.n-1];
        int upRight[] = new int[2*this.n-1];

        //finds queen and increment its row and diagonal values.
        for (int x = 0; x < array.length; x++) {
            downRight[array[x]+x]++;
            upRight[x-array[x]+this.n-1]++;

        }
        //adds 1 to cost if queens are attacking each other
        for (int i = 0; i < downRight.length; i++) {
            if (downRight[i] > 0) cost += downRight[i] - 1;
            if (upRight[i] > 0) cost += upRight[i] - 1;
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
            System.out.print((i+arrayPrintIndexing) + " ");
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
        SimulatedAnnealing sa = new SimulatedAnnealing();
        long startTime = System.currentTimeMillis();
        sa.runAlgorithm();
        long endTime = System.currentTimeMillis();
        double executionTime = endTime - startTime;
        System.out.println("n = " + sa.n);
        System.out.println("Solutions found: "+ sa.solutionsFound);
        System.out.println("Unique solutions found: "+ sa.solutionSet.size());
        System.out.println("Execution time: " + executionTime/1000 + " seconds");
    }
}