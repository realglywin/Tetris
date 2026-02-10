package main;

import javax.swing.*;
import java.awt.*;

public class TileManager extends JPanel {
    Graphics2D[] ScreenTiles; // Tile Size for each
    public TileManager(Graphics2D[] Screens){
        ScreenTiles = Screens;
    }

    public void paintComponent(Graphics2D g){
        super.paintComponent(g);
    }
}
