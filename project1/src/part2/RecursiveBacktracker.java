package part2;
import java.util.Scanner;


public class RecursiveBacktracker {

    private boolean stepByStep;
    private boolean solutionsToConsole;
    private int n;
    private int solutionNum = 0;
    private Board board;
    private Scanner reader;

    private RecursiveBacktracker() {

        // ---- SETTINGS ---------------------------------
        stepByStep = false;
        solutionsToConsole = false;
        // -----------------------------------------------

        System.out.println("stepByStep = " + stepByStep);
        System.out.println("solutionsToConsole = " + solutionsToConsole);
        System.out.println("");
        reader = new Scanner(System.in);
        System.out.print("n:");
        n = reader.nextInt();
        reader.nextLine();
        System.out.print("Initial queen positions:");
        String initialQueenStr = reader.nextLine();
        board = new Board(n, initialQueenStr);
    }

    private void findSolutions(int col){
        if (stepByStep && solutionNum == 0){
            board.printBoard();
            System.out.println("Press Enter to continue...");
            reader.nextLine();
        }
        if (col == n){
            if (solutionsToConsole) board.printList(board.queenList);
            solutionNum += 1;
        }
        else {
            for (int row = 0; row < n; row++) {
                if(board.placeQueen(col, row)) {
                    findSolutions(col+1);
                    board.removeQueen(col, row);
                }
            }
        }
    }

    public static void main(String[] args) {
        RecursiveBacktracker recBac = new RecursiveBacktracker();
        long startTime = System.currentTimeMillis();
        recBac.findSolutions(recBac.board.startCol);
        long endTime = System.currentTimeMillis();
        double executionTime = endTime - startTime;
        System.out.println("\nNumber of solutions: " + recBac.solutionNum);
        System.out.println("Execution time: " + executionTime/1000 + " seconds");
    }
}