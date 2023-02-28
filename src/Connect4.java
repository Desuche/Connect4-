import java.util.Scanner;

/**
 * @author: Uche Destiny Nnanna (SID) 21201609
 * <p>
 * For the instruction of the assignment please refer to the assignment
 * GitHub.
 * Plagiarism is a serious offense and can be easily detected. Please
 * don't share your code to your classmate even if they are threatening
 * you with your friendship. If they don't have the ability to work on
 * something that can compile, they would not be able to change your
 * code to a state that we can't detect the act of plagiarism. For the
 * first commit of plagiarism, regardless you shared your code or
 * copied code from others, you will receive 0 with an addition of 5
 * mark penalty. If you commit plagiarism twice, your case will be
 * presented in the exam board, and you will receive an F directly.
 * <p>
 * If you cannot work out the logic of the assignment, simply contact
 * us on Piazza. The teaching team is more the eager to provide
 * you help. We can extend your submission due if it is really
 * necessary. Just please, don't give up.
 */
public class Connect4 {

    /**
     * Total number of rows of the game board. Use this constant whenever possible.
     */
    public static final int HEIGHT = 6;
    /**
     * Total number of columns of the game board. Use this constant whenever
     * possible.
     */
    public static final int WIDTH = 8;

    /**
     * Your main program. You don't need to change this part. This has been done for
     * you.
     */
    public static void main(String[] args) {
        new Connect4().runOnce();
    }

    /**
     * Your program entry. There are two lines missing. Please complete the line
     * labeled with TODO. You can, however, write more than two lines to complete
     * the logic required by TODO. You are not supposed to modify any part other
     * than the TODOs.
     */
    void runOnce() {
        // For people who are not familiar with constants - HEIGHT and WIDTH are two
        // constants defined above. These two constants are visible in the entire
        // program. They cannot be further modified, i.e., it is impossible to write
        // HEIGHT = HEIGHT + 1; or WIDTH = 0; anywhere in your code. However, you can
        // use
        // these two constants as a reference, i.e., row = HEIGHT - 1, for example.

        int[][] board = new int[HEIGHT][WIDTH];
        char[] symbols = {'1', '2'};
        int player = 1;
        printBoard(board, symbols);

        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        while (!isGameOver(board) && !quit) {
            System.out.println("Player " + player + ", please enter a command. Press 'h' for help");
            char s = scanner.next().charAt(0);
            switch (s) {
                case 'h':
                case 'H':
                    printHelpMenu();
                    break;
                case 'c':
                case 'C':
                    changeSymbol(player, symbols);
                    break;
                case 'q':
                case 'Q':
                    quit = true;
                    System.out.println("Bye~");
                    continue;
                case 'r':
                    restart(board);
                    printBoard(board, symbols);
                    continue;
                default:
                    if (!validate(s, board)) {
                        System.out.println("Wrong input!, please do again");
                        continue;
                    }

                    // convert the char 's' to the integer 'column', with the value 0 to 7
                    int column = s - 48;
                    fillBoard(board, column, player);
                    printBoard(board, symbols);
                    if (isGameOver(board)) {
                        System.out.println("Player " + player + ", you win!");
                        break;
                    } else if (checkMate(player, board))
                        System.out.println("Check mate!");
                    else if (check(player, board))
                        System.out.println("Check!");

                    player = player ^ 3; //switching players

            } // end switch
        } // end while
        scanner.close();
    }

