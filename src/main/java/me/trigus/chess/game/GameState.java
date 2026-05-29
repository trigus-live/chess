package me.trigus.chess.game;

public class GameState {

    private final Piece[][] pieces = new Piece[8][8]; // ROW COL

    private boolean whiteTurn = true;

    public GameState() {

    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    public void toggleWhiteTurn() {
        this.whiteTurn = !this.whiteTurn;
    }

    public Piece getPiece(int row, int col) {
        return pieces[row][col];
    }

    public Piece[][] getPieces() {
        return pieces;
    }

    public void init () {
        pieces[0][0] = new Piece(PieceType.ROOK, true);
        pieces[0][1] = new Piece(PieceType.KNIGHT, true);
        pieces[0][2] = new Piece(PieceType.BISHOP, true);
        pieces[0][3] = new Piece(PieceType.QUEEN, true);
        pieces[0][4] = new Piece(PieceType.KING, true);
        pieces[0][5] = new Piece(PieceType.BISHOP, true);
        pieces[0][6] = new Piece(PieceType.KNIGHT, true);
        pieces[0][7] = new Piece(PieceType.ROOK, true);
        pieces[7][0] = new Piece(PieceType.ROOK, false);
        pieces[7][1] = new Piece(PieceType.KNIGHT, false);
        pieces[7][2] = new Piece(PieceType.BISHOP, false);
        pieces[7][3] = new Piece(PieceType.QUEEN, false);
        pieces[7][4] = new Piece(PieceType.KING, false);
        pieces[7][5] = new Piece(PieceType.BISHOP, false);
        pieces[7][6] = new Piece(PieceType.KNIGHT, false);
        pieces[7][7] = new Piece(PieceType.ROOK, false);

        for (int i = 0; i < 8; i++) {
            pieces[1][i] = new Piece(PieceType.PAWN, true);
            pieces[6][i] = new Piece(PieceType.PAWN, false);
        }
    }
}
