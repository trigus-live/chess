package me.trigus.chess.io.command;

import me.trigus.chess.App;

public class QuitCommand extends AbstractCommand {

    public QuitCommand(String name) {
        super(name);
    }

    @Override
    public boolean run(String[] args) {
        App.setQuit();
        App.getConsole().info("quitting...");
        return true;
    }

    @Override
    public String getHelp() {
        return "quits the game :)";
    }
}
