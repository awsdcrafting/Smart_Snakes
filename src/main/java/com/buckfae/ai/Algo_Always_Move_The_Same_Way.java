package com.buckfae.ai;

import com.buckfae.game.Game;
import com.buckfae.game.Processing;
import com.buckfae.game.Snake;

public class Algo_Always_Move_The_Same_Way implements Brain {

    //We start the algo by first going to a corner, if this is true we know that we are in the actual algo and not in the setup anymore
    boolean got_to_first_corner = false;


    public int calculate_next_move(double[][] input_values) {

        if(!got_to_first_corner){

            //There is no wall ahead of us
            if(input_values[3][0] > 1) {

                return 0;
            }
            //There is a wall ahead of us and one to the left / right -> we hit our first corner
            if(input_values[3][0] <= 1 && input_values[5][0] <= 1){
                System.out.println("We hit our first corner");
            }
            //There is a wall ahead of us but no wall to the left / right -> We turn left to get to our first corner
            if(input_values[3][0] <= 1 && input_values[5][0] > 1) {
                return 1;
            }

        }

        return 0;
    }

    public void generate_brains_for_new_generation() {

        //We do this for each game
        for(Game game: Processing.games){

            //Generates a new Brain
            Brain new_brain = new Algo_Always_Move_The_Same_Way();

            //Adds the new brain to the snake
            game.snake.brain = new_brain;
        }
    }

}
