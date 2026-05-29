package me.trigus.chess.game;

import me.trigus.chess.App;

public enum PieceType {
    PAWN ('P', true),
    PAWN_B ('P', false),
    KING ('K', true),
    KING_B ('K', false),
    QUEEN ('Q', true),
    QUEEN_B ('Q', false),
    ROOK ('R', true),
    ROOK_B ('R', false),
    BISHOP ('B', true),
    BISHOP_B ('B', false),
    KNIGHT ('N', true),
    KNIGHT_B ('N', false);

    private final long RANK_FIRST = 0x1100000000000000L;
    private final long FILE_FIRST = 0x1010101010101010L;

    public final char symbol;
    public final boolean isWhite;

    PieceType(char symbol, boolean isWhite) {
        this.symbol = symbol;
        this.isWhite = isWhite;
    }

    public long getRawMoves (long position) {
        long moves = 0;
        int index = (int) (Math.log(position) / Math.log(2));
        int currentRank = index / 8;
        int currentFile = index % 8;

        App.getConsole().debug("working with position/rank/file: " + position + " " + currentRank + " " + currentFile);

        switch (this) {
            case PAWN:
                if ((position & RANK_FIRST >> 8) != 0) {
                    moves |= position >> 16;
                }
                moves |= position >> 8;
                break;

            case PAWN_B:
                if ((position & RANK_FIRST >> (8 * 6)) != 0) {
                    moves |= position << 16;
                }
                moves |= position << 8;
                break;

            case KING:
            case KING_B:
            case QUEEN:
            case QUEEN_B:
            case ROOK, ROOK_B:
                moves |= RANK_FIRST >> currentRank * 8;
                moves |= FILE_FIRST >> currentFile * 8;
            case BISHOP:
            case BISHOP_B:
            case KNIGHT:
            case KNIGHT_B:
        }
        return moves;
    }
}