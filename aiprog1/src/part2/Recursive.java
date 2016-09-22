package part2;


import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by xcr on 23/08/16.
 */
public class Recursive {


    private int legalSolutions;
    private int[] queenArray;
    private boolean[] horizontal, upRight, downRight;
    private int n,start;


    public Recursive(){
        Scanner scanner = new Scanner(System.in);
        legalSolutions = 0;


        while(true){
            System.out.print("n = ");
            this.n = scanner.nextInt();
            horizontal = new boolean[n];
            downRight = new boolean[2*n - 1];
            upRight = new boolean[2*n - 1];
            System.out.print("input = ");
            scanner.nextLine();
            this.queenArray = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            if(startInputCheck(queenArray)) break;
            else System.out.println("Invalid input. Try again");
        }
        backTrackAll(start);



    }

    public boolean startInputCheck(int[] array){
        boolean first = true;
        if (array.length != n){
            System.out.println("Wrong input length");
            return false;
        }
        for (int i = 0; i < array.length; i++) {
            array[i] -= 1;
            if (array[i] == -1){
                if(first== true){
                    this.start = i;
                    first = false;
                }

            }
            else{
                if(!legalPlacement(array[i],i)) {
                    System.out.println("ILLEGAL PLACEMENT");
                    return false;
                }else{
                    downRight[array[i]+i] = true;
                    upRight[i-array[i]+this.n-1]= true;
                    horizontal[array[i]] = true;
                }
            }
        }
    return true;
    }
//returns true of the possition is not under attack, else false

    public boolean legalPlacement(int y, int x) {

        if(downRight[y+x] == false && upRight[x-y+this.n-1]== false && horizontal[y] == false){
            return true;
        }
        else {
            return false;
        }

    }

//prints the board
    public void printBoard() {
        String str;
        for (int i = 0; i < n; i++) {
            str = "";
            for (int j = 0; j < n; j++) {
                if(queenArray[j] == i) str+= 'Q';
                else str+='X';
            }
            System.out.println(str);

        }
        System.out.println();
    }
    public void printQueenArray(){
        for (int nr :
                queenArray) {
            System.out.println(nr);
        }
    }

    public void removeQueen(int y,int x){
        downRight[y + x] = false;
        upRight[x - y + this.n - 1] = false;
        horizontal[y] = false;

    }
    //places queen if possible
    public void putQueen(int y, int x){

        downRight[y+x] = true;
        upRight[x-y+this.n-1]= true;
        horizontal[y] = true;
        queenArray[x] = y;

    }
    public void printChecks(){
        System.out.println("upright");
        for (int i = 0; i < upRight.length; i++) {
            System.out.println(upRight[i]);
        }
        System.out.println("downright");
        for (int i = 0; i < downRight.length; i++) {
            System.out.println(downRight[i]);
        }
        System.out.println("hori");
        for (int i = 0; i < horizontal.length; i++) {
            System.out.println(horizontal[i]);
        }
    }

    //backtrack for finding every solution
    public void backTrackAll(int x){
        for (int i = 0; i < this.n; i++) {
            if (legalPlacement(i, x)) {
                putQueen(i, x);
               // printBoard();

                //System.out.println();

                if (x < this.n - 1) {
                    backTrackAll(x + 1);
                } else if (x == this.n - 1) {
                    this.legalSolutions++;
                }
                removeQueen(i,x);
            }
        }

    }


    public static void main(String[] args) {
        Recursive board = new Recursive();
        long start = System.currentTimeMillis();

        long end = System.currentTimeMillis();
        double time = end - start;
        System.out.println(time);
        System.out.println(board.legalSolutions);
    }
}