package main;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class TetrisGame {
    public static final int BOARD_W = 10;
    public static final int BOARD_H = 20;

    private final int[][] board = new int[BOARD_H][BOARD_W];

    private final Deque<TetrominoType> nextQueue = new ArrayDeque<>();
    private List<TetrominoType> bag = new ArrayList<>();
    private int bagIndex = 0;

    private Piece current;
    private TetrominoType hold = null;
    private boolean holdUsedThisTurn = false;

    private boolean paused = false;
    private boolean gameOver = false;

    private int score = 0;
    private int lines = 0;
    private int level = 1;

    private long fallTimerMs = 0;
    private long fallIntervalMs = 800;

    public TetrisGame() {
        refillBag();
        for (int i = 0; i < 5; i++) nextQueue.addLast(drawFromBag());
        spawnNext();
        recomputeFallInterval();
    }

    public void reset() {
        for (int r = 0; r < BOARD_H; r++) {
            for (int c = 0; c < BOARD_W; c++) board[r][c] = 0;
        }
        nextQueue.clear();
        bag.clear();
        bagIndex = 0;
        hold = null;
        holdUsedThisTurn = false;
        paused = false;
        gameOver = false;

        score = 0;
        lines = 0;
        level = 1;

        fallTimerMs = 0;
        fallIntervalMs = 800;

        refillBag();
        for (int i = 0; i < 5; i++) nextQueue.addLast(drawFromBag());
        spawnNext();
        recomputeFallInterval();
    }

    public void togglePause() {
        if (!gameOver) paused = !paused;
    }

    public boolean isPaused() { return paused; }
    public boolean isGameOver() { return gameOver; }

    public int getScore() { return score; }
    public int getLines() { return lines; }
    public int getLevel() { return level; }

    public TetrominoType getHold() { return hold; }
    public List<TetrominoType> getNextPreview() { return List.copyOf(nextQueue); }

    public int[][] getBoard() { return board; }
    public Piece getCurrent() { return current; }

    public void tick(long deltaMs, InputFrame in) {
        if (in.pausePressed) togglePause();
        if (paused) return;

        if (gameOver) {
            if (in.restartPressed) reset();
            return;
        }
        if (in.leftPressed) tryMove(-1, 0);
        if (in.rightPressed) tryMove(1, 0);

        if (in.rotatePressed) tryRotateWithKicks();

        if (in.holdPressed) hold();

        if (in.hardDropPressed) {
            int dropped = hardDrop();
            score += 2 * dropped;
            lockPiece();
            return;
        }

        long interval = in.downPressed ? Math.max(30, fallIntervalMs / 10) : fallIntervalMs;

        fallTimerMs += deltaMs;
        while (fallTimerMs >= interval) {
            fallTimerMs -= interval;
            if (!tryMove(0, 1)) {
                lockPiece();
                break;
            } else if (in.downPressed) {
                score += 1;
            }
        }
    }

    private void hold() {
        if (holdUsedThisTurn) return;

        TetrominoType curType = current.type;
        if (hold == null) {
            hold = curType;
            spawnNext();
        } else {
            TetrominoType tmp = hold;
            hold = curType;
            spawn(tmp);
        }
        holdUsedThisTurn = true;
    }

    private int hardDrop() {
        int n = 0;
        while (tryMove(0, 1)) n++;
        return n;
    }

    private boolean tryMove(int dx, int dy) {
        Piece p = current.copy();
        p.col += dx;
        p.row += dy;
        if (collides(p)) return false;
        current = p;
        return true;
    }

    private void tryRotateWithKicks() {
        Piece p = current.copy();
        p.rotate();
        if (!collides(p)) {
            current = p;
            return;
        }

        int[] kicks = {1, -1, 2, -2};
        for (int k : kicks) {
            Piece pk = p.copy();
            pk.col += k;
            if (!collides(pk)) {
                current = pk;
                return;
            }
        }
    }

    private boolean collides(Piece p) {
        for (int r = 0; r < p.shape.length; r++) {
            for (int c = 0; c < p.shape[r].length; c++) {
                if (p.shape[r][c] == 0) continue;

                int br = p.row + r;
                int bc = p.col + c;

                if (bc < 0 || bc >= BOARD_W) return true;
                if (br >= BOARD_H) return true;

                if (br >= 0 && board[br][bc] != 0) return true;
            }
        }
        return false;
    }

    private void lockPiece() {
        for (int r = 0; r < current.shape.length; r++) {
            for (int c = 0; c < current.shape[r].length; c++) {
                if (current.shape[r][c] == 0) continue;

                int br = current.row + r;
                int bc = current.col + c;
                if (br >= 0 && br < BOARD_H && bc >= 0 && bc < BOARD_W) {
                    board[br][bc] = current.tileID;
                }
            }
        }

        int cleared = clearLines();
        if (cleared > 0) {
            int base = switch (cleared) {
                case 1 -> 40;
                case 2 -> 100;
                case 3 -> 300;
                default -> 1200;
            };
            score += base * level;
            lines += cleared;
            level = (lines / 10) + 1;
            recomputeFallInterval();
        }

        holdUsedThisTurn = false;
        spawnNext();
    }

    private int clearLines() {
        int cleared = 0;

        for (int r = BOARD_H - 1; r >= 0; r--) {
            boolean full = true;
            for (int c = 0; c < BOARD_W; c++) {
                if (board[r][c] == 0) { full = false; break; }
            }
            if (full) {
                cleared++;
                for (int rr = r; rr > 0; rr--) {
                    System.arraycopy(board[rr - 1], 0, board[rr], 0, BOARD_W);
                }
                for (int c = 0; c < BOARD_W; c++) board[0][c] = 0;
                r++;
            }
        }
        return cleared;
    }

    private void spawnNext() {
        TetrominoType next = nextQueue.removeFirst();
        nextQueue.addLast(drawFromBag());
        spawn(next);
    }

    private void spawn(TetrominoType type) {
        int startCol = (BOARD_W / 2) - 2;
        current = new Piece(type, -1, startCol);

        if (collides(current)) {
            current.row = 0;
            if (collides(current)) {
                gameOver = true;
            }
        }
    }

    private void recomputeFallInterval() {
        long v = 800 - (level - 1) * 60L;
        fallIntervalMs = Math.max(80, v);
    }

    private void refillBag() {
        bag = new ArrayList<>(List.of(TetrominoType.values()));
        Collections.shuffle(bag);
        bagIndex = 0;
    }

    private TetrominoType drawFromBag() {
        if (bagIndex >= bag.size()) refillBag();
        return bag.get(bagIndex++);
    }

    public static final class InputFrame {
        public final boolean leftPressed;
        public final boolean rightPressed;
        public final boolean downPressed;
        public final boolean rotatePressed;
        public final boolean hardDropPressed;
        public final boolean holdPressed;
        public final boolean restartPressed;
        public final boolean pausePressed;

        public InputFrame(boolean leftPressed, boolean rightPressed, boolean downPressed,
                          boolean rotatePressed, boolean hardDropPressed, boolean holdPressed,
                          boolean restartPressed, boolean pausePressed) {
            this.leftPressed = leftPressed;
            this.rightPressed = rightPressed;
            this.downPressed = downPressed;
            this.rotatePressed = rotatePressed;
            this.hardDropPressed = hardDropPressed;
            this.holdPressed = holdPressed;
            this.restartPressed = restartPressed;
            this.pausePressed = pausePressed;
        }
    }
}