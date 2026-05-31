package me.trigus.chess;

import me.trigus.chess.game.Engine;
import me.trigus.chess.io.command.CommandInterpreter;
import me.trigus.chess.io.Console;

public class App {

    private static Console console;
    private static Engine engine;
    private static CommandInterpreter commandInterpreter;

    private static boolean quit = false;

    static void main(String[] args) {
        console = new Console();
        engine = new Engine();
        commandInterpreter = new CommandInterpreter();

        console.info("Welcome to Chess!");

        try {
            while (!quit) {
                String input = console.getNextInput();

                if (commandInterpreter.isCommand(input)) {
                    boolean success = commandInterpreter.execute(input);
                    if (!success) {
                        console.error("command execution failed!");
                    }
                } else {
                    boolean success = engine.move(input);
                    if (success) engine.drawBoard(0L);
                }
            }
        }  catch (Exception e) {
            console.error("- - - the game crashed :( - - -");
            console.error(e.getMessage());
        }
    }

    public static void setQuit () {
        quit = true;
    }

    public static Engine getEngine() {
        return engine;
    }

    public static Console getConsole() {
        return console;
    }

    public static CommandInterpreter getCommandInterpreter() {
        return commandInterpreter;
    }
}
