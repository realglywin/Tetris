package main;

import javax.swing.*;
import java.awt.*;

public class Tile extends JPanel {
    int X;S
    int Y;
    Image tileImage;

    public Tile(int x, int y){
        X = x;
        Y = y;
    }

    public void paintComponent(Graphics2D g2){
        super.paintComponent(g2);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(tileImage, X, Y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);
    }
}
