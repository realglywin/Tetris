package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public static boolean leftPressed = false;
    public static boolean rightPressed = false;
    public static boolean rotatePressed = false;
    public static boolean downPressed = false;

    public static boolean hardDropPressed = false;
    public static boolean holdPressed = false;
    public static boolean restartPressed = false;
    public static boolean pausePressed = false;

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_LEFT -> leftPressed = true;
            case KeyEvent.VK_RIGHT -> rightPressed = true;
            case KeyEvent.VK_DOWN -> downPressed = true;
            case KeyEvent.VK_UP -> rotatePressed = true;

            case KeyEvent.VK_SPACE -> hardDropPressed = true;
            case KeyEvent.VK_C -> holdPressed = true;
            case KeyEvent.VK_R -> restartPressed = true;
            case KeyEvent.VK_P -> pausePressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_LEFT -> leftPressed = false;
            case KeyEvent.VK_RIGHT -> rightPressed = false;
            case KeyEvent.VK_DOWN -> downPressed = false;
            case KeyEvent.VK_UP -> rotatePressed = false;

            case KeyEvent.VK_SPACE -> hardDropPressed = false;
            case KeyEvent.VK_C -> holdPressed = false;
            case KeyEvent.VK_R -> restartPressed = false;
            case KeyEvent.VK_P -> pausePressed = false;
        }
    }
}