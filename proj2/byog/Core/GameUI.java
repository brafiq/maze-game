package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
//import edu.princeton.cs.introcs.StdAudio;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;


public class GameUI {

    static TERenderer ter = new TERenderer();

    /**
     * Display the menu screen, which consists of: Title, New Game, Load Game,
     * and Quit.
     */
    static void displayMenu() {
        StdDraw.setPenColor(Color.WHITE);
        int textY = Game.HEIGHT / 2 + 10;
        StdDraw.text(Game.WIDTH / 2, textY, "CS61B: Obsessive Flower Collector");
        Font jeff = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(jeff);
        StdDraw.text(Game.WIDTH / 2, textY - 5, "New Game (Press N)");
        StdDraw.text(Game.WIDTH / 2, textY - 10, "New Game: 2 Players (Press 2)");
        StdDraw.text(Game.WIDTH / 2, textY - 15, "Load Game (Press L)");
        StdDraw.text(Game.WIDTH / 2, textY - 20, "Quit (Press Q)");

        StdDraw.show();
    }

    /**
     * Display the New Game screen which prompts the user for a seed.
     */
    static void drawNewGameScreen() {
        Font brian = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(brian);
        String instruction = "Please enter a seed, and then press S: ";
        StdDraw.text(Game.WIDTH / 2, (Game.HEIGHT / 2), instruction);
        StdDraw.show();
    }

    /**
     * Displays the characters being typed on the screen. This method is specifically
     * for soliciting a seed input.
     */
    static void drawInput(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.text(Game.WIDTH / 2, (Game.HEIGHT / 2) - 5, s);
        drawNewGameScreen();
    }

