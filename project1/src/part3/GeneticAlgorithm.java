package part3;
import java.util.*;


@SuppressWarnings({"Duplicates", "FieldCanBeLocal", "PointlessBooleanExpression"})
public class GeneticAlgorithm {

    private boolean input;
    private Random random;
    private int n, popSize, numOfMutations, solutionsFound, runTime, arrayPrintIndexing, maxGenerations, maxSolutions, numOfParents;
    private int[] startBoard, parents;
    private Scanner reader;
    private int[][] populationArray, parents2;
    private HashSet<String> solutionSet;
    private String endCondition;


    private GeneticAlgorithm() {

        // ---- SETTINGS ---------------------------------
        this.input = false;
        this.n = 20;
        this.numOfMutations = 1;
        this.arrayPrintIndexing = 1;
        this.runTime = 10;
        this.maxSolutions = 10000;
        this.maxGenerations = 100000;
        this.numOfParents = 10;
        this.endCondition = "runTime"; //runTime, solutions, generations
        // -----------------------------------------------
        reader = new Scanner(System.in);
        this.startBoard = new int[n];

        if (input) {
            System.out.print("n = ");
            this.n = reader.nextInt();
            reader.nextLine();
            System.out.print("input = ");
            String initialBoardString = reader.nextLine();
            if (initialBoardString.length() > 0) {
                this.startBoard = processInput(initialBoardString);
            }
        }

        this.popSize = 5 * n; //5*n;
        this.solutionsFound = 0;
        this.solutionSet = new HashSet<>();
        this.random = new Random();
        this.populationArray = new int[popSize][n];

        System.out.println("Initial board:");
        printBoard(startBoard);
        sortRows(startBoard);
        System.out.println("\nSorted initial board:");
        printBoard(startBoard);
        populationArray = generatePopulation();
    }

    private void runAlgorithm() {
        long startTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();

        if (endCondition.equals("runTime")) {
            while (currentTime - startTime < runTime * 1000) {
                parents = selectMultipleParents(numOfParents);
                populationArray = breed(populationArray[parents[random.nextInt(parents.length)]], populationArray[random.nextInt(parents.length)]);
               // populationArray = breedMultiple(parents);
                currentTime = System.currentTimeMillis();
            }
        } else if (endCondition.equals("solutions")) {
            while (solutionSet.size() < maxSolutions) {
                parents = selectTwoParents();
                populationArray = breed(populationArray[parents[0]], populationArray[parents[1]]);
            }
        } else if (endCondition.equals("generations")) {
            int generation = 0;
            while (generation < maxGenerations) {
                parents = selectTwoParents();
                populationArray = breed(populationArray[parents[0]], populationArray[parents[1]]);
                generation++;
            }
        }
    }

    private int[] selectTwoParents() {
        int[] parentCostArray = new int[popSize];
        int[] bestParentIndex = new int[2];
        int currentBest = 4 * this.n;

        for (int i = 0; i < popSize; i++) {
            parentCostArray[i] = calculateCost2(populationArray[i]);
        }
        for (int i = 0; i < bestParentIndex.length; i++) {
            for (int j = 0; j < parentCostArray.length; j++) {
                if (parentCostArray[j] < currentBest) {
                    currentBest = parentCostArray[j];
                    bestParentIndex[i] = j;
                }
            }
            parentCostArray[bestParentIndex[i]] = 4 * n;
            currentBest = 4 * n;
        }
        return bestParentIndex;
    }

    private int[] selectMultipleParents(int numberOfParents) {
        int[] parentCostArray = new int[popSize];
        int[] bestParentIndex = new int[numberOfParents];
        int currentBest = 4 * this.n;

        for (int i = 0; i < popSize; i++) {
            parentCostArray[i] = calculateCost2(populationArray[i]);
        }
        for (int i = 0; i < bestParentIndex.length; i++) {
            for (int j = 0; j < parentCostArray.length; j++) {
                if (parentCostArray[j] < currentBest) {
                    currentBest = parentCostArray[j];
                    bestParentIndex[i] = j;
                }
            }
            parentCostArray[bestParentIndex[i]] = 4 * n;
            currentBest = 4 * n;
        }
        // printArray(bestParentIndex);
        return bestParentIndex;
    }

