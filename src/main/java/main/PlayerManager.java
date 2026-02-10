package main;

import java.awt.*;

public class PlayerManager {

    final int WIDTH = 360;
    final int HEIGHT = 600;

    final Color BORDER_COLOR = Color.WHITE;

    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    public PlayerManager() {
        left_x = (GamePanel.WIDTH / 2) - WIDTH / 2;
        right_x = (GamePanel.WIDTH / 2) + WIDTH / 2;
        top_y = 50;
        bottom_y = top_y + HEIGHT;
    }

    public void update(){


    }

    public void draw (Graphics2D g2){
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);
    }
}
