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

    public long getRawMoves (long position, GameState gameState) {
        final long RANK_FIRST = 0x00000000000000FFL;
        final long RANK_LAST = 0xFF00000000000000L;
        final long FILE_FIRST = 0x0101010101010101L;
        final long FILE_LAST = 0x8080808080808080L;
        final long DIAGONAL_A1 = 0x8040201008040201L;
        final long DIAGONAL_H1 = 0x0102040810204080L;
        final long KNIGHT_C3 = 0x0000000A1100110AL;

        long moves = 0;

        int[] indices = Util.positionToIndexCoords(position);
        int currentRank = indices[0];
        int currentFile = indices[1];

        App.getConsole().debug("working with position/rank/file: " + position + " " + currentRank + " " + currentFile);



        switch (this) {
            case PAWN:
                if ((position & RANK_FIRST << 8) != 0L) {
                    if (gameState.getPiece (position << 8) == null)
                        moves |= position << 16;
                }
                moves |= position << 8;

                moves |= moves & ~gameState.getBitBoardAllPieces();

                long topLeftPosition = position << 7;
                long topRightPosition = position << 9;

                if ((position & FILE_FIRST) != 0) topLeftPosition = 0L;
                if ((position & FILE_LAST) != 0) topRightPosition = 0L;

                Piece topLeft = gameState.getPiece(topLeftPosition);
                Piece topRight = gameState.getPiece(topRightPosition);



                if (gameState.getEnPassantPosition() == topLeftPosition || topLeft != null && topLeft.getType().isWhite != isWhite) moves |= topLeftPosition;
                if (gameState.getEnPassantPosition() == topRightPosition || topRight != null && topRight.getType().isWhite != isWhite) moves |= topRightPosition;

                break;

            case PAWN_B:
                if ((position & RANK_FIRST << (8 * 6)) != 0) {
                    if (gameState.getPiece (position >>> 8) == null)
                        moves |= position >>> 16;
                }
                moves |= position >> 8;

                moves |= moves & ~gameState.getBitBoardAllPieces();

                long bottomLeftPosition = position >>> 9;
                long bottomRightPosition = position >>> 7;

                if ((position & FILE_FIRST) != 0) bottomLeftPosition = 0L;
                if ((position & FILE_LAST) != 0) bottomRightPosition = 0L;

                Piece bottomLeft = gameState.getPiece(bottomLeftPosition);
                Piece bottomRight = gameState.getPiece(bottomRightPosition);

                if (gameState.getEnPassantPosition() == bottomLeftPosition || bottomLeft != null && bottomLeft.getType().isWhite != isWhite) moves |= position >> 9;
                if (gameState.getEnPassantPosition() == bottomRightPosition || bottomRight != null && bottomRight.getType().isWhite != isWhite) moves |= position >> 7;



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

                if (gameState.isCastleWhiteQueen() && gameState.isTurnWhite()) {
                    long castleMask = 0xCL;
                    if ((castleMask & gameState.getBitBoardAllPieces()) == 0) {
                        moves |= position >>> 2;
                    }
                } else if (gameState.isCastleWhiteKing() && gameState.isTurnWhite()) {
                    long castleMask = 0x60L;
                    if ((castleMask & gameState.getBitBoardAllPieces()) == 0) {
                        moves |= position << 2;
                    }
                } else if (gameState.isCastleBlackQueen() && !gameState.isTurnWhite()) {
                    long castleMask = 0xC00000000000000L;
                    if ((castleMask & gameState.getBitBoardAllPieces()) == 0) {
                        moves |= position >>> 2;
                    }
                } else if (gameState.isCastleBlackKing() && !gameState.isTurnWhite()) {
                    long castleMask = 0x6000000000000000L;
                    if ((castleMask & gameState.getBitBoardAllPieces()) == 0) {
                        moves |= position << 2;
                    }
                }

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
        return (moves & ~position);
    }
}