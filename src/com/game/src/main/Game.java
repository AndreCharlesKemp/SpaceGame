package com.game.src.main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Game extends Canvas implements Runnable {

    //    Create the game box & set Title
    public static final int WIDTH = 320;
    public static final int HEIGHT = WIDTH / 12 * 9;
    public static final int SCALE = 2;
    public final String TITLE = "2D Space Game";

    private boolean running = false;
    private Thread thread;

    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private BufferedImage spriteSheet = null;

    public void init() {
        BufferedImageLoader loader = new BufferedImageLoader();
        try{

            spriteSheet = loader.loadImage("src/res/SpriteSheet.png");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private synchronized void start() {
        if (running) return;

        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private synchronized void stop() {
        if (!running) return;

        running = false;
        try {
            thread.join();                  // Join all the threads together and wait for them to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    @Override
    public void run() {
        init();
        long lastTime = System.nanoTime();
        final double amountOfTicks = 60.0;  // Set the frameRate
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int updates = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();

        // Game Loop - handles updates and rendering images
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                tick();
                updates++;
                delta--;
            }

            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(updates + " Ticks, Fps " + frames);
                updates = 0;
                frames = 0;
            }
        }
        stop();
    }

    // Everything in the game that updates
    private void tick() {

    }

    // Everything in the game that renders
    private void render() {
        BufferStrategy bs = this.getBufferStrategy();

        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        //////////////////////////////////
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        //////////////////////////////////
        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {

//        Create new instance of game class
        Game game = new Game();

//        Set size
        game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

//        Create new JFrame object
        JFrame frame = new JFrame(game.TITLE);
        frame.add(game);
        frame.pack();                                           // Sets the size to to that of it's contents
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // Define what the exit button does
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);                      // Centre the game to middle of the screen
        frame.setVisible(true);

        game.start();
    }


}
