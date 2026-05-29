package me.trigus.chess.io.command;

import me.trigus.chess.App;

public class DrawCommand extends AbstractCommand {
    public DrawCommand(String name) {
        super(name);
    }

    @Override
    public boolean run(String[] args) {
        App.getEngine().drawBoard();
        return true;
    }

    @Override
    public String getHelp() {
        return "redraws the board";
    }
}
