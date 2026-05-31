package me.trigus.chess.game;

public class Piece {

    private final PieceType type;
    private long position = 0L;

    public Piece(PieceType type, long position) {
        this.type = type;
        this.position = position;
    }

    public Piece(PieceType type) {
        this (type, 0L);
    }

    @Override
    public String toString() {
        return type.symbol + "";
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public PieceType getType() {
        return type;
    }
}
