package me.trigus.chess.io.command;

import me.trigus.chess.App;

public class DebugCommand extends AbstractCommand {
    public DebugCommand(String name) {
        super(name);
    }

    @Override
    public boolean run(String[] args) {
        if (args.length == 0) {
            App.getConsole().info("debug output is " + (App.getConsole().isDebug()));

        } else if (args[0].equalsIgnoreCase("true")) {
            App.getConsole().info("enabling debug output");
            App.getConsole().setDebug(true);

        } else if (args[0].equalsIgnoreCase("false")) {
            App.getConsole().info("disabling debug output");
            App.getConsole().setDebug(false);
        } else {
            App.getConsole().warning("invalid debug option - '"  + args[0] + "'");
        }
        return true;
    }

    @Override
    public String getHelp() {
        return "Controls the output of debug messages. Use '/debug' to view the current setting. Use '/debug [true|false]'" +
                "to change current setting.";
    }
}
