package com.buckfae.game;

import com.buckfae.ai.AI_Handler;
import com.buckfae.ai.Algo_Generic_AI;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Random;

import static java.awt.Color.black;
import static java.awt.Color.white;

public class Processing extends PApplet {


    //--------------Variables to play around with----------------

        //--------Main Window-------------
        //How many games there should be
        public static int population_size = 1500;

        //How big the window should be
        public int main_window_size_x = 1000;
        public int main_window_size_y = 1000;

        //The size of one game, should be a multiple of ten as one sqare in the game itself has a size of 10
        public static int defaultGameSize = 100;    //The games are sqares

        public int space_between_games_x = 20;
        public int space_between_games_y = 20;

        //Space between the games and the border of the window
        public int spaces_to_side[] = {50, 50, 50, 50}; // Left, Right, Up, Down

        //Default size of text
        public int default_text_size = 20;

        //Each n Frames the snakes will move (Game runs default at 60 fps)
        public int frames_to_wait_between_two_moves = 0;

    //-----------------------------------------------------------

    //-------------Variables not to play around with--------------

        //You can call this Papplet from any class to do processing stuff from other classes.
        public static PApplet processing;

        private static Processing instance;

        public static Processing getInstance()
        {
            return instance;
        }

        //All games are stored here
        public static ArrayList<Game> games;

        //Total ammount of games to draw
        private int total_amount_of_games_to_be_drawn;

        //Handles the AI
        AI_Handler ai_handler;

        //Frames we are waiting already, once >= frames to wait we make a move
        int frames_currently_waiting = 0;

        //0 -> Ahead, 1 -> Left, 2 -> Right, allows the human to control the snake
        public static int direction_choosen_by_hoooman = 0;

    //-----------------------------------------------------------

    //Is called by main to initialize Processing
    public void initialize_processing(){
        instance = this;
        //Initializes processing
        String[] processingArgs = {""};
        processing = new Processing();
        PApplet.runSketch(processingArgs, processing);

    }


    public void setup(){

        //Sets the title of the window to Hello World
        surface.setTitle("Smart Snakes - by BUCKFAE");

        //Sets the framerate to 60
        frameRate(60);

        //Calculates the dimensions of the game
        createGames();

        //Gives all snakes their Brains
        ai_handler  = new AI_Handler();

    }

    public void settings(){

        //Sets the size of the window
        size(main_window_size_x, main_window_size_y);

    }

    //Is called each frame
    public void draw(){

        //Draws the background
        background(255);

        //draws all games and the frame around them
        draw_games();

        //test_game_switching();

        //If we waited long enough to move, we move
        if(frames_currently_waiting++ >= frames_to_wait_between_two_moves){
            move_this_frame();
        }

        //Checks if all snakes are dead and creates a new Population if necessary
        ai_handler.update_population();

    }

    public void keyReleased(){

        //Right now the snakes will only make a move, if the spacebar is pressed
        if(key == ' '){
            //allows the snakes to move this frame
            move_this_frame();
        }

        //These are just used if a hoooman controls the game, to do that change the variable in the AI_Handler class
        if(keyCode == UP){
            direction_choosen_by_hoooman = 0;
            move_this_frame();
        }
        if(keyCode == LEFT){
            direction_choosen_by_hoooman = 1;
            move_this_frame();
        }
        if(keyCode == RIGHT){
            direction_choosen_by_hoooman = 2;
            move_this_frame();
        }

        if(key == 'b'){
            System.out.println("We will generate bigger brains after the next genration");
            Algo_Generic_AI.generate_bigger_brains = true;
        }
    }

    public void move_this_frame(){
        //Allows all snakes to make another move and makes the ai handler update
        for(Game game: games){
            game.snake.make_a_move = true;
        }
        ai_handler.update_this_frame = true;
    }

    //Draws all games
    public void draw_games(){

        //Draws one big Frame across all games
        stroke(black.getRGB());
        fill(white.getRGB());
        rect(spaces_to_side[0], spaces_to_side[2],
                main_window_size_x - spaces_to_side[0] - spaces_to_side[1],
                main_window_size_y - spaces_to_side[2] - spaces_to_side[3]);

        displayText("Games", spaces_to_side[0], spaces_to_side[2] - 3);
        //Draws all games
        for(Game game: games){
                game.draw_game();
        }

    }

    public void displayText(String text, int x, int y){

        //Sets the text size to default
        textSize(default_text_size);

        //Sets the textcolor to black
        fill(black.getRGB());
        stroke(black.getRGB());

        //Displays the text at the given coordinates
        text(text, x, y);
    }

