package me.trigus.chess.io.command;

public abstract class AbstractCommand {

    private final String name;

    public AbstractCommand(String name) {
        this.name = name;
    }

    public abstract boolean run(String[] args);

    public abstract String getHelp ();

    public String getName() {
        return name;
    }
}
