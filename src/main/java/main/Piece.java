package main;

public class Piece extends Tetromino {
    public final TetrominoType type;

    public Piece(TetrominoType type, int row, int col) {
        this.type = type;
        this.shape = copyShape(type.shape);
        this.tileID = type.tileId;
        this.row = row;
        this.col = col;
    }

    public Piece copy() {
        Piece p = new Piece(this.type, this.row, this.col);
        p.shape = copyShape(this.shape);
        p.tileID = this.tileID;
        return p;
    }
}