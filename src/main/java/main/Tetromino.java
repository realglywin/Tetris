package main;

public abstract class Tetromino {
    public int[][] shape;
    public int row, col;
    public int tileID;

    protected static int[][] copyShape(int[][] src) {
        int[][] out = new int[src.length][src[0].length];
        for (int r = 0; r < src.length; r++) {
            System.arraycopy(src[r], 0, out[r], 0, src[r].length);
        }
        return out;
    }

    public void rotate() {
        int n = shape.length;
        int[][] rotated = new int[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                rotated[c][n - 1 - r] = shape[r][c];
            }
        }
        shape = rotated;
    }
}