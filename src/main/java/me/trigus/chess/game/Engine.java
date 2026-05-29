package me.trigus.chess.game;

import me.trigus.chess.App;
import me.trigus.chess.util.Util;

public class Engine {

    private final GameState gameState;

    public Engine() {
        gameState = new GameState();
    }

    public void init() {
        gameState.init();
    }

    public boolean move(String input) {
        //e4-a2
        if (input.length() != 5 || input.charAt(2) != '-') {
            App.getConsole().warning("invalid move input, use '/help' for more information.");
            return false;
        }

        input = input.toUpperCase();
        App.getConsole().debug("working with move " + input);

        String[] parts = input.split("-");
        char srcCol = parts[0].charAt(0);
        char srcRow = parts[0].charAt(1);
        char dstCol = parts[1].charAt(0);
        char dstRow = parts[1].charAt(1);

        if (srcCol < 'A' || srcCol > 'H') {
            App.getConsole().warning("invalid column '" + srcCol + "'");
            return false;
        } else if (srcRow < '1' || srcRow > '8') {
            App.getConsole().warning("invalid row '" + srcRow + "'");
            return false;
        } else if (dstCol < 'A' || dstCol > 'H') {
            App.getConsole().warning("invalid column '" + dstCol + "'");
            return false;
        } else if (dstRow < '1' || dstRow > '8') {
            App.getConsole().warning("invalid row '" + dstRow + "'");
            return false;
        }


        srcCol -= 'A';
        dstCol -= 'A';
        srcRow -= '1';
        dstRow -= '1';
        App.getConsole().debug("srcCol: " + (int)srcCol + ", srcRow: " + (int)srcRow +
                ", dstCol: " + (int)dstCol + ", dstRow: " + (int)dstRow);

        Piece piece = gameState.getPiece(Util.coordsToPosition(srcRow, srcCol));
        if (piece == null) {
            App.getConsole().warning("no piece at source square");
            return false;
        } else if (piece.getType().isWhite && !gameState.isWhiteTurn()) {
            App.getConsole().warning("it's black's turn");
            return false;
        } else if (!piece.getType().isWhite && gameState.isWhiteTurn()) {
            App.getConsole().warning("it's white's turn");
            return false;
        }

        Piece target =  gameState.getPiece(Util.coordsToPosition(dstRow, dstCol));
        if (target != null && target.getType().isWhite == piece.getType().isWhite) {
            App.getConsole().warning("can't capture own pieces");
            return false;
        }

        long rawMoves = piece.getType().getRawMoves(Util.coordsToPosition(srcRow, srcCol));
        App.getConsole().debug("rawMoves: " + rawMoves);

        if ((rawMoves & Util.coordsToPosition(dstRow, dstCol)) == 0L) {
            App.getConsole().warning("illegal move");
            return false;
        }

        // no errors, move can be made
        if (target != null) {
            gameState.removePiece(target);
        }
        piece.setPosition(Util.coordsToPosition(dstRow, dstCol));
        piece.setHasMoved(true);

        gameState.toggleWhiteTurn();

        return true;
    }

    public void drawBoard() {
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 9; j++) {
                if (i % 2 == 0) {
                    if (j == 8) IO.print("+");
                    else IO.print ("+  -  ");
                } else {
                    if (j == 8) IO.print("|");

                    else { // draw pieces
                        Piece current = gameState.getPiece(Util.coordsToPosition(7 - i / 2, j));
                        String s;
                        if (current == null) {
                            s = "|     ";
                        } else {
                            s = "| " +
                                    (current.getType().isWhite ? "<" : ">") +
                                    current +
                                    (current.getType().isWhite ? ">" : "<") +
                                    " ";
                        }
                        IO.print (s);
                    }
                }


            }
            IO.println();
        }
    }
}
