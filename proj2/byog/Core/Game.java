package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdAudio;
import edu.princeton.cs.introcs.StdDraw;


import java.awt.Color;
import java.awt.Font;
import java.util.Random;
//import edu.princeton.cs.introcs.StdAudio;
import java.util.ArrayList;


public class Game {

    TERenderer ter = new TERenderer();
    public static final int WIDTH = 80;
    public static final int HEIGHT = 35;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        //StdAudio.loop("/aquemini.wav");
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        GameUI.displayMenu();
        ArrayList<Character> newGameCharList = new ArrayList<>(3);
        newGameCharList.add('N');
        newGameCharList.add('n');
        newGameCharList.add('2');
        char charTyped;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                charTyped = StdDraw.nextKeyTyped();
                if (newGameCharList.contains(charTyped)) {
                    GameUI.startNewGame(charTyped);
                } else if (charTyped == 'L' || charTyped == 'l') {
                    StdAudio.play("/startupgame.wav");
                    try {
                        font = new Font("Monaco", Font.BOLD, 14);
                        StdDraw.setFont(font);
                        WorldGenerator loadedWorld = GameUI.loadWorld();
                        ter.renderFrame(loadedWorld.world);
                        GameUI.activateGameKeysAndHUD(loadedWorld);
                        GameUI.displayGameOverScreen();
                        break;
                    } catch (IllegalArgumentException e) {
                        System.exit(0);
                    }
                } else if (charTyped == 'Q' || charTyped == 'q') {
                    StdAudio.play("/buttonClick.wav");
                    System.exit(0);
                } else {
                    continue;
                }
            }
        }
    }


    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {

        long seed = WorldGenerator.extractNumber(input);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        //Fills the world with black spaces
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        WorldGenerator currentWG = new WorldGenerator(world, false);
        Random r = new Random(seed);
        int indexOfSubmission = 0;

        for (int i = 0; i < input.length(); i += 1) {
            char charTyped = input.charAt(i);
            if (charTyped == 'N' || charTyped == 'n') {
                continue;
            } else if (charTyped == 'S' || charTyped == 's') {
                indexOfSubmission = input.indexOf(charTyped) + 1;
                currentWG.generateRandomStructures(r);
                for (int j = indexOfSubmission; j < input.length(); j += 1) {
                    charTyped = input.charAt(j);
                    if (charTyped == 'W' || charTyped == 'w') {
                        currentWG.player.moveUp();
                    } else if (charTyped == 'A' || charTyped == 'a') {
                        currentWG.player.moveLeft();
                    } else if (charTyped == 'S' || charTyped == 's') {
                        currentWG.player.moveDown();
                    } else if (charTyped == 'D' || charTyped == 'd') {
                        currentWG.player.moveRight();
                    } else if (charTyped == ':') {
                        GameUI.saveWorld(currentWG);
                        break;
                    }
                }
                break;
            } else if (charTyped == 'L' || charTyped == 'l') {
                currentWG = GameUI.loadWorld();
                for (int j = indexOfSubmission; j < input.length(); j += 1) {
                    charTyped = input.charAt(j);
                    if (charTyped == 'W' || charTyped == 'w') {
                        currentWG.player.moveUp();
                    } else if (charTyped == 'A' || charTyped == 'a') {
                        currentWG.player.moveLeft();
                    } else if (charTyped == 'S' || charTyped == 's') {
                        currentWG.player.moveDown();
                    } else if (charTyped == 'D' || charTyped == 'd') {
                        currentWG.player.moveRight();
                    } else if (charTyped == ':') {
                        GameUI.saveWorld(currentWG);
                        break;
                    }
                }
                break;
            }
        }
        TETile[][] finalWorldFrame = currentWG.world;
        return finalWorldFrame;
    }

}
