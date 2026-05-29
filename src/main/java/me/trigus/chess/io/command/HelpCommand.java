package me.trigus.chess.io.command;

import me.trigus.chess.App;

public class HelpCommand extends AbstractCommand {

    private final String[][] HELP_TOPICS = new String[][] {
            {"play", "enter your move in the form 'e2-e4' (or 'E2-E4') to move a piece from E2 to E4."}
    };

    public HelpCommand(String name) {
        super(name);
    }

    @Override
    public boolean run(String[] args) {
        if (args.length == 0) {
            App.getConsole().info(getHelp());
        } else {
            AbstractCommand command = App.getCommandInterpreter().getCommand(args[0]);
            String topicHelp = getTopicHelp(args[0]);

            if (command == null && topicHelp == null) {
                App.getConsole().warning("unknown command or topic: " + args[0] + ". Use '/help' for additional information.");
            } else if (command != null) {
                App.getConsole().info("- - - help for command " + command.getName() + " - - -");
                App.getConsole().info(command.getHelp());
            } else {
                App.getConsole().info("- - - help for topic " + args[0] + " - - -");
                App.getConsole().info(topicHelp);
            }
        }
        return true;
    }

    private String getTopicHelp (String topic) {
        for (String[] topics : HELP_TOPICS) {
            if (topic.equals(topics[0])) {
                return topics[1];
            }
        }
        return null;
    }

    @Override
    public String getHelp() {
        AbstractCommand[] commands = App.getCommandInterpreter().getCommands();

        StringBuilder sb = new StringBuilder();
        sb.append("- - - general help - - -");
        sb.append("\nUse '/help [keyword]' to view help on a command or topic.\n");

        sb.append("Available commands are: ");
        for (AbstractCommand command : commands) {
            sb.append(command.getName());
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(".\n");

        sb.append("Available topics are: ");
        for (String[] topics : HELP_TOPICS) {
            sb.append(topics[0]);
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(".");

        return sb.toString();
    }
}
