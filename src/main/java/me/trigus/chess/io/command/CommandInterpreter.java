package me.trigus.chess.io.command;

import me.trigus.chess.App;

public class CommandInterpreter {

    private final String COMMAND_PREFIX = "/";

    private final AbstractCommand[] commands =  new AbstractCommand[]{
            new QuitCommand("quit"),
            new StartCommand("start"),
            new HelpCommand("help"),
            new DebugCommand("debug"),
            new DrawCommand("draw"),
    };


    public boolean execute(String input) {
        if (input.startsWith(COMMAND_PREFIX)) {
            String command = input.substring(COMMAND_PREFIX.length());

            String[] split = command.split(" ");
            String[] args = new String[split.length - 1];

            if (split.length > 1) {
                System.arraycopy(split, 1, args, 0, split.length - 1);
            }

            for (AbstractCommand cmd : commands) {
                if (cmd.getName().equalsIgnoreCase(split[0])) {
                    return cmd.run(args);
                }
            }

            App.getConsole().warning("unknown command: " + command);
            return true;

        } else {
            return false;
        }
    }

    public AbstractCommand[] getCommands() {
        return commands;
    }

    public AbstractCommand getCommand(String command) {
        for (AbstractCommand cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(command)) {
                return cmd;
            }
        }
        return null;
    }

    public boolean isCommand (String input) {
        return input.startsWith(COMMAND_PREFIX);
    }
}
