package part1;
import java.util.Scanner;


public class RecursiveBacktracker {

    private boolean stepByStep;
    private int n;
    private Board board;
    private Scanner reader;

    private RecursiveBacktracker() {

        // ---- SETTINGS ---------------------------------
        stepByStep = false;
        // -----------------------------------------------

        System.out.println("stepByStep = " + stepByStep);
        System.out.println("");
        reader = new Scanner(System.in);
        System.out.println("n=8");
        n = 8;
        System.out.print("Initial queen positions:");
        String initialQueenStr = reader.nextLine();
        board = new Board(n, initialQueenStr);
    }

    private boolean findSolutions(int col){
        if (stepByStep){
            board.printBoard();
            System.out.println("Press Enter to continue...");
            reader.nextLine();
        }
        if (col == n) {
            System.out.println("Solution found:");
            board.printBoard();
            return true;
        }
        else {
            for (int row = 0; row < n; row++) {
                if (board.placeQueen(col, row)) {
                    if (findSolutions(col + 1)) return true;
                    board.removeQueen(col, row);
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        RecursiveBacktracker recBac = new RecursiveBacktracker();
        long startTime = System.currentTimeMillis();
        recBac.findSolutions(recBac.board.startCol);
        long endTime = System.currentTimeMillis();
        double executionTime = endTime - startTime;
        System.out.println("Execution time: " + executionTime/1000 + " seconds");
    }
}