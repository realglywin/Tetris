package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TileRegistry {
    private static final Map<Integer, BufferedImage> tiles = new HashMap<>();

    public static void loadTiles() {
        try {
            for (int id = 1; id <= 11; id++) {
                String fileName = String.format("/blocks/tile%03d.png", id - 1);

                try (InputStream in = TileRegistry.class.getResourceAsStream(fileName)) {
                    if (in == null) {
                        throw new IllegalStateException("Missing resource on classpath: " + fileName);
                    }
                    tiles.put(id, ImageIO.read(in));
                }
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Error loading tiles: Ensure images are in resources/blocks");
            e.printStackTrace();
        }
    }

    public static BufferedImage getTile(int id) {
        return tiles.get(id);
    }
}