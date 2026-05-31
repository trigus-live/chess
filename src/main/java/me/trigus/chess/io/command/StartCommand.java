package me.trigus.chess.io.command;

import me.trigus.chess.App;

public class StartCommand extends AbstractCommand {
    public StartCommand(String name) {
        super(name);
    }

    @Override
    public boolean run(String[] args) {
        App.getEngine().init();
        App.getEngine().drawBoard(0L);
        return true;
    }

    @Override
    public String getHelp() {
        return "initiates the engine and starts the game";
    }
}
