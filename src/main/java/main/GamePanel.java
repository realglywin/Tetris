package main;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    final int FPS = 60;

    final static int ORIGINAL_TILE_SIZE = 16;
    final static int SCALE = 2;

    final static int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;

    final int MAX_SCREEN_COLUMNS = 23;
    final int MAX_SCREEN_ROWS = 22;

    final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COLUMNS;
    final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROWS;

    KeyHandler keyHandler = new KeyHandler();
    Thread gameThread;
    ScreenManager screenManager;

    private final TetrisGame game = new TetrisGame();

    private long lastTickNs = System.nanoTime();

    private boolean prevRotate = false;
    private boolean prevHardDrop = false;
    private boolean prevHold = false;
    private boolean prevRestart = false;
    private boolean prevPause = false;

    private boolean prevLeft = false;
    private boolean prevRight = false;

    private static final int WELL_R = 0;
    private static final int WELL_C = 0;
    private static final int WELL_W = 12;
    private static final int WELL_H = 22;

    private static final int BOARD_R0 = WELL_R + 1;
    private static final int BOARD_C0 = WELL_C + 1;

    private static final int UI_R = 14;
    private static final int UI_C = 13;
    private static final int UI_W = 9;
    private static final int UI_H = 8;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        TileRegistry.loadTiles();
        screenManager = new ScreenManager(MAX_SCREEN_ROWS, MAX_SCREEN_COLUMNS);
    }

    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime / 1_000_000);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        long now = System.nanoTime();
        long deltaMs = Math.max(0, (now - lastTickNs) / 1_000_000);
        lastTickNs = now;

        boolean rotateJust = KeyHandler.rotatePressed && !prevRotate;
        boolean hardDropJust = KeyHandler.hardDropPressed && !prevHardDrop;
        boolean holdJust = KeyHandler.holdPressed && !prevHold;
        boolean restartJust = KeyHandler.restartPressed && !prevRestart;
        boolean pauseJust = KeyHandler.pausePressed && !prevPause;

        boolean leftJust = KeyHandler.leftPressed && !prevLeft;
        boolean rightJust = KeyHandler.rightPressed && !prevRight;

        prevRotate = KeyHandler.rotatePressed;
        prevHardDrop = KeyHandler.hardDropPressed;
        prevHold = KeyHandler.holdPressed;
        prevRestart = KeyHandler.restartPressed;
        prevPause = KeyHandler.pausePressed;

        prevLeft = KeyHandler.leftPressed;
        prevRight = KeyHandler.rightPressed;

        TetrisGame.InputFrame in = new TetrisGame.InputFrame(
                leftJust,                 // <- changed (was KeyHandler.leftPressed)
                rightJust,                // <- changed (was KeyHandler.rightPressed)
                KeyHandler.downPressed,
                rotateJust,
                hardDropJust,
                holdJust,
                restartJust,
                pauseJust
        );

        game.tick(deltaMs, in);

        screenManager.clear();

        screenManager.drawBox(WELL_R, WELL_C, WELL_W, WELL_H, 1);

        screenManager.drawBox(UI_R, UI_C, UI_W, UI_H, 2);

        int[][] board = game.getBoard();
        for (int r = 0; r < TetrisGame.BOARD_H; r++) {
            for (int c = 0; c < TetrisGame.BOARD_W; c++) {
                int id = board[r][c];
                if (id != 0) {
                    screenManager.setTile(BOARD_R0 + r, BOARD_C0 + c, id);
                }
            }
        }
        Piece cur = game.getCurrent();
        if (cur != null) {
            for (int r = 0; r < cur.shape.length; r++) {
                for (int c = 0; c < cur.shape[r].length; c++) {
                    if (cur.shape[r][c] == 0) continue;
                    int br = cur.row + r;
                    int bc = cur.col + c;
                    if (br >= 0) {
                        screenManager.setTile(BOARD_R0 + br, BOARD_C0 + bc, cur.tileID);
                    }
                }
            }
        }
    }

    private void drawNextPreview(Graphics2D g2, TetrominoType type, int x, int y) {
        if (type == null) return;

        int[][] shape = type.shape;
        int tileId = type.tileId;

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 0) continue;
                g2.drawImage(
                        TileRegistry.getTile(tileId),
                        x + c * TILE_SIZE,
                        y + r * TILE_SIZE,
                        TILE_SIZE, TILE_SIZE,
                        null
                );
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        screenManager.render(g2);

        int innerX = (UI_C + 1) * TILE_SIZE;
        int innerY = (UI_R + 1) * TILE_SIZE;

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Consolas", Font.PLAIN, 14));

        int textX = innerX + 6;
        int cursorY = innerY + 16;

        g2.drawString("NEXT", textX, cursorY);

        TetrominoType next = game.getNextPreview().isEmpty() ? null : game.getNextPreview().getFirst();
        int previewX = innerX + 6;
        int previewY = innerY + 22;
        drawNextPreview(g2, next, previewX, previewY);

        cursorY = innerY + (TILE_SIZE * 4) + 16; // after preview area
        g2.drawString("SCORE: " + game.getScore(), textX, cursorY);
        g2.drawString("LINES: " + game.getLines(), textX, cursorY + 16);
        g2.drawString("LEVEL: " + game.getLevel(), textX, cursorY + 32);

        if (game.isPaused()) {
            g2.drawString("PAUSED", textX, cursorY + 52);
        }
        if (game.isGameOver()) {
            g2.drawString("GAME OVER", textX, cursorY + 52);
            g2.drawString("Press R", textX, cursorY + 68);
        }
    }
}