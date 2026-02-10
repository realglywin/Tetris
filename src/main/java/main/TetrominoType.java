package main;

public enum TetrominoType {
    I(new int[][]{
            {0, 0, 0, 0},
            {1, 1, 1, 1},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
    }, 3),

    J(new int[][]{
            {1, 0, 0},
            {1, 1, 1},
            {0, 0, 0}
    }, 4),

    L(new int[][]{
            {0, 0, 1},
            {1, 1, 1},
            {0, 0, 0}
    }, 5),

    O(new int[][]{
            {1, 1},
            {1, 1}
    }, 6),

    S(new int[][]{
            {0, 1, 1},
            {1, 1, 0},
            {0, 0, 0}
    }, 7),

    T(new int[][]{
            {0, 1, 0},
            {1, 1, 1},
            {0, 0, 0}
    }, 8),

    Z(new int[][]{
            {1, 1, 0},
            {0, 1, 1},
            {0, 0, 0}
    }, 9);

    public final int[][] shape;
    public final int tileId;

    TetrominoType(int[][] shape, int tileId) {
        this.shape = shape;
        this.tileId = tileId;
    }
}