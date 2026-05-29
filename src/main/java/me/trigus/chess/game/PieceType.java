package me.trigus.chess.game;

public enum PieceType {
    PAWN('P'),
    KING('K'),
    QUEEN('Q'),
    ROOK('R'),
    BISHOP('B'),
    KNIGHT('N');

    public final char symbol;

    PieceType(char symbol) {
        this.symbol = symbol;
    }
}