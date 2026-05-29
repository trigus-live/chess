package me.trigus.chess.game;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    //private final Piece[][] pieces = new Piece[8][8]; // ROW COL
    private final List<Piece> piecesList = new ArrayList<>();

    private boolean whiteTurn = true;

    public GameState() {

    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void toggleWhiteTurn() {
        this.whiteTurn = !this.whiteTurn;
    }

    public void addPiece(Piece piece) {
        piecesList.add(piece);
    }

    public void removePiece(Piece piece) {
        piecesList.remove(piece);
    }

    public void removePiece (long position) {
        int index = -1;
        for (int i = 0; i < piecesList.size(); i++) {
            if (piecesList.get(i).getPosition() == position) {
                index = i;
            }
        }
        if (index != -1) {
            piecesList.remove(index);
        }
    }

    public Piece getPiece (long position) {
        for (Piece piece : piecesList) {
            if (piece.getPosition() == position) {
                return piece;
            }
        }
        return null;
    }

    /*public Piece getPiece(int row, int col) {
        return pieces[row][col];
    }*/

    /*public Piece[][] getPieces() {
        return pieces;
    }*/

    public void init () {
        piecesList.add(new Piece(PieceType.ROOK, 0x1L));
        piecesList.add(new Piece(PieceType.KNIGHT, 0x2L));
        piecesList.add(new Piece(PieceType.BISHOP, 0x4L));
        piecesList.add(new Piece(PieceType.QUEEN, 0x8L));
        piecesList.add(new Piece(PieceType.KING, 0x10L));
        piecesList.add(new Piece(PieceType.BISHOP, 0x20L));
        piecesList.add(new Piece(PieceType.KNIGHT, 0x40L));
        piecesList.add(new Piece(PieceType.ROOK, 0x80L));

        piecesList.add (new Piece (PieceType.ROOK_B, 0x100000000000000L));
        piecesList.add(new Piece (PieceType.KNIGHT_B, 0x200000000000000L));
        piecesList.add(new Piece (PieceType.BISHOP_B, 0x400000000000000L));
        piecesList.add(new Piece (PieceType.QUEEN_B, 0x800000000000000L));
        piecesList.add(new Piece (PieceType.KING_B, 0x1000000000000000L));
        piecesList.add(new Piece (PieceType.BISHOP_B, 0x2000000000000000L));
        piecesList.add(new Piece (PieceType.KNIGHT_B, 0x4000000000000000L));
        piecesList.add(new Piece (PieceType.ROOK, 0x8000000000000000L));

        long start = 0x100L;
        for (int i = 0; i < 8; i++) {
            piecesList.add(new Piece (PieceType.PAWN, start));
            long black = start << 5 * 8;
            piecesList.add(new Piece (PieceType.PAWN_B, black));
            start = start << 1;
        }
    }
}
