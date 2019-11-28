package com.company.arraygames;

import java.util.Scanner;

public class Connect4 {

    private Scanner s = new Scanner(System.in);

    private final int red = 1; // keeping track of which player is playing made easy
    private final int blue = -1;
    private final int green = -9; // just a random number
    // colour stuff
    private final String redColour = "\u001B[31m";
    private final String blueColour = "\u001B[34m";
    private final String yellowColour = "\u001B[33m";
    private final String greenColour = "\u001B[32m";
    private final String resetColour = "\u001B[0m";
    private final String greenCoin = String.format("%sO%s", greenColour, resetColour); // green coin called if someone wins to highlight where they won
    private final String redCoin = String.format("%sO%s", redColour, resetColour); // easier to draw the coins like this (makes a red O)
    private final String blueCoin = String.format("%sO%s", blueColour, resetColour); // (blue O)
    private final String whiteCoin = "O"; // (white O)

    private boolean playing = true; // playing is true as long as someone doesn't win

    private String gw;
    private String gt;

    private boolean tied = false;

    private final int cols = 6; // amt of cols
    private final int rows = 7; // amt of roles

    private int[][] board = new int[cols][rows]; // board 6x7 (col, row)

    private int turn = 0; // keep track of the number of turns passed
    private int currentPlayer = red; // red always starts

    private Connect4() {
        displayBoard();
        while (playing) {
            try {
                turn++; // next turn
                if (checkForTie()) {
                    gt = String.format("%n%sTie!%s%n%n", yellowColour, resetColour);
                    tied = true;
                    playing = false;
                    break;
                }
                playerTurn(currentPlayer, requestMove()); // request a move from the current player
                if(checkForWin(currentPlayer)) { // if the current player has won
                    if(currentPlayer == red) { // if the player was red
                        gw = String.format("%n%sRed Wins in %d Turns!%s%n%n", redColour, turn, resetColour);
                        playing = false; // stop playing
                        break; // break the loop
                    } else if(currentPlayer == blue) { // if the player was blue
                        gw = String.format("%n%sBlue Wins in %d Turns!%s%n%n", blueColour, turn, resetColour);
                        playing = false; // stop playing
                        break; // break the loop
                    }
                }
                displayBoard(); // show the updated board (if nobody has won)
                currentPlayer *= -1; // switch the player
            } catch (Exception e) {
                System.out.println("oops");
            }
        }
        displayBoard(); // show the final board (winning play highlighted green) when/if somebody has won
        if(tied) {
            System.out.println(gt);
        } else {
            System.out.println(gw);
        }
    }

    private int requestMove() {
        boolean validMove = false; // start out as declaring the user's move as invalid
        String msg; // message to be output to the console based on which player is making a move
        int desiredMove = -1; // placeholder for desiredMove
        if (currentPlayer == red) {
            msg = String.format("%sRed, where would you like to place your coin?%s ", redColour, resetColour);
        } else {
            msg = String.format("%sBlue, where would you like to place your coin?%s ", blueColour, resetColour);
        }
        System.out.println(msg);
        while (!validMove) { // as long as the input the user gave isn't valid
            try {
                desiredMove = s.nextInt(); // ask for a new input

                // here we subtract 1 from desiredMove because the players don't see (0,0) as (0,0), they see it as (1,1)
                if (desiredMove - 1 > board.length || desiredMove - 1 < 0) { // check if the number they entered is out of bounds
                    System.out.printf("%d is not a valid column because it is out of bounds. Try again.%n", desiredMove);
                } else if (board[0][desiredMove - 1] == red || board[0][desiredMove - 1] == blue) { // if someone is already there then u cant
                    System.out.printf("%d is not a valid column because there is already a piece there.%n", desiredMove);
                } else { // the number the user entered is good
                    validMove = true;
                }
            } catch (Exception e) { // catches an error (for if the player entered not an integer)
                System.out.println("That is not a valid number. Try again.");
                s.nextLine();
            }
        }
        return desiredMove - 1; // send back the move to the game loop (playerTurn function)
    }