    public void createGames(){

        //Initializes the games
        games = new ArrayList<>();

        for (int i = 0; i < population_size; i++) {
            games.add(new Game(-1, -1, false));
        }

        setup_calculate_game_dimensions();
    }

    public void setup_calculate_game_dimensions(){

        //How much space on the (x/y)-Axis one game needs
        int total_size_of_one_game_x = size_of_one_game + space_between_games_x;
        int total_size_of_one_game_y = size_of_one_game + space_between_games_y;

        //Total ammount of Games
        int amount_of_games_x = (main_window_size_x - spaces_to_side[0] - spaces_to_side[1])
                / (total_size_of_one_game_x);
        int amount_of_games_y = (main_window_size_y - spaces_to_side[2] - spaces_to_side[3])
                / (total_size_of_one_game_y);

        //Tells the user how many games will be created
        System.out.println("\n");
        System.out.println("Amount of games x: " + amount_of_games_x);
        System.out.println("Amount of games y: " + amount_of_games_y);

        //Calculates the total amount of games
        total_amount_of_games_to_be_drawn = amount_of_games_x * amount_of_games_y;

        System.out.println("Total amount of games to be drawn: " + total_amount_of_games_to_be_drawn);

        //Calculates how much pixels we are not using
        int space_left_x = main_window_size_x - spaces_to_side[0] - spaces_to_side[1]
                - (amount_of_games_x * total_size_of_one_game_x) + space_between_games_x;
        int space_left_y = main_window_size_y - spaces_to_side[2] - spaces_to_side[3]
                - (amount_of_games_y * total_size_of_one_game_y) + space_between_games_y;

        //Tells the user how many pixels we are not using
        System.out.println("We have " + space_left_x + " pixels left on the x-Axis");
        System.out.println("We have " + space_left_y + " pixels left on the y-Axis");


        //tracks how many games we created yet
        int amount_of_games_created = 0;

        for (Game game : games)
        {
            game.is_currently_shown = false;
        }

        //Creates all games and sets their coordinates
        loop_game_x:
        for(int games_x = 0; games_x < amount_of_games_x; games_x++){

            for(int games_y = 0; games_y < amount_of_games_y; games_y++){

                //We still need to draw more games
                if(amount_of_games_created++ < population_size) {

                    //Calculates the coordinates for the new game
                    int new_game_x = spaces_to_side[0] + space_left_x / 2
                            + (games_x * (size_of_one_game + space_between_games_x));
                    int new_game_y = spaces_to_side[2] + space_left_y / 2
                            + (games_y * (size_of_one_game + space_between_games_y));

                    //We still have room to display a game
                    Game currentGame = games.get(amount_of_games_created);
                    currentGame.is_currently_shown = true;
                    currentGame.game_x = new_game_x;
                    currentGame.game_y = new_game_y;
                }
                //We drew enough games
                else {
                    //We stop creating games
                    break loop_game_x;
                }
            }
        }
        System.out.println("Games shown: " + amount_of_games_created);

        //Logs total amount of games
        System.out.println("Games total: " + games.size() + "\n\n");

    }

    //Call this method to switch two random snakes
    public void test_game_switching(){
        Random random = new Random();
        if(random.nextInt(100) < 10){
            Game.switch_two_games_position(games.get(random.nextInt(games.size() - 1)), games.get(random.nextInt(games.size() - 1)));
        }
    }

    //increases the size of a game
    public static void increaseGameSize(int amount)
    {
        size_of_one_game += (10 * amount);
    }

    //the multiplier by which the board should be increased
    public static double defaultMultiplier = 1.1;
    //every x generations the board will be increased in its size
    public static int generationMultiplier = 15;
    public static int size_of_one_game = defaultGameSize;
    //the max size of the game board
    public static int maxGameSize = roundToTen(defaultGameSize * 2.5);

    public static void multiplyGameSize(){
        multiplyGameSize(defaultMultiplier);
    }

    public static void multiplyGameSize(double multiplier)
    {
        size_of_one_game = roundToTen(size_of_one_game * multiplier);
        if (size_of_one_game > maxGameSize){
            size_of_one_game = maxGameSize;
        }
    }

    public static int roundToTen(double d){
       return roundToTen((int)Math.round(d));
    }

    public static int roundToTen(int i){
        int r = i % 10;
        if (r<5)
        {
            i -= r;
        }else{
            i += 10 - r;
        }
        return i;
    }
}
