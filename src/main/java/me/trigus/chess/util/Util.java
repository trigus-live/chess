package me.trigus.chess.util;

public class Util {

    /**
     * Converts index coords to a bit-encoded position
     * @param coords array of length 2 containing row and col indices ranging 0-7
     * @return 64-bit number with the bit corresponding to the position set to 1
     * lowest bit corresponds to A1, the highest bit to H8
     * returns 0 if the input array is invalid
     */
    public static long indexCoordsToPosition(int[] coords) {
        if (coords.length != 2) return 0L;
        if (coords[0] < 0 || coords[1] < 0 || coords[0] > 7 || coords[1] > 7) return 0L;

        long pos = 0x1L;
        pos = pos << coords[0] * 8;
        pos = pos << coords[1];
        return pos;
    }

    /**
     * Converts grid coords from A:1-H:8 to index coords from 0:0-8:8.
     * @param gridCoords string of length 2 containing grid coords in the format [A-H][1-8]
     * @return array of length 2 containing the corresponding row and col in range 0-7
     */
    public static int[] gridCoordsToIndexCoords(String gridCoords) {
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

    /**
     * Converts grid coords from A:1-H:8 to a bit-encoded position
     * @param gridCoords string of length 2 containing grid coords in the format [A-H][1-8]
     * @return 64-bit number with the bit corresponding to the position set to 1
     * lowest bit corresponds to A1, the highest bit to H8
     * returns 0 if the input string is invalid
     */
    public static long gridCoordsToPosition (String gridCoords) {
        int[] coords = gridCoordsToIndexCoords(gridCoords);
        if (coords == null) {
            return 0L;
        }

        return indexCoordsToPosition(coords);
    }

    /**
     * Converts bit-encoded position to index coords
     * @param position 64-bit number with the bit corresponding to the position set to 1
     * @return array of length 2 containing the corresponding row and col in range 0-7
     */
    public static int[] positionToIndexCoords(long position) {
        int index = (int) (Math.log(position) / Math.log(2));

        int currentRank = index / 8;
        int currentFile = index % 8;

        if (position < 0) {
            currentRank = 7;
            currentFile = 7;
        }

        return new int[]{currentRank, currentFile};
    }

    public static String positionToGridCoords(long position) {
        int[] indices =  positionToIndexCoords(position);

        return String.valueOf ((char) ('a' + indices[1])) +
                (char) ('1' + indices[0]);

        return sb;
    }
}
