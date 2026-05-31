package me.trigus.chess.util;

public class Util {

    public static long coordsToPosition (int row, int col) {
        long pos = 0x1L;
        pos = pos << row * 8;
        pos = pos << col;
        return pos;
    }

    public static int[] gridCoordsToRowColBase0 (String gridCoords) {
        if (gridCoords == null || gridCoords.length() != 2) {
            return null;
        }

        char[] chars = gridCoords.toUpperCase().toCharArray();
        char file =  chars[0];
        char rank = chars[1];

        int col = file - 'A';
        int row = rank - '1';

        return new int[]{row, col};
    }

    public static long gridCoordsToPosition (String gridCoords) {
        int[] coords = gridCoordsToRowColBase0(gridCoords);
        if (coords == null) {
            return 0L;
        }

        return coordsToPosition(coords[0], coords[1]);
    }
}