    private int[][] selectParents2(int[][] populationArray) {
        int[] parentCostArray = new int[popSize];
        int[][] parentCouplesArray = new int[popSize][2];
        int parentCost;
        int totalCost = 0;

        for (int i = 0; i < popSize; i++) {
            parentCost = calculateCost2(populationArray[i]);
            parentCostArray[i] = parentCost;
            totalCost += parentCost;
        }
//        printArray(parentCostArray);
        for (int i = 0; i < popSize; i++) {
            parentCostArray[i] = totalCost - parentCostArray[i];
        }
        totalCost = 0;
        for (int i = 0; i < popSize; i++) {
            totalCost += parentCostArray[i];
        }

        double parentPointer;
        for (int j = 0; j < popSize; j++) {
            parentPointer = random.nextDouble() * totalCost;
            for (int i = 0; i < popSize; i++) {

                //            System.out.println("TotalCost = " + totalCost + " InitCost = " + parentCostArray[i] + "ParentPointer = " + parentPointer);
                parentPointer -= (parentCostArray[i]);
                if (parentPointer < 0.0) {
                    parentCouplesArray[j][0] = i;
                    break;
                }
            }
            parentCouplesArray[j][1] = parentCouplesArray[j][0];
            while (parentCouplesArray[j][0] == parentCouplesArray[j][1]) {
                parentPointer = random.nextDouble() * totalCost;
                for (int i = 0; i < popSize; i++) {
                    parentPointer -= (parentCostArray[i]);
                    if (parentPointer < 0.0) {
                        parentCouplesArray[j][1] = i;
                        break;
                    }
                }
            }

        }
//        printArray(parentCouplesArray[0]);
        return parentCouplesArray;
    }

    private int[][] breed(int[] first, int[] second) {
        int[][] newPopulationArray = new int[popSize][n];
        for (int i = 0; i < popSize; i++) {
//            int[] child = singlePointCrossover(first, second, 0.5);
            int[] child = singlePointCrossover(first, second, random.nextDouble());
//            int[] child = first.clone();
            sortRows(child);
            child = mutate(child);
            newPopulationArray[i] = child;
        }
        return newPopulationArray;
    }

    private int[][] breedMultiple(int[] parentIndexArray) {


        int[][] newPopulationArray = new int[popSize][n];
        for (int i = 0; i < popSize; i++) {
//            int[] child = singlePointCrossover(first, second, 0.5);
            int parent1 = parentIndexArray[random.nextInt(parents.length)];
            int parent2 = parentIndexArray[random.nextInt(parents.length)];

            while (parent2 == parent1){
                parent2 = parentIndexArray[random.nextInt(parents.length)];
            }
            int[] child = singlePointCrossover(populationArray[parent1], populationArray[parent2], random.nextDouble());
//            int[] child = first.clone();
            sortRows(child);
            child = mutate(child);
            newPopulationArray[i] = child;
        }
        return newPopulationArray;
    }

    private void breed2() {

        for (int i = 0; i < popSize; i++) {
            int[] child = new int[n];
//
//            //optimise!!! use two for loops or copy first, insert second.
            for (int j = 0; j < this.n; j++) {
                if (j <= (n - 1) / 2) child[j] = populationArray[parents2[i][0]][j];

                else child[j] = populationArray[parents2[i][0]][j];
            }
            sortRows(child);
            child = mutate(child);
            populationArray[i] = child;
        }
    }

    private int[] singlePointCrossover(int[] first, int[] second, double crossPointPercent) {
        int[] child = new int[n];
        int i = 0;
        while (i < crossPointPercent * n) {
            child[i] = first[i];
            i++;
        }
        while (i < n) {
            child[i] = second[i];
            i++;
        }
        return child;
    }

    private int[][] generatePopulation() {
        int[][] newPopulationArray = new int[popSize][n];
        for (int i = 0; i < popSize; i++) {
            newPopulationArray[i] = mutate(startBoard);
        }
        System.out.println();
        return newPopulationArray;
    }

