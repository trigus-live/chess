package me.trigus.chess.game;

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
