package com.buckfae.ai;

import com.buckfae.game.Game;
import com.buckfae.game.Processing;
import com.buckfae.game.Snake;

//Simple algo that always moves left if there is a wall ahead or if the food is to the left
public class Algo_Always_Move_The_Same_Way implements Brain {


    public int calculate_next_move(double[][] input_values) {

        //If the food is to the left
        if(input_values[1][2] != Processing.games.get(0).fields.length * Processing.games.get(0).fields.length){
            return 1; //We go left
        }

        //There is a wall ahead of us
        if(input_values[3][0] == 1){
            return 1; //We go left
        }

        //Neither food nor a wall is there -> We go ahead
        return 0;
    }

    public void generate_brains_for_new_generation() {

        //We do this for each game
        for(Game game: Processing.games){

            //Resets the Snake
            game.snake = new Snake(game,0);

            //Generates a new Brain
            Brain new_brain = new Algo_Always_Move_The_Same_Way();

            //Adds the new brain to the snake
            game.snake.brain = new_brain;
        }
    }

}
