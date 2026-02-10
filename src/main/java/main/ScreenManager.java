package main;

import java.awt.*;

public class ScreenManager {
    private final int rows, cols;
    private final int[][] grid;

    public ScreenManager(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows][cols];
    }

    public void clear() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = 0;
            }
        }
    }

    public void drawBox(int startR, int startC, int w, int h, int tileID) {
        for (int r = startR; r < startR + h; r++) {
            for (int c = startC; c < startC + w; c++) {
                if (r == startR || r == startR + h - 1 || c == startC || c == startC + w - 1) {
                    grid[r][c] = tileID;
                }
            }
        }
    }

    public void setTile(int r, int c, int id) {
        if (r >= 0 && r < rows && c >= 0 && c < cols) grid[r][c] = id;
    }

    public void render(Graphics2D g2) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int id = grid[r][c];
                if (id != 0) {
                    g2.drawImage(TileRegistry.getTile(id), c * GamePanel.TILE_SIZE, r * GamePanel.TILE_SIZE,
                            GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);
                }
            }
        }
    }
}