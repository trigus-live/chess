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

    public GameState getGameState() {
        return gameState;
    }

    public boolean move(String input) {
        //e4-a2
        if (input.length() != 5 || input.charAt(2) != '-') {
            App.getConsole().warning("invalid move input, use '/help' for more information.");
            return false;
        }

        App.getConsole().debug("working with move " + input);

        String[] parts = input.split("-");
        int[] src = Util.gridCoordsToRowColBase0(parts[0]);
        int[] dst = Util.gridCoordsToRowColBase0(parts[1]);

        int srcRow = src[0];
        int srcCol = src[1];
        int dstRow = dst[0];
        int dstCol = dst[1];

        App.getConsole().debug("srcRow: " + src[0] + ", srcCol: " + src[1] +
                ", dstRow: " + dst[0] + ", dstCol: " + dst[1]);

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

        long rawMoves = piece.getType().getRawMoves(Util.coordsToPosition(srcRow, srcCol), gameState);
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

    public void drawBoard(long highlightMask) {
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 9; j++) {
                if (i % 2 == 0) {
                    if (j == 8) IO.print("+");
                    else IO.print ("+  -  ");
                } else {
                    if (j == 8) IO.print("|");

                    else { // draw pieces
                        long position = Util.coordsToPosition(7 - i / 2, j);
                        Piece current = gameState.getPiece(position);
                        boolean highlight = 0 != (highlightMask & position);

                        StringBuilder sb = new StringBuilder();
                        sb.append("|");

                        if (highlight) {
                            sb.append("[");
                        } else {
                            sb.append(" ");
                        }

                        if (current == null) {
                            sb.append("   ");
                        } else {
                            sb.append(current.getType().isWhite ? "<" : ">");
                            sb.append(current);
                            sb.append(current.getType().isWhite ? ">" : "<");
                        }

                        if (highlight) {
                            sb.append("]");
                        } else {
                            sb.append(" ");
                        }

                        IO.print (sb.toString());
                    }
                }


            }
            IO.println();
        }
    }
}
