package me.trigus.chess.util;

public record Bitmask (long mask) {

    public static Bitmask rank (int rank) {
        if (rank < 0 || rank > 7) throw new IllegalArgumentException();
        return new Bitmask (0xFFL << (rank * 8));
    }

    public static Bitmask rank (int start, int end) {
        long mask = 0x0L;
        for (int i = start; i <= end; i ++) {
            mask |= rank(i).mask;
        }
        return new Bitmask (mask);
    }

    public static Bitmask file (int file) {
        if (file < 0 || file > 7) throw new IllegalArgumentException();
        return new Bitmask(0x0101010101010101L << file);
    }

    public static Bitmask file (int start, int end) {
        long mask = 0x0L;
        for (int i = start; i <= end; i ++) {
            mask |= file(i).mask;
        }
        return new Bitmask(mask);
    }

    public static Bitmask diagonalA1 (int shift) {
        long mask = 0x8040201008040201L;
        if (shift < -7 || shift > 7) throw new IllegalArgumentException();

        if (shift < 0) {
            shift = -shift;
            mask >>>= 8 * shift + shift;
            mask <<= 8 * shift;
        } else {
            mask <<= 8 * shift + shift;
            mask >>>= 8 * shift;
        }

        return new Bitmask(mask);
    }

    public static Bitmask diagonalH1 (int shift) {
        long mask = 0x0102040810204080L;

        if (shift < 0) {
            shift = -shift;
            mask <<= 8 * shift - shift + 1;
            mask >>>= 8 * shift + 1;
        } else if (shift > 0) {
            mask >>>= 8 * shift - shift + 1;
            mask <<= 8 * shift + 1;
        }

        return new Bitmask(mask);
    }

    public static Bitmask castle (boolean isWhite, boolean castleQueen) {
        if (isWhite && castleQueen) return new Bitmask(0x000000000000000EL);
        if (isWhite) return new Bitmask(0x00000000000060L);
        if (castleQueen) return new Bitmask(0x0E00000000000000L);
        return new Bitmask(0x6000000000000000L);
    }

}
