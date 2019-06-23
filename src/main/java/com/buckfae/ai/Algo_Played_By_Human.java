package com.buckfae.ai;

import com.buckfae.game.Game;
import com.buckfae.game.Processing;
import com.buckfae.game.Snake;

public class Algo_Played_By_Human implements Brain{

    public int calculate_next_move(double[][] input_values) {

        Processing.games.get(0).snake.print_input_values(input_values);

        return Processing.direction_choosen_by_hoooman;
    }

    public void generate_brains_for_new_generation() {


        //We do this for each game
        for(Game game: Processing.games){

            //Resets the Snake
            game.snake = new Snake(game);

            //Generates a new Brain
            Brain new_brain = new Algo_Played_By_Human();

            //Adds the new brain to the snake
            game.snake.brain = new_brain;
        }
    }
    }

