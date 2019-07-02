package com.buckfae.game;

import com.buckfae.ai.Brain;

import java.util.Collections;

import static java.awt.Color.black;
import static java.awt.Color.white;

public class Game {

    public Snake snake;

    //The Coordinates of where the game in the grid is
    public int game_x;
    public int game_y;

    //If the game is currently shown
    boolean is_currently_shown;

    //Stores all the fields of the game
    public Field fields[][];


    public Game(int game_x, int game_y, boolean is_currently_shown){

        this.game_x = game_x;
        this.game_y = game_y;

        this.is_currently_shown = is_currently_shown;

        //generates the grid
        this.generate_grid();

        this.snake = new Snake(this);
    }

    public void recreateGrid(boolean snake){
        this.generate_grid();
        if(snake){
            Brain brain = this.snake.brain;
            this.snake = new Snake(this);
            this.snake.brain = brain;
        }
    }


    public void draw_game(){

        //Updates the snake of the game
        snake.update_snake();

        if(is_currently_shown){

            //Draws the border around the game
            Processing.processing.stroke(black.getRGB());
            Processing.processing.fill(white.getRGB());
            Processing.processing.rect(game_x, game_y, Processing.size_of_one_game, Processing.size_of_one_game);

            //Draws all fields
            for(Field[] fields: fields) {
                for (Field field : fields) {
                    field.draw_field();
                }
            }
        }

    }

    //Switches the position of two games and changes their visibility accordingly
    public static void switch_two_games_position(Game game1, Game game2){

        //Saves the values of the first game
        int temp_x = game1.game_x;
        int temp_y = game1.game_y;
        boolean temp_is_currently_shown = game1.is_currently_shown;

        //Sets the new values for the first game
        game1.game_x = game2.game_x;
        game1.game_y = game2.game_y;
        game1.is_currently_shown = game2.is_currently_shown;

        //Sets the values or the second game
        game2.game_x = temp_x;
        game2.game_y = temp_y;
        game2.is_currently_shown = temp_is_currently_shown;

        //Swaps the two objects that if we do games.get(0) we still get the element in the upper left corner
        Collections.swap(Processing.games, Processing.games.indexOf(game1), Processing.games.indexOf(game2));

    }

    public void generate_grid(){

        //Calculates hos many tiles we will have
        int size_of_one_game = Processing.size_of_one_game;
        int amount_of_tiles = size_of_one_game / Field.field_size;

        //Initializes the fields
        fields = new Field[amount_of_tiles][amount_of_tiles];

        //Creates all fields
        for(int x = 0; x < amount_of_tiles; x++){

            for(int y = 0; y < amount_of_tiles; y++){

                fields[x][y] = new Field(Field.field_size * x, Field.field_size * y, this);
            }
        }

    }

    @Override
    public String toString() {
        return "Game{" +
                "game_x=" + game_x +
                ", game_y=" + game_y +
                ", is_currently_shown=" + is_currently_shown +
                '}';
    }
}

