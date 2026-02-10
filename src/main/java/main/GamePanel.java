package main;

import javax.swing.*;
import java.awt.*;


public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    public static final int FPS = 60;

    Thread gameThread;

    public static final Color BG_COLOR = Color.black;

    PlayerManager pm;
    ScoreBoard sb;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(BG_COLOR);
        this.setLayout(null);

        this.pm = new PlayerManager();
        this.sb = new ScoreBoard();



    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double updateFrame = 1_000_000_000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while ( gameThread.isAlive() ) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / updateFrame;
            lastTime = currentTime;
            if  ( delta >= 1 ) {
                update();
                repaint();
                delta = 0;
            }
        }

    }
    public void update(){
        pm.update();
        sb.update();

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        pm.draw(g2d);
        sb.draw(g2d);
    }
}
