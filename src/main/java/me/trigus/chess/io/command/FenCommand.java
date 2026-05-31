package me.trigus.chess.io.command;

import me.trigus.chess.App;

public class FenCommand extends AbstractCommand{

    public FenCommand(String name) {
        super(name);
    }

    @Override
    public boolean run(String[] args) {
        if (args.length == 0) {
            return run(new String[]{"print"});

        } else {
            switch (args[0]) {
                case "load":
                    if (args.length < 2) {
                        App.getConsole().warning("missing fen");
                        return true;
                    }

                    StringBuilder fen = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        fen.append(args[i]).append(" ");
                    }
                    fen.deleteCharAt(fen.length() - 1);

                    App.getEngine().getGameState().loadFen(fen.toString());

                    return true;
                case "print":
                    App.getConsole().info(App.getEngine().getGameState().getFen());
                    return true;
            }
        }
        return false;
    }

    @Override
    public String getHelp() {
        return "use '/fen print' to print fen of current game\nuse '/fen load' to load fen into game";
    }
}