    private int[] mutate(int[] oldArray) {
        int[] array = oldArray.clone();
        for (int i = 0; i < this.numOfMutations; i++) {
            swapColumns(array);
        }
        return array;
    }

    private void swapColumns(int[] array) {
        int first = random.nextInt(array.length);
        int second = random.nextInt(array.length);
        while (first == second) {
            second = random.nextInt(array.length);
        }
        int temp = array[first];
        array[first] = array[second];
        array[second] = temp;
    }

    private void sortRows(int[] array) {
        boolean[] taken = new boolean[this.n];
        ArrayList<Integer> collisionColumns = new ArrayList<>();

        for (int col = 0; col < array.length; col++) {
            if (!taken[array[col]]) taken[array[col]] = true;
            else collisionColumns.add(col);
        }
        for (int row = 0; row < taken.length; row++) {
            if (taken[row] == false) {
                array[collisionColumns.remove(random.nextInt(collisionColumns.size()))] = row;
            }
        }
    }

    private int calculateCostWithRows(int[] array) {
        int cost = 0;
        int downRight[] = new int[2 * this.n - 1];
        int upRight[] = new int[2 * this.n - 1];
        int horizontal[] = new int[array.length];
        //finds queen and increment its row and diagonal values.
        for (int x = 0; x < array.length; x++) {
            downRight[array[x] + x]++;
            upRight[x - array[x] + this.n - 1]++;
            horizontal[array[x]]++;
        }
        //adds 1 to cost if queens are attacking each other
        for (int i = 0; i < downRight.length; i++) {
            if (downRight[i] > 0) cost += downRight[i] - 1;
            if (upRight[i] > 0) cost += upRight[i] - 1;
        }
        for (int i = 0; i < horizontal.length; i++) {
            if (horizontal[i] > 0) cost += horizontal[i] - 1;
        }
        if (cost == 0) {
            solutionSet.add(arrayToString(array));
            solutionsFound++;
        }
        return cost;
    }

    private int calculateCost2(int[] array) {
        int cost = 0;
        int downRight[] = new int[2 * this.n - 1];
        int upRight[] = new int[2 * this.n - 1];
        //finds queen and increment its row and diagonal values.
        for (int x = 0; x < array.length; x++) {
            downRight[array[x] + x]++;
            upRight[x - array[x] + this.n - 1]++;
        }
        //adds 1 to cost if queens are attacking each other
        for (int i = 0; i < downRight.length; i++) {
            if (downRight[i] > 0) cost += downRight[i] - 1;
            if (upRight[i] > 0) cost += upRight[i] - 1;
        }
        if (cost == 0) {
            solutionSet.add(arrayToString(array));
            solutionsFound++;
        }
        return cost;
    }

    private void printBoard(int[] array) {
        printArray(array);
        for (int i = this.n - 1; i > -1; i--) {
            for (int j = 0; j < this.n; j++) {
                if (array[j] == i) System.out.print("X ");
                else System.out.print("- ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void printArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print((array[i] + arrayPrintIndexing) + " ");
        }
        System.out.println();
    }

    private void printPopulation(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            printArray(array[i]);
            printBoard(array[i]);
        }
    }

    private String arrayToString(int[] array) {
        String str = "";
        for (int i : array) str += i;
        return str;
    }

    private int[] processInput(String input) {
        int[] array = Arrays.stream(input.split(" ")).mapToInt(Integer::parseInt).toArray();
        for (int i = 0; i < array.length; i++) {
            array[i]--;
        }
        return array;
    }

    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm();
        long startTime = System.currentTimeMillis();
        ga.runAlgorithm();
        long endTime = System.currentTimeMillis();
        double executionTime = endTime - startTime;
        System.out.println("n = " + ga.n);
        System.out.println("popSize = " + ga.popSize);
        System.out.println("numOfMutations = " + ga.numOfMutations);
        System.out.println("\nSolutions found: " + ga.solutionsFound);
        System.out.println("Unique solutions found: " + ga.solutionSet.size());
        System.out.println("Execution time: " + executionTime / 1000 + " seconds");
    }
}