    /**
     * Enables seed inputs while on the input-seed screen, which is
     * accessed after selecting New Game.
     *
     * @return
     */
    static String solicitSeedInput() {
        String soFar = "";
        char charTyped;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                charTyped = StdDraw.nextKeyTyped();
                if (charTyped == 'S' || charTyped == 's') {
                    //StdAudio.play("/buttonClick.wav");
                    return soFar;
                }
                soFar += charTyped;
                drawInput(soFar);
            }
        }
    }

    /**
     * Activates the game keys in game
     * (i.e. the ability to move character, as well as quit and save while in game).
     */
    static void activateGameKeysAndHUD(WorldGenerator wg) {
        char charTyped;
        String soFar = "";
        while (true) {
            if (StdDraw.isMousePressed() && !wg.twoPlayers) {
                double newXPos = StdDraw.mouseX();
                double newYPos = StdDraw.mouseY();
                Position newPos = new Position((int) newXPos, (int) newYPos);
                wg.player.clickChangePos(newPos);
            } else if (StdDraw.hasNextKeyTyped()) {
                charTyped = StdDraw.nextKeyTyped();
                soFar += charTyped;
                if (!wg.twoPlayers) {
                    if (charTyped == 'w') {
                        wg.player.moveUp();
                    } else if (charTyped == 's') {
                        wg.player.moveDown();
                    } else if (charTyped == 'a') {
                        wg.player.moveLeft();
                    } else if (charTyped == 'd') {
                        wg.player.moveRight();
                    } else if (soFar.contains(":Q") || soFar.contains(":q")) {
                        saveWorld(wg);
                        System.exit(0);
                    }
                } else {
                    //Player 1
                    if (charTyped == 'w') {
                        wg.player.moveUp();
                    } else if (charTyped == 's') {
                        wg.player.moveDown();
                    } else if (charTyped == 'a') {
                        wg.player.moveLeft();
                    } else if (charTyped == 'd') {
                        wg.player.moveRight();
                        //Player 2
                    } else if (charTyped == 'i') {
                        wg.player2.moveUp();
                    } else if (charTyped == 'k') {
                        wg.player2.moveDown();
                    } else if (charTyped == 'j') {
                        wg.player2.moveLeft();
                    } else if (charTyped == 'l') {
                        wg.player2.moveRight();
                    } else if (soFar.contains(":Q") || soFar.contains(":q")) {
                        saveWorld(wg);
                        System.exit(0);
                    }
                }
            }
            ter.renderFrame(wg.world);
            flowersRemaining(wg);
            hUD(wg);
            if (wg.numFlowers == 0) {
                StdDraw.pause(500);
                break;
            }
        }
    }

    /**
     * Displays the game over screen with the following options:
     * New game 1 player, new game 2 player, and quit game.
     */
    static void displayGameOverScreen() {
        //StdAudio.play("/emerald_done.wav");
        StdDraw.clear(Color.BLACK);
        String praise = "Nice! You successfully wasted your time by collecting all flowers!";
        String newGame = "New Game: 1 Player (Press N)";
        String newGame2 = "New Game: 2 Player (Press 2)";
        Font font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.text(Game.WIDTH / 2, Game.HEIGHT / 2, praise);
        StdDraw.text(Game.WIDTH / 2, (Game.HEIGHT / 2) - 3, newGame);
        StdDraw.text(Game.WIDTH / 2, (Game.HEIGHT / 2) - 6, newGame2);
        StdDraw.text(Game.WIDTH / 2, (Game.HEIGHT / 2) - 9, "Quit (Press Q)");
        StdDraw.show();
        while (true) {
            char charTyped;
            if (StdDraw.hasNextKeyTyped()) {
                charTyped = StdDraw.nextKeyTyped();
                if (charTyped == 'Q' || charTyped == 'q') {
                    //StdAudio.play("/buttonClick.wav");
                    System.exit(0);
                } else if (charTyped == '2') {
                    startNewGame(charTyped);
                } else if (charTyped == 'N' || charTyped == 'n') {
                    startNewGame(charTyped);
                }
            }
        }
    }

    /**
     * Enables the ability to start a new game on the "Game Over" screen.
     */
    static void startNewGame(char charTyped) {
        if (charTyped == 'N' || charTyped == 'n') {
            //StdAudio.play("/buttonClick.wav");
            StdDraw.clear(Color.BLACK);
            GameUI.drawNewGameScreen();
            String input = GameUI.solicitSeedInput();
            //Set up new world
            Font font = new Font("Monaco", Font.BOLD, 14); //Default tile sizes
            StdDraw.setFont(font);
            long seed = WorldGenerator.extractNumber(input);
            TETile[][] world = new TETile[Game.WIDTH][Game.HEIGHT];
            for (int x = 0; x < Game.WIDTH; x += 1) {
                for (int y = 0; y < Game.HEIGHT; y += 1) {
                    world[x][y] = Tileset.NOTHING;
                }
            }
            Random r = new Random(seed);
            WorldGenerator wg = new WorldGenerator(world, false);
            wg.generateRandomStructures(r);
            GameUI.populateFlowers(wg, r);
            GameUI.activateGameKeysAndHUD(wg);
            GameUI.displayGameOverScreen();
        } else if (charTyped == '2') {
            //StdAudio.play("/buttonClick.wav");
            StdDraw.clear(Color.BLACK);
            GameUI.drawNewGameScreen();
            String input = GameUI.solicitSeedInput();
            Font font = new Font("Monaco", Font.BOLD, 14); //Default tile sizes
            StdDraw.setFont(font);
            long seed = WorldGenerator.extractNumber(input);
            TETile[][] world = new TETile[Game.WIDTH][Game.HEIGHT];
            for (int x = 0; x < Game.WIDTH; x += 1) {
                for (int y = 0; y < Game.HEIGHT; y += 1) {
                    world[x][y] = Tileset.NOTHING;
                }
            }
            Random r = new Random(seed);
            WorldGenerator wg = new WorldGenerator(world, true);
            wg.generateRandomStructures(r);
            GameUI.populateFlowers(wg, r);
            GameUI.activateGameKeysAndHUD(wg);
            GameUI.displayGameOverScreen();
        }
    }

    /**
     * Keeps a tally on the top left of the screen of the
     * flowers remaining to be collected.
     */
    static void flowersRemaining(WorldGenerator wg) {
        StdDraw.setPenColor(Color.WHITE);
        String message = "Collect all the flowers! Remaining: " + wg.numFlowers;
        StdDraw.text(9, Game.HEIGHT - 1, message);
    }

    /**
     * Populate the rooms and hallways with flowers.
     */
    static void populateFlowers(WorldGenerator wg, Random r) {
        for (int x = 0; x < WorldGenerator.WORLD_WIDTH; x += 1) {
            for (int y = 0; y < WorldGenerator.WORLD_HEIGHT; y += 1) {
                if (wg.world[x][y].equals(Tileset.FLOOR)) {
                    boolean decision = RandomUtils.bernoulli(r, 0.10);
                    if (decision) {
                        wg.world[x][y] = Tileset.FLOWER;
                        wg.numFlowers += 1;
                    }
                }
            }
        }
    }

    /**
     * Heads up display for mouse hovering.
     * Displays the description of the tile that the mouse is currently
     * hovering over.
     */
    static void hUD(WorldGenerator wg) {
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();
        try {
            TETile tile = wg.world[(int) x][(int) y];
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(Game.WIDTH / 2, Game.HEIGHT - 1, tile.description());
        } catch (ArrayIndexOutOfBoundsException e) {
            StdDraw.text(Game.WIDTH / 2, Game.HEIGHT - 1, "nothing");
        }
        StdDraw.show();
    }

    /* Saving and loading methods */

    /**
     * Allows for a world being played on to be saved for play later.
     *
     * @source SaveDemo Skeleton
     */
    static void saveWorld(WorldGenerator w) {
        File f = new File("./world.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(w);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    /**
     * Allows for a saved world to be loaded and resume play.
     *
     * @source SaveDemo Skeleton
     */
    static WorldGenerator loadWorld() {
        File f = new File("./world.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                WorldGenerator loadWorld = (WorldGenerator) os.readObject();
                os.close();
                return loadWorld;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        return new WorldGenerator(new TETile[Game.WIDTH][Game.HEIGHT], false);
    }

}
