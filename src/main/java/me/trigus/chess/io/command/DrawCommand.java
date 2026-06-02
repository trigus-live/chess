package me.trigus.chess.io.command;

import me.trigus.chess.App;
import me.trigus.chess.game.Piece;
import me.trigus.chess.util.Util;

public class DrawCommand extends AbstractCommand {
    public DrawCommand(String name) {
        super(name);
    }

    @Override
    public boolean run(String[] args) {
        long highlightMask = 0L;

        if (args.length > 0) {
            // show legal moves
            if (args[0].equals ("lm")) {
                if (args.length < 2) {
                    App.getConsole ().warning ("missing argument for lm");
                    return true;
                }

                String coords = args[1];
                long position = Util.gridCoordsToPosition (coords);

                Piece piece = App.getEngine ().getGameState ().getPiece (position);
                if (piece == null) {
                    App.getConsole ().warning ("no piece at target square");
                    return true;
                }

                highlightMask = piece.getType ().getRawMoves (position);
            }
        }


        App.getEngine().drawBoard(highlightMask);
        return true;
    }

    @Override
    public String getHelp() {
        return "redraws the board";
    }
}
