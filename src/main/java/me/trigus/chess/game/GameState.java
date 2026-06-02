package me.trigus.chess.game;

import me.trigus.chess.App;
import me.trigus.chess.util.Util;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    public static final String FEN_START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private final List<Piece> piecesList = new ArrayList<>();

    private boolean turnWhite;

    private boolean castleWhiteKing;
    private boolean castleWhiteQueen;
    private boolean castleBlackKing;
    private boolean castleBlackQueen;

    private long enPassantPosition;

    private int halfMoveCount;

    private int fullMoveCount;

    public GameState() {

    }

    public void init () {
        loadFen(FEN_START);
    }

    public void loadFen (String fen) {
        // check fen
        App.getConsole().warning("Checking validity of fen is not implemented yet. Proceed with caution!");
        // end check lol

        String[] parts =  fen.split(" ");
        if (parts.length != 6) {
            App.getConsole().warning("invalid fen, missing spaces");
            return;
        }

        // load pieces
        piecesList.clear();

        int rank = 7;
        int file = 0;
        for (char c : parts[0].toCharArray()) {
            if (c == '/') {
                rank--;
                file = 0;
            } else if (c >= '1' && c <= '8') {
                file += c - '0';
            } else {
                PieceType pieceType = PieceType.fromSymbol(c);
                if (pieceType != null) {
                    Piece piece = new Piece(pieceType, Util.indexCoordsToPosition(new int[]{rank, file}));
                    piecesList.add(piece);
                    file++;
                } else {
                    App.getConsole().warning("invalid fen, unknown piece type: " + c);
                    return;
                }
            }
        }

        // side to move
        if (parts[1].length() != 1) {
            App.getConsole().warning("invalid fen at section 2");
            return;
        }

        turnWhite = parts[1].charAt(0) == 'w';

        //castling
        if (parts[2].isEmpty() || parts[2].length() > 4) {
            App.getConsole().warning("invalid fen at section 3");
            return;
        }
        castleWhiteKing = parts[2].contains("K");
        castleWhiteQueen = parts[2].contains("Q");
        castleBlackKing = parts[2].contains("k");
        castleBlackQueen = parts[2].contains("q");

        // en passant
        if (!parts[3].equals("-")) {
            if (parts[3].length() != 2) {
                App.getConsole().warning("invalid fen  at section 4");
                return;
            }
            enPassantPosition = Util.gridCoordsToPosition(parts[3]);
        }

        try {
            halfMoveCount = Integer.parseInt(parts[4]);
        } catch (NumberFormatException e) {
            App.getConsole().warning("invalid fen at section 5");
            return;
        }

        try {
            fullMoveCount = Integer.parseInt(parts[5]);
        } catch (NumberFormatException e) {
            App.getConsole().warning("invalid fen at section 6");
            return;
        }

        App.getConsole().info("successfully loaded fen");
    }

    public String getFen () {
        StringBuilder sb = new StringBuilder();

        int[] lastPos = {7, -1};

        for (Piece piece : piecesList) {
            int[] currentPos = Util.positionToIndexCoords(piece.getPosition());

            while (lastPos[0] != currentPos[0]) {
                int spaceToEndOfRank = 7 - lastPos[1];
                if (spaceToEndOfRank > 0) sb.append(spaceToEndOfRank);
                sb.append("/");
                lastPos = new int[]{lastPos[0] - 1, -1};
            }

            if (currentPos[1] - lastPos[1] > 1) { // space between the two pieces
                sb.append(currentPos[1] - lastPos[1] - 1);
            }
            sb.append(piece);

            lastPos = currentPos;
        }


        int[] finalPos = {0, 8};
        while (lastPos[0] != finalPos[0]) {
            int spaceToEndOfRank = 7 - lastPos[1];
            if (spaceToEndOfRank > 0) sb.append(spaceToEndOfRank);
            sb.append("/");
            lastPos = new int[]{lastPos[0] - 1, -1};
        }

        if (finalPos[1] - lastPos[1] > 1) {
            sb.append(finalPos[1] - lastPos[1] - 1);
        }

        sb.append(" ");

        sb.append(turnWhite ? "w" : "b");

        sb.append(" ");

        boolean nobodyCanCastle = true;
        if (castleWhiteKing) { sb.append("K"); nobodyCanCastle = false; }
        if (castleWhiteQueen) { sb.append("Q"); nobodyCanCastle = false; }
        if (castleBlackKing) { sb.append("k"); nobodyCanCastle = false; }
        if (castleBlackQueen) { sb.append("q"); nobodyCanCastle = false; }
        if (nobodyCanCastle) {sb.append("-");}

        sb.append(" ");

        if (enPassantPosition == 0) sb.append("-");
        else sb.append(Util.positionToGridCoords(enPassantPosition));

        sb.append(" ");

        sb.append(halfMoveCount);

        sb.append(" ");

        sb.append(fullMoveCount);

        return sb.toString();
    }

    public boolean isTurnWhite() {
        return turnWhite;
    }
    public void setTurnWhite(boolean turnWhite) {
        this.turnWhite = turnWhite;
    }
    public void toggleTurnWhite () {
        turnWhite = !turnWhite;
    }

    public boolean isCastleWhiteKing() {
        return castleWhiteKing;
    }
    public void setCastleWhiteKing(boolean castleWhiteKing) {
        this.castleWhiteKing = castleWhiteKing;
    }
    public boolean isCastleWhiteQueen() {
        return castleWhiteQueen;
    }
    public void setCastleWhiteQueen(boolean castleWhiteQueen) {
        this.castleWhiteQueen = castleWhiteQueen;
    }
    public boolean isCastleBlackKing() {
        return castleBlackKing;
    }
    public void setCastleBlackKing(boolean castleBlackKing) {
        this.castleBlackKing = castleBlackKing;
    }
    public boolean isCastleBlackQueen() {
        return castleBlackQueen;
    }
    public void setCastleBlackQueen(boolean castleBlackQueen) {
        this.castleBlackQueen = castleBlackQueen;
    }

    public long getEnPassantPosition() {
        return enPassantPosition;
    }
    public void setEnPassantPosition(long enPassantPosition) {
        this.enPassantPosition = enPassantPosition;
    }

    public int getHalfMoveCount() {
        return halfMoveCount;
    }
    public void setHalfMoveCount(int halfMoveCount) {
        this.halfMoveCount = halfMoveCount;
    }
    public void incrementHalfMoveCount() {
        halfMoveCount++;
    }
    public int getFullMoveCount() {
        return fullMoveCount;
    }
    public void setFullMoveCount(int fullMoveCount) {
        this.fullMoveCount = fullMoveCount;
    }
    public void incrementFullMoveCount() {
        fullMoveCount++;
    }

    public long getBitmaskPieces(boolean isWhite) {
        long board = 0x0L;
        for (Piece piece : piecesList) {
            if (piece.getPieceType().isWhite == isWhite) board = board | piece.getPosition ();
        }

        return board;
    }
    public long getBitmaskAllPieces() {
        return  getBitmaskPieces(true) | getBitmaskPieces(false);
    }
    public long getBitmaskAttackingSquares (boolean isWhite) {
        long board = 0x0L;
        for (Piece piece : piecesList) {
            if (piece.getPieceType().isWhite == isWhite) {
                board |= piece.getLegalMoves(this);
            }
        }

        return board;
    }


    public void addPiece(Piece piece) {

        int[] indices = Util.positionToIndexCoords(piece.getPosition());

        for (int i = 0; i <= piecesList.size(); i++) {

            if (i == piecesList.size()) {
                piecesList.add(piece);
                break;
            }

            long currentPosition = piecesList.get(i).getPosition();
            int[] currentIndices = Util.positionToIndexCoords(currentPosition);

            if (indices[0] > currentIndices[0]) {
                piecesList.add(i, piece);
                break;
            } else if (indices[0] == currentIndices[0]){
                if (indices[1] < currentIndices[1]) {
                    piecesList.add(i, piece);
                    break;
                }
            }

        }
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
}
