package me.trigus.chess.game;

import me.trigus.chess.util.Bitmask;

public class Piece {

    private final PieceType pieceType;
    private long position;

    public Piece(PieceType pieceType, long position) {
        this.pieceType = pieceType;
        this.position = position;
    }
    public Piece(PieceType pieceType) {
        this (pieceType, 0L);
    }

    public long getLegalMoves (GameState gameState) {
        long rawMoves = pieceType.getRawMoves(position);
        long bitmaskAllPieces = gameState.getBitBoardAllPieces();

        if (pieceType == PieceType.PAWN || pieceType == PieceType.PAWN_B) {
            // pawns can't capture pieces directly
            rawMoves &= ~bitmaskAllPieces;

            // pawns can't double push if a piece is in the way
            if (pieceType.isWhite && (position & Bitmask.rank(1).mask()) != 0) {
                Piece front = gameState.getPiece(position << 8);
                if (front != null) rawMoves &= ~Bitmask.rank(3).mask();

            } else if (!pieceType.isWhite && (position & Bitmask.rank(6).mask()) != 0) {
                Piece front = gameState.getPiece(position >>> 8);
                if (front != null)
                    rawMoves &= ~Bitmask.rank(4).mask();
            }

            // add pawn captures including en-passant
            long pawnCaptureMask = 0L;
            if (pieceType.isWhite) {
                pawnCaptureMask |= position << 7;
                pawnCaptureMask |= position << 9;
            } else {
                pawnCaptureMask |= position >>> 7;
                pawnCaptureMask |= position >>> 9;
            }

            // filter board wraps
            if ((position & Bitmask.file(0).mask()) != 0x0L) pawnCaptureMask &= ~Bitmask.file(7).mask();
            if ((position & Bitmask.file(7).mask()) != 0x0L) pawnCaptureMask &= ~Bitmask.file(0).mask();

            rawMoves |= pawnCaptureMask & (gameState.getBitboardPieces(!getPieceType().isWhite) | gameState.getEnPassantPosition());
        }



        return rawMoves;
    }

    public long getPosition() {
        return position;
    }
    public void setPosition(long position) {
        this.position = position;
    }
    public PieceType getPieceType() {
        return pieceType;
    }

    @Override
    public String toString() {
        return pieceType.symbol + "";
    }
}
