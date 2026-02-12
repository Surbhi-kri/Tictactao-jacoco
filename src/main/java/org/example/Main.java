package org.example;



import java.util.Random;
import java.util.Scanner;

public class Main {
    static char[][] tictac = new char[3][3];
    static Scanner sc = new Scanner(System.in);
    static Random random = new Random();

    public static void main(String[] args) {
        while (true) {
            System.out.print("Input command: ");
            String line = sc.nextLine().trim();
            String[] parts = line.split("\\s+");

            if (parts[0].equals("exit")) {
                break;
            } else if (parts[0].equals("start")) {
                if (parts.length != 3) {
                    System.out.println("Bad parameters!");
                    continue;
                }

                String playerX = parts[1];
                String playerO = parts[2];

                assignValue(tictac);

                switch (playerX + "-" + playerO) {
                    case "easy-easy":
                    case "easy-user":
                    case "user-easy":
                    case "user-user":
                    case "user-medium":
                    case "medium-user":
                    case "medium-medium":
                    case "hard-user":
                    case "user-hard":
                    case "hard-hard":
                        bothAI(playerX, playerO);
                        break;
                    default:
                        System.out.println("Bad parameters!");
                }
            } else {
                System.out.println("Bad parameters!");
            }
        }
    }


    static void bothAI(String typeX, String typeO) {
        printBoard(tictac);
        while (true) {
            makeMove(typeX, 'X');
            printBoard(tictac);
            if (checkGameEnd('X'))
                break;

            makeMove(typeO, 'O');
            printBoard(tictac);
            if (checkGameEnd('O'))
                break;
        }
    }

    static void makeMove(String type, char player) {
        switch (type) {
            case "user":
                userMove(player);
                break;
            case "easy":
                easyMove(player);
                break;
            case "medium":
                mediumMove(player);
                break;
            case "hard":
                hardMove(player);
                break;
        }
    }

    static boolean checkGameEnd(char player) {
        if (win(tictac, player)) {
            System.out.println(player + " wins");
            return true;
        }
        if (isDraw()) {
            System.out.println("Draw");
            return true;
        }
        return false;
    }

    //  USER
    static void userMove(char player) {
        while (true) {
            System.out.print("Enter the coordinates: ");
            String[] parts = sc.nextLine().split("\\s+");

            if (parts.length != 2) {
                System.out.println("You should enter numbers!");
                continue;
            }

            int r, c;
            try {
                r = Integer.parseInt(parts[0]);
                c = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("You should enter numbers!");
                continue;
            }

            if (r < 1 || r > 3 || c < 1 || c > 3) {
                System.out.println("Coordinates should be from 1 to 3!");
                continue;
            }

            if (tictac[r - 1][c - 1] != ' ') {
                System.out.println("This cell is occupied! Choose another one!");
                continue;
            }

            tictac[r - 1][c - 1] = player;
            break;
        }
    }

    //  EASY
    static void easyMove(char player) {
        System.out.println("Making move level \"easy\"");
        while (true) {
            int r = random.nextInt(3);
            int c = random.nextInt(3);
            if (tictac[r][c] == ' ') {
                tictac[r][c] = player;
                break;
            }
        }
    }

    //  MEDIUM
    static void mediumMove(char player) {
        System.out.println("Making move level \"medium\"");

        if (tryWinOrBlock(player))
            return;

        char opponent = player == 'X' ? 'O' : 'X';
        if (tryWinOrBlock(opponent, player))
            return;

        easyMove(player);
    }

    static boolean tryWinOrBlock(char p) {
        return tryWinOrBlock(p, p);
    }

    static boolean tryWinOrBlock(char check, char place) {
        for (int i = 0; i < 3; i++) {
            if (count(check, i, 0, i, 1, i, 2) == 2)
                for (int j = 0; j < 3; j++)
                    if (tictac[i][j] == ' ') {
                        tictac[i][j] = place;
                        return true;
                    }

            if (count(check, 0, i, 1, i, 2, i) == 2)
                for (int j = 0; j < 3; j++)
                    if (tictac[j][i] == ' ') {
                        tictac[j][i] = place;
                        return true;
                    }
        }

        if (count(check, 0, 0, 1, 1, 2, 2) == 2)
            for (int i = 0; i < 3; i++)
                if (tictac[i][i] == ' ') {
                    tictac[i][i] = place;
                    return true;
                }

        if (count(check, 0, 2, 1, 1, 2, 0) == 2)
            for (int i = 0; i < 3; i++)
                if (tictac[i][2 - i] == ' ') {
                    tictac[i][2 - i] = place;
                    return true;
                }

        return false;
    }

    // HARD
    static void hardMove(char player) {
        System.out.println("Making move level \"hard\"");

        int bestScore = Integer.MIN_VALUE;
        int bestR = -1, bestC = -1;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (tictac[i][j] == ' ') {
                    tictac[i][j] = player;
                    int score = minimax(false, player);
                    tictac[i][j] = ' ';
                    if (score > bestScore) {
                        bestScore = score;
                        bestR = i;
                        bestC = j;
                    }
                }

        tictac[bestR][bestC] = player;
    }

    static int minimax(boolean isMax, char ai) {
        char human = ai == 'X' ? 'O' : 'X';

        if (win(tictac, ai))
            return 10;
        if (win(tictac, human))
            return -10;
        if (isDraw())
            return 0;

        int best = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (tictac[i][j] == ' ') {
                    tictac[i][j] = isMax ? ai : human;
                    int score = minimax(!isMax, ai);
                    tictac[i][j] = ' ';
                    best = isMax ? Math.max(best, score) : Math.min(best, score);
                }
        return best;
    }


    static int count(char p, int r1, int c1, int r2, int c2, int r3, int c3) {
        int cnt = 0;
        if (tictac[r1][c1] == p)
            cnt++;
        if (tictac[r2][c2] == p)
            cnt++;
        if (tictac[r3][c3] == p)
            cnt++;
        return cnt;
    }

    static void assignValue(char[][] b) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                b[i][j] = ' ';
    }

    static void printBoard(char[][] b) {
        System.out.println("---------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++)
                System.out.print(b[i][j] + " ");
            System.out.println("|");
        }
        System.out.println("---------");
    }

    static boolean isDraw() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (tictac[i][j] == ' ')
                    return false;
        return true;
    }

    static boolean win(char[][] b, char p) {
        for (int i = 0; i < 3; i++) {
            if (b[i][0] == p && b[i][1] == p && b[i][2] == p)
                return true;
            if (b[0][i] == p && b[1][i] == p && b[2][i] == p)
                return true;
        }
        return (b[0][0] == p && b[1][1] == p && b[2][2] == p) ||
                (b[0][2] == p && b[1][1] == p && b[2][0] == p);
    }
}

