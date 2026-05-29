package me.trigus.chess.game;

public class Piece {

    private final boolean isWhite;
    private final PieceType type;

    private boolean hasMoved = false;

    public Piece(PieceType type, boolean isWhite) {
        this.type = type;
        this.isWhite = isWhite;
    }

    @Override
    public String toString() {
        return type.symbol + "";
    }

    public boolean isWhite() {
        return isWhite;
    }

    public PieceType getType() {
        return type;
    }

    public boolean isHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}
