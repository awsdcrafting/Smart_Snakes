package com.buckfae.ai;

import com.buckfae.game.Game;
import com.buckfae.game.Processing;

import java.util.ArrayList;
import java.util.Arrays;

public class AI_Handler {

    //----------------Variables to play around with-----------------------------

        int id_of_brain_to_choose = 2;
        /*
            0 -> Algo_Testing_Algo
            1 -> Algo_Always_Move_The_Same_Way
            2 -> Algo_Generic_AI
            3 -> Algo_Played_By_Human

            If you want to add a new Algo yourself make sure to add your Algo in the
                switch statement below. This may seem complicated but it allows to
                switch between two Algos just by changing one variable. When creating
                your own Algo make sure to implement Brain, then you will have all
                necessary functions, you just need to fill them with code
        */
    //--------------------------------------------------------------------------


    public boolean update_this_frame = false;

    public AI_Handler(){

        //Decides what the master_brain will be
        //If you want to add your own Algo add it's id here as well
        //Each game gets their brain
        for(Game game: Processing.games){

            switch (id_of_brain_to_choose){
                case 0:
                    game.snake.brain = new Algo_Testing_Algo();
                    break;
                case 1:
                    game.snake.brain = new Algo_Always_Move_The_Same_Way();
                    break;
                case 2:
                    //The generic AI expects us to give him an initial amount of neurons per layer
                    ArrayList<Integer> hidden_layer_neuron_count = new ArrayList<Integer>(Arrays.asList(24, 24, 24));
                    game.snake.brain = new Algo_Generic_AI(21, hidden_layer_neuron_count, 3);
                    break;
                case 3:
                    game.snake.brain = new Algo_Played_By_Human();
                    break;
            }

        }
    }


    public void update_population(){

        //Update this frame will always be true if the snakes moved this frame
        if(update_this_frame) {

            //Checks if all Snakes are dead
            boolean all_dead = true;
            //Goes through all snakes and checks if at least one is alive
            for (Game game : Processing.games) {
                if (!game.snake.is_dead) {
                    all_dead = false;
                    break;
                }
            }

            //All snakes are dead
            if (all_dead) {

                //Generates the brains for a new generation and resets the snakes as well
                Processing.games.get(0).snake.brain.generate_brains_for_new_generation();
            }

            //We don't update next frame
            update_this_frame = false;
        }

    }
}
