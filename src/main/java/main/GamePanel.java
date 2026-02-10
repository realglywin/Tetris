package main;

import javax.security.auth.kerberos.KerberosTicket;
import javax.swing.*;
import java.awt.*;


public class GamePanel extends JPanel implements Runnable {

    final int FPS = 60;

    final int ORIGINAL_TILE_SIZE = 16;
    final int SCALE = 2;

    final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;
    final int MAX_SCREEN_COLUMNS = 12;
    final int MAX_SCREEN_ROWS = 22;

    final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COLUMNS;
    final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROWS;


    KeyHandler keyHandler = new KeyHandler();

    Thread gameThread;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
    }


    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        long currentTime = System.nanoTime();
        while (gameThread != null) {
            update();
            repaint();
            try {

                double remainingTime = nextDrawTime - System.nanoTime();

                if (remainingTime < 0) remainingTime = 0;

                Thread.sleep((long) remainingTime / 1_000_000);

                nextDrawTime += drawInterval;

            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
