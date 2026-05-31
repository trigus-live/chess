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
    private final long DIAGONAL_A1 = 0x8040201008040201L;
    private final long DIAGONAL_H1 = 0x0102040810204080L;
    private final long KNIGHT_C3 = 0x0000000A1100110AL;

    public final char symbol;
    public final boolean isWhite;

    PieceType(char symbol, boolean isWhite) {
        this.symbol = symbol;
        this.isWhite = isWhite;
    }

    public long getRawMoves (long position, GameState gameState) {
        long moves = 0;

        int index = (int) (Math.log(position) / Math.log(2));
        int currentRank = index / 8;
        int currentFile = index % 8;
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

                moves |= position << 8;

                if (topLeft != null && topLeft.getType().isWhite != isWhite) moves |= position << 7;
                if (topRight != null && topRight.getType().isWhite != isWhite) moves |= position << 9;

                //TODO en-passant

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

                //TODO en-passant

                break;

            case KING, KING_B:
                if ((position & RANK_FIRST) == 0) {
                    moves |= position >>> 7;
                    moves |= position >>> 8;
                    moves |= position >>> 9;
                }
                if ((position & RANK_LAST) == 0) {
                    moves |= position << 7;
                    moves |= position << 8;
                    moves |= position << 9;
                }
                if ((position & FILE_FIRST) == 0) {
                    moves |= position << 7;
                    moves |= position >>> 1;
                    moves |= position >>> 9;
                }
                if ((position & FILE_LAST) == 0) {
                    moves |= position >>> 7;
                    moves |= position << 1;
                    moves |= position << 9;
                }
                // TODO Castle
                break;

            case QUEEN, QUEEN_B:
                moves |= PieceType.ROOK.getRawMoves(position, gameState);
                moves |= PieceType.BISHOP.getRawMoves(position, gameState);
                break;

            case ROOK, ROOK_B:
                moves |= RANK_FIRST << currentRank * 8;
                moves |= FILE_FIRST << currentFile;
                break;

            case BISHOP, BISHOP_B:

                int leftMove = currentFile - currentRank;
                int bottomMove = 8 * ((currentFile + currentRank) - 7);

                long preDiagonalA1;
                long preDiagonalH1;

                // put the diagonals on the current position
                // you can't really shift by negative values, so if/else-if is required
                if (leftMove < 0) preDiagonalA1 = DIAGONAL_A1 >>> -leftMove;
                else preDiagonalA1 = DIAGONAL_A1 << leftMove;
                if (bottomMove < 0)  preDiagonalH1 = DIAGONAL_H1 >>> -bottomMove;
                else preDiagonalH1 = DIAGONAL_H1 << bottomMove;

                // cut the diagonals that wrapped around the board
                if (leftMove < 0) {
                    for (int i = 0; i < -leftMove; i++) {
                        preDiagonalA1 &= ~(FILE_LAST >>> i);
                    }
                } else {
                    for (int i = 0; i < leftMove; i++) {
                        preDiagonalA1 &= ~(FILE_FIRST << i);
                    }
                }
                if (bottomMove < 0) {
                    for (int i = 0; i < -bottomMove; i++) {
                        preDiagonalH1 &= ~(FILE_LAST >>> (i * 8));
                    }
                } else {
                    for (int i = 0; i < bottomMove; i++) {
                        preDiagonalH1 &= ~(FILE_FIRST << (i * 8));
                    }
                }

                // moves are ready
                moves |= preDiagonalA1;
                moves |= preDiagonalH1;
                break;

            case KNIGHT, KNIGHT_B:
                // knight mask is based on c3
                int offsetRank = currentRank - 2;
                int offsetFile = currentFile - 2;

                int offset = offsetFile + offsetRank * 8;
                long preMask;

                // put knight mask on current position
                if (offset < 0) preMask = KNIGHT_C3 >>> -offset;
                else preMask = KNIGHT_C3 << offset;

                // cut legal moves, wrapped around the board
                if (offsetFile < 0) {
                    preMask &= ~(FILE_LAST | FILE_LAST >>> 1); // cutting files G and H
                } else if (offsetFile > 3) {
                    preMask &= ~(FILE_FIRST | FILE_FIRST << 1); // cutting files A and B
                }

                moves |= preMask;
                break;
        }
        return (moves &= ~position);
    }
}