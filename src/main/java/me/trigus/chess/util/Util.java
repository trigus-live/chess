package me.trigus.chess.util;

public class Util {

    public static long coordsToPosition (int row, int col) {
        long pos = 0x1L;
        pos = pos << row * 8;
        pos = pos << col;
        return pos;
    }


}
