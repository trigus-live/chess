package me.trigus.chess.io;

import java.util.Scanner;

public class Console {

    private static final String PREFIX_INFO = "[INFO]";
    private static final String PREFIX_ERROR = "[ERROR]";
    private static final String PREFIX_WARNING = "[WARNING]";
    private static final String PREFIX_DEBUG = "[DEBUG]";
    private static final String PROMPT = " > ";

    private final Scanner scanner;
    private boolean debug = false;

    public Console(Scanner scanner) {
        this.scanner = scanner;
    }

    public Console() {
        this.scanner = new Scanner(System.in);
    }

    public String getNextInput () {
        output(PROMPT);
        return scanner.nextLine();
    }

    public void info (String s) {
        output(PREFIX_INFO + normalizeMessage(s));
    }

    public void warning (String s) {
        output(PREFIX_WARNING + normalizeMessage(s));
    }

    public void error (String s) {
        output(PREFIX_ERROR + normalizeMessage(s));
    }

    public void debug (String s) {
        if (isDebug()) output(PREFIX_DEBUG + normalizeMessage(s));
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private String normalizeMessage (String message) {
        return " " + message + "\n";
    }

    private void output(String msg) {
        System.out.print(msg);
    }
}
