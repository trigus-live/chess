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
        long src = Util.gridCoordsToPosition(parts[0]);
        long dst = Util.gridCoordsToPosition(parts[1]);

        Piece piece = gameState.getPiece(src);
        if (piece == null) {
            App.getConsole().warning("no piece at source square");
            return false;
        } else if (piece.getType().isWhite && !gameState.isTurnWhite()) {
            App.getConsole().warning("it's black's turn");
            return false;
        } else if (!piece.getType().isWhite && gameState.isTurnWhite()) {
            App.getConsole().warning("it's white's turn");
            return false;
        }

        Piece target =  gameState.getPiece(dst);
        if (target != null && target.getType().isWhite == piece.getType().isWhite) {
            App.getConsole().warning("can't capture own pieces");
            return false;
        }

        long rawMoves = piece.getType().getRawMoves(src, gameState);

        if ((rawMoves & dst) == 0L) {
            App.getConsole().warning("illegal move");
            return false;
        }

        // no errors, move can be made
        if (target != null) {
            gameState.removePiece(target);
        }
        piece.setPosition(dst);

        // check en-passant capture
        if (dst == gameState.getEnPassantPosition()) {
            if (gameState.isTurnWhite()) {
                gameState.removePiece(dst >>> 8);
            } else {
                gameState.removePiece(dst << 8);
            }
        }

        // check castle
        if (piece.getType() == PieceType.KING && src - dst == 2) {
            Piece rook = gameState.getPiece(dst >>> 2);
            rook.setPosition(dst << 2);
            gameState.removePiece(rook);
            gameState.addPiece(rook);
        } else if (piece.getType() == PieceType.KING && src - dst == -2) {
            Piece rook = gameState.getPiece(dst << 1);
            rook.setPosition(dst >>> 1);
            gameState.removePiece(rook);
            gameState.addPiece(rook);
        }  else if (piece.getType() == PieceType.KING_B && src - dst == 2) {
            Piece rook = gameState.getPiece(dst >>> 2);
            rook.setPosition(dst << 2);
            gameState.removePiece(rook);
            gameState.addPiece(rook);
        }  else if (piece.getType() == PieceType.KING_B && src - dst == -2) {
            Piece rook = gameState.getPiece(dst << 1);
            rook.setPosition(dst >>> 1);
            gameState.removePiece(rook);
            gameState.addPiece(rook);
        }

        // need to remove and add again to maintain FEN-Order in gameState.piecesList
        gameState.removePiece(piece);
        gameState.addPiece(piece);

        // update castle-states
        if ((piece.getType() == PieceType.ROOK_B && (src & 0x0100000000000000L) != 0) || piece.getType() == PieceType.KING_B) {
            gameState.setCastleBlackQueen(false);
        }
        if ((piece.getType() == PieceType.ROOK_B && (src & 0x8000000000000000L) != 0) || piece.getType() == PieceType.KING_B) {
            gameState.setCastleBlackKing(false);
        }
        if ((piece.getType() == PieceType.ROOK && (src & 0x0000000000000001L) != 0) || piece.getType() == PieceType.KING) {
            gameState.setCastleWhiteQueen(false);
        }
        if ((piece.getType() == PieceType.ROOK && (src & 0x0000000000000080L) != 0) || piece.getType() == PieceType.KING) {
            gameState.setCastleWhiteKing(false);
        }


        // pawn promotion
        if (piece.getType() == PieceType.PAWN && (dst << 8 == 0)) {
            Piece promotion = new Piece(PieceType.QUEEN, piece.getPosition());
            gameState.removePiece(piece);
            gameState.addPiece(promotion);
        } else if (piece.getType() == PieceType.PAWN_B && (dst >>> 8 == 0)) {
            Piece promotion = new Piece(PieceType.QUEEN_B, piece.getPosition());
            gameState.removePiece(piece);
            gameState.addPiece(promotion);
        }

        // update en passant position
        if (piece.getType() == PieceType.PAWN && (dst >>> 16 == src)) {
            gameState.setEnPassantPosition(src << 8);
        } else if (piece.getType() == PieceType.PAWN_B && (dst << 16 == src)) {
            gameState.setEnPassantPosition(src >>> 8);
        } else {
            gameState.setEnPassantPosition(0x0L);
        }

        // update half-move rule
        if (piece.getType() == PieceType.PAWN || piece.getType() == PieceType.PAWN_B || target != null) {
            gameState.setHalfMoveCount(0);
        } else {
            gameState.incrementHalfMoveCount();
        }

        // update white turn and move count
        if (!gameState.isTurnWhite()) gameState.incrementFullMoveCount();
        gameState.toggleTurnWhite();

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
                        long position = Util.indexCoordsToPosition(new int[]{7 - i / 2, j});
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
