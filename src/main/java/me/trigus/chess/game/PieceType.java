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

    private final long RANK_FIRST = 0x00000000000000FFL;
    private final long RANK_LAST = 0xFF00000000000000L;
    private final long FILE_FIRST = 0x0101010101010101L;
    private final long FILE_LAST = 0x8080808080808080L;

    public final char symbol;
    public final boolean isWhite;

    PieceType(char symbol, boolean isWhite) {
        this.symbol = symbol;
        this.isWhite = isWhite;
    }

    public long getRawMoves (long position, GameState gameState) {
        long moves = 0;

        int index = (int) (Math.log(position) / Math.log(2));
        int currentRank = index / 8 + 1;
        int currentFile = index % 8 + 1;
        if (position < 0) {
            currentRank = 8;
            currentFile = 8;
        }

        App.getConsole().debug("working with position/rank/file: " + position + " " + currentRank + " " + currentFile);



        switch (this) {
            case PAWN:
                if ((position & RANK_FIRST << 8) != 0L) {
                    moves |= position << 16;
                }
                Piece topLeft = gameState.getPiece(position << 7);
                Piece topRight = gameState.getPiece(position << 9);

                if ((position & FILE_FIRST) != 0) topLeft = null;
                if ((position & FILE_LAST) != 0) topRight = null;

                if (topLeft != null && topLeft.getType().isWhite != isWhite) moves |= position << 7;
                if (topRight != null && topRight.getType().isWhite != isWhite) moves |= position << 9;

                moves |= position << 8;
                break;

            case PAWN_B:
                if ((position & RANK_FIRST << (8 * 6)) != 0) {
                    moves |= position >> 16;
                }

                Piece bottomLeft = gameState.getPiece(position >> 9);
                Piece bottomRight = gameState.getPiece(position >> 7);

                if ((position & FILE_FIRST) != 0) bottomLeft = null;
                if ((position & FILE_LAST) != 0) bottomRight = null;

                if (bottomLeft != null && bottomLeft.getType().isWhite != isWhite) moves |= position >> 9;
                if (bottomRight != null && bottomRight.getType().isWhite != isWhite) moves |= position >> 7;

                moves |= position >> 8;
                break;

            case KING:
            case KING_B:
            case QUEEN:
            case QUEEN_B:
            case ROOK, ROOK_B:
                moves |= RANK_FIRST << (currentRank - 1) * 8;
                moves |= FILE_FIRST << currentFile - 1;
            case BISHOP:
            case BISHOP_B:
            case KNIGHT:
            case KNIGHT_B:
        }
        return moves;
    }
}