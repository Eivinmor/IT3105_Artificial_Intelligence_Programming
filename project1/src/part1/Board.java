package part1;


@SuppressWarnings("Duplicates")
class Board {

    private boolean[] rowList, upDiagList, dnDiagList;
    private int n;
    int[] queenList;
    int startCol;

    Board(int n, String initialQueenStr) {
        this.n = n;
        this.rowList = new boolean[n];
        this.upDiagList = new boolean[2*n-1];
        this.dnDiagList = new boolean[2*n-1];
        int[] initialQueenList = strToList(initialQueenStr);

        startCol = getStartCol(initialQueenStr);
        queenList = new int[n];
        System.arraycopy(initialQueenList, 0, queenList, 0, initialQueenList.length);
        System.out.println(); printBoard();

        if(!processInput(queenList)) {
            System.out.println("Invalid input. Queen(s) threatened.");
            System.exit(1);
        }
    }

    private int[] strToList(String str) {
        String[] charList = str.replace("0", "").split(" ");
        int[] intList = new int[n];

        for (int i = 0; i < charList.length; i++) {
            if (!charList[i].isEmpty()) {
                intList[i] = Integer.parseInt(charList[i]);
            }
        }
        return intList;
    }

    private boolean processInput(int[] queenList){
        int col = 0;
        int row = queenList[col] - 1;

        while (row > -1) {
            if (rowList[row] || upDiagList[col + row] || dnDiagList[n-1+row-col]) {
                return false;
            }
            else {
                this.rowList[row] = true;
                this.upDiagList[col + row] = true;
                this.dnDiagList[n - 1 + row - col] = true;
            }
            col += 1;
            row = queenList[col] - 1;
        }
        return true;
    }

    private int getStartCol(String initialQueenStr){
        String shortString = initialQueenStr.replace("0", "").replace(" ", "");
        if (shortString.isEmpty()) {
            return 0;
        }
        else {
            return shortString.length();
        }
    }

    boolean placeQueen(int col, int row) {
        if (!(rowList[row] || upDiagList[col + row] || dnDiagList[n-1+row-col])){
            queenList[col] = row;
            this.rowList[row] = true;
            this.upDiagList[row + col] = true;
            this.dnDiagList[n - 1 + row - col] = true;
            queenList[col] = row + 1;
            return true;
        }
        return false;
    }

    void removeQueen(int col, int row) {
        this.rowList[row] = false;
        this.upDiagList[row + col] = false;
        this.dnDiagList[n - 1 + row - col] = false;
        queenList[col] = 0;
    }

    void printList(int[] list){
        for(int e : list) {
            System.out.print(e + " ");
        }
        System.out.println();
    }

    void printBoard() {
        printList(queenList);
        for (int i = n-1; i > -1; i--) {
            for (int j = 0; j < n; j++) {
                if (queenList[j] == i+1) System.out.print("X ");
                else System.out.print("- ");
            }
            System.out.println();
        }
        System.out.println();
    }
}