    /**
     * Reset the board to the initial state
     *
     * @param board - the game board array
     */
    void restart(int[][] board) {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                board[i][j] = 0;
            }
        }
    }

    /**
     * It allows a player to choose a new symbol to represents its chess.
     * This method should ask the player to enter a new symbol so that symbol is not
     * the same as its opponent.
     * Otherwise, the player will need to enter it again until they are different.
     *
     * @param player  - the player who is about to change its symbol
     * @param symbols - the symbols array storing the players' symbols.
     */
    void changeSymbol(int player, char[] symbols) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the new symbol: ");
        char symbol = scanner.next().charAt(0);

        char otherPlayerSymbol = symbols[(player ^ 3) - 1];
        if (symbol == otherPlayerSymbol) {
            System.out.println("Unusable symbol");
            changeSymbol(player, symbols);
            return;
        } // return is acting as break for the recursive changeSymbol method.
        switch (symbol) {
            case 'h':
            case 'H':
            case 'c':
            case 'C':
            case 'q':
            case 'Q':
            case 'r':
            case 'R':
                System.out.println("Unusable symbol");
                changeSymbol(player, symbols);
                return; // return is acting as break for the recursive changeSymbol method. It is not necessary here for now, but it will be if any additional code is added after this switch case.

            default:
                symbols[player - 1] = symbol;
        }

    }

    /**
     * This method is used to check if a player has filled any three consecutive cells.
     * The pattern of consecutive cells is specified when passing the arguments a, b, and c.
     *
     * @param player - the player whose connection is being checked
     * @param a      - b, c - values of the cells being checked for connection. Input in the form of board[*][*]
     */
    boolean checkConnection(int player, int a, int b, int c) {
        boolean cond1 = (a * b * c != 0);
        // cond1 ensures neither is zero. Redundant because player is never zero so cond2 will fail anyway. I am too lazy to remove. (injecting some humor into code)
        boolean cond2 = (   ( ((player - a) | (b - c)) | (player - c) ) == 0    ); //ensures all in the specified sequence has same value as player
        return (cond1 && cond2);
    }


    /**
     * This method returns true if the player "player" plays immediately, he/she may
     * end the game. This warns the other player to
     * place his/her next block in a correct position.
     *
     * @param player - the player who is about to win if the other player does not
     *               stop him
     * @param board  - the 2D array of the game board.
     * @return true if the player is about to win, false if the player is not.
     */
    boolean check(int player, int[][] board) {
        /* Loop-heavy brute force solution. Please suggest alternative, I am interested.
         * all if statements follow the same format ( if there is a connection AND the next slot in the sequence is playable)
         */
        //vertical check
        for (int x = 0; x < WIDTH; x++) {
            for (int y = HEIGHT - 1; y > 2; y--) {
                if (checkConnection(player, board[y][x], board[y - 1][x], board[y - 2][x]) && (board[y - 3][x] == 0))
                    return true;
            }
        }
        //horizontal check from left side
        for (int x = 0; x < HEIGHT; x++) {
            for (int y = 0; y < 5; y++) {
                if (checkConnection(player, board[x][y], board[x][y + 1], board[x][y + 2]) && ((board[x][y + 3] == 0) && ((x == HEIGHT - 1) || (board[x + 1][y + 3] != 0))))
                    return true;
                if (checkConnection(player, board[x][y], board[x][y + 1], board[x][y + 3]) && ((board[x][y + 2] == 0) && ((x == HEIGHT - 1) || (board[x + 1][y + 2] != 0))))
                    return true;
            }
        }
        //horizontal check from right side
        for (int x = 0; x < HEIGHT; x++) {
            for (int y = 7; y > 2; y--) {
                if (checkConnection(player, board[x][y], board[x][y - 1], board[x][y - 2]) && ((board[x][y - 3] == 0) && ((x == HEIGHT - 1) || (board[x + 1][y - 3] != 0))))
                    return true;
                if (checkConnection(player, board[x][y], board[x][y - 1], board[x][y - 3]) && ((board[x][y - 2] == 0) && ((x == HEIGHT - 1) || (board[x + 1][y - 2] != 0))))
                    return true;
            }
        }

        //diagonal (forward slash) check from top right
        for (int x = 3; x < WIDTH; x++) {
            for (int y = 0; y < 3; y++) {
                if (checkConnection(player, board[y][x], board[y + 1][x - 1], board[y + 2][x - 2]) && ((board[y + 3][x - 3] == 0) && ((y == 2) || (board[y + 4][x - 3] != 0))))
                    return true;
                if (checkConnection(player, board[y][x], board[y + 1][x - 1], board[y + 3][x - 3]) && ((board[y + 2][x - 2] == 0) && ((y == 2) || (board[y + 3][x - 2] != 0))))
                    return true;
            }
        }
        //diagonal (forward slash) check from bottom left
        for (int x = 0; x < 5; x++) {
            for (int y = HEIGHT - 1; y > 2; y--) {
                if (checkConnection(player, board[y][x], board[y - 1][x + 1], board[y - 2][x + 2]) && ((board[y - 3][x + 3] == 0) && (board[y - 2][x + 3] != 0)))
                    return true;
                if (checkConnection(player, board[y][x], board[y - 1][x + 1], board[y - 3][x + 3]) && ((board[y - 2][x + 2] == 0) && (board[y - 1][x + 2] != 0)))
                    return true;
            }
        }
        //diagonal (backward slash) check from top left
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                if (checkConnection(player, board[y][x], board[y + 1][x + 1], board[y + 2][x + 2]) && ((board[y + 3][x + 3] == 0) && ((y == 2) || (board[y + 4][x + 3] != 0))))
                    return true;
                if (checkConnection(player, board[y][x], board[y + 1][x + 1], board[y + 3][x + 3]) && ((board[y + 2][x + 2] == 0) && ((y == 2) || (board[y + 3][x + 2] != 0))))
                    return true;
            }
        }
        //diagonal (backward slash) check bottom right
        for (int x = 3; x < WIDTH; x++) {
            for (int y = HEIGHT - 1; y > 2; y--) {
                if (checkConnection(player, board[y][x], board[y - 1][x - 1], board[y - 2][x - 2]) && ((board[y - 3][x - 3] == 0) && (board[y - 2][x - 3] != 0)))
                    return true;
                if (checkConnection(player, board[y][x], board[y - 1][x - 1], board[y - 3][x - 3]) && ((board[y - 2][x - 2] == 0) && (board[y - 1][x - 2] != 0)))
                    return true;
            }
        }

        return false;
    }

    /**
     * This method is very similar to the method check. However, a check-mate move
     * means no matter how the other player place his/her next block, in the next
     * turn the player can win the game with certain move.
     * <p>
     * A check-mate move must be a check move. Not all check moves are check-mate
     * move.
     *
     * @param player - the player who is about to win no matter what the other
     *               player does
     * @param board  - the 2D array of the game board/
     * @return true if the player is about to win
     */
    boolean checkMate(int player, int[][] board) {
        /* Hmm! How to solve this?
         *  No other solution in mind, so I have decided to simulate a brute force counter check. If all possible counter checks fail to remove the check or fail to end the game, then checkmate.
         *  So, the algorithm imitates the opposite player, and makes a legal play in every column. If any of the legal plays manages to reduce number of checks to zero or manages to win the game, then cannot be a checkmate.
         */
        if (check(player, board)) {
            int counterCheck = 0;
            int counteringPlayer = player ^ 3; //switching players

            outerLoop:
            for (int i = 0; i < WIDTH; i++) {
                for (int j = HEIGHT - 1; j > -1; j--) {
                    if (board[j][i] == 0) {             //for all playable positions (all empty positions without any empty spot under them)
                        board[j][i] = counteringPlayer; //simulate countermove in a legal position
                        if (isGameOver(board) || !check(player, board)) {
                            counterCheck++;
                            board[j][i] = 0;            // reset location of simulated countermove back to zero
                            break outerLoop;
                        }                               //if any counter move successfully eliminates the check flag or wins the game, then stop everything. No checkmate.
                        board[j][i] = 0;                // reset location of simulated countermove back to zero
                        break;                          //break works with the closest for loop to ensure that only legal positions (without any empty spot under them) are simulated.
                        // This prevents checkmate from simulating the entire board as that would be a waste of resources.
                    }
                }
            }
            return (counterCheck == 0);             // checkmate, if no counter check worked.
        }
        return false;
    }

    /**
     * Validate if the input is valid. This input should be one of the character
     * '0', '1', '2', '3,' ..., '7'.
     * The column corresponding to that input should not be full.
     *
     * @param input - the character of the column that the block is intended to
     *              place
     * @param board - the game board
     * @return - true if it is valid, false if it is invalid -e.g., '8', 'c', '@',
     * EOT (which has an unicode 4).
     */
    boolean validate(char input, int[][] board) {
        return ((input >= 48) && (input <= 55)) && (board[0][input - 48] == 0);
    }

    /**
     * Given the column (in integer) that a player wish to place his/her block,
     * update the game board. You may assume that the input has been validated before
     * calling this method, i.e., there always has room to place the block when
     * calling this method.
     *
     * @param board  - the game board
     * @param column - the column that the player wants to place its block
     * @param player - 1 or 2, the player.
     */
    void fillBoard(int[][] board, int column, int player) {

        for (int i = HEIGHT - 1; i >= 0; i--) {
            if (board[i][column] == 0) {
                board[i][column] = player;
                break;
            }
        }
    }

    /**
     * Print the Help Menu. Please try to understand the switch case in runOnce and
     * Provide a one line comment about the purpose of each symbol.
     */
    void printHelpMenu() {
        System.out.println("\nConnect-4 by Des");

        System.out.println("OBJECTIVE:" + "\n" + "To be the first player to connect 4 your characters in a row (either vertically, horizontally, or diagonally)");
        System.out.println("\nTo play your turn, enter the digit corresponding to the column you wish to place your character");
        System.out.println("Press (case insensitive):\n 'c' to change your character \t'r' to restart \t'q' to exit\n");
    }

    /**
     * Determine if the game is over. Game is over if and only if one of the player
     * has a connect-4 or the entire game board is fully filled.
     *
     * @param board - the game board
     * @return - true if the game is over, false otherwise.
     */
    boolean isGameOver(int[][] board) {
        //vertical check
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < 3; y++) {
                int sum = (board[y][x] + board[y + 1][x] + board[y + 2][x] + board[y + 3][x]);
                if (((sum / 4.0 == 1.0) || (sum / 8.0 == 1.0)) && (board[y][x] * board[y + 1][x] * board[y + 2][x] * board[y + 3][x]) != 0)
                    return true;

            }
        }
        //horizontal check
        for (int x = 0; x < HEIGHT; x++) {
            for (int y = 0; y < 5; y++) {
                int sum = (board[x][y] + board[x][y + 1] + board[x][y + 2] + board[x][y + 3]);
                if (((sum / 4.0 == 1.0) || (sum / 8.0 == 1.0)) && ((board[x][y] * board[x][y + 1] * board[x][y + 2] * board[x][y + 3]) != 0))
                    return true;
            }
        }
        //diagonal (forward slash) check
        for (int x = 3; x < WIDTH; x++) {
            for (int y = 0; y < 3; y++) {
                int sum = (board[y][x] + board[y + 1][x - 1] + board[y + 2][x - 2] + board[y + 3][x - 3]);
                if (((sum / 4.0 == 1.0) || (sum / 8.0 == 1.0)) && (board[y][x] * board[y + 1][x - 1] * board[y + 2][x - 2] * board[y + 3][x - 3]) != 0)
                    return true;
            }
        }
        //diagonal (backward slash) check
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                int sum = (board[y][x] + board[y + 1][x + 1] + board[y + 2][x + 2] + board[y + 3][x + 3]);
                if (((sum / 4.0 == 1.0) || (sum / 8.0 == 1.0)) && (board[y][x] * board[y + 1][x + 1] * board[y + 2][x + 2] * board[y + 3][x + 3]) != 0)
                    return true;
            }
        }
        return boardFull(board);
    }

    boolean boardFull(int[][] board) {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }

        return true;

    }

    /**
     * Print the game board in a particular format. The instruction can be referred
     * to the GitHub or the demo program. By default, Player 1 uses the character
     * '1' to represent its block. Player 2 uses the character '2'. They can be
     * overrided by the value of symbols array. This method does not change the
     * value of the gameboard nor the symbols array.
     *
     * @param board   - the game board to be printed.
     * @param symbols - the symbols that represents player 1 and player 2.
     */
    void printBoard(int[][] board, char[] symbols) {
        System.out.print(' ');
        for (int i = 0; i < WIDTH; i++) {
            System.out.print(i);
        }
        System.out.print("\n ");
        for (int i = 0; i < WIDTH; i++) {
            System.out.print('-');
        }
        System.out.println();
        for (int i = 0; i < HEIGHT; i++) {
            System.out.print('|');
            for (int j = 0; j < WIDTH; j++) {
                char print = ' ';
                if (board[i][j] == 1) {
                    print = symbols[0];
                } else if (board[i][j] == 2) {
                    print = symbols[1];
                }
                System.out.print(print);
            }
            System.out.print('|');
            System.out.println();
        }

        System.out.print(' ');
        for (int i = 0; i < WIDTH; i++) {
            System.out.print('-');
        }
        System.out.println();
    }


}