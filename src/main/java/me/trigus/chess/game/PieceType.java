package me.trigus.chess.game;

import me.trigus.chess.App;
import me.trigus.chess.util.Bitmask;
import me.trigus.chess.util.Util;

public enum PieceType {
    PAWN ('P', true, false),
    PAWN_B ('p', false, false),
    KING ('K', true, false),
    KING_B ('k', false, false),
    QUEEN ('Q', true, true),
    QUEEN_B ('q', false, true),
    ROOK ('R', true, true),
    ROOK_B ('r', false, true),
    BISHOP ('B', true, true),
    BISHOP_B ('b', false, true),
    KNIGHT ('N', true, false),
    KNIGHT_B ('n', false, false);

    public final char symbol;
    public final boolean isWhite;
    public final boolean isSlidingPiece;

    PieceType(char symbol, boolean isWhite,  boolean isSlidingPiece) {
        this.symbol = symbol;
        this.isWhite = isWhite;
        this.isSlidingPiece = isSlidingPiece;
    }

    public static PieceType fromSymbol (char symbol) {
        return switch (symbol) {
            case 'P' -> PAWN;
            case 'p' -> PAWN_B;
            case 'K' -> KING;
            case 'k' -> KING_B;
            case 'Q' -> QUEEN;
            case 'q' -> QUEEN_B;
            case 'R' -> ROOK;
            case 'r' -> ROOK_B;
            case 'B' -> BISHOP;
            case 'b' -> BISHOP_B;
            case 'N' -> KNIGHT;
            case 'n' -> KNIGHT_B;
            default -> null;
        };
    }

    /**
     * Computes legal moves of a piece based on its current position on an empty board.
     * @param position position of the piece encoded in 64-Bit long
     * @return all possible next positions of the piece, encoded in 64-Bit long
     */
    public long getRawMoves (long position) {
        long moves = 0x0L;

        int[] indices = Util.positionToIndexCoords(position);
        int currentRank = indices[0];
        int currentFile = indices[1];

        App.getConsole().debug("computing raw moves for '" + symbol + "', rank: " + currentRank + ", file: " + currentFile);

        switch (this) {

            case PAWN -> {
                moves |= position << 8;
                if ((position >>> 8 & Bitmask.rank(0).mask()) != 0)
                    moves |= position << 16;

            } case PAWN_B -> {
                moves |= position >>> 8;
                if ((position << 8 & Bitmask.rank(7).mask()) != 0)
                    moves |= position >>> 16;

            } case KNIGHT, KNIGHT_B, KING, KING_B -> {
                long preMaskC3;

                // movement mask if piece located on C3
                if (this == KNIGHT || this == KNIGHT_B) preMaskC3 = 0x0000000A1100110AL;
                else preMaskC3 = 0x000000000E0A0E00L;

                // shift rank
                int offsetRank = currentRank - 2;

                if (offsetRank >= 0) preMaskC3 <<= offsetRank * 8;
                else preMaskC3 >>>= -offsetRank * 8;

                //shift file
                int offsetFile = currentFile - 2;
                if (offsetFile >= 0) preMaskC3 <<= offsetFile;
                else preMaskC3 >>>= -offsetFile;

                // cut left side overflows
                if (currentFile >= 6) preMaskC3 &= ~(Bitmask.file(0, 1).mask());
                // cut right side overflows
                else if (currentFile <= 1) preMaskC3 &= ~(Bitmask.file(6, 7).mask());

                moves |= preMaskC3;

            } case ROOK, ROOK_B -> {
                moves |= Bitmask.rank(currentRank).mask();
                moves |= Bitmask.file(currentFile).mask();

            } case BISHOP, BISHOP_B -> {
                int offsetA1 = currentFile - currentRank;
                int offsetH1 = currentFile + currentRank - 7;

                // shift diagonals and cut top or bottom ranks if necessary
                moves |= Bitmask.diagonalA1(offsetA1).mask();
                moves |= Bitmask.diagonalH1(offsetH1).mask();

            } case QUEEN, QUEEN_B -> {
                moves |= PieceType.BISHOP.getRawMoves(position);
                moves |= PieceType.ROOK.getRawMoves(position);
            }
        }

        return (moves & ~position);
    }
}