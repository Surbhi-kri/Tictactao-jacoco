package org.example;

import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream outputStream;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        // Reset board before each test
        Main.assignValue(Main.tictac);

        // Capture System.out
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
    @Test
    void testMain_ExitImmediately() {
        // Simulate user typing "exit"
        String input = "exit\n";
        Main.sc = new java.util.Scanner(new ByteArrayInputStream(input.getBytes()));

        Main.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Input command:"));
        // Nothing else should happen since we exited immediately
    }

    @Test
    void testMain_BadParameters() {
        // Simulate user typing invalid commands then exit
        String input = "hello\nstart easy\nstart easy hard\nexit\n";
        Main.sc = new java.util.Scanner(new ByteArrayInputStream(input.getBytes()));

        Main.main(new String[]{});

        String output = outContent.toString();
        // Should print "Bad parameters!" for invalid commands
        int badParamsCount = output.split("Bad parameters!").length - 1;
        assertEquals(3, badParamsCount); // hello, start easy, start easy hard
    }

    @Test
    void testMain_StartEasyEasyAndExit() {
        // Simulate "start easy easy" followed by "exit"
        String input = "start easy easy\nexit\n";
        Main.sc = new java.util.Scanner(new ByteArrayInputStream(input.getBytes()));

        Main.main(new String[]{});

        String output = outContent.toString();
        // Should include game board printout
        assertTrue(output.contains("---------"));
        assertTrue(output.contains("Making move level \"easy\""));
    }
    @Test
    void testUserMove_ValidInput() {
        // Simulate user input "2 3"
        String input = "2 3\n";
        Main.sc = new java.util.Scanner(new ByteArrayInputStream(input.getBytes()));

        Main.userMove('X');

        // Check the move was made correctly
        assertEquals('X', Main.tictac[1][2]);  // 2nd row, 3rd col
    }

    @Test
    void testUserMove_InvalidNumberInput() {
        // Simulate invalid input followed by a valid one
        String input = "a b\n1 1\n";
        Main.sc = new java.util.Scanner(new ByteArrayInputStream(input.getBytes()));

        Main.userMove('O');

        // Check the move was placed correctly after retry
        assertEquals('O', Main.tictac[0][0]);

        // Check that the invalid input message was printed
        String output = outContent.toString();
        assertTrue(output.contains("You should enter numbers!"));
    }

    @Test
    void testUserMove_CoordinatesOutOfRange() {
        // Input out-of-range coordinates first
        String input = "0 4\n3 2\n";
        Main.sc = new java.util.Scanner(new ByteArrayInputStream(input.getBytes()));

        Main.userMove('X');

        assertEquals('X', Main.tictac[2][1]);  // 3rd row, 2nd col
        assertTrue(outContent.toString().contains("Coordinates should be from 1 to 3!"));
    }

    @Test
    void testUserMove_CellOccupied() {
        // Mark cell (1,1) as occupied
        Main.tictac[0][0] = 'O';
        String input = "1 1\n2 2\n";
        Main.sc = new java.util.Scanner(new ByteArrayInputStream(input.getBytes()));

        Main.userMove('X');

        assertEquals('X', Main.tictac[1][1]); // 2nd row, 2nd col
        assertTrue(outContent.toString().contains("This cell is occupied! Choose another one!"));
    }
    @Test
    void testAssignValue() {
        // Fill board with X
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                Main.tictac[i][j] = 'X';

        Main.assignValue(Main.tictac);

        // Check all are empty
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                assertEquals(' ', Main.tictac[i][j]);
    }

    @Test
    void testWinRows() {
        Main.assignValue(Main.tictac);
        Main.tictac[0][0] = Main.tictac[0][1] = Main.tictac[0][2] = 'X';
        assertTrue(Main.win(Main.tictac, 'X'));
        assertFalse(Main.win(Main.tictac, 'O'));
    }

    @Test
    void testWinColumns() {
        Main.assignValue(Main.tictac);
        Main.tictac[0][0] = Main.tictac[1][0] = Main.tictac[2][0] = 'O';
        assertTrue(Main.win(Main.tictac, 'O'));
    }

    @Test
    void testWinDiagonals() {
        Main.assignValue(Main.tictac);
        Main.tictac[0][0] = Main.tictac[1][1] = Main.tictac[2][2] = 'X';
        assertTrue(Main.win(Main.tictac, 'X'));

        Main.assignValue(Main.tictac);
        Main.tictac[0][2] = Main.tictac[1][1] = Main.tictac[2][0] = 'O';
        assertTrue(Main.win(Main.tictac, 'O'));
    }

    @Test
    void testIsDraw() {
        Main.assignValue(Main.tictac);

        char[][] drawBoard = {
                {'X','O','X'},
                {'X','O','O'},
                {'O','X','X'}
        };

        Main.tictac = drawBoard;
        assertTrue(Main.isDraw());
    }

    @Test
    void testEasyMove() {
        Main.assignValue(Main.tictac);
        Main.easyMove('X');

        int count = 0;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (Main.tictac[i][j] == 'X') count++;

        assertEquals(1, count);
    }

    @Test
    void testHardMoveMakesValidMove() {
        Main.assignValue(Main.tictac);
        Main.hardMove('X');

        int count = 0;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (Main.tictac[i][j] == 'X') count++;

        assertEquals(1, count);
    }

    @Test
    void testMediumMoveBlocksOrWins() {
        Main.assignValue(Main.tictac);
        // Setup board so X can win
        Main.tictac[0][0] = 'X';
        Main.tictac[0][1] = 'X';
        Main.mediumMove('X');
        assertEquals('X', Main.tictac[0][2]);
    }

    @Test
    void testCount() {
        Main.assignValue(Main.tictac);
        Main.tictac[0][0] = Main.tictac[0][1] = 'X';
        assertEquals(2, Main.count('X', 0,0, 0,1, 0,2));
        assertEquals(0, Main.count('O', 0,0,0,1,0,2));
    }

    @Test
    void testTryWinOrBlock_RowWin() {
        // X has two in a row, empty cell should be filled
        Main.tictac[0][0] = 'X';
        Main.tictac[0][1] = 'X';
        boolean result = Main.tryWinOrBlock('X');

        assertTrue(result, "Should be able to win");
        assertEquals('X', Main.tictac[0][2], "Empty cell should be filled for win");
    }

    @Test
    void testTryWinOrBlock_ColumnWin() {
        // O has two in a column
        Main.tictac[0][1] = 'O';
        Main.tictac[1][1] = 'O';
        boolean result = Main.tryWinOrBlock('O');

        assertTrue(result);
        assertEquals('O', Main.tictac[2][1]);
    }

    @Test
    void testTryWinOrBlock_DiagonalWin() {
        // X has two in main diagonal
        Main.tictac[0][0] = 'X';
        Main.tictac[1][1] = 'X';
        boolean result = Main.tryWinOrBlock('X');

        assertTrue(result);
        assertEquals('X', Main.tictac[2][2]);
    }

    @Test
    void testTryWinOrBlock_AntiDiagonalWin() {
        // O has two in anti-diagonal
        Main.tictac[0][2] = 'O';
        Main.tictac[1][1] = 'O';
        boolean result = Main.tryWinOrBlock('O');

        assertTrue(result);
        assertEquals('O', Main.tictac[2][0]);
    }

    @Test
    void testTryWinOrBlock_NoMove() {
        // Board empty, check for impossible move (using 'Z' which is not on board)
        boolean result = Main.tryWinOrBlock('Z');
        assertFalse(result, "No move should be possible for 'Z'");
    }
}
