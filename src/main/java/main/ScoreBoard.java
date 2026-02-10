package main;

import java.awt.*;

public class ScoreBoard {

    final int LENGTH = 300;

    final Color BORDER_COLOR = Color.WHITE;

    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    public ScoreBoard() {
        left_x = PlayerManager.right_x + 50;
        right_x = left_x + LENGTH;
        bottom_y = PlayerManager.bottom_y;
        top_y = bottom_y + LENGTH;
    }

    public void update(){


    }

    public void draw (Graphics2D g2){
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x, top_y, LENGTH, LENGTH);
    }
}