    private void playerTurn(int player, int col) { // method for moving the game piece down if there is nothing below it (gravity)
        int newRow = 0; // keep track of which row the piece should end up in
        if(board[board.length - 1][col] != blue && board[board.length - 1][col] != red) { // check if the piece can just go straight to the bottom
            board[board.length - 1][col] = player;
        } else {
            for(int i = 0; i < board.length - 1; i++) { // if the piece can't go straight to the bottom, find where the piece may rest
                if(board[i + 1][col] != blue && board[i + 1][col] != red) { // check if the game piece can fall to another row
                    newRow++;
                } else { // if it can't, set the piece to be in the position it is currently in
                    board[newRow][col] = player;
                    break; // break the for loop because there is no need to stay in the loop (we already found the piece's new location)
                }
            }
        }
    }

    private void displayBoard() {

        System.out.printf("%s   1  2  3  4  5  6  7%s%n", yellowColour, resetColour); // displays a coordinate system so the players can see which column is which

        /**
         * draw the board such that:
         * empty spots show up as white
         * red pieces show up as red
         * blue pieces show up as blue
         * and the game winning move shows up as green
         */
        for (int i = 0; i < board.length; i++) { // rows
            for (int j = 0; j < board[i].length; j++) { // columns
                if(j == 0) {
                    System.out.printf("%s%d%s ", yellowColour, i+1, resetColour); // draw the row coordinates if the row has yet to be drawn
                }
                if(board[i][j] == red) {
                    System.out.printf("|%s|", redCoin);
                } else if(board[i][j] == blue) {
                    System.out.printf("|%s|", blueCoin);
                } else if(board[i][j] == green) {
                    System.out.printf("|%s|", greenCoin);
                } else {
                    System.out.printf("|%s|", whiteCoin);
                }
            }
            System.out.println(); // new line
        }
    }

    private boolean checkForTie() {
        if(turn >= 43) { // if the board is full
            return true;
        }
        return false;
    }

    private boolean checkForWin(int player) {
        /**
         * left/fight is very light on math, as it is only checking in one direction (x)
         * if we check for a left/right win first, and the player wins with left/right, we save lots of processing
         * because we dont need to check for diagonal wins
         *
         * we do up/down next for the same reason as left/right
         *
         * we check for diagonal wins at the end because they are the most math heavy functions
         *
         * for each case it's split up in to multiple if statements to make it easier to follow and because having 3 '&&' signs is ugly
         */
        if(checkLeftRight(player)) {
            return true;
        } else if(checkUpDown(player)) {
            return true;
        } else if(checkDiagonal1(player)) {
            return true;
        } else if(checkDiagonal2(player)) {
            return true;
        }
    return false;
    }

    private boolean checkDiagonal2(int player) {
        for(int i = 2; i < 6; i++) { // rows
            for(int j = 0; j < 4; j++) { // columns
                try {
                    if(board[i][j] == player) { // check the pattern if its a player
                        if(board[i - 1][j + 1] == player) {
                            if(board[i - 2][j + 2] == player) {
                                if(board[i - 3][j + 3] == player) {
                                    for(int k = 0; k <= 3; k++) {
                                        board[i - k][j + k] = green; // go back and re draw the winning move as green
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
        return false;
    }

    private boolean checkDiagonal1(int player) {
        for(int i = 0; i < 3; i++) { // rows
            for(int j = 0; j < 4; j++) { // columns
                try {
                    if(board[i][j] == player) { // check the pattern if its a player
                        if(board[i + 1][j + 1] == player) {
                            if(board[i + 2][j + 2] == player) {
                                if(board[i + 3][j + 3] == player) {
                                    for(int k = 0; k <= 3; k++) {
                                        board[i + k][j + k] = green; // go back and re draw the winning move as green
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
        return false;
    }

    private boolean checkUpDown(int player) {
        for(int i = 0; i < board.length - 3; i++) {
            for(int j = 0; j < board[i].length; j++) {
                try {
                    if(board[i][j] == player) { // check the pattern if its a player
                        if(board[i + 1][j] == player) {
                            if(board[i + 2][j] == player) {
                                if(board[i + 3][j] == player) {
                                    for(int k = 0; k <= 3; k++) {
                                        board[i + k][j] = green; // go back and re draw the winning move as green
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
        return false;
    }

    private boolean checkLeftRight(int player) {
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length - 3; j++) {
                try {
                    if(board[i][j] == player) { // check the pattern if its a player
                        if(board[i][j + 1] == player) {
                            if(board[i][j + 2] == player) {
                                if(board[i][j + 3] == player) {
                                    for(int k = 0; k <= 3; k++) {
                                        board[i][j + k] = green; // go back and re draw the winning move as green
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        new Connect4();
    }
}