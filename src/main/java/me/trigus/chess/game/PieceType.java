package me.trigus.chess.game;

import me.trigus.chess.App;
import me.trigus.chess.util.Util;

public enum PieceType {
    PAWN ('P', true),
    PAWN_B ('p', false),
    KING ('K', true),
    KING_B ('k', false),
    QUEEN ('Q', true),
    QUEEN_B ('q', false),
    ROOK ('R', true),
    ROOK_B ('r', false),
    BISHOP ('B', true),
    BISHOP_B ('b', false),
    KNIGHT ('N', true),
    KNIGHT_B ('n', false);

    public final char symbol;
    public final boolean isWhite;

    PieceType(char symbol, boolean isWhite) {
        this.symbol = symbol;
        this.isWhite = isWhite;
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
        final long RANK_FIRST = 0x00000000000000FFL;
        final long RANK_LAST = 0xFF00000000000000L;
        final long FILE_FIRST = 0x0101010101010101L;
        final long FILE_LAST = 0x8080808080808080L;

        long moves = 0x0L;

        int[] indices = Util.positionToIndexCoords(position);
        int currentRank = indices[0];
        int currentFile = indices[1];

        App.getConsole().debug("computing raw moves for '" + symbol + "', rank: " + currentRank + ", file: " + currentFile);

        switch (this) {

            case PAWN -> {
                moves |= position << 8;
                if ((position >>> 8 & RANK_FIRST) != 0)
                    moves |= position << 16;

            } case PAWN_B -> {
                moves |= position >>> 8;
                if ((position << 8 & RANK_LAST) != 0)
                    moves |= position >>> 16;

            } case KNIGHT, KNIGHT_B, KING, KING_B -> {
                long preMaskC3 = 0L;

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
                if (currentFile >= 6) preMaskC3 &= ~(FILE_FIRST | FILE_FIRST << 1);
                // cut right side overflows
                else if (currentFile <= 1) preMaskC3 &= ~(FILE_LAST | FILE_LAST >>> 1);

                moves |= preMaskC3;

            } case ROOK, ROOK_B -> {
                moves |= RANK_FIRST << currentRank * 8;
                moves |= FILE_FIRST << currentFile;

            } case BISHOP, BISHOP_B -> {
                long preDiagonalA1 = 0x8040201008040201L;
                long preDiagonalH1 = 0x0102040810204080L;
                int offsetA1 = currentFile - currentRank;
                int offsetH1 = currentFile + currentRank - 7;

                // shift diagonals and cut top or bottom ranks if necessary
                if (offsetA1 > 0) {
                    preDiagonalA1 <<= 8 * offsetA1 + offsetA1;
                    preDiagonalA1 >>>= 8 * offsetA1;
                } else if (offsetA1 < 0) {
                    preDiagonalA1 >>>= 8 * -offsetA1 - offsetA1;
                    preDiagonalA1 <<= 8 * -offsetA1;
                }

                if (offsetH1 > 0) {
                    preDiagonalH1 >>>= 8 * offsetH1 - offsetH1 + 1;
                    preDiagonalH1 <<= 8 * offsetH1 + 1;
                } else if (offsetH1 < 0) {
                    preDiagonalH1 <<= 8 * -offsetH1 + offsetH1 + 1;
                    preDiagonalH1 >>>= 8 * -offsetH1 + 1;
                }

                moves |= preDiagonalA1;
                moves |= preDiagonalH1;

            } case QUEEN, QUEEN_B -> {
                moves |= PieceType.BISHOP.getRawMoves(position);
                moves |= PieceType.ROOK.getRawMoves(position);
            }
        }

        return (moves & ~position);
    }
